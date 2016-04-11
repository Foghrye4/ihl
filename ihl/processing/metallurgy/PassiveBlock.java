package ihl.processing.metallurgy;

import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.items_blocks.IHLItemBlock;
import ihl.utils.IHLUtils;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PassiveBlock  extends Block{
	
	@SideOnly(Side.CLIENT)
	private IIcon[][] textures;

	private static PassiveBlock instance;

	public PassiveBlock() 
	{
		super(Material.iron);
        this.setCreativeTab(IHLCreativeTab.tab);
        this.setBlockName("IHLShieldAssemblyUnitBlock");
        this.setHardness(2F);
        this.setResistance(1F);
        instance=this;
	}
	
	public static void init()
	{
		PassiveBlock block = new PassiveBlock();
		GameRegistry.registerBlock(block, IHLItemBlock.class, "IHLShieldAssemblyUnitBlock");
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			IHLItemBlock blockItem = (IHLItemBlock) Item.getItemFromBlock(block);
			blockItem.nameMap.put(var1[i].meta,var1[i].unlocalizedName);
			IHLUtils.registerLocally(var1[i].unlocalizedName, new ItemStack(block,1,var1[i].meta));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tabs, List itemList)
    {
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			ItemStack stack = IHLUtils.getThisModItemStack(var1[i].unlocalizedName);
	        itemList.add(stack);
		}
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		textures = new IIcon[2][6];
		textures[0][3]=textures[0][4]=textures[0][2]=textures[0][5]=this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":shieldAU");
		textures[1][0]=textures[1][1]=textures[0][0]=textures[0][1]=par1IconRegister.registerIcon(IHLModInfo.MODID + ":shieldAUTop");
		textures[1][3]=textures[1][4]=textures[1][2]=textures[1][5]=this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":shieldAU2");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) 
	{
		int meta = world.getBlockMetadata(x, y, z);
		return textures[meta][side];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) 
	{
		return textures[meta][side];
	}
	
    public enum Type
    {
    	AdvancedShieldAssemblyUnitBlock("advancedShieldAssemblyUnitBlock", 1),
    	IHLShieldAssemblyUnitBlock("IHLShieldAssemblyUnitBlock", 0);
    	Type(String unlocalizedName1, int meta1)
    	{
    		unlocalizedName=unlocalizedName1;
    		meta=meta1;
    	}
		public String unlocalizedName;
		public int meta;
    }
}