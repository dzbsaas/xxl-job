package com.xxl.job.admin.configration.jacksonConfig;

/**
 * @Author lwk
 * @Date 2021/5/28 14:15
 * @Version 1.0
 * @Description
 */
public class Id {

    private final Long val;

    public Long getVal() {
        return val;
    }

    public Id(Long val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }
}