package ihl.i_hate_liquids;

import java.util.ArrayList;
import java.util.List;

import ihl.IHLMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class InvisibleMagicanEntity extends Entity {
	private int entityAge=0;
	private int viscosityTimer=0;
	private int viscosity=5;
	public int x0=0;
	public int y0=0;
	public int z0=0;
    public List<XYZ> flowXYZ = new ArrayList<XYZ>();
    public List<Block> blockList = new ArrayList<Block>();
    private Block block;
    private int lowestPointsCounter=0;
    
	public InvisibleMagicanEntity(World arg0) 
	{
		super(arg0);
		this.isImmuneToFire=true;
		this.noClip=true;
		this.setSize(0F, 0F);
	}
	
	public InvisibleMagicanEntity(World arg0, int x, int y, int z) 
	{
		super(arg0);
		this.x0=x;
		this.y0=y;
		this.z0=z;
		this.setPosition(x, y, z);
		this.isImmuneToFire=true;
		this.noClip=true;
		this.setSize(0F, 0F);
	}
	
    @Override
	public void onUpdate()
    {
    	if(!this.worldObj.isRemote)
    	{
    		if(this.blockList.isEmpty())
    		{
    			if(!takeLowestPoint())
    			{
    				this.setDead();
    				return;
    			}
    			else
        		{
        			if(this.block!=null)
        			{
        				if(this.block==Blocks.water||this.block==Blocks.flowing_water)
        				{
        					this.viscosity=-1;
        				}
        				else if(this.block instanceof IFluidBlock)
        				{
        					IFluidBlock bf = (IFluidBlock) this.block;
        					if(bf.getFluid()!=null && bf.getFluid().getViscosity()>600)
        					{
        						this.viscosity=bf.getFluid().getViscosity()/300;
        					}
        				}
        				else if(this.block.getMaterial()==Material.lava)
        				{
        					this.viscosity=20;
        				}
        				else
        				{
        					this.viscosity=20;
        				}
        			}
        			else
        			{
        				this.entityAge++;
        			}
        		}
    		}
    		else
    		{
    			if(this.viscosityTimer < this.viscosity)
    			{
    				this.viscosityTimer++;
    			}
    			else
    			{
    				this.viscosityTimer=0;
    				XYZ xyz;
    				if(!this.flowXYZ.isEmpty())
    				{
    				xyz = this.flowXYZ.remove(0);
    	    		if(this.flowXYZ.isEmpty())
    	    		{
    	    			Block block1=this.blockList.get(0);
    	    			//System.out.println("Trying to create new source");
    	    			if(this.setBlock(xyz.x, xyz.y, xyz.z, block1))
    	    			{
    	    				this.blockList.remove(0);
    	    				//System.out.println("Creating new source at "+xyz.x+","+xyz.y+","+xyz.z);
    	    			}
    	    			else
    	    			{
    	    				//System.out.println("Failed to create new source!");
    	    			}
    	    			if(!takeLowestPoint())
    	    			{
    	    				this.setDead();
    	    				return;
    	    			}
    	    		}
    	    		else
    	    		{
            			Block blockToReplace = this.worldObj.getBlock(xyz.x, xyz.y, xyz.z);
            			if(blockToReplace==null || blockToReplace.isAir(worldObj, xyz.x, xyz.y, xyz.z) || blockToReplace.getMaterial().isLiquid() || blockToReplace==Blocks.snow_layer || blockToReplace==Blocks.vine || blockToReplace==Blocks.tallgrass || blockToReplace==Blocks.deadbush)
            			{
            				if(this.flowXYZ.size()<=this.blockList.size())
            				{
            					Block block1=this.blockList.get(0);
            	    			if(this.setBlock(xyz.x, xyz.y, xyz.z, block1))
            	    			{
            	    				this.blockList.remove(0);
            	    				//System.out.println("Creating new source at "+xyz.x+","+xyz.y+","+xyz.z);
            	    			}
            				}
            				else
            				{
            					if(!this.setNonSourceBlock(xyz.x, xyz.y, xyz.z, block))
                				{
                					//System.out.println("Failed to setNonSourceBlock at "+xyz.x+","+xyz.y+","+xyz.z);
                				}
            				}
            			}
            			else
            			{
            				this.flowXYZ.clear();
            			}
    	    		}
    				}
    			}
    		}
			if(this.lowestPointsCounter > this.blockList.size())
			{
				this.searchSourceDestroyCount(worldObj, this.x0, this.y0, this.z0, block, this.lowestPointsCounter);
				if(!this.blockList.isEmpty())
				{
					this.block=this.blockList.get(0);
				}
			}
        	if(this.entityAge>200)
        	{
        		this.setDead();
        		return;
        	}
    	}
    }

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound arg0) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound arg0) {}
    
    private int getFlowDecay(int par2, int par3, int par4)
    {
    	Block block = this.worldObj.getBlock(par2, par3, par4);
    	return  block.getMaterial().isLiquid() ? this.worldObj.getBlockMetadata(par2, par3, par4) : -1;
    }
    
    public boolean replaceBlock(int x,int y,int z, Block block)
    {	   
		if(!IHLMod.cccFiniteWater&&(block==Blocks.flowing_water || block==Blocks.water))
		{
			if(this.worldObj.setBlock(x,y,z,IHLMod.flowing_water,6,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,IHLMod.flowing_water,10);
				return true;
			}
			return false;
		}
		else
		if(!IHLMod.cccFiniteWater&&this.worldObj.provider.isHellWorld&&(block==Blocks.flowing_lava || block==Blocks.lava))
		{
			if(this.worldObj.setBlock(x,y,z,IHLMod.flowing_lava,6,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,IHLMod.flowing_lava,10);
				return true;
			}
			return false;
		}
		else
		{
			if(this.worldObj.setBlockMetadataWithNotify(x, y, z, 6,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,this.worldObj.getBlock(x, y, z),10);
				return true;
			}
			return false;
		}
    }
    
    public boolean setNonSourceBlock(int x,int y,int z,Block block)
    {
		if(block==Blocks.flowing_water || block==Blocks.water)
		{
			if(this.worldObj.setBlock(x,y,z,Blocks.flowing_water,1,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,Blocks.flowing_water,80);
				return true;
			}
			return false;
		}
		else
		if(block==Blocks.flowing_lava || block==Blocks.lava)
		{
			if(this.worldObj.setBlock(x,y,z,Blocks.flowing_lava,1,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,Blocks.flowing_lava,80);
				return true;
			}
			return false;
		}
		else
		{
			if(this.worldObj.setBlock(x, y, z, block, 1,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z, block ,80);
				return true;
			}
			return false;
		}
    }
    
    public boolean setBlock(int x,int y,int z, Block block)
    {
		if(block==Blocks.flowing_water || block==Blocks.water)
		{
			if(this.worldObj.setBlock(x,y,z,Blocks.flowing_water,0,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,Blocks.flowing_water,20);
				return true;
			}
			return false;
		}
		else
		if(block==Blocks.flowing_lava || block==Blocks.lava)
		{
			if(this.worldObj.setBlock(x,y,z,Blocks.flowing_lava,0,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,Blocks.flowing_lava,20);
				return true;
			}
			return false;
		}
		else
		{
			if(this.worldObj.setBlock(x,y,z,block,0,3))
			{
				this.worldObj.scheduleBlockUpdate(x,y,z,block,20);
				return true;
			}
			return false;
		}
    }
    
    private boolean takeLowestPoint()
    {
        int xz[]={0,1,0,-1,0};
        Block block;
        int x=x0;
        int y=y0; 
        int z=z0;
        List<Long> list = new ArrayList<Long>();
        this.flowXYZ.clear();
		for(int thread=0;thread<=256;thread++)
		{

	        long number = new Long(x*256L*256L+y*256L+z);
	        list.add(number);
	        boolean skip=false;
			block = this.worldObj.getBlock(x, y-1, z);
			number = new Long(x*256L*256L+(y-1)*256L+z);
			if((block.isAir(this.worldObj, x, y-1, z)||getFlowDecay(x, y-1, z)>=1) && !list.contains(number))
			{
				y--;
				this.lowestPointsCounter=1;
				skip=true;
			}
			if(!skip)
			{
				for(int i=0;i<=3;i++)
				{
					block = this.worldObj.getBlock(x+xz[i], y, z+xz[i+1]);
					number = new Long((x+xz[i])*256L*256L+y*256L+z+xz[i+1]);
					if((block.isAir(this.worldObj, x+xz[i], y, z+xz[i+1])||getFlowDecay(x+xz[i], y, z+xz[i+1])>=1) && !list.contains(number))
					{
						x+=xz[i];
						z+=xz[i+1];
						skip=true;
						this.lowestPointsCounter++;
						break;
					}
				}
			}
			XYZ xyz=new XYZ(x,y,z);
			this.flowXYZ.add(xyz);
			if(!skip)
			{
				break;
			}
		}
		if(y<y0)
		{
			return true;
		}
		return false;
    }

	private int searchSourceDestroyCount(World world, int startx, int starty, int startz,
			Block type, int countCells) {
		int currentFlowDecay=getFlowDecay(world, startx, starty, startz);
		for (int i=0; i<64;i++)
			{
				if(getFlowDecay(world, startx, starty+1, startz)>=0)
				{
					starty++;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx+1, starty+1, startz)>=0)
				{
					starty++;
					startx++;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx-1, starty+1, startz)>=0)
				{
					starty++;
					startx--;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx, starty+1, startz+1)>=0)
				{
					starty++;
					startz++;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx, starty+1, startz-1)>=0)
				{
					starty++;
					startz--;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				//Start checking neighbor blocks to lower flow decay.
				else if(getFlowDecay(world, startx-1, starty, startz)<currentFlowDecay&&getFlowDecay(world, startx-1, starty, startz)!=-1)
				{
					startx--;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else if(getFlowDecay(world, startx, starty, startz+1)<currentFlowDecay&&getFlowDecay(world, startx, starty, startz+1)!=-1)
				{
					startz++;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else if(getFlowDecay(world, startx, starty, startz-1)<currentFlowDecay&&getFlowDecay(world, startx, starty, startz-1)!=-1)
				{
					startz--;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else if(getFlowDecay(world, startx+1, starty, startz)<currentFlowDecay&&getFlowDecay(world, startx+1, starty, startz)!=-1)
				{
				startx++;
				currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else {break;}
			}
		List<XYZ> xyzlist = new ArrayList<XYZ>();
		if(currentFlowDecay==0)
		{
			xyzlist.add(new XYZ(startx, starty, startz));
			Block block=worldObj.getBlock(startx, starty, startz);
			replaceBlock(startx, starty, startz, block);
			this.blockList.add(block);
			int listPos=0;
			for(int i=0;i<=countCells;i++)
			{
				if(getFlowDecay(world, startx-1, starty, startz)==0 && !xyzlist.contains(new XYZ(startx-1, starty, startz)))
				{
					startx--;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					block=worldObj.getBlock(startx, starty, startz);
					replaceBlock(startx, starty, startz, block);
					this.blockList.add(block);
				}
				else if(getFlowDecay(world, startx, starty, startz+1)==0 && !xyzlist.contains(new XYZ(startx, starty, startz+1)))
				{
					startz++;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					block=worldObj.getBlock(startx, starty, startz);
					replaceBlock(startx, starty, startz, block);
					this.blockList.add(block);
				}
				else if(getFlowDecay(world, startx, starty, startz-1)==0 && !xyzlist.contains(new XYZ(startx, starty, startz-1)))
				{
					startz--;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					block=worldObj.getBlock(startx, starty, startz);
					replaceBlock(startx, starty, startz, block);
					this.blockList.add(block);
				}
				else if(getFlowDecay(world, startx+1, starty, startz)==0 && !xyzlist.contains(new XYZ(startx+1, starty, startz)))
				{
					startx++;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					block=worldObj.getBlock(startx, starty, startz);
					replaceBlock(startx, starty, startz, block);
					this.blockList.add(block);
				}
				else 
				{
					if(listPos>0)
					{
						listPos--;
						XYZ xyz = xyzlist.get(listPos);
						startx=xyz.x;
						starty=xyz.y;
						startz=xyz.z;
					}
				}
			}
		}
		return xyzlist.size();	
	}

	private int getFlowDecay(World world, int startx, int starty, int startz) {
		return this.getFlowDecay(startx, starty, startz);
	}
}
