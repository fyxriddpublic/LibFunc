package com.fyxridd.lib.func.api;

import com.fyxridd.lib.func.FuncPlugin;
import com.fyxridd.lib.func.api.annotation.FuncType;
import org.bukkit.entity.Player;

public class FuncApi {
    /**
     * 注册格式
     * 必须在插件自身数据文件夹内的config.yml文件里有个func项配置
     */
    public static void registerFormat(String plugin) {
        FuncPlugin.instance.getFuncManager().registerFormat(plugin);
    }
    
    /**
     * 注册功能
     * 同一个插件可注册多次,但对于同一类型,功能名不能重复
     * @param funcInstance 要注册的实例
     */
    public static void registerFunc(String plugin, Object funcInstance) {
        FuncPlugin.instance.getFuncManager().registerFunc(plugin, funcInstance);
    }

    /**
     * 检测玩家是否调用功能
     * @param p 玩家
     * @param msg (玩家发出的)完整的信息
     * @return 是否是调用功能
     */
    public static boolean checkOnFunc(Player p, String msg) {
        return FuncPlugin.instance.getFuncManager().checkOnFunc(p, msg);
    }

    /**
     * 玩家触发功能时调用
     * @param p 玩家
     * @param type 类型
     * @param plugin 插件
     * @param func 功能名
     * @param value 只包含变量的字符串,可为空字符串不为null
     */
    public static void onFunc(Player p, FuncType.Type type, String plugin, String func, String value) {
        FuncPlugin.instance.getFuncManager().onFunc(p, type, plugin, func, value);
    }
}
