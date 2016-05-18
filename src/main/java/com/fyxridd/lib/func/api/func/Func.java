package com.fyxridd.lib.func.api.func;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能
 * 方法变量类型允许:
 * Boolean
 * Byte
 * Short
 * Integer
 * Long
 * Float
 * Double
 * String (通用)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Func {
    /**
     * 功能名
     */
    String value();
}
