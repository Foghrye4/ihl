package ihl.flexible_cable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.interfaces.ICableHolder;
import ihl.interfaces.IMultiPowerCableHolder;
import ihl.interfaces.INetworkListener;
import ihl.items_blocks.FlexibleCableItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class NodeEntity extends Entity implements INetworkListener{
	
	public NodeEntity prevAnchorEntity;
	public Entity nextAnchorEntity;
	private int anchorX;
	private int anchorY;
	private int anchorZ;
	private short anchorFacing;
	private int anchorDimensionId;
	public boolean shouldFollowPlayer=false;
	protected int chainUniqueID=-2;
	public int chainArrangeNumber=-2;
	protected int checkTimer=201;
	public int colorIndex=16777215;
	public int renderEvery=1;
	public int type=1;//0 - uninsulated wire; 1 - insulated cable; 2 - data cable
	public float dx0=0;
	public float dy0=0;
	public float dz0=0;
	public final int n=48;
	public final float[] rotationPitchArray = new float[n+1];
	public final float[] rotationYawArray = new float[n+1];
	public final float[] translationX = new float[n+1];
	public final float[] translationY = new float[n+1];
	public final float[] translationZ = new float[n+1];
	public double virtualNodePosX;
	public double virtualNodePosY;
	public double virtualNodePosZ;
	public double renderPosX;
	public double renderPosY;
	public double renderPosZ;
	public double lastTickRenderPosX;
	public double lastTickRenderPosY;
	public double lastTickRenderPosZ;
	private boolean alreadyRegistered=false;
	private boolean shouldUpdateRender=true;

	public NodeEntity(World world) 
	{
		super(world);
		if(world.isRemote)
    	{
			IHLMod.proxy.addEntityToList(this);
    		this.setSize(2f, 0.2f);
    	}
		else
		{
			IHLMod.proxy.addEntityToServerList(this);
			this.setSize(0.5F, 0.1F);
		}
		this.renderDistanceWeight = 5.0D;
		this.yOffset+=0.15F;
		this.virtualNodePosX=this.posX;
		this.virtualNodePosY=this.posY;
		this.virtualNodePosZ=this.posZ;
        this.motionX=0D;
        this.motionY=0D; 
        this.motionZ=0D;
	}
	
	@Override
	public void setInPortal(){}
	
	@Override
	public void travelToDimension(int dimensionId){}
	
	@Override
	public void setSize(float width, float heigth)
	{
		super.setSize(width, heigth);
	}
	
	public void setVirtualNodePos(double d,double e, double f)
	{
		virtualNodePosX=d;
		virtualNodePosY=e;
		virtualNodePosZ=f;
		this.registerAndSendData(null);
	}
	
	@Override
	public void registerAndSendData(EntityPlayerMP player)
	{
    	if(!worldObj.isRemote)
    	{
    		if(!alreadyRegistered)
    		{
        		Set<NodeEntity> nes;
        		if(IHLMod.proxy.nodeEntityRegistry.containsKey(this.getChainUniqueID()))
        		{
        			nes=IHLMod.proxy.nodeEntityRegistry.get(this.getChainUniqueID());
        		}
        		else
        		{
        			nes=new HashSet<NodeEntity>();
        			IHLMod.proxy.nodeEntityRegistry.put(this.getChainUniqueID(),nes);
        		}
        		nes.add(this);
        		alreadyRegistered=true;
    		}
    		ByteBuf bb = Unpooled.buffer(30); 
    		ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(bb);
    		try 
    		{
    			byteBufOutputStream.write(1);
    			byteBufOutputStream.writeInt(this.getEntityId());
    			byteBufOutputStream.writeInt(this.getChainUniqueID());
    			byteBufOutputStream.writeInt(this.chainArrangeNumber);
    			byteBufOutputStream.writeByte(this.type);
    			byteBufOutputStream.writeInt(this.colorIndex);
    			byteBufOutputStream.writeDouble(this.virtualNodePosX);
    			byteBufOutputStream.writeDouble(this.virtualNodePosY);
    			byteBufOutputStream.writeDouble(this.virtualNodePosZ);
    			byteBufOutputStream.writeBoolean(this.shouldFollowPlayer);
    			if(player==null)
    			{
        			IHLMod.proxy.sendFromServerToAll(new FMLProxyPacket(byteBufOutputStream.buffer(), IHLModInfo.MODID));
    			}
    			else
    			{
        			IHLMod.proxy.sendFromServerToPlayer(new FMLProxyPacket(byteBufOutputStream.buffer(), IHLModInfo.MODID),player);
    			}
    			byteBufOutputStream.close();
    		} 
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    	}		
	}
	
    @SuppressWarnings("unchecked")
	@Override
	public void onUpdate()
    {
        super.onUpdate();
		if(this.checkTimer==201)
		{
			if(worldObj.isRemote)
			{
				IHLMod.proxy.recieveDelayedDataPacket(this);
			}
			else
			{
				this.registerAndSendData(null);
			}
		}
        if(prevAnchorEntity==null||(nextAnchorEntity==null || nextAnchorEntity instanceof EntityPlayer || nextAnchorEntity instanceof EntityItem))
        {
    			double range = 16D;
    			AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.posX-range,this.posY-range,this.posZ-range,this.posX+range,this.posY+range,this.posZ+range);
    			List<NodeEntity> eItemsList = this.worldObj.getEntitiesWithinAABB(NodeEntity.class, searchArea);
    			if(!eItemsList.isEmpty())
    			{
    				Iterator<NodeEntity> ei = eItemsList.iterator();
                	while(ei.hasNext())
                	{
                		NodeEntity node=(NodeEntity) ei.next();
                		if(node.getChainUniqueID()==this.getChainUniqueID())
                		{
                			if(node.chainArrangeNumber==this.chainArrangeNumber-1)
                			{
                				this.prevAnchorEntity=node;
                				node.nextAnchorEntity=this;
                				node.shouldFollowPlayer=false;
                			}
                			else if(node.chainArrangeNumber==this.chainArrangeNumber+1)
                			{
                				this.nextAnchorEntity=node;
                				this.shouldFollowPlayer=false;
                				node.prevAnchorEntity=this;
                			}
                		}
            			if(prevAnchorEntity!=null && nextAnchorEntity!=null)
            			{
            				break;
            			}
                	}
    			}
        }
    	if(this.shouldFollowPlayer && (this.nextAnchorEntity == null || this.nextAnchorEntity.isDead))
    	{
			double range = 16D;
			AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.posX-range,this.posY-range,this.posZ-range,this.posX+range,this.posY+range,this.posZ+range);
			List<EntityPlayer> eItemsList = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, searchArea);
			if(!eItemsList.isEmpty())
			{
				Iterator<EntityPlayer> ei = eItemsList.iterator();
            	while(ei.hasNext())
            	{
            		EntityPlayer player=(EntityPlayer) ei.next();
            		if(this.playerHasItemStack(player))
            		{
            			this.nextAnchorEntity=player;
            		}
            		
            	}
			}
    	}
    	if(this.shouldFollowPlayer && this.nextAnchorEntity instanceof EntityPlayer)
    	{
    		EntityPlayer player = (EntityPlayer) this.nextAnchorEntity;
    		if(!this.playerHasItemStack(player))
    		{
    			double range = 16D;
    			AxisAlignedBB searchArea = AxisAlignedBB.getBoundingBox(this.posX-range,this.posY-range,this.posZ-range,this.posX+range,this.posY+range,this.posZ+range);
    			List<EntityItem> eItemsList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, searchArea);
    			if(!eItemsList.isEmpty())
    			{
    				Iterator<EntityItem> ei = eItemsList.iterator();
                	while(ei.hasNext())
                	{
                		EntityItem eitem = ei.next();
                		if(this.isItemHasSameChainId(eitem.getEntityItem()))
                		{
                			this.nextAnchorEntity=eitem;
                			break;
                		}
                	}
    			}
    		}
    	}
		if(nextAnchorEntity!=null && this.getDistanceSqToEntity(nextAnchorEntity)>2D)
		{
				this.nextAnchorEntity.addVelocity((this.posX-this.nextAnchorEntity.posX)*0.02D, (this.posY-this.nextAnchorEntity.posY)*0.02D, (this.posZ-this.nextAnchorEntity.posZ)*0.02D);
		}
    	if(!worldObj.isRemote)
        {
        		double x0,x2,y0,y2,z0,z2;
        		x2=x0=this.virtualNodePosX;
        		y2=y0=this.virtualNodePosY;
        		z2=z0=this.virtualNodePosZ;
        		if(nextAnchorEntity!=null)
        		{
        			x2=nextAnchorEntity.posX;
        			y2=nextAnchorEntity.posY;
        			z2=nextAnchorEntity.posZ;
        		}
        		if(prevAnchorEntity!=null)
        		{
        			x0=prevAnchorEntity.posX;
        			y0=prevAnchorEntity.posY;
        			z0=prevAnchorEntity.posZ;
        		}
    			double d1 = (x0-x2)*(x0-x2)+(y0-y2)*(y0-y2)+(z0-z2)*(z0-z2);
            	if(d1>4D)
            	{
            		this.motionX+=(x0+x2)*0.05D-this.posX*0.1D;
            		this.motionY+=(y0+y2)*0.05D-this.posY*0.1D;
            		this.motionZ+=(z0+z2)*0.05D-this.posZ*0.1D;
            	}
            	else
            	{
            		double d2 = this.getDistanceSq(x2,y2,z2);
            		if(d2>1D)
            		{
            			this.motionX+=(x2-this.posX)*0.01D;
            			this.motionY+=(y2-this.posY)*0.01D;
            			this.motionZ+=(z2-this.posZ)*0.01D;
            		}
            		double d3 = this.getDistanceSq(x0,y0,z0);
            		if(d3>1D)
            		{
            			this.motionX+=(x0-this.posX)*0.01D;
            			this.motionY+=(y0-this.posY)*0.01D;
            			this.motionZ+=(z0-this.posZ)*0.01D;
            		}
            	}
    			this.motionY-=0.005D;
    	        this.motionX*=0.8D;
    	        this.motionY*=0.8D; 
    	        this.motionZ*=0.8D;
    	    	this.moveEntity(this.motionX, this.motionY, this.motionZ);
        }
        if(this.onGround)
        {
            this.motionY *= -0.5D;
        }
        if(worldObj.isRemote)
        {
        	this.lastTickRenderPosX=this.renderPosX;
        	this.lastTickRenderPosY=this.renderPosY;
        	this.lastTickRenderPosZ=this.renderPosZ;
        	this.renderPosX=(float) this.prevPosX;
        	this.renderPosY=(float) this.prevPosY;
        	this.renderPosZ=(float) this.prevPosZ;
        	shouldUpdateRender=
        			Math.abs(this.lastTickRenderPosX-this.renderPosX)+
        			Math.abs(this.lastTickRenderPosY-this.renderPosY)+
        			Math.abs(this.lastTickRenderPosZ-this.renderPosZ)>0.01f;
        	
			float xi,yi,zi;
			float x0=xi=(float)this.prevPosX;
			float y0=yi=(float)this.prevPosY;
			float z0=zi=(float)this.prevPosZ;
			float dx0=this.dx0;
			float dy0=this.dy0;
			float dz0=this.dz0;
			float dx1=(float) (this.virtualNodePosX-xi);
			float dy1=(float) (this.virtualNodePosY-yi);
			float dz1=(float) (this.virtualNodePosZ-zi);
			if(this.nextAnchorEntity!=null)
			{
				dx1=(float) (this.nextAnchorEntity.prevPosX-xi);
				dy1=(float) (this.nextAnchorEntity.prevPosY-yi);
				dz1=(float) (this.nextAnchorEntity.prevPosZ-zi);
			}
			if(this.prevAnchorEntity==null)
			{
				this.renderPosX=this.virtualNodePosX;
				this.renderPosY=this.virtualNodePosY;
				this.renderPosZ=this.virtualNodePosZ;
				x0=xi=(float) this.virtualNodePosX;
				y0=yi=(float) this.virtualNodePosY;
				z0=zi=(float) this.virtualNodePosZ;
			}
			if(this.nextAnchorEntity instanceof NodeEntity)
			{
				shouldUpdateRender=this.shouldUpdateRender || ((NodeEntity)this.nextAnchorEntity).shouldUpdateRender;
				if(((NodeEntity)this.nextAnchorEntity).nextAnchorEntity!=null)
				{
					Entity nne = ((NodeEntity)this.nextAnchorEntity).nextAnchorEntity;
					dx1=(float) (nne.prevPosX-xi);
					dy1=(float) (nne.prevPosY-yi);
					dz1=(float) (nne.prevPosZ-zi);
				}
				else
				{
					dx1=(float) (((NodeEntity) this.nextAnchorEntity).virtualNodePosX-xi);
					dy1=(float) (((NodeEntity) this.nextAnchorEntity).virtualNodePosY-yi);
					dz1=(float) (((NodeEntity) this.nextAnchorEntity).virtualNodePosZ-zi);
				}
			}
			if(shouldUpdateRender)
			{
				float x1=(float) this.virtualNodePosX;
				float y1=(float) this.virtualNodePosY;
				float z1=(float) this.virtualNodePosZ;
				if(this.nextAnchorEntity!=null)
				{
					if(nextAnchorEntity instanceof NodeEntity)
					{
						x1=(float) ((NodeEntity) nextAnchorEntity).renderPosX;
						y1=(float) ((NodeEntity) nextAnchorEntity).renderPosY;
						z1=(float) ((NodeEntity) nextAnchorEntity).renderPosZ;
    				}
    				else
    				{
        				x1=(float) nextAnchorEntity.posX;
        				y1=(float) nextAnchorEntity.posY;
        				z1=(float) nextAnchorEntity.posZ;
    				}
				}
				float d = (x0-x1)*(x0-x1)+(y0-y1)*(y0-y1)+(z0-z1)*(z0-z1);
				if(d>2f)
				{
					renderEvery=1;
				}/*
				else if(d>0.5f)
				{
					renderEvery=2;
				}
				else
				{
					renderEvery=4;
				}*/
				int i1=0;
				for(float i=1f/n;i<=1f+1f/n;i+=((float)renderEvery)/n,i1+=renderEvery)
				{
					float dxi = xi;
					float dyi = yi;
					float dzi = zi;
					xi=(dx1-2*x1+2*dx0+2*x0-dx0)*i*i*i+(3*x1-dx1-3*dx0-3*x0+dx0)*i*i+dx0*i+x0;
					yi=(dy1-2*y1+2*dy0+2*y0-dy0)*i*i*i+(3*y1-dy1-3*dy0-3*y0+dy0)*i*i+dy0*i+y0;
					zi=(dz1-2*z1+2*dz0+2*z0-dz0)*i*i*i+(3*z1-dz1-3*dz0-3*z0+dz0)*i*i+dz0*i+z0;
					dxi-=xi;
					dyi-=yi;
					dzi-=zi;
	            	double var7 = MathHelper.sqrt_double(dxi * dxi + dzi * dzi);
	            	float rotationPitch = (float) Math.atan2(dxi, dzi);
	            	float rotationYaw = (float) (-Math.atan2(dyi, var7));
	            	rotationPitchArray[i1]=rotationPitch;
	            	rotationYawArray[i1]=rotationYaw;
	            	translationX[i1]=dxi;
	            	translationY[i1]=dyi;
	            	translationZ[i1]=dzi;
				}
				if(this.nextAnchorEntity instanceof NodeEntity)
				{
					NodeEntity next = (NodeEntity) this.nextAnchorEntity;
					next.dx0=dx1;
					next.dy0=dy1;
					next.dz0=dz1;
				}
			}
		}
        if(--this.checkTimer<=0)
        {
        	if(!worldObj.isRemote)
        	{
        		WorldServer world = MinecraftServer.getServer().worldServerForDimension(this.anchorDimensionId);
        		TileEntity te = world.getTileEntity(this.anchorX, this.anchorY, this.anchorZ);
				if(te==null || !(te instanceof ICableHolder || te instanceof IMultiPowerCableHolder))
				{
					this.setDead();
				}
				else
				{
					if(te instanceof ICableHolder)
					{
						if(((ICableHolder)te).isCableRemoved(this.chainUniqueID))
						{
							this.setDead();
						}
					}
					if(te instanceof IMultiPowerCableHolder)
					{
						if(((IMultiPowerCableHolder)te).isCableRemoved(this.chainUniqueID))
						{
							this.setDead();
						}
					}
				}
        	}
			this.checkTimer=200;
        }
    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) 
	{
		this.setChainUniqueID(nbt.getInteger("chainUniqueID"));
		this.chainArrangeNumber=nbt.getInteger("chainArrangeNumber");
		this.anchorX=nbt.getInteger("anchorX");
		this.anchorY=nbt.getInteger("anchorY");
		this.anchorZ=nbt.getInteger("anchorZ");
		this.anchorFacing=nbt.getShort("anchorFacing");
		this.anchorDimensionId=nbt.getInteger("anchorDimensionId");
		this.type=nbt.getInteger("type");
		this.colorIndex=nbt.getInteger("colorIndex");
		this.shouldFollowPlayer=nbt.getBoolean("shouldFollowPlayer");
		if(nbt.hasKey("width"))
		{
			this.setSize(nbt.getFloat("width"), nbt.getFloat("height"));
		}
		this.virtualNodePosX=nbt.getDouble("virtualNodePosX");
		this.virtualNodePosY=nbt.getDouble("virtualNodePosY");
		this.virtualNodePosZ=nbt.getDouble("virtualNodePosZ");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) 
	{
		nbt.setFloat("width", this.width);
		nbt.setFloat("height", this.height);
		nbt.setInteger("chainUniqueID", this.getChainUniqueID());
		nbt.setInteger("chainArrangeNumber", this.chainArrangeNumber);
		nbt.setInteger("anchorX", this.anchorX);
		nbt.setInteger("anchorY", this.anchorY);
		nbt.setInteger("anchorZ", this.anchorZ);
		nbt.setShort("anchorFacing",this.anchorFacing);
		nbt.setInteger("anchorDimensionId",this.anchorDimensionId);
		nbt.setInteger("type", this.type);
		nbt.setInteger("colorIndex", this.colorIndex);
		nbt.setBoolean("shouldFollowPlayer",this.shouldFollowPlayer);
		nbt.setDouble("virtualNodePosX",this.virtualNodePosX);
		nbt.setDouble("virtualNodePosY",this.virtualNodePosY);
		nbt.setDouble("virtualNodePosZ",this.virtualNodePosZ);
	}
	
	public void setAnchor(int x, int y, int z, short facing, int dimensionId)
	{
		this.anchorX=x;
		this.anchorY=y;
		this.anchorZ=z;
		this.anchorFacing=facing;
		this.anchorDimensionId=dimensionId;
	}
	
    public boolean playerHasItemStack(EntityPlayer player)
    {
        int var2;
        for (var2 = 0; var2 < player.inventory.mainInventory.length; ++var2)
        {
            if(this.isItemHasSameChainId(player.inventory.mainInventory[var2]))
            {
                return true;
            }
        }
        return false;
    }

	private boolean isItemHasSameChainId(ItemStack itemStack) 
	{
		if(itemStack!=null)
		{
			if(itemStack.getItem() instanceof FlexibleCableItem)
			{
				return itemStack.stackTagCompound.getInteger("chainUID") == this.chainUniqueID;
			}
		}
		return false;
	}

	public int getChainUniqueID() {
		return chainUniqueID;
	}

	public void setChainUniqueID(int chainUniqueID) {
		this.chainUniqueID = chainUniqueID;
	}
	
	@Override
	public int getId() 
	{
		return this.getEntityId();
	}

	@Override
	public void recieveData(ByteBufInputStream byteBufInputStream) 
	{
   		try 
   		{
			this.setChainUniqueID(byteBufInputStream.readInt());
	   		this.chainArrangeNumber=byteBufInputStream.readInt();
	   		this.type=byteBufInputStream.readByte();
			this.colorIndex=byteBufInputStream.readInt();
			this.virtualNodePosX=byteBufInputStream.readDouble();
			this.virtualNodePosY=byteBufInputStream.readDouble();
			this.virtualNodePosZ=byteBufInputStream.readDouble();
			this.shouldFollowPlayer=byteBufInputStream.readBoolean();
		}
   		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void entityInit() {}

	@Override
	public boolean isInvalid() 
	{
		return this.isDead;
	}

	public void setVirtualNodePosToNearestPortal() 
	{
		int x0 = (int)this.posX;
		int y0 = (int)this.posY;
		int z0 = (int)this.posZ;
		for(int xi=x0-2;xi<=x0+2;xi++)
		{
			for(int yi=y0-2;yi<=y0+2;yi++)
			{
				for(int zi=z0-2;zi<=z0+2;zi++)
				{
					Block block = worldObj.getBlock(xi, yi, zi);
					if(block==Blocks.portal||block==Blocks.end_portal)
					{
						this.setVirtualNodePos(xi+0.5d, yi+0.5d, zi+0.5d);
						return;
					}
				}
			}
		}
		
	}
}
