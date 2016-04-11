package ihl.utils;

import ihl.IHLMod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityIHLExplosion extends Entity {
	private Explosion explosion;
	private boolean explosionDone=false;
	public Set<Long> effectBorderBlocks;
	public Set<Long> effectBorderBlocksWithLowPosition;
	public Set<Long> blocksConnectedWithBedrock;
	public Set<Long> blocksNotConnectedWithBedrock;
	public int explosionPower;
	public int x;
	public int y;
	public int z;
    private Map<Long,Entity> entityCache = new HashMap();
	
	public EntityIHLExplosion(World world,int x1,int y1,int z1, int explosionPower1) {
		super(world);
		this.effectBorderBlocks=new HashSet();
		this.effectBorderBlocksWithLowPosition=new HashSet();
		this.blocksConnectedWithBedrock=new HashSet();
		this.blocksNotConnectedWithBedrock=new HashSet();
		this.isImmuneToFire=true;
		this.noClip=true;
		this.setSize(0F, 0F);
		this.x=x1;
		this.y=y1;
		this.z=z1;
		this.setPosition(x+0.5d, y+0.5d, z+0.5d);
		this.explosionPower = explosionPower1;
        }


	
    @Override
	public void onUpdate()
    { 
    	if(!worldObj.isRemote)
    	{
    		if(!effectBorderBlocks.isEmpty())
    		{
    			boolean hasSomeBlocksStickedToAir=false;
    			Iterator<Long> ebbi = effectBorderBlocks.iterator();
    			while(ebbi.hasNext())
    			{
    				long longNumber = ebbi.next();
    				int[] xyz = IHLUtils.decodeXYZ(longNumber);
    				Block block = worldObj.getBlock(x+xyz[0], y+xyz[1], z+xyz[2]);
    				if(block!=Blocks.air && !block.isAir(worldObj, x+xyz[0], y+xyz[1], z+xyz[2]))
    				{
    					if(block.getMaterial().isLiquid())
    					{
    						block.onNeighborBlockChange(worldObj, x+xyz[0], y+xyz[1], z+xyz[2], block);
    					}
    					else if(!this.isBlockConnectedWithBedrock(longNumber))
    					{
    						hasSomeBlocksStickedToAir=true;
    					}
    				}
    			}
    			Iterator<Long> bnctbi = this.blocksNotConnectedWithBedrock.iterator();
    			while(bnctbi.hasNext())
    			{
    				long longNumber5 = bnctbi.next();
    				int[] xyz = IHLUtils.decodeXYZ(longNumber5);
    				Block block = worldObj.getBlock(x+xyz[0], y+xyz[1], z+xyz[2]);
    				float bh = block.getBlockHardness(worldObj, x+xyz[0], y+xyz[1], z+xyz[2]);
    				if(bh>=0f && bh<Float.MAX_VALUE)
    				{
    					block.onBlockDestroyedByExplosion(worldObj, x+xyz[0], y+xyz[1], z+xyz[2], explosion);
    				}
    			}
    		}
    		if(!this.explosionDone)
    		{
    			IHLMod.explosionHandler.setPower(IHLMod.explosionHandler.startVectors, explosionPower);
        		IHLMod.explosionHandler.doExplosion(worldObj, x, y, z, IHLMod.explosionHandler.startVectors);
        		this.explosionDone=true;
    		}
    	}
    }
    
    private boolean isBlockConnectedWithBedrock(long longNumber) 
    {
    	if(this.blocksNotConnectedWithBedrock.contains(longNumber))
    	{
    		return false;
    	}
    	Set<Long> path = new HashSet();
		Iterator<Long> pathi = path.iterator();
    	Set<Long> deadlockBlocks = new HashSet();
		int[] xyz = IHLUtils.decodeXYZ(longNumber);
    	int absX=x+xyz[0];
    	int absY=y+xyz[1];
    	int absZ=z+xyz[2];
    	path.add(longNumber);
    	while(absY>4)
    	{
        	int xyzi[] = {0,0,-1,0,0,1,0,0};
        	Block block;
        	int x2,y2,z2;
        	boolean deadlock = true;
        	for(int i=0;i<=5;i++)
    		{
        		x2=absX+xyzi[i];
        		y2=absY+xyzi[i+2];
        		z2=absZ+xyzi[i+1];
        		long longNumber2 = IHLUtils.encodeXYZ(x2-x, y2-y, z2-z);
        		if(this.blocksConnectedWithBedrock.contains(longNumber2))
        		{
        			this.blocksConnectedWithBedrock.addAll(path);
        			return true;
        		}
        		if(!path.contains(longNumber2))
        		{
        			block = worldObj.getBlock(x2,y2,z2);
        			if(block!=Blocks.air && !block.isAir(worldObj, x2,y2,z2) && !block.getMaterial().isLiquid())
        			{
        				absX=x2;
        				absY=y2;
        				absZ=z2;
            			path.add(longNumber2);
            			if(absY<=4)
            			{
            				this.blocksConnectedWithBedrock.addAll(path);
                			return true;
            			}
            			deadlock = false;
            			break;
        			}
        		}
    		}
        	if(deadlock)
        	{
        		long longNumber3 = IHLUtils.encodeXYZ(absX-x, absY-y, absZ-z);
        		deadlockBlocks.add(longNumber3);
        		boolean deadEnd=true;
        		while(pathi.hasNext())
        		{
            		long longNumber4 = pathi.next();
            		if(!deadlockBlocks.contains(longNumber4))
            		{
            			int[] xyz2 = IHLUtils.decodeXYZ(longNumber4);
            	    	absX=x+xyz2[0];
            	    	absY=y+xyz2[1];
            	    	absZ=z+xyz2[2];
            	    	deadEnd=false;
            	    	break;
            		}
        		}
        		if(deadEnd)
        		{
    				this.blocksNotConnectedWithBedrock.addAll(path);
        			return false;
        		}
        	}
    	}
    	this.blocksConnectedWithBedrock.addAll(path);
		return true;
	}

    
	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound arg0) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound arg0) {}
	

}
