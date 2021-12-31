package com.xxl.job.admin.utils;///*

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;


@Component
public class SpringUtils {

    /**
     * applicationContext
     */
    public static ApplicationContext applicationContext;

    @Resource
    private ApplicationContext applicationContext2;

    @PostConstruct
    public void initial() {
        SpringUtils.applicationContext = applicationContext2;
    }

    /**
     * 获取实例
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 获取bean名称
     */
    public static String[] getBeanNames(Class<?> beanClass) {
        return applicationContext.getBeanNamesForType(beanClass);
    }

    /**
     * 获取实例
     *
     * @param name Bean名称
     * @param type Bean类型
     * @return 实例
     */
    public static <T> T getBean(String name, Class<T> type) {
        return applicationContext.getBean(name, type);
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    public static Optional<ApplicationContext> tryGetApplicationContext() {
        return Optional.ofNullable(applicationContext);
    }

}