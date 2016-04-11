package ihl.items_blocks;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.interfaces.IWire;

public class FiberItem extends Item implements IWire {
    
	protected String info;
	protected FiberMaterial material;
	private String registryName;

	public FiberItem(FiberMaterial material1, int leadsNum1, String registryName1) 
	{
		super();
		this.registryName=registryName1;
		this.material=material1;
		this.info=material.description;
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setMaxStackSize(1);
		this.setUnlocalizedName(registryName);
	}
	
	public FiberItem()
	{
		super();
	}
			
	public static void init()
	{
		FiberMaterial[] var1 = FiberMaterial.values();
		for(int i=0;i<var1.length;i++)
		{
			String registryName = "fiber"+var1[i].description;
			GameRegistry.registerItem(new FiberItem(var1[i],1,registryName),registryName);
		}
	}
	
    @Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList)
    {
    	ItemStack stack = new ItemStack(item);
    	stack.stackTagCompound=new NBTTagCompound();
    	stack.stackTagCompound.setInteger("fullLength", 1024);
    	stack.stackTagCompound.setInteger("length", 1024);
        itemList.add(stack);
    }
	
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
        if(itemStack.stackTagCompound!=null)
        {
        	info.add("Length " + itemStack.stackTagCompound.getInteger("length") +"m");
        }
        info.add(this.info);
    }

	@Override
	public String getTag() 
	{
		return "length";
	}

	@Override
	public String getTagSecondary() 
	{
		return "fullLength";
	}
	
	@Override
   	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":"+this.registryName);
    }
	
	public static enum FiberMaterial
	{
		Glass("Glass");
		FiberMaterial(String description1)
		{
			description=description1;
		}
		public String description;
	}
	

	@Override
	public boolean isSameWire(ItemStack stack1,ItemStack stack2) 
	{
		return stack1.getItem()==stack2.getItem();
	}

}
