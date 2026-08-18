package com.chenfan.compat.mixin;

import com.chenfan.compat.client.SimulatedBannerBridge;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerScreen.class, priority = 900)
public abstract class AbstractContainerScreenMixin {
    @Inject(method = "renderTooltip", at = @At("HEAD"), require = 0)
    private void aeronauticsModernUICompat$renderSimulatedBannersBeforeTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        Object self = this;
        if (!(self instanceof CreativeModeInventoryScreen screen)) {
            return;
        }

        if (SimulatedBannerBridge.isCurrentSimulatedTab()) {
            SimulatedBannerBridge.renderBeforeTooltip(screen, graphics, mouseX, mouseY);
        }
    }
}
