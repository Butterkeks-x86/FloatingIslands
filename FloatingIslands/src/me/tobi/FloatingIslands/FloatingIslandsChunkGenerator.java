package me.tobi.FloatingIslands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
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

	@Override
	public boolean canSpawn(World world, int x, int z){
		Block highest=world.getBlockAt(x, world.getHighestBlockYAt(x, z), z);
		if(highest.getType()==Material.GRASS) return true;
		else return false;
	}
	
	private void generateIslandAccordingToBiome(byte[][] chunk, int height,
			Random ran, Biome bio){
		if(bio==Biome.DESERT || bio==Biome.DESERT_HILLS){
			generateSandIsland(chunk, height, ran);
		}
		else{
			generateDirtIsland(chunk, height, ran);
		}
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
