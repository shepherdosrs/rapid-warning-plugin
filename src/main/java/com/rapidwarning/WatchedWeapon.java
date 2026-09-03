package com.rapidwarning;

import lombok.Getter;

/**
 * The weapons this plugin watches, and the style each one should be left on.
 * <p>
 * The desired style is declared per weapon rather than as a shared constant on
 * purpose: because COM_MODE is a button index, a hardcoded "1" is only Rapid for
 * weapons that present the three ranged styles. Adding a row forces you to state
 * its own index instead of inheriting a coincidence.
 */
@Getter
public enum WatchedWeapon
{
	TWISTED_BOW(20997, "Twisted bow", RangedStyle.RAPID),
	ZARYTE_CROSSBOW(26374, "Zaryte crossbow", RangedStyle.RAPID);

	private final int itemId;
	private final String displayName;
	private final RangedStyle desiredStyle;

	WatchedWeapon(int itemId, String displayName, RangedStyle desiredStyle)
	{
		this.itemId = itemId;
		this.displayName = displayName;
		this.desiredStyle = desiredStyle;
	}
}
