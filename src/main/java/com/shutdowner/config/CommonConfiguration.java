package com.shutdowner.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration
{
    public final ForgeConfigSpec.IntValue     maxShutDownTime;
    public final ForgeConfigSpec.BooleanValue shouldDetectShutDownHang;
    public final ForgeConfigSpec.BooleanValue shouldDetectHang;
    public final ForgeConfigSpec.BooleanValue shouldAutoShutDown;
    public final ForgeConfigSpec.BooleanValue printThreads;
    public final ForgeConfigSpec.IntValue     shutDownInterval;

    protected CommonConfiguration(final ForgeConfigSpec.Builder builder)
    {
        builder.push("Shutdowner settings");

        builder.push("server shutdown hang settings");

        builder.comment("Whether to kill the server when shutdown takes too long. default: true");
        shouldDetectShutDownHang = builder.define("shouldDetectShutDownHang", true);

        builder.comment("The maximum time the shutdown is allowed to take. default: 120s");
        maxShutDownTime = builder.defineInRange("maxShutDownTime", 120, 1, 20000);

        builder.comment("Print remaining threads to log? default:true");
        printThreads = builder.define("printThreads", true);

        builder.pop();

        builder.push("server timed shutdown settings");

        builder.comment("Whether to use the timed shutdown default: true");
        shouldAutoShutDown = builder.define("shouldAutoShutDown", true);

        builder.comment("Shutdown ever X minutes default: 180");
        shutDownInterval = builder.defineInRange("shutDownInterval", 180, 1, 20000);

        builder.pop();

        builder.push("server runtime hang settings");

        builder.comment("Whether to detect and close the server hang during runtime default: true");
        shouldDetectHang = builder.define("shouldDetectHang", true);
        // Escapes the current category level
        builder.pop();
        builder.pop();
    }
}
