package ihl.collector;

import ic2.api.item.ElectricItem;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ihl.IHL3dPathFinder;
import ihl.IHLMod;
import ihl.IHLModInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CollectorEntity extends EntityFlying {
	protected int energy = 300;
	private int maxEnergy = 1000;
	public EntityItem target;
	public TileEntity hopper;
	public TileEntity charger;
    protected double lastItemX=0;
    protected double lastItemY=0;
    protected double lastItemZ=0;
    protected ItemStack capturedStack;
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private IHL3dPathFinder pathFinder3D;
    private int stuckCooldown=0;
    private boolean clientSideNeedEnergyUpdate = true;
    public int hopperx;
    public int hoppery;
    public int hopperz;
	protected int energyConsume=1;
	protected Item thisItem=IHLMod.collectorItem;
	protected List<EntityItem> eItemIgnoreList = new ArrayList();
	public Tasks currentTask=Tasks.IDLE;
	private int teUpdateTimer = 0;
	private double[] last5TickPosX = new double[5];
	private double[] last5TickPosY = new double[5];
	private double[] last5TickPosZ = new double[5];

    public CollectorEntity(World par1World)
    {
        super(par1World);
        //this.preventEntitySpawning = true;
        this.setSize(0.9F, 0.25F);
        this.pathFinder3D=new IHL3dPathFinder(this);
        this.isImmuneToFire=true;
		this.waypointX=this.posX;
		this.waypointY=this.posY;
		this.waypointZ=this.posZ;
		this.lastItemX=this.posX;
		this.lastItemY=this.posY;
		this.lastItemZ=this.posZ;
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0D);
    }

    @Override
	protected void entityInit()
    {
    	super.entityInit();
    	this.dataWatcher.addObject(16, Integer.valueOf(this.maxEnergy));
    	this.dataWatcher.addObject(17, Integer.valueOf(this.energy));
    	this.dataWatcher.addObjectByDataType(18, 5);
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    @Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return par1Entity.boundingBox;
    }
    
    /**
     * returns the bounding box for this entity
     */
    @Override
	public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
	public boolean canBePushed()
    {
        return true;
    }

    public CollectorEntity(World par1World, double par2, double par4, double par6)
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
		this.waypointX=this.posX;
		this.waypointY=this.posY;
		this.waypointZ=this.posZ;
    }


    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
	public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    @Override
	public void onUpdate()
    {
        super.onUpdate();
        if(teUpdateTimer<120)
        {
        	teUpdateTimer++;
        }
        else
        {
        	teUpdateTimer=0;
        }
        	if(teUpdateTimer % 5 ==0)
        	{
           	this.last5TickPosX[0]=this.posX;
           	this.last5TickPosY[0]=this.posY;
           	this.last5TickPosZ[0]=this.posZ;
        	}
        	else
        	{
               	this.last5TickPosX[teUpdateTimer % 5]=this.last5TickPosX[teUpdateTimer % 5-1];
               	this.last5TickPosY[teUpdateTimer % 5]=this.last5TickPosY[teUpdateTimer % 5-1];
               	this.last5TickPosZ[teUpdateTimer % 5]=this.last5TickPosZ[teUpdateTimer % 5-1];
        	}
        if(this.worldObj.isRemote && this.clientSideNeedEnergyUpdate)
        {
        	this.energy=this.dataWatcher.getWatchableObjectInt(17);
        	this.maxEnergy=this.dataWatcher.getWatchableObjectInt(16);
        	this.clientSideNeedEnergyUpdate=false;
        }
        if(this.energy>=1)
        {
        	this.energy-=this.energyConsume;
        }
        else if(!this.worldObj.isRemote)
        {
           	this.dropAsItem();
        }
        if(this.charger==null && teUpdateTimer==20)
        {
			List<TileEntity> teList = this.worldObj.loadedTileEntityList;
			Iterator teIterator=teList.iterator();
			while(teIterator.hasNext())
			{
				TileEntity te = (TileEntity) teIterator.next();
				if(!te.isInvalid() && this.charger==null && (te instanceof ChargerEjectorTileEntity || te instanceof TileEntityElectricBlock || te instanceof TileEntityBaseGenerator))
				{
					double dist1 =this.getDistanceSq(te.xCoord,te.yCoord,te.zCoord);
					if(dist1<4096D)
					{
						if(te instanceof TileEntityElectricBlock)
						{
							TileEntityElectricBlock charger1 = (TileEntityElectricBlock) te;
							if(charger1.chargeSlot.isEmpty())
							{
								this.charger=charger1;
							}
						}
						else if(te instanceof TileEntityBaseGenerator)
						{
							TileEntityBaseGenerator charger1 = (TileEntityBaseGenerator) te;
							if(charger1.chargeSlot.isEmpty())
							{
								this.charger=charger1;
							}
						}
						else if(te instanceof ChargerEjectorTileEntity)
						{
							ChargerEjectorTileEntity charger1 = (ChargerEjectorTileEntity) te;
							if(charger1.chargeSlot.getFirstEmptyStack()>=0)
							{
								this.charger=charger1;
							}
						}
					}
				}
				else if(!te.isInvalid() && this.charger!=null && (te instanceof ChargerEjectorTileEntity || te instanceof TileEntityElectricBlock || te instanceof TileEntityBaseGenerator))
				{
					double dist1 =this.getDistanceSq(this.charger.xCoord,this.charger.yCoord,this.charger.zCoord);
					double dist2 =this.getDistanceSq(te.xCoord,te.yCoord,te.zCoord);
					if(this.charger instanceof ChargerEjectorTileEntity)
					{
						if(te instanceof ChargerEjectorTileEntity)
						{
							ChargerEjectorTileEntity charger1 = (ChargerEjectorTileEntity) te;
							if(dist2<dist1 && charger1.chargeSlot.getFirstEmptyStack()>=0)
							{
								this.charger=charger1;
							}
						}
					}
					else
					{
						if(te instanceof TileEntityElectricBlock)
						{
							TileEntityElectricBlock charger1 = (TileEntityElectricBlock) te;
							if(dist2<dist1 && charger1.chargeSlot.isEmpty())
							{
								this.charger=charger1;
							}
						}
						else if(te instanceof TileEntityBaseGenerator)
						{
							TileEntityBaseGenerator charger1 = (TileEntityBaseGenerator) te;
							if(dist2<dist1 && charger1.chargeSlot.isEmpty())
							{
								this.charger=charger1;
							}
						}
						else if(te instanceof ChargerEjectorTileEntity)
						{
							ChargerEjectorTileEntity charger1 = (ChargerEjectorTileEntity) te;
							if(dist2<dist1 && charger1.chargeSlot.getFirstEmptyStack()>=0)
							{
								this.charger=charger1;
							}
						}
						
					}
				}
			}

        }
        
		if(!this.worldObj.isRemote && hopperx!=0 || hoppery!=0 || hopperz!=0)
		{
			TileEntity te = this.worldObj.getTileEntity(hopperx, hoppery, hopperz);
			if(te!=null && te instanceof IInventory)
			{
				this.hopper=te;
			}
			hopperx=0;
			hoppery=0;
			hopperz=0;
		}
   		if(this.hopper==null && teUpdateTimer==10)
		{
			List<TileEntity> teList = this.worldObj.loadedTileEntityList;
			Iterator teIterator=teList.iterator();
			while(teIterator.hasNext())
			{
				TileEntity te = (TileEntity) teIterator.next();
				if(this.hopper==null && te instanceof IInventory)
				{
					this.hopper=te;
				}
				else if(te instanceof IInventory)
				{
					double dist1 =this.getDistanceSq(this.hopper.xCoord,this.hopper.yCoord,this.hopper.zCoord);
					double dist2 =this.getDistanceSq(te.xCoord,te.yCoord,te.zCoord);
					if(dist2<dist1)
					{
						this.hopper=te;
					}
				}
			}
		}
    }

    @Override
	public void readEntityFromNBT(NBTTagCompound nbt)
    {
    	try{
    		super.readEntityFromNBT(nbt);
    		this.energy = nbt.getInteger("energy");
    		this.maxEnergy = nbt.getInteger("maxEnergy");
    		this.hopperx = nbt.getInteger("hopperx");
    		this.hoppery = nbt.getInteger("hoppery");
    		this.hopperz = nbt.getInteger("hopperz");
   			NBTTagList var2 = nbt.getTagList("Items", 10);
   			if(var2!=null)
   			{
   				NBTTagCompound var4 = var2.getCompoundTagAt(0);
        		this.capturedStack = ItemStack.loadItemStackFromNBT(var4);
        		if(this.capturedStack!=null)
        		{
        			this.dataWatcher.updateObject(18, this.capturedStack.copy());
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
    	try{
    		super.writeEntityToNBT(nbt);
    		nbt.setInteger("energy", this.energy);
    		nbt.setInteger("maxEnergy", this.maxEnergy);
    		if(hopper!=null)
    		{
        		nbt.setInteger("hopperx", hopper.xCoord);
        		nbt.setInteger("hoppery", hopper.yCoord);
        		nbt.setInteger("hopperz", hopper.zCoord);
    		}
    		else
    		{
        		nbt.setInteger("hopperx", hopperx);
        		nbt.setInteger("hoppery", hoppery);
        		nbt.setInteger("hopperz", hopperz);
    		}
    		if(this.capturedStack!=null)
    		{
    			NBTTagList var2 = new NBTTagList();
    			NBTTagCompound var4 = new NBTTagCompound();
    			var4.setByte("Slot", (byte)0);
    			this.capturedStack.writeToNBT(var4);
    			var2.appendTag(var4);
    			nbt.setTag("Items", var2);
    		}
    	}
    	catch(Throwable e)
    	{
    		e.printStackTrace();
    	}
    }
  
    @Override
	public float getShadowSize()
    {
        return 0.5F;
    }
    
    @Override
    protected String getHurtSound()
    {
        return IHLModInfo.MODID+":collectorHurt";
    }
    
    @Override
    protected String getDeathSound()
    {
        return IHLModInfo.MODID+":collectorHurt";
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
    	return super.attackEntityFrom(par1DamageSource, par2);
    }
    
    @Override
	protected void updateEntityActionState()
    {
        double var1 = this.waypointX - this.posX;
        double var3 = this.waypointY - this.posY;
        double var5 = this.waypointZ - this.posZ;
        double var7 = var1 * var1 + var3 * var3 + var5 * var5;
    	if(currentTask==Tasks.IDLE)
    	{
    		if(teUpdateTimer>=12)
    		{
    			if(this.randomWaypoints())
    			{
            		currentTask=Tasks.SEARCH_ITEM;
            		var7 = 0.5D;
            	}
    		}
    		return;
    	}
    	
        if(var7 < 1.0D || var7 > 3600.0D)
        {
        	if(var7 < 1.0D)
        	{
        		 if(this.energy<1000*this.energyConsume)
        		 {
        			 currentTask=Tasks.RECHARGE;
        		 }
        		switch(currentTask)
        		{
        			case RECHARGE:
        				this.rechargeMe();
        		        if(this.charger!=null)
        		        {
        		        	if(this.pathFinder3D.findPath(this.charger.xCoord+0.5D,this.charger.yCoord+1.5D, this.charger.zCoord+0.5D))
        		        	{
        		            	this.waypointX=this.pathFinder3D.waypointX;
        		            	this.waypointY=this.pathFinder3D.waypointY;
        		            	this.waypointZ=this.pathFinder3D.waypointZ;
        		        	}
        		        	else
        		        	{
        		        		this.charger=null;
        		        		this.randomWaypoints();
        		        	}
        		        }
        				break;
        			case SEARCH_ITEM:
        				this.harvest();
        				List<EntityItem> eItemsList=this.getEItemsList();
        	        	if(this.target==null)
        	        	{
        	        		if(eItemsList.isEmpty())
        	        		{
        	        			this.lastItemX=this.posX;
        	        			this.lastItemY=this.posY;
        	        			this.lastItemZ=this.posZ;
        	        		}
        	        		else
        	        		{
        	            	Iterator ei = eItemsList.iterator();
        	            	while(ei.hasNext())
        	            	{
        	            		EntityItem newTarget=(EntityItem) ei.next();
        	            		if(!newTarget.isDead && newTarget.getEntityItem()!=null && newTarget.getEntityItem().stackSize>0 && this.canAdd(newTarget.getEntityItem()) && !this.eItemIgnoreList.contains(newTarget))
        	            		{
        	            			if(this.pathFinder3D.findPath(newTarget.posX, newTarget.posY+1.0D, newTarget.posZ))
        	            			{
            	            			this.target=newTarget;
                		            	this.waypointX=this.pathFinder3D.waypointX;
                		            	this.waypointY=this.pathFinder3D.waypointY;
                		            	this.waypointZ=this.pathFinder3D.waypointZ;
            	            			break;
        	            			}
        	            			else
        	            			{
        	            				this.eItemIgnoreList.add(newTarget);
        	            			}
        	            		}
        	            	}
        	        		}
        	        	}
        	        	else
        	        	{
        	        		if(this.target.isDead)
        	        		{
        	        			this.target=null;
        	        		}
        	        		else if(this.pathFinder3D.findPath(target.posX, target.posY+1.0D, target.posZ))
	            			{
        		            	this.waypointX=this.pathFinder3D.waypointX;
        		            	this.waypointY=this.pathFinder3D.waypointY;
        		            	this.waypointZ=this.pathFinder3D.waypointZ;
    	            			break;
	            			}
	            			else
	            			{
	            				this.eItemIgnoreList.add(target);
	            				this.target=null;
	            			}
        	        	}
        	        	if(this.target==null && this.capturedStack!=null)
        	        	{
        	        		currentTask=Tasks.DROP_ITEM;
        					this.waypointX=this.posX;
        					this.waypointY=this.posY;
        					this.waypointZ=this.posZ;
        	        	}
        	        	else if(this.target==null)
        	        	{
        	        		this.randomWaypoints();
        	        	}
        				break;
        			case DROP_ITEM:
        				this.dropItem();
        				if(this.hopper!=null && this.capturedStack!=null)
        		    	{
        		        	this.pathFinder3D.findPath(this.hopper.xCoord,this.hopper.yCoord+2.0D, this.hopper.zCoord);
        		        	this.waypointX=this.pathFinder3D.waypointX;
        		        	this.waypointY=this.pathFinder3D.waypointY;
        		        	this.waypointZ=this.pathFinder3D.waypointZ;
        		    	}
        				else
        				{
        					currentTask=Tasks.SEARCH_ITEM;
        					this.waypointX=this.posX;
        					this.waypointY=this.posY;
        					this.waypointZ=this.posZ;
        				}
        				break;
        			default:
        				break;
        		}
        	}
        }
        if(this.isEntityInsideOpaqueBlock())
        {
        	this.noClip=true;
        	int xyz[] = {0,0,1,0,0,-1,0,0};
        	Block block;
        	int x,y,z;
        	for(int i=0;i<=5;i++)
			{
        		x=MathHelper.floor_double(this.posX)+xyz[i];
        		y=MathHelper.floor_double(this.posY)+xyz[i+1];
        		z=MathHelper.floor_double(this.posZ)+xyz[i+2];
				block = this.worldObj.getBlock(x,y,z);
				if(block.isAir(this.worldObj, x,y,z))
				{
					double varx=x+0.5D;
					double vary=y+0.5D;
					double varz=z+0.5D;
					this.setPosition(varx, vary, varz);
					this.setHealth(this.getMaxHealth());
					this.noClip=false;
					break;
				}
			}
        }

        if(var7>0.01D)
        {
        	var7 = MathHelper.sqrt_double(var7);
        	this.motionX += var1 / var7 * 0.02D;
        	this.motionY += var3 / var7 * 0.02D;
        	this.motionZ += var5 / var7 * 0.02D;
        }
        if (this.courseChangeCooldown-- <= 0)
        {
            this.courseChangeCooldown += this.rand.nextInt(5) + 2;

        }
        float rYaw=-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI;
        float rYawD=this.rotationYaw-rYaw;
        this.renderYawOffset = this.rotationYaw -= rYawD*0.1F;
    }

    protected boolean canAdd(ItemStack stack) 
    {
    	if(this.capturedStack==null)
    	{
    		return true;
    	}
    	else
    	{
    		if(this.capturedStack.stackSize<this.capturedStack.getMaxStackSize())
    		{
    			return this.capturedStack.getItem()==stack.getItem() && this.capturedStack.getItemDamage()==stack.getItemDamage();
    		}
    	}
		return false;
	}

	/**
     * True if the ghast has an unobstructed line of travel to the waypoint.
     */
    private boolean isCourseTraversable(double par1, double par3, double par5, double par7)
    {
        double var9 = (par1 - this.posX) / par7;
        double var11 = (par3 - this.posY) / par7;
        double var13 = (par5 - this.posZ) / par7;
        AxisAlignedBB var15 = this.boundingBox.copy();

        for (int var16 = 1; var16 < par7; ++var16)
        {
            var15.offset(var9, var11, var13);

            if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty())
            {
                return false;
            }
        }

        return true;
    }


	public int getStored() 
	{
		return this.energy;
	}

	public void setStored(int value) 
	{
		this.energy=value;
	}
	
    private double mov(double motion)
    {
    	double max=0.1D;
    	if(motion>max)
    	{
    		return max;
    	}
    	else if(motion<-max)
    	{
    		return -max;
    	}
    	else
    	{
    		return 0D;
    	}
    }
    
    public ItemStack getVisibleItemStack()
    {
    	return this.dataWatcher.getWatchableObjectItemStack(18);
    }
    
    private void harvest()
    {
        double range = 1D;
        AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.posX-range,this.posY-range-1D,this.posZ-range,this.posX+range,this.posY+range,this.posZ+range);
  		List<EntityItem> eItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, searchArea);
        if(eItemsList!=null && eItemsList.size()>0)
           	{
           		Iterator itemIterator = eItemsList.iterator();
           		while(itemIterator.hasNext())
           		{
           			EntityItem eItem = (EntityItem) itemIterator.next();
           			if(eItem.getEntityItem()!=null && eItem.getEntityItem().stackSize>0)
           			{
           				if(this.canAdd(eItem.getEntityItem()))
           				{
           					this.harvest(eItem);
           				}
           			}
           			else if(eItem.getEntityItem().stackSize<=0)
           			{
           				if(!this.worldObj.isRemote)eItem.setDead();
           			}
           			else if(eItem.getEntityItem()==null)
           			{
           				if(!this.worldObj.isRemote)eItem.setDead();
           			}
           		}
          	}
    }
    
    protected void dropItem()
    {
    	if(this.hopper!=null && this.capturedStack!=null)
    	{
    		double var1 = this.hopper.xCoord - this.posX;
        	double var3 = this.hopper.yCoord - this.posY + 2D;
        	double var5 = this.hopper.zCoord - this.posZ;
        	double var7 = var1 * var1 + var3 * var3 + var5 * var5;
        	if (var7 < 2.0D)
        	{
        		if(this.addItemStackToInventory((IInventory)this.hopper, this.capturedStack))
        		{
        			this.capturedStack=null;
        			this.dataWatcher.updateObject(18, this.capturedStack);
        		}
        	}
    	}
    }
    
    private void rechargeMe()
    {
    	if(this.charger!=null)
    	{
    		double var1 = this.charger.xCoord - this.posX;
    		double var3 = this.charger.yCoord - this.posY + 2D;
    		double var5 = this.charger.zCoord - this.posZ;
    		double var7 = var1 * var1 + var3 * var3 + var5 * var5;
    		if (var7 < 2.0D)
    		{
    			if(this.charger instanceof TileEntityElectricBlock)
    			{
    				InvSlotCharge chargeSlot =((TileEntityElectricBlock) this.charger).chargeSlot;
        			if(chargeSlot.isEmpty())
        			{
        		    	ItemStack itemStack = this.getItemStack(this.energy);
        		    	if(this.hopper!=null)
        		    	{
        		    		int[] hopperxyz = {this.hopper.xCoord,this.hopper.yCoord,this.hopper.zCoord};
        		    		if(itemStack.stackTagCompound==null)
        		    		{
        		    			itemStack.stackTagCompound=new NBTTagCompound();
        		    		}
        		    		itemStack.stackTagCompound.setIntArray("hopperxyz", hopperxyz);
        		    	}

        				chargeSlot.put(itemStack);
        		    	if(this.capturedStack!=null)
        		    	{
        		        	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.capturedStack));
        		    	}
        				this.setDead();
        			}
        			else
        			{
        				this.charger=null;
        			}
    			}
    			else if(this.charger instanceof TileEntityBaseGenerator)
    			{
    				InvSlotCharge chargeSlot =((TileEntityBaseGenerator) this.charger).chargeSlot;
        			if(chargeSlot.isEmpty())
        			{
        		    	ItemStack itemStack = this.getItemStack(this.energy);
        		    	if(this.hopper!=null)
        		    	{
        		    		int[] hopperxyz = {this.hopper.xCoord,this.hopper.yCoord,this.hopper.zCoord};
        		    		if(itemStack.stackTagCompound==null)
        		    		{
        		    			itemStack.stackTagCompound=new NBTTagCompound();
        		    		}
        		    		itemStack.stackTagCompound.setIntArray("hopperxyz", hopperxyz);
        		    	}

        				chargeSlot.put(itemStack);        		    	
        				if(this.capturedStack!=null)
        		    	{
        		        	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.capturedStack));
        		    	}
        				this.setDead();
        			}
        			else
        			{
        				this.charger=null;
        			}
    			}
    			else if(this.charger instanceof ChargerEjectorTileEntity)
    			{
    				InvSlotMultiCharge chargeSlot =((ChargerEjectorTileEntity) this.charger).chargeSlot;
        			if(chargeSlot.isEmpty())
        			{
        		    	ItemStack itemStack = this.getItemStack(this.energy);
        		    	if(this.hopper!=null)
        		    	{
        		    		int[] hopperxyz = {this.hopper.xCoord,this.hopper.yCoord,this.hopper.zCoord};
        		    		if(itemStack.stackTagCompound==null)
        		    		{
        		    			itemStack.stackTagCompound=new NBTTagCompound();
        		    		}
        		    		itemStack.stackTagCompound.setIntArray("hopperxyz", hopperxyz);
        		    	}
        				chargeSlot.put(itemStack);
        				spawnContainedItems();
        				this.setDead();
        			}
        			else
        			{
        				this.charger=null;
        			}
    			}
    			else
    			{
    				this.charger=null;
    				return;
    			}
    		}
    	}
    }
    
    private boolean randomWaypoints()
    {
    	double wx,wy,wz;
		if(this.hopper!=null)
		{
	        double var1 = this.hopper.xCoord - this.posX;
	        double var3 = this.hopper.yCoord - this.posY + 1.5D;
	        double var5 = this.hopper.zCoord - this.posZ;
	        double var7 = var1 * var1 + var3 * var3 + var5 * var5;
	        if(var7>4096D)
	        {
		        wx = (double)this.hopper.xCoord + (double)(this.rand.nextFloat() * 8.0F - 4.0F);
		        wy = (double)this.hopper.yCoord + (double)(this.rand.nextFloat() * 8.0F - 4.0F);
		        wz = (double)this.hopper.zCoord + (double)(this.rand.nextFloat() * 8.0F - 4.0F);
	        }
	        else if(var3>16D)
	        {
		        wx = this.posX + (this.rand.nextFloat() * 8.0F - 4.0F);
		        wy = (double)this.hopper.yCoord + (double)(this.rand.nextFloat() * 8.0F - 4.0F);
		        wz = this.posZ + (this.rand.nextFloat() * 8.0F - 4.0F);
	        }
	        else
	        {
		        wx = this.posX + (this.rand.nextFloat() * 8.0F - 4.0F);
		        wy = this.posY + (this.rand.nextFloat() * 8.0F - 4.0F);
		        wz = this.posZ + (this.rand.nextFloat() * 8.0F - 4.0F);
	        }
		}
		else
		{
	        wx = this.posX + (this.rand.nextFloat() * 8.0F - 4.0F);
	        wy = this.posY + (this.rand.nextFloat() * 2.0F - 1.0F);
	        wz = this.posZ + (this.rand.nextFloat() * 8.0F - 4.0F);
		}
    	if(this.pathFinder3D.findPath(wx, wy, wz))
    	{
        	this.waypointX=this.pathFinder3D.waypointX;
        	this.waypointY=this.pathFinder3D.waypointY;
        	this.waypointZ=this.pathFinder3D.waypointZ;
			return true;
		}
		else
		{
			return false;
		}
    }
    

    protected boolean addItemStackToInventory(IInventory hopper, ItemStack stack)
	{
    	int startFrom=0;
    	int endWith=hopper.getSizeInventory()-1;
		if(IHLMod.isGregTechModLoaded && IHLMod.isGT_API_Version_5 && hopper instanceof gregtech.api.metatileentity.BaseMetaTileEntity)
		{
			startFrom=endWith=4;
		}
		for(int i=startFrom;i<=endWith;++i)
		{
			if(hopper.isItemValidForSlot(i, stack))
			{
			ItemStack hopperSlot = hopper.getStackInSlot(i);
			if(hopperSlot!=null && hopperSlot.stackSize<hopper.getInventoryStackLimit() && hopperSlot.getItem() == stack.getItem() && hopperSlot.getItemDamage() == stack.getItemDamage())
			{
				if(hopperSlot.stackSize + stack.stackSize<=hopper.getInventoryStackLimit())
				{
					hopperSlot.stackSize+=stack.stackSize;
					return true;
				}
				else
				{
					int newSize=hopper.getInventoryStackLimit()-hopperSlot.stackSize;
					hopperSlot.stackSize=hopper.getInventoryStackLimit();
					stack.stackSize-=newSize;
				}
			}
			else if(hopperSlot==null)
			{
				if(stack.stackSize<=hopper.getInventoryStackLimit())
				{
					hopper.setInventorySlotContents(i, stack);
					return true;
				}
				else
				{
					ItemStack stackAdd = stack.splitStack(hopper.getInventoryStackLimit());
					hopper.setInventorySlotContents(i, stackAdd);
				}
			}
			}
		}
		return false;
	}
    
    public float getRenderEnergyLevel()
    {
    	return (float)this.energy/(float)this.maxEnergy;
    }
    
    public void setEnergy(double d, double e)
    {
    	if(e<Integer.MAX_VALUE)
    	{
    	   	this.maxEnergy=(int) e;
    	}
    	else
    	{
    		this.maxEnergy=Integer.MAX_VALUE;
    	}
    	
    	if(d<this.maxEnergy)
    	{
        	this.energy=(int) d;
    	}
    	else
    	{
        	this.energy=this.maxEnergy;
    	}
    	this.dataWatcher.updateObject(17, Integer.valueOf(this.energy));
    	this.dataWatcher.updateObject(16, Integer.valueOf(this.maxEnergy));
    }
    
    @Override
    public boolean interact(EntityPlayer player)
    {
        if(player.inventory.getCurrentItem()==null && !this.worldObj.isRemote)
        {
        	this.dropAsItem();
            return true;
        }
		return false;
    }
    
    
    protected void dropAsItem()
    {	
    	ItemStack itemStack = this.getItemStack(this.energy);
    	if(this.hopper!=null)
    	{
    		int[] hopperxyz = {this.hopper.xCoord,this.hopper.yCoord,this.hopper.zCoord};
    		if(itemStack.stackTagCompound==null)
    		{
    			itemStack.stackTagCompound=new NBTTagCompound();
    		}
    		itemStack.stackTagCompound.setIntArray("hopperxyz", hopperxyz);
    	}
    	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemStack));
    	spawnContainedItems();
    	this.setDead();
    }
    
    protected List<EntityItem> getEItemsList()
    {
        double range = 32D;
        AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.lastItemX-range,this.lastItemY-range,this.lastItemZ-range,this.lastItemX+range,this.lastItemY+range,this.lastItemZ+range);
        List<EntityItem> eItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, searchArea);
    	return eItemsList;
    }
    
    public ItemStack getItemStack(int charge)
    {
        ItemStack ret = new ItemStack(this.thisItem);
        ElectricItem.manager.charge(ret, charge, this.maxEnergy, true, false);
        return ret;
    }
    
    public enum Tasks
    {
    	RECHARGE,
    	SEARCH_ITEM,
    	DROP_ITEM,
    	IDLE
    }
    
    protected void spawnContainedItems()
    {
    	if(this.capturedStack!=null)
    	{
        	this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.capturedStack));
    	}
    }
    
    protected void harvest(EntityItem eItem)
    {
			if(this.capturedStack==null)
			{
				this.capturedStack=eItem.getEntityItem();
				if(!this.worldObj.isRemote)eItem.setDead();
				this.target=null;
          		if(this.capturedStack!=null)
          		{
          			this.dataWatcher.updateObject(18, this.capturedStack.copy());
    			this.lastItemX=this.posX;
    			this.lastItemY=this.posY;
    			this.lastItemZ=this.posZ;
          		}
			}
			else if(this.capturedStack.getItem() == eItem.getEntityItem().getItem() && this.capturedStack.getItemDamage() == eItem.getEntityItem().getItemDamage())
			{
				this.capturedStack.stackSize+=eItem.getEntityItem().stackSize;
				if(!this.worldObj.isRemote)eItem.setDead();
				this.target=null;
     			this.dataWatcher.updateObject(18, this.capturedStack.copy());
			this.lastItemX=this.posX;
			this.lastItemY=this.posY;
			this.lastItemZ=this.posZ;
			}
    }
    
}