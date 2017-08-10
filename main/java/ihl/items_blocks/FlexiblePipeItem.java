package ihl.items_blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.utils.IHLUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class FlexiblePipeItem extends Item {
    
	private static Map<Integer, IIcon> iconMap = new HashMap<Integer, IIcon>();
	private static Map<Integer, String> nameMap = new HashMap<Integer, String>();

	public FlexiblePipeItem() 
	{
		super();
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setMaxStackSize(1);
		this.setUnlocalizedName("flexiblePipe");
	}
	
	public static void init()
	{
		FlexiblePipeItem item = new FlexiblePipeItem();
		Type[] var1 = Type.values();
		GameRegistry.registerItem(item,item.getUnlocalizedName());
		for(int i=0;i<var1.length;i++)
		{
			nameMap.put(var1[i].damage,var1[i].unLocalizedName);
			IHLUtils.registerLocally(var1[i].unLocalizedName, new ItemStack(item,1,var1[i].damage));
		}
	}
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList)
    {
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			ItemStack stack = IHLUtils.getThisModItemStack(var1[i].unLocalizedName);
	    	stack.stackTagCompound=new NBTTagCompound();
	    	stack.stackTagCompound.setInteger("fullLength", 16);
	    	stack.stackTagCompound.setInteger("length", 16);
	        itemList.add(stack);
		}
    }
	
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
        if(itemStack.stackTagCompound!=null)
        {
        	info.add("Length " + itemStack.stackTagCompound.getInteger("length") +"m");
        }
    }
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			iconMap.put(var1[i].damage, register.registerIcon(IHLModInfo.MODID + ":"+var1[i].unLocalizedName));
		}
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int i) 
	{
		return iconMap.get(i);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
		return nameMap.get(stack.getItemDamage());
	}

	public static enum Type
	{
		VulcanizedRubber(1, "pipeVulcanizedRubber"),
		RubberWithSulfurPipe(0, "pipeRubberWithSulfur");
		Type(int damage1, String unLocalizedName1)
		{
			damage=damage1;
			unLocalizedName=unLocalizedName1;
		}
		public int damage;
		public String unLocalizedName;
	}
}
