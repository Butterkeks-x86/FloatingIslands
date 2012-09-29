package com.git.butterkeks_x86.floatingIslands.listeners;

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
