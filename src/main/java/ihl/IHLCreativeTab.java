package ihl;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IHLCreativeTab extends CreativeTabs {
	
    private static ItemStack ic2_handpump;
    public static final CreativeTabs tab = new IHLCreativeTab();
	 
	public IHLCreativeTab()
	{
		super("IHL");
	}

	@Override
	public ItemStack getIconItemStack()
	{
		ic2_handpump = new ItemStack(IHLMod.ic2_handpump);
	    return ic2_handpump;
	}

	@Override
	public Item getTabIconItem()
	{
	   return null;
	}
	
	@Override
	public String getTranslatedTabLabel()
	{
		return "IHL";
	}
}