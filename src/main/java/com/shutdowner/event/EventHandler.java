package com.shutdowner.event;

import com.shutdowner.config.CommonConfiguration;
import com.shutdowner.handlers.ShutDownHandler;
import com.shutdowner.threading.WatcherThread;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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
    public static void onServerStopping(final ServerStoppingEvent event)
    {
        watcherThread.notifyShutDownEvent();
    }

    @SubscribeEvent
    public static void onServerStopping(final ServerStoppedEvent event)
    {
        watcherThread.notifyShutDownDone();
    }

    @SubscribeEvent
    public static void onServerStarted(final ServerStartedEvent event)
    {
        ShutDownHandler.onServerStart();
    }

    @SubscribeEvent
    public static void onServerTick(final ServerTickEvent.Post event)
    {
        watcherThread.onServerTick();
        if (FMLEnvironment.dist.isDedicatedServer())
        {
            ShutDownHandler.onServerTick();
        }
    }

    public static ExecutorService executor = Executors.newFixedThreadPool(1);

    @SubscribeEvent
    public static void onWorldLoad(final LevelEvent.Load event)
    {
        if (watcherThread == null && CommonConfiguration.config.getCommonConfig().shouldDetectShutDownHang && !event.getLevel().isClientSide())
        {
            watcherThread = new WatcherThread();
            executor.submit(watcherThread);
        }
    }
}
