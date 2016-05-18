package com.fyxridd.lib.func.manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.log.Level;
import com.fyxridd.lib.core.api.log.LogApi;
import com.fyxridd.lib.core.config.ConfigManager.Setter;
import com.fyxridd.lib.func.api.FuncApi;
import com.fyxridd.lib.func.api.annotation.Func;
import com.fyxridd.lib.func.api.annotation.FuncType;
import com.fyxridd.lib.func.api.annotation.FuncType.Type;
import com.fyxridd.lib.func.api.annotation.Param;
import com.fyxridd.lib.func.api.format.FormatContext;
import com.fyxridd.lib.func.api.format.FormatContextConfig;

public class FuncManager {
    /**
     * 功能上下文
     */
    private class FuncContext {
        //方法所属的实例
        private Object instance;
        //方法
        private Method method;
        //位置 使用的变量名
        private Map<Integer, String> posToParam;

        public FuncContext(Object instance, Method method, Map<Integer, String> posToParam) {
            this.instance = instance;
            this.method = method;
            this.posToParam = posToParam;
        }

        public Object getInstance() {
            return instance;
        }

        public Method getMethod() {
            return method;
        }

        public Map<Integer, String> getPosToParam() {
            return posToParam;
        }
    }
    
    public class ParamElement {
        //变量名
        private String name;
        //可选
        private boolean optional;
        //延展
        private boolean extend;
        public ParamElement(String name, boolean optional, boolean extend) {
            super();
            this.name = name;
            this.optional = optional;
            this.extend = extend;
        }
        public String getName() {
            return name;
        }
        public boolean isOptional() {
            return optional;
        }
        public boolean isExtend() {
            return extend;
        }
    }
    
    public static final String LOG_TO_USER = "Func-ToUser";
    public static final String LOG_TO_PROGRAMMER = "Func-ToProgrammer";
    
    private static final Pattern paramsPattern = Pattern.compile("\\{([\\?]?)([\\w]+)([\\+]?)\\}");

    //插件名 格式定义配置
    private Map<String, FormatContextConfig> formats = new HashMap<>();
    //插件名 功能类型 功能名 功能上下文
    private Map<String, Map<Type, Map<String, FuncContext>>> handlers = new HashMap<>();
    
    public FuncManager() {
        //注册日志上下文
        LogApi.register(LOG_TO_USER);
        LogApi.register(LOG_TO_PROGRAMMER);
    }

    /**
     * @see FuncApi#registerFormat(String)
     */
    public void registerFormat(final String plugin) {
        //注册配置
        ConfigApi.register(plugin, FormatContextConfig.class);
        //添加配置监听
        ConfigApi.addListener(plugin, FormatContextConfig.class, new Setter<FormatContextConfig>(){
            @Override
            public void set(FormatContextConfig newValue) {
                formats.put(plugin, newValue);
            }
        });
    }
    
    /**
     * @see FuncApi#registerFunc(String, Class)
     */
    public void registerFunc(String plugin, Class<?> funcClass) {
        try {
            Object instance = funcClass.newInstance();
            
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
            
            Method[] methods = funcClass.getDeclaredMethods();
            if (methods != null) {
                for (Method method:methods) {
                    Func func = method.getAnnotation(Func.class);
                    if (func != null) {
                        if (funcMap.containsKey(func.value())) throw new Exception("duplicate func name '"+func.value()+"'");//功能名重复
                        Map<Integer, String> posToParam = new HashMap<>();
                        {
                            //方法第一个变量必然是Player
                            //方法有@Param标记的变量必然是String类型
                            for (int index=0;index<method.getParameterTypes().length;index++) {
                                for (Annotation anno:method.getParameterAnnotations()[index]) {
                                    if (anno instanceof Param) {
                                        posToParam.put(index, ((Param) anno).value());
                                        break;
                                    }
                                }
                            }
                        }
                        funcMap.put(func.value(), new FuncContext(instance, method, posToParam));
                    }
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
     * 玩家触发功能时调用
     * @param p 玩家
     * @param type 类型
     * @param plugin 插件
     * @param func 功能名
     * @param value 只包含变量的字符串,可为空字符串不为null
     */
    public void onFunc(Player p, Type type, String plugin, String func, String value) {
        FormatContextConfig formatContextConfig = formats.get(plugin);
        if (formatContextConfig != null) {
            //变量名 变量值
            Map<String, String> params = formatContextConfig.getFormats().get(type, func, value);
            //获取功能上下文
            Map<Type, Map<String, FuncContext>> map2 = handlers.get(plugin);
            if (map2 != null) {
                Map<String, FuncContext> map3 = map2.get(type);
                if (map3 != null) {
                    FuncContext funcContext = map3.get(func);
                    if (funcContext != null) {
                        Object[] args = new Object[funcContext.getMethod().getParameterTypes().length];
                        args[0] = p;
                        for (int index=1;index<args.length;index++) {
                            String paramName = funcContext.getPosToParam().get(index);
                            String paramValue = params.get(paramName);
                            args[index] = paramValue;
                        }
                        //调用方法
                        try {
                            funcContext.getMethod().invoke(funcContext.getInstance(), args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 获取变量元素集合
     * 变量名在字符串中都以'{[?]变量名[+]}'的格式存在
     * @return 不为null可为空列表
     */
    public List<ParamElement> getParams(String s) {
        List<ParamElement> result = new ArrayList<>();
        if (s != null) {
            Matcher matcher = paramsPattern.matcher(s);
            while (matcher.find()) {
                boolean optional = matcher.group(1).equals("?");
                String name = matcher.group(2);
                boolean extend = matcher.group(3).equals("+");
                
                result.add(new ParamElement(name, optional, extend));
            }
        }
        return result;
    }
}
