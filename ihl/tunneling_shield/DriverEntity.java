package ihl.tunneling_shield;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class DriverEntity extends Entity {

	public DriverTileEntity parent;

	public DriverEntity(World arg0) 
	{
		super(arg0);
        this.ignoreFrustumCheck = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.noClip=true;
	}
	
	public DriverEntity(World arg0, DriverTileEntity te, int x, int y, int z) 
	{
		super(arg0);
		this.parent=te;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.ignoreFrustumCheck = true;
	}

	@Override
    public boolean isInRangeToRenderDist(double par1)
    {
        return true;
    }
	

    @Override
	public void onUpdate()
    {
        super.onUpdate();

        if(this.parent!=null)
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
        	this.lastTickPosX = this.prevPosX = this.posX =this.parent.xCoord+this.parent.getModelShiftAmount()*this.parent.mX();
        	this.lastTickPosY = this.prevPosY = this.posY =(this.parent.yCoord);
        	this.lastTickPosZ = this.prevPosZ = this.posZ =this.parent.zCoord+this.parent.getModelShiftAmount()*this.parent.mZ();
        }
        else
        {
        	this.setDead();
        }
    }

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {}
}
