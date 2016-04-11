package ihl.items_blocks;

import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.processing.chemistry.ExplosiveTileEntity;
import ihl.processing.metallurgy.MuffleFurnanceTileEntity;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExplosiveBlock extends Block implements ITileEntityProvider{
	
	@SideOnly(Side.CLIENT)
	IIcon textureSide, 
	textureBack, 
	textureTop;

	public ExplosiveBlock() 
	{
		super(Material.tnt);
        this.setCreativeTab(IHLCreativeTab.tab);
        this.setBlockName("ihlExplosive");
        this.setHardness(2F);
        this.setResistance(1F);
	}
	
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if(IC2.platform.isSimulating())
		{
		}
	}
	
 	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) 
	{
    	ItemStack result = new ItemStack(item);
    	result.stackTagCompound=new NBTTagCompound();
    	result.stackTagCompound.setInteger("explosionType", 0);//0 - IC2; 1- IHL
    	result.stackTagCompound.setInteger("explosionPower", 100000);
		//itemList.add(result);
		result = new ItemStack(item);
    	result.stackTagCompound=new NBTTagCompound();
    	result.stackTagCompound.setInteger("explosionType", 1);//0 - IC2; 1- IHL
    	result.stackTagCompound.setInteger("explosionPower", Integer.MAX_VALUE);//31000
		itemList.add(result);
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta)
	{
		if(!world.isRemote)
		{
			TileEntity te = world.getTileEntity(x, y, z);
		}
		super.onBlockPreDestroy(world, x, y, z, meta);
	}

	@Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.1F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.9F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBounds(0.0F, 0.0F, 0.9F, 1.0F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
			this.setBlockBoundsForItemRender();
    }
	
    @Override
	public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
    	int facing = 3;
		TileEntity te = blockAccess.getTileEntity(x, y, z);
		if(te!=null && te instanceof IWrenchable)
		{
			IWrenchable tebh = (IWrenchable) te;
			facing=tebh.getFacing();
		}
		super.setBlockBoundsBasedOnState(blockAccess, x, y, z);
    }
	
	public static void init()
	{
		GameRegistry.registerBlock(new ExplosiveBlock(), IHLItemBlock.class,"ihlExplosive");
		GameRegistry.registerTileEntity(ExplosiveTileEntity.class, "explosiveTileEntity");
		ExplosiveType[] var1 = ExplosiveType.values();
	}
	
	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
		super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, flag);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		return new ExplosiveTileEntity();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenSide");
	   	this.textureBack = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenBack");
		this.textureSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenSide");
	   	this.textureTop = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenTop");
	}
   	
	@Override
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}
	
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int i,float pos_x,float pos_y,float pos_z){
		TileEntity te = world.getTileEntity(x,y,z);
		if(IC2.platform.isSimulating() && te instanceof ExplosiveTileEntity)
		{
			((ExplosiveTileEntity)te).createExplosion(player);
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
        if(t!=null)
        {
        	t.readFromNBT(itemStack.stackTagCompound);
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
				2,3,0,1,4,5,
				2,3,1,0,4,5,
				2,3,5,4,0,1,
				2,3,4,5,1,0
				};
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IWrenchable)
		{
			IWrenchable tebh = (IWrenchable) te;
			facing=tebh.getFacing();
		}
		return this.getIconFromFacing(facing, side);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) 
	{
		return this.getIconFromFacing(3, side);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromFacing(int facing, int side) 
	{
			return this.textureSide;
	}

	@Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int metadata, int flag)
    {
		return true;
    }
    
    public enum ExplosiveType
    {
    	MuffleFurnace("muffleFurnance",MuffleFurnanceTileEntity.class, true, new ItemStack(Items.brick));
    	ExplosiveType(String unlocalizedName1, Class teclass1, boolean isNormalBlock1,ItemStack itemDroppedOnBlockDestroy1)
    	{
    		unlocalizedName=unlocalizedName1;
    		teclass=teclass1;
    		isNormalBlock=isNormalBlock1;
    		itemDroppedOnBlockDestroy=itemDroppedOnBlockDestroy1;
    	}
		String unlocalizedName;
		Class teclass;
		boolean isNormalBlock=true;
		ItemStack itemDroppedOnBlockDestroy;
    }
    
	@Override
	public int getRenderType()
	{
		return 0;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return true;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getInnerTextureForBlockRenderer()
	{
		return this.blockIcon;
	}
}
