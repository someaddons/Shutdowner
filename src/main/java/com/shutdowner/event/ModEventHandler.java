package com.shutdowner.event;

import com.shutdowner.Shutdowner;
import com.shutdowner.handlers.ShutDownHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ModEventHandler
{
    @SubscribeEvent
    public static void onConfigChanged(ModConfigEvent event)
    {
        Shutdowner.getConfig().parseConfig();
        ShutDownHandler.reset();
    }
}
