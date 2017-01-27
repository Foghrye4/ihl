package ihl.processing.chemistry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.utils.IHLUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BatteryItem extends Item implements IElectricItem, IItemHudInfo
{
    public int maxCharge=65536;
    public int transferLimit = 4096;
    public int tier = 4;	
    private static Map<Integer, IIcon> iconMap = new HashMap<Integer, IIcon>();
	private static Map<Integer, String> nameMap = new HashMap<Integer, String>();
	private static Map<Integer, String> descriptionMap = new HashMap<Integer, String>();

    public BatteryItem()
    {
        super();
        this.setMaxDamage(27);
        this.setCreativeTab(IHLCreativeTab.tab);
        this.maxStackSize=1;
        this.canRepair=false;
		this.setUnlocalizedName("battery");
    }
    
	public static void init()
	{
		BatteryItem item = new BatteryItem();
		GameRegistry.registerItem(item,item.getUnlocalizedName());
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			nameMap.put(var1[i].damage,var1[i].unLocalizedName);
			IHLUtils.registerLocally(var1[i].unLocalizedName, new ItemStack(item,1,var1[i].damage));
			if(var1[i].description!=null)
			{
				descriptionMap.put(var1[i].damage,var1[i].description);
			}
		}
	}

    @Override
	public boolean canProvideEnergy(ItemStack itemStack)
    {
        return true;
    }

    @Override
	public Item getChargedItem(ItemStack itemStack)
    {
        return this;
    }

    @Override
	public Item getEmptyItem(ItemStack itemStack)
    {
        return this;
    }

    @Override
	public double getMaxCharge(ItemStack itemStack)
    {
        return this.maxCharge;
    }

    @Override
	public int getTier(ItemStack itemStack)
    {
        return this.tier;
    }

    @Override
	public double getTransferLimit(ItemStack itemStack)
    {
        return this.transferLimit;
    }

    @Override
	public List<String> getHudInfo(ItemStack itemStack)
    {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(itemStack));
        return info;
    }
    
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		Type[] var1 = Type.values();
		for(int i=0;i<var1.length;i++)
		{
			iconMap.put(var1[i].damage, register.registerIcon(IHLModInfo.MODID + ":"+var1[i].textureName));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int i) 
	{
		return iconMap.get(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
		return nameMap.get(0);//stack.getItemDamage());
	}

	@SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void func_150895_a(Item item, CreativeTabs par2CreativeTabs, List itemList)
    {
        ItemStack itemStack = new ItemStack(this, 1);
        ItemStack charged;

        if (this.getChargedItem(itemStack) == this)
        {
            charged = new ItemStack(this, 1);
            ElectricItem.manager.charge(charged, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false);
            itemList.add(charged);
        }

        if (this.getEmptyItem(itemStack) == this)
        {
            charged = new ItemStack(this, 1);
            ElectricItem.manager.charge(charged, 0.0D, Integer.MAX_VALUE, true, false);
            itemList.add(charged);
        }
    }
    
    public static ItemStack getFullyChargedItemStack(String name)
    {
    	ItemStack stack = IHLUtils.getThisModItemStack(name);
    	ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false);
    	return stack;
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
        if(BatteryItem.descriptionMap.containsKey(0))//itemStack.getItemDamage()))
        {
        	info.add(BatteryItem.descriptionMap.get(0));//itemStack.getItemDamage()));
        }
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (this.maxCharge-ElectricItem.manager.getCharge(stack))/this.maxCharge;
    }
    
    public enum Type
	{
		LeadAcidBattery(0,"leadAcidBattery","Pb/Pb2PbO4 + H2SO4");
		Type(int damage1,String unlocalizedName1,String description1)
		{
			damage=damage1;
			textureName=oreRegistryName=unLocalizedName=unlocalizedName1;
			description=description1;
		}
		public int damage;
		public String unLocalizedName;
		public String oreRegistryName;
		public String description;
		public String textureName;
	}

	public static ItemStack getFullyChargedItemStackWithSize(String name, int stackSize1) 
	{
		ItemStack stack = getFullyChargedItemStack(name);
		stack.stackSize=stackSize1;
		return stack;
	}
}