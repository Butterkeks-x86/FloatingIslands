package me.tobi.FloatingIslands.Listeners;

import java.io.File;

import me.tobi.FloatingIslands.Util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
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
	private static int spawnX=6, spawnY=64, spawnZ=7;
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
				//look for an appropriate empty chunk
				Chunk spawnChunk=getNearEmptyChunkInPopulatedBiome(
						world.getSpawnLocation().getChunk());
				//spawn block within this chunk is fixed
				Block spawnBlock=spawnChunk.getBlock(spawnX, spawnY, spawnZ);
				//create the spawning island
				createSpawnIsland(spawnBlock.getRelative(-1, -1, -1));
				//set the new spawn ...
				player.getWorld().setSpawnLocation(
						spawnBlock.getX(),
						spawnBlock.getY(),
						spawnBlock.getZ());
				//...and save it to file
				Util.saveSpawnToFile(pluginDataFolder.getAbsolutePath()+"\\spawn",
						spawnBlock);
				//get the spawn location and teleport the player to it
				Location spawn=new Location(world, spawnBlock.getX()+0.5,
						spawnBlock.getY()+0.5, spawnBlock.getZ()+0.5);
				player.teleport(spawn);
			}

			/*always to do: give the player his/her starter kit*/
			player.getInventory().addItem(new ItemStack(Material.ICE, 1));
			player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
			player.getInventory().addItem(new ItemStack(Material.MELON_SEEDS, 1));
		}
	}
	
	/**
	 * Searches for an empty chunk in a populated biome, i.e. not in
	 * a ocean or river biome
	 * @param chunk The chunk to start the search with (will be considered itself)
	 * @return A empty chunk within a populated biome
	 */
	private Chunk getNearEmptyChunkInPopulatedBiome(Chunk chunk){
		Biome bio;
		do{
			while(Util.getFirstSolidBlockInChunk(chunk,
					maxGenHeight, minGenHeight).getType()!=Material.AIR){
				chunk=chunk.getWorld().getChunkAt(chunk.getX()+1,
						chunk.getZ()-1);
			}
			bio=chunk.getWorld().getBiome(chunk.getX()*16, chunk.getZ()*16);
		}while(bio==Biome.OCEAN || bio==Biome.FROZEN_OCEAN
				|| bio==Biome.RIVER || bio==Biome.FROZEN_RIVER);
		return chunk;
	}
	
	/**
	 * Creates the spawning island
	 * @param startBlock The first block of the spawning island
	 */
	private void createSpawnIsland(Block startBlock){
		//two layers of dirt
		for(int y=-2; y<0; y++){
			for(int x=0; x<3; x++){
				for(int z=0; z<3; z++){
					startBlock.getRelative(x, y, z).setType(Material.DIRT);
				}
			}
		}
		//the spawn bedrock block
		startBlock.getRelative(1, -2, 1).setType(Material.BEDROCK);
		//top layer of grass
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				startBlock.getRelative(x, 0, z).setType(Material.GRASS);
			}
		}
		//lower two layers of leaves
		for(int y=4; y<6; y++){
			for(int x=0; x<5; x++){
				for(int z=0; z<5; z++){
					startBlock.getRelative(x, y, z).setType(Material.LEAVES);
				}
			}
		}
		//upper two layers of leaves
		for(int y=6; y<8; y++){
			for(int x=1; x<4; x++){
				for(int z=1; z<4; z++){
					if((x==1 || x==3) && (z==1 || z==3))continue;
					else startBlock.getRelative(x, y, z).setType(Material.LEAVES);
				}
			}
		}
		//the stem
		for(int y=1; y<7; y++){
			startBlock.getRelative(2, y, 2).setType(Material.LOG);
		}
	}
}
