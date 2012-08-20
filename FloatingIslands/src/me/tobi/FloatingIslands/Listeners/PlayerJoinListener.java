package me.tobi.FloatingIslands.Listeners;

import me.tobi.FloatingIslands.Util;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;


public class PlayerJoinListener implements Listener {
	
	private int maxGenHeight=127;
	private int minGenHeight=0;
	
	public PlayerJoinListener(int maxGenHeight, int minGenHeight){
		this.maxGenHeight=maxGenHeight;
		this.minGenHeight=minGenHeight;
	}
	
	/**
	 * Teleports Player to a valid spwan location; when logging in
	 * for the first time, fill inventory with lava bucket, ice and melon seeds.
	 * @param pjevt
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent pjevt){
		Player player=pjevt.getPlayer();
		if(!player.hasPlayedBefore()){
			/*TODO: start items -> chest*/
			player.getInventory().addItem(new ItemStack(Material.ICE, 1));
			player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
			player.getInventory().addItem(new ItemStack(Material.MELON_SEEDS, 1));
			
			/*ensure that the player spawns at a valid spawn location*/
			Block spawnBlock=player.getWorld().getSpawnLocation().getBlock();
			if(!Util.isValidSpawn(spawnBlock)){
				System.out.println("invalid spawn at x="+spawnBlock.getX()+" y="+
						spawnBlock.getY()+" z="+spawnBlock.getZ());
				spawnBlock=getNearestSpawnBlock(spawnBlock);
				System.out.println("new spawn is at x="+spawnBlock.getX()+" y="+
						spawnBlock.getY()+" z="+spawnBlock.getZ());
				player.getWorld().setSpawnLocation(
						spawnBlock.getX(),
						spawnBlock.getY(),
						spawnBlock.getZ()
				);
			}
			//spawnBlock is the block the player spawns inside!
			Util.ensureTreeAtIsland(spawnBlock.getRelative(BlockFace.DOWN));
			player.teleport(spawnBlock.getLocation());
		}
	}
	
	/**
	 * Tries to find a near valid spawn location
	 * @param world The world the spawn is in
	 * @param oldSpawn The old and invalid spawn
	 * @return The block the player spawns inside
	 */
	private Block getNearestSpawnBlock(Block oldSpawnBlock){
		Chunk chunk=oldSpawnBlock.getChunk();
		do{
			Block block=
					Util.getFirstSolidBlockInChunk(chunk, maxGenHeight, minGenHeight);
			/*if a grass block was found*/
			if(block.getType()==Material.GRASS){
				if(Util.isValidSpawn(block.getRelative(BlockFace.UP))){
					return block.getRelative(BlockFace.UP);
				}
				else{
					chunk=chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ()-1);
					continue;
				}
			}
			/*if no grass block was found*/
			else{
				chunk=chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ());
				continue;
			}
		}while(true);
	}
}
