package ihl.tunneling_shield;

import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import ic2.api.Direction;
import ic2.core.ContainerBase;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.processing.metallurgy.BasicElectricMotorTileEntity;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;

public class HydrotransportPulpRegeneratorTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler
{
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
    public final InvSlotOutput outputSlot;
	final IHLFluidTank fluidTank = new IHLFluidTank(2000);
	private int timer=0;
    
	public HydrotransportPulpRegeneratorTileEntity() {
		super();
        this.drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 1);
		this.outputSlot = new InvSlotOutput(this, "output", 3, 12);
		this.isGuiScreenOpened=true;
		this.operationLength=26;
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", fluidTankTag);
    }
    
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("hydrotransportPulpRegenerator");
	}
    
    @Override
    public void updateEntityServer()
    {
    	super.updateEntityServer();
		ForgeDirection direction = ForgeDirection.getOrientation(this.getFacing());
		TileEntity te = worldObj.getTileEntity(xCoord+direction.offsetX,yCoord+direction.offsetY,zCoord+direction.offsetZ);
    	if(!this.getActive() && this.fluidTank.getFluid()!=null && isMachineOperational())
    	{
    		if(te instanceof DriverTileEntity)
    		{
    			this.setActive(true);
    		}
    	}
    	else if(this.getActive())
    	{
    		timer++;
    		if(!(te instanceof DriverTileEntity) || this.fluidTank.getFluid()==null || !isMachineOperational())
    		{
    			this.setActive(false);
    		}
    	}
    	if(timer>100)
    	{
    		this.fluidTank.drain(1, true);
    		this.energy-=1d;
    		timer=0;
    	}
    	if(timer%10==0)
    	{
    		if(te instanceof DriverTileEntity)
    		{
    			DriverTileEntity dte = (DriverTileEntity) te;
    			if(dte.getActive())
    			{
    				this.fluidTank.drain(1, true);
    			}
    		}
    	}
   		IHLUtils.handleFluidSlotsBehaviour(null, drainInputSlot, emptyFluidItemsSlot, fluidTank);
    }

	@Override
	public void operate() 
	{
		ForgeDirection direction = ForgeDirection.getOrientation(this.getFacing()).getOpposite();
		TileEntity te = worldObj.getTileEntity(xCoord+direction.offsetX, yCoord+direction.offsetY,zCoord+direction.offsetZ);
		int i0=0;
		for(int i1=i0;i1<outputSlot.size();i1++)
		{
			if(outputSlot.get(i1)!=null)
			{
				i0=i1;
			}
		}
		if(te instanceof ISidedInventory)
		{
			ISidedInventory si = (ISidedInventory)te;
			for(int i=0;i<si.getSizeInventory();i++)
			{
				if(outputSlot.get(i0)==null)
				{
					break;
				}
					if(si.canInsertItem(i, outputSlot.get(i0), Direction.fromForgeDirection(direction).toSideValue()))
					{
						if(si.getStackInSlot(i)==null)
						{
							si.setInventorySlotContents(i, outputSlot.get(i0));
							outputSlot.put(i0,null);
						}
						else if(IHLUtils.isItemStacksIsEqual(si.getStackInSlot(i),outputSlot.get(i0),true))
						{
							int stackSizeBefore = si.getStackInSlot(i).stackSize;
							si.getStackInSlot(i).stackSize+=outputSlot.get(i0).stackSize;
							if(si.getStackInSlot(i).stackSize>si.getInventoryStackLimit())
							{
								si.getStackInSlot(i).stackSize=si.getInventoryStackLimit();
								outputSlot.get(i0).stackSize-=si.getInventoryStackLimit()-stackSizeBefore;
								if(outputSlot.get(i0).stackSize<=0)
								{
									outputSlot.put(i0,null);
								}
							}
							else
							{
								outputSlot.put(i0,null);
							}
						}

					}
			}
		}
		else if(te instanceof IInventory)
		{
			IInventory si = (IInventory)te;
			for(int i=0;i<si.getSizeInventory();i++)
			{
				if(outputSlot.get(i0)==null)
				{
					break;
				}
				if(si.isItemValidForSlot(i, outputSlot.get(i0)))
				{
					if(si.getStackInSlot(i)==null)
					{
						si.setInventorySlotContents(i, outputSlot.get(i0));
						outputSlot.put(i0,null);
					}
					else if(IHLUtils.isItemStacksIsEqual(si.getStackInSlot(i),outputSlot.get(i0),true))
					{
						int stackSizeBefore = si.getStackInSlot(i).stackSize;
						si.getStackInSlot(i).stackSize+=outputSlot.get(i0).stackSize;
						if(si.getStackInSlot(i).stackSize>si.getInventoryStackLimit())
						{
							si.getStackInSlot(i).stackSize=si.getInventoryStackLimit();
							outputSlot.get(i0).stackSize-=si.getInventoryStackLimit()-stackSizeBefore;
							if(outputSlot.get(i0).stackSize<=0)
							{
								outputSlot.put(i0,null);
							}
						}
						else
						{
							outputSlot.put(i0,null);
						}
					}
				}
			}
		}
		else
		{
			for(int i1=i0;i1<outputSlot.size();i1++)
			{
				if(outputSlot.get(i1)!=null)
				{
					this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, xCoord+direction.offsetX+0.5d, yCoord+direction.offsetY+0.5d,zCoord+direction.offsetZ+0.5d, outputSlot.get(i1)));
					outputSlot.put(i1, null);
					break;
				}
			}
		}
	}
	
	private boolean isMachineOperational()
	{
		return this.engine.correctContent() && this.energy>=this.energyConsume/this.engine.getEfficiency();
	}

	@Override
	public List[] getInput() 
	{
		return null;
	}

	@Override
	public boolean canOperate() 
	{
		return !this.outputSlot.isEmpty();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new HydrotransportPulpRegeneratorGui(new HydrotransportPulpRegeneratorContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new HydrotransportPulpRegeneratorContainer(player, this);
	}

	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection arg0, Fluid arg1) 
	{
		return true;
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, int arg1, boolean arg2) {
		return null;
	}

	@Override
	public int fill(ForgeDirection arg0, FluidStack resource, boolean doFill) 
	{
		return this.fluidTank.fill(resource, doFill);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) 
	{
		return new FluidTankInfo[] {this.fluidTank.getInfo()};
	}
    
}