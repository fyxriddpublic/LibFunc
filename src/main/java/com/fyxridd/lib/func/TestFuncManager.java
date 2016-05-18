package com.fyxridd.lib.func;

import com.fyxridd.lib.func.api.func.Func;
import com.fyxridd.lib.func.api.func.FuncType;
import com.fyxridd.lib.func.api.func.Optional;
import org.bukkit.command.CommandSender;

@FuncType(FuncType.Type.CMD)
public class TestFuncManager {
    @Func("buy")
    public void onBuy(CommandSender sender, String id, String confirmPrice, @Optional String amount) {

    }
}
