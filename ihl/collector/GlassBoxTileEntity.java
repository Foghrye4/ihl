package ihl.collector;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.network.NetworkManager;
import ihl.utils.IHLUtils;

public class GlassBoxTileEntity extends TileEntityInventory implements IHasGui {

    public final InvSlot invSlot;
	public boolean isGuiScreenOpened=false;
	private int bigTimer=0;

	public GlassBoxTileEntity()
	{
        super();
        this.invSlot = new InvSlot(this, "inventory", 0, InvSlot.Access.IO, 16);
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) 
	{
		for(int i=0;i<this.invSlot.size();i++)
		{
			if(this.invSlot.get(i)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.invSlot.get(i)));
		}
		return IHLUtils.getThisModItemStack("glassBoxBlock");
	}
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("invSlot");
		return fields;
    }
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
	
    @Override
	public void updateEntityServer()
    {
    		boolean needInvUpdate=false;
    			List<EntityItem> eItemList = new ArrayList();
    			for(int i=0;i<this.invSlot.size();i++)
    			{
    				if(this.invSlot.get(i)==null)
    				{
    	        		if(eItemList.isEmpty())
    	        		{
    	        			eItemList = this.getEItemsList();
    	            		if(eItemList.isEmpty())
    	            		{
    	            			break;
    	            		}
    	            		else
    	            		{
    	            			EntityItem entityItem = eItemList.remove(0);
        	        			ItemStack eitem = entityItem.getEntityItem();
    	        				entityItem.setDead();
        	        			if(eitem!=null)
        	        			{
        	        				this.invSlot.put(i, eitem);
        	        				needInvUpdate=true;
        	        			}
        	            		if(eItemList.isEmpty())
        	            		{
        	            			break;
        	            		}
    	            		}
    	        		}
    	        		else
    	        		{
    	        			EntityItem entityItem = eItemList.remove(0);
    	        			ItemStack eitem = entityItem.getEntityItem();
	        				entityItem.setDead();
    	        			if(eitem!=null)
    	        			{
    	        				this.invSlot.put(i, eitem);
    	        				needInvUpdate=true;
    	        			}
    	            		if(eItemList.isEmpty())
    	            		{
    	            			break;
    	            		}
    	        		}
    				}
    				else
    				{
    		            EntityPlayer player = this.worldObj.getClosestPlayer(this.xCoord+0.5D, this.yCoord+0.5D, this.zCoord+0.5D, 0.5D);
    		            if(player!=null && player instanceof EntityPlayerMP)
    		            {
  		            			if(player.inventory.getFirstEmptyStack()>=0 && player.inventory.addItemStackToInventory(this.invSlot.get(i)))
  		            			{
    		            				this.invSlot.put(i, null);
    		            				needInvUpdate=true;
   		            			}
    		            }
    				}
    			}
    		if(needInvUpdate)
    		{
    			IC2.network.get().updateTileEntityField(this, "invSlot");
    		}
    }
    
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer arg0, boolean arg1) 
	{
		return new GlassBoxGui(new GlassBoxContainer(arg0, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer arg0) {
		return new GlassBoxContainer(arg0, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) 	
	{
		this.isGuiScreenOpened=false;
	}

	@Override
	public String getInventoryName() {
		return "glass_box";
	}
	
    public boolean getGui(EntityPlayer player)
    {
    		this.isGuiScreenOpened = this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
			return this.isGuiScreenOpened;
    }
    
    protected List<EntityItem> getEItemsList()
    {
        double range = 0.2D;
        AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.xCoord,this.yCoord,this.zCoord,this.xCoord+1.0D,this.yCoord+1.0D+range,this.zCoord+1.0D);
        List<EntityItem> eItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, searchArea);
    	return eItemsList;
    }
    
    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
    {
    	super.setInventorySlotContents(var1, var2);
    	IC2.network.get().updateTileEntityField(this, "invSlot");
    }
    
    public void dropContents()
    {
		for(int i=0;i<this.invSlot.size();i++)
		{
			if(this.invSlot.get(i)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.invSlot.get(i)));
		}
    }
    
	@Override
	public short getFacing()
	{
		return 3;
	}
}
