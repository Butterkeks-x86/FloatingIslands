package com.github.butterkeks_x86.floatingIslands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Util {
	
	/**
	 * Provides the highest block at a given position of given type
	 * @param world The world the location is in
	 * @param x The x-coordinate to search
	 * @param z The z-coordinate to search
	 * @param minHeight the minimum height to search at
	 * @param maxHeight The maximum heigth to search at
	 * @param type The type of block searched for
	 * @return The highest block of this type or the block at minimum height
	 */
	public static Block getHighestBlockOfType(World world, int x, int z,
			int minHeight, int maxHeight, Material type){
		Block block=world.getBlockAt(x, maxHeight, z);
		while(block.getType()!=type && block.getY()>minHeight){
			block=block.getRelative(BlockFace.DOWN);
		}
		return block;
	}
	
	/**
	 * Provides the first non-air block in the given chunk
	 * @param chunk
	 * @param maxHeight The maximum height to search at
	 * @param minHeight The minimum heigth to sreach at
	 * @return The first solid block or the last tested air block, if none found
	 */
	public static Block getFirstSolidBlockInChunk(Chunk chunk,
			int maxHeight, int minHeight){
		Block retBlock=null;
		for(int x=0; x<16; x++){
			for(int z=0; z<16; z++){
				retBlock=chunk.getBlock(x, maxHeight, z);
				while(retBlock.getType()==Material.AIR
						&& retBlock.getY()>minHeight){
					retBlock=retBlock.getRelative(BlockFace.DOWN);
				}
				if(retBlock.getType()!=Material.AIR) return retBlock;
			}
		}
		return retBlock;
	}
	
	/**
	 * Gets a direct adjacent block (not diagonal) block of the given type
	 * @param block The block whose adjacent neighbour is searched
	 * @param type The type of the block searched for
	 * @return The next adjacent block of the given type or null if none found
	 */
	public static Block getAjacentBlockOfType(Block block, Material type){
		if(block.getRelative(BlockFace.NORTH).getType()!=type){
			if(block.getRelative(BlockFace.EAST).getType()!=type){
				if(block.getRelative(BlockFace.SOUTH).getType()!=type){
					if(block.getRelative(BlockFace.WEST).getType()!=type){
						return null;
					}
					else return block.getRelative(BlockFace.WEST);
				}
				else return block.getRelative(BlockFace.SOUTH);
			}
			else return block.getRelative(BlockFace.EAST);
		}
		else return block.getRelative(BlockFace.NORTH);
	}
	
	/**
	 * Ensures a valid spawn for the floating islands world. if the spawn could be read
	 * from file, it will be set. If not, it will be searched for a new valid spawn,
	 * the spawning island will be created, the spawn will be set and written to file.
	 * Note: this only applies to FloatingIslands worlds! This method will run endlessly
	 * if providing a normal world or invalid FloatingIslands config!
	 * @param floatingIslandsWorld The world to ensure a valid spawn
	 * @param dataFolder The folder to search for a old spawn file
	 * @param config The config of the FloatingIslands plugin
	 */
	private static int spawnX=6, spawnY=64, spawnZ=7; //TODO: read from config?
	public static void ensureValidSpawn(World floatingIslandsWorld, File dataFolder,
			FloatingIslandsConfig config){
		/*first, try to get old spawn*/
		Block oldSpawn;
		oldSpawn=Util.readSpawnFromFile(
				dataFolder.getAbsolutePath()+"/spawn", floatingIslandsWorld);
		if(oldSpawn!=null){
			return;
		}
		/*if not found, look for a new spawn and save it as default*/
		else{
			//look for an appropriate empty chunk
			Chunk spawnChunk=getNearEmptyChunkInPopulatedBiome(
					floatingIslandsWorld.getSpawnLocation().getChunk(),
					config.level1MaxGenHeight,
					config.level1MinGenHeight);
			//spawn block within this chunk is fixed
			Block spawnBlock=spawnChunk.getBlock(spawnX, spawnY, spawnZ);
			//create the spawning island
			createSpawnIsland(spawnBlock.getRelative(-1, -1, -1));
			//set the new spawn ...
			floatingIslandsWorld.setSpawnLocation(
					spawnBlock.getX(),
					spawnBlock.getY(),
					spawnBlock.getZ());
			//...and save it to file
			Util.saveSpawnToFile(dataFolder.getAbsolutePath()+"/spawn", spawnBlock);
		}
	}
	
	/**
	 * Searches for an empty chunk in a populated biome, i.e. not in
	 * a ocean or river biome
	 * @param chunk The chunk to start the search with (will be considered itself)
	 * @param level1MaxGenHeight ChunkGenerator maximum generation height
	 * @param level1MinGenHeight ChunkGenerator minimum generation height
	 * @return A empty chunk within a populated biome
	 */	
	public static Chunk getNearEmptyChunkInPopulatedBiome(Chunk chunk,
			int maxGenHeight, int minGenHeight){
		while(true){
			Biome bio=chunk.getWorld().getBiome(chunk.getX()*16, chunk.getZ()*16);
			while(bio==Biome.OCEAN || bio==Biome.FROZEN_OCEAN
					|| bio==Biome.RIVER || bio==Biome.FROZEN_RIVER){
				chunk=chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ()-1);
				bio=chunk.getWorld().getBiome(chunk.getX()*16, chunk.getZ()*16);
			}
			Block first=Util.getFirstSolidBlockInChunk(chunk, maxGenHeight, minGenHeight);
			while(first.getType()!=Material.AIR){
				chunk=chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ()-1);
				first=Util.getFirstSolidBlockInChunk(chunk, maxGenHeight, minGenHeight);
			}
			bio=chunk.getWorld().getBiome(chunk.getX()*16, chunk.getZ()*16);
			if(bio!=Biome.OCEAN && bio!=Biome.FROZEN_OCEAN
				&& bio!=Biome.RIVER && bio!=Biome.FROZEN_RIVER){
				return chunk;
			}
		}
	}
	
	/**
	 * Creates the spawning island
	 * @param startBlock The first block of the spawning island
	 */
	public static void createSpawnIsland(Block startBlock){
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
	
	/**
	 * Saves a given spawn location to a file as specified by path. Old spawn (if exists)
	 * might be overwritten.
	 * @param path The path to the spawn file
	 * @param spawnBlock The spawn block to spawn inside
	 */
	public static void saveSpawnToFile(String path, Block spawnBlock){
		BufferedWriter out=null;
		try {
			out=new BufferedWriter(new FileWriter(path));
			out.write(spawnBlock.getX()+" "+spawnBlock.getY()+" "
					+spawnBlock.getZ()+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out!=null){
				try{
					out.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Tries to read a spawn block from a given spawn file.
	 * @param path The path to the spawn file
	 * @param world The world the spawn resides in
	 * @return The spawn block if found; null otherwise
	 */
	public static Block readSpawnFromFile(String path, World world){
		BufferedReader in=null;
		Block ret=null;
		try{
			in=new BufferedReader(new FileReader(path));
			String tokens[]=in.readLine().split(" ");
			if(tokens.length==3){
				ret=world.getBlockAt(
						Integer.parseInt(tokens[0]),
						Integer.parseInt(tokens[1]),
						Integer.parseInt(tokens[2])
						);
			}
		}catch(FileNotFoundException e){
			return null;
		}catch(IOException e){
			e.printStackTrace();
		}catch(NumberFormatException e){
			e.printStackTrace();
		}finally{
			try{
				if(in!=null){
					in.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * Serializes a PlayerHandler object to the according file.
	 * @param ph The PlayerHandler to serialize
	 * @param dataFolder the FloatingIslandsPlugin data folder
	 */
	public static void serializePlayerHandler(FloatingIslandsPlayerHandler ph,
			File dataFolder){
		if(ph==null) return;
		ObjectOutputStream oout=null;
		try{
			//proove that file exists; if not create a new one
			File playerFile=new File(dataFolder.getAbsolutePath()+"/"+ph.getPlayerName());
			if(!playerFile.exists()){
				playerFile.createNewFile();
			}
			//write the given PlayerHandler to file
			oout=new ObjectOutputStream(new FileOutputStream(playerFile));
			oout.writeObject(ph);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(oout!=null){
					oout.close();
				}
			}catch(IOException e){
					e.printStackTrace();
			}
		}
	}
	
	/**
	 * Tries to deserialize a PlayerHandler object from a file.
	 * @param dataFolder The data folder of FloatingIsalndsPlugin
	 * @param playerName the name of the player whose Playerhandler is searched
	 * @return the requested PlayerHandler or null, if file or object in file not found
	 */
	public static FloatingIslandsPlayerHandler deserializePlayerHandler(File dataFolder,
			String playerName){
		//first, proove that file exists
		File playerFile=new File(dataFolder.getAbsolutePath()+"/"+playerName);
		if(!playerFile.exists()){
			return null;
		}
		//try to read in the requested object
		ObjectInputStream oin=null;
		Object ret=null;
		try{
			oin=new ObjectInputStream(new FileInputStream(playerFile));
			ret=oin.readObject();
		}catch(ClassNotFoundException e){
			ret=null;
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(oin!=null){
					oin.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		//perform simple validation
		if(((FloatingIslandsPlayerHandler)ret).getPlayerName().equalsIgnoreCase(playerName)){
			return (FloatingIslandsPlayerHandler)ret;
		}
		else return null;
	}
}
