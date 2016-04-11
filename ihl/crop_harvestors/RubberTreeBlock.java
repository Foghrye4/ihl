package ihl.crop_harvestors;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IC2Items;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RubberTreeBlock extends Block{
	
		IIcon textureSide, textureTop;
		TreeType type;
	
		public RubberTreeBlock(TreeType type1) 
		{
			super(Material.wood);
			type=type1;
		}

		@Override
	    public Item getItemDropped(int var1, Random rnd, int var2)
	    {
			switch(type)
			{
			case RUBBERTREE:
				return IC2Items.getItem("rubberWood").getItem();
			case SPRUCE:
				return Blocks.log.getItemDropped(var1, rnd, var2);
			default:
				return IC2Items.getItem("rubberWood").getItem();
			}
			
	    }
		
		@Override
		public boolean canSustainLeaves(IBlockAccess blockAccess, int x, int y, int z)
		{
			return true;
		}
		
		@Override
		public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
		{
			ItemStack result;
			switch(type)
			{
			case RUBBERTREE:
				result = IC2Items.getItem("rubberWood").copy();
			case SPRUCE:
				result = new ItemStack(Blocks.log,1,1);
			default:
				result = IC2Items.getItem("rubberWood").copy();
			}
	        this.dropBlockAsItem(world, x, y, z, result);
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public void registerBlockIcons(IIconRegister par1IconRegister)
		{
			ItemStack result;
			switch(type)
			{
			case RUBBERTREE:
		   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blockRubWoodFront");
				this.textureTop = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blockRubWoodTop");
				this.textureSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blockRubWoodSide");
			case SPRUCE:
		   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":blockSpruceFront");
				this.textureTop = par1IconRegister.registerIcon("minecraft:log_spruce_top");
				this.textureSide = par1IconRegister.registerIcon("minecraft:log_spruce");
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
				facing=world.getBlockMetadata(x, y, z);
			switch (mask[facing*6+side])
			{
			case 0:
				return this.textureSide;
			case 1:
				return this.blockIcon;
			case 2:
				return this.textureTop;
			case 3:
				return this.textureTop;
			case 4:
				return this.textureSide;
			case 5:
				return this.textureSide;
			default:
				return this.textureSide;
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
			case 2:
				return this.blockIcon;
			case 3:
				return this.textureSide;
			case 4:
				return this.textureSide;
			case 5:
				return this.textureSide;
			default:
				return this.textureSide;
			}
		}
		
		public enum TreeType
		{
			RUBBERTREE,
			SPRUCE
		}
		
	    @Override
	    public boolean isWood(IBlockAccess world, int x, int y, int z)
	    {
	        return true;
	    }

	    @Override
	    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	    {
	        return 4;
	    }

	    @Override
	    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	    {
	        return 20;
	    }
		
}
