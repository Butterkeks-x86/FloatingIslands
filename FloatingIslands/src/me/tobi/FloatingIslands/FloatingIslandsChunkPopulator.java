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

public class FloatingIslandsChunkPopulator extends BlockPopulator {
	
	private World world;
	private Chunk chunk;
	private Random ran;
	
	@Override
	public void populate(World world, Random ran, Chunk chunk){
		this.world=world;
		this.chunk=chunk;
		this.ran=ran;
		placeObjects();
	}
	
	private void placeObjects(){
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
		placeJungleObjects();
	}

	private void placeSwampObjects() {
		int y=getHighestChunkBlockY()+1;
		if(chunk.getBlock(0, y-1, 0).getType()!=Material.GRASS) return;
		//TODO: ignore spawn island
		Random ran=new Random();
		for(int x=0; x<3; x++){
			for(int z=0; z<2; z++){
				int r=ran.nextInt(100);
				if(r<15){
					changeChunkBlockAt(x,y-1,z, Material.WATER, 0);
					changeChunkBlockAt(x,y,z, Material.WATER_LILY, 0);
				}
				else if(r<35){
					changeChunkBlockAt(x,y,z, Material.BROWN_MUSHROOM, 0);
				}
				else if(r<55){
					changeChunkBlockAt(x,y,z, Material.RED_MUSHROOM, 0);
				}
				else{}
			}
		}
		if(ran.nextInt()<50){
			world.generateTree(chunk.getBlock(2,y,2).getLocation(),
					TreeType.SWAMP);
		}
	}

	private void placePlainsObjects() {
		int y=getHighestChunkBlockY()+1;
		if(chunk.getBlock(0, y-1, 0).getType()!=Material.GRASS) return;
		//TODO: ignore spawn island
		Random ran=new Random();
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(100);
				if(r<40){
					changeChunkBlockAt(x,y,z, Material.LONG_GRASS, 0);
				}
				else if(r<55){
					changeChunkBlockAt(x,y,z, Material.YELLOW_FLOWER, 0);
				}
				else if(r<70){
					changeChunkBlockAt(x,y,z, Material.RED_ROSE, 0);
				}
				else{}
			}
		}
	}

	private void placeIcePlainsObjects() {
		int y=getHighestChunkBlockY()+1;
		if(chunk.getBlock(0, y-1, 0).getType()!=Material.GRASS) return;
		//TODO: ignore spawn island
		Random ran=new Random();
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(100);
				if(r<10){
					changeChunkBlockAt(x,y,z, Material.LONG_GRASS, 1);
				}
				else if(r<20){
					changeChunkBlockAt(x,y-1,z, Material.ICE, 0);
				}
				else{
					changeChunkBlockAt(x,y,z, Material.SNOW, 0);
				}
			}
		}
	}

	private void placeJungleObjects() {
		for(int x=0; x<16; x++){
			for(int z=0; z<16; z++){
				Block block=getHighestChunkBlockYAt(x,z);
				if(block.getType()==Material.GRASS){
					int r=ran.nextInt();
					if(r<20){ //jungle treee
						world.generateTree(
								block.getRelative(BlockFace.UP).getLocation(),
								TreeType.SMALL_JUNGLE);
					}
					else if(r<40){ //reeds at water source block
						Block neighbour=getAdjacentBlockOfType(block, Material.GRASS);
						if(neighbour!=null){
							neighbour.getRelative(BlockFace.UP).setType(Material.AIR);
							neighbour.setType(Material.WATER);
							block.getRelative(BlockFace.UP)
								.setType(Material.SUGAR_CANE_BLOCK);
						}
					}
					else if(r<60){ //tall grass
						block.getRelative(BlockFace.UP).setType(Material.LONG_GRASS);
					}
					else{}
				}
			}
		}
	}

	private void placeTaigaObjects() {
		// TODO Auto-generated method stub
		
	}

	private void placeForestObjects() {
		// TODO Auto-generated method stub
		
	}

	private void placeDesertObjects() {
		int y=getHighestChunkBlockY()+1;
		if(chunk.getBlock(0, y-1, 0).getType()!=Material.SAND) return;
		//TODO: ignore spawn island
		Random ran=new Random();
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				int r=ran.nextInt(100);
				if(r<10){
					changeChunkBlockAt(x,y,z, Material.LONG_GRASS, 0);
				}
				else if(r<25){
					changeChunkBlockAt(x-1,y-1,z, Material.WATER, 0);
					changeChunkBlockAt(x,y,z, Material.SUGAR_CANE_BLOCK, 0);
				}
				else if(r<40){
					changeChunkBlockAt(x,y,z, Material.CACTUS, 0);
				}
				else{}
			}
		}
	}
	
	private int getHighestChunkBlockY(){
		Block highest=chunk.getBlock(0, 127, 0);
		while(highest.getType()==Material.AIR && highest.getY()>0){
			highest=highest.getRelative(0, -1, 0);
		}
		int y=highest.getY();
		assert(y>=0);
		assert(y<128);
		return y;
	}
	
	/**
	 * Returns the highest non-air block at the position x,z (relative chunk)
	 * @param x 0...15
	 * @param z 0...15
	 * @return The highest non-ait block or the lowest air block, if none found
	 */
	private Block getHighestChunkBlockYAt(int x, int z){
		Block highest=chunk.getBlock(x, 127, z);
		while(highest.getType()==Material.AIR && highest.getY()>0){
			highest=highest.getRelative(0, -1, 0);
		}
		return highest;
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
	
	private void changeChunkBlockAt(int x, int y, int z, Material newType,
			int newData){
		Block b=chunk.getBlock(x, y, z);
		b.setType(newType);
		b.setData((byte)newData);
	}
}
