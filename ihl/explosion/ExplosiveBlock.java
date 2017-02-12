package ihl.explosion;

import ic2.core.IC2;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.items_blocks.IHLItemBlock;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExplosiveBlock extends Block implements ITileEntityProvider{
	
	IIcon[] textures = new IIcon[6];

	public ExplosiveBlock() 
	{
		super(Material.tnt);
        this.setCreativeTab(IHLCreativeTab.tab);
        this.setBlockName("ihlExplosive");
        this.setHardness(2F);
        this.setResistance(1F);
	}
	
	@Override
 	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) 
	{
    	ItemStack result = new ItemStack(item);
    	result.stackTagCompound=new NBTTagCompound();
    	result.stackTagCompound.setInteger("explosionType", 1);//0 - IC2; 1- IHL
    	result.stackTagCompound.setInteger("explosionPower", 31000);
		itemList.add(result);
		result = new ItemStack(item);
    	result.stackTagCompound=new NBTTagCompound();
    	result.stackTagCompound.setInteger("explosionType", 1);//0 - IC2; 1- IHL
    	result.stackTagCompound.setInteger("explosionPower", Integer.MAX_VALUE);//31000
		itemList.add(result);
	}
	
	@Override
 	@SuppressWarnings("rawtypes")
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
    
	public static void init()
	{
		GameRegistry.registerBlock(new ExplosiveBlock(), IHLItemBlock.class,"ihlExplosive");
		GameRegistry.registerTileEntity(ExplosiveTileEntity.class, "explosiveTileEntity");
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
		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":explosiveBlockSide");
	   	this.textures[0] = par1IconRegister.registerIcon(IHLModInfo.MODID + ":explosiveBlockBottom");
	   	this.textures[1] = par1IconRegister.registerIcon(IHLModInfo.MODID + ":explosiveBlockTop");
	   	this.textures[2] = this.textures[3] = this.textures[4] = this.textures[5] = this.blockIcon;
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
			return ((ExplosiveTileEntity)te).ignite(player);
		}
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		TileEntity te = world.getTileEntity(x,y,z);
		if(IC2.platform.isSimulating() && te instanceof ExplosiveTileEntity)
		{
			int[] xyz = {0,0,1,0,0,-1,0,0};
			for(int i=2;i<xyz.length;i++)
			{
				if(world.getBlock(x+xyz[i-2], y+xyz[i-1], z+xyz[i])==Blocks.fire && !te.isInvalid())
				{
					((ExplosiveTileEntity)te).ignite();
				}
			}
		}
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
		return this.getIcon(side, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) 
	{
		return this.textures[side];
	}
	
	@Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int metadata, int flag)
    {
		return true;
    }
    
    public enum ExplosiveType
    {
    	MuffleFurnace("muffleFurnance",MuffleFurnanceTileEntity.class, true, new ItemStack(Items.brick));
    	ExplosiveType(String unlocalizedName1, Class<?> teclass1, boolean isNormalBlock1,ItemStack itemDroppedOnBlockDestroy1)
    	{
    		unlocalizedName=unlocalizedName1;
    		teclass=teclass1;
    		isNormalBlock=isNormalBlock1;
    		itemDroppedOnBlockDestroy=itemDroppedOnBlockDestroy1;
    	}
		String unlocalizedName;
		Class<?> teclass;
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
	
	@Override
    public boolean canDropFromExplosion(Explosion explosion)
    {
        return false;
    }
	
	@Override
	public float getExplosionResistance(Entity entity)
	{
		return -1f;
	}
	
	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y ,int z, double explosionX, double explosionY, double explosionZ)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof ExplosiveTileEntity && !((ExplosiveTileEntity)te).isInvalid())
		{
			return -((ExplosiveTileEntity)te).explosionPower;
		}
		return -1f;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y ,int z, Explosion explosion)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof ExplosiveTileEntity)
		{
			((ExplosiveTileEntity)te).createExplosion(null);
		}
	}
	
	
}
