package com.example;

import com.google.inject.Provides;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


@Slf4j
@PluginDescriptor(
		name = "Surge Potion Reminder"
)
public class SurgePotionReminderPlugin extends Plugin
{
	private DeathChargeTimer timer = null;

	private int overlayVisible;

	@Inject
	private SurgePotionReminderConfig config;

	@Inject
	private SurgePotionReminderOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;

	public static final Pattern DEATH_CHARGE_ACTIVE =
			Pattern.compile("<col=[A-Fa-f\\d]+>Upon the death of your next foe, some of your special attack energy will be restored\\.</col>");

	public static final Pattern UPGRADED_DEATH_CHARGE_ACTIVE =
			Pattern.compile("<col=[A-Fa-f\\d]+>Upon the death of your next two foes, some of your special attack energy will be restored\\.</col>");

	@Override
	protected void startUp()
	{
		overlayVisible = -1;
		timer = new DeathChargeTimer();
	}

	@Override
	protected void shutDown()
	{
		removeOverlay();
		timer = null;
	}

	@Provides
	SurgePotionReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DeathChargeReminderConfig.class);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		final String message = event.getMessage();

		if (message.matches(DEATH_CHARGE_ACTIVE.pattern()) || message.matches(UPGRADED_DEATH_CHARGE_ACTIVE.pattern()))
		{
			timer.start();
		}
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (overlayVisible != -1)
		{
			checkOverlay();
		}

		if (this.timer == null)
		{
			return;
		}

		switch (this.timer.getState()) {
			case ACTIVE:
				removeOverlay();
				break;
			case EXPIRED:
				addOverlay();
				timer.stop();
				break;
			case IDLE:
			default:
				break;
		}
	}

	private void addOverlay()
	{
		overlayManager.add(overlay);
		overlayVisible = client.getTickCount();
	}

	private void removeOverlay()
	{
		overlayManager.remove(overlay);
		overlayVisible = -1;
	}

	private void checkOverlay()
	{
		if (client.getTickCount() - overlayVisible >= config.overlayDuration())
		{
			removeOverlay();
		}
	}
}