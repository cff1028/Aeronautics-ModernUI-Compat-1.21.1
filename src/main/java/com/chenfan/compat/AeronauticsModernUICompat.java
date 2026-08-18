package com.chenfan.compat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AeronauticsModernUICompat.MODID)
public final class AeronauticsModernUICompat {
    public static final String MODID = "aeronautics_modernui_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AeronauticsModernUICompat(IEventBus modEventBus) {
        LOGGER.info("Aeronautics: ModernUI Compat loaded");
    }
}
