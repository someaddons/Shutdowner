package com.shutdowner.event;

import com.shutdowner.Shutdowner;
import com.shutdowner.handlers.ShutDownHandler;
import com.shutdowner.threading.WatcherThread;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

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
    public static void onServerStopping(final FMLServerStoppingEvent event)
    {
        watcherThread.notifyShutDownEvent();
    }

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void onServerStopping(final FMLServerStoppedEvent event)
    {
        watcherThread.notifyShutDownDone();
    }

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void onServerStarted(final FMLServerStartedEvent event)
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
    public static void onWorldLoad(final WorldEvent.Load event)
    {
        if (watcherThread == null && Shutdowner.getConfig().getCommonConfig().shouldDetectShutDownHang.get())
        {
            watcherThread = new WatcherThread();
            executor.submit(watcherThread);
        }
    }
}
