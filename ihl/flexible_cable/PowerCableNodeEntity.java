package ihl.flexible_cable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ic2.core.IC2DamageSource;
import ic2.core.item.armor.ItemArmorHazmat;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.interfaces.IEnergyNetNode;
import ihl.utils.IHLUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class PowerCableNodeEntity extends NodeEntity implements IEnergyNetNode{
	
	private Set<NBTTagCompound> cableList;
	private NBTTagCompound cable;
	private double soundRange=10d;
	private final static float groundConductivity=0.005f;
	private int lastCheckTimer=0;
	
	public PowerCableNodeEntity(World world) 
	{
		super(world);
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return !worldObj.isRemote && !this.noClip;
	}
	
	@Override
	public boolean canBePushed()
	{
		return !worldObj.isRemote && !this.noClip;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if(!worldObj.isRemote)
		{ 
			IHLGrid grid = IHLMod.enet.cablesToGrids.get(this.chainUniqueID);
			if(grid!=null && grid.energy>0d)
			{
				double voltage = grid.getSinkVoltage(this);
				if(this.onGround && voltage>=10d && this.getMaxAllowableVoltage()<voltage)
				{
	                grid.drawEnergy(voltage*voltage*groundConductivity, this);
					if(Math.abs(lastCheckTimer-checkTimer) > 10)
					{
						for(Object player:worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox(this.posX-soundRange,this.posY-soundRange,this.posZ-soundRange, this.posX+soundRange,this.posY+soundRange,this.posZ+soundRange)))
						{
							if(player instanceof EntityPlayerMP)
							{
								EntityPlayerMP playerMP = (EntityPlayerMP)player;
								playerMP.playerNetServerHandler.sendPacket(new S2APacketParticles("largesmoke",(float)this.posX,(float)this.posY,(float)this.posZ,worldObj.rand.nextFloat()*0.1f-0.05f,0.1f,worldObj.rand.nextFloat()*0.1f-0.05f,0.1f,4));
							}
						}
						lastCheckTimer=checkTimer;
					}
				}
				if(this.nextAnchorEntity!=null)
				{
					List<EntityLivingBase> entityList = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(4d, 4d,4d));
					for(EntityLivingBase elb:entityList)
					{
						if(elb.boundingBox!=null)
						{
							if(IHLUtils.isSegmentInsideAABB(elb.boundingBox,this.posX,this.posY,this.posZ,this.nextAnchorEntity.posX,this.nextAnchorEntity.posY,this.nextAnchorEntity.posZ))
							{
								this.applyEntityCollision(elb);
							}
						}
					}
				}
			}
    	}
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		return boundingBox;
	}


	@Override
	public void applyEntityCollision(Entity entity)
	{
		super.applyEntityCollision(entity);
		IHLGrid grid = IHLMod.enet.cablesToGrids.get(this.chainUniqueID);
		if(entity instanceof EntityLivingBase && grid!=null && grid.energy>=1d)
		{
			double voltage = grid.getSinkVoltage(this);
			if(this.getMaxAllowableVoltage()<voltage)
			{
				EntityLivingBase victim=(EntityLivingBase) entity;
	            if (!ItemArmorHazmat.hasCompleteHazmat(victim))
	            {
	            	float damage=(float)Math.min(voltage*groundConductivity,grid.energy*1000d/voltage);
	                victim.attackEntityFrom(IC2DamageSource.electricity, damage);
	                grid.drawEnergy(damage*voltage/1000d, this);
	                if(voltage>1000)
	                {
	                	sendSound();
	                	IHLMod.proxy.spawnParticleFromServer(3,worldObj, (float)this.posX,(float)this.posY,(float)this.posZ,0f,0f,0f,10f);
	                }
	            }
			}
		}
	}
    
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) 
	{
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("cable"))
		{
			this.addCable(nbt.getCompoundTag("cable"));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) 
	{
		super.writeEntityToNBT(nbt);
		if(this.cable!=null)
		{
			nbt.setTag("cable",this.cable);
		}
	}
	
	@Override
	public IHLGrid getGrid() 
	{
		return IHLMod.enet.cablesToGrids.get(this.chainUniqueID);
	}

	@Override
	public int getGridID() 
	{
		return 0;
	}

	@Override
	public void setGrid(int newGridID) {}

	@Override
	public double getMaxAllowableVoltage() 
	{
		if(this.cable!=null)
		{
			return this.cable.getInteger("maxVoltage");
		}
		else
		{
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public boolean addCable(NBTTagCompound cable1) 
	{
		this.cable=cable1;
		return true;
	}

	@Override
	public Set<NBTTagCompound> getCableList() {
		if(cableList==null)
		{
			cableList=new HashSet<NBTTagCompound>(1);
			if(this.cable!=null)
			{
				cableList.add(this.cable);
			}
		}
		return cableList;
	}

	@Override
	public void removeAttachedChains() {}
	
	public void sendSound()
	{
		for(Object player:worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox(this.posX-soundRange,this.posY-soundRange,this.posZ-soundRange, this.posX+soundRange,this.posY+soundRange,this.posZ+soundRange)))
		{
			if(player instanceof EntityPlayerMP)
			{
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				playerMP.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(IHLModInfo.MODID+":electrocution",this.posX,this.posY,this.posZ,1f,1f));
			}
		}
	}

	@Override
	public double[] getPortPos(EntityLivingBase player) 
	{
		return new double[3];
	}
	
	@Override
	public void remove(NBTTagCompound cable) 
	{
		this.cableList.remove(cable);
	}

	@Override
	public boolean isCableRemoved(int chainUniqueID) 
	{
		return false;
	}

	@Override
	public void setCableCheck(boolean b) {}

	@Override
	public double getEnergyAmountThisNodeWant() 
	{
		return 0;
	}

	@Override
	public void injectEnergyInThisNode(double amount, double voltage) {}

}
