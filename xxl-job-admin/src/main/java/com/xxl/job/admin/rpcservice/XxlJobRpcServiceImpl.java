package com.xxl.job.admin.rpcservice;

import com.alibaba.nacos.common.utils.StringUtils;
import com.seetech.rpcmodules.xxljob.dto.normal.JobDetails;
import com.seetech.rpcmodules.xxljob.dto.req.XxlJobAddReq;
import com.seetech.rpcmodules.xxljob.dto.req.XxlJobUpdateReq;
import com.seetech.rpcmodules.xxljob.dto.req.XxlRepeatJobAddReq;
import com.seetech.rpcmodules.xxljob.dto.res.JobParamRes;
import com.seetech.rpcmodules.xxljob.rpcinterfaces.XxlJobRpcService;
import com.seetech.util.EmptyUtil;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.utils.JavaBeanUtils;
import com.xxl.job.admin.utils.JsonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobContext;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class XxlJobRpcServiceImpl implements XxlJobRpcService {

    @Resource
    private XxlJobService xxlJobService;

    //获取当前任务参数
    @Override
    public JobParamRes getJobParam(XxlJobContext xxlJobContext) {
        //XxlJobContext xxlJobContext = XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return null;
        }
        String jobParam = xxlJobContext.getJobParam();
        JobParamRes jobParamRes = null;
        if (!StringUtils.isBlank(jobParam)) {
            jobParamRes = JsonUtils.toObject(jobParam, JobParamRes.class);
        }
        return jobParamRes;
    }

    /**
     * 通过业务id查询job
     *
     * @param relateId 业务id
     * @return
     */
    @SneakyThrows
    @Override
    public List<JobDetails> businessIdSelect(String relateId) {
        if (ObjectUtils.isEmpty(relateId)) {
            throw new RuntimeException("请输入正确的业务ID，业务ID不能为null或0");
        }
        ReturnT<List<XxlJobInfo>> xxlJobInfoReturn = xxlJobService.businessIdSelect(relateId);
        if (xxlJobInfoReturn.getCode() != 200) {
            throw new RuntimeException(xxlJobInfoReturn.getMsg());
        }
        return JavaBeanUtils.copy(xxlJobInfoReturn.getContent(), JobDetails.class);
    }

    /**
     * 通过业务id和执行器名称查询任务详情
     *
     * @param relateId        业务id
     * @param executorHandler 执行器名称
     * @return
     */
    @SneakyThrows
    @Override
    public List<JobDetails> businessIdSelect(String relateId, String executorHandler) {
        ReturnT<List<XxlJobInfo>> xxlJobInfoReturn = xxlJobService.businessIdAndExecutorHandler(relateId, executorHandler);
        if (xxlJobInfoReturn.getCode() != 200) {
            throw new RuntimeException(xxlJobInfoReturn.getMsg());
        }
        return JavaBeanUtils.copy(xxlJobInfoReturn.getContent(), JobDetails.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Integer jobAdd(XxlJobAddReq xxlJobAddReq) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        BeanUtils.copyProperties(xxlJobAddReq, xxlJobInfo);

        //业务参数
        JobParamRes jobParmar = new JobParamRes();
        jobParmar.setRelateId(xxlJobAddReq.getRelateId());
        jobParmar.setExtraRelateInfo(xxlJobAddReq.getExtraRelateInfo());
        //额外参数
        xxlJobInfo.setExecutorParam(JsonUtils.toJson(jobParmar));
        xxlJobInfo.setRelateId(xxlJobAddReq.getRelateId());

        ReturnT<String> addResult = xxlJobService.add(xxlJobInfo);
        if (addResult.getCode() != 200) {
            throw new RuntimeException(addResult.getMsg());
        }
        return Integer.parseInt(addResult.getContent());
    }

    /**
     * 添加重复任务
     *
     * @param repeatJobAddReq 重复任务参数
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Integer repeatJobAdd(XxlRepeatJobAddReq repeatJobAddReq) {
        if (EmptyUtil.isEmpty(repeatJobAddReq.getIntervalTime()) || repeatJobAddReq.getIntervalTime() <= 0) {
            throw new RuntimeException("任务间隔时间不能为空并且不能够小于等于0");
        }
        if (EmptyUtil.isEmpty(repeatJobAddReq.getRunTimes()) || repeatJobAddReq.getRunTimes() <= 1) {
            throw new RuntimeException("重复执行次数不能为空并且不能小于等于1");
        }
        if (EmptyUtil.isEmpty(repeatJobAddReq.getScheduleConf())) {
            throw new RuntimeException("起始时间不能为空并且必须是一个精确的起始时间");
        }


        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        JavaBeanUtils.copy(repeatJobAddReq, xxlJobInfo);
        xxlJobInfo.setIntervalTime(repeatJobAddReq.getIntervalTime());

        //业务参数
        JobParamRes jobParmar = new JobParamRes();
        jobParmar.setRelateId(repeatJobAddReq.getRelateId());
        jobParmar.setExtraRelateInfo(repeatJobAddReq.getExtraRelateInfo());
        //额外参数
        xxlJobInfo.setExecutorParam(JsonUtils.toJson(jobParmar));
        xxlJobInfo.setRelateId(repeatJobAddReq.getRelateId());
        xxlJobInfo.setScheduleType("repeat");

        ReturnT<String> addResult = xxlJobService.add(xxlJobInfo);
        if (addResult.getCode() != 200) {
            throw new RuntimeException(addResult.getMsg());
        }
        return Integer.parseInt(addResult.getContent());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void jobUpdate(XxlJobUpdateReq xxlJobUpdateReq) {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        BeanUtils.copyProperties(xxlJobUpdateReq, xxlJobInfo);

        JobParamRes jobParmar = new JobParamRes();
        jobParmar.setRelateId(xxlJobUpdateReq.getRelateId());
        jobParmar.setExtraRelateInfo(xxlJobUpdateReq.getExtraRelateInfo());
        //额外参数
        xxlJobInfo.setExecutorParam(JsonUtils.toJson(jobParmar));
        xxlJobInfo.setRelateId(xxlJobUpdateReq.getRelateId());

        ReturnT<String> updateResult = xxlJobService.update(xxlJobInfo);
        if (updateResult.getCode() != 200) {
            throw new RuntimeException(updateResult.getMsg());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void jobDel(Integer id) {
        if (ObjectUtils.isEmpty(id) || id == 0) {
            throw new RuntimeException("请输入正确的任务ID，任务ID不能为null或0");
        }
        ReturnT<String> removeResult = xxlJobService.remove(id);
        if (removeResult.getCode() != 200) {
            throw new RuntimeException(removeResult.getMsg());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void jobStart(Integer id) {
        if (ObjectUtils.isEmpty(id) || id == 0) {
            throw new RuntimeException("请输入正确的任务ID，任务ID不能为null或0");
        }
        ReturnT<String> startResult = xxlJobService.start(id);
        if (startResult.getCode() != 200) {
            throw new RuntimeException(startResult.getMsg());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void jobStop(Integer id) {
        if (ObjectUtils.isEmpty(id) || id == 0) {
            throw new RuntimeException("请输入正确的任务ID，任务ID不能为null或0");
        }
        ReturnT<String> startResult = xxlJobService.stop(id);
        if (startResult.getCode() != 200) {
            throw new RuntimeException(startResult.getMsg());
        }
    }
}
