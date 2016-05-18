package com.fyxridd.lib.func.api.format;

import java.util.Map;

import com.fyxridd.lib.func.api.annotation.FuncType.Type;

/**
 * 格式上下文
 * (指定义在配置文件中的格式的相关信息)
 */
public interface FormatContext {
    /**
     * @param type 类型
     * @param func 功能
     * @param value 只包含变量相关信息,可为空字符串不为null
     * @return '变量名 变量值'的映射表,不为null
     */
    Map<String, String> get(Type type, String func, String value);
}
