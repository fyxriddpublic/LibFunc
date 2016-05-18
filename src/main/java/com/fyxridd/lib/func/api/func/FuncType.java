package com.fyxridd.lib.func.api.func;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuncType {
    enum Type {
        CMD,
        CHAT,
        ITEM
        ;
    }
    
    Type value();
}
