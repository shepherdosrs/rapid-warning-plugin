package com.rapidwarning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

class RapidWarningOverlay extends OverlayPanel
{
	private final RapidWarningPlugin plugin;
	private final RapidWarningConfig config;

	@Inject
	private RapidWarningOverlay(RapidWarningPlugin plugin, RapidWarningConfig config)
	{
		super(plugin);

		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);

		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// The plugin decides whether there is a warning and what it says; the
		// overlay only draws it. No game state is read from the render path.
		final String text = plugin.getWarningText();

		if (text == null)
		{
			return null;
		}

		panelComponent.getChildren().clear();
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(text)
			.color(Color.WHITE)
			.build());

		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(text) + 10, 0));
		panelComponent.setBackgroundColor(config.overlayColor());

		return super.render(graphics);
	}
}
