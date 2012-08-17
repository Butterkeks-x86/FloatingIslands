package me.tobi.FloatingIslands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Location;
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
		
		int spawnHeight=parent.getConfig().getInt("spawn-height");
		int maxGenHeight=parent.getConfig().getInt("max-gen-height");
		int minGenHeight=parent.getConfig().getInt("min-gen-height");
		double islandGenProbability=
					parent.getConfig().getDouble("island-gen-probability");
		Logger logger=parent.getLogger();
		byte[][] result;
		result=new byte[16][];
		
		Location spawn=world.getSpawnLocation();
		/*accept only a spawn at a chunk border at spawn height*/
		if(spawn==null){
			logger.warning("spawn is null, creating new one");
			world.setSpawnLocation(chunkX*16, spawnHeight, chunkZ*16);
			spawn=world.getSpawnLocation();
			logger.info("new spawn is at "+spawn.getBlockX()+", "+spawn.getBlockY()
					+", "+spawn.getBlockZ());
		}
		else if(spawn.getBlockX()%16!=0 || spawn.getBlockY()!=spawnHeight
				|| spawn.getBlockZ()%16!=0){
			logger.warning("invalid spawn location at "+spawn.getBlockX()+", "
				+spawn.getBlockY()+", "+spawn.getBlockZ()+"; creating new one");
			world.setSpawnLocation(chunkX*16, spawnHeight, chunkZ*16);
			spawn=world.getSpawnLocation();
			logger.info("new spawn is at "+spawn.getBlockX()+", "+spawn.getBlockY()
					+", "+spawn.getBlockZ());
		}
		/*if chunk requested to generate is spawn, create start island*/
		if(spawn.getBlockX()/16==chunkX && spawn.getBlockZ()/16==chunkZ){
			logger.info("generating start island");
			generateStartIsland(result, random);
		}
		/*else create with propability t other island*/
		else{
			if(random.nextInt(100)<Math.floor(islandGenProbability*100)){
				int height=random.nextInt(maxGenHeight-minGenHeight)+minGenHeight;
				generateIslandAccordingToBiome(result, height, random,
						biomeGrid.getBiome(3, 3));
			}
		}
		
		//TODO: remove, since for debugging purposes
		new StructureGenerator(result, random)
			.generateLayer(0, 0, 0, 16, (byte)Material.BEDROCK.getId());
		
		return result;
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world){
		ArrayList<BlockPopulator> list=new ArrayList<BlockPopulator>();
		list.add(new FloatingIslandsChunkPopulator());
		return list;
	}

	@Override
	public boolean canSpawn(World world, int x, int z){
		Block highest=world.getBlockAt(x, world.getHighestBlockYAt(x, z), z);
		if(highest.getType()==Material.GRASS) return true;
		else return false;
	}
	
	/**
	 * Generates the Island all players spawn on
	 * @param chunk The chunk to generate the island in
	 * @param ran The random needed
	 */
	private void generateStartIsland(byte[][] chunk, Random ran){
		//TODO: place spawn dynamically -> generate island according to this
		int spawnHeight=parent.getConfig().getInt("spawn-height");
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		sg.generateLayers(0, spawnHeight-3, 0, 3, 2, (byte)Material.DIRT.getId());
		sg.generateLayer(0, spawnHeight-2, 0, 3, (byte)Material.GRASS.getId());
		sg.setBlock(0, spawnHeight-1, 0, (byte)Material.BEDROCK.getId());
		sg.generateNormalTree(2, spawnHeight, 2, 
				(byte)Material.LOG.getId(), (byte)Material.LEAVES.getId());
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
		sg.generateLayers(0, height, 0, 3, 3, (byte)Material.SAND.getId());
	}
	
	private void generateDirtIsland(byte[][] chunk, int height, Random ran){
		StructureGenerator sg=new StructureGenerator(chunk, ran);
		sg.generateLayers(0, height-1, 0, 3, 2, (byte)Material.DIRT.getId());
		sg.generateLayer(0, height, 0, 3, (byte)Material.GRASS.getId());
	}
}
