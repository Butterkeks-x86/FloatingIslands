/*******************************************************************************
 * Copyright (c) 2012 Butterkeks-x86.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.github.butterkeks_x86.floatingIslands;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;


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
	public boolean generateStructures=true;
	public ItemStack []startItems=new ItemStack[5];
	
	/**
	 * Constructor.
	 * @param config The FileConfiguration object of the parent plugin
	 */
	public FloatingIslandsConfig(FileConfiguration config){
		this.fileConfig=config;
		//standard start items: ice block, lava bucket, melon seed, bone
		startItems[0]=new ItemStack(Material.ICE, 1);
		startItems[1]=new ItemStack(Material.LAVA_BUCKET, 1);
		startItems[2]=new ItemStack(Material.MELON_SEEDS, 1);
		startItems[3]=new ItemStack(Material.BONE, 1);
	}
	
	/**
	 * Parses the values from FileConfiguration object to variables.
	 * If a option does not exists, it will be used a own default value
	 * instead of bukkit default values (no zeroes etc.)
	 */
	public void parse(){
		int intTmp=0;
		double doubleTmp=0;
		ItemStack itemTmp=null;
		
		//level0 generation params
		intTmp=fileConfig.getInt("level0-max-gen-height");
		if(intTmp!=0){
			level0MaxGenHeight=intTmp;
		}
		intTmp=fileConfig.getInt("level0-min-gen-height");
		if(intTmp!=0){
			level0MinGenHeight=intTmp;
		}
		doubleTmp=fileConfig.getDouble("level0-gen-probability");
		if(doubleTmp!=0){
			level0GenProbability=doubleTmp;
		}
		//level1 generation params
		intTmp=fileConfig.getInt("level1-max-gen-height");
		if(intTmp!=0){
			level1MaxGenHeight=intTmp;
		}
		intTmp=fileConfig.getInt("level1-min-gen-height");
		if(intTmp!=0){
			level1MinGenHeight=intTmp;
		}
		doubleTmp=fileConfig.getDouble("level1-gen-probability");
		if(doubleTmp!=0){
			level1GenProbability=doubleTmp;
		}
		//generate structures
		generateStructures=fileConfig.getBoolean("generate-structures");
		//start items
		itemTmp=fileConfig.getItemStack("startItems.one");
		if(itemTmp!=null){
			startItems[0]=itemTmp;
		}
		itemTmp=fileConfig.getItemStack("startItems.two");
		if(itemTmp!=null){
			startItems[1]=itemTmp;
		}
		itemTmp=fileConfig.getItemStack("startItems.three");
		if(itemTmp!=null){
			startItems[2]=itemTmp;
		}
		itemTmp=fileConfig.getItemStack("startItems.four");
		if(itemTmp!=null){
			startItems[3]=itemTmp;
		}
		itemTmp=fileConfig.getItemStack("startItems.five");
		if(itemTmp!=null){
			startItems[4]=itemTmp;
		}
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
			  +" islandGenProbability: "+level1GenProbability
			  +" generateStructures: "+generateStructures;
	}
}
