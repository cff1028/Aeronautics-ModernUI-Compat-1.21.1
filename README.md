# Aeronautics: ModernUI Compat (Minecraft 1.21.1 / NeoForge)

External compatibility patch for Create Simulated/Aeronautics creative-tab section banners and Modern UI tooltips.

## What it fixes

Create Simulated 1.3.1 draws `SimulatedCreativeTab.renderBanners(...)` from a
`CreativeModeInventoryScreen.render@TAIL` mixin. That places the custom section banner after the
vanilla tooltip pass. Modern UI's translucent tooltip background/shadow therefore blends against the
vanilla empty creative slots first, and Simulated paints the banner over that result afterwards.

This compat changes the effective order to:

1. Vanilla creative inventory, slots and creative-tab UI
2. Simulated section banners
3. `GuiGraphics.flush()`
4. `AbstractContainerScreen.renderTooltip(...)`
5. Modern UI tooltip/shadow

No Simulated source or JAR is modified.

## Target environment

- Minecraft 1.21.1
- NeoForge 21.1.233 (other compatible 21.1.x builds may also work)
- Create Simulated 1.3.1, standalone or bundled with Create Aeronautics
- Modern UI 3.13.0.1 / compatible 1.21.1 builds
- Java 21

## Build

```bash
./gradlew build
```

The output JAR is written to `build/libs/`.

## Useful log messages

When the bridge resolves successfully, the client log contains:

```text
Resolved Create Simulated creative banner renderer
```

If Simulated changes its runtime classes/API, the compat logs an error and keeps Simulated's
original late renderer instead of intentionally removing the banners.
