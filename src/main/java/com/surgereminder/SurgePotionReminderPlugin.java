package com.surgereminder;

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
	private SurgePotionTimer timer = null;

	private int overlayVisible;

	@Inject
	private SurgePotionReminderConfig config;

	@Inject
	private SurgePotionReminderOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;

	public static final Pattern SURGE_POTION_AVAILABLE =
			Pattern.compile("<col=[A-Fa-f\\d]+>You now feel capable of drinking another dose of surge potion</col>\\.");

	@Override
	protected void startUp()
	{
		overlayVisible = -1;
		timer = new SurgePotionTimer();
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
		return configManager.getConfig(SurgePotionReminderConfig.class);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		final String message = event.getMessage();

		if (message.matches(SURGE_POTION_AVAILABLE.pattern()))
		{
			addOverlay();
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