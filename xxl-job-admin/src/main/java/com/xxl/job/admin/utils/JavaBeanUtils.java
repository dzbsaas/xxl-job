package com.xxl.job.admin.utils;//package com.seetech.util;


import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.util.ArrayList;
import java.util.List;

public class JavaBeanUtils {

    /**
     * 使用cglib提供的转换器
     *
     * @param sourceObj 源对象
     * @param targerObj 目标对象
     */
    public static void copy(Object sourceObj, Object targerObj) {
        BeanCopier beanCopier = BeanCopier.create(sourceObj.getClass(), targerObj.getClass(), false);
        beanCopier.copy(sourceObj, targerObj, null);
    }

    /**
     * 使用自定义转换器
     *
     * @param sourceObj 源对象
     * @param targerObj 目标对象
     * @param converter 自定义转换器
     */
    public static void copy(Object sourceObj, Object targerObj, Converter converter) {
        BeanCopier beanCopier = BeanCopier.create(sourceObj.getClass(), targerObj.getClass(), true);
        beanCopier.copy(sourceObj, targerObj, converter);
    }

    /**
     * 将 list 对象拷贝成目标 list
     *
     * @param sourceObj   源集合
     * @param targerClass 目标class
     */
    public static <T> List<T> copy(List<?> sourceObj, Class<T> targerClass) throws IllegalAccessException, InstantiationException {
        List<T> targer = new ArrayList<>();
        for (Object source : sourceObj) {
            T t = targerClass.newInstance();
            BeanCopier beanCopier = BeanCopier.create(source.getClass(), targerClass, false);
            beanCopier.copy(source, t, null);
            targer.add(t);
        }
        return targer;
    }

}
