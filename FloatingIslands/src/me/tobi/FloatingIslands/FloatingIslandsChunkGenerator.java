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
	
	private int level0MaxGenHeight=32;
	private int level0MinGenHeight=6;
	private double level0GenProbability=0.1;
	private int level1MaxGenHeight=127;
	private int level1MinGenHeight=0;
	private double level1GenProbability=0.3;
	
	/**
	 * Constructor
	 * @param logger The logger to use within this class
	 */
	public FloatingIslandsChunkGenerator(int level0MaxGenHeight,
			int level0MinGenHeight, double level0GenProbability,
			int level1MaxGenHeight, int level1MinGenHeight,
			double level1GenProbability){
		this.level0MaxGenHeight=level0MaxGenHeight;
		this.level0MinGenHeight=level0MinGenHeight;
		this.level0GenProbability=level0GenProbability;
		this.level1MaxGenHeight=level1MaxGenHeight;
		this.level1MinGenHeight=level1MinGenHeight;
		this.level1GenProbability=level1GenProbability;
	}
	
	@Override
	public byte[][] generateBlockSections(World world, Random random,
			int chunkX, int chunkZ, BiomeGrid biomeGrid){
		
		

		byte[][] result;
		result=new byte[16][];
		
		/*generate with given probability a level0 island*/
		if(random.nextInt(100)<Math.floor(level0GenProbability*100)){
			int y=random.nextInt(level0MaxGenHeight-level0MinGenHeight)
					+level0MinGenHeight;
			int x=random.nextInt(14);
			int z=random.nextInt(14);
			generateLevel0Island(result, x, y, z, random);
		}
		
		/*generate with given probability an level1 island*/
		if(random.nextInt(100)<Math.floor(level1GenProbability*100)){
			int height=random.nextInt(level1MaxGenHeight-level1MinGenHeight)
					+level1MinGenHeight;
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
		list.add(new FloatingIslandsChunkPopulator(level1MaxGenHeight,
				level1MinGenHeight));
		return list;
	}
	
	/**
	 * Return always false: spawn location will be evaluated in playerJoinListener
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
			generateSandIsland(chunk, x, y, z, ran);
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
			generateDirtIsland(chunk, x, y, z, ran);
		}
	}
	
	private void generateDirtIslandWithOres(byte[][] chunk, int x, int y, int z,
			Random ran) {
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		if(z<7){
			sg.generateLayersRandomReplace(x, y-1, z, 3, 2,
					(byte)Material.DIRT.getId(), (byte)Material.IRON_ORE.getId());
		}
		else{
			sg.generateLayersRandomReplace(x, y-1, z, 3, 2,
					(byte)Material.DIRT.getId(), (byte)Material.COAL_ORE.getId());
		}
		sg.generateLayer(x, y, z, 3, (byte)Material.GRASS.getId());
	}

	private void generateSandIsland(byte[][] chunk, int x, int y, int z, Random ran){
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		sg.generateLayers(x, y, z, 3, 3, (byte)Material.SAND.getId());
	}
	
	private void generateDirtIsland(byte[][] chunk, int x, int y, int z, Random ran){
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		sg.generateLayers(x, y-1, z, 3, 2, (byte)Material.DIRT.getId());
		sg.generateLayer(x, y, z, 3, (byte)Material.GRASS.getId());
	}
	
	private void generateLevel0Island(byte[][] chunk, int xs, int ys, int zs,
			Random ran){
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		//first two layers of ores
		for(int y=ys-2; y<ys; y++){
			for(int x=xs; x<xs+3; x++){
				for(int z=zs; z<zs+3; z++){
					int r=ran.nextInt(1000);
					if(r<150){
						sg.setBlock(x, y, z, (byte)Material.COAL_ORE.getId());
					}
					else if(r<250){
						sg.setBlock(x, y, z, (byte)Material.IRON_ORE.getId());
					}
					else if(r<300){
						sg.setBlock(x, y, z, (byte)Material.REDSTONE_ORE.getId());
					}
					else if(r<340){
						sg.setBlock(x, y, z, (byte)Material.GOLD_ORE.getId());
					}
					else if(r<370){
						sg.setBlock(x, y, z, (byte)Material.DIAMOND_ORE.getId());
					}
					else sg.setBlock(x, y, z, (byte)Material.STONE.getId());
				}
			}
		}
		//top layer of stone and lava
		for(int x=xs; x<xs+3; x++){
			for(int z=zs; z<zs+3; z++){
				int r=ran.nextInt(1000);
				if(r<100 && !((x==xs || x==xs+2) && (z==zs || z==zs+2))){
					sg.setBlock(x, ys, z, (byte)Material.LAVA.getId());
				}
				else sg.setBlock(x, ys, z, (byte)Material.STONE.getId());
			}
		}
	}
}
