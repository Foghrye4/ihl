package ihl.enviroment;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCache;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.core.block.TileEntityBlock;
import ihl.IHLMod;
import ihl.handpump.BlockWithCoordinates;
import ihl.utils.IHLUtils;

public class MirrorTileEntity extends TileEntityBlock
{
	public final List<BlockWithCoordinates> bwc = new ArrayList();
	public final List<Entity> reflectedEntity = new ArrayList();
	public final List<TileEntity> reflectedTileEntity = new ArrayList();
	private int bwcListPos=0;
	private int reflectedEntityListPos=0;
	private int reflectedTileEntityListPos=0;
	private int reflectionVolumePos=0;
	boolean firstTick=true;
	public int dnx,dpx,dny,dpy,dnz,dpz;
	boolean shouldReflect=true;
	int reflectExtensionRight=0;
	int reflectExtensionTop=0;
	int timer=0;
	int blockReflectionUpdateTimer=0;
	int reflectionVolumeSize=-1;
	AxisAlignedBB reflectionArea;
	int displayListCache=-1;
	public ChunkCache chunkCache;
	public boolean needRenderUpdate=false;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	private int lastBWCSize;
	
	public MirrorTileEntity() 
	{}

    @Override
    public void updateEntityClient()
    {
    	super.updateEntityClient();
    	if(this.shouldReflect)
    	{
    		int i=0;
    		for(i=0;i<Math.min(this.reflectionVolumeSize, IHLMod.config.mirrorReflectionUpdateSpeed);i++)
    		{
        		this.checkReflectionVolume();
    		}
    		for(i=0;i<Math.min(this.bwc.size(), IHLMod.config.mirrorReflectionUpdateSpeed);i++)
    		{
        		this.checkBWCList();
    		}
    		for(i=0;i<Math.min(this.reflectedTileEntity.size(), IHLMod.config.mirrorReflectionUpdateSpeed);i++)
    		{
        		this.checkTileEntityList();
    		}
    	}
    	if(timer++>20)
    	{
    		boolean isFirst=true;
			TileEntity t = worldObj.getTileEntity(xCoord-mXT(),yCoord-mYT(), zCoord-mZT());
			if(t instanceof MirrorTileEntity)
			{
				if(((MirrorTileEntity)t).getFacing()==this.getFacing())
				{
					isFirst=false;
				}
			}
			t = worldObj.getTileEntity(xCoord-mXR(),yCoord-mYR(), zCoord-mZR());
			if(t instanceof MirrorTileEntity)
			{
				if(((MirrorTileEntity)t).getFacing()==this.getFacing())
				{
					isFirst=false;
				}
			}
			if(isFirst)
			{
				this.shouldReflect=true;
				this.reflectExtensionTop=0;
				this.reflectExtensionRight=0;
				for(int i=1;i<=IHLMod.config.mirrorReflectionRange+2;i++)
				{
					t = worldObj.getTileEntity(xCoord+mXT()*i,yCoord+mYT()*i, zCoord+mZT()*i);
					if(t instanceof MirrorTileEntity && ((MirrorTileEntity)t).getFacing()==this.getFacing())
					{
						MirrorTileEntity te = (MirrorTileEntity) t;
						te.shouldReflect=false;
						this.reflectExtensionTop=i;
					}
					else
					{
						break;
					}
				}
				a:for(int i0=1;i0<=IHLMod.config.mirrorReflectionRange+2;i0++)
				{
					for(int i=0;i<=this.reflectExtensionTop;i++)
					{
						t = worldObj.getTileEntity(xCoord+mXT()*i+mXR()*i0,yCoord+mYT()*i+mYR()*i0, zCoord+mZT()*i+mZR()*i0);
						if(t instanceof MirrorTileEntity && ((MirrorTileEntity)t).getFacing()==this.getFacing())
						{
							MirrorTileEntity te = (MirrorTileEntity) t;
							te.shouldReflect=false;
						}
						else
						{
							break a;
						}
					}
					this.reflectExtensionRight=i0;
				}
			}
			if(this.reflectionArea!=null)
			{
				this.reflectedEntity.clear();
				this.reflectedEntity.addAll(worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.reflectionArea));
				this.reflectedEntity.addAll(worldObj.getEntitiesWithinAABB(EntityBoat.class, this.reflectionArea));
				this.reflectedEntity.addAll(worldObj.getEntitiesWithinAABB(EntityItem.class, this.reflectionArea));
				this.reflectedEntity.addAll(worldObj.getEntitiesWithinAABB(EntityMinecart.class, this.reflectionArea));
			}
    		timer=0;
    	}
		if(firstTick && this.shouldReflect)
		{
			int ix,iy,iz;
			int range=IHLMod.config.mirrorReflectionRange;
			ForgeDirection direction = ForgeDirection.getOrientation(this.getFacing());
			bwc.clear();
			{
				dpx = direction.offsetX==-1?0:1;
				dnx = direction.offsetX==1?0:1;
				dpy = direction.offsetY==-1?0:1;
				dny = direction.offsetY==1?0:1;
				dpz = direction.offsetZ==-1?0:1;
				dnz = direction.offsetZ==1?0:1;
				reflectionVolumeSize=(range*dnx+range*dpx+dnx)*(range*dny+range*dpy+dny)*(range*dnz+range*dpz+dnz);
				reflectionArea = AxisAlignedBB.getBoundingBox(xCoord-range*dnx-1d, yCoord-range*dny-1d, zCoord-range*dnz-1d, xCoord+range*dpx+dnx+1d, yCoord+range*dpy+dny+1d, zCoord+range*dpz+dnz+1d);
				minX = xCoord-range*dnx;
				maxX = xCoord+range*dpx+dnx;
				minY = Math.max(yCoord-range*dny,0);
				maxY = Math.min(yCoord+range*dpy+dny,this.worldObj.getActualHeight());
				minZ = zCoord-range*dnz;
				maxZ = zCoord+range*dpz+dnz;
				chunkCache = new ChunkCache(worldObj, minX, minY, minZ, maxX, maxY, maxZ, 16);
				for(ix=minX;ix<maxX;ix++)
				{
					for(iy=minY;iy<maxY;iy++)
					{
						for(iz=minZ;iz<maxZ;iz++)
						{
							Block block = worldObj.getBlock(ix, iy, iz);
							if(block!=null && !block.isAir(worldObj, ix, iy, iz) && isBlockVisible(block,ix,iy,iz))
							{						
								TileEntity te = worldObj.getTileEntity(ix, iy, iz);
								if(te!=null && !te.isInvalid() && !reflectedTileEntity.contains(te) && TileEntityRendererDispatcher.instance.hasSpecialRenderer(te) && !(te instanceof MirrorTileEntity))
								{
									reflectedTileEntity.add(te);
								}
				    			if(block.getRenderType()>=0)
								{
									bwc.add(new BlockWithCoordinates(block, ix, iy, iz, worldObj.getBlockMetadata(ix, iy, iz)));
								}
							}
							
						}
					}
				}
			}
			lastBWCSize=bwc.size();
			firstTick=false;
		}
	}

	private void dropMirrorStatusByDirection(ForgeDirection direction) 
	{
		for(int i=1;i<=IHLMod.config.mirrorReflectionRange+2;i++)
		{
			TileEntity t = worldObj.getTileEntity(xCoord+direction.offsetX*i,yCoord+direction.offsetY*i, zCoord+direction.offsetZ*i);
			if(t instanceof MirrorTileEntity)
			{
				MirrorTileEntity te = (MirrorTileEntity) t;
				te.firstTick=false;
			}
			else
			{
				break;
			}
		}
		
	}

	private boolean isBlockVisible(Block block, int x, int y, int z) 
	{
		if(block instanceof MirrorBlock)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(this.getFacing());
			int x1=x*direction.offsetX;
			int y1=y*direction.offsetY;
			int z1=z*direction.offsetZ;
			int x2=xCoord*direction.offsetX;
			int y2=yCoord*direction.offsetY;
			int z2=zCoord*direction.offsetZ;
			return !(x1==x2 && y1==y2 && z1==z2);
		}
        if(block.shouldSideBeRendered(worldObj, x, y-1, z, 0))
    	{
        	return true;
    	}
        if(block.shouldSideBeRendered(worldObj, x, y+1, z, 1))
    	{
        	return true;
    	}
    	if(block.shouldSideBeRendered(worldObj, x, y, z-1, 2))
    	{
        	return true;
        }
    	if(block.shouldSideBeRendered(worldObj, x, y, z+1, 3))
    	{
        	return true;
        }
    	if(block.shouldSideBeRendered(worldObj, x-1, y, z, 4))
    	{
        	return true;
        }
    	if(block.shouldSideBeRendered(worldObj, x+1, y, z, 5))
    	{
        	return true;
    	}
		return false;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}
	
	@Override
	public void setFacing(short side) 
	{
		super.setFacing(side);
		this.bwc.clear();
		this.firstTick=true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("mirror");
	}
	
	public float[] getMirrorAxis()
	{
		ForgeDirection direction = ForgeDirection.getOrientation(this.getFacing());
		int mx = direction.offsetX!=0?-1:1;
		int my = direction.offsetY!=0?-1:1;
		int mz = direction.offsetZ!=0?-1:1;
		return new float[] {mx,my,mz,direction.offsetX,direction.offsetY,direction.offsetZ};
	}
	
	private void checkBWCList()
	{
    	if(++this.bwcListPos>=bwc.size())
    	{
    		this.bwcListPos=0;
    	}
    	if(!bwc.isEmpty())
    	{
    		BlockWithCoordinates bwcEntry = bwc.get(bwcListPos);
    		if(bwcEntry.block!=worldObj.getBlock(bwcEntry.x, bwcEntry.y, bwcEntry.z))
    		{
    			bwc.remove(bwcListPos);
    		}
    	}
	}
	
	private void checkTileEntityList()
	{
    	if(++this.reflectedTileEntityListPos>=reflectedTileEntity.size())
    	{
    		this.reflectedTileEntityListPos=0;
    	}
    	if(!reflectedTileEntity.isEmpty())
    	{
    		TileEntity entry = reflectedTileEntity.get(reflectedTileEntityListPos);
    		if(entry==null || entry.isInvalid() || entry!=worldObj.getTileEntity(entry.xCoord, entry.yCoord, entry.zCoord))
    		{
    			reflectedTileEntity.remove(reflectedTileEntityListPos);
    		}
    	}
	}
	
	private void checkReflectionVolume()
	{
    	if(++this.reflectionVolumePos>=reflectionVolumeSize)
    	{
    		this.reflectionVolumePos=0;
    		if(lastBWCSize!=bwc.size())
    		{
    			this.needRenderUpdate=true;
    			lastBWCSize=bwc.size();
    		}
    	}
    	if(reflectionVolumeSize>0)
    	{
			int range=IHLMod.config.mirrorReflectionRange;
			//reflectionVolumePos = x+y*maxX+z*maxX*maxY;
    		int z = reflectionVolumePos / (maxX-minX) / (maxY-minY);
    		int y = (reflectionVolumePos % ((maxX-minX) * (maxY-minY)))/ (maxX-minX);
    		int x = reflectionVolumePos - y * (maxX-minX) - z * (maxX-minX) * (maxY-minY);
    		//for example position is 69 volume is 4*5*5=100, therefore z will be 69 / (4*5)=3
    		//y will be (69 % (4*5))/4=9/4=2
    		//x will be 69 - 2*4 - 3*4*5 = 69-8-60=1
    		//for check 1+2*4+3*4*5=1+8+60
    		int ix = minX+x;
    		int iy = minY+y;
    		int iz = minZ+z;
    		Block block = worldObj.getBlock(ix, iy, iz);
    		if(block!=null  && !block.isAir(worldObj, ix, iy, iz) && isBlockVisible(block,ix,iy,iz))
    		{
				TileEntity te = worldObj.getTileEntity(ix, iy, iz);
				if(te!=null && !te.isInvalid() && !reflectedTileEntity.contains(te) && TileEntityRendererDispatcher.instance.hasSpecialRenderer(te) && !(te instanceof MirrorTileEntity))
				{
					reflectedTileEntity.add(te);
				}
    			if(block.getRenderType()>=0)
				{
					BlockWithCoordinates bwc1 = new BlockWithCoordinates(block, ix, iy, iz, worldObj.getBlockMetadata(ix, iy, iz));
					if(!bwc.contains(bwc1))
					{
						bwc.add(bwc1);
					}
				}
    		}
    	}
	}

	private int mXR()
			{
				switch(this.getFacing())
				{
				case 0:
				return 1;
				case 1:
				return -1;
				case 2:
				return 1;
				case 3:
				return -1;
				default:
				return 0;
				}
			}
		    
	    private int mYR()
			{
				return 0;
			}
			
	    private int mZR()
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
		public AxisAlignedBB  getRenderBoundingBox()
		{
			double xMinD=(double)this.xCoord-this.reflectExtensionRight*Math.abs(this.mXR())-this.reflectExtensionTop*Math.abs(this.mXT());
			double xMaxD=(double)this.xCoord+this.reflectExtensionRight*Math.abs(this.mXR())+this.reflectExtensionTop*Math.abs(this.mXT())+1d;
			double yMinD=(double)this.yCoord-this.reflectExtensionRight*Math.abs(this.mYR())-this.reflectExtensionTop*Math.abs(this.mYT());
			double yMaxD=(double)this.yCoord+this.reflectExtensionRight*Math.abs(this.mYR())+this.reflectExtensionTop*Math.abs(this.mYT())+1d;
			double zMinD=(double)this.zCoord-this.reflectExtensionRight*Math.abs(this.mZR())-this.reflectExtensionTop*Math.abs(this.mZT());
			double zMaxD=(double)this.zCoord+this.reflectExtensionRight*Math.abs(this.mZR())+this.reflectExtensionTop*Math.abs(this.mZT())+1d;
			return AxisAlignedBB.getBoundingBox(xMinD, yMinD, zMinD, xMaxD, yMaxD, zMaxD);		    
		}	 
	    
		@Override
	    public boolean shouldRenderInPass(int pass)
	    {
	        return pass==0;
	    }
}