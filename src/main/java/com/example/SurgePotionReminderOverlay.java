package com.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class SurgePotionReminderOverlay extends OverlayPanel
{
    private final SurgePotionReminderConfig config;
    private final Client client;

    @Inject
    private SurgePotionReminderOverlay(SurgePotionReminderConfig config, Client client)
    {
        this.config = config;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        final String surgePotionMessage = "Surge potion is ready";
        final int length = graphics.getFontMetrics().stringWidth(surgePotionMessage);

        panelComponent.getChildren().clear();

        panelComponent.getChildren().add((LineComponent.builder())
                .left(surgePotionMessage)
                .build());

        panelComponent.setPreferredSize(new Dimension(length + 10, 0));
        panelComponent.setBackgroundColor(config.overlayColor());

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);

        return panelComponent.render(graphics);
    }
}