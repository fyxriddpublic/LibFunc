package com.fyxridd.lib.func.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuncType {
    public enum Type {
        CMD,
        CHAT,
        ITEM
        ;
    }
    
    Type value();
}
