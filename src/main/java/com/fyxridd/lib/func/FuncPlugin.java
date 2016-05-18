package com.fyxridd.lib.func;

import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.func.config.FuncConfig;
import com.fyxridd.lib.func.manager.FuncManager;

public class FuncPlugin extends SimplePlugin{
    public static FuncPlugin instance;

    private FuncManager funcManager;
    
    @Override
    public void onEnable() {
        instance = this;

        //注册配置
        ConfigApi.register(pn, FuncConfig.class);

        funcManager = new FuncManager();
        
        super.onEnable();
    }

    public FuncManager getFuncManager() {
        return funcManager;
    }
}