package ihl.processing.metallurgy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import ihl.interfaces.IWorkspaceElement;
import ihl.utils.IHLUtils;

public class BigMachineFrameTileEntity extends TileEntity implements IWorkspaceElement {

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) 
	{
		return false;
	}

	@Override
	public short getFacing() 
	{
		return 0;
	}

	@Override
	public void setFacing(short facing) {}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) 
	{
		return true;
	}

	@Override
	public float getWrenchDropRate() 
	{
		return 1.0f;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) 
	{
		return IHLUtils.getThisModItemStack("frameSteel");
	}

	@Override
	public boolean canBeUsed() 
	{
		int ix,iy,iz;
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		int x0 = xCoord;
		int y0 = yCoord;
		int z0 = zCoord;
		for(ix=x;ix>x-3;ix--)
		{
			for(iy=y;iy>y-3;iy--)
			{
				for(iz=z;iz>z-3;iz--)
				{
					TileEntity te = worldObj.getTileEntity(ix, iy, iz);
					if(te instanceof BigMachineFrameTileEntity)
					{
						x0=ix+1;
						y0=iy+1;
						z0=iz+1;
					}
				}
			}
		}
		for(ix=x0-1;ix<x0+1;ix++)
		{
			for(iy=y0-1;iy<y0+1;iy++)
			{
				for(iz=z0-1;iz<z0+1;iz++)
				{
					if(ix!=x0 && iy!=y0 && iz!=z0)
					{
						TileEntity te = worldObj.getTileEntity(ix, iy, iz);
						if(!(te instanceof BigMachineFrameTileEntity))
						{
							return false;
						}
					}
					else
					{
						Block block = worldObj.getBlock(ix, iy, iz);
						if(block!=Blocks.air && !block.isAir(worldObj, ix, iy, iz))
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public void use() {}

	@Override
	public boolean getIsInvalid() 
	{
		return this.isInvalid();
	}

}
