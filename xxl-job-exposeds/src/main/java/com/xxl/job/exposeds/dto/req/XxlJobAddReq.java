package com.xxl.job.exposeds.dto.req;


import lombok.Data;

import java.io.Serializable;

/**
 * @Author lwk
 * @Date 2021/7/30 16:33
 * @Version 1.0
 * @Description
 */
@Data
public class XxlJobAddReq implements Serializable {
    private static final long serialVersionUID = 5940196865602634396L;

    //执行器主键
    private int jobGroup;

    //任务描述
    private String jobDesc;

    //Cron表达式
    private String scheduleConf;

    //执行器名称 JobHandler
    private String executorHandler;

    /**
     * FIRST 第一个
     * LAST  最后一个
     * ROUND 轮询
     * RANDOM 随机
     * CONSISTENT_HASH 一致hash
     * LEAST_FREQUENTLY_USED 最不经常用的
     * LEAST_RECENTLY_USED 最近最久使用
     * FAILOVER  故障转移
     * BUSYOVER  忙碌转移
     * SHARDING_BROADCAST 广播分片
     * <p>
     * <p>
     * 默认 故障转移
     */
    //执行路由策略 路由策略
    private String executorRouteStrategy = "FAILOVER";

    //业务关联id
    private String relateId;

    //额外信息
    private String extraRelateInfo;

    //失败重试次数
    private int executorFailRetryCount;
}
