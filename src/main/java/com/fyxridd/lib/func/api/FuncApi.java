package com.fyxridd.lib.func.api;

import com.fyxridd.lib.func.FuncPlugin;

public class FuncApi {
    /**
     * 注册格式
     * 必须在插件自身数据文件夹内的config.yml文件里有个func项配置
     */
    public void registerFormat(String plugin) {
        FuncPlugin.instance.getFuncManager().registerFormat(plugin);
    }
    
    /**
     * 注册功能
     * 同一个插件可注册多次,但对于同一类型,功能名不能重复
     * @param funcClass 此类必须有个空的构造器
     */
    public void registerFunc(String plugin, Class<?> funcClass) {
        FuncPlugin.instance.getFuncManager().registerFunc(plugin, funcClass);
    }
}
