package com.fyxridd.lib.func.api.func;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认值(如果没有提供此变量,则使用的默认值)
 * (只能放在最后一个变量上)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
    String value();
}
