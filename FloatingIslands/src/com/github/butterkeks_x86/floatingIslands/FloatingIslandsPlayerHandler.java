/*******************************************************************************
 * Copyright (c) 2012 Butterkeks-x86.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.github.butterkeks_x86.floatingIslands;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This class provides a simple and efficient way to handle the different spawn locations
 * and inventories of a player. Both are properties of this class and can be set or
 * obtained via methods. Since this class is nearly full serializable (except the server),
 * it can easyly written to a file and read from it. But don't forget to set the servers
 * property after deserialization since it is transient!
 *
 */
public class FloatingIslandsPlayerHandler implements Serializable{
	
	public static final long serialVersionUID=1L;
	
	/*this property won't be serialized, so set it manually after deserialization*/
	private transient Server server;
	
	private String playerName; //the name of the correspondent player
	private SerializableLocation regularSpawn=null; //player's regualr spawn
	private SerializableInventory regularInventory; //player's regular inventory
	private SerializableLocation floatingIslandsSpawn=null; //players FloatingIslands spawn
	private SerializableInventory floatingIslandsInventory; //player's FloatingIslands inv
	private boolean joinedFloatingIslands=false; //true if the player joined realm once
	
	/**
	 * Constructor.
	 * @param server The correspondent Minecraft server object
	 * @param playerName The name of the player (via player.getName())
	 */
	public FloatingIslandsPlayerHandler(Server server, String playerName){
		this.playerName=playerName;
		this.server=server;
	}
	
	/**
	 * Player's name.
	 * @return The name of the correspndent player of this handler
	 */
	public String getPlayerName(){
		return playerName;
	}
	
	/**
	 * Sets the server. Don't forget to do this after every deserialization, since this
	 * property isn't serialized and will be needed by all getSpawn() methods!
	 * @param server The Minecraft server object
	 */
	public void setServer(Server server){
		this.server=server;
	}
	
	/**
	 * Query player already joined FloatingIslandsRealm.
	 * @return True if the player visited FloatingIslandsRealm once or more, false otherwise
	 */
	public boolean hasJoinedFloatingIslandsBefore(){
		return joinedFloatingIslands;
	}
	
	/**
	 * Sets the floatingIsplandsJoinedProperty. True means the player has at least once
	 * joined FloatingIslandsRealm; false means that the player has never joined.
	 * @param joined The new value of the property
	 */
	public void setFloatingIslandJoined(boolean joined){
		joinedFloatingIslands=joined;
	}
	
	/**
	 * Gets the regular spawn (in regular world).
	 * @return The regular spawn if regularSpawn and servers property set, null otherwise
	 */
	public Location getRegularSpawn(){
		if(regularSpawn!=null && server!=null){
			return regularSpawn.getLocation(server);
		}
		else return null;
	}
	
	/**
	 * Sets the regular spawn if given param is not null.
	 * @param spawn The new regular spawn to be set
	 */
	public void setRegularSpawn(Location spawn){
		if(spawn!=null){
			this.regularSpawn=new SerializableLocation(spawn);
		}
	}
	
	/**
	 * Fills the player's inventory with the items he obtained in the regular world. If there
	 * is no such inventory, nothing will be done.
	 * @param inv The palyer's inventory
	 */
	public void fillRegularInventory(Inventory inv){
		if(regularInventory!=null){
			regularInventory.fillInventory(inv);
		}
	}
	
	/**
	 * Sets the contents of the regular inventory.
	 * @param inv The player's current regular inventory
	 */
	public void setRegularInventory(Inventory inv){
		this.regularInventory=new SerializableInventory(inv);
	}
	
	/**
	 * Gets the FloatingIslands spawn.
	 * @return The FloatingIslands spawn if it and servers property set, null otherwise
	 */
	public Location getFloatingIslandsSpawn(){
		if(floatingIslandsSpawn!=null && server!=null){
			return floatingIslandsSpawn.getLocation(server);
		}
		else return null;
	}
	
	/**
	 * Sets the FloatingIslands spawn if given param is not null.
	 * @param spawn The new FloatingIslands spawn to be set
	 */
	public void setFloatingIslandsSpawn(Location spawn){
		if(spawn!=null){
			this.floatingIslandsSpawn=new SerializableLocation(spawn);
		}
	}
	
	/**
	 * Fills the player's inventory with the items he obtained in the FloatingIsland world.
	 * If there is no such inventory, nothing will be done.
	 * @param inv The palyer's inventory
	 */
	public void fillFloatingIslandsInventory(Inventory inv){
		if(floatingIslandsInventory!=null){
			floatingIslandsInventory.fillInventory(inv);
		}
	}
	
	/**
	 * Sets the contents of the FloatingIslands inventory.
	 * @param inv The player's current FloatingIslands inventory
	 */
	public void setFloatingIslandsInventory(Inventory inv){
		this.floatingIslandsInventory=new SerializableInventory(inv);
	}
	
	/**
	 * Private helper class which provides a serializable Location. When reassembling
	 * the original Location, the server's object is needed to get the according world.
	 *
	 */
	private class SerializableLocation implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		private UUID worldUID; //the UUID of the correspndent world
		private double x, y, z; //the values of each axis
		
		/**
		 * Constructor. Creates a new serializable Location object from a regular Location.
		 * @param loc The regular Location to create the serializable one from
		 */
		public SerializableLocation(Location loc){
			this.worldUID=loc.getWorld().getUID();
			this.x=loc.getX();
			this.y=loc.getY();
			this.z=loc.getZ();
		}
		
		/**
		 * Gets the correspondent Location from this serializable object. Note that the
		 * servers object is needed to perform this conversion. if not provided, the returned
		 * Location will be null.
		 * @param server The Minecraft server's object
		 * @return The correspondent location or null if an error occurred
		 */
		public Location getLocation(Server server){
			if(worldUID!=null){
				World world=server.getWorld(worldUID);
				if(world!=null){
					return new Location(world, x,y,z);
				}
				else return null;
			}
			else return null;
		}
	}
	
	/**
	 * This class provides a serializable inventory.
	 *
	 */
	private class SerializableInventory implements Serializable{

		private static final long serialVersionUID = 1L;
		private SerializableItemStack []serItemStacks;
		
		/**
		 * Constructs a serializable inventory from a regular player's inventory.
		 * Even empty slots will be considered.
		 * @param inv The current inventory of the player
		 */
		public SerializableInventory(Inventory inv){
			ItemStack []itemStacks=inv.getContents();
			serItemStacks=new SerializableItemStack[itemStacks.length];
			for(int i=0; i<itemStacks.length; i++){
				serItemStacks[i]=new SerializableItemStack(itemStacks[i]);
			}
		}
		
		/**
		 * Fills a player's inventory according to the item stacks listed in this
		 * serializable inventory. All other items will be replaced.
		 * @param inv The player's inventory to fill
		 */
		public void fillInventory(Inventory inv){
			if(serItemStacks!=null){
				ItemStack stacks[]=new ItemStack[serItemStacks.length];
				for(int i=0; i<stacks.length; i++){
					stacks[i]=serItemStacks[i].getItemStack();
				}
				inv.setContents(stacks);
			}
		}
		
		/**
		 * This class privides a serializable ItemStack.
		 *
		 */
		private class SerializableItemStack implements Serializable{
			
			public static final long serialVersionUID=1L;
			private int type;
			private int amount;
			private byte materialData;
			private short durability=0;
			//private Map<Enchantment, Integer> enchantments; TODO: enchantments
			
			/**
			 * Constructor from regular ItemStack.
			 * @param stack A regular ItemStack that will be converted into a serializable one
			 */
			public SerializableItemStack(ItemStack stack){
				if(stack!=null){
					this.type=stack.getType().getId();
					this.amount=stack.getAmount();
					this.materialData=stack.getData().getData();
					this.durability=stack.getDurability();
				}
				else type=-1;
			}
			
			/**
			 * Provides a equivalent ItemStack.
			 * @return A ItemStack that contains exactly the same items
			 */
			public ItemStack getItemStack(){
				if(type==-1){
					return null;
				}
				else return new ItemStack(type, amount, durability, materialData);
			}
		}
	}
}
