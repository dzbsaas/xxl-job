package com.xxl.job.admin.rpcservice;

import com.seetech.rpcmodules.test.TestService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class TestServiceImpl implements TestService {

    @Override
    public String testDubbo(String name) {
        System.out.println("dubbo调用成功！！");
        return name;
    }
}
