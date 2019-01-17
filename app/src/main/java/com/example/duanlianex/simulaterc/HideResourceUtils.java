package com.example.duanlianex.simulaterc;

import java.lang.reflect.Method;

/**
 * Created by duanlian.ex on 2019/1/17.
 */

public class HideResourceUtils {



    //获取系统属性
    public static  String getProperty(String className,String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName(className);
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "unknown" ));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return value;
        }
    }
}
