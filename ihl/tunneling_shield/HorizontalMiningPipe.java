package ihl.tunneling_shield;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.item.tool.ItemToolWrench;
import ihl.IHLMod;
import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HorizontalMiningPipe extends Block {

	public static ArrayList<DriverTileEntity> teList = new ArrayList<DriverTileEntity>();
	
	public HorizontalMiningPipe() {
		super(Material.iron);
		this.setBlockBounds(0.1875F, 0.1875F, 0.1875F, 0.8125F, 0.8125F, 0.8125F);
	}
	
	public HorizontalMiningPipe(int meta) {
		super(Material.iron);
		this.setBlockBounds(0.1875F*meta, 0.1875F, 0.1875F*(1-meta), 0.8125F+0.1875F*(1-meta), 0.8125F, 0.8125F+0.1875F*meta);
	}
	
	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}
	
	@Override
	public Item getItem(World world, int x, int y, int z)
	{
		return Ic2Items.miningPipe.getItem();
	}
	
	@Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Ic2Items.miningPipe.getItem();
    }
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
		ItemStack result = Ic2Items.miningPipe;
		result.stackSize=1;
        this.dropBlockAsItem(world, x, y, z, result);
	}
	
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("ic2:machine/blockMiningPipe");
    }
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return this.blockIcon;
	}
	
	/**
	* The type of render function that is called for this block
	*/
	@Override
	public int getRenderType()
	{
	return -2;
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
	
	private void checkIfMachinePart(World world, int x, int y, int z)
	{
		if(teList.size()>0)
		{
			for (int i=0;i<teList.size();i++)
			{
				if(teList.get(i)!=null && !teList.get(i).isInvalid())
				{
					if(teList.get(i).isShaftCoord(x, y, z))
					{
						return;
					}
				}
				else
				{
					teList.remove(i);
					i--;
				}
			}
		}
	}
	
	private void checkIfMachinePartNN(World world, int x, int y, int z)
	{
		boolean setToAir=true;
		if(teList.size()>0)
		{
			for (int i=0;i<teList.size();i++)
			{
				if(teList.get(i)!=null)
				{
					if(teList.get(i).isShaftCoordNN(x, y, z) && teList.get(i).hasShield)
					{
							setToAir=false;
					}
				}
				else
				{
					teList.remove(i);
					i--;
				}
			}
		}
		if(setToAir)
		{
			world.setBlockToAir(x, y, z);
		}
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
		this.checkIfMachinePartNN(world, x, y, z);

	}
	
	@Override
	public int tickRate(World world)
	{
		return 200;
	}
	
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z)
	{
		ItemStack currentIS = entityPlayer.inventory.getCurrentItem();
		if(currentIS!=null)
		{
			if(currentIS.getItem() instanceof ItemToolWrench && entityPlayer.inventory.hasItem(Ic2Items.miningPipe.getItem()))
			{
				if(((ItemToolWrench)currentIS.getItem()).canTakeDamage(currentIS, 10))
				{
                    if (IC2.platform.isRendering())
                    {
                        IC2.audioManager.playOnce(entityPlayer, PositionSpec.Hand, "Tools/wrench.ogg", true, 1.0F);
                    }
                    if(IC2.platform.isSimulating())
                    {
            		if(teList.size()>0)
            		{
            			for (int i3=0;i3<teList.size();i3++)
            			{
            				DriverTileEntity t = teList.get(i3);
            				if(t!=null)
            				{
            					if(t.isShaftCoordNN(x, y, z) && t.hasShield)
            					{
        							int xA=t.xCoord+t.shaftDestroyedAtA*t.mX();
        							int zA=t.zCoord+t.shaftDestroyedAtA*t.mZ();
        							int xB=t.xCoord+t.shaftDestroyedAtB*t.mX();
        							int zB=t.zCoord+t.shaftDestroyedAtB*t.mZ();
            						if(t.shaftDestroyedAtA!=0 && t.shaftDestroyedAtA==t.shaftDestroyedAtB)
            						{
            							if(Math.abs(x-xA+z-zA)==1)
            							{
            								if(world.setBlock(xA, y, zA, t.mX()!=0?IHLMod.horizontalMiningPipeZ:IHLMod.horizontalMiningPipeX))
            								{
                								t.shaftDestroyedAtA=t.shaftDestroyedAtB=0;
                								entityPlayer.inventory.consumeInventoryItem(Ic2Items.miningPipe.getItem());
                								entityPlayer.inventoryContainer.detectAndSendChanges();
                								return true;
            								}
            							}
            						}
            						else if(t.shaftDestroyedAtA!=0)
            						{
            							if(Math.abs(x-xA+z-zA)==1)
            							{
            								if(world.setBlock(xA, y, zA, t.mX()!=0?IHLMod.horizontalMiningPipeZ:IHLMod.horizontalMiningPipeX))
            								{
                								t.shaftDestroyedAtA++;
                								entityPlayer.inventory.consumeInventoryItem(Ic2Items.miningPipe.getItem());
                								entityPlayer.inventoryContainer.detectAndSendChanges();
                								return true;
            								}
            							}
            							if(Math.abs(x-xB+z-zB)==1)
            							{
            								if(world.setBlock(xB, y, zB, t.mX()!=0?IHLMod.horizontalMiningPipeZ:IHLMod.horizontalMiningPipeX))
            								{
                								t.shaftDestroyedAtB--;
                								entityPlayer.inventory.consumeInventoryItem(Ic2Items.miningPipe.getItem());
                								entityPlayer.inventoryContainer.detectAndSendChanges();
                								return true;
            								}
            							}
            						}
            					}
            				}
            				else
            				{
            					teList.remove(i);
            					i--;
            				}
            			}
            		}
   					((ItemToolWrench)currentIS.getItem()).damage(currentIS, 10, entityPlayer);
                    }
				}
				return true;
			}
		}
		return false;
    }
}