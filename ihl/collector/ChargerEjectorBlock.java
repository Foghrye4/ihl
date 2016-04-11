package ihl.collector;

import ic2.api.item.IC2Items;
import ihl.IHLModInfo;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ChargerEjectorBlock extends BlockContainer {

	public ChargerEjectorBlock(Material material) {
		super(material);
	}

	@Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return IC2Items.getItem("glassFiberCableItem").getItem();
    }

	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		return new ChargerEjectorTileEntity();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":ace");
	}
   	
	@Override
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}
	
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z){
		if(world.getTileEntity(x,y,z) instanceof ChargerEjectorTileEntity)
        	{
			ChargerEjectorTileEntity var10 = (ChargerEjectorTileEntity)world.getTileEntity(x,y,z);
				if (var10 == null || entityPlayer.isSneaking()) {
        			return false;
        		}
        		else
        		{
        			return var10.getGui(entityPlayer);
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

	@Override
	public boolean isOpaqueCube()
	{
	return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
	return false;
	}
}
