package me.tobi.FloatingIslands;

import java.util.Random;

public class StructureGenerator {
	
	private byte[][] chunk;
	private Random random;
	
	public StructureGenerator(byte[][] chunk, Random random){
		this.chunk=chunk;
		this.random=random;
	}
	
	/**
	 * Generates a Layer of some material
	 * @param xs
	 * @param ys Height of the Layer
	 * @param zs
	 * @param size The size of the layer -> size x size blocks
	 * @param matId The material of the blocks
	 * @return true if generation was successful, else false
	 */
	public boolean generateLayer(int xs, int ys, int zs, int size, byte matId){
		assert xs>=0;
		assert zs>=0;
		assert xs+size<=16;
		assert zs+size<=16;
		for(int x=xs; x<xs+size; x++){
			for(int z=zs; z<zs+size; z++){
				setBlock(x, ys, z, matId);
			}
		}
		return true;
	}
	
	public static boolean generateLayerRandomReplace(int xs, int ys, int zs, 
			int size, byte matId, byte repId){
		return true;
	}
	
	/**
	 * Generates a set of Layers
	 * @param xs
	 * @param ys Height of the highest layer
	 * @param zs
	 * @param size Size of one layer
	 * @param layerCount Hight of all layers, i.e. layer count
	 * @param matId Material id of the blocks
	 * @return True if generation was successfull
	 */
	public boolean generateLayers(int xs, int ys, int zs, int size, int layerCount,
			byte matId){
		assert ys-layerCount+1>=0;
		assert ys<256;
		for(int y=ys-layerCount+1; y<=ys; y++){
			generateLayer(xs, y, zs, size, matId);
		}
		return true;
	}
	
	public boolean generateStem(int xs, int ys, int zs, int length, byte matId){
		assert ys>=0;
		assert ys+length<=256;
		for(int y=ys; y<ys+length; y++){
			setBlock(xs, y, zs, matId);
		}
		return true;
	}
	
	/**
	 * Generates an ordinary tree, six blocks high
	 * @param xs
	 * @param ys Height of the first log block
	 * @param zs
	 * @param logId Material id of the logs
	 * @param leaveId Material id of the leaves
	 * @return True if generation was successful
	 */
	public boolean generateNormalTree(int xs, int ys, int zs, byte logId, 
			byte leaveId){
		assert ys>=0;
		assert ys+6<=256;
		boolean ret=generateLayer(xs-2, ys+3, zs-2, 5, leaveId); //first leave layer
		for(int x=xs-2; x<xs+3; x++){ //second leave layer
			for(int z=zs-2; z<zs+3; z++){
				if((z==zs-2 || z==xs+2) && (x==zs-2 || x==zs+2)) continue;
				setBlock(x, ys+4, z, leaveId);
			}
		}
		setBlock(xs+1, ys+5, zs, leaveId); //top leaves
		setBlock(xs, ys+5, zs+1, leaveId);
		setBlock(xs, ys+5, zs-1, leaveId);
		setBlock(xs, ys+5, zs, leaveId);
		setBlock(xs-1, ys+5, zs, leaveId);
		generateStem(xs, ys, zs, 5, logId);
		return ret;
	}
	
	public void setBlock(int x, int y, int z, byte blkid) {
	    if (chunk[y >> 4] == null) //is this chunkpart already initialised?
	    {
	        chunk[y >> 4] = new byte[4096]; //initialise the chunk part
	    }
	    chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid; //set the block (look above, how this is done)
	}
}
