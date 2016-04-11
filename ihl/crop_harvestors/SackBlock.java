package ihl.crop_harvestors;

import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class SackBlock extends Block implements ITileEntityProvider{
	
		public SackBlock(Material material) 
		{
			super(material);
	        this.setCreativeTab(IHLCreativeTab.tab);
		}
		
		@Override
		public TileEntity createNewTileEntity(World world, int var2) {
			return new SackTileEntity();
		}
		

		@Override
		@SideOnly(Side.CLIENT)
		public void registerBlockIcons(IIconRegister par1IconRegister)
		{
	   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":sackItem");
		}
		
		@Override
	    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
	    {
	        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.1F, 0.8F);
	        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.21F, 1.0F, 0.8F);
	        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 1.0F, 0.21F);
	        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	        this.setBlockBounds(0.79F, 0.0F, 0.0F, 0.8F, 1.0F, 0.8F);
	        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	        this.setBlockBounds(0.0F, 0.0F, 0.79F, 0.8F, 1.0F, 0.8F);
	        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	        this.setBlockBoundsForItemRender();
	    }
		
	    @Override
		public void setBlockBoundsForItemRender()
	    {
	        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	    }
		
		@Override
		public boolean hasTileEntity(int metadata)
		{
		    return true;
		}
		
		@Override
		public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z){
			TileEntity te = world.getTileEntity(x,y,z);
			if(!world.isRemote && te instanceof SackTileEntity)
	        	{
				SackTileEntity ste = (SackTileEntity)te;
					if (ste == null || entityPlayer.isSneaking()) {
	        			return false;
	        		}
	        		else
	        		{
	        			if(ste.fluidTank.getFluid()!=null)
	        			{
	        				if(entityPlayer.inventory.getCurrentItem()!=null)
	            			{
	        					if(entityPlayer.inventory.getCurrentItem().getItem() instanceof IFluidContainerItem)
	        					{
	        						return false;
	        					}
	        					FluidStack drainFS = ste.drain(ForgeDirection.UNKNOWN, ste.fluidTank.getCapacity(), false);
	            				ItemStack stackToAdd = FluidContainerRegistry.fillFluidContainer(drainFS, entityPlayer.inventory.getCurrentItem());
	            				if(stackToAdd!=null)
	            				{
	            					if (entityPlayer.inventory.addItemStackToInventory(stackToAdd))
	    	    				    {
	    	    				    	entityPlayer.inventory.getCurrentItem().stackSize--;
	    	    				    	entityPlayer.inventoryContainer.detectAndSendChanges();
	    	    				    	ste.drain(ForgeDirection.UNKNOWN, FluidContainerRegistry.getContainerCapacity(stackToAdd),true);
	    	    			        }
	            				}
	            			}
	        			}
	        		}
	        	}
			return false;
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
	    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
	    {
			int var7 = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
	        TileEntity t = world.getTileEntity(x, y, z);
	        if(t!=null && t instanceof SackTileEntity)
	        {
	        	SackTileEntity te = (SackTileEntity)t;
		        		switch(var7)
		        		{
		        		case 0:
			        		te.setFacing((short) 3);
			        		break;
		        		case 1:
			        		te.setFacing((short) 4);
			        		break;
		        		case 2:
			        		te.setFacing((short) 2);
			        		break;
		        		case 3:
			        		te.setFacing((short) 5);
			        		break;
		        		default:
			        		break;
		        		}

	        }
	    }
}
