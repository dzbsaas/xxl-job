package com.xxl.job.admin.core.thread;

import com.seetech.util.EmptyUtil;
import com.seetech.util.TimeToCronUtil;
import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.JobRepeatRecord;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * job lose-monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobCompleteHelper {
    private static Logger logger = LoggerFactory.getLogger(JobCompleteHelper.class);

    private static JobCompleteHelper instance = new JobCompleteHelper();


    public static JobCompleteHelper getInstance() {
        return instance;
    }

    private static XxlJobInfoDao xxlJobInfoDao;

    private static XxlJobService xxlJobService;

    public JobCompleteHelper(XxlJobInfoDao xxlJobInfoDao, XxlJobService xxlJobService) {
        JobCompleteHelper.xxlJobInfoDao = xxlJobInfoDao;
        JobCompleteHelper.xxlJobService = xxlJobService;
    }

    public JobCompleteHelper() {
    }

    // ---------------------- monitor ----------------------

    private ThreadPoolExecutor callbackThreadPool = null;
    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {

        // for callback
        callbackThreadPool = new ThreadPoolExecutor(
                2,
                20,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-job, admin JobLosedMonitorHelper-callbackThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        r.run();
                        logger.warn(">>>>>>>>>>> xxl-job, callback too fast, match threadpool rejected handler(run now).");
                    }
                });


        // for monitor
        monitorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // wait for JobTriggerPoolHelper-init
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }

                // monitor
                while (!toStop) {
                    try {
                        // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
                        Date losedTime = DateUtil.addMinutes(new Date(), -10);
                        List<Long> losedJobIds = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().findLostJobIds(losedTime);

                        if (losedJobIds != null && losedJobIds.size() > 0) {
                            for (Long logId : losedJobIds) {

                                XxlJobLog jobLog = new XxlJobLog();
                                jobLog.setId(logId);

                                jobLog.setHandleTime(new Date());
                                jobLog.setHandleCode(ReturnT.FAIL_CODE);
                                jobLog.setHandleMsg(I18nUtil.getString("joblog_lost_fail"));

                                XxlJobCompleter.updateHandleInfoAndFinish(jobLog);
                            }

                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job fail monitor thread error:{}", e);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

                logger.info(">>>>>>>>>>> xxl-job, JobLosedMonitorHelper stop");

            }
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin JobLosedMonitorHelper");
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        callbackThreadPool.shutdownNow();

        // stop monitorThread (interrupt and wait)
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------
    //todo 执行器执行完成回调
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {

        callbackThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (HandleCallbackParam handleCallbackParam : callbackParamList) {
                    ReturnT<String> callbackResult = callback(handleCallbackParam);
                    logger.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                            (callbackResult.getCode() == ReturnT.SUCCESS_CODE ? "success" : "fail"), handleCallbackParam, callbackResult);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    //todo 执行器执行完成回调
    @SneakyThrows
    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        XxlJobLog log = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(handleCallbackParam.getLogId());
        //System.out.println("定时任务执行了回调："+log.getJobId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getHandleMsg() != null) {
            handleMsg.append(handleCallbackParam.getHandleMsg());
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getHandleCode());
        log.setHandleMsg(handleMsg.toString());
        XxlJobCompleter.updateHandleInfoAndFinish(log);

        //只有当是重复任务类型的时候 才执行此逻辑
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if (EmptyUtil.isNotEmpty(xxlJobInfo)) {
            List<JobRepeatRecord> jobRepeatRecords = xxlJobInfoDao.selectListJobRepeatRecord(xxlJobInfo.getJobFlag());
            if (log.getHandleCode() == 200) {
                if (EmptyUtil.isNotEmpty(jobRepeatRecords)) {
                    if ("repeat".equals(xxlJobInfo.getScheduleType())) {
                        this.changeScheduleType(xxlJobInfo, jobRepeatRecords);
                    }
                    if ("FIX_RATE".equals(xxlJobInfo.getScheduleType())) {
                        this.changeScheduleTime(xxlJobInfo, jobRepeatRecords);
                    }
                }
            } else {
                xxlJobInfoDao.updateJobRepeatRecord(jobRepeatRecords.get(0).getId(), jobRepeatRecords.get(0).getSurplusRunTimes(), 500);
            }
        }

        return ReturnT.SUCCESS;
    }

    /**
     * 将任务改变为固定速率
     *
     * @param xxlJobInfo
     * @param jobRepeatRecords
     */
    public void changeScheduleType(XxlJobInfo xxlJobInfo, List<JobRepeatRecord> jobRepeatRecords) {
        xxlJobInfo.setScheduleType("FIX_RATE");
        xxlJobInfo.setScheduleConf(String.valueOf(xxlJobInfo.getIntervalTime()));
        xxlJobService.update(xxlJobInfo);
        xxlJobService.start(xxlJobInfo.getId());
        xxlJobInfoDao.updateJobRepeatRecord(jobRepeatRecords.get(0).getId(), jobRepeatRecords.get(0).getSurplusRunTimes() + 1, 200);
    }


    public void changeScheduleTime(XxlJobInfo xxlJobInfo, List<JobRepeatRecord> jobRepeatRecords) {
        Integer a = jobRepeatRecords.get(0).getSurplusRunTimes() + 1;
        xxlJobInfoDao.updateJobRepeatRecord(jobRepeatRecords.get(0).getId(), a, 200);
        if (a >= xxlJobInfo.getRunTimes()) {
            xxlJobService.remove(xxlJobInfo.getId());
            xxlJobInfoDao.delJobRepeatRecord(jobRepeatRecords.get(0).getId());
        }
    }


//    /**
//     * 重复任务逻辑
//     *
//     * @param jobFlag 任务组标识
//     * @param code    当前任务执行状态
//     */
//    private void jobRepeatRecord(String jobFlag, int code, XxlJobInfo xxlJobInfo) {
//        if (EmptyUtil.isNotEmpty(jobFlag)) {
//            //查询出当前任务组
//            List<JobRepeatRecord> jobRepeatRecords = xxlJobInfoDao.selectListJobRepeatRecord(jobFlag);
//            if (jobRepeatRecords.size() > 1) {
//                throw new RuntimeException("系统错误！！在系统记录中同一个任务组只允许有一个！！");
//            }
//            JobRepeatRecord jobRepeatRecord = jobRepeatRecords.get(0);
//            if (code == 200) { //当前任务执行成功
//                this.runSuccess(jobRepeatRecord, xxlJobInfo);
//            } else {//当前任务执行失败
//                this.runFail(jobRepeatRecord);
//            }
//        }
//    }
//
//    /**
//     * 当前任务运行成功
//     *
//     * @param jobRepeatRecord 当前组参数
//     */
//    public void runSuccess(JobRepeatRecord jobRepeatRecord, XxlJobInfo xxlJobInfo) {
//        int surplusRunTimes = jobRepeatRecord.getSurplusRunTimes() - 1;  //剩余的次数
//        String beginRunTime = jobRepeatRecord.getBeginRunTime();     //起始时间
//        int intervalTime = jobRepeatRecord.getIntervalTime();     //间隔时间
//        if (surplusRunTimes > 0) { //重复执行任务还未结束
//            JobRepeatRecord jobRepeatRecordNew = new JobRepeatRecord();
//            jobRepeatRecordNew.setId(jobRepeatRecord.getId());
//            jobRepeatRecordNew.setSurplusRunTimes(surplusRunTimes);//剩余次数
//            jobRepeatRecordNew.setStatus(200);
//
//            Integer runTimes = xxlJobInfo.getRunTimes();
//            //计算出下次运行的时间
//            LocalDateTime nextRunTime =
//                    TimeToCronUtil.cronAndLocalTime(beginRunTime)
//                            .plusSeconds(intervalTime);
//            //更新记录
//            xxlJobInfoDao.updateJobRepeatRecord(jobRepeatRecord.getId(), surplusRunTimes, 200, TimeToCronUtil.cronAndLocalTime(nextRunTime));
//            //获取当前时间
//            LocalDateTime nowDate = LocalDateTime.now();
//            if (nextRunTime.isBefore(nowDate) || nextRunTime.isEqual(nowDate)) {//下一次执行时间比在当前时间之前
//                //立即执行一次
//                JobTriggerPoolHelper.trigger(xxlJobInfo.getId(), TriggerTypeEnum.MANUAL, -1, null, xxlJobInfo.getExecutorParam(), "");
//                System.out.println("立即执行一次！！！");
//            } else { //下一次执行时间比在当前时间之后 （创建下一次的任务规则）
//                xxlJobInfo.setScheduleConf(TimeToCronUtil.cronAndLocalTime(nextRunTime));
//                xxlJobService.update(xxlJobInfo);
//                xxlJobService.start(xxlJobInfo.getId());
//                System.out.println("定时执行！！！");
//            }
//        } else if (surplusRunTimes == 0) {
//            System.out.println("定时重复任务完成！！");
//            xxlJobService.remove(xxlJobInfo.getId());
//        }
//    }
//

    /**
     * 执行器执行失败
     *
     * @param jobRepeatRecord
     */
    public void runFail(JobRepeatRecord jobRepeatRecord) {
        System.out.println("定时任务失败！！");
        xxlJobInfoDao.updateJobRepeatRecord(jobRepeatRecord.getId(), jobRepeatRecord.getSurplusRunTimes(), 500);
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
        Date parse = sdf.parse("00 00 00 31 01 ? 2022");

        Instant instant = parse.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        System.out.println(localDateTime);

    }
}
