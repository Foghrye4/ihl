package ihl.worldgen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenMinableMeta extends WorldGenerator {

	protected final Block mineableBlock;
	protected final Set<Block> replaceableBlocks=new HashSet();
	protected final Block liquidBedBlock;
	protected final int mineableBlockMeta;
	protected final int numberOfBlocks;
	protected int blockCounter;
	protected final int veinMaxHeightLimit;
	protected final int veinMinHeightLimit;
	
	public boolean maxBlockReached=false;
	public int lastX=-1;
	public int lastY=64;
	public int lastZ=-1;
	protected ForgeDirection veinDirection =  ForgeDirection.SOUTH;//EAST
	protected final Map<Long, Set<int[]>> oreCoordinatesToChunkCoordinates;
	protected final Map<Long,WorldGenMinableMeta> blockToOreCoordinates;
	protected final Set<Long> precalculatedChunkChache;
	
	public WorldGenMinableMeta(Set<Long> precalculatedChunkChache1, Map<Long,WorldGenMinableMeta> blockToOreCoordinates1, Map<Long, Set<int[]>> oreCoordinatesToChunkCoordinates2, Block block, int meta, int numberOfBlocks1, Block... blocks) {
		this(precalculatedChunkChache1 ,blockToOreCoordinates1, oreCoordinatesToChunkCoordinates2, block, Blocks.clay, meta, numberOfBlocks1, 0, 128, blocks);
	}

	public WorldGenMinableMeta(Set<Long> precalculatedChunkChache1, Map<Long,WorldGenMinableMeta> blockToOreCoordinates1, Map<Long,Set<int[]>> oreCoordinatesToChunkCoordinates1, Block block, Block liquidBedBlock1, int meta, int numberOfBlocks1, int veinMinHeightLimit1, int veinMaxHeightLimit1, Block... replacedBlock1) 
	{
		precalculatedChunkChache=precalculatedChunkChache1;
		blockToOreCoordinates=blockToOreCoordinates1;
		oreCoordinatesToChunkCoordinates=oreCoordinatesToChunkCoordinates1;
		mineableBlock = block;
		replaceableBlocks.addAll(Arrays.asList(replacedBlock1));
		mineableBlockMeta = meta;
		liquidBedBlock=liquidBedBlock1;
		numberOfBlocks = numberOfBlocks1;
		veinMaxHeightLimit=veinMaxHeightLimit1;
		veinMinHeightLimit=veinMinHeightLimit1;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) 
	{
		boolean success=false;
		int chunkX = x>>4;
		int chunkZ = z>>4;
    	int xyz[] = {0,0,1,0,0,-1,0,0};
    	for(int i=0;i<=5;i++)
		{
    		int x0 = x+xyz[i];
    		int y0 = y+xyz[i+1];
    		int z0 = z+xyz[i+2];
    		if(x0>>4==chunkX && z0>>4==chunkZ)
    		{
    			if(replaceableBlocks.contains(world.getBlock(x0, y0, z0)))
    			{
    				world.setBlock(x0, y0, z0, mineableBlock, mineableBlockMeta, 2);
    				success=true;
    			}
    		}
		}
		return false;
	}
	
	public boolean generateNewVein(Random random, int chunkX1, int chunkZ1)
	{
		Vec3 veinVector = Vec3.createVectorHelper(random.nextFloat()*2-1f, random.nextFloat()-0.5f, random.nextFloat()*2-1f);
		veinVector.normalize();
		int chunkX = chunkX1;
		int chunkZ = chunkZ1;
		int x0=chunkX << 4;
		int z0=chunkZ << 4;
		int y0=random.nextInt(veinMaxHeightLimit);
		//System.out.println("generated new vein with height="+y0);
		float fx=x0;
		float fy=y0;
		float fz=z0;
		long chunkKey = (chunkX<<16)+chunkZ;
		for(int i=0;i<this.numberOfBlocks;i++)
		{
			int[] oreCoordinatesArray = new int[] {x0,y0,z0};
			long oreCoordinatesKey = (x0<<30)+(y0<<15)+z0;
			{
				chunkX=x0>>4;
				chunkZ=z0>>4;
				chunkKey = (chunkX<<16)+chunkZ;
				updateOreCoordinatesToChunkCoordinates(chunkKey, oreCoordinatesArray);
				this.blockToOreCoordinates.put(oreCoordinatesKey, this);
				this.precalculatedChunkChache.add(chunkKey);
			}
			fx+=veinVector.xCoord+random.nextFloat()*0.25f;
			fy+=veinVector.yCoord+random.nextFloat()*0.25f;
			fz+=veinVector.zCoord+random.nextFloat()*0.25f;
			x0=Math.round(fx);
			y0=Math.round(fy);
			z0=Math.round(fz);
		}
		return true;
	}
	
	protected void updateOreCoordinatesToChunkCoordinates(long chunkKey, int[] oreCoordinates)
	{
		if(this.oreCoordinatesToChunkCoordinates.containsKey(chunkKey))
		{
			this.oreCoordinatesToChunkCoordinates.get(chunkKey).add(oreCoordinates);
		}
		else
		{
			Set<int[]> oreCoordinatesS = new HashSet();
			oreCoordinatesS.add(oreCoordinates);
			this.oreCoordinatesToChunkCoordinates.put(chunkKey, oreCoordinatesS);
		}
	}

}
