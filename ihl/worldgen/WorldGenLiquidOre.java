package ihl.worldgen;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenLiquidOre extends WorldGenMinableMeta {

	public WorldGenLiquidOre(Set<Long> precalculatedChunkChache1,Map<Long, WorldGenMinableMeta> blockToOreCoordinates, Map<Long, Set<int[]>> oreCoordinatesToChunkCoordinates, Block block, Block liquidBedBlock1, int meta, int numberOfBlocks1, int veinMinHeightLimit1, int veinMaxHeightLimit1, Block... replacedBlock1) 
	{
		super(precalculatedChunkChache1,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,block, liquidBedBlock1, meta, numberOfBlocks1, veinMinHeightLimit1, veinMaxHeightLimit1, replacedBlock1);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) 
	{
		if(replaceableBlocks.contains(world.getBlock(x, y, z)))
		{
			if(y<44 || world.getBlock(x, y+1, z).isAir(world, x, y+1, z) || world.getBlock(x, y+1, z).equals(Blocks.air))
			{
				world.setBlock(x, y, z, mineableBlock, mineableBlockMeta, 2);
		    	int x1,y1,z1;
		    	int xyz[] = {0,0,1,0,0,-1,0,0};
		    	Block block;
		    	for(int i=0;i<=5;i++)
				{
		    		x1=x+xyz[i];
		    		y1=y+xyz[i+1];
		    		z1=z+xyz[i+2];
					block = world.getBlock(x1,y1,z1);
					if(block!=mineableBlock)
					{
						if(block.getMaterial()==Material.lava)
						{
							world.setBlock(x1, y1, z1, Blocks.obsidian, 0, 2);
						}
						else if(!block.isAir(world, x1, y1, z1) || xyz[i+1]<=0)
						{
							world.setBlock(x1, y1, z1, liquidBedBlock, 0, 2);
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean generateNewVein(Random random, int chunkX1, int chunkZ1)
	{
		int chunkX = chunkX1;
		int chunkZ = chunkZ1;
		int x0=chunkX << 4;
		int z0=chunkZ << 4;
		long chunkKey = (chunkX<<16)+chunkZ;
		for(int ix=x0-this.numberOfBlocks/2;ix<x0+this.numberOfBlocks/2;ix++)
		{
			for(int iz=z0-this.numberOfBlocks/2;iz<z0+this.numberOfBlocks/2;iz++)
			{
				for(int iy=this.veinMinHeightLimit;iy<=this.veinMaxHeightLimit;iy++)
				{
					if((ix-x0)*(ix-x0)+(iz-z0)*(iz-z0) < this.numberOfBlocks*this.numberOfBlocks/4)
					{
						int[] oreCoordinatesArray = new int[] {ix,iy,iz};
						long oreCoordinatesKey = (ix<<30)+(iy<<15)+iz;
						{
							chunkX=x0>>4;
							chunkZ=z0>>4;
							chunkKey = (chunkX<<16)+chunkZ;
							updateOreCoordinatesToChunkCoordinates(chunkKey, oreCoordinatesArray);
							this.blockToOreCoordinates.put(oreCoordinatesKey, this);
							this.precalculatedChunkChache.add(chunkKey);
						}
					}
				}
			}
		}
		return true;
	}
}
