package me.tobi.FloatingIslands;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.java.JavaPlugin;

public class FloatingIslandsChunkPopulator extends BlockPopulator {
	
	private World world;
	private Chunk chunk;
	private Random ran;
	private int maxGenHeight=127;
	private int minGenHeight=0;
	
	public FloatingIslandsChunkPopulator(JavaPlugin parent){
		maxGenHeight=parent.getConfig().getInt("max-gen-height");
		minGenHeight=parent.getConfig().getInt("min-gen-height");
	}
	
	@Override
	public void populate(World world, Random ran, Chunk chunk){
		this.world=world;
		this.chunk=chunk;
		this.ran=ran;
		placeObjects();
	}
	
	private void placeObjects(){
		Block startBlock=getFirstSolidBlockInChunk();
		
		if(startBlock.getType()==Material.GRASS){
			/*no biome switch statement for debugging purposes; TODO: alter*/
			placeSwampObjects(startBlock);
		}
		else if(startBlock.getType()==Material.SAND){
			placeDesertObjects(startBlock);
		}
		
//		Biome biome=world.getBiome(chunk.getX()*16, chunk.getZ()+16);
//		switch(biome){
//		case DESERT: placeDesertObjects(); break;
//		case DESERT_HILLS: placeDesertObjects(); break;
//		case FOREST: placeForestObjects(); break;
//		case FOREST_HILLS: placeForestObjects(); break;
//		case TAIGA: placeTaigaObjects(); break;
//		case TAIGA_HILLS: placeTaigaObjects(); break;
//		case PLAINS: placePlainsObjects(); break;
//		case JUNGLE: placeJungleObjects(); break;
//		case JUNGLE_HILLS: placeJungleObjects(); break;
//		case SWAMPLAND: placeSwampObjects(); break;
//		case ICE_PLAINS: placeIcePlainsObjects(); break;
//		default: break;
//		}
		
	}
	
	/**
	 * Places swamp objects on an island
	 * @param startBlock The first block of the island to operate on
	 */
	private void placeSwampObjects(Block startBlock) {
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(1000);
				/*try to spawn mushrooms with higher probability,
				 *  since low light levels are rare*/
				if(startBlock.getRelative(x, 1, z).getLightLevel()<13){
					if(r<150){
						startBlock.getRelative(x, 1, z)
							.setType(Material.RED_MUSHROOM);
					}
					else if(r<450){
						startBlock.getRelative(x, 1, z)
							.setType(Material.BROWN_MUSHROOM);
					}
				}
				else if(r<100){ //water with lily pad
					startBlock.getRelative(x, 0, z).setType(Material.WATER);
					startBlock.getRelative(x, 1, z).setType(Material.WATER_LILY);
				}
				else if(r<350){ //tree
					world.generateTree(
							startBlock.getRelative(x, 1, z).getLocation(),
							TreeType.SWAMP);
				}
			}
		}
	}
	
	/**
	 * Modifies a island according to the plains biome
	 * @param startBlock The blcok where the island starts
	 */
	private void placePlainsObjects(Block startBlock) {
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(1000);
				if(r<200){ //yellow flower
					startBlock.getRelative(x, 1, z).setType(Material.YELLOW_FLOWER);
				}
				else if(r<400){ //red rose
					startBlock.getRelative(x, 1, z).setType(Material.RED_ROSE);
				}
				else{ //tall grass
					startBlock.getRelative(x, 1, z).setType(Material.LONG_GRASS);
					startBlock.getRelative(x, 1, z).setData((byte)1);
				}
			}
		}
	}
	
	/**
	 * Modifies a island according to the ice plains biome
	 * @param startBlock The first block of the island
	 */
	private void placeIcePlainsObjects(Block startBlock) {
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(1000);
				if(r<100){ //ice block
					startBlock.getRelative(x, 0, z).setType(Material.ICE);
				}
				else if(r<300){ //tall grass
					startBlock.getRelative(x, 1, z).setType(Material.LONG_GRASS);
					startBlock.getRelative(x, 1, z).setData((byte)1);
				}
				else{ //layer of snow
					startBlock.getRelative(x, 1, z).setType(Material.SNOW);
				}
			}
		}
	}

	private void placeJungleObjects(Block startBlock) {
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(1000);
				if(r<100){ //small jungle tree with coca bean
					/*if we've set a adjacent tree before, forget about it*/
					if(x>0 && startBlock.getRelative(x-1, 1, z)
							.getType()==Material.LOG){
						continue;
					}
					if(z>0 && startBlock.getRelative(x, 1, z-1)
							.getType()==Material.LOG){
						continue;
					}
					world.generateTree(
							startBlock.getRelative(x, 1, z).getLocation(),
							TreeType.SMALL_JUNGLE);
					Block log=startBlock.getRelative(x-1, 3, z);
					if(log.getType()==Material.AIR){
						log.setType(Material.COCOA);
						log.setData((byte)3); //for cocoa direction
					}
				}
				/* reeds at water source block:
				 * only look for a adjacent block "behind" and ensure that
				 * this one is still on the island*/
				else if(r<250 && x>0){
					Block adjacent=startBlock.getRelative(x-1, 0, z);
					if(adjacent.getRelative(BlockFace.UP).getType()==Material.AIR){
						adjacent.setType(Material.WATER);
						startBlock.getRelative(x, 1, z)
							.setType(Material.SUGAR_CANE_BLOCK);
					}
				}
				else if(r<400 && z>0){
					Block adjacent=startBlock.getRelative(x, 0, z-1);
					if(adjacent.getRelative(BlockFace.UP).getType()==Material.AIR){
						adjacent.setType(Material.WATER);
						startBlock.getRelative(x, 1, z)
							.setType(Material.SUGAR_CANE_BLOCK);
					}
				}
			}
		}
	}

	private void placeTaigaObjects(Block startBlock) {
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(1000);
				/*since low light places for mushrooms are rare,
				 * try to spawn them with higher probability*/
				if(startBlock.getRelative(x, 1, z).getLightLevel()<13){
					if(r<150){
						startBlock.getRelative(x, 1, z)
							.setType(Material.RED_MUSHROOM);
					}
					else if(r<450){
						startBlock.getRelative(x, 1, z)
							.setType(Material.BROWN_MUSHROOM);
					}
				}
				else if(r<100){ //spruce tree
					world.generateTree(
							startBlock.getRelative(x, 1, z).getLocation(),
							TreeType.REDWOOD);
				}
				else if(r<200){
					startBlock.getRelative(x, 1, z).setType(Material.PUMPKIN);
				}
			}
		}
	}

	private void placeForestObjects() {
		//TODO
	}
	
	/**
	 * Modifies island at given start position according to desert biome
	 * @param startBlock The first block of the island
	 */
	private void placeDesertObjects(Block startBlock) {
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(1000);
				if(r<100){
					startBlock.getRelative(x, 1, z).setType(Material.CACTUS);
					/*remove surrounding blocks since cacti need this space*/
					startBlock.getRelative(x-1, 1, z).setType(Material.AIR);
					startBlock.getRelative(x+1, 1, z).setType(Material.AIR);
					startBlock.getRelative(x, 1, z-1).setType(Material.AIR);
					startBlock.getRelative(x, 1, z+1).setType(Material.AIR);
				}
				else if(r<400){
					startBlock.getRelative(x, 1, z).setType(Material.LONG_GRASS);
				}
			}
		}
	}
	
	private Block getAdjacentBlockOfType(Block block, Material type){
		Block result=block.getRelative(BlockFace.NORTH);
		if(result.getType()!=type){
			result=block.getRelative(BlockFace.EAST);
			if(result.getType()!=type){
				result=block.getRelative(BlockFace.SOUTH);
				if(result.getType()!=type){
					result=block.getRelative(BlockFace.WEST);
					if(result.getType()!=type){
						return null;
					}
					else return result;
				}
				else return result;
			}
			else return result;
		}
		else return result;
	}
	
	private Block getFirstSolidBlockInChunk(){
		Block retBlock=null;
		for(int x=0; x<16; x++){
			for(int z=0; z<16; z++){
				retBlock=chunk.getBlock(x, maxGenHeight, z);
				while(retBlock.getType()==Material.AIR
						&& retBlock.getY()>minGenHeight){
					retBlock=retBlock.getRelative(BlockFace.DOWN);
				}
				if(retBlock.getType()!=Material.AIR) return retBlock;
			}
		}
		return retBlock;
	}
}
