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
            .text("Wave" + wave)
            .build());

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
                    .right(String.valueOf(info.runners()))
                    .build()
    );

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("Healers")
                    .right(String.valueOf(info.healers()))
                    .build()
    );

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("Fighters")
                    .right(String.valueOf(info.fighters()))
                    .build()
    );

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("Rangers")
                    .right(String.valueOf(info.rangers()))
                    .build()
    );

    panelComponent.getChildren().add(
            LineComponent.builder()
                    .left("HR / FR / RR / RnR")
                    .right(
                            info.healerReserves() + " / " +
                                    info.fighterReserves() + " / " +
                                    info.rangerReserves() + " / " +
                                    info.runnerReserves()
                    )
                    .build()
    );

    return panelComponent.render(graphics);
}
}
