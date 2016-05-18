package com.fyxridd.lib.func.format;

import java.util.Map;

import com.fyxridd.lib.func.api.func.FuncType.Type;

/**
 * 格式上下文
 * (指定义在配置文件中的功能变量格式)
 */
public interface FormatContext {
    /**
     * 获取变量-值的映射
     * @param type 类型
     * @param func 功能
     * @param value 只包含变量相关信息,可为空字符串不为null
     * @return '变量名 变量值'的映射表,不为null
     */
    Map<String, String> get(Type type, String func, String value);
}
