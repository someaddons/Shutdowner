package com.shutdowner.config;

import com.shutdowner.Shutdowner;
import com.shutdowner.handlers.ShutDownHandler;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class Configuration
{
    /**
     * Loaded everywhere, not synced
     */
    private final CommonConfiguration commonConfig;

    /**
     * Loaded clientside, not synced
     */
    // private final ClientConfiguration clientConfig;

    /**
     * Builds configuration tree.
     */
    public Configuration()
    {
        final Pair<CommonConfiguration, ForgeConfigSpec> com = new ForgeConfigSpec.Builder().configure(CommonConfiguration::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.getRight());

        commonConfig = com.getLeft();
    }

    public CommonConfiguration getCommonConfig()
    {
        return commonConfig;
    }

    /**
     * Parses the config list entries
     */
    public void parseConfig()
    {
        ShutDownHandler.announcements.clear();
        for (final String data : commonConfig.shutdownMessages.get())
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
        for (final String data : commonConfig.shutdownTimes.get())
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
}
