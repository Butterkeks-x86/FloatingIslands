package me.tobi.FloatingIslands.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
	
	/**
	 * Sets the exact respawn location if the palyer died, instead of
	 * spawning only near the spawnpoint
	 * @param prevt
	 */
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent prevt){
		Player player=prevt.getPlayer();
		prevt.setRespawnLocation(player.getWorld().getSpawnLocation());
	}
}
