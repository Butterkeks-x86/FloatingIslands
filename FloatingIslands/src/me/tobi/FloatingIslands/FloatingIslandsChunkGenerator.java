package me.tobi.FloatingIslands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class FloatingIslandsChunkGenerator extends ChunkGenerator {
	
	private FloatingIslandsConfig config;
	
	/**
	 * Constructor
	 * @param logger The logger to use within this class
	 */
	public FloatingIslandsChunkGenerator(FloatingIslandsConfig config){
		this.config=config;
	}
	
	@Override
	public byte[][] generateBlockSections(World world, Random random,
			int chunkX, int chunkZ, BiomeGrid biomeGrid){
		
		

		byte[][] result;
		result=new byte[16][];
		
		/*generate with given probability a level0 island*/
		if(random.nextInt(100)<Math.floor(config.level0GenProbability*100)){
			int y=random.nextInt(config.level0MaxGenHeight-config.level0MinGenHeight)
					+config.level0MinGenHeight;
			int x=random.nextInt(14);
			int z=random.nextInt(14);
			generateLevel0Island(result, x, y, z, random);
		}
		
		/*generate with given probability an level1 island*/
		if(random.nextInt(100)<Math.floor(config.level1GenProbability*100)){
			int height=random.nextInt(
					config.level1MaxGenHeight-config.level1MinGenHeight)
					+config.level1MinGenHeight;
			int x=random.nextInt(14);
			int z=random.nextInt(14);
			generateLevel1Island(result, x, height, z, random,
					biomeGrid.getBiome(x, z));
		}
		
		return result;
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world){
		ArrayList<BlockPopulator> list=new ArrayList<BlockPopulator>();
		list.add(new FloatingIslandsChunkPopulator(config.level1MaxGenHeight,
				config.level1MinGenHeight));
		return list;
	}
	
	/**
	 * Return always false: spawn location will be prepared in playerJoinListener
	 */
	@Override
	public boolean canSpawn(World world, int x, int z){
		return false;
	}
	
	/**
	 * Generates an Island according to a given biome.
	 * @param chunk The byte array the result should be stored
	 * @param x The x start coordinate within the chunk 
	 * @param y The height of the top blocks of the island
	 * @param z The z start coordinate within the chunk
	 * @param ran The random to use
	 * @param bio The given biome
	 */
	private void generateLevel1Island(byte[][] chunk, int x, int y, int z,
			Random ran, Biome bio){
		if(bio==Biome.DESERT || bio==Biome.DESERT_HILLS || bio==Biome.BEACH){
			generateSandIsland(chunk, x, y, z);
		}
		else if(bio==Biome.FOREST_HILLS || bio==Biome.TAIGA_HILLS
				|| bio==Biome.JUNGLE_HILLS || bio==Biome.ICE_MOUNTAINS
				|| bio==Biome.SMALL_MOUNTAINS || bio==Biome.EXTREME_HILLS){
			generateDirtIslandWithOres(chunk, x, y, z, ran);
		}
		else if(bio==Biome.OCEAN || bio==Biome.FROZEN_OCEAN || bio==Biome.RIVER
				|| bio==Biome.FROZEN_RIVER){
			return; //do nothing
		}
		else{
			generateDirtIsland(chunk, x, y, z);
		}
	}
	
	/**
	 * Generates a 3x3x3 sand island at given start coordinates within given chunk.
	 * The start coordinates represend the top layer SW edge block.
	 * @param chunk The chunk to generate in
	 * @param xs The x start cordinate (0...13)
	 * @param ys The y start coordinate (2...127), i.e. top layer
	 * @param zs The z start coordinate (0...13)
	 */
	private void generateSandIsland(byte[][] chunk, int xs, int ys, int zs){
		for(int y=ys-2; y<=ys; y++){
			for(int x=xs; x<xs+3; x++){
				for(int z=zs; z<zs+3; z++){
					setBlock(chunk, x, y, z, Material.SAND);
				}
			}
		}
	}
	
	/**
	 * Generates a dirt island with a top layer of grass with in given chunk.
	 * Start coordinates represent top layer SW edge block.
	 * @param chunk The chunk to generate in
	 * @param xs The x start coordinate (0...13)
	 * @param ys The y start coordinate (2...127)
	 * @param zs The z start coordinate (0...13)
	 */
	private void generateDirtIsland(byte[][] chunk, int xs, int ys, int zs){
		for(int y=ys-2; y<ys; y++){
			for(int x=xs; x<xs+3; x++){
				for(int z=zs; z<zs+3; z++){
					setBlock(chunk, x, y, z, Material.DIRT);
				}
			}
		}
		for(int x=xs; x<xs+3; x++){
			for(int z=zs; z<zs+3; z++){
				setBlock(chunk, x, ys, z, Material.GRASS);
			}
		}
	}
	
	/**
	 * Generates a dirt island with a top layer of grass and two layers
	 * of randomly placed iron or coal ores.
	 * The start coordinates represent the top layer SW edge block.
	 * @param chunk The chunk to generate in
	 * @param xs The x start coordinate (0...13)
	 * @param ys The y start coordinate (2...127)
	 * @param zs The z start coordinate (0...13)
	 * @param ran The random needed
	 */
	private void generateDirtIslandWithOres(byte [][]chunk, int xs, int ys, int zs,
			Random ran){
		for(int y=ys-2; y<ys; y++){
			for(int x=xs; x<xs+3; x++){
				for(int z=zs; z<zs+3; z++){
					int r=ran.nextInt(1000);
					if(r<100){
						setBlock(chunk, x, y, z, Material.COAL_ORE);
					}
					else if(r<150){
						setBlock(chunk, x, y, z, Material.IRON_ORE);
					}
					else setBlock(chunk, x, y, z, Material.DIRT);
				}
			}
		}
		for(int x=xs; x<xs+3; x++){
			for(int z=zs; z<zs+3; z++){
				setBlock(chunk, x, ys, z, Material.GRASS);
			}
		}
	}
	
	private void generateLevel0Island(byte[][] chunk, int xs, int ys, int zs,
			Random ran){
		//first two layers of ores
		for(int y=ys-2; y<ys; y++){
			for(int x=xs; x<xs+3; x++){
				for(int z=zs; z<zs+3; z++){
					int r=ran.nextInt(1000);
					if(r<150){
						setBlock(chunk, x, y, z, Material.COAL_ORE);
					}
					else if(r<250){
						setBlock(chunk, x, y, z, Material.IRON_ORE);
					}
					else if(r<300){
						setBlock(chunk, x, y, z, Material.REDSTONE_ORE);
					}
					else if(r<340){
						setBlock(chunk, x, y, z, Material.GOLD_ORE);
					}
					else if(r<370){
						setBlock(chunk, x, y, z, Material.DIAMOND_ORE);
					}
					else setBlock(chunk, x, y, z, Material.STONE);
				}
			}
		}
		//top layer of stone and lava
		for(int x=xs; x<xs+3; x++){
			for(int z=zs; z<zs+3; z++){
				int r=ran.nextInt(1000);
				if(r<100 && !((x==xs || x==xs+2) && (z==zs || z==zs+2))){
					setBlock(chunk, x, ys, z, Material.LAVA);
				}
				else setBlock(chunk, x, ys, z, Material.STONE);
			}
		}
	}
	
	/**
	 * Utility method to set a block within the chunk array.
	 * @param chunk The chunk array to set the block in
	 * @param x The x coordinate of the block, values from 0...15
	 * @param y The y coordinate of the block, values 0...127
	 * @param z The z coordinate of the block, values 0...15
	 * @param type The Material of the block
	 */
	private void setBlock(byte [][]chunk, int x, int y, int z, Material type) {
	    if (chunk[y >> 4] == null) //is this chunkpart already initialised?
	    {
	        chunk[y >> 4] = new byte[4096]; //initialise the chunk part
	    }
	    //set the block
	    chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte)type.getId();
	}
}
