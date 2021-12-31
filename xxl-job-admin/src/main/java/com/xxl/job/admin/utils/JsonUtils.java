package com.xxl.job.admin.utils;///*

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;


/**
 * Utils - JSON
 */
public final class JsonUtils {

    /**
     * ObjectMapper
     */
    private static ObjectMapper mapper = SpringUtils.getBean(ObjectMapper.class);

    /**
     * jackson内部使用缓存保存序列化处理器,后续调用默认使用缓存处理器
     * 若需通过其他途径进行json序列化需重新创建ObjectMapper对象.
     * 此对象用于非Jackson注释使用场景,可动态修改json序列化配置信息
     * 详情见
     *
     * @See com.fasterxml.jackson.databind.ser.DefaultSerializerProvider #serializeValue()
     * @See com.fasterxml.jackson.databind.SerializerProvider #findTypedValueSerializer()
     */
    private static ObjectMapper mapper2 = new ObjectMapper();

    /**
     * 不可实例化
     */
    private JsonUtils() {
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param value 对象
     * @return JSOn字符串
     */
    public static String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJsonByFilter(Object value, Class<?> classType, String... ignorableFieldNames) throws JsonProcessingException {

        mapper2.addMixIn(classType, MyFilter.class);
        SimpleBeanPropertyFilter newFilter3 = SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames);


        FilterProvider filterProvider3 = new SimpleFilterProvider().addFilter("myFilter", newFilter3);
        mapper2.setFilterProvider(filterProvider3);
        return mapper2.writeValueAsString(value);
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json      JSON字符串
     * @param valueType 对象类型
     * @return 对象
     */
    public static <T> T toObject(String json, Class<T> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJsonExclude(Object value, Class<?> classType, String... ignorableFieldNames) throws JsonProcessingException {

        mapper2.addMixIn(classType, MyFilter.class);
        SimpleBeanPropertyFilter newFilter3 = SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames);


        FilterProvider filterProvider3 = new SimpleFilterProvider().addFilter("myFilter", newFilter3);
        mapper2.setFilterProvider(filterProvider3);
        return mapper2.writeValueAsString(value);
    }


    public static String toJsonByInclude(Object value, Class<?> classType, String... FieldNames) throws JsonProcessingException {

        mapper2.addMixIn(classType, MyFilter.class);
        SimpleBeanPropertyFilter newFilter3 = SimpleBeanPropertyFilter.filterOutAllExcept(FieldNames);


        FilterProvider filterProvider3 = new SimpleFilterProvider().addFilter("myFilter", newFilter3);
        mapper2.setFilterProvider(filterProvider3);
        return mapper2.writeValueAsString(value);
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json          JSON字符串
     * @param typeReference 对象类型
     * @return 对象
     */
    public static <T> T toObject(String json, TypeReference<?> typeReference) {
        try {
            return (T) mapper.readValue(json, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json     JSON字符串
     * @param javaType 对象类型
     * @return 对象
     */
    public static <T> T toObject(String json, JavaType javaType) {
        try {
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}