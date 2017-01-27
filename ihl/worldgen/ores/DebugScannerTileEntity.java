package ihl.worldgen.ores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.oredict.OreDictionary;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;

public class DebugScannerTileEntity extends TileEntityInventory implements IHasGui
{
    public final InvSlot itemsSlot;
    private ItemStack lastItem;
    public List<String> oreDictionaryEntries = new ArrayList<String>();
    
    public DebugScannerTileEntity()
    {
    	this.itemsSlot = new InvSlot(this, "drainInput", 0, InvSlot.Access.I, 2);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=(short)side && side!=0 && side!=1;
	}
	
    @Override
	public void updateEntityClient()
    {
        if(IC2.platform.isRendering())
        {
        	if(this.itemsSlot.get(0)!=null && this.itemsSlot.get(0)!=lastItem)
        	{
        		this.oreDictionaryEntries.clear();
        		int[] ids = OreDictionary.getOreIDs(this.itemsSlot.get());
        		String itemNameFromIR = Item.itemRegistry.getNameForObject(this.itemsSlot.get().getItem());
        		this.oreDictionaryEntries.add("ItemRegistry entry:");
        		this.oreDictionaryEntries.add(" "+itemNameFromIR);
        		this.oreDictionaryEntries.add("Item damage: " + this.itemsSlot.get().getItemDamage());
        		this.oreDictionaryEntries.add("Item class:");
        		this.oreDictionaryEntries.add(" "+this.itemsSlot.get().getItem().getClass().getCanonicalName());
        		if(this.itemsSlot.get().stackTagCompound!=null)
        		{
        			this.oreDictionaryEntries.add("NBT keys:");
        			Iterator<?> iterator = this.itemsSlot.get().stackTagCompound.func_150296_c().iterator();
        			while(iterator.hasNext())
        			{
        				String entry = (String) iterator.next();
        				if(this.itemsSlot.get().stackTagCompound.getTag(entry) instanceof NBTTagCompound)
        				{
            				this.oreDictionaryEntries.add(" "+entry);
            				NBTTagCompound ct = this.itemsSlot.get().stackTagCompound.getCompoundTag(entry);
            				if(ct!=null && ct.func_150296_c()!=null && !ct.func_150296_c().isEmpty())
            				{
            					this.oreDictionaryEntries.add(" -NBT compound tag subkeys:");
            					Iterator<?> stIterator = ct.func_150296_c().iterator();
                    			while(stIterator.hasNext())
                    			{
                    				String entry2 = (String) stIterator.next();
                    				this.oreDictionaryEntries.add("    "+entry2+"="+ct.getString(entry2));
                    			}
            				}
        				}
        				else
        				{
            				this.oreDictionaryEntries.add(" "+entry+"="+this.itemsSlot.get().stackTagCompound.getString(entry));
        				}
        			}
        		}
        		if(ids.length>0)
        		{
        			this.oreDictionaryEntries.add("Ore dict. entries:");
        			for(int i=0;i<ids.length;i++)
        			{
        				this.oreDictionaryEntries.add(" "+OreDictionary.getOreName(ids[i]));
        			}
        		}
        		lastItem=this.itemsSlot.get(0);
        	}
        	if(this.itemsSlot.get(1)!=null && this.itemsSlot.get(1)!=lastItem)
        	{
        		this.oreDictionaryEntries.clear();
        		if(this.itemsSlot.get(1).getItem() instanceof IFluidContainerItem)
        		{
        			FluidStack fluid = ((IFluidContainerItem)this.itemsSlot.get(1).getItem()).getFluid(this.itemsSlot.get(1));
        			if(fluid!=null)
        			{
                		this.oreDictionaryEntries.add("Internal fluid name:");
                		this.oreDictionaryEntries.add(" "+fluid.getFluid().getName());
                		this.oreDictionaryEntries.add("Fluid class:");
                		this.oreDictionaryEntries.add(" "+fluid.getFluid().getClass().getName());
                		this.oreDictionaryEntries.add("Fluid id:");
                		this.oreDictionaryEntries.add(" "+fluid.getFluid().getID());
                		this.oreDictionaryEntries.add("Fluid still icon:");
                		if(fluid.getFluid().getStillIcon()!=null)
                		{
                    		this.oreDictionaryEntries.add(" "+fluid.getFluid().getStillIcon().getIconName());
                		}
                		else
                		{
                    		this.oreDictionaryEntries.add(" <missing icon> ");
                		}
                		this.oreDictionaryEntries.add("Fluid flowing icon:");
                		if(fluid.getFluid().getFlowingIcon()!=null)
                		{
                    		this.oreDictionaryEntries.add(" "+fluid.getFluid().getFlowingIcon().getIconName());
                		}
                		else
                		{
                    		this.oreDictionaryEntries.add(" <missing icon> ");
                		}
        			}
        		}
        		lastItem=this.itemsSlot.get(1);
        	}
        }
    }
    
    /**
     * Returns the name of the inventory
     */
    @Override
	public String getInventoryName()
    {
        return "debugScanner";
    }

    @Override
	public ContainerBase<?> getGuiContainer(EntityPlayer entityPlayer)
    {
        return new DebugScannerContainer(entityPlayer, this);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin)
    {
        return new DebugScannerGui(new DebugScannerContainer(entityPlayer, this));
    }

    @Override
	public void onGuiClosed(EntityPlayer entityPlayer) {}


    public boolean getGui(EntityPlayer player)
    {
			return this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
    }
 }