package com.chenfan.compat.mixin;

import com.chenfan.compat.client.SimulatedBannerBridge;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
        targets = "dev.simulated_team.simulated.registrate.simulated_tab.SimulatedCreativeTab",
        remap = false,
        priority = 900
)
public abstract class SimulatedCreativeTabMixin {
    @Inject(method = "renderBanners", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void aeronauticsModernUICompat$suppressOriginalLateBannerRender(
            CreativeModeInventoryScreen screen,
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        if (SimulatedBannerBridge.shouldSuppressLateRender()) {
            ci.cancel();
        }
    }
}
