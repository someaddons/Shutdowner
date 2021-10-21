package com.shutdowner.handlers;

import com.shutdowner.Shutdowner;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles timed shutdown and its announcements.
 */
public class ShutDownHandler
{
    private static       int  tickTimer         = 0;
    private static final int  TICK_INTERVAL     = 20;
    private static       long shutdownInterval  = 600;
    private static       long serverStartedTime = 0;
    private static       int  secondsPassed     = 0;

    private static int announceMentIndex = 0;

    public static final List<Tuple<Integer, String>>  announcements = new ArrayList<>();
    public static final List<Tuple<Integer, Integer>> shutdownTimes = new ArrayList<>();

    public static void onServerTick()
    {
        if (++tickTimer == TICK_INTERVAL && Shutdowner.getConfig().getCommonConfig().shouldAutoShutDown.get())
        {
            tickTimer = 0;

            secondsPassed = Math.round((System.currentTimeMillis() - serverStartedTime) / 1000f);
            announceShutdown();

            if (secondsPassed > shutdownInterval)
            {
                if (secondsPassed > shutdownInterval + 1)
                {
                    List<ServerPlayer> playerss = new ArrayList<>(
                      ServerLifecycleHooks.getCurrentServer()
                        .getPlayerList()
                        .getPlayers());

                    playerss.forEach(player -> player.connection.disconnect(new TextComponent(Shutdowner.getConfig().getCommonConfig().disconnectMessage.get())));
                    serverStartedTime = System.currentTimeMillis();
                    ServerLifecycleHooks.getCurrentServer().halt(false);
                }
            }
        }
    }

    /**
     * Returns the time to shutdown in seconds
     *
     * @return
     */
    private static long getTimeToNextShutdown()
    {
        long max = 240000;
        for (final Tuple<Integer, Integer> shutdownTime : shutdownTimes)
        {
            LocalTime localShutdownTime = LocalTime.of(shutdownTime.getA(), shutdownTime.getB());
            long diff = LocalTime.now().until(localShutdownTime, ChronoUnit.SECONDS);
            if (diff < 0)
            {
                // If its negative shift it into the next day
                diff += 86400;
            }
            if (diff < max && diff >= 0)
            {
                max = diff;
            }
        }
        return max;
    }

    public static void announceShutdown()
    {
        long secondsLeft = shutdownInterval - secondsPassed;
        if (announceMentIndex < announcements.size() && secondsLeft <= announcements.get(announceMentIndex).getA())
        {
            Shutdowner.LOGGER.info(announcements.get(announceMentIndex).getB());
            for (final Player player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
            {
                player.sendMessage(new TextComponent(announcements.get(announceMentIndex).getB()), player.getUUID());
            }
            announceMentIndex++;
        }
    }

    /**
     * Resets the current shutdown
     */
    public static void reset()
    {
        serverStartedTime = System.currentTimeMillis();
        announceMentIndex = 0;
        shutdownInterval = getTimeToNextShutdown();
    }

    public static void onServerStart()
    {
        reset();
    }
}
