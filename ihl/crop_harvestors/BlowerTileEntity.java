package ihl.crop_harvestors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.IC2Items;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import ihl.IHLMod;
import ihl.utils.IHLInvSlotDischarge;

public class BlowerTileEntity extends TileEntityInventory implements IEnergySink, IHasGui, IUpgradableBlock
{
    private int startUpCounter=0;
    private int tier=1;
    private int defaultTier=1;
    public int maxStorage=100;
    private int defaultMaxStorage=100;
    private double energy=0D;
    private double defaultEnergyConsume=5D;
    private double energyConsume=5D;
    private int defaultAirSpeedBase=100;
    public int airSpeedBase=100;
    private int sCAFCount=0;
    public boolean addedToEnergyNet = false;
    private ArrayList<AirSpeedZone> aszlist = new ArrayList<AirSpeedZone>();
    public final InvSlotUpgrade upgradeSlot;
    public final IHLInvSlotDischarge dischargeSlot;
	public int updateChecksum=-1;
    private final float lambda=0.00001F;
    public int operationRange=-1;
    public int lastOperationRange=0;
    
    private AudioSource startAS;
    private AudioSource loopAS;
    private AudioSource stopAS;
	
    public BlowerTileEntity()
    {
    	this.defaultTier=IHLMod.config.blowerTier;
    	this.defaultMaxStorage=IHLMod.config.blowerMaxEnergyStorage;
    	this.defaultEnergyConsume=IHLMod.config.blowerEnergyConsumePerTick;
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 2, 4);
        this.dischargeSlot = new IHLInvSlotDischarge(this, 1, Access.IO, this.tier, InvSlot.InvSide.BOTTOM);
    }
    
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("tier");
		fields.add("airSpeedBase");
		fields.add("maxStorage");
		return fields;
    }
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        try
        {
            this.energy = nbttagcompound.getDouble("energy");
        }
        catch (Exception var3)
        {
            this.energy = nbttagcompound.getInteger("energy");

            if (this.maxStorage > Integer.MAX_VALUE)
            {
                this.energy *= 10.0D;
            }
        }
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
    }
    
    @Override
	public void onLoaded()
    {
        super.onLoaded();
        if (IC2.platform.isSimulating())
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
        if (IC2.platform.isRendering() && (this.startAS==null||this.loopAS==null||this.stopAS==null))
        {
        	this.startAS = IC2.audioManager.createSource(this, PositionSpec.Center, this.getStartSoundFile(),false,false, 1F);
        	this.loopAS = IC2.audioManager.createSource(this, PositionSpec.Center, this.getLoopSoundFile(),true,false, 1F);
        	this.stopAS = IC2.audioManager.createSource(this, PositionSpec.Center, this.getStopSoundFile(),false,false, 1F);
        }
    }
    
    @Override
	public void onUnloaded()
    {
        if (IC2.platform.isRendering() && this.loopAS != null)
        {
            this.startAS.stop();
			this.loopAS.stop();
			this.stopAS.stop();
            this.startAS = null;
            this.loopAS = null;
            this.stopAS = null;
            IC2.audioManager.removeSources(this);
        }
        
        if (IC2.platform.isSimulating() && this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.onUnloaded();
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=(short)side;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		if(this.dischargeSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.dischargeSlot.get()));
		if(this.upgradeSlot.get(0)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(0)));
		if(this.upgradeSlot.get(1)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(1)));
		if(this.upgradeSlot.get(2)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(2)));
		if(this.upgradeSlot.get(3)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(3)));
		return new ItemStack(IHLMod.blowerBlock,1);
	}
	
	@Override
    public void setFacing(short facing1)
    {
    	super.setFacing(facing1);
    	this.updateChecksum=-1;
    }
    
    public boolean enableUpdateEntity()
    {
        return true;
    }
    
    public String getStartSoundFile()
    {
        return "Machines/IHL Industrial Fan/start.ogg";
    }
    
    public String getLoopSoundFile()
    {
        return "Machines/IHL Industrial Fan/loop.ogg";
    }

    public String getStopSoundFile()
    {
        return "Machines/IHL Industrial Fan/stop.ogg";
    }

    @Override
	public void updateEntityClient()
    {
        this.updateBoth();
        if(IC2.platform.isRendering() && this.loopAS!=null)
        {
        	if(this.getActive())
        	{
        		if(this.startUpCounter>12)
        		{  
        			this.startUpCounter=0;
        		}
        		if(this.startUpCounter==0)
        		{        		
        			this.stopAS.stop();
        			this.startAS.play();
        		}
        		if(this.startUpCounter<10)
        		{
        			this.startUpCounter++;
        		}
        		else if(this.startUpCounter==10)
        		{
        			this.startAS.stop();
        			this.loopAS.play();
        			this.startUpCounter++;
        		}
        	}
        	else if(this.startUpCounter>0)
        	{
        		this.startAS.stop();
        		this.loopAS.stop();
        		this.stopAS.play();
        		if(this.startUpCounter<170)
        		{
        			this.startUpCounter++;
        		}
        		else
        		{
            		this.startUpCounter=0;
            		this.stopAS.stop();
        		}
        	}
        }
        else if(IC2.platform.isRendering() && !this.getActive() && this.loopAS!=null)
        {
        	this.loopAS.stop();
        }
    }
    
    public void updateBoth()
    {
        if(this.dischargeSlot.tier!=this.tier)
        {
        	this.dischargeSlot.tier=this.tier;
        }
    }
    	
    	
    @SuppressWarnings("unchecked")
	@Override
	public void updateEntityServer()
    {
    	
        if (IC2.platform.isSimulating())
        {
            this.setOverclockRates();
        }
        this.updateBoth();
    	if(this.getDemandedEnergy() > 1.0D)
    	{
    		double amount = this.dischargeSlot.discharge(this.getDemandedEnergy(), false);
    		this.energy += amount;
    	}
        if(this.energy>this.maxStorage)
        {
        	this.energy=this.maxStorage;
        }
    	if(IC2.platform.isSimulating()&&!this.getActive()&&this.energy>=this.energyConsume*2D)
    	{
			this.setActive(true);
    	}
    	else if(IC2.platform.isSimulating()&&this.getActive()&&this.energy<this.energyConsume)
    	{
    		this.setActive(false);
    	}
    	if(!this.aszlist.isEmpty() && this.getActive())
    	{
    		if(this.energy>=this.energyConsume)this.energy-=this.energyConsume;
    		Iterator<AirSpeedZone> it = this.aszlist.iterator();
    		a:while(it.hasNext())
    		{
    			AirSpeedZone asz = it.next();
    			List<Entity> entityList = worldObj.getEntitiesWithinAABB(Entity.class, asz.getAABB());
    			if(!entityList.isEmpty())
    			{
					if(this.sCAFCount<=0)
					{
						this.aszlist.clear();
						this.calculateAirSpeed();
						this.sCAFCount=40;
						break a;
					}
    				Iterator<Entity> ei = entityList.iterator();
        			while(ei.hasNext())
        			{
        				Entity ent = ei.next();
        				if(asz.isEntityMoveable(ent))
        				{
        					if(ent instanceof EntityPlayer)
        					{
        						if(worldObj.isRemote)
        						{
        							ent.addVelocity(mX()*0.1D, mY()*0.05D, mZ()*0.1D);
        						}
        					}
        					else
        					{
    							ent.addVelocity(mX()*0.1D, mY()*0.05D, mZ()*0.1D);
        					}
        				}
        			}
    			}
    		}
    		
    	}
    	if(this.sCAFCount>0)
    	{
    		this.sCAFCount--;
    	}
    	else
    	{
    		this.calculateAirSpeed();
    		if(this.operationRange==this.lastOperationRange)
    		{
    			this.sCAFCount=600;
    		}
    		else
    		{
    			this.sCAFCount=20;
    			this.lastOperationRange=this.operationRange;
    		}
    	}
    	

    }
    
    private void calculateAirSpeed()
    {
    	int x=xCoord;
    	int y=yCoord;
    	int z=zCoord;
    	int airSpeed=airSpeedBase;
		int x0,y0,z0;
		x0=x;
		y0=y;
		z0=z;
		int flowExtL=0;
		int flowExtR=0;
		int flowExtT=0;
		int flowExtB=0;
		int flowCanBeExtL=0;
		int flowCanBeExtR=0;
		int flowCanBeExtT=0;
		int flowCanBeExtB=0;
		
		int checkFlowL=0;
		int checkFlowR=0;
		int checkFlowT=0;
		int checkFlowB=0;
		
		checkFlowL = Math.max(
				this.neighborBlowerOperationRange(this.xCoord+mXL()+mX(), this.yCoord+mYL()+mY(),this.zCoord+mZL()+mZ()),
				this.neighborBlowerOperationRange(this.xCoord+mXL()-mX(), this.yCoord+mYL()-mY(), this.zCoord+mZL()-mZ())
				);
		checkFlowR = Math.max(
				this.neighborBlowerOperationRange(this.xCoord-mXL()+mX(), this.yCoord-mYL()+mY(),this.zCoord-mZL()+mZ()),
				this.neighborBlowerOperationRange(this.xCoord-mXL()-mX(), this.yCoord-mYL()-mY(), this.zCoord-mZL()-mZ())
				);
		checkFlowT = Math.max(Math.max(
				this.neighborBlowerOperationRange(this.xCoord+mXT()+mX(), this.yCoord+mYT()+mY(),this.zCoord+mZT()+mZ()),
				this.neighborBlowerOperationRange(this.xCoord+mXT()-mX(), this.yCoord+mYT()-mY(), this.zCoord+mZT()-mZ())
				),
				this.neighborBlowerOperationRange(this.xCoord+mXT(), this.yCoord+mYT(),this.zCoord+mZT()));
		checkFlowB = Math.max(Math.max(
				this.neighborBlowerOperationRange(this.xCoord-mXT()+mX(), this.yCoord-mYT()+mY(),this.zCoord-mZT()+mZ()),
				this.neighborBlowerOperationRange(this.xCoord-mXT()-mX(), this.yCoord-mYT()-mY(), this.zCoord-mZT()-mZ())
				),
				this.neighborBlowerOperationRange(this.xCoord-mXT(), this.yCoord-mYT(),this.zCoord-mZT()));
		int asg = this.getAirSpeedGrade(airSpeed);
		a:for(int step=0;step<256;step++)
    	{
    		x+=mX();
    		y+=mY();
    		z+=mZ();
    		airSpeed-=Math.round(lambda*(airSpeed*airSpeed))+1;
			asg = this.getAirSpeedGrade(airSpeed);
			if(asg==0)
			{
				break;
			}
			int i1,i2,il,ir,it,ib;
			i1=0;i2=0;il=0;ir=0;it=0;ib=0;
    		for(int tb=-flowExtB;tb<=flowExtT;tb++)
    		{
    			i1++;
    			if(this.isAirPassable(x+mXL()*(flowExtL+1)+mXT()*tb,y+mYL()*(flowExtL+1)+mYT()*tb,z+mZL()*(flowExtL+1)+mZT()*tb))
    			{
    				il++;
    			}
    			if(this.isAirPassable(x-mXL()*(flowExtR+1)+mXT()*tb,y-mYL()*(flowExtR+1)+mYT()*tb,z-mZL()*(flowExtR+1)+mZT()*tb))
    			{
    				ir++;
    			}
    		}
			if(i1==il && step>=checkFlowL)
			{
				flowCanBeExtL++;
			}
			if(i1==ir && step>=checkFlowR)
			{
    			flowCanBeExtR++;
			}
    		for(int lr=-flowExtL;lr<=flowExtR;lr++)
    		{
    			i2++;
    			if(this.isAirPassable(x+mXT()*(flowExtT+1)+mXL()*lr,y+mYT()*(flowExtT+1)+mYL()*lr,z+mZT()*(flowExtT+1)+mZL()*lr))
    			{
    				it++;
    			}
    			if(this.isAirPassable(x-mXT()*(flowExtB+1)+mXL()*lr,y-mYT()*(flowExtB+1)+mYL()*lr,z-mZT()*(flowExtB+1)+mZL()*lr))
    			{
    				ib++;
    			}
    		}
    		if(i2==it && step>=checkFlowT)
			{
				flowCanBeExtT++;
			}
			if(i2==ib && step>=checkFlowB)
			{
    			flowCanBeExtB++;
			}
			airSpeed-=Math.round(lambda*(airSpeed*airSpeed)*(il+ir+it+ib))+il+ir+it+ib;
			asg = this.getAirSpeedGrade(airSpeed);
			if(asg==0)
			{
				this.operationRange=step;
				break;
			}
    		if(flowCanBeExtB>=4)
    		{
    			flowCanBeExtB=0;
    			this.aszlist.add(new AirSpeedZone(asg,
    					x0-mXL()*flowExtR-mXT()*flowExtB,
    					y0-mYL()*flowExtR-mYT()*flowExtB,
    					z0-mZL()*flowExtR-mZT()*flowExtB,
    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
    			x0=x;
    			y0=y;
    			z0=z;
    			int s0=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			flowExtB++;
    			int s1=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			airSpeed=airSpeed*s0/s1;
    		}
    		else if(flowCanBeExtT>=4)
    		{
    			flowCanBeExtT=0;
    			this.aszlist.add(new AirSpeedZone(asg,
    					x0-mXL()*flowExtR-mXT()*flowExtB,
    					y0-mYL()*flowExtR-mYT()*flowExtB,
    					z0-mZL()*flowExtR-mZT()*flowExtB,
    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
    			x0=x;
    			y0=y;
    			z0=z;
    			int s0=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			flowExtT++;
    			int s1=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			airSpeed=airSpeed*s0/s1;
    		}
    		else if(flowCanBeExtR>=4)
    		{
    			flowCanBeExtR=0;
    			this.aszlist.add(new AirSpeedZone(asg,
    					x0-mXL()*flowExtR-mXT()*flowExtB,
    					y0-mYL()*flowExtR-mYT()*flowExtB,
    					z0-mZL()*flowExtR-mZT()*flowExtB,
    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
    			x0=x;
    			y0=y;
    			z0=z;
    			int s0=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			flowExtR++;
    			int s1=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			airSpeed=airSpeed*s0/s1;
    		}
    		else if(flowCanBeExtL>=3)
    		{
    			flowCanBeExtL=0;
    			this.aszlist.add(new AirSpeedZone(asg,
    					x0-mXL()*flowExtR-mXT()*flowExtB,
    					y0-mYL()*flowExtR-mYT()*flowExtB,
    					z0-mZL()*flowExtR-mZT()*flowExtB,
    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
    			x0=x;
    			y0=y;
    			z0=z;
    			int s0=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			flowExtL++;
    			int s1=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			airSpeed=airSpeed*s0/s1;
    		}
    		else if(flowCanBeExtR>=3)
    		{
    			flowCanBeExtR=0;
    			this.aszlist.add(new AirSpeedZone(asg,
    					x0-mXL()*flowExtR-mXT()*flowExtB,
    					y0-mYL()*flowExtR-mYT()*flowExtB,
    					z0-mZL()*flowExtR-mZT()*flowExtB,
    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
    			x0=x;
    			y0=y;
    			z0=z;
    			int s0=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			flowExtR++;
    			int s1=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			airSpeed=airSpeed*s0/s1;
    		}
    		else if(flowCanBeExtT>=3)
    		{
       			flowCanBeExtT=0;
    			this.aszlist.add(new AirSpeedZone(asg,
    					x0-mXL()*flowExtR-mXT()*flowExtB,
    					y0-mYL()*flowExtR-mYT()*flowExtB,
    					z0-mZL()*flowExtR-mZT()*flowExtB,
    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
    			x0=x;
    			y0=y;
    			z0=z;
    			int s0=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			flowExtT++;
    			int s1=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			airSpeed=airSpeed*s0/s1;
    		}
    		else if(flowCanBeExtB>=3)
    		{
       			flowCanBeExtB=0;
    			this.aszlist.add(new AirSpeedZone(asg,
    					x0-mXL()*flowExtR-mXT()*flowExtB,
    					y0-mYL()*flowExtR-mYT()*flowExtB,
    					z0-mZL()*flowExtR-mZT()*flowExtB,
    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
    			x0=x;
    			y0=y;
    			z0=z;
    			int s0=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			flowExtB++;
    			int s1=(flowExtR+flowExtL+1)*(flowExtT+flowExtB+1);
    			airSpeed=airSpeed*s0/s1;
    		}
    		for(int tb=-flowExtB;tb<=flowExtT;tb++)
    		{
        		for(int lr=-flowExtR;lr<=flowExtL;lr++)
        		{
        			if(!this.isAirPassable(x+mXL()*lr+mXT()*tb+mX(),y+mYL()*lr+mYT()*tb+mY(),z+mZL()*lr+mZT()*tb+mZ()))
        			{
        				if(lr==0 && tb==0)
        				{
        					//System.out.println("Flow breakened by wall check at step="+step);
        					this.operationRange=step;
        					break a;
        				}
        				else
        				{
        	    			this.aszlist.add(new AirSpeedZone(asg,
        	    					x0-mXL()*flowExtR-mXT()*flowExtB,
        	    					y0-mYL()*flowExtR-mYT()*flowExtB,
        	    					z0-mZL()*flowExtR-mZT()*flowExtB,
        	    					x-mX()+mXL()*flowExtL+mXT()*flowExtT,
        	    					y-mY()+mYL()*flowExtL+mYT()*flowExtT,
        	    					z-mZ()+mZL()*flowExtL+mZT()*flowExtT));
        	    			x0=x;
        	    			y0=y;
        	    			z0=z;
        					if(lr>0)
        					{
        						flowExtL=lr-1;
        					}
        					else if(lr<0)
        					{
        						flowExtR=-1-lr;
        					}
        					if(tb>0)
        					{
        						flowExtT=tb-1;
        					}
        					else if(tb<0)
        					{
        						flowExtB=-1-tb;
        					}
        				}
        			}
        		}
    		}
    		//System.out.println("Airspeed at step="+step+"equals="+airSpeed);
    	}
		if(asg>0)
		{
			this.aszlist.add(new AirSpeedZone(asg,
					x0-mXL()*flowExtR-mXT()*flowExtB,
					y0-mYL()*flowExtR-mYT()*flowExtB,
					z0-mZL()*flowExtR-mZT()*flowExtB,
					x+mXL()*flowExtL+mXT()*flowExtT,
					y+mYL()*flowExtL+mYT()*flowExtT,
					z+mZL()*flowExtL+mZT()*flowExtT));
		}
    }
    
    
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
		switch(direction)
		{
		case UP:
			return mY()!=1;
		case DOWN:
			return mY()!=-1;
		case NORTH:
			return mZ()!=-1 && mZL()==0;
		case EAST:
			return mX()!=1 && mXL()==0;
		case SOUTH:
			return mZ()!=1 && mZL()==0;
		case WEST:
			return mX()!=-1 && mXL()==0;
		default:
			return false;
		}
	}
		
	private boolean isAirPassable(int x,int y,int z)
	{
		Block block = worldObj.getBlock(x,y,z);
		if(block!=null && !block.isNormalCube(worldObj,x,y,z))
		{
			if(
					!block.isCollidable() ||
				block.isAir(worldObj,x, y, z) ||
				block.getBlockBoundsMinX()>0.15D ||
				block.getBlockBoundsMaxX()<0.85D ||
				block.getBlockBoundsMinY()>0.15D ||
				block.getBlockBoundsMaxY()<0.85D ||
				block.getBlockBoundsMinZ()>0.15D ||
				block.getBlockBoundsMaxZ()<0.85D
					)
			{
				return true;
			}
			else
			{
				AxisAlignedBB cb = block.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
				if(cb!=null)
				{
					if(
							cb.maxX<=1D &&
							cb.maxX>=0D &&
							cb.minX<=1D &&
							cb.minX>=0D &&
							cb.maxY<=1D &&
							cb.maxY>=0D &&
							cb.minY<=1D &&
							cb.minY>=0D &&
							cb.maxZ<=1D &&
							cb.maxZ>=0D &&
							cb.minZ<=1D &&
							cb.minZ>=0D
							)
					{
						if(
						cb.maxX<0.85D||
						cb.minX>0.15D||
						cb.maxY<0.85D||
						cb.minY>0.15D||
						cb.maxZ<0.85D||
						cb.minZ>0.15D
							)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private int getAirSpeedGrade(int airSpeed)
	{
		if(airSpeed<30)
		{
			return 0;
		}
		else if(airSpeed<100)
		{
			return 1;
		}
		else if(airSpeed<200)
		{
			return 2;
		}
		else if(airSpeed<300)
		{
			return 3;
		}
		else if(airSpeed<400)
		{
			return 4;
		}
		else if(airSpeed<500)
		{
			return 5;
		}
		else if(airSpeed<600)
		{
			return 6;
		}
		else
		{
			return 7;
		}
	}
	
	private class AirSpeedZone
	{
		private int airSpeedGrade, x0, y0, z0, x1, y1, z1;
		public AirSpeedZone(int airSpeedGrade1, int vx0, int vy0, int vz0, int vx1,int vy1,int vz1)
		{
			this.airSpeedGrade=airSpeedGrade1;
			this.x0=vx0;
			this.y0=vy0;
			this.z0=vz0;
			this.x1=vx1;
			this.y1=vy1;
			this.z1=vz1;
		}
		
		public AxisAlignedBB getAABB()
		{
			return AxisAlignedBB.getBoundingBox(Math.min(x0,x1), Math.min(y0,y1), Math.min(z0,z1), Math.max(x0,x1)+1D, Math.max(y0,y1)+1D, Math.max(z0,z1)+1D);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj  instanceof AirSpeedZone)
			{
				AirSpeedZone asz = (AirSpeedZone)obj;
				return (
						this.x0==asz.x0 && 
						this.y0==asz.y0 && 
						this.z0==asz.z0 && 
						this.x1==asz.x1 && 
						this.y1==asz.y1 && 
						this.z1==asz.z1);
			}
			return false;
		}
		
		 public boolean isEntityMoveable(Entity entity)
		 {
			 if(this.airSpeedGrade>=7)
			 {
				 return true;
			 }
			 else
			 {
				 return (entity.width+1F)*(entity.height+1F)<=this.airSpeedGrade*2;
			 }
		 }
	}

    private int mX()
	{
		switch(this.getFacing())
		{
		case 4:
		return -1;
		case 5:
		return 1;
		default:
		return 0;
		}
	}
    
    private int mY()
	{
		switch(this.getFacing())
		{
		case 0:
		return -1;
		case 1:
		return 1;
		default:
		return 0;
		}
	}
	
    private int mZ()
	{
		switch(this.getFacing())
		{
		case 2:
		return -1;
		case 3:
		return 1;
		default:
		return 0;
		}
	}
	
    private int mXL()
		{
			switch(this.getFacing())
			{
			case 0:
			return -1;
			case 1:
			return 1;
			case 2:
			return -1;
			case 3:
			return 1;
			default:
			return 0;
			}
		}
	    
    private int mYL()
		{
			return 0;
		}
		
    private int mZL()
		{
			switch(this.getFacing())
			{
			case 4:
			return 1;
			case 5:
			return -1;
			default:
			return 0;
			}
		}
		
    private int mXT()
			{
				return 0;
			}
		
    private int mYT()
			{
				switch(this.getFacing())
				{
				case 2:
				return 1;
				case 3:
				return 1;
				case 4:
				return 1;
				case 5:
				return 1;
				default:
				return 0;
				}
			}

    private int mZT()
			{
				switch(this.getFacing())
				{
				case 0:
				return 1;
				case 1:
				return -1;
				default:
				return 0;
				}
			}
		 

@Override
	public String getInventoryName() {
		return "blower";
	}
	
	public int getStored() {
		return Math.round((float)this.energy);
	}
	
	public void setStored(int value) {
		this.energy=value;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer arg0, boolean arg1) {
		return new BlowerGui(new BlowerContainer(arg0, this));
	}
	
    public boolean getGui(EntityPlayer player)
    {
			return this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
    }
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer arg0) {
		return new BlowerContainer(arg0, this);
	}
	@Override
	public void onGuiClosed(EntityPlayer arg0) {}
	
	public void setOverclockRates()
    {
		int speedUp=0;
		int tierUp=0;
		int capacityUp=0;
		int checksum=0;
		for(int i=0;i<this.upgradeSlot.size();i++)
		{
			if(this.upgradeSlot.get(i)!=null)
			{
				if(this.upgradeSlot.get(i).getItemDamage()==IC2Items.getItem("overclockerUpgrade").getItemDamage())
				{
					speedUp+=this.upgradeSlot.get(i).stackSize;
				}
				if(this.upgradeSlot.get(i).getItemDamage()==IC2Items.getItem("transformerUpgrade").getItemDamage())
				{
					tierUp+=this.upgradeSlot.get(i).stackSize;
				}
				if(this.upgradeSlot.get(i).getItemDamage()==IC2Items.getItem("energyStorageUpgrade").getItemDamage())
				{
					capacityUp+=this.upgradeSlot.get(i).stackSize;
				}
			}
		}
		checksum=speedUp*4096+tierUp*64+capacityUp;
		if(this.updateChecksum!=checksum)
		{
			
			if(
				this.isAirPassable(this.xCoord+mXL(),this.yCoord+mYL(),this.zCoord+mZL())&&
				this.isAirPassable(this.xCoord-mXL(),this.yCoord-mYL(),this.zCoord-mZL()))
			{
				this.airSpeedBase=Math.min(3284,(int) (this.defaultAirSpeedBase*Math.pow(1.7D, speedUp)));
			}
			else if(
					!this.isAirPassable(this.xCoord+mXL(),this.yCoord+mYL(),this.zCoord+mZL())&&
					!this.isAirPassable(this.xCoord-mXL(),this.yCoord-mYL(),this.zCoord-mZL()))
				{
					this.airSpeedBase=0;
				}
			else
			{
				this.airSpeedBase=Math.min(3282,(int) (this.defaultAirSpeedBase*Math.pow(1.7D, speedUp)*0.5D));
			}
			this.maxStorage=this.defaultMaxStorage + capacityUp*10000;
	        IC2.network.get().updateTileEntityField(this, "maxStorage");
			this.energyConsume=(int) Math.min(this.defaultEnergyConsume*Math.pow(1.6D, speedUp),this.maxStorage);
			IC2.network.get().updateTileEntityField(this, "airSpeedBase");
			this.tier=this.defaultTier+tierUp;
			IC2.network.get().updateTileEntityField(this, "tier");
	        this.updateChecksum=checksum;
		}
    }
	
    //1.7.10 API
	@Override
	public double getDemandedEnergy()
	{
		 return this.maxStorage - this.energy;
	}
	
	@Override
	public int getSinkTier()
	{
		return this.tier;
	}
	
	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
        if (this.energy  >= this.maxStorage)
        {
            return amount;
        }
        else
        {
            this.energy += amount;
            return 0.0D;
        }
	}
	
	@Override
	public double getEnergy() {
		return this.energy;
	}
	
	@Override
	public boolean useEnergy(double amount) 
	{
        if (this.energy >= amount)
        {
            this.energy -= amount;
            return true;
        }
        else
        {
            return false;
        }
	}
	
	public int neighborBlowerOperationRange(int x,int y,int z)
	{
		if(this.worldObj.getTileEntity(x, y, z)!=null && this.worldObj.getTileEntity(x, y, z) instanceof BlowerTileEntity)
		{
			BlowerTileEntity bte = (BlowerTileEntity) this.worldObj.getTileEntity(x, y, z);
			if(bte.getFacing()==this.getFacing())
			{
				return bte.operationRange;
			}
		}
		return -1;
	}

	public ItemStack getOutput(int arg0) 
	{
		return null;
	}

	public int getOutputSize() {
		return 0;
	}

	public void setOutput(int arg0, ItemStack arg1) {
	}

	@Override
	public Set<UpgradableProperty> getUpgradableProperties() 
	{
		Set<UpgradableProperty> properties = new HashSet<UpgradableProperty>();
		properties.add(UpgradableProperty.Processing);
		properties.add(UpgradableProperty.EnergyStorage);
		properties.add(UpgradableProperty.Transformer);
		return properties;
	}
}