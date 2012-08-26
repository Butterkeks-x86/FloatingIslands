package me.tobi.FloatingIslands;


import java.io.File;

import me.tobi.FloatingIslands.Listeners.PlayerJoinListener;
import me.tobi.FloatingIslands.Listeners.PlayerRespawnListener;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;


public class FloatingIslands extends JavaPlugin{
	
	public static final String VERSION="0.1";
	private int level0MaxGenHeight=32;
	private int level0MinGenHeight=6;
	private double level0GenProbability=0.1;
	private int level1MaxGenHeight=80;
	private int level1MinGenHeight=48;
	private double level1GenProbability=0.3;
	
	@Override
	public void onEnable(){
		/*first, parse island generation parameters from config*/
		level0MaxGenHeight=this.getConfig().getInt("level0-max-gen-height");
		level0MinGenHeight=this.getConfig().getInt("level0-min-gen-height");
		level0GenProbability=this.getConfig().getDouble("level0-gen-probability");
		getLogger().info("level0: maxGenHeight: "+level0MaxGenHeight+"; minGenHeight: "
				+level0MinGenHeight+"; islandGenProbability: "+level0GenProbability);
		level1MaxGenHeight=this.getConfig().getInt("level1-max-gen-height");
		level1MinGenHeight=this.getConfig().getInt("level1-min-gen-height");
		level1GenProbability=this.getConfig().getDouble("level1-gen-probability");
		getLogger().info("level1: maxGenHeight: "+level1MaxGenHeight+"; minGenHeight: "
				+level1MinGenHeight+"; islandGenProbability: "+level1GenProbability);
		/*create folder and config file if they do not exist*/
		if(!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}
		if(!new File(this.getConfig().getCurrentPath()).exists()){
			this.saveDefaultConfig();
		}
		/*on first join teleport the player to accurate spawn position*/
		getServer().getPluginManager().registerEvents(
				new PlayerJoinListener(level1MaxGenHeight, level1MinGenHeight,
						this.getDataFolder()), this);
		/*on respawn, teleport the player to accurate spawn position*/
		getServer().getPluginManager().registerEvents(
				new PlayerRespawnListener(), this);
		getLogger().info("FloatingIslands version "+VERSION+" enabled.");
	}
	
	@Override
	public void onDisable(){
		getLogger().info("FloatingIslands disabled.");
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
		return new FloatingIslandsChunkGenerator(
				level0MaxGenHeight, level0MinGenHeight, level0GenProbability,
				level1MaxGenHeight, level1MinGenHeight, level1GenProbability);
	}
}
