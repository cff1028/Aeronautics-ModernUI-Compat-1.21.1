package com.chenfan.compat.client;

import com.chenfan.compat.AeronauticsModernUICompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class SimulatedBannerBridge {
    private static final String TAB_SERVICE_CLASS = "dev.simulated_team.simulated.service.SimTabService";
    private static final String CREATIVE_TAB_CLASS = "dev.simulated_team.simulated.registrate.simulated_tab.SimulatedCreativeTab";

    private static final ThreadLocal<Boolean> RELOCATED_CALL = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> RELOCATED_THIS_RENDER = ThreadLocal.withInitial(() -> false);

    private static final Object TAB_SERVICE_INSTANCE;
    private static final Method GET_CREATIVE_TAB;
    private static final Method RENDER_BANNERS;
    private static final boolean AVAILABLE;

    private static volatile CreativeModeTab cachedSimulatedTab;
    private static volatile boolean currentSimulatedTabSelected;
    private static volatile boolean runtimeHealthy = true;
    private static boolean warnedInvocationFailure;

    static {
        Object service = null;
        Method getCreativeTab = null;
        Method renderBanners = null;
        boolean available = false;

        try {
            Class<?> tabServiceClass = Class.forName(TAB_SERVICE_CLASS);
            Field instanceField = tabServiceClass.getField("INSTANCE");
            service = instanceField.get(null);
            getCreativeTab = tabServiceClass.getMethod("getCreativeTab");

            Class<?> simulatedCreativeTabClass = Class.forName(CREATIVE_TAB_CLASS);
            renderBanners = simulatedCreativeTabClass.getMethod(
                    "renderBanners",
                    CreativeModeInventoryScreen.class,
                    GuiGraphics.class,
                    int.class,
                    int.class
            );

            available = true;
            AeronauticsModernUICompat.LOGGER.info("Resolved Create Simulated creative banner renderer");
        } catch (ReflectiveOperationException | RuntimeException | LinkageError error) {
            AeronauticsModernUICompat.LOGGER.error(
                    "Could not resolve Create Simulated creative banner renderer. " +
                            "The compatibility patch will stay inactive and Simulated's original renderer will be preserved.",
                    error
            );
        }

        TAB_SERVICE_INSTANCE = service;
        GET_CREATIVE_TAB = getCreativeTab;
        RENDER_BANNERS = renderBanners;
        AVAILABLE = available;
    }

    private SimulatedBannerBridge() {
    }

    public static boolean isAvailable() {
        return AVAILABLE && runtimeHealthy;
    }

    public static void resetCurrentTab() {
        currentSimulatedTabSelected = false;
        RELOCATED_THIS_RENDER.set(false);
    }

    public static void onTabSelected(CreativeModeTab selectedTab) {
        currentSimulatedTabSelected = isSimulatedTab(selectedTab);
        RELOCATED_THIS_RENDER.set(false);
    }

    public static boolean isCurrentSimulatedTab() {
        return isAvailable() && currentSimulatedTabSelected;
    }

    public static boolean isSimulatedTab(CreativeModeTab selectedTab) {
        if (!isAvailable() || selectedTab == null) {
            return false;
        }

        CreativeModeTab simulatedTab = cachedSimulatedTab;
        if (simulatedTab == null) {
            try {
                simulatedTab = (CreativeModeTab) GET_CREATIVE_TAB.invoke(TAB_SERVICE_INSTANCE);
                cachedSimulatedTab = simulatedTab;
            } catch (ReflectiveOperationException | ClassCastException | LinkageError error) {
                disableAfterInvocationFailure("resolve the Simulated creative tab", error);
                return false;
            }
        }

        return selectedTab == simulatedTab;
    }

    public static void renderBeforeTooltip(
            CreativeModeInventoryScreen screen,
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        if (!isAvailable()) {
            return;
        }

        RELOCATED_THIS_RENDER.set(false);
        RELOCATED_CALL.set(true);
        try {
            RENDER_BANNERS.invoke(null, screen, graphics, mouseX, mouseY);
            graphics.flush();
            RELOCATED_THIS_RENDER.set(true);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError error) {
            disableAfterInvocationFailure("invoke SimulatedCreativeTab.renderBanners", error);
        } finally {
            RELOCATED_CALL.remove();
        }
    }

    public static boolean shouldSuppressLateRender() {
        if (!isAvailable() || RELOCATED_CALL.get() || !RELOCATED_THIS_RENDER.get()) {
            return false;
        }

        RELOCATED_THIS_RENDER.set(false);
        return true;
    }

    private static synchronized void disableAfterInvocationFailure(String action, Throwable error) {
        runtimeHealthy = false;
        if (warnedInvocationFailure) {
            return;
        }
        warnedInvocationFailure = true;
        AeronauticsModernUICompat.LOGGER.error(
                "Failed to {}. Disabling the relocated render path and falling back to Simulated's original renderer for this session.",
                action,
                error
        );
    }
}
