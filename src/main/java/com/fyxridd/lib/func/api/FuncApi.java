package com.fyxridd.lib.func.api;

import com.fyxridd.lib.func.FuncPlugin;
import org.bukkit.entity.Player;

public class FuncApi {
    /**
     * 注册功能类型,实际是添加挂钩,即将指定的功能类型挂钩到指定的前缀上
     * (注册时机不用太早,只需在具体的检测触发功能前注册即可)
     */
    public static void registerTypeHook(String type, String prefix) {
        FuncPlugin.instance.getFuncManager().registerTypeHook(type, prefix);
    }
    
    /**
     * 注册功能
     * 同一个插件可注册多次,但对于同一类型,功能名不能重复
     * @param funcInstance 要注册的实例
     */
    public static void register(String plugin, Object funcInstance) {
        FuncPlugin.instance.getFuncManager().register(plugin, funcInstance);
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
    public static void onFunc(Player p, String type, String plugin, String func, String value) {
        FuncPlugin.instance.getFuncManager().onFunc(p, type, plugin, func, value);
    }
}
