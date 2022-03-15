package com.xxl.job.exposeds.dto.res;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author lwk
 * @Date 2021/8/2 10:11
 * @Version 1.0
 * @Description
 */
@Data
public class JobParamRes implements Serializable {
    private static final long serialVersionUID = 601837046892858973L;

    /**
     * 业务关联id
     */
    private String relateId;

    /**
     * 额外参数
     */
    private String extraRelateInfo;

    /**
     * 任务id
     */
    private String jobId;
}
