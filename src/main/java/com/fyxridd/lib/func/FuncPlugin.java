package com.fyxridd.lib.func;

import com.fyxridd.lib.core.api.SqlApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.core.config.ConfigManager;
import com.fyxridd.lib.func.manager.FuncManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.io.File;

public class FuncPlugin extends SimplePlugin{
    public static FuncPlugin instance;

    private FuncManager funcManager;
    
    @Override
    public void onEnable() {
        instance = this;

        funcManager = new FuncManager();
        
        super.onEnable();
    }

    public FuncManager getFuncManager() {
        return funcManager;
    }
}