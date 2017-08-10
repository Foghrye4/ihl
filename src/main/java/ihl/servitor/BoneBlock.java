package ihl.servitor;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BoneBlock extends Block {
	IIcon textureSide2,textureSide3,textureSide4,textureTop;
	
	public BoneBlock(Material material) {
		super(material);
        this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":boneBlockSide1");
		this.textureTop = par1IconRegister.registerIcon(IHLModInfo.MODID + ":boneBlockTop");
   		this.textureSide2 = par1IconRegister.registerIcon(IHLModInfo.MODID + ":boneBlockSide2");
   		this.textureSide3 = par1IconRegister.registerIcon(IHLModInfo.MODID + ":boneBlockSide3");
   		this.textureSide4 = par1IconRegister.registerIcon(IHLModInfo.MODID + ":boneBlockSide4");
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if(!world.isRemote)
		{
			if(world.getBlock(x, y+1, z)==Blocks.fire)
			{
				AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y+1D, z, x+1D, y+2D, z+1D); 
				List pigList = world.getEntitiesWithinAABB(EntityPig.class, aabb);
				if(pigList!=null && pigList.size()>0)
				{
					EntityPig pig = (EntityPig) pigList.get(0);
					DamageSource ds = DamageSource.wither;
					pig.attackEntityFrom(ds, 10F);
		        	LostHeadEntity lh = new LostHeadEntity(world);
		        	lh.setPosition(pig.posX,pig.posY,pig.posZ);
		        	world.spawnEntityInWorld(lh);
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) 
	{
		Block topBlock = world.getBlock(x, y+1, z);
		Block bottomBlock = world.getBlock(x, y-1, z);
		IIcon sideIcon = this.blockIcon;
		if(topBlock==this && bottomBlock==this)
		{
			sideIcon = this.textureSide4;
		}
		else if(topBlock==this)
		{
			sideIcon = this.textureSide3;
		}
		else if(bottomBlock==this)
		{
			sideIcon = this.textureSide2;
		}
		switch (side)
		{
		case 0:
			return this.textureTop;
		case 1:
			return this.textureTop;
		default:
			return sideIcon;
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
			return this.textureTop;
		case 1:
			return this.textureTop;
		default:
			return sideIcon;
		}
	}
}
