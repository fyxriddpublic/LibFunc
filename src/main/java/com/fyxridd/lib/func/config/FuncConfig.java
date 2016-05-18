package com.fyxridd.lib.func.config;

import com.fyxridd.lib.core.api.config.basic.Path;
import com.fyxridd.lib.core.api.config.convert.ConfigConvert;
import com.fyxridd.lib.core.api.lang.LangConverter;
import com.fyxridd.lib.core.api.lang.LangGetter;

public class FuncConfig {
    @Path("symbol.cmd")
    private String cmdSymbol;
    @Path("symbol.item")
    private String itemSymbol;
    @Path("symbol.chat")
    private String chatSymbol;

    @Path("lang")
    @ConfigConvert(LangConverter.class)
    private LangGetter lang;

    public String getCmdSymbol() {
        return cmdSymbol;
    }

    public String getItemSymbol() {
        return itemSymbol;
    }

    public String getChatSymbol() {
        return chatSymbol;
    }

    public LangGetter getLang() {
        return lang;
    }
}
