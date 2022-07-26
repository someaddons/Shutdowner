package com.shutdowner.event;

import com.shutdowner.Shutdowner;
import com.shutdowner.handlers.ShutDownHandler;
import com.shutdowner.threading.WatcherThread;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handler to catch server tick events
 */
public class EventHandler
{
    /**
     * Update frequency
     */
    private static WatcherThread watcherThread;

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void onServerStopping(final ServerStoppingEvent event)
    {
        watcherThread.notifyShutDownEvent();
    }

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void onServerStopping(final ServerStoppedEvent event)
    {
        watcherThread.notifyShutDownDone();
    }

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void onServerStarted(final ServerStartedEvent event)
    {
        ShutDownHandler.onServerStart();
    }

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void onServerTick(final TickEvent.ServerTickEvent event)
    {
        watcherThread.onServerTick();
        ShutDownHandler.onServerTick();
    }

    public static ExecutorService executor = Executors.newFixedThreadPool(1);

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void onWorldLoad(final LevelEvent.Load event)
    {
        if (watcherThread == null && Shutdowner.getConfig().getCommonConfig().shouldDetectShutDownHang.get())
        {
            watcherThread = new WatcherThread();
            executor.submit(watcherThread);
        }
    }
}
