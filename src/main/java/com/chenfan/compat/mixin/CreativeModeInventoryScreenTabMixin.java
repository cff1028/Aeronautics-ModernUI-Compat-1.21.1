package com.chenfan.compat.mixin;

import com.chenfan.compat.client.SimulatedBannerBridge;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CreativeModeInventoryScreen.class, priority = 900)
public abstract class CreativeModeInventoryScreenTabMixin {
    @Inject(method = "init", at = @At("HEAD"), require = 0)
    private void aeronauticsModernUICompat$resetTrackedTab(CallbackInfo ci) {
        SimulatedBannerBridge.resetCurrentTab();
    }

    @Inject(method = "selectTab", at = @At("HEAD"), require = 0)
    private void aeronauticsModernUICompat$trackSelectedTab(CreativeModeTab tab, CallbackInfo ci) {
        SimulatedBannerBridge.onTabSelected(tab);
    }
}
