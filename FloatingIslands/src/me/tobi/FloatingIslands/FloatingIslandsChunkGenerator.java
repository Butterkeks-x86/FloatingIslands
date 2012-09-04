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
