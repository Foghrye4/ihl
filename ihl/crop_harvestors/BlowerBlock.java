package ihl.crop_harvestors;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IC2Items;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.utils.IHLUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlowerBlock extends Block implements ITileEntityProvider{
	
		IIcon textureLeft, textureRight, textureBack;
	
		public BlowerBlock(Material material) 
		{
			super(material);
	        this.setCreativeTab(IHLCreativeTab.tab);
		}

		@Override
	    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	    {
	        return IC2Items.getItem("machine").getItem();
	    }
		
		@Override
		public void	onNeighborBlockChange(World world, int x, int y, int z, Block block)
		{
			TileEntity te = world.getTileEntity(x,y,z);
			if(te instanceof BlowerTileEntity)
	        	{
					BlowerTileEntity bte = (BlowerTileEntity)te;
					bte.updateChecksum=-1;
	        	}
		}
		
		@Override
		public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
		{
		}
		
		@Override
		public TileEntity createNewTileEntity(World world, int var2) {
			return new BlowerTileEntity();
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public void registerBlockIcons(IIconRegister par1IconRegister)
		{
	   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blowerFront");
			this.textureBack = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blowerBack");
	   		this.textureLeft = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blowerLeft");
	   		this.textureRight = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blowerRight");
		}
	   	
		@Override
		public boolean hasTileEntity(int metadata)
		{
		    return true;
		}
		
		@Override
		public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z){
			TileEntity te = world.getTileEntity(x,y,z);
			if(te instanceof BlowerTileEntity)
	        	{
				BlowerTileEntity bte = (BlowerTileEntity)te;
					if (bte == null || entityPlayer.isSneaking()) {
	        			return false;
	        		}
	        		else
	        		{
	        			return bte.getGui(entityPlayer);
	        		}
	        	}
			 return false;
		}
		
	    /**
	     * Called when the block is placed in the world.
	     */
		@Override
	    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
	    {
	        TileEntity t = world.getTileEntity(x, y, z);
	        if(t!=null && t instanceof BlowerTileEntity)
	        {
	        		((BlowerTileEntity)t).setFacing(IHLUtils.getFacingFromPlayerView(player, false));
	        }
	    }
		
		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) 
		{
			int facing=3;
			int mask[] = {
					0,1,2,3,4,5,
					1,0,3,2,4,5,
					3,2,0,1,4,5,
					2,3,1,0,4,5,
					2,3,5,4,0,1,
					2,3,4,5,1,0
					};
			TileEntity te = world.getTileEntity(x, y, z);
			if(te!=null)
			{
				BlowerTileEntity tebh = (BlowerTileEntity) te;
				facing=tebh.getFacing();
			}

			switch (mask[facing*6+side])
			{
			case 0:
				return this.blockIcon;
			case 1:
				return this.textureBack;
			case 2:
				return this.textureBack;
			case 3:
				return this.textureBack;
			case 4:
				return this.textureLeft;
			case 5:
				return this.textureRight;
			default:
				return this.textureLeft;
			}
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon(int side, int meta) 
		{
			IIcon sideIcon = this.blockIcon;
			switch (side)
			{
			case 0:
				return this.blockIcon;
			case 1:
				return this.textureBack;
			case 2:
				return this.textureLeft;
			case 3:
				return this.textureRight;
			case 4:
				return this.textureBack;
			case 5:
				return this.textureBack;
			default:
				return this.textureLeft;
			}
		}
		
}
