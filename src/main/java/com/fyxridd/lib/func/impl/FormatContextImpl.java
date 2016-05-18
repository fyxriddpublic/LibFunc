package com.fyxridd.lib.func.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.func.api.func.FuncType.Type;
import com.fyxridd.lib.func.format.FormatContext;
import com.fyxridd.lib.func.manager.FuncManager.ParamElement;

public class FormatContextImpl implements FormatContext{
    //类型 功能名 变量元素列表
    private Map<Type, Map<String, List<ParamElement>>> map;

    public FormatContextImpl(Map<Type, Map<String, List<ParamElement>>> map) {
        super();
        this.map = map;
    }

    @Override
    public Map<String, String> get(Type type, String func, String value) {
        Map<String, String> result = new HashMap<>();
        
        Map<String, List<ParamElement>> paramElements = map.get(type);
        if (paramElements != null) {
            List<ParamElement> list = paramElements.get(func);
            if (list != null) {
                String[] args = value.split(" ");
                for (int index=0;index<list.size();index++) {
                    boolean last = (index == list.size()-1);
                    ParamElement e = list.get(index);
                    String v;
                    if (!last) v = args[index];
                    else if (e.isOptional() && args.length < list.size()) v = "";
                    else if (e.isExtend()) v = UtilApi.combine(args, " ", index, args.length-1);
                    else v = args[index];
                    result.put(e.getName(), v);
                }
            }
        }
        return result;
    }
    
}
