package ihl.enviroment;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.IC2;
import ihl.IHLModInfo;
import ihl.i_hate_liquids.XYZ;
import ihl.utils.IHLUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class SpotlightTileEntity extends LightBulbTileEntity implements INetworkTileEntityEventListener
{
	Vec3 directionVector=Vec3.createVectorHelper(1, 0, 0);
	private static final int maxLightRange=128;
	private final Set<XYZ> xyzGlowningAir = new HashSet<XYZ>();
	private static final Set<Vec3> lightSphereVectors = new HashSet<Vec3>();
	public float rotationPitch=0f;
	public float rotationYaw=0f;
	public float prevRotationPitch=0f;
	public float prevRotationYaw=0f;
	boolean needLightTargetUpdate=false;
	
	public static void createLightSphereVectors()
	{
		for(double y=-1d;y<=1d;y+=0.2d)
		{
			double sqr = 1-y*y;
			double x,z;
			for(double alpha=0;alpha<Math.PI*2;alpha+=Math.PI*2d/(sqr*16d+0.1d))
			{
				x=Math.cos(alpha)*sqr;
				z=Math.sin(alpha)*sqr;
				lightSphereVectors.add(Vec3.createVectorHelper(x*0.5d, y*0.5d, z*0.5d));
			}
		}
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        directionVector.xCoord = nbttagcompound.getDouble("directionVector.xCoord");
        directionVector.yCoord = nbttagcompound.getDouble("directionVector.yCoord");
        directionVector.zCoord = nbttagcompound.getDouble("directionVector.zCoord");
		this.rotationPitch=this.getVectorPitchAngle(directionVector);
		this.rotationYaw=this.getVectorYawAngle(directionVector);
		needLightTargetUpdate=true;
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("directionVector.xCoord", directionVector.xCoord);
        nbttagcompound.setDouble("directionVector.yCoord", directionVector.yCoord);
        nbttagcompound.setDouble("directionVector.zCoord", directionVector.zCoord);
    }
	
    @Override
	public List<String> getNetworkedFields()
    {
        List<String> list = super.getNetworkedFields();
        list.add("rotationPitch");
        list.add("rotationYaw");
        return list;
    }
	
	public void setLightOn()
	{
		Iterator<XYZ> gai = xyzGlowningAir.iterator();
		while(gai.hasNext())
		{
			XYZ xyz = gai.next();
			Block block = worldObj.getBlock(xyz.x,xyz.y,xyz.z);
			if(block==Blocks.air || block.isAir(this.worldObj, xyz.x,xyz.y,xyz.z))
			{
				worldObj.setBlock(xyz.x, xyz.y, xyz.z, LightBulbBlock.glowningAir);
			}
		}
	}
	
	public void setLightOff()
	{
		Iterator<XYZ> gai = xyzGlowningAir.iterator();
		while(gai.hasNext())
		{
			XYZ xyz = gai.next();
			Block block = worldObj.getBlock(xyz.x,xyz.y,xyz.z);
			if(block == LightBulbBlock.glowningAir)
			{
				worldObj.setBlockToAir(xyz.x, xyz.y, xyz.z);
			}
		}
	}
	
	@Override
    public void updateEntity()
    {
		super.updateEntity();
		if(needLightTargetUpdate)
		{
			this.generateGlowningAirList();
			this.updateLightState();
			needLightTargetUpdate=false;
		}
		if(this.prevRotationPitch!=this.rotationPitch)
		{
	        IC2.network.get().updateTileEntityField(this, "rotationPitch");
			this.prevRotationPitch=this.rotationPitch;
		}
		if(this.prevRotationYaw!=this.rotationYaw)
		{
	        IC2.network.get().updateTileEntityField(this, "rotationYaw");
			this.prevRotationYaw=this.rotationYaw;
		}
    }
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("spotlight");
	}
	
    public void setDirectionVector(EntityLivingBase player)
	{
    	ForgeDirection dir = ForgeDirection.getOrientation(this.getFacing());
    	Vec3 lookVec = player.getLookVec();
    	if(lookVec.xCoord*dir.offsetX+lookVec.yCoord*dir.offsetY+lookVec.zCoord*dir.offsetZ<0)
    	{
    		double x=player.posX-this.xCoord-0.5D;
    		double y=player.posY+player.getEyeHeight()-this.yCoord-0.5D;
    		double z=player.posZ-this.zCoord-0.5D;
    		double d=Math.sqrt(x*x+y*y+z*z);
    		directionVector=Vec3.createVectorHelper(x/d, y/d, z/d);
    	}
    	else
    	{
    		directionVector=player.getLookVec();
    	}
		this.rotationPitch=this.getVectorPitchAngle(directionVector);
		this.rotationYaw=this.getVectorYawAngle(directionVector);
		this.needLightTargetUpdate=true;
        IC2.network.get().initiateTileEntityEvent(this, 0, true);
	}
	
	private void generateGlowningAirList()
	{
		this.setLightOff();
		xyzGlowningAir.clear();
		Iterator<Vec3> lvi = lightSphereVectors.iterator();
		while(lvi.hasNext())
		{
			traceVectorToLastAirBlock(lvi.next());
		}
	}
	
	private void traceVectorToLastAirBlock(Vec3 vector)
	{
		double dx=directionVector.xCoord;
		double dy=directionVector.yCoord;
		double dz=directionVector.zCoord;
		double dx1=vector.xCoord;
		double dy1=vector.yCoord;
		double dz1=vector.zCoord;
		if(2*dx*dx1+dx1*dx1+2*dy*dy1+dy1*dy1+2*dz*dz1+dz1*dz1>0d)
		{
			double x1=xCoord+0.5d;
			double y1=yCoord+0.5d;
			double z1=zCoord+0.5d;
			int xi1=xCoord;
			int yi1=yCoord;
			int zi1=zCoord;
			int airx=xCoord;
			int airy=yCoord;
			int airz=zCoord;
			for(int i=0;i<maxLightRange;i++)
			{
				int xi = (int)Math.floor(x1+(dx+dx1)*i*0.79);
				int yi = (int)Math.floor(y1+(dy+dy1)*i*0.79);
				int zi = (int)Math.floor(z1+(dz+dz1)*i*0.79);
				if(xi!=xi1||yi!=yi1||zi!=zi1)
				{
					Block block = worldObj.getBlock(xi,yi,zi);
					if(block!=Blocks.air && !block.isAir(worldObj, xi, yi, zi) && block.getMaterial()!=Material.glass && block.getMaterial()!=Material.air)
					{
						xyzGlowningAir.add(new XYZ(airx,airy,airz));
						break;
					}
					else
					{
						if(block.isAir(worldObj, xi, yi, zi) || block==Blocks.air)
						{
							airx=xi;
							airy=yi;
							airz=zi;
						}
						xi1=xi;
						yi1=yi;
						zi1=zi;
					}
				}
			}
		}
	}
	
	@Override
	protected void updateLightState()
    {
		if(this.getActive())
		{
    		setLightOn();
		}
		else
		{
    		setLightOff();
		}
    }
	
	private float getVectorPitchAngle(Vec3 vector)
	{
		double x=vector.xCoord;
		double y=vector.yCoord;
		double z=vector.zCoord;
		switch(this.getFacing())
		{
			case 0:
				return (float)-(Math.abs(Math.asin(z))+Math.abs(Math.asin(x)));
			case 1:
				return (float)(Math.abs(Math.asin(z))+Math.abs(Math.asin(x)));
			case 2:
				return (float)(Math.abs(Math.asin(y))+Math.abs(Math.asin(x)));
			case 3:
				return (float)(Math.abs(Math.asin(y))+Math.abs(Math.asin(x)));
			case 4:
				return (float)(Math.abs(Math.asin(y))+Math.abs(Math.asin(z)));
			case 5:
				return (float)(Math.abs(Math.asin(y))+Math.abs(Math.asin(z)));
			default:
				return (float)(-Math.asin(y));
		}
	}
	
	private float getVectorYawAngle(Vec3 vector)
	{
		double x=vector.xCoord;
		double y=vector.yCoord;
		double z=vector.zCoord;
		switch(this.getFacing())
		{
			case 0:
				if(z>=0)
				{
					return (float)(Math.acos(x/Math.sqrt(x*x+z*z))-Math.PI/2);
				}
				else
				{
					return (float)(-Math.acos(x/Math.sqrt(x*x+z*z))-Math.PI/2);
				}
			case 1:
				if(z>=0)
				{
					return (float)(Math.acos(x/Math.sqrt(x*x+z*z))-Math.PI/2);
				}
				else
				{
					return (float)(-Math.acos(x/Math.sqrt(x*x+z*z))-Math.PI/2);
				}
			case 2:
				if(y>=0)
				{
					return (float)(-Math.acos(x/Math.sqrt(x*x+y*y))-Math.PI/2);
				}
				else
				{
					return (float)(Math.acos(x/Math.sqrt(x*x+y*y))-Math.PI/2);
				}
			case 3:
				if(y>=0)
				{
					return (float)(-Math.acos(x/Math.sqrt(x*x+y*y))-Math.PI/2);
				}
				else
				{
					return (float)(Math.acos(x/Math.sqrt(x*x+y*y))-Math.PI/2);
				}
			case 4:
				if(y>=0)
				{
					return (float)(-Math.acos(z/Math.sqrt(z*z+y*y))-Math.PI/2);
				}
				else
				{
					return (float)(Math.acos(z/Math.sqrt(z*z+y*y))-Math.PI/2);
				}
			case 5:
				if(y>=0)
				{
					return (float)(-Math.acos(z/Math.sqrt(z*z+y*y))-Math.PI/2);
				}
				else
				{
					return (float)(Math.acos(z/Math.sqrt(z*z+y*y))-Math.PI/2);
				}
			default:
				return 0f;
		}
	}

	@Override
	public void onNetworkEvent(int event) 
	{
    	this.worldObj.playSound(xCoord+0.5d, yCoord+0.5d, zCoord+0.5d, IHLModInfo.MODID+":spotlightRotating", 10F, 1f, true);
	}
}


/* -Y DOWN(0, -1, 0)  0
/* +Y UP(0, 1, 0)     1
/* -Z NORTH(0, 0, -1) 2
/* +Z SOUTH(0, 0, 1)  3
/* -X WEST(-1, 0, 0), 4
/* +X EAST(1, 0, 0),  5
//VALID_DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};*/
