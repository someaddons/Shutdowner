package com.shutdowner.mixin;

import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.shutdowner.event.EventHandler.watcherThread;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin
{
    @Inject(method = "tickPaused", at = @At("HEAD"))
    private void onPause(final CallbackInfo ci)
    {
        watcherThread.onServerTick();
    }
}
