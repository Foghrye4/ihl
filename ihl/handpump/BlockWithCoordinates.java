package ihl.handpump;

import ihl.IHLMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockWithCoordinates {
public Block block;
public int x;
public int y;
public int z;
public int meta;

public BlockWithCoordinates(Block block1, int x1, int y1, int z1, int meta1)
{
	block=block1;
	x=x1;
	y=y1;
	z=z1;
	meta=meta1;
}

public boolean setMetadataOrDestroyBlock(World world, int meta, int maxMeta)
{
	if(meta>maxMeta)
	{
		return world.setBlockToAir(x, y, z);
	}
	else if(isWaterBlock() && IHLMod.cccFiniteWater)
	{
		if(world.setBlock(x,y,z,Blocks.flowing_water,meta,3))
		{
			world.scheduleBlockUpdate(x,y,z,Blocks.flowing_water,2);
			return true;
		}
		return false;
	}
	else if(isWaterBlock())
	{
		if(world.setBlock(x,y,z,IHLMod.flowing_water,meta,3))
		{
			world.scheduleBlockUpdate(x,y,z,IHLMod.flowing_water,2);
			return true;
		}
		return false;
	}
	else if(isLavaBlock() && world.provider.isHellWorld && !IHLMod.cccFiniteWater)
	{
		if(world.setBlock(x,y,z,IHLMod.flowing_lava,meta,3))
		{
			world.scheduleBlockUpdate(x,y,z,IHLMod.flowing_lava,2);
			return true;
		}
		return false;
	}
	else if(isLavaBlock())
	{
		if(world.setBlock(x,y,z,Blocks.flowing_lava,meta,3))
		{
			world.scheduleBlockUpdate(x,y,z,Blocks.flowing_lava,2);
			return true;
		}
		return false;
	}
	return world.setBlockMetadataWithNotify(x, y, z, meta, 3);
}

public boolean isSameTypeBlock(World world, int x2, int y2, int z2) 
{
	Block block2 = world.getBlock(x2, y2, z2);
	if(isWaterBlock())
	{
		return block2==Blocks.water||block2==Blocks.flowing_water||block2==IHLMod.flowing_water;
	}
	else if(isLavaBlock())
	{
		return block2==Blocks.lava||block2==Blocks.flowing_lava||block2==IHLMod.flowing_lava;
	}
	else
	{
		return block2==block;
	}
}

public boolean isWaterBlock() 
{
	if(block==Blocks.water||block==Blocks.flowing_water||block==IHLMod.flowing_water)
	{
		return true;
	}
	return false;
}

public boolean isLavaBlock() 
{
	if(block==Blocks.lava||block==Blocks.flowing_lava||block==IHLMod.flowing_lava)
	{
		return true;
	}
	return false;
}

@Override
public boolean equals(Object other)
{
	if(other instanceof BlockWithCoordinates)
	{
		BlockWithCoordinates bwc = (BlockWithCoordinates)other;
		return bwc.block==this.block && bwc.x==this.x && bwc.y==this.y && bwc.z==this.z;
	}
	return false;
}

}
