package me.tobi.FloatingIslands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Util {
	/**
	 * Tests, if a given block is valid for spawning a player
	 * @param spawnBlock The block, the player spawns "inside"
	 * @return True, if a valid spawnBlock, false otherwise
	 */
	public static boolean isValidSpawn(Block spawnBlock){
		//ensure that the block below the player is grass
		if(spawnBlock.getRelative(BlockFace.DOWN).getType()==Material.GRASS){
			//ensure that the player can spawn inside the spawn block
			if(spawnBlock.getType()==Material.AIR
					|| spawnBlock.getType()==Material.GRASS
					|| spawnBlock.getType()==Material.SUGAR_CANE_BLOCK
					|| spawnBlock.getType()==Material.RED_MUSHROOM
					|| spawnBlock.getType()==Material.BROWN_MUSHROOM
					|| spawnBlock.getType()==Material.RED_ROSE
					|| spawnBlock.getType()==Material.YELLOW_FLOWER
					|| spawnBlock.getType()==Material.SNOW
			){
				//ensure that the two blocks above them are air
				if(spawnBlock.getRelative(0, 2, 0).getType()==Material.AIR
					&& spawnBlock.getRelative(0, 3, 0).getType()==Material.AIR){
					return true;
				}
				else return false;
			}
			else return false;
		}
		else return false;
	}
	
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
	 * Ensures a tree on an island
	 * @param startBlock The first block of this island
	 */
	public static void ensureTreeAtIsland(Block startBlock){
		boolean treeFound=false;
		for(int x=0; x<3; x++){
			for(int z=0; z<3; z++){
				if(startBlock.getRelative(x, 1, z).getType()==Material.LOG){
					treeFound=true;
					return;
				}
			}
		}
		if(!treeFound){
			startBlock.getRelative(2, 0, 2).setType(Material.DIRT); //base of tree
			startBlock.getRelative(2, 1, 2).setType(Material.AIR); //free this block
			startBlock.getWorld().generateTree(
					startBlock.getRelative(2, 1, 2).getLocation(),
					TreeType.TREE
			);
		}
	}
	
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
}
