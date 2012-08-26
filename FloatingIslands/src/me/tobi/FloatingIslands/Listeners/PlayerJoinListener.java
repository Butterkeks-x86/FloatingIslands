package me.tobi.FloatingIslands.Listeners;

import java.io.File;

import me.tobi.FloatingIslands.Util;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
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
	private File pluginDataFolder;
	
	public PlayerJoinListener(int maxGenHeight, int minGenHeight,
			File pluginDataFolder){
		this.maxGenHeight=maxGenHeight;
		this.minGenHeight=minGenHeight;
		this.pluginDataFolder=pluginDataFolder;
	}
	
	/**
	 * Teleports Player to a valid spwan location; when logging in
	 * for the first time, fill inventory with lava bucket, ice and melon seeds.
	 * @param pjevt
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent pjevt){
		Player player=pjevt.getPlayer();
		World world=player.getWorld();
		
		if(!player.hasPlayedBefore()){
			/*first, try to get old spawn and teleport player to it if found*/
			Block oldSpawn;
			oldSpawn=Util.readSpawnFromFile(
					pluginDataFolder.getAbsolutePath()+"\\spawn", world);
			if(oldSpawn!=null){
				System.out.println("found old spawn x="+oldSpawn.getX()+" y="+
						oldSpawn.getY()+" z="+oldSpawn.getZ());
				player.teleport(oldSpawn.getLocation());
			}
			/*if not found, look for a new spawn and save it as default*/
			else{
				//search for a new spawn
				Block spawnBlock=getNearestSpawnBlock(
						world.getChunkAt(world.getSpawnLocation()));
				System.out.println("new spawn is at x="+spawnBlock.getX()+" y="+
						spawnBlock.getY()+" z="+spawnBlock.getZ());
				player.getWorld().setSpawnLocation(
						spawnBlock.getX(),
						spawnBlock.getY(),
						spawnBlock.getZ()
				);
				//save the new spawn to a file
				Util.saveSpawnToFile(pluginDataFolder.getAbsolutePath()+"\\spawn",
						spawnBlock);
				//place bedrock at spawn
				spawnBlock.getRelative(0, -3, 0).setType(Material.BEDROCK);
				//ensure a tree at spawn island
				Util.ensureTreeAtIsland(spawnBlock.getRelative(-1, -1, -1));
				//ensure air at player spawn (because of generated tree!)
				spawnBlock.getRelative(0, 1, 0).setType(Material.AIR);
				spawnBlock.getRelative(0, 2, 0).setType(Material.AIR);
				//finally, teleport the player to the new spawn location
				player.teleport(spawnBlock.getLocation());
			}

			/*always to do: give the player his/her starter kit*/
			player.getInventory().addItem(new ItemStack(Material.ICE, 1));
			player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
			player.getInventory().addItem(new ItemStack(Material.MELON_SEEDS, 1));
		}
	}
	
	/**
	 * Tries to find a near valid spawn location
	 * @param oldChunk The chunk the old spawn is in
	 * @return The block the player spawns inside
	 */
	private Block getNearestSpawnBlock(Chunk oldChunk){
		Chunk chunk=oldChunk.getWorld().getChunkAt(oldChunk.getX()+1,
				oldChunk.getZ()+1); //move to another chunk
		do{
			Block block=
					Util.getFirstSolidBlockInChunk(chunk, maxGenHeight, minGenHeight);
			block=block.getRelative(1, 0, 1); //we want the new spawn at the center
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
