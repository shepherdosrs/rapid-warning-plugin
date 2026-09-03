package com.rapidwarning;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Rapid Warning",
	description = "Warn if your Twisted bow or Zaryte crossbow is not on Rapid",
	tags = {"rapid", "attack", "style", "twisted", "bow", "zaryte", "crossbow", "ranged"}
)
public class RapidWarningPlugin extends Plugin
{
	public static final String CONFIG_GROUP = "rapidwarning";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private RapidWarningOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	/** Every item id that resolves to a watched weapon, variations included. */
	private final Map<Integer, WatchedWeapon> weaponsByItemId = new HashMap<>();

	/**
	 * The warning message, non-null exactly when a watched weapon is equipped on
	 * the wrong style. The overlay reads this and nothing else, so no game state is
	 * touched from the render path.
	 */
	@Getter
	private String warningText;

	@Override
	protected void startUp()
	{
		buildWeaponLookup();
		overlayManager.add(overlay);
		clientThread.invoke(this::update);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		warningText = null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			update();
		}
		else if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			warningText = null;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// COM_MODE is the selected attack-style button. The weapon category varbit
		// changes when you swap into a weapon whose combat tab has a different
		// layout, which can move the selection without COM_MODE itself changing.
		if (event.getVarpId() == VarPlayerID.COM_MODE
			|| event.getVarbitId() == VarbitID.COMBAT_WEAPON_CATEGORY)
		{
			update();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		update();
	}

	/**
	 * Recompute the warning. Every input is event-driven, so there is no per-tick
	 * work and no GameTick subscription.
	 */
	private void update()
	{
		final WatchedWeapon weapon = weaponsByItemId.get(getEquippedWeaponId());

		if (weapon == null)
		{
			warningText = null;
			return;
		}

		final RangedStyle style = RangedStyle.fromIndex(client.getVarpValue(VarPlayerID.COM_MODE));

		if (style == weapon.getDesiredStyle())
		{
			warningText = null;
			return;
		}

		warningText = weapon.getDisplayName()
			+ " is on " + (style == null ? "an unknown style" : style.getDisplayName());
	}

	private Integer getEquippedWeaponId()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return null;
		}

		final Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		final PlayerComposition composition = player.getPlayerComposition();
		if (composition == null)
		{
			return null;
		}

		return composition.getEquipmentId(KitType.WEAPON);
	}

	/**
	 * Map every variation of each watched weapon onto its entry, so ornament kits
	 * and charged/beta variants resolve to the same weapon.
	 * <p>
	 * Both calls are total: map() normalises to the base id and falls back to
	 * identity, so a variant id in the enum still expands to its whole group, and
	 * getVariations() falls back to a singleton, so a weapon with no variation
	 * group is still registered under its own id.
	 */
	private void buildWeaponLookup()
	{
		weaponsByItemId.clear();

		for (WatchedWeapon weapon : WatchedWeapon.values())
		{
			final int baseId = ItemVariationMapping.map(weapon.getItemId());

			for (Integer itemId : ItemVariationMapping.getVariations(baseId))
			{
				weaponsByItemId.put(itemId, weapon);
			}
		}

		log.debug("Watching {} item ids: {}", weaponsByItemId.size(), weaponsByItemId.keySet());
	}

	@Provides
	RapidWarningConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RapidWarningConfig.class);
	}
}
