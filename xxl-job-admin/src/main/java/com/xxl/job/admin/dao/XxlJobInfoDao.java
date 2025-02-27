package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.JobRepeatRecord;
import com.xxl.job.admin.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface XxlJobInfoDao {

    public List<XxlJobInfo> pageList(@Param("offset") int offset,
                                     @Param("pagesize") int pagesize,
                                     @Param("jobGroup") int jobGroup,
                                     @Param("triggerStatus") int triggerStatus,
                                     @Param("jobDesc") String jobDesc,
                                     @Param("executorHandler") String executorHandler,
                                     @Param("author") String author);

    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("jobGroup") int jobGroup,
                             @Param("triggerStatus") int triggerStatus,
                             @Param("jobDesc") String jobDesc,
                             @Param("executorHandler") String executorHandler,
                             @Param("author") String author);

    public int save(XxlJobInfo info);

    public XxlJobInfo loadById(@Param("id") int id);

    public int update(XxlJobInfo xxlJobInfo);

    public int updateParams(XxlJobInfo xxlJobInfo);

    public int delete(@Param("id") long id);

    public List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

    public int findAllCount();

    public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize);

    public int scheduleUpdate(XxlJobInfo xxlJobInfo);

    public XxlJobInfo selOneJob(@Param("id") int id);

    public List<XxlJobInfo> businessIdSelect(@Param("id") String id);

    public List<XxlJobInfo> businessIdAndExecutorHandler(@Param("id") String id, @Param("executorHandler") String executorHandler);

    public void saveJobRepeatRecord(JobRepeatRecord jobRepeatRecord);

    public List<JobRepeatRecord> selectListJobRepeatRecord(@Param("jobFlag") String jobFlag);

    public void updateJobRepeatRecord(@Param("id") int id, @Param("surplusRunTimes") int surplusRunTimes, @Param("status") int status);

    public void delJobRepeatRecord(@Param("id") int id);
}
