package com.fyxridd.lib.func;

import org.bukkit.entity.Player;

import com.fyxridd.lib.func.api.annotation.Func;
import com.fyxridd.lib.func.api.annotation.FuncType;
import com.fyxridd.lib.func.api.annotation.FuncType.Type;
import com.fyxridd.lib.func.api.annotation.Param;

@FuncType(Type.CMD)
public class TestFuncManager {
    @Func("info")
    public void info(Player p) {
    }

    @Func("kill")
    public void kill(Player p, @Param("tar") String tar) {
    }
}
