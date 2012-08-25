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
	
	private int maxGenHeight=127;
	private int minGenHeight=0;
	private double islandGenProbability=0.3;
	
	/**
	 * Constructor
	 * @param logger The logger to use within this class
	 */
	public FloatingIslandsChunkGenerator(int maxGenHeight, int minGenHeight,
			double islandGenProbability){
		this.maxGenHeight=maxGenHeight;
		this.minGenHeight=minGenHeight;
		this.islandGenProbability=islandGenProbability;
	}
	
	@Override
	public byte[][] generateBlockSections(World world, Random random,
			int chunkX, int chunkZ, BiomeGrid biomeGrid){
		
		

		byte[][] result;
		result=new byte[16][];
		
		/*generate with given probability an island*/
		if(random.nextInt(100)<Math.floor(islandGenProbability*100)){
			int height=random.nextInt(maxGenHeight-minGenHeight)+minGenHeight;
			int x=random.nextInt(14);
			int z=random.nextInt(14);
			generateIslandAccordingToBiome(result, x, height, z, random,
					biomeGrid.getBiome(x, z));
		}
		
		//TODO: remove, since for debugging purposes
		new StructureGenerator(result, random)
			.generateLayer(0, 0, 0, 16, (byte)Material.BEDROCK.getId());
		
		return result;
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world){
		ArrayList<BlockPopulator> list=new ArrayList<BlockPopulator>();
		list.add(new FloatingIslandsChunkPopulator(maxGenHeight, minGenHeight));
		return list;
	}
	
	/* Some kind useless, since world attempts to few spawns to
	 * have a hight probability of targeting a usable spawn place
	 * (non-Javadoc)
	 * @see org.bukkit.generator.ChunkGenerator#canSpawn(org.bukkit.World, int, int)
	 */
	@Override
	public boolean canSpawn(World world, int x, int z){
//		Block spawnBlock=Util.getHighestBlockOfType(world, x, z,
//				minGenHeight, maxGenHeight, Material.GRASS);
//		if(spawnBlock.getType()!=Material.GRASS) return false;
//		else return Util.isValidSpawn(spawnBlock.getRelative(BlockFace.UP));
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
	private void generateIslandAccordingToBiome(byte[][] chunk, int x, int y, int z,
			Random ran, Biome bio){
		if(bio==Biome.DESERT || bio==Biome.DESERT_HILLS || bio==Biome.BEACH){
			generateSandIsland(chunk, x, y, z, ran);
		}
		else if(bio==Biome.FOREST_HILLS || bio==Biome.TAIGA_HILLS
				|| bio==Biome.JUNGLE_HILLS || bio==Biome.ICE_MOUNTAINS
				|| bio==Biome.SMALL_MOUNTAINS || bio==Biome.EXTREME_HILLS){
			generateDirtIslandWithOres(chunk, x, y, z, ran);
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
}
