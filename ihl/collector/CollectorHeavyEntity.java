package ihl.collector;

import ihl.IHLMod;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class CollectorHeavyEntity extends CollectorEntity {
    private ItemStack capturedStack2;
    private ItemStack capturedStack3;
    private ItemStack capturedStack4;

	public CollectorHeavyEntity(World par1World) 
	{
		super(par1World);
		this.setSize(0.98F, 0.25F);
		this.energyConsume=4;
		this.thisItem=IHLMod.collectorHeavyItem;
		this.waypointX=this.posX;
		this.waypointY=this.posY;
		this.waypointZ=this.posZ;
		this.lastItemX=this.posX;
		this.lastItemY=this.posY;
		this.lastItemZ=this.posZ;
	}
	
    public CollectorHeavyEntity(World par1World, double par2, double par4, double par6)
    {
        this(par1World);
        this.setPosition(par2, par4, par6);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
		this.lastItemX=this.posX;
		this.lastItemY=this.posY;
		this.lastItemZ=this.posZ;
		this.energyConsume=4;
		this.thisItem=IHLMod.collectorHeavyItem;
    }

	
	@Override
    protected void entityInit()
    {
    	super.entityInit();
    	this.dataWatcher.addObjectByDataType(19, 5);
    	this.dataWatcher.addObjectByDataType(20, 5);
    	this.dataWatcher.addObjectByDataType(21, 5);
    }
	
	@Override
    protected void dropItem()
    {
		if(this.hopper!=null)
		{
        double var1 = this.hopper.xCoord - this.posX;
        double var3 = this.hopper.yCoord - this.posY + 2D;
        double var5 = this.hopper.zCoord - this.posZ;
        double var7 = var1 * var1 + var3 * var3 + var5 * var5;

        if (var7 < 2.0D)
        {
        	for(int i=0;i<4;i++)
    		{
            	if(getItemStackNum(i)!=null && this.addItemStackToInventory((IInventory)this.hopper, getItemStackNum(i)))
            	{
            		setItemStackSlotContent(i,null);
            	}
    		}
        }
		}
    }
	
    public ItemStack getVisibleItemStack(int num)
    {
    	return this.dataWatcher.getWatchableObjectItemStack(18+num);
    }
    
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
	    super.readEntityFromNBT(nbt);
	    try
	    {
	        NBTTagList var2 = nbt.getTagList("Items", 10);
	        if(var2!=null)
	        {
	        	for(int i=1;i<4;i++)
	        	{
	        		NBTTagCompound var4 = var2.getCompoundTagAt(i);
	        		if(var4!=null)
	        		{
	        			this.setItemStackSlotContent(i, ItemStack.loadItemStackFromNBT(var4));
	       				if(this.getItemStackNum(i)!=null)
	       				{	
	       					this.dataWatcher.updateObject(18+i, this.getItemStackNum(i).copy());
	       				}	
	        		}
	        	}
	        }
	    }
	    catch(Throwable e)
	    {
	    	e.printStackTrace();
	    }
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
    	super.writeEntityToNBT(nbt);
    	try
    	{
	        NBTTagList var2 = nbt.getTagList("Items", 10);
	        if(var2!=null)
	        {
		    	for(int i=1;i<4;i++)
		    	{
		    		if(this.getItemStackNum(i)!=null)
		    		{
		    			NBTTagCompound var4 = new NBTTagCompound();
		    			var4.setByte("Slot", (byte)i);
		    			this.getItemStackNum(i).writeToNBT(var4);
		    			var2.appendTag(var4);
		    		}
		    	}
	        }
    	}
    	catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
    protected void spawnContainedItems()
    {
    	if(this.capturedStack!=null)
    	{
        	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.capturedStack));
    	}
    	if(this.capturedStack2!=null)
    	{
        	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.capturedStack2));
    	}
    	if(this.capturedStack3!=null)
    	{
        	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.capturedStack3));
    	}
    	if(this.capturedStack4!=null)
    	{
        	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.capturedStack4));
    	}
    }

	private ItemStack getItemStackNum(int num)
	{
		switch(num)
		{
		case 0:
			return this.capturedStack;
		case 1:
			return this.capturedStack2;
		case 2:
			return this.capturedStack3;
		case 3:
			return this.capturedStack4;
		default:
			return this.capturedStack;
		}
	}
	
	private void setItemStackSlotContent(int num, ItemStack itemStack)
	{
		switch(num)
		{
		case 0:
			this.capturedStack=itemStack;
			if(this.capturedStack!=null)
      		{
				this.dataWatcher.updateObject(18, this.capturedStack.copy());
      		}
			else
			{
				this.dataWatcher.updateObject(18, this.capturedStack);
			}
			break;
		case 1:
			this.capturedStack2=itemStack;
			if(this.capturedStack2!=null)
      		{
				this.dataWatcher.updateObject(19, this.capturedStack2.copy());
      		}
			else
			{
				this.dataWatcher.updateObject(19, this.capturedStack2);
			}
			break;
		case 2:
			this.capturedStack3=itemStack;
			if(this.capturedStack3!=null)
      		{
				this.dataWatcher.updateObject(20, this.capturedStack3.copy());
      		}
			else
			{
				this.dataWatcher.updateObject(20, this.capturedStack3);
			}
			break;
		case 3:
			this.capturedStack4=itemStack;
			if(this.capturedStack4!=null)
      		{
				this.dataWatcher.updateObject(21, this.capturedStack4.copy());
      		}
			else
			{
				this.dataWatcher.updateObject(21, this.capturedStack4);
			}
			break;
		default:
			break;
		}
	}
	
	private void addItemStackSlotContent(int num, ItemStack itemStack)
	{
		switch(num)
		{
		case 0:
			this.capturedStack.stackSize+=itemStack.stackSize;
			break;
		case 1:
			this.capturedStack2.stackSize+=itemStack.stackSize;
			break;
		case 2:
			this.capturedStack3.stackSize+=itemStack.stackSize;
			break;
		case 3:
			this.capturedStack4.stackSize+=itemStack.stackSize;
			break;
		default:
			break;
		}
	}
	
	@Override
    protected boolean canAdd(ItemStack stack) 
    {
    	for(int i=0;i<4;i++)
    	{
    		if(getItemStackNum(i)==null)
    		{
    			return true;
    		}
    		else
    		{
    			if(getItemStackNum(i).stackSize<getItemStackNum(i).getMaxStackSize() && getItemStackNum(i).getItem()==stack.getItem() && getItemStackNum(i).getItemDamage()==stack.getItemDamage())
    			{
    				return true;
    			}
    		}
    	}
		return false;
	}
    
	@Override
    protected void harvest(EntityItem eItem)
    {
    	for(int i=0;i<4;i++)
    	{
    		if(getItemStackNum(i)==null)
    		{
				this.setItemStackSlotContent(i, eItem.getEntityItem());
				if(!this.worldObj.isRemote)eItem.setDead();
				this.target=null;
          		if(this.capturedStack!=null)
          		{
          			this.dataWatcher.updateObject(18+i, getItemStackNum(i).copy());
          			this.lastItemX=this.posX;
          			this.lastItemY=this.posY;
          			this.lastItemZ=this.posZ;
          		}
          		return;
			}
			else if(getItemStackNum(i).getItem() == eItem.getEntityItem().getItem() && getItemStackNum(i).getItemDamage() == eItem.getEntityItem().getItemDamage())
			{
				addItemStackSlotContent(i,eItem.getEntityItem());
				if(!this.worldObj.isRemote)eItem.setDead();
				this.target=null;
     			this.dataWatcher.updateObject(18+i, getItemStackNum(i).copy());
     			this.lastItemX=this.posX;
     			this.lastItemY=this.posY;
     			this.lastItemZ=this.posZ;
     			return;
			}
    	}
    }
}
