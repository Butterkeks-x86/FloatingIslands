package me.tobi.FloatingIslands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class FloatingIslandsChunkGenerator extends ChunkGenerator {
	
	static FloatingIslandsChunkGenerator instance;
	JavaPlugin parent;
	
	/**
	 * proteced constructor -> Singleton
	 * @param logger The logger to use within this class
	 */
	protected FloatingIslandsChunkGenerator(JavaPlugin parent){
		this.parent=parent;
	}
	
	/**
	 * Gets the instance of this Chunk Generator
	 * @param logger The logger to use by the instance
	 * @return A reference to the Chunk Generator instance
	 */
	public static FloatingIslandsChunkGenerator getInstance(JavaPlugin parent){
		if(instance!=null) return instance;
		else{
			instance=new FloatingIslandsChunkGenerator(parent);
			return instance;
		}
	}
	
	@Override
	public byte[][] generateBlockSections(World world, Random random,
			int chunkX, int chunkZ, BiomeGrid biomeGrid){
		
		int maxGenHeight=parent.getConfig().getInt("max-gen-height");
		int minGenHeight=parent.getConfig().getInt("min-gen-height");
		double islandGenProbability=
					parent.getConfig().getDouble("island-gen-probability");

		byte[][] result;
		result=new byte[16][];
		
		/*generate with given probability an island*/
		if(random.nextInt(100)<Math.floor(islandGenProbability*100)){
			int height=random.nextInt(maxGenHeight-minGenHeight)+minGenHeight;
			generateIslandAccordingToBiome(result, height, random,
					biomeGrid.getBiome(3, 3));
		}
		
		//TODO: remove, since for debugging purposes
		new StructureGenerator(result, random)
			.generateLayer(0, 0, 0, 16, (byte)Material.BEDROCK.getId());
		
		return result;
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world){
		ArrayList<BlockPopulator> list=new ArrayList<BlockPopulator>();
		list.add(new FloatingIslandsChunkPopulator(parent));
		return list;
	}
	
	/* Some kind useless, since world attempts to few spawns to
	 * have a hight probability of targeting a usable spawn place
	 * (non-Javadoc)
	 * @see org.bukkit.generator.ChunkGenerator#canSpawn(org.bukkit.World, int, int)
	 */
	@Override
	public boolean canSpawn(World world, int x, int z){
		int maxGenHeight=parent.getConfig().getInt("max-gen-height");
		int minGenHeight=parent.getConfig().getInt("min-gen-height");
		Block block=world.getBlockAt(x, maxGenHeight, z);
		while(block.getType()!=Material.GRASS && block.getY()>minGenHeight){
			block=block.getRelative(BlockFace.DOWN);
		}
		if(block.getType()==Material.GRASS){
			if(block.getRelative(0, 1, 0).getType()==Material.AIR
					&& block.getRelative(0, 2, 0).getType()==Material.AIR
					&& block.getRelative(0, 3, 0).getType()==Material.AIR){
				return true;
			}
			else return false;
		}
		else return false;
	}
	
	private void generateIslandAccordingToBiome(byte[][] chunk, int height,
			Random ran, Biome bio){
		if(bio==Biome.DESERT || bio==Biome.DESERT_HILLS){
			generateSandIsland(chunk, height, ran);
		}
		else if(bio==Biome.FOREST_HILLS || bio==Biome.TAIGA_HILLS
				|| bio==Biome.JUNGLE_HILLS || bio==Biome.ICE_MOUNTAINS
				|| bio==Biome.SMALL_MOUNTAINS || bio==Biome.EXTREME_HILLS){
			generateDirtIslandWithOres(chunk, height, ran);
		}
		else{
			generateDirtIsland(chunk, height, ran);
		}
	}
	
	private void generateDirtIslandWithOres(byte[][] chunk, int height,
			Random ran) {
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		int x=ran.nextInt(14);
		int z=ran.nextInt(14);
		if(z<7){
			sg.generateLayersRandomReplace(x, height-1, z, 3, 2,
					(byte)Material.DIRT.getId(), (byte)Material.IRON_ORE.getId());
		}
		else{
			sg.generateLayersRandomReplace(x, height-1, z, 3, 2,
					(byte)Material.DIRT.getId(), (byte)Material.COAL_ORE.getId());
		}
		sg.generateLayer(x, height, z, 3, (byte)Material.GRASS.getId());
	}

	private void generateSandIsland(byte[][] chunk, int height, Random ran){
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		int x=ran.nextInt(14);
		int z=ran.nextInt(14);
		sg.generateLayers(x, height, z, 3, 3, (byte)Material.SAND.getId());
	}
	
	private void generateDirtIsland(byte[][] chunk, int height, Random ran){
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		int x=ran.nextInt(14);
		int z=ran.nextInt(14);
		sg.generateLayers(x, height-1, z, 3, 2, (byte)Material.DIRT.getId());
		sg.generateLayer(x, height, z, 3, (byte)Material.GRASS.getId());
	}
}
