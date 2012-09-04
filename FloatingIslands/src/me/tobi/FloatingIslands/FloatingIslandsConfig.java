package me.tobi.FloatingIslands;

import org.bukkit.configuration.file.FileConfiguration;


/**
 * Parses and holds the configuration of the floating islands plugin
 * to keep it simple.
 * All members are properly initialized, even if not found in the configuration,
 * and public accessible.
 * 
 */
public class FloatingIslandsConfig {
	
	private FileConfiguration fileConfig;
	
	public int level0MinGenHeight=0;
	public int level0MaxGenHeight=127;
	public double level0GenProbability=0.3;
	public int level1MinGenHeight=0;
	public int level1MaxGenHeight=127;
	public double level1GenProbability=0.1;
	
	/**
	 * Constructor.
	 * @param config The FileConfiguration object of the parent plugin
	 */
	public FloatingIslandsConfig(FileConfiguration config){
		this.fileConfig=config;
	}
	
	/**
	 * Parses the values from FileConfiguration object to variables.
	 */
	public void parse(){
		level0MaxGenHeight=fileConfig.getInt("level0-max-gen-height");
		level0MinGenHeight=fileConfig.getInt("level0-min-gen-height");
		level0GenProbability=fileConfig.getDouble("level0-gen-probability");
		level1MaxGenHeight=fileConfig.getInt("level1-max-gen-height");
		level1MinGenHeight=fileConfig.getInt("level1-min-gen-height");
		level1GenProbability=fileConfig.getDouble("level1-gen-probability");
	}
	
	/**
	 * Produces a String containing all relevant configuration parameters.
	 * @return A dump String with all stored parameters.
	 */
	public String getConfigurationAsString(){
		return "level0: maxGenHeight: "+level0MaxGenHeight
			  +" minGenHeight: "+level0MinGenHeight
			  +" islandGenProbability: "+level0GenProbability
			  +" level1: maxGenHeight: "+level1MaxGenHeight
			  +" minGenHeight: "+level1MinGenHeight
			  +" islandGenProbability: "+level1GenProbability;
	}
}
