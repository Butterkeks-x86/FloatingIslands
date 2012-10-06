/*******************************************************************************
 * Copyright (c) 2012 Butterkeks-x86.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.github.butterkeks_x86.floatingIslands;


import java.io.File;
import java.util.List;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.butterkeks_x86.floatingIslands.listeners.PlayerRespawnListener;


public class FloatingIslands extends JavaPlugin{
	
	public static final String VERSION="1.2beta";
	private FloatingIslandsConfig config;
	private World floatingIslandsWorld;
	
	@Override
	public void onEnable(){
		
		/*ensure that the plugin data folder and the config file exist*/
		if(!this.getDataFolder().exists()){
			boolean created=this.getDataFolder().mkdir();
			if(!created) this.getLogger().warning("Unable to create plugin data folder!");
		}
		if(!new File(this.getConfig().getCurrentPath()).exists()){
			this.saveDefaultConfig();
		}
		
		/*load and parse configuratiion parameters*/
		config=new FloatingIslandsConfig(this.getConfig());
		config.parse();
		this.getLogger().info(config.getConfigurationAsString());
		
		/*generate/load the floatings islands world*/
		WorldCreator wc=new WorldCreator("FloatingIslandsWorld");
		wc.environment(World.Environment.NORMAL);
		wc.generateStructures(config.generateStructures);
		wc.generator(new FloatingIslandsChunkGenerator(config));
		List<World> worlds=this.getServer().getWorlds();
		if(!worlds.isEmpty() && worlds.get(0)!=null){
			wc.seed(worlds.get(0).getSeed());
		}
		wc.type(WorldType.NORMAL);
		floatingIslandsWorld=getServer().createWorld(wc);
		Util.ensureValidSpawn(floatingIslandsWorld, this.getDataFolder(), config);

		/*register event handlers*/
		getServer().getPluginManager().registerEvents(
				new PlayerRespawnListener(floatingIslandsWorld), this);
		
		getLogger().info("FloatingIslands version "+VERSION+" enabled.");
	}
	
	@Override
	public void onDisable(){
		getLogger().info("FloatingIslands disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args){
		//floating islands plugin commands
		if(cmd.getName().equalsIgnoreCase("floatingIslands")){
			//check for correct number of arguments
			if(args.length==0){
				sender.sendMessage("Too few arguments.");
				return false; //will display usage from plugin.yml
			}
			else if(args.length>1){
				sender.sendMessage("Too many arguments.");
				return false; //will display usage from plugin.yml
			}
			//check for help message
			else if(args[0].equalsIgnoreCase("help")){
				sender.sendMessage("\nFloatingIslands version "+VERSION+"\n"+
						"/floatingIslands help - displays short info of this command\n"+
						"/floatingIslands join - join the FloatingIslands realm\n"+
						"/floatingIslands leave - leave the FloatingIslands realm");
				return true;
			}
			//check for join option
			else if(args[0].equalsIgnoreCase("join")){
				if(sender instanceof Player){
					Player player=(Player)sender;
					if(player.hasPermission("floatingIslands.join")){
						if(player.getWorld()!=floatingIslandsWorld){
							PlayerHandler ph=Util.deserializePlayerHandler(
									this.getDataFolder(), player.getName());
							if(ph==null){
								ph=new PlayerHandler(this.getServer(),
										player.getName());
							}
							else ph.setServer(this.getServer());
							ph.setRegularSpawn(player.getLocation());
							ph.setRegularInventory(player.getInventory());
							Location fiSpawn=ph.getFloatingIslandsSpawn();
							if(!ph.hasJoinedFloatingIslandsBefore()){
								player.getInventory().setContents(config.getStartItems());
								ph.setFloatingIslandJoined(true);
							}
							else{
								ph.fillFloatingIslandsInventory(player.getInventory());
							}
							Util.serializePlayerHandler(ph, this.getDataFolder());
							if(fiSpawn!=null){
								player.teleport(fiSpawn);
							}
							else{
								player.teleport(floatingIslandsWorld.getSpawnLocation());
							}
						}
						else{
							player.sendMessage("You are already in the " +
									"FloatingIslands realm.");
						}
					}
					else{
						player.sendMessage("You are not allowed to join the" +
								" FloatingIslands realm!");
					}
				}
				else{
					sender.sendMessage("This command can only be executed ingame.");
				}
				return true;
			}
			//check for leave option
			else if(args[0].equalsIgnoreCase("leave")){
				if(sender instanceof Player){
					Player player=(Player)sender;
					if(player.hasPermission("floatingIslands.leave")){
						if(player.getWorld()==floatingIslandsWorld){
							List<World> worlds=this.getServer().getWorlds();
							if(worlds.size()>0){
								PlayerHandler ph=Util.deserializePlayerHandler(
										this.getDataFolder(), player.getName());
								if(ph==null){
									ph=new PlayerHandler(this.getServer(),
											player.getName());
								}
								else ph.setServer(this.getServer());
								ph.setFloatingIslandsSpawn(player.getLocation());
								ph.setFloatingIslandsInventory(player.getInventory());
								Location regularSpawn=ph.getRegularSpawn();
								ph.fillRegularInventory(player.getInventory());
								Util.serializePlayerHandler(ph, this.getDataFolder());
								if(regularSpawn!=null){
									player.teleport(regularSpawn);
								}
								else{
									player.teleport(worlds.get(0).getSpawnLocation());
								}
							}
							else{
								player.sendMessage("Unable to find regular world."
										+"Abort.");
							}
						}
						else{
							player.sendMessage("You are not in the " +
									"FloatingIslands realm, so you can't leave.");
						}
					}
					else{
						player.sendMessage("You are not allowed to leave the"+
								" FloatingIslands realm!");
					}
				}
				else{
					sender.sendMessage("This command can only be executed ingame.");
				}
				return true;
			}
			//invalid option
			else{
				sender.sendMessage("Unknown option \""+args[0]+"\".");
				return false; //will display usage from plugin.yml
			}
		}
		//unknwon command, not interpreted by this plugin
		else return false;
	}
}
