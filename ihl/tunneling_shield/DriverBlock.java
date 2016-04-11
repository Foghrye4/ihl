package ihl.tunneling_shield;
import ic2.api.item.IC2Items;
import ic2.core.IC2;
import ihl.IHLModInfo;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DriverBlock extends BlockContainer {
	
	public DriverBlock(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta)
	{
		if(IC2.platform.isSimulating())
		{
			DriverTileEntity te = (DriverTileEntity)world.getTileEntity(x,y,z);
			te.cleanUp();
		}
	}
	
	@Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return IC2Items.getItem("machine").getItem();
    }
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
		ItemStack result = IC2Items.getItem("machine").copy();
        this.dropBlockAsItem(world, x, y, z, result);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		return new DriverTileEntity();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":driver");
	}
   	
	@Override
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}
	

	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z){
		if(world.getTileEntity(x,y,z) instanceof DriverTileEntity)
        	{
				DriverTileEntity var10 = (DriverTileEntity)world.getTileEntity(x,y,z);
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
}