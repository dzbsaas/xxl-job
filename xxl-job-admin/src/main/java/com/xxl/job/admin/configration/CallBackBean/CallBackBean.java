package com.xxl.job.admin.configration.CallBackBean;

import com.xxl.job.admin.core.thread.JobCompleteHelper;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.XxlJobService;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class CallBackBean {

    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    @Resource
    private XxlJobService xxlJobService;

    @PostConstruct
    public void xxlJobBean() {
        JobCompleteHelper jobCompleteHelper = new JobCompleteHelper(xxlJobInfoDao, xxlJobService);
    }

}
