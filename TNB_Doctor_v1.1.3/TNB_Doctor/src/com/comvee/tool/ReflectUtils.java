package com.comvee.tool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by friendlove-pc on 15/7/29.
 */
public  class ReflectUtils {

    public static boolean isBooleanType(Class<?> c) {
        return boolean.class == c || Boolean.class == c;
    }

    public static boolean isIntegerType(Class<?> c) {
        return int.class == c || Integer.class == c;
    }

    public static boolean isStringType(Class<?> c) {
        return String.class == c;
    }

    public static boolean isFloatType(Class<?> c) {
        return float.class == c || Float.class == c;
    }

    public static boolean isLongType(Class<?> c) {
        return long.class == c || Long.class == c;
    }

    public static boolean isDoubleType(Class<?> c) {
        return double.class == c || Double.class == c;
    }

    public static boolean isListType(Class<?> c){
        return ArrayList.class == c || List.class == c || LinkedList.class == c;
    }


}
