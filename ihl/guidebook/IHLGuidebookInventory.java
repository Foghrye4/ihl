package ihl.guidebook;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;

public class IHLGuidebookInventory implements IHasGui{

	ItemStack thisItemStack;
	IInventory inventoryContainer;
	public ItemStack[] content = new ItemStack[4];
	
	public IHLGuidebookInventory(EntityPlayer player, ItemStack stack) {
		thisItemStack=stack;
		inventoryContainer=player.inventory;
	}
	
	public IHLGuidebookInventory(IInventory inventoryContainer1, ItemStack stack) 
	{
		thisItemStack=stack;
		inventoryContainer=inventoryContainer1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new IHLGuidebookGui(new IHLGuidebookContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new IHLGuidebookContainer(player, this);
	}

	@Override
	public String getInventoryName() {
		return "IHLGuidebook";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack stack) {
		return false;
	}
	
	@Override
    public int getInventoryStackLimit()
    {
        return 1;
    }
	
	@Override
	public ItemStack getStackInSlot(int slotNumber) {
		return content[slotNumber];
	}
	
	@Override
	public void setInventorySlotContents(int arg0, ItemStack arg1) 
	{
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemStack decrStackSize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void markDirty() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean isThisContainer(ItemStack stack) 
	{
		return this.thisItemStack.equals(stack);
	}
}
