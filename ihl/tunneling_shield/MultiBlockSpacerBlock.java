package ihl.tunneling_shield;

import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.PositionSpec;
import ic2.core.item.tool.ItemToolWrench;
import ihl.utils.IHLUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class MultiBlockSpacerBlock extends Block {
	
	public static Set<IMultiBlock> teList = new HashSet<IMultiBlock>();
	
	public MultiBlockSpacerBlock() 
	{
		super(Material.iron);
	}

	/**
	* The type of render function that is called for this block
	*/
	@Override
	public int getRenderType()
	{
	return -2;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world,int x, int y, int z, int meta, int flag)
	{
		return new ArrayList<ItemStack>();
	}
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
	{
		super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
		this.checkIfMachinePart(world, x, y, z);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
		this.checkIfMachinePart(world, x, y, z);
	}
	
	private boolean checkIfMachinePart(World world, int x, int y, int z)
	{
		if(!teList.isEmpty())
		{
			Iterator<IMultiBlock> i = MultiBlockSpacerBlock.teList.iterator();
			while(i.hasNext())
			{
				IMultiBlock te = i.next();
				if(te!=null && !te.getIsInvalid())
				{
					if(te.isAPartOfStructure(x, y, z, true))
					{
						return true;
					}
				}
				else
				{
					teList.remove(i);
				}
			}
		}
		return false;
	}
	
	private IMultiBlock getLinkedTileEntity(World world, int x, int y, int z)
	{
		if(!teList.isEmpty())
		{
			Iterator<IMultiBlock> i = MultiBlockSpacerBlock.teList.iterator();
			while(i.hasNext())
			{
				IMultiBlock te = i.next();
				if(te!=null && !te.getIsInvalid())
				{
					if(te.isAPartOfStructure(x, y, z, false))
					{
						return te;
					}
				}
				else
				{
					teList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	* Is this block (a) opaque and (B) a full 1m cube? This determines whether or not to render the shared face of two
	* adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	*/
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	* If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	*/
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y,int z)
	{	
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}
	
	@Override
    public void onNeighborBlockChange(World world, int x, int y,int z, Block block)
    {
		this.updateTick(world, x, y, z, world.rand);
	}
	
@Override
public void updateTick(World world, int x, int y,int z, Random rand)
{
	world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	if(!world.isRemote)
	{
		boolean setToAir=true;
		if(teList.size()>0)
		{
			if(!teList.isEmpty())
			{
				Iterator<IMultiBlock> i = MultiBlockSpacerBlock.teList.iterator();
				while(i.hasNext())
				{
					IMultiBlock te = i.next();
					if(te!=null && !te.getIsInvalid())
					{
						if(te.isAPartOfStructure(x, y, z, false))
						{
							setToAir=false;
						}
					}
					else
					{
						teList.remove(i);
					}
				}
			}
		}
		if(setToAir)
		{
			world.setBlockToAir(x, y, z);
		}
	}
}
	
	@Override
	public int tickRate(World world)
	{
		return 200;
	}
	
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z)
	{
    	IMultiBlock te = this.getLinkedTileEntity(world, x, y, z);
		ItemStack currentIS = entityPlayer.inventory.getCurrentItem();
		if(currentIS!=null)
		{
			if(currentIS.getItem() instanceof ItemToolWrench)
			{
				if(((ItemToolWrench)currentIS.getItem()).canTakeDamage(currentIS, 10))
				{
                    if (IC2.platform.isRendering())
                    {
                        IC2.audioManager.playOnce(entityPlayer, PositionSpec.Hand, "Tools/wrench.ogg", true, 1.0F);
                    }
                    else if(IC2.platform.isSimulating())
                    {
                    	if(te instanceof DriverTileEntity)
                    	{
                    		DriverTileEntity dte =(DriverTileEntity) te;
                    		if(dte.hasShield)
                    		{
                    			if(dte.advancedShield)
                    			{
                					world.spawnEntityInWorld(new EntityItem(world, x, y, z, IHLUtils.getThisModItemStack("advancedShieldAssemblyUnitBlock")));
                    			}
                    			else
                    			{
                					world.spawnEntityInWorld(new EntityItem(world, x, y, z, IHLUtils.getThisModItemStack("IHLShieldAssemblyUnitBlock")));
                    			}
                    		}
                    		dte.isAPartOfStructure(x, y, z, true);
        					world.setBlockToAir(x, y, z);
                    	}
    					((ItemToolWrench)currentIS.getItem()).damage(currentIS, 10, entityPlayer);
                    }
				}
				return true;
			}
		}
		return te instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(entityPlayer, (IHasGui)te) : true) : false;
    }
}