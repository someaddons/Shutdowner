package com.shutdowner.threading;

import com.shutdowner.Shutdowner;
import com.shutdowner.event.EventHandler;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class WatcherThread implements Runnable
{
    private static final int FIVEMINMILISECONDS = 300000;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private AtomicLong shutDownTime    = new AtomicLong(0);
    private AtomicLong maxShutDownTime = new AtomicLong(0);

    private AtomicBoolean shutDownDone = new AtomicBoolean(false);

    @Override
    public void run()
    {
        init();

        while (true)
        {
            try
            {
                /**
                 * Server shutdown hangs
                 */
                if (shuttingDown.get() && System.currentTimeMillis() - shutDownTime.get() > maxShutDownTime.get())
                {
                    Shutdowner.LOGGER.warn("Detected server shutdown hanging, killing");
                    try
                    {
                        Executors.newCachedThreadPool().submit(() -> ServerLifecycleHooks.getCurrentServer().saveAllChunks(true, false, false));
                    }
                    catch (Exception e)
                    {
                        Shutdowner.LOGGER.warn("Error during saving before killing:", e);
                    }

                    Thread.sleep(60000);
                    printThreads();
                    Thread.sleep(10000);
                    Runtime.getRuntime().halt(0);
                }

                /**
                 * Server shut down fine, jvm still alive
                 */
                if (shutDownDone.get())
                {
                    Thread.sleep(5000);
                    Shutdowner.LOGGER.warn("Server shut down correctly, ending gracefully");
                    Thread.sleep(1000);
                    Runtime.getRuntime().halt(0);
                }

                /**
                 * Detects whether the server is hanging
                 */
                if (Shutdowner.getConfig().getCommonConfig().shouldDetectHang.get() && System.currentTimeMillis() - lastTick.get() > FIVEMINMILISECONDS)
                {
                    Shutdowner.LOGGER.warn("Detected server hanging, shutting down");

                    try
                    {
                        Executors.newCachedThreadPool().submit(() -> ServerLifecycleHooks.getCurrentServer().saveAllChunks(true, false, false));
                    }
                    catch (Exception e)
                    {
                        Shutdowner.LOGGER.warn("Error during saving before killing:", e);
                    }

                    Thread.sleep(60000);
                    printThreads();
                    Thread.sleep(10000);
                    Runtime.getRuntime().halt(0);
                }

                Thread.sleep(30000);
            }
            catch (InterruptedException e)
            {
                Shutdowner.LOGGER.warn("Shutdowner watcher thread interrupted, shutting down");
                EventHandler.executor.shutdownNow();
                break;
            }
        }
    }

    private void init()
    {
        lastTick.set(System.currentTimeMillis());
    }

    /**
     * Prints the remaining threads
     */
    private void printThreads()
    {
        if (Shutdowner.getConfig().getCommonConfig().printThreads.get())
        {
            for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet())
            {
                if (entry.getKey() != Thread.currentThread() && !entry.getKey().isDaemon())
                {
                    Shutdowner.LOGGER.warn("------------ Thread: " + entry.getKey().getName() + " is still running! stacktrace below");

                    for (StackTraceElement element : entry.getValue())
                    {
                        Shutdowner.LOGGER.warn(element.toString());
                    }
                }
            }
        }
    }

    /**
     * Notifies the thread of a shutdown event.
     */
    public void notifyShutDownEvent()
    {
        shuttingDown.set(true);
        shutDownTime.set(System.currentTimeMillis());
        maxShutDownTime.set(Shutdowner.getConfig().getCommonConfig().maxShutDownTime.get() * 1000);
    }

    /**
     * Notifies the thread when the server is done with its usual shutdown tasks.
     */
    public void notifyShutDownDone()
    {
        shutDownDone.set(true);
    }

    /**
     * Sets the current time on server tick, used to detect if the server is still alive.
     */
    private AtomicLong lastTick  = new AtomicLong(0);
    private int        tickTimer = 0;

    public void onServerTick()
    {
        if (++tickTimer == 100)
        {
            tickTimer = 0;
            lastTick.set(System.currentTimeMillis());
        }
    }
}
