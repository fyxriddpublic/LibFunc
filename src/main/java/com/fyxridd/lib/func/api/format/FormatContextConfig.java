package com.fyxridd.lib.func.api.format;

import com.fyxridd.lib.core.api.config.basic.Path;
import com.fyxridd.lib.core.api.config.convert.ConfigConvert;

public class FormatContextConfig {
    @Path("func")
    @ConfigConvert(FormatContextConverter.class)
    private FormatContext formats;

    public FormatContext getFormats() {
        return formats;
    }
}
