package com.shutdowner;

import com.shutdowner.event.EventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.shutdowner.Constants.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class Shutdowner
{
    public static final Logger LOGGER = LogManager.getLogger();

    public Shutdowner(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(EventHandler.class);
    }
}
