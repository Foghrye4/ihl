package ihl.i_hate_liquids;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDynamicLiquidPlus extends BlockLiquid{

	public BlockDynamicLiquidPlus(Material material) 
	{
		super(material);
        this.enableStats = false;
        //this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
   	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID+":metalShards");
    }
   	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
			return this.blockIcon;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random p_149674_5_)
	{
		if(!world.isRemote)
		{
		int meta = world.getBlockMetadata(x, y, z);
		if(meta>=7)
		{
			world.setBlockToAir(x,y,z);
			return;
		}
		int meta2=16;
		int fd;
		int xz[] = {0,-1,0,1,0};
			for(int i=0;i<=3;i++)
			{
				fd=getFlowDecay(world,x+xz[i], y, z+xz[i+1]);
				if(fd!=-1 && meta2>fd)
				{
					meta2=fd;
				}
			}
			if(meta2==16)
			{
				world.setBlockMetadataWithNotify(x,y,z,meta+1,3);
			}
			else if(meta2>=6)
			{
				world.setBlockToAir(x,y,z);
				return;
			}
			else
			{
				world.setBlockMetadataWithNotify(x,y,z,meta2+1,3);
			}
		world.scheduleBlockUpdate(x, y, z, this,20);
	    }
	}

	private int getFlowDecay(World par1World, int par2, int par3, int par4)
	{
	    	Block block = par1World.getBlock(par2, par3, par4);
	    	return  block.getMaterial().isLiquid() ? par1World.getBlockMetadata(par2, par3, par4) : -1;
	}
}
