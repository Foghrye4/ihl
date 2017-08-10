package ihl.trans_dimensional_item_teleporter;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IC2Items;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TDITBlock extends Block implements ITileEntityProvider{
	
		IIcon textureLeft, textureRight, textureBack, textureBottom;
	
		public TDITBlock(Material material) 
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
		public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
		{
		}
		
		@Override
		public TileEntity createNewTileEntity(World world, int var2) {
			return new TDITTileEntity();
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public void registerBlockIcons(IIconRegister par1IconRegister)
		{
	   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":tditTop");
			this.textureBack = par1IconRegister.registerIcon(IHLModInfo.MODID + ":tditBack");
	   		this.textureLeft = par1IconRegister.registerIcon(IHLModInfo.MODID + ":tditLeft");
	   		this.textureRight = par1IconRegister.registerIcon(IHLModInfo.MODID + ":tditRight");
	   		this.textureBottom = par1IconRegister.registerIcon(IHLModInfo.MODID + ":tditBottom");
		}
	   	
		@Override
		public boolean hasTileEntity(int metadata)
		{
		    return true;
		}
		
		@Override
		public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z){
			TileEntity te = world.getTileEntity(x,y,z);
			if(te instanceof TDITTileEntity)
	        	{
				TDITTileEntity bte = (TDITTileEntity)te;
					if (bte == null || entityPlayer.isSneaking()) {
	        			return false;
	        		}
	        		else if(entityPlayer.getCurrentEquippedItem()==null || !(entityPlayer.getCurrentEquippedItem().getItem() instanceof TDITFrequencyTransmitter))
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
		@SideOnly(Side.CLIENT)
		public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) 
		{
			switch (side)
			{
			case 0:
				return this.textureBottom;
			case 1:
				return this.blockIcon;
			case 2:
				return this.textureBack;
			case 3:
				return this.textureBack;
			case 4:
				return this.textureLeft;
			case 5:
				return this.textureRight;
			default:
				return this.blockIcon;
			}
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon(int side, int meta) 
		{
			switch (side)
			{
			case 0:
				return this.textureBottom;
			case 1:
				return this.blockIcon;
			case 2:
				return this.textureBack;
			case 3:
				return this.textureBack;
			case 4:
				return this.textureLeft;
			case 5:
				return this.textureRight;
			default:
				return this.blockIcon;
			}
		}
		
}
