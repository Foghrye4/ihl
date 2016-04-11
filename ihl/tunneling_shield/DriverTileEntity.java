package ihl.tunneling_shield;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.IC2Items;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import ihl.IHLMod;
import ihl.utils.IHLInvSlotDischarge;
import ihl.utils.IHLUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class DriverTileEntity extends TileEntityInventory implements IEnergySink, IHasGui, INetworkClientTileEntityEventListener, IUpgradableBlock, IMultiBlock {
	
    private AudioSource audioSource;
    private AudioSource startAS;
    private AudioSource loopAS;
    private AudioSource stopAS;
    private int startUpCounter=0;
    public final InvSlotConsumableId miningPipesSlot;
    public final InvSlotUpgrade upgradeSlot;
    public final IHLInvSlotDischarge dischargeSlot;
    public int tier;
    private int defaultTier;
    public int maxStorage;
    private int defaultMaxStorage;
	public boolean hasShield=false;
	private boolean prevHasShield=false;
	public int shaftDestroyedAtA=0;
	private int prevShaftDestroyedAtA=0;
	public int shaftDestroyedAtB=0;
	private int prevShaftDestroyedAtB=0;
	public int modelShift=0;
	public int modelShiftSpeed;
    private int defaultModelShiftSpeed;
	public int blockShift=1;
	private int lastBlockShift=1;
	private double energy = 0;
	private double energyConsume;
    private double defaultEnergyConsume;
	private boolean addedToEnergyNet=false;
	public int xShaftMin, xShaftMax, yShaft, zShaftMin, zShaftMax, xShieldMin, xShieldMax, yShieldMin, yShieldMax, zShieldMin, zShieldMax;
	private int updateChecksum=0;
	private boolean renderHelperExist=false;
	public DriverEntity renderHelper;
	public boolean reverseModeOn=false;
	public boolean autoReverseModeOn=true;
	private float lastModelShiftAmount=1F;
	private List<Block> blockBlackList = new ArrayList();
	private List<Block> blockWhiteList = new ArrayList();
	public boolean advancedShield=false;
	
	public DriverTileEntity()
	{
        super();
        this.miningPipesSlot = new InvSlotConsumableId(this, "miningPipesSlot", 1, 12, new Item[] {Ic2Items.miningPipe.getItem()});
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 2, 4);
        this.dischargeSlot = new IHLInvSlotDischarge(this, 1, Access.IO, this.tier, InvSlot.InvSide.BOTTOM);
        this.defaultTier=IHLMod.config.ts02DefaultTier;
        this.defaultMaxStorage=IHLMod.config.ts02DefaultMaxEnergyStorage;
        this.defaultModelShiftSpeed=IHLMod.config.ts02DefaultSpeed;
        this.defaultEnergyConsume=IHLMod.config.ts02DefaultOperationEUCost;
        this.tier=this.defaultTier;
        this.maxStorage=this.defaultMaxStorage;
        this.modelShiftSpeed=this.defaultModelShiftSpeed;
        this.energyConsume=this.defaultEnergyConsume;
        for(int i6=0;i6<IHLMod.config.ts02BlockBlackListString.length;i6++)
        {
        	String blockName = IHLMod.config.ts02BlockBlackListString[i6];
        	Block block = Block.getBlockFromName(blockName);
        	if(block==null)
        	{
        		ItemStack stack = IC2Items.getItem(blockName);
        		if(stack!=null)
        		{
        			block = StackUtil.getBlock(stack);
        		}
        	}
        	if(block!=null)
        	{
        		blockBlackList.add(block);
        	}
        }
        for(int i7=0;i7<IHLMod.config.ts02BlockWhiteListString.length;i7++)
        {
        	String blockName = IHLMod.config.ts02BlockWhiteListString[i7];
        	Block block = Block.getBlockFromName(blockName);
        	if(block==null)
        	{
        		ItemStack stack = IC2Items.getItem(blockName);
        		if(stack!=null)
        		{
        			block = StackUtil.getBlock(stack);
        		}
        	}
        	if(block!=null)
        	{
        		blockWhiteList.add(block);
        	}

        }
        blockWhiteList.add(IHLMod.multiBlockSpacerBlock);
        blockWhiteList.add(IHLMod.horizontalMiningPipeX);
        blockWhiteList.add(IHLMod.horizontalMiningPipeZ);
        blockWhiteList.add(Blocks.air);
   	}

	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("hasShield");
		fields.add("modelShift");
		fields.add("shaftDestroyedAtA");
		fields.add("shaftDestroyedAtB");
		fields.add("tier");
		fields.add("modelShiftSpeed");
		fields.add("maxStorage");
		fields.add("reverseModeOn");
		fields.add("autoReverseModeOn");
		fields.add("advancedShield");
		return fields;
    }
	
    public String getShieldSoundFile()
    {
        return "Machines/MinerOp.ogg";
    }
    
    public String getStartSoundFile()
    {
        return "Machines/IHL Tunneling Shield/TS02Start.ogg";
    }
    
    public String getLoopSoundFile()
    {
        return "Machines/IHL Tunneling Shield/TS02Loop.ogg";
    }

    public String getStopSoundFile()
    {
        return "Machines/IHL Tunneling Shield/TS02Stop.ogg";
    }
    
    @Override
	public void onLoaded()
    {
        super.onLoaded();
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
        if (IC2.platform.isRendering() && (this.startAS==null||this.loopAS==null||this.stopAS==null))
        {
        	this.startAS = IC2.audioManager.createSource(this, PositionSpec.Center, this.getStartSoundFile(),false,false, 0.5F);
        	this.loopAS = IC2.audioManager.createSource(this, PositionSpec.Center, this.getLoopSoundFile(),true,false, 0.5F);
        	this.stopAS = IC2.audioManager.createSource(this, PositionSpec.Center, this.getStopSoundFile(),false,false, 0.5F);
        }
    }
    
    @Override
	public void onUnloaded()
    {
        super.onUnloaded();
        
        if (IC2.platform.isRendering() && this.audioSource != null)
        {
            this.audioSource.stop();
            this.audioSource = null;
            this.startAS.stop();
			this.loopAS.stop();
			this.stopAS.stop();
            this.startAS = null;
            this.loopAS = null;
            this.stopAS = null;
            IC2.audioManager.removeSources(this);
        }
        
        if (IC2.platform.isSimulating()&&this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
    }
    
    
    @Override
	public void updateEntityClient()
    {
    	super.updateEntityClient();
    	updateEntityServer();
    	if(!this.renderHelperExist)
    	{
    			List<DriverEntity> deList = this.worldObj.getEntitiesWithinAABB(DriverEntity.class, this.getOperationZone());
    			if(deList.size()>0)
    			{
    				Iterator<DriverEntity> dei = deList.iterator();
    				while(dei.hasNext())
    				{
    					DriverEntity rh=dei.next();
    					if(!rh.isDead)
    					{
    	    				this.renderHelper=rh;
    	    				rh.parent=this;
    	    				this.renderHelperExist=true;
    	    				break;
    					}
    				}
    				if(!this.renderHelperExist)
    				{
            			this.renderHelper=new DriverEntity(this.worldObj, this, this.xCoord+this.blockShift*this.mX(), this.yCoord, this.zCoord+this.blockShift*this.mZ());
            			this.renderHelperExist=true;
    				}
    			}
    			else
    			{
        			this.renderHelper=new DriverEntity(this.worldObj, this, this.xCoord+this.blockShift*this.mX(), this.yCoord, this.zCoord+this.blockShift*this.mZ());
        			this.renderHelper.parent=this;
        			this.renderHelperExist=true;
    			}
    	}
    	
    	if(this.renderHelper!=null && !this.renderHelper.isDead && !this.worldObj.loadedEntityList.contains(this.renderHelper))
    	{
    		this.worldObj.spawnEntityInWorld(this.renderHelper);
    	}
        if(this.audioSource==null && this.renderHelper!=null)this.audioSource = IC2.audioManager.createSource(this.renderHelper, PositionSpec.Center, this.getShieldSoundFile(),true,false, 1.0F);
    	if(this.audioSource!=null)
    	{
            this.audioSource.updatePosition();
    	}
        if(this.audioSource!=null)
        {
        	if(this.getActive())
        	{
        		if(this.startUpCounter>62)
        		{  
        			this.startUpCounter=0;
        		}
        		if(this.startUpCounter==0)
        		{        		
        			this.stopAS.stop();
        			this.startAS.play();
        		}
        		if(this.startUpCounter<60)
        		{
        			this.startUpCounter++;
        		}
        		else if(this.startUpCounter==60)
        		{
        			this.startAS.stop();
        			this.loopAS.play();
        			this.audioSource.play();
        			this.startUpCounter++;
        		}
        	}
        	else if(this.startUpCounter>0)
        	{
        		this.startAS.stop();
        		this.loopAS.stop();
        		this.audioSource.stop();
        		this.stopAS.play();
        		if(this.startUpCounter<100)
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
        else if(!this.getActive() && this.audioSource!=null)
        {
        	this.audioSource.stop();
        }
    }
    
    @Override
	public void updateEntityServer()
    {
        if (IC2.platform.isSimulating())
        {
            this.setOverclockRates();
        }
        if(this.dischargeSlot.tier!=this.tier)
        {
        	this.dischargeSlot.tier=this.tier;
        }
        int xyz[]={0,0,1,0,-1,-1,1,1,-1,2,0,2,1,2,-1,-2,0,-2,1,-2,-1,0};
        boolean freeToDigForward = true;
        boolean freeToMoveBack = true;
        if(this.blockShift>=127)
        {
        	freeToDigForward = false;
        	if(this.autoReverseModeOn && !this.reverseModeOn && this.worldObj.isRemote)
        	{
        		this.reverseModeOn=true;
        		IC2.network.get().initiateClientTileEntityEvent(this, 0);
        	}
        }
        else
        {
        	for(int i = 0;i<21;i++)
        	{
        		Block block = this.worldObj.getBlock(this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift+1)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift+1)*this.mZ());
        		if(this.blockBlackList.contains(block))
        		{
        			freeToDigForward = false;
        		}
        	}
        }
        
    	if(this.blockShift<=3)
    	{
			freeToMoveBack = false;
    	}
    	else
    	{
    		for(int i = 0;i<21;i++)
    		{
    			Block block = this.worldObj.getBlock(this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-3)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-3)*this.mZ());
    			if(block.getCollisionBoundingBoxFromPool(worldObj, this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-3)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-3)*this.mZ())!=null && !block.getMaterial().isLiquid() && !this.blockWhiteList.contains(block))
    			{
    				freeToMoveBack = false;
    			}
    			block = this.worldObj.getBlock(this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-2)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-2)*this.mZ());
    			if(block.getCollisionBoundingBoxFromPool(worldObj, this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-2)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-2)*this.mZ())!=null && !block.getMaterial().isLiquid() && !this.blockWhiteList.contains(block))
    			{
    				freeToMoveBack = false;
    			}
    		}
    	}

        if(IC2.platform.isSimulating())
        {
        	if(this.hasShield && this.shaftDestroyedAtA==0 && this.energy>=this.energyConsume)
        	{
        		if(this.reverseModeOn && freeToMoveBack)
        		{
           			this.energy-=this.energyConsume;
           			this.setActive(true);
        		}
        		else if((!this.reverseModeOn) && freeToDigForward && this.miningPipesSlot.get()!=null && this.miningPipesSlot.get().stackSize>=1)
        		{
           			this.energy-=this.energyConsume;
           			this.setActive(true);
        		}
        		else
        		{
           			this.setActive(false);
        		}
        	}
    		else
    		{
       			this.setActive(false);
    		}
        }
        if(this.getActive())
        {
        	double velocity;
        	if(this.reverseModeOn && this.modelShift>0)
        	{
        		this.modelShift--;
       			List<Entity> entityList = this.worldObj.getEntitiesWithinAABB(Entity.class, this.getShieldBladesZone(0D));
				velocity = -0.5D;
       			if(entityList.size()>0)
       			{
       				for(int i0=0;i0<entityList.size();i0++)
       				{
       					entityList.get(i0).addVelocity(velocity*mX(), 0.25D, velocity*mZ());
       				}
       			}
        	}
        	else
        	{
        		this.modelShift++;
       			List<EntityLivingBase> entityLivingList = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getShieldBladesZone(2.5D));
				float damage = 800F/(this.modelShiftSpeed);
				velocity = 0.1D*damage;
       			if(entityLivingList.size()>0)
       			{
       				for(int i1=0;i1<entityLivingList.size();i1++)
       				{
       					entityLivingList.get(i1).attackEntityFrom(DamageSource.fallingBlock, damage);
       					entityLivingList.get(i1).addVelocity(velocity*mX(), 0.25D*velocity, velocity*mZ());
       				}
       			}
       			List<EntityLiving> entityLivingList2 = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.getShieldBladesZone(2.5D));
       			velocity*=0.1D;
       			if(entityLivingList2.size()>0)
       			{
       				for(int i2=0;i2<entityLivingList2.size();i2++)
       				{
       					entityLivingList2.get(i2).attackEntityFrom(DamageSource.fallingBlock, damage);
       					entityLivingList2.get(i2).addVelocity(velocity*mX(), 0.25D*velocity, velocity*mZ());
       				}
       			}
        	}
        }        
    	this.blockShift=Math.round(this.getModelShiftAmount()+1.0F+(this.reverseModeOn?2.0F:0));
        if (IC2.platform.isSimulating())
        {
            if (this.prevHasShield!= this.hasShield)
            {
                IC2.network.get().updateTileEntityField(this, "hasShield");
                this.prevHasShield = this.hasShield;
            }
            if (this.prevShaftDestroyedAtA!= this.shaftDestroyedAtA)
            {
                IC2.network.get().updateTileEntityField(this, "shaftDestroyedAtA");
                this.prevShaftDestroyedAtA=this.shaftDestroyedAtA;
            }
            if (this.prevShaftDestroyedAtB!= this.shaftDestroyedAtB)
            {
                IC2.network.get().updateTileEntityField(this, "shaftDestroyedAtB");
                this.prevShaftDestroyedAtB=this.shaftDestroyedAtB;
            }
        	if(this.getDemandedEnergy() > 1.0D)
        	{
        		double amount = this.dischargeSlot.discharge(this.getDemandedEnergy(), false);
        		this.energy += amount;
        	}
            if(this.energy>this.maxStorage)
            {
            	this.energy=this.maxStorage;
            }
        }

        if(this.blockShift!=this.lastBlockShift)
        {
        	if(this.getActive() && this.reverseModeOn && IC2.platform.isSimulating() && this.lastModelShiftAmount-this.getModelShiftAmount()<1F)
        	{
        		if(this.miningPipesSlot.get()==null)
        		{
            		this.miningPipesSlot.put(Ic2Items.miningPipe.copy());
        		}
        		else if(this.miningPipesSlot.get().stackSize<64)
        		{
            		this.miningPipesSlot.get().stackSize++;
        		}
        		else
        		{
        			this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj,this.xCoord,this.yCoord+1,this.zCoord,this.miningPipesSlot.get()));
            		this.miningPipesSlot.put(Ic2Items.miningPipe.copy());
        		}
        		this.lastModelShiftAmount-=1F;
        	}
        	else if(this.getActive() && IC2.platform.isSimulating() && this.getModelShiftAmount()-this.lastModelShiftAmount>1F)
        	{
                this.miningPipesSlot.consume(1);
                this.lastModelShiftAmount+=1F;
                IC2.network.get().updateTileEntityField(this, "modelShift");
        	}
        	this.defineTechBlockVariables();
   			if(this.getActive() && IC2.platform.isSimulating())
        	{
   				if(this.reverseModeOn)
   				{
   					for(int i = 0;i<21;i++)
   					{
   						if(this.blockShift>=2)
   						{
   							this.worldObj.setBlockToAir(this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift)*this.mZ());
   						}
   						if(this.blockShift>=3)
   						{
   							Block block = this.worldObj.getBlock(this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-2)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-2)*this.mZ());
   							if(block!=IHLMod.multiBlockSpacerBlock && block!=IHLMod.horizontalMiningPipeX && block!=IHLMod.horizontalMiningPipeZ)
   							{
   								this.harvestDrops(block, this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-2)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-2)*this.mZ(), this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-3)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-3)*this.mZ(), IHLMod.multiBlockSpacerBlock);
   							}
   							else
   							{
   	   							this.worldObj.setBlock(this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-2)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-2)*this.mZ(), IHLMod.multiBlockSpacerBlock);
   							}
   						}
   					}
   				}
   				else
   				{
   					for(int i = 0;i<21;i++)
   					{
   						Block block = this.worldObj.getBlock(this.xCoord+xyz[i+1]*this.mZ()+this.blockShift*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+this.blockShift*this.mZ());
   						this.harvestMiningDrops(block, this.xCoord+xyz[i+1]*this.mZ()+this.blockShift*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+this.blockShift*this.mZ(), this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-2)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-2)*this.mZ(), IHLMod.multiBlockSpacerBlock);
   						if(this.blockShift>=3)
   						{
   							if(i!=0)
   							{
   								this.worldObj.setBlockToAir(this.xCoord+xyz[i+1]*this.mZ()+(this.blockShift-2)*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+(this.blockShift-2)*this.mZ());
   							}
   							else
   							{
   								this.worldObj.setBlock(this.xCoord+(this.blockShift-2)*this.mX(), this.yCoord, this.zCoord+(this.blockShift-2)*this.mZ(), this.mX()!=0?IHLMod.horizontalMiningPipeZ:IHLMod.horizontalMiningPipeX);
   							}
   						}
                
   					}
            	}
        	}
            this.lastBlockShift=this.blockShift;
        }
    }

	private void harvestMiningDrops(Block block, int x, int y, int z, int x1, int y1, int z1, Block setToBlock)
	{
		if(block!=null && block!=Blocks.air && block!=Blocks.bedrock)
		{
			int meta = worldObj.getBlockMetadata(x, y, z);
			ArrayList<ItemStack> drops = block.getDrops(this.worldObj, x, y, z, meta, 0);
			if(drops.size()>0)
			{ 
				if(!this.advancedShield || !this.handleDropsByHydrotransport(drops))
				{
					for(int i1=0;i1<drops.size();i1++)
					{
						this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, x1, y1, z1, drops.get(i1)));
					}
				}
			}
		}
		this.worldObj.setBlock(x, y, z, setToBlock);
	}

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
			double correction = this.reverseModeOn?0.1D:1D;
			float newModelShiftSpeed=(float)Math.max(correction*this.defaultModelShiftSpeed*Math.pow(0.7D, speedUp),10D);
			this.modelShift=Math.round(this.modelShift*newModelShiftSpeed/this.modelShiftSpeed);
           	this.modelShiftSpeed=Math.round(newModelShiftSpeed);
			this.tier=this.defaultTier+tierUp;
			IC2.network.get().updateTileEntityField(this, "tier");
			this.maxStorage=this.defaultMaxStorage + capacityUp*10000;
	        IC2.network.get().updateTileEntityField(this, "maxStorage");
			this.energyConsume=Math.min(correction*this.defaultEnergyConsume*Math.pow(1.6D, speedUp),this.maxStorage);
			IC2.network.get().updateTileEntityField(this, "modelShift");
			IC2.network.get().updateTileEntityField(this, "modelShiftSpeed");
	        this.updateChecksum=checksum;
		};
    }

	@Override
	public void writeToNBT(NBTTagCompound var1) {
        super.writeToNBT(var1);
        var1.setDouble("energy", this.energy);
        var1.setBoolean("hasShield", this.hasShield);
        var1.setBoolean("autoReverseModeOn", this.autoReverseModeOn);
        var1.setBoolean("reverseModeOn", this.reverseModeOn);
        var1.setBoolean("advancedShield", this.advancedShield);
        var1.setInteger("modelShift", this.modelShift);
        var1.setInteger("shaftDestroyedAtA", this.shaftDestroyedAtA);
        var1.setInteger("shaftDestroyedAtB", this.shaftDestroyedAtB);
        var1.setInteger("modelShiftSpeed", this.modelShiftSpeed);
	}

	@Override
	public void readFromNBT(NBTTagCompound var1) {
        super.readFromNBT(var1);
        this.hasShield=var1.getBoolean("hasShield");
        this.autoReverseModeOn=var1.getBoolean("autoReverseModeOn");
   		this.reverseModeOn=var1.getBoolean("reverseModeOn");
   		this.advancedShield=var1.getBoolean("advancedShield");
        this.modelShift=var1.getInteger("modelShift");
        this.modelShiftSpeed=var1.getInteger("modelShiftSpeed");
        this.energy=var1.getDouble("energy");
        this.shaftDestroyedAtA=var1.getInteger("shaftDestroyedAtA");
        this.shaftDestroyedAtB=var1.getInteger("shaftDestroyedAtB");
		if(this.hasShield)
		{
			if(!MultiBlockSpacerBlock.teList.contains(this))
			{
				MultiBlockSpacerBlock.teList.add(this);
			}
			if(this.mX()==0)
			{
				if(!HorizontalMiningPipe.teList.contains(this))
				{
					HorizontalMiningPipe.teList.add(this);
				}
			}
			else
			{
				if(!HorizontalMiningPipe.teList.contains(this))
				{
					HorizontalMiningPipe.teList.add(this);
				}
			}
		}
    	float shift=(float)this.modelShift/(float)this.modelShiftSpeed+1.0F;
    	this.lastBlockShift=this.blockShift=Math.round(shift);
    	this.defineTechBlockVariables();
        this.lastModelShiftAmount=this.getModelShiftAmount();
	}
	
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return !this.hasShield && this.getFacing()!=(short)side;
	}

	@Override
	public short getFacing() {
		return super.getFacing();
	}

	@Override
	public void setFacing(short facing) {
		short newFacing = (short) Math.max(2, facing);
		super.setFacing(newFacing);
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1.0F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		this.cleanUp();
		if(this.hasShield)
			{
				if(this.advancedShield)
				{
					this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord+(this.blockShift-2)*this.mX(), this.yCoord, this.zCoord+(this.blockShift-2)*this.mZ(), IHLUtils.getThisModItemStack("advancedShieldAssemblyUnitBlock")));
				}
				else
				{
					this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord+(this.blockShift-2)*this.mX(), this.yCoord, this.zCoord+(this.blockShift-2)*this.mZ(), IHLUtils.getThisModItemStack("IHLShieldAssemblyUnitBlock")));
				}
			}
		if(this.miningPipesSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.miningPipesSlot.get()));
		if(this.dischargeSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.dischargeSlot.get()));
		if(this.upgradeSlot.get(0)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(0)));
		if(this.upgradeSlot.get(1)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(1)));
		if(this.upgradeSlot.get(2)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(2)));
		if(this.upgradeSlot.get(3)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.upgradeSlot.get(3)));
		return new ItemStack(IHLMod.driverBlock,1);
	}

			@Override
			public String getInventoryName() {
                return "IHL TS-002";
			}

			@Override
			@SideOnly(Side.CLIENT)
			public GuiScreen getGui(EntityPlayer arg0, boolean arg1) {
				return new DriverGui(new DriverContainer(arg0, this));
			}

			@Override
			public ContainerBase getGuiContainer(EntityPlayer arg0) {
				return new DriverContainer(arg0, this);
			}

		    @Override
			public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
		    {
		        return true;
		    }
   
		    public boolean getGui(EntityPlayer player)
		    {
					return this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
		    }
		    
		    public boolean canPlaceShield()
		    {
    			if(this.hasShield)
    			{
    				return false;
    			}
		    	Block block;
	        	int xyz[]={0,0,1,0,-1,-1,1,1,-1,2,0,2,1,2,-1,-2,0,-2,1,-2,-1,0};
        		for(int i = 0;i<21;i++)
        		{
        			block = this.worldObj.getBlock(this.xCoord+xyz[i+1]*this.mZ()+this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+this.mZ());
        			if(!block.isAir(this.worldObj, this.xCoord+xyz[i+1]*this.mZ()+this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+this.mZ()))
        			{
        				return false;
        			}
        		}
        		return true;
		    }
		    
		    public int mX()
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
			
		    public int mZ()
			{
				switch(this.getFacing())
				{
				case 3:
				return 1;
				case 2:
				return -1;
				case 4:
				return 0;
				case 5:
				return 0;
				default:
				return -1;
				}
			}

			public float getModelShiftAmount() {
				return (float)this.modelShift/(float)this.modelShiftSpeed;
			}

		    private AxisAlignedBB getOperationZone()
		    {
				int x=this.xCoord;
				int y=this.yCoord;
				int z=this.zCoord;
				double extension=this.getModelShiftAmount()+1.0D;
				return AxisAlignedBB.getBoundingBox(x + extension*Math.min(this.mX(),0)-2.0D, y-2.0D, z + extension*Math.min(this.mZ(),0)-2.0D, x + 3.0D + extension*Math.max(this.mX(),0), y + 3.0D, z + 3.0D + extension*Math.max(this.mZ(),0));		    
			}
		    
		    private AxisAlignedBB getShieldBladesZone(double offset)
		    {
				int x=this.xCoord;
				int y=this.yCoord;
				int z=this.zCoord;
				double extension=this.getModelShiftAmount()+offset;
				double xShieldMinD=this.xCoord+extension*this.mX()-1D*Math.max(this.mX(), 0)-2D*Math.abs(this.mZ());
				double xShieldMaxD=this.xCoord+extension*this.mX()-1D*Math.min(this.mX(), 0)+3D*Math.abs(this.mZ());
				double yShieldMinD=this.yCoord-2D;
				double yShieldMaxD=this.yCoord+3D;
				double zShieldMinD=this.zCoord+extension*this.mZ()-1D*Math.max(this.mZ(), 0)-2D*Math.abs(this.mX());
				double zShieldMaxD=this.zCoord+extension*this.mZ()-1D*Math.min(this.mZ(), 0)+3D*Math.abs(this.mX());
				return AxisAlignedBB.getBoundingBox(xShieldMinD, yShieldMinD, zShieldMinD, xShieldMaxD, yShieldMaxD, zShieldMaxD);		    
			}
		    
			public int getStored() {
				return Math.round((float)this.energy);
			}

			public void setStored(int value) {
				this.energy=value;
			}

			@Override
			public void onGuiClosed(EntityPlayer arg0) {}
			
			public void installShield() {
        		if(IC2.platform.isSimulating())
        		{
   				this.hasShield=true;
				int x=this.xCoord;
				int y=this.yCoord;
				int z=this.zCoord;
				MultiBlockSpacerBlock.teList.add(this);
				this.defineTechBlockVariables();
				if(this.mX()==0)
				{
					HorizontalMiningPipe.teList.add(this);
				}
				else
				{
					HorizontalMiningPipe.teList.add(this);
				}
				
        			int xyz[]={0,0,1,0,-1,-1,1,1,-1,2,0,2,1,2,-1,-2,0,-2,1,-2,-1,0};
	        		for(int i1 = 0;i1<21;i1++)
        			{
	        			this.worldObj.setBlock(x+xyz[i1+1]*this.mZ()+this.mX(), y+xyz[i1], z+xyz[i1+1]*this.mX()+this.mZ(), IHLMod.multiBlockSpacerBlock, 0, 3);
        			}
        		}
			}
			
			public boolean isShaftCoord(int x, int y, int z) 
			{
				if(IC2.platform.isSimulating() && y==this.yShaft && x>=this.xShaftMin && x<=this.xShaftMax && z>=this.zShaftMin && z<=this.zShaftMax)
				{
					int sda = (x-this.xCoord)*this.mX()+(z-this.zCoord)*this.mZ();
					if(this.shaftDestroyedAtA==0 && this.shaftDestroyedAtB==0)
					{
						this.shaftDestroyedAtA=this.shaftDestroyedAtB=sda;
					}
					else if(sda>this.shaftDestroyedAtB)
					{
						for(int i4=this.shaftDestroyedAtB;i4<sda;i4++)
						{
							int x1=this.xCoord+i4*mX();
							int y1=this.yCoord;
							int z1=this.zCoord+i4*mZ();
							Block block=this.worldObj.getBlock(x1, y1, z1);
							if(block instanceof HorizontalMiningPipe)
							{
								this.harvestDrops(block, x1, y1, z1, x1, y1, z1, Blocks.air);
							}
						}
						this.shaftDestroyedAtB=sda;
					}
					else if(sda<this.shaftDestroyedAtA)
					{
						for(int i4=sda;i4<this.shaftDestroyedAtA;i4++)
						{
							int x1=this.xCoord+i4*mX();
							int y1=this.yCoord;
							int z1=this.zCoord+i4*mZ();
							Block block=this.worldObj.getBlock(x1, y1, z1);
							if(block instanceof HorizontalMiningPipe)
							{
								this.harvestDrops(block, x1, y1, z1, x1, y1, z1, Blocks.air);
							}
						}
						this.shaftDestroyedAtA=sda;
					}
		            return true;
				}
				return false;
			}
			
			@Override
			public boolean isAPartOfStructure(int x, int y, int z, boolean onBlockBeak) 
			{
				if(IC2.platform.isSimulating() && y>=this.yShieldMin && y<=this.yShieldMax && x>=this.xShieldMin && x<=this.xShieldMax && z>=this.zShieldMin && z<=this.zShieldMax)
				{
					if(onBlockBeak)
					{
						this.hasShield=false;
						this.advancedShield=false;
						this.modelShift=0;
						this.lastBlockShift=this.blockShift=1;
						this.shaftDestroyedAtA=0;
		            	IC2.network.get().updateTileEntityField(this, "modelShift");
		            	IC2.network.get().updateTileEntityField(this, "advancedShield");
						this.cleanUp();
					}
					return true;
				}
				return false;
			}
			
			public boolean isShaftCoordNN(int x, int y, int z) 
			{
				if(y==this.yShaft && x>=this.xShaftMin && x<=this.xShaftMax && z>=this.zShaftMin && z<=this.zShaftMax)
				{
					return true;
				}
				return false;
			}

			public void cleanUp() 
			{
	        	int xyz[]={0,0,1,0,-1,-1,1,1,-1,2,0,2,1,2,-1,-2,0,-2,1,-2,-1,0};
	        	for(int shift = 0;shift<=this.blockShift+1;shift++)
	        	{
	        		for(int i = 0;i<21;i++)
	        		{
	        			Block block = this.worldObj.getBlock(this.xCoord+xyz[i+1]*this.mZ()+shift*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+shift*this.mZ());
	        			if(block instanceof HorizontalMiningPipe)
	        			{
	          				ArrayList<ItemStack> drops = block.getDrops(this.worldObj, this.xCoord+xyz[i+1]*this.mZ()+shift*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+shift*this.mZ(), 0, 3);
	        				if(drops.size()>0)
	        				{
	        					for(int i1=0;i1<drops.size();i1++)
	        					{
	        						this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord+xyz[i+1]*this.mZ()+shift*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+shift*this.mZ(), drops.get(i1)));
	        					}
	        				}
	        				this.worldObj.setBlockToAir(this.xCoord+xyz[i+1]*this.mZ()+shift*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+shift*this.mZ());
	        			}
	        			else if(block instanceof MultiBlockSpacerBlock)
	        			{
	          				this.worldObj.setBlockToAir(this.xCoord+xyz[i+1]*this.mZ()+shift*this.mX(), this.yCoord+xyz[i], this.zCoord+xyz[i+1]*this.mX()+shift*this.mZ());
	        			}
	        		}
	        	}
			}

			private boolean handleDropsByHydrotransport(ArrayList<ItemStack> drops) 
			{
				ForgeDirection direction = ForgeDirection.getOrientation(this.getFacing()).getOpposite();
				TileEntity te = worldObj.getTileEntity(xCoord+direction.offsetX, yCoord+direction.offsetY, zCoord+direction.offsetZ);
				if(te instanceof HydrotransportPulpRegeneratorTileEntity)
				{
					HydrotransportPulpRegeneratorTileEntity hprte = (HydrotransportPulpRegeneratorTileEntity) te;
					if(hprte.getActive())
					{
						ArrayList<ItemStack> drops1 = new ArrayList();
						Iterator<ItemStack> dropsIterator = drops.iterator();
						while(dropsIterator.hasNext())
						{
							ItemStack stack = dropsIterator.next();
							RecipeOutput maceratorOut = Recipes.macerator.getOutputFor(stack, false);
							if(maceratorOut!=null && !maceratorOut.items.isEmpty())
							{
								Iterator<ItemStack> maceratorOutIterator = maceratorOut.items.iterator();
								while(maceratorOutIterator.hasNext())
								{
									drops1.add(maceratorOutIterator.next().copy());
								}
							}
							else
							{
								drops1.add(stack);
							}
						}
						if(hprte.outputSlot.canAdd(drops1))
						{
							hprte.outputSlot.add(drops1);
						}
						else
						{
							for(int i1=0;i1<drops1.size();i1++)
							{
								this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, xCoord+direction.offsetX, yCoord+direction.offsetY, zCoord+direction.offsetZ, drops1.get(i1)));
							}
						}
						return true;
					}
				}
				return false;
			}

			@Override
			public void onNetworkEvent(EntityPlayer player, int event) 
			{
				switch(event)
				{
				case 0:
					this.reverseModeOn=!this.reverseModeOn;
	                IC2.network.get().updateTileEntityField(this, "reverseModeOn");
					this.updateChecksum=-1;
					this.setOverclockRates();
					break;
				case 1:
					this.autoReverseModeOn=!this.autoReverseModeOn;
	                IC2.network.get().updateTileEntityField(this, "autoReverseModeOn");
					break;
				default:
					break;
				}
			}
			
			public void defineTechBlockVariables()
			{
		        this.xShaftMin=this.xCoord+this.blockShift*Math.min(this.mX(), 0);
		        this.xShaftMax=this.xCoord+this.blockShift*Math.max(this.mX(), 0);
		        this.yShaft=this.yCoord;
		        this.zShaftMin=this.zCoord+this.blockShift*Math.min(this.mZ(), 0);
		        this.zShaftMax=this.zCoord+this.blockShift*Math.max(this.mZ(), 0);
		        this.xShieldMin=this.xCoord+this.blockShift*this.mX()-3*Math.max(this.mX(), 0)-2*Math.abs(this.mZ());
		        this.xShieldMax=this.xCoord+this.blockShift*this.mX()-3*Math.min(this.mX(), 0)+2*Math.abs(this.mZ());
		        this.yShieldMin=this.yCoord-2;
		        this.yShieldMax=this.yCoord+2;
		        this.zShieldMin=this.zCoord+this.blockShift*this.mZ()-3*Math.max(this.mZ(), 0)-2*Math.abs(this.mX());
		        this.zShieldMax=this.zCoord+this.blockShift*this.mZ()-3*Math.min(this.mZ(), 0)+2*Math.abs(this.mX());
			}
			
			public void harvestDrops(Block block, int x, int y, int z, int x1, int y1, int z1, Block setToBlock)
			{
				if(block!=null && block!=Blocks.air && block!=Blocks.bedrock)
				{
					int meta = worldObj.getBlockMetadata(x, y, z);
					ArrayList<ItemStack> drops = block.getDrops(this.worldObj, x, y, z, meta, 0);
					if(drops.size()>0)
					{
						for(int i1=0;i1<drops.size();i1++)
						{
							this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, x1, y1, z1, drops.get(i1)));
						}
					}
				}
				this.worldObj.setBlock(x, y, z, setToBlock);
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
			
			@Override
		    public boolean shouldRenderInPass(int pass)
		    {
		        return pass==0;
		    }

			@Override
			public boolean getIsInvalid() 
			{
				return this.isInvalid();
			}

			public void installAdvancedShield() 
			{
				this.installShield();
				this.advancedShield=true;
                IC2.network.get().updateTileEntityField(this, "advancedShield");
			}

			public ItemStack getOutput(int arg0) 
			{
				return null;
			}

			public int getOutputSize() {
				return 0;
			}

			public void setOutput(int arg0, ItemStack arg1) 
			{
			}

			@Override
			public Set<UpgradableProperty> getUpgradableProperties() 
			{
				Set<UpgradableProperty> properties = new HashSet<UpgradableProperty>();
				properties.add(UpgradableProperty.EnergyStorage);
				properties.add(UpgradableProperty.Processing);
				properties.add(UpgradableProperty.Transformer);
				return properties;
			}
}
