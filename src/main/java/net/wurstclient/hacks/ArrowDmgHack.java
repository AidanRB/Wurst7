/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"arrow dmg", "ArrowDamage", "arrow damage"})
public final class ArrowDmgHack extends Hack
{
	private final SliderSetting packets = new SliderSetting("Packets",
		"Amount of packets to send.\n" + "More packets = higher damage.", 200,
		2, 2000, 2, ValueDisplay.INTEGER);
	
	private final CheckboxSetting yeetTridents = new CheckboxSetting(
		"Trident yeet mode",
		"When enabled, tridents fly much further. Doesn't seem to affect damage or Riptide.\n\n"
			+ "\u00a7c\u00a7lWARNING:\u00a7r You can easily lose your trident by enabling this option!",
		false);
	
	public ArrowDmgHack()
	{
		super("ArrowDMG");
		setCategory(Category.COMBAT);
		addSetting(packets);
		addSetting(yeetTridents);
	}
	
	// TODO: Make this a proper event
	public void onStopUsingItem()
	{
		if(!isEnabled())
			return;
		
		ClientPlayerEntity player = MC.player;
		ClientPlayNetworkHandler netHandler = player.networkHandler;
		
		if(!isValidItem(player.getMainHandStack().getItem()))
			return;
		
		netHandler.sendPacket(
			new ClientCommandC2SPacket(player, Mode.START_SPRINTING));
		
		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();
		
		for(int i = 0; i < packets.getValueI() / 2; i++)
		{
			netHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x,
				y - 1e-10, z, true));
			netHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x,
				y + 1e-10, z, false));
		}
	}
	
	private boolean isValidItem(Item item)
	{
		if(yeetTridents.isChecked() && item == Items.TRIDENT)
			return true;
		
		return item == Items.BOW;
	}
}