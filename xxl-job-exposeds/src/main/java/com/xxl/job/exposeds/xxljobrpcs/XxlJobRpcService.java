package com.xxl.job.exposeds.xxljobrpcs;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.exposeds.dto.normal.JobDetails;
import com.xxl.job.exposeds.dto.req.XxlJobAddReq;
import com.xxl.job.exposeds.dto.req.XxlJobUpdateReq;
import com.xxl.job.exposeds.dto.req.XxlRepeatJobAddReq;
import com.xxl.job.exposeds.dto.res.JobParamRes;

import java.util.List;

public interface XxlJobRpcService {

    //获取当前任务参数
    JobParamRes getJobParam(XxlJobContext context);

    //通过业务id查询job
    List<JobDetails> businessIdSelect(String relateId);

    //通过业务id和执行器名称查询任务详情
    List<JobDetails> businessIdSelect(String relateId, String executorHandler);

    //添加任务
    Integer jobAdd(XxlJobAddReq xxlJobAddReq);

    //添加重复任务
    Integer repeatJobAdd(XxlRepeatJobAddReq repeatJobAddReq);

    //修改任务
    void jobUpdate(XxlJobUpdateReq xxlJobUpdateReq);

    //删除任务
    void jobDel(Integer id);

    //开始任务
    void jobStart(Integer id);

    //暂停任务
    void jobStop(Integer id);


}
