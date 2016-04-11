package ihl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class IHL3dPathFinder {
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    
    public int lowestPointX;
    public int lowestPointY;
    public int lowestPointZ;
    
    public double targetX;
    public double targetY;
    public double targetZ;
    
    private Entity entity;
    
	public IHL3dPathFinder(Entity entity1)
	{
		this.entity=entity1;
	}
	
	public boolean findPath(double x2, double y2, double z2)
	{
		if(this.isCourseTraversable(x2, y2, z2, this.entity.posX, this.entity.posY, this.entity.posZ))
		{
			this.waypointX=x2;
			this.waypointY=y2;
			this.waypointZ=z2;
			return true;
		}
		else
		{
			return this.takeVector(x2, y2, z2);
		}
	}
	
    private boolean isCourseTraversable(double toX, double toY, double toZ, double fromX, double fromY, double fromZ)
    {
        double var9 = toX - fromX;
        double var11 = toY - fromY;
        double var13 = toZ - fromZ;
        double var7 = MathHelper.sqrt_double(var9 * var9 + var11 * var11 + var13 * var13);
        var9 /= var7;
        var11 /= var7;
        var13 /= var7;
        AxisAlignedBB var15 = this.entity.boundingBox.copy();
        for (int var16 = 1; var16 < var7; ++var16)
        {
            var15.offset(var9, var11, var13);
            if (!this.entity.worldObj.getCollidingBoundingBoxes(this.entity, var15).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean takeVector(double toX, double toY, double toZ)
    {
        int x=Math.round((float)toX);
        int y=Math.round((float)toY);
        int z=Math.round((float)toZ);
		int xyz[] = {0,0,1,0,0,-1,0,0};
		List list = new ArrayList();
		for(int thread=0;thread<=256;thread++)
		{
			if(this.isCourseTraversable(x+0.5D, y+0.5D, z+0.5D, this.entity.posX, this.entity.posY, this.entity.posZ))
			{
				this.waypointX=x+0.5D;
				this.waypointY=y+0.5D;
				this.waypointZ=z+0.5D;
				return true;
			}
	        Block block;
	        long number = new Long(x*256L*256L+y*256L+z);
	        list.add(number);
	        double lastDistanceSquared = this.entity.getDistanceSq(x, y, z);
	        boolean skip=false;
			for(int i=0;i<=5;i++)
			{
				block = entity.worldObj.getBlock(x+xyz[i], y+xyz[i+1], z+xyz[i+2]);
				number = new Long((x+xyz[i])*256L*256L+(y+xyz[i+1])*256L+z+xyz[i+2]);
				if(block.isAir(entity.worldObj, x+xyz[i], y+xyz[i+1], z+xyz[i+2]) && !list.contains(number) && this.entity.getDistanceSq((double)x+xyz[i], (double)y+xyz[i+1], (double)z+xyz[i+2])<lastDistanceSquared)
				{
					x+=xyz[i];
					y+=xyz[i+1];
					z+=xyz[i+2];
					skip=true;
					break;
				}
			}
			if(!skip)
			{
				for(int i=0;i<=5;i++)
				{
					block = entity.worldObj.getBlock(x+xyz[i], y+xyz[i+1], z+xyz[i+2]);
					number = new Long((x+xyz[i])*256L*256L+(y+xyz[i+1])*256L+z+xyz[i+2]);
					if(block.isAir(entity.worldObj, x+xyz[i], y+xyz[i+1], z+xyz[i+2]) && !list.contains(number))
					{
						x+=xyz[i];
						y+=xyz[i+1];
						z+=xyz[i+2];
						break;
					}
				}
			}
		}
		return false;
    }
}
