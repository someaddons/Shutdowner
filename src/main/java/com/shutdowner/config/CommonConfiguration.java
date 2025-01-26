package com.shutdowner.config;

import com.cupboard.config.CupboardConfig;
import com.cupboard.config.ICommonConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shutdowner.Shutdowner;
import com.shutdowner.handlers.ShutDownHandler;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonConfiguration implements ICommonConfig
{
    public static CupboardConfig<CommonConfiguration> config = new CupboardConfig<>("shutdowner", new CommonConfiguration());

    public int          maxShutDownTime          = 120;
    public boolean      shouldDetectShutDownHang = true;
    public boolean      shouldDetectHang         = true;
    public boolean      shouldAutoShutDown       = true;
    public boolean      printThreads             = true;
    public String       disconnectMessage        = "Server shutting down";
    public List<String> shutdownMessages         = new ArrayList<>(Arrays.asList(
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
        "0;Shutting down now"));
    public List<String> shutdownTimes            = new ArrayList<>(Arrays.asList("3:00", "11:00", "16:00"));

    @Override
    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:",
            "Whether to kill the server when shutdown takes too long. default: true");
        entry.addProperty("shouldDetectShutDownHang", shouldDetectShutDownHang);
        root.add("shouldDetectShutDownHang", entry);


        final JsonObject entry2 = new JsonObject();
        entry2.addProperty("desc:",
            "The maximum time the shutdown is allowed to take. default: 120s");
        entry2.addProperty("maxShutDownTime", maxShutDownTime);
        root.add("maxShutDownTime", entry2);

        final JsonObject entry3 = new JsonObject();
        entry3.addProperty("desc:",
            "Print remaining threads to log? default:true");
        entry3.addProperty("printThreads", printThreads);
        root.add("printThreads", entry3);

        final JsonObject entry4 = new JsonObject();
        entry4.addProperty("desc:",
            "Whether to use the timed shutdown default: true");
        entry4.addProperty("shouldAutoShutDown", shouldAutoShutDown);
        root.add("shouldAutoShutDown", entry4);

        final JsonObject entry5 = new JsonObject();
        entry5.addProperty("desc:",
            "Set the disconnect message for the players");
        entry5.addProperty("disconnectMessage", disconnectMessage);
        root.add("disconnectMessage", entry5);


        final JsonObject entry8 = new JsonObject();
        entry8.addProperty("desc:",
            "Whether to detect and close the server hang during runtime default: true");
        entry8.addProperty("shouldDetectHang", shouldDetectHang);
        root.add("shouldDetectHang", entry8);

        final JsonObject entry6 = new JsonObject();
        final JsonArray times = new JsonArray();
        entry6.addProperty("desc:",
            "Shutdown timepoints, format: [\"time1\",\"time2\"] e.g. [\"18:00\",\"23:00\"]");
        for (final String time : shutdownTimes)
        {
            times.add(time);
        }
        entry6.add("shutdownTimes", times);
        root.add("shutdownTimes", entry6);

        final JsonObject entry7 = new JsonObject();
        final JsonArray messages = new JsonArray();
        entry7.addProperty("desc:",
            "Shutting down timed messages, format: [secondsToShutdown;Message] e.g. [\"300;Server is restarting in 5min\",\"150;Server is restarting in 2.5min\"]");
        for (final String time : shutdownMessages)
        {
            messages.add(time);
        }
        entry7.add("shutdownMessages", messages);
        root.add("shutdownMessages", entry7);

        return root;
    }

    public void parseConfig()
    {
        ShutDownHandler.announcements.clear();
        for (final String data : config.getCommonConfig().shutdownMessages)
        {
            final String[] splitData = data.split(";");
            if (splitData.length != 2)
            {
                Shutdowner.LOGGER.warn("Mistake in config entry: " + data + " wrong format.");
                continue;
            }

            try
            {
                final int time = Integer.parseInt(splitData[0]);
                ShutDownHandler.announcements.add(new Tuple<>(time, splitData[1]));
            }
            catch (Exception e)
            {
                Shutdowner.LOGGER.warn("Mistake in config entry: " + data, e);
            }
        }

        ShutDownHandler.shutdownTimes.clear();
        for (final String data : config.getCommonConfig().shutdownTimes)
        {
            final String[] splitData = data.split(":");
            if (splitData.length != 2)
            {
                Shutdowner.LOGGER.warn("Mistake in config entry: " + data + " wrong format.");
                continue;
            }

            try
            {
                final int hour = Integer.parseInt(splitData[0]);
                final int minutes = Integer.parseInt(splitData[1]);
                ShutDownHandler.shutdownTimes.add(new Tuple<>(hour, minutes));
            }
            catch (Exception e)
            {
                Shutdowner.LOGGER.warn("Mistake in config entry: " + data, e);
            }
        }
    }

    @Override
    public void deserialize(final JsonObject data)
    {
        shouldDetectShutDownHang = data.get("shouldDetectShutDownHang").getAsJsonObject().get("shouldDetectShutDownHang").getAsBoolean();
        maxShutDownTime = data.get("maxShutDownTime").getAsJsonObject().get("maxShutDownTime").getAsInt();
        printThreads = data.get("printThreads").getAsJsonObject().get("printThreads").getAsBoolean();
        shouldAutoShutDown = data.get("shouldAutoShutDown").getAsJsonObject().get("shouldAutoShutDown").getAsBoolean();
        shouldDetectHang = data.get("shouldDetectHang").getAsJsonObject().get("shouldDetectHang").getAsBoolean();
        disconnectMessage = data.get("disconnectMessage").getAsJsonObject().get("disconnectMessage").getAsString();
        shutdownTimes = new ArrayList<>();
        for (final JsonElement listElement : data.get("shutdownTimes").getAsJsonObject().get("shutdownTimes").getAsJsonArray())
        {
            shutdownTimes.add(listElement.getAsString());
        }
        shutdownMessages = new ArrayList<>();
        for (final JsonElement listElement : data.get("shutdownMessages").getAsJsonObject().get("shutdownMessages").getAsJsonArray())
        {
            shutdownMessages.add(listElement.getAsString());
        }

        parseConfig();
    }
}
