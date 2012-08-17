package me.tobi.FloatingIslands;


import me.tobi.FloatingIslands.Listeners.PlayerJoinListener;
import me.tobi.FloatingIslands.Listeners.PlayerRespawnListener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;


public class FloatingIslands extends JavaPlugin{
	
	public static final String VERSION="0.1";
	public static FloatingIslandsSettings settings;
	
	@Override
	public void onEnable(){
		/*on first join teleport the player to accurate spawn position*/
		getServer().getPluginManager().registerEvents(
				new PlayerJoinListener(), this);
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
		return FloatingIslandsChunkGenerator.getInstance(this);
	}
}
