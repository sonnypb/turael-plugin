package com.bawaveinfo;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.ColorScheme;
import java.awt.Color;
import net.runelite.client.util.ColorUtil;

public class BaWaveInfoOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private BaWaveInfoPlugin plugin;

    int wave = 10;
    WaveInfo info = WaveData.get(wave);

    @Inject
    public BaWaveInfoOverlay() {
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

@Override
public Dimension render(Graphics2D graphics)
{
    panelComponent.getChildren().clear();

    panelComponent.getChildren().add(TitleComponent.builder()
            .text("Wave " + wave)
            .build());

    panelComponent.setBackgroundColor(
            ColorUtil.colorWithAlpha(new Color(20, 20, 20, 156), 180)
    );
    panelComponent.setPreferredSize(new Dimension(100, 0));

    WaveInfo info = plugin.getWaveInfo();

    if (info == null)
    {
        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("Not in BA")
                        .build()
        );

        return panelComponent.render(graphics);
    }

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("Runners")
                    .right(info.runners() + " / " + info.runnerReserves())
                    .build()
    );

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("Healers")
                    .right(info.healers() + " / " + info.healerReserves())
                    .build()
    );

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("Fighters")
                    .right(info.fighters() + " / " + info.fighterReserves())
                    .build()
    );

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("Rangers")
                    .right(info.rangers() + " / " + info.fighterReserves())
                    .build()
    );

    return panelComponent.render(graphics);
    }
}
