package me.tobi.FloatingIslands;


import me.tobi.FloatingIslands.Listeners.PlayerJoinListener;
import me.tobi.FloatingIslands.Listeners.PlayerRespawnListener;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;


public class FloatingIslands extends JavaPlugin{
	
	public static final String VERSION="0.1";
	private int maxGenHeight=127;
	private int minGenHeight=0;
	private double islandGenProbability=0.3;
	
	@Override
	public void onEnable(){
		/*first, parse island generation parameters from config*/
		maxGenHeight=this.getConfig().getInt("max-gen-height");
		minGenHeight=this.getConfig().getInt("min-gen-height");
		islandGenProbability=this.getConfig().getDouble("island-gen-probability");
		getLogger().info("maxGenHeight: "+maxGenHeight+"; minGenHeight: "+minGenHeight
				+"; islandGenProbability: "+islandGenProbability);
		
		/*on first join teleport the player to accurate spawn position*/
		getServer().getPluginManager().registerEvents(
				new PlayerJoinListener(maxGenHeight, minGenHeight), this);
		/*on respawn, teleport the player to accurate spawn position*/
//		getServer().getPluginManager().registerEvents(
//				new PlayerRespawnListener(), this);
		getLogger().info("FloatingIslands version "+VERSION+" enabled.");
	}
	
	@Override
	public void onDisable(){
		getLogger().info("FloatingIslands disabled.");
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
		return new FloatingIslandsChunkGenerator(maxGenHeight,
				minGenHeight, islandGenProbability);
	}
}
