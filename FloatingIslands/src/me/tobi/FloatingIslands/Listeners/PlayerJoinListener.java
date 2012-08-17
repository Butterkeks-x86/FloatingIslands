package me.tobi.FloatingIslands.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;


public class PlayerJoinListener implements Listener {
	
	/**
	 * Teleports Player to exact spwan location when logging in
	 * for the first time.
	 * @param pjevt
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent pjevt){
		Player player=pjevt.getPlayer();
		if(!player.hasPlayedBefore()){ //if first join -> teleport to exact spawn
			player.teleport(player.getWorld().getSpawnLocation());
			player.getInventory().addItem(new ItemStack(Material.ICE, 1));
			player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
		}
	}
}
