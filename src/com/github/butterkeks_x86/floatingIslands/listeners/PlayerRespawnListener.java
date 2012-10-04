/*******************************************************************************
 * Copyright (c) 2012 Butterkeks-x86.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.github.butterkeks_x86.floatingIslands.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
	
	private World floatingIslandsWorld;
	
	public PlayerRespawnListener(World floatingIslandsWorld){
		this.floatingIslandsWorld=floatingIslandsWorld;
	}
	
	/**
	 * Sets the exact respawn location if the palyer died, instead of
	 * spawning only near the spawnpoint
	 * @param prevt
	 */
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent prevt){
		Player player=prevt.getPlayer();
		if(player.getWorld()==floatingIslandsWorld){
			prevt.setRespawnLocation(floatingIslandsWorld.getSpawnLocation());
		}
	}
}
