package com.bawaveinfo;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.Color;

public class BaWaveInfoOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private BaWaveInfoPlugin plugin;

    @Inject
    public BaWaveInfoOverlay() {
        setPosition(OverlayPosition.TOP_LEFT);
        panelComponent.setBackgroundColor(new Color(0x88212121, true));
        panelComponent.setPreferredSize(new Dimension(100, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (!plugin.isInBaSession())
        {
            return null;
        }

        panelComponent.getChildren().clear();

        BaRole role = plugin.getCurrentRole();
        WaveInfo info = plugin.getWaveInfo();
        int wave = plugin.getCurrentWave();

        // Not in BA
        if (wave <= 0 || info == null)
        {
            panelComponent.getChildren().add(
                    TitleComponent.builder()
                            .text("Barbarian Assault")
                            .build()
            );

            return panelComponent.render(graphics);
        }

        // In BA
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Wave: " + wave)
                .build());

        if (role == null)
        {
            return panelComponent.render(graphics);
        }

        switch (role) {
            case ATTACKER:
                panelComponent.getChildren().add(
                        LineComponent.builder()
                                .left("Fighters")
                                .right(info.fighters() + " / " + info.fighterReserves())
                                .build()
                );

                panelComponent.getChildren().add(
                        LineComponent.builder()
                                .left("Rangers")
                                .right(info.rangers() + " / " + info.rangerReserves())
                                .build()
                );

                if (plugin.isTrackingSpawns()) {
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("Fighters Left")
                                    .right(String.valueOf(plugin.getFightersRemaining()))
                                    .build()
                    );

                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("Rangers Left")
                                    .right(String.valueOf(plugin.getRangersRemaining()))
                                    .build()
                    );
                }
                break;

            case DEFENDER:
                panelComponent.getChildren().add(
                        LineComponent.builder()
                                .left("Runners")
                                .right(info.runners() + " / " + info.runnerReserves())
                                .build()
                );

                if (plugin.isTrackingSpawns()) {
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("Runners Left")
                                    .right(String.valueOf(plugin.getRunnersRemaining()))
                                    .build()
                    );
                }
                break;

            case HEALER:
                panelComponent.getChildren().add(
                        LineComponent.builder()
                                .left("Healers")
                                .right(info.healers() + " / " + info.healerReserves())
                                .build()
                );

                if (plugin.isTrackingSpawns()) {
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("Healers Left")
                                    .right(String.valueOf(plugin.getHealersRemaining()))
                                    .build()
                    );
                }
                break;

            case COLLECTOR:
                panelComponent.getChildren().add(
                        LineComponent.builder()
                                .left("Collector")
                                .build()
                );
                break;
        }
        return panelComponent.render(graphics);
    }
}

