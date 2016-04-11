package ihl.guidebook;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.item.IHandHeldInventory;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;

public class IHLGuidebookItem extends Item implements IHandHeldInventory{

	public IHLGuidebookItem() {
		super();
		this.setUnlocalizedName("guidebook");
		this.setCreativeTab(IHLCreativeTab.tab);
		this.maxStackSize=1;
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setMaxDamage(0);
	}
	
	public static void init()
	{
		GameRegistry.registerItem(new IHLGuidebookItem(), "guidebook");
	}

	@Override
	public IHasGui getInventory(EntityPlayer entityPlayer, ItemStack itemStack) 
	{
		return new IHLGuidebookInventory(entityPlayer, itemStack);
	}
	
    @Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (IC2.platform.isSimulating())
        {
            IC2.platform.launchGui(entityPlayer, this.getInventory(entityPlayer, itemStack));
        }
        return itemStack;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		itemIcon=register.registerIcon(IHLModInfo.MODID + ":guidebook");
	}
	
    @Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
    {
		return false;
    }
    
}
