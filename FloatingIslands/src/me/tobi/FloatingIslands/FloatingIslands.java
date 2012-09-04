package me.tobi.FloatingIslands;


import java.io.File;

import me.tobi.FloatingIslands.Listeners.PlayerJoinListener;
import me.tobi.FloatingIslands.Listeners.PlayerRespawnListener;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;


public class FloatingIslands extends JavaPlugin{
	
	public static final String VERSION="0.1";
	private FloatingIslandsConfig config;
	
	@Override
	public void onEnable(){
		/*first, load and parse configuratiion parameters*/
		config=new FloatingIslandsConfig(this.getConfig());
		config.parse();
		this.getLogger().info(config.getConfigurationAsString());
		/*create folder and config file if they do not exist*/
		if(!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}
		if(!new File(this.getConfig().getCurrentPath()).exists()){
			this.saveDefaultConfig();
		}
		/*on first join teleport the player to accurate spawn position*/
		getServer().getPluginManager().registerEvents(
				new PlayerJoinListener(config, this.getDataFolder()), this);
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
		return new FloatingIslandsChunkGenerator(this.config);
	}
}
