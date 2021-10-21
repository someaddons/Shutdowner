package com.shutdowner.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonConfiguration
{
    public final ForgeConfigSpec.IntValue                            maxShutDownTime;
    public final ForgeConfigSpec.BooleanValue                        shouldDetectShutDownHang;
    public final ForgeConfigSpec.BooleanValue                        shouldDetectHang;
    public final ForgeConfigSpec.BooleanValue                        shouldAutoShutDown;
    public final ForgeConfigSpec.BooleanValue                        printThreads;
    public final ForgeConfigSpec.ConfigValue<String>                 disconnectMessage;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> shutdownMessages;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> shutdownTimes;

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

        builder.comment("Set the disconnect message for the players");
        disconnectMessage = builder.define("disconnectMessage", "Server shutting down");

        builder.comment("Shutdown timepoints,, format: [\"time1\",\"time2\"] e.g. [\"18:00\",\"23:00\"]");
        shutdownTimes = builder.defineList("shutdownTimes",
          new ArrayList<>(Arrays.asList(
            "3:00",
            "11:00",
            "16:00"))
          , e -> e instanceof String && ((String) e).contains(":"));

        builder.comment("Shutting down timed messages, format: [secondsToShutdown;Message] e.g. [\"300;Server is restarting in 5min\",\"150;Server is restarting in 2.5min\"]");
        shutdownMessages = builder.defineList("shutdownMessages",
          new ArrayList<>(Arrays.asList(
            "300;Server is restarting in 5min",
            "180;3 minutes till shutdown",
            "120;2 minutes till shutdown",
            "60;1 minute till shutdown",
            "30;30 sec till shutdown",
            "10;10 sec till shuwdown",
            "9;9",
            "8;8",
            "7;7",
            "6;6",
            "5;5",
            "4;4",
            "3;3",
            "2;2",
            "1;1",
            "0;Shutting down now"))
          , e -> e instanceof String && ((String) e).contains(";"));

        builder.pop();

        builder.push("server runtime hang settings");

        builder.comment("Whether to detect and close the server hang during runtime default: true");
        shouldDetectHang = builder.define("shouldDetectHang", true);
        // Escapes the current category level
        builder.pop();
        builder.pop();
    }
}
