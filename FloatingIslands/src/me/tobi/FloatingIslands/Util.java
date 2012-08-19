package me.tobi.FloatingIslands;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Util {
	/**
	 * 
	 * @param spawnBlock The block, above the player spawns
	 * @return
	 */
	public static boolean isValidSpawn(Block spawnBlock){
		if(spawnBlock.getType()==Material.GRASS){
			if(spawnBlock.getRelative(0, 1, 0).getType()==Material.AIR
					&& spawnBlock.getRelative(0, 2, 0).getType()==Material.AIR
					&& spawnBlock.getRelative(0, 3, 0).getType()==Material.AIR){
				return true;
			}
			else return false;
		}
		else return false;
	}
	
	public static Block getHighestBlockOfType(World world, int x, int z,
			int minHeight, int maxHeight, Material type){
		Block block=world.getBlockAt(x, maxHeight, z);
		while(block.getType()!=type && block.getY()>minHeight){
			block=block.getRelative(BlockFace.DOWN);
		}
		return block;
	}
	
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
			startBlock.getRelative(2, 0, 2).setType(Material.GRASS);
			startBlock.getRelative(2, 1, 2).setType(Material.AIR);
			startBlock.getWorld().generateTree(
					startBlock.getRelative(2, 1, 2).getLocation(),
					TreeType.TREE
			);
		}
	}
}
