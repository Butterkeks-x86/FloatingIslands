package me.tobi.FloatingIslands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FloatingIslandsPlayerHandler {
	
	private Player player;
	private File dataFolder;
	private Location regularSpawn=null;
	private Location floatingIslandsSpawn=null;
	
	public FloatingIslandsPlayerHandler(Player player, File dataFolder){
		this.player=player;
		this.dataFolder=dataFolder;
	}
	
	public void toggleInventory(){
		//TODO:
	}
	
	public Location getRegularSpawn(){
		return regularSpawn;
	}
	
	public void setRegularSpawn(Location spawn){
		if(spawn!=null){
			this.regularSpawn=spawn;
		}
	}
	
	public Location getFloatingIslandsSpawn(){
		return floatingIslandsSpawn;
	}
	
	public void setFloatingIslandsSpawn(Location spawn){
		if(spawn!=null){
			this.floatingIslandsSpawn=spawn;
		}
	}
	
	/**
	 * Tries to read the file associated with the palyer and parse its contents
	 * to locations. The resulting locations might be null.
	 */
	public void readFromPlayerFile(){
		File playerData=new File(dataFolder.getAbsolutePath()+"/"+player.getName());
		BufferedReader in=null;
		try{
			in=new BufferedReader(new FileReader(playerData));
			//first line
			String line=in.readLine();
			Location loc=parseLocation(line);
			if(loc!=null){
				if(loc.getWorld().getName().
						equalsIgnoreCase("FloatingIslandsWorld")){
					floatingIslandsSpawn=loc;
				}
				else{
					regularSpawn=loc;
				}
			}
			//second line
			line=in.readLine();
			loc=parseLocation(line);
			if(loc!=null){
				if(loc.getWorld().getName().
						equalsIgnoreCase("FloatingIslandsWorld")){
					floatingIslandsSpawn=loc;
				}
				else{
					regularSpawn=loc;
				}
			}
		}catch(FileNotFoundException e){
			return;
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private Location parseLocation(String coords){
		if(coords!=null){
			String []tokens=coords.split(" ");
			if(tokens.length==4){
				World world=player.getServer().getWorld(tokens[0]);
				if(world==null){
					return null;
				}
				else{
					Double x, y, z;
					try{
						x=Double.parseDouble(tokens[1]);
						y=Double.parseDouble(tokens[2]);
						z=Double.parseDouble(tokens[3]);
					}catch(NumberFormatException e){
						e.printStackTrace();
						return null;
					}
					return new Location(world, x,y,z);
				}
			}
			else return null;
		}
		else return null;
	}
	
	public void saveToPlayerFile(){
		File playerFile=new File(dataFolder.getAbsolutePath()+"/"+player.getName());
		BufferedWriter out=null;
		try{
			out=new BufferedWriter(new FileWriter(playerFile));
			out.write(""); //clear file
			if(floatingIslandsSpawn!=null){
				out.write(floatingIslandsSpawn.getWorld().getName()
						+" "+floatingIslandsSpawn.getX()
						+" "+floatingIslandsSpawn.getY()
						+" "+floatingIslandsSpawn.getZ()
						+"\n"
				);
			}
			if(regularSpawn!=null){
				out.write(regularSpawn.getWorld().getName()
						+" "+regularSpawn.getX()
						+" "+regularSpawn.getY()
						+" "+regularSpawn.getZ()
						+"\n"
				);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(out!=null){
					out.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
