package com.xxl.job.admin.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 重复任务执行记录
 */
public class JobRepeatRecord implements Serializable {

    //重复任务记录ID
    private int id;

    //创建时间
    private LocalDateTime createTime;

    //修改时间
    private LocalDateTime updateTime;

    //任务组唯一标识
    private String jobFlag;

    //剩余运行次数
    private int surplusRunTimes;

    //起始运行时间 (cron表达式)
    private String beginRunTime;

    //间隔时间 （单位:s）
    private int intervalTime;

    //当前状态(200:正常状态，500:任务异常中断)
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getJobFlag() {
        return jobFlag;
    }

    public void setJobFlag(String jobFlag) {
        this.jobFlag = jobFlag;
    }

    public Integer getSurplusRunTimes() {
        return surplusRunTimes;
    }

    public void setSurplusRunTimes(Integer surplusRunTimes) {
        this.surplusRunTimes = surplusRunTimes;
    }

    public String getBeginRunTime() {
        return beginRunTime;
    }

    public void setBeginRunTime(String beginRunTime) {
        this.beginRunTime = beginRunTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
