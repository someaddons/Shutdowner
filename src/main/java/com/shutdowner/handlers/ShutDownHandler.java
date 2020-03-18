package com.shutdowner.handlers;

import com.shutdowner.Shutdowner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles timed shutdown and its announcements.
 */
public class ShutDownHandler
{
    private static       int  tickTimer         = 0;
    private static final int  TICK_INTERVAL     = 20;
    private static       long shutdownInterval  = Shutdowner.getConfig().getCommonConfig().shutDownInterval.get() * 60;
    private static       long serverStartedTime = 0;
    private static       int  secondsPassed     = 0;

    private static int announceMentIndex = 0;

    private static final List<Tuple<Integer, String>> announcements = new ArrayList<>();

    public static void onServerTick()
    {
        if (++tickTimer == TICK_INTERVAL && Shutdowner.getConfig().getCommonConfig().shouldAutoShutDown.get())
        {
            tickTimer = 0;

            secondsPassed = Math.round((System.currentTimeMillis() - serverStartedTime) / 1000f);
            announceShutdown();

            if (secondsPassed > shutdownInterval)
            {
                ServerLifecycleHooks.getCurrentServer()
                  .getPlayerList()
                  .getPlayers()
                  .forEach(player -> player.connection.disconnect(new StringTextComponent("Server shutting down")));

                if (secondsPassed > shutdownInterval + 1)
                {
                    ServerLifecycleHooks.getCurrentServer()
                      .getPlayerList()
                      .getPlayers()
                      .forEach(player -> player.connection.disconnect(new StringTextComponent("Server shutting down")));
                    serverStartedTime = System.currentTimeMillis();
                    ServerLifecycleHooks.getCurrentServer().initiateShutdown(false);
                }
            }
        }
    }

    public static void announceShutdown()
    {
        long secondsLeft = shutdownInterval - secondsPassed;
        if (announceMentIndex < announcements.size() && secondsLeft <= announcements.get(announceMentIndex).getA())
        {
            Shutdowner.LOGGER.info(announcements.get(announceMentIndex).getB());
            for (final PlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
            {
                player.sendMessage(new StringTextComponent(announcements.get(announceMentIndex).getB()));
            }
            announceMentIndex++;
        }
    }

    /**
     * Resets the current shutdown
     */
    private static void reset()
    {
        serverStartedTime = System.currentTimeMillis();
        announceMentIndex = 0;
    }

    public static void onServerStart()
    {
        announcements.clear();
        announcements.add(new Tuple<>(300, "Server is shutting down in 5min"));
        announcements.add(new Tuple<>(180, "3 minutes till shutdown"));
        announcements.add(new Tuple<>(120, "2 minutes till shutdown"));
        announcements.add(new Tuple<>(60, "1 minute till shutdown"));
        announcements.add(new Tuple<>(30, "30 sec till shutdown"));
        announcements.add(new Tuple<>(10, "10 sec till shuwdown"));
        announcements.add(new Tuple<>(9, "9"));
        announcements.add(new Tuple<>(8, "8"));
        announcements.add(new Tuple<>(7, "7"));
        announcements.add(new Tuple<>(6, "6"));
        announcements.add(new Tuple<>(5, "5"));
        announcements.add(new Tuple<>(4, "4"));
        announcements.add(new Tuple<>(3, "3"));
        announcements.add(new Tuple<>(2, "2"));
        announcements.add(new Tuple<>(1, "1"));
        announcements.add(new Tuple<>(0, "Shutting down now"));

        reset();
    }
}
