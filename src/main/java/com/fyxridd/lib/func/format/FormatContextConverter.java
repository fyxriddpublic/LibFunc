package com.fyxridd.lib.func.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.fyxridd.lib.core.api.config.convert.ConfigConvert.ConfigConverter;
import com.fyxridd.lib.func.FuncPlugin;
import com.fyxridd.lib.func.api.annotation.FuncType.Type;
import com.fyxridd.lib.func.impl.FormatContextImpl;
import com.fyxridd.lib.func.manager.FuncManager.ParamElement;

public class FormatContextConverter implements ConfigConverter<FormatContext> {
    @Override
    public FormatContext convert(String plugin, ConfigurationSection config) throws Exception {
        Map<Type, Map<String, List<ParamElement>>> map = new HashMap<>();
        
        if (config.contains("cmds")) {
            Map<String, List<ParamElement>> paramElements = new HashMap<>();
            map.put(Type.CMD, paramElements);
            
            ConfigurationSection cs = config.getConfigurationSection("cmds");
            for (String funcName:cs.getValues(false).keySet()) paramElements.put(funcName, FuncPlugin.instance.getFuncManager().getParams(cs.getString(funcName)));
        }

        if (config.contains("items")) {
            Map<String, List<ParamElement>> paramElements = new HashMap<>();
            map.put(Type.ITEM, paramElements);
            
            ConfigurationSection cs = config.getConfigurationSection("items");
            for (String funcName:cs.getValues(false).keySet()) paramElements.put(funcName, FuncPlugin.instance.getFuncManager().getParams(cs.getString(funcName)));
        }

        if (config.contains("chats")) {
            Map<String, List<ParamElement>> paramElements = new HashMap<>();
            map.put(Type.CHAT, paramElements);
            
            ConfigurationSection cs = config.getConfigurationSection("chats");
            for (String funcName:cs.getValues(false).keySet()) paramElements.put(funcName, FuncPlugin.instance.getFuncManager().getParams(cs.getString(funcName)));
        }
        
        return new FormatContextImpl(map);
    }
}
