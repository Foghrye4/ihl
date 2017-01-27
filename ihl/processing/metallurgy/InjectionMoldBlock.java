package ihl.processing.metallurgy;

import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.items_blocks.IHLItemBlock;
import ihl.metallurgy.constants.Details;
import ihl.utils.IHLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InjectionMoldBlock extends Block implements ITileEntityProvider{
	
	IIcon textureSide;
	
	public static InjectionMoldBlock instance;
	public static String[] materials = new String[] {"Bronze","Steel","Gold","Magnesium", "Lithium", "TarPitch", "Potassium", "Sodium"};

	public InjectionMoldBlock() 
	{
		super(Material.rock);
        this.setResistance(0.5F);
        this.setHardness(0.5F);
        this.setBlockName("injectionMold");
		this.setCreativeTab(IHLCreativeTab.tab);
		instance=this;
	}
	
	public static void init()
	{
		GameRegistry.registerBlock(new InjectionMoldBlock(),IHLItemBlock.class,"injectionMold");
		GameRegistry.registerTileEntity(InjectionMoldTileEntity.class,"injectionMoldTileEntity");
	}

	@Override
	public void	dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag)
	{
		super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, flag);
	}
	

 	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) 
	{
    	ItemStack result = new ItemStack(item);
    	result.stackTagCompound=new NBTTagCompound();
    	result.stackTagCompound.setString("resultSuffix", "ingot");
		itemList.add(result);
	}

	
	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		return new InjectionMoldTileEntity();
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta)
	{
		if(!world.isRemote)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te!=null && te instanceof InjectionMoldTileEntity)
			{
				InjectionMoldTileEntity gte = (InjectionMoldTileEntity) te;
				gte.dropContents();
			}
		}
		super.onBlockPreDestroy(world, x, y, z, meta);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) 
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        TileEntity t = world.getTileEntity(x, y, z);
        if(t!=null)
        {
        	InjectionMoldTileEntity te = (InjectionMoldTileEntity)t;
        	if(te.result!=null)drops.add(te.result);
        	if(te.result2!=null)drops.add(te.result2);
        }
        return drops;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
   		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":injectionMoldTop");
   		this.textureSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":injectionMoldSide");
	}
   	
	@Override
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}
	
	@Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack)
    {
        TileEntity t = world.getTileEntity(x, y, z);
        if(t!=null && t instanceof InjectionMoldTileEntity)
        {
        	InjectionMoldTileEntity te = (InjectionMoldTileEntity)t;
        	te.resultSuffix=itemStack.stackTagCompound.getString("resultSuffix");
        	if(itemStack.stackTagCompound.hasKey("isContainStearin"))
        	{
        		te.isContainStearin=itemStack.stackTagCompound.getBoolean("isContainStearin");
        	}
        	if(itemStack.stackTagCompound.hasKey("maxAmount"))
        	{
        		te.maxAmount=itemStack.stackTagCompound.getByte("maxAmount");
        	}

        }
    }
	@Override
	public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int i,float pos_x,float pos_y,float pos_z)
	{
		if(player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof Crucible)
		{		
			if(!world.isRemote)
			{
				return ((Crucible)player.getCurrentEquippedItem().getItem()).onItemUse(player.getCurrentEquippedItem(), player, world, x, y, z, 0, pos_x, pos_y, pos_z);
			}
			return true;
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
		case 1:
			return this.blockIcon;
		case 0:
			return this.textureSide;
		case 2:
			return this.textureSide;
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) 
	{
		switch (side)
		{
		case 1:
			return this.blockIcon;
		case 0:
			return this.textureSide;
		case 2:
			return this.textureSide;
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
	
	public ItemStack getSandInjectionMoldForResult(String result1)
	{
		ItemStack stack = new ItemStack(this);
		stack.stackTagCompound=new NBTTagCompound();
    	stack.stackTagCompound.setString("resultSuffix", result1);
    	registerRecipes(result1, stack);
    	return stack;
	}
	
	public ItemStack getGypsumInjectionMoldForResult(String result1)
	{
		ItemStack stack = new ItemStack(this);
		stack.stackTagCompound=new NBTTagCompound();
    	stack.stackTagCompound.setString("resultSuffix", result1);
    	stack.stackTagCompound.setByte("maxAmount", (byte)1);
    	stack.stackTagCompound.setBoolean("isContainStearin", true);
    	registerRecipes(result1, stack);
    	return stack;
	}
	
	public static void registerRecipes(String result1,ItemStack stack1)
	{
		for(int i=0;i<materials.length;i++)
		{
			String material = InjectionMoldBlock.materials[i];
			ItemStack stack = IHLUtils.getItemStackIfExist(result1+material);
			if(stack!=null)
			{
				material=material.toLowerCase(Locale.ROOT);
				FluidStack fluidstack = IHLUtils.getFluidStackIfExist("molten."+material,Details.getMeltingFluidAmount(result1));
				if(fluidstack!=null)
				{
					// Cap hash to positive integer range.
					stack1.setItemDamage(result1.hashCode() & Integer.MAX_VALUE);
/*					IHLMod.log.info("Setting damage for injection mold to: "
							+result1.toLowerCase(Locale.ROOT).hashCode()
							+" using hash code of "+result1
							+" Result of lower case: "+result1.toLowerCase(Locale.ROOT));*/
					InjectionMoldTileEntity.addRecipe(fluidstack,stack1,stack);
				}
			}
		}
	}

}