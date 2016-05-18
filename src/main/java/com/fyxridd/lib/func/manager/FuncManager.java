package com.fyxridd.lib.func.manager;

import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.event.PlayerChatEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.api.log.Level;
import com.fyxridd.lib.core.api.log.LogApi;
import com.fyxridd.lib.core.config.ConfigManager.Setter;
import com.fyxridd.lib.func.FuncPlugin;
import com.fyxridd.lib.func.api.FuncApi;
import com.fyxridd.lib.func.api.func.Extend;
import com.fyxridd.lib.func.api.func.Func;
import com.fyxridd.lib.func.api.func.FuncType;
import com.fyxridd.lib.func.api.func.FuncType.Type;
import com.fyxridd.lib.func.api.func.Optional;
import com.fyxridd.lib.func.config.FuncConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.EventExecutor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FuncManager {
    /**
     * 功能上下文
     */
    private class FuncContext {
        //方法所属的实例
        private Object instance;
        //方法
        private Method method;

        //最后一个变量是否可选
        private boolean lastOptional;
        //最后一个变量是否延展
        private boolean lastExtend;

        public FuncContext(Object instance, Method method, boolean lastOptional, boolean lastExtend) {
            this.instance = instance;
            this.method = method;
            this.lastOptional = lastOptional;
            this.lastExtend = lastExtend;
        }

        public Object getInstance() {
            return instance;
        }

        public Method getMethod() {
            return method;
        }

        public boolean isLastOptional() {
            return lastOptional;
        }

        public boolean isLastExtend() {
            return lastExtend;
        }
    }
    
    public static final String LOG_TO_USER = "Func-ToUser";
    public static final String LOG_TO_PROGRAMMER = "Func-ToProgrammer";
    
    private FuncConfig config;

    //插件名 功能类型 功能名 功能上下文
    private Map<String, Map<Type, Map<String, FuncContext>>> handlers = new HashMap<>();
    
    public FuncManager() {
        //注册日志上下文
        LogApi.register(LOG_TO_USER);
        LogApi.register(LOG_TO_PROGRAMMER);
        //添加配置监听
        ConfigApi.addListener(FuncPlugin.instance.pn, FuncConfig.class, new Setter<FuncConfig>() {
            @Override
            public void set(FuncConfig value) {
                config = value;
            }
        });
        //注册事件
        {
            //聊天事件
            Bukkit.getPluginManager().registerEvent(PlayerChatEvent.class, FuncPlugin.instance, EventPriority.LOW, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerChatEvent event = (PlayerChatEvent) e;
                    if (checkOnFunc(event.getP(), event.getMsg())) event.setCancelled(true);
                }
            }, FuncPlugin.instance, true);
            //命令事件
            Bukkit.getPluginManager().registerEvent(PlayerCommandPreprocessEvent.class, FuncPlugin.instance, EventPriority.LOW, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerCommandPreprocessEvent event = (PlayerCommandPreprocessEvent) e;
                    if (checkOnFunc(event.getPlayer(), event.getMessage())) event.setCancelled(true);
                }
            }, FuncPlugin.instance, true);
        }
    }

    /**
     * @see FuncApi#register(String, Object)
     */
    public void register(String plugin, Object funcInstance) {
        try {
            Class<?> funcClass = funcInstance.getClass();

            Map<Type, Map<String, FuncContext>> m = handlers.get(plugin);
            if (m == null) {
                m = new HashMap<>();
                handlers.put(plugin, m);
            }
            
            FuncType funcType = funcClass.getAnnotation(FuncType.class);
            if (funcType == null) throw new Exception("FuncType can't be null!");//类必须有@FuncType标记
            Map<String, FuncContext> funcMap = m.get(funcType.value());
            if (funcMap == null) {
                funcMap = new HashMap<>();
                m.put(funcType.value(), funcMap);
            }
            
            for (Method method:funcClass.getDeclaredMethods()) {
                Func func = method.getAnnotation(Func.class);
                if (func != null) {
                    if (funcMap.containsKey(func.value())) throw new Exception("duplicate func name '"+func.value()+"'");//功能名重复

                    boolean lastOptional = false;
                    boolean lastExtend = false;

                    Annotation[][] annotations = method.getParameterAnnotations();
                    for (Annotation a:annotations[annotations.length-1]) {
                        if (a instanceof Optional) lastOptional = true;
                        else if (a instanceof Extend) lastExtend = true;
                    }

                    funcMap.put(func.value(), new FuncContext(funcInstance, method, lastOptional, lastExtend));
                }
            }
        } catch (Exception e) {
            //服主看
            LogApi.log(LOG_TO_USER, Level.SEVERE, e.getMessage());
            //程序员看
            LogApi.log(LOG_TO_PROGRAMMER, Level.SEVERE, UtilApi.convertException(e));
        }
    }

    /**
     * @see FuncApi#checkOnFunc(Player, String)
     */
    public boolean checkOnFunc(CommandSender sender, String msg) {
        if (msg == null) return false;

        //Type
        Type type;
        int _symbolLength;
        {
            if (msg.startsWith(config.getCmdSymbol())) {
                type = Type.CMD;
                _symbolLength = config.getCmdSymbol().length();
            }else if (msg.startsWith(config.getItemSymbol())) {
                type = Type.ITEM;
                _symbolLength = config.getItemSymbol().length();
            }else if (msg.startsWith(config.getChatSymbol())) {
                type = Type.CHAT;
                _symbolLength = config.getChatSymbol().length();
            }else return false;
        }

        //是功能调用

        //功能格式错误
        String[] _args = msg.substring(_symbolLength).split(" ", 3);
        if (_args.length < 2) {
            MessageApi.sendGraceful(sender, get(sender instanceof Player?sender.getName():null, 10), true);
            return true;
        }

        //plugin
        String plugin = _args[0];

        //func
        String func = _args[1];

        //value
        String value = _args.length > 2?_args[2]:"";

        //执行功能
        onFunc(sender, type, plugin, func, value);

        return true;
    }

    /**
     * @see FuncApi#onFunc(Player, Type, String, String, String)
     */
    public void onFunc(CommandSender sender, Type type, String plugin, String func, String value) {
        try {
            //获取功能上下文
            Map<Type, Map<String, FuncContext>> map2 = handlers.get(plugin);
            if (map2 != null) {
                Map<String, FuncContext> map3 = map2.get(type);
                if (map3 != null) {
                    FuncContext funcContext = map3.get(func);
                    if (funcContext != null) {
                        String[] ss = value.split(" ");
                        Object[] args = new Object[funcContext.getMethod().getParameterTypes().length];
                        args[0] = sender;
                        //最后一个前
                        for (int index=1;index<args.length-1;index++) args[index] = ss[index-1];
                        //最后一个
                        String last;
                        if (funcContext.isLastOptional() && ss.length < args.length-1) last = "";
                        else if (funcContext.isLastExtend()) last = UtilApi.combine(ss, " ", args.length-2, ss.length-1);
                        else last = ss[args.length-2];
                        args[args.length-1] = last;
                        //调用方法
                        funcContext.getMethod().invoke(funcContext.getInstance(), args);
                    }
                }
            }
        } catch (Exception e) {
            MessageApi.sendGraceful(sender, get(sender instanceof Player?sender.getName():null, 20), true);
            CoreApi.debug(UtilApi.convertException(e));
        }
    }

    private FancyMessage get(String player, int id, Object... args) {
        return config.getLang().get(player, id, args);
    }
}
