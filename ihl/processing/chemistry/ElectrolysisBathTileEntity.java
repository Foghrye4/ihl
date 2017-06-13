package ihl.processing.chemistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.flexible_cable.FlexibleCableHolderBaseTileEntity;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class ElectrolysisBathTileEntity extends FlexibleCableHolderBaseTileEntity implements IHasGui, INetworkClientTileEntityEventListener, IFluidHandler
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("electrolysisbath");
    public final ApparatusProcessableInvSlot input;
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
	public short progress;
	protected short operationLength=20000;//Short.MAX_VALUE=32767
	private final IHLFluidTank fluidTank = new IHLFluidTank(2000);
	private final static double resistance=5D;
    
	public ElectrolysisBathTileEntity() {
		super();
        this.drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
        this.fillInputSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 1);
		this.input = new ApparatusProcessableInvSlot(this, "input", 3, Access.IO, 1, 64);
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
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("electrolysisBath");
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    
    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
   		IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, fluidTank);
        if (this.canOperate())
        {
            this.setActive(true);
            if (this.progress == 0)
            {
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
            }
            if(gridID!=-1 && this.getGrid().energy>0D)
            {
            	double drawEnergy = getEnergyAmountThisNodeWant();
            	this.progress+=drawEnergy;
            	this.getGrid().drawEnergy(drawEnergy, this);
            }

            if (this.progress >= this.operationLength)
            {
                this.operate();
                this.progress = 0;
                IC2.network.get().initiateTileEntityEvent(this, 2, true);
            }
        }
        else
        {
            if (this.progress != 0 && this.getActive())
            {
                IC2.network.get().initiateTileEntityEvent(this, 1, true);
            }
            if (!this.canOperate())
            {
                this.progress = 0;
            }
            this.setActive(false);
        }
    }
    
    @Override
    public FluidStack drain(ForgeDirection from, int amount, boolean doDrain)
    {
    	FluidStack fstack = this.fluidTank.drain(amount, doDrain);
		return fstack;
    }

	
    //1.7.10 API
	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return true;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid arg1) {
		return true;
	}

	@Override
	public String getInventoryName() {
		return "Electrolysis bath";
	}

	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		TileEntity te = worldObj.getTileEntity(xCoord,  yCoord-1, zCoord);
		if(te instanceof IFluidHandler && this.fluidTank.getFluid()!=null)
		{
			IFluidHandler ifhte = (IFluidHandler)te;
			if(ifhte.canFill(ForgeDirection.UP, this.fluidTank.getFluid().getFluid()))
			{
				int filled = ifhte.fill(ForgeDirection.UP, this.fluidTank.drain(this.getTankAmount(), false), true);
				this.fluidTank.drain(filled, true);
			}
		}
	}

    public int gaugeProgressScaled(int i)
    {
        return this.progress * i / operationLength;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new ElectrolysisBathGui(new ElectrolysisBathContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		this.fluidTank.sortFluidsByDensity();
		return new ElectrolysisBathContainer(player, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {}

    public boolean canOperate()
    {
    	return getOutput()!=null;
    }
    
    public UniversalRecipeOutput getOutput()
    {
    	return ElectrolysisBathTileEntity.recipeManager.getOutputFor(this.getInput());
    }

	@SuppressWarnings("rawtypes")
	public List[] getInput()
	{
		for(int i=0;i<fluidTank.getNumberOfFluids();i++)
		{
			List[] rInput = new List[] {Arrays.asList(new FluidStack[]{fluidTank.getFluid(i)}), this.input.getItemStackList()};
			if(ElectrolysisBathTileEntity.recipeManager.getOutputFor(rInput)!=null)
			{
				return rInput;
			}
		}
		return new List[] {Arrays.asList(new FluidStack[]{fluidTank.getFluid()}), this.input.getItemStackList()};
	}
	
	public void operate() 
	{
		UniversalRecipeInput recipeInput = ElectrolysisBathTileEntity.recipeManager.getRecipeInput(getInput());
		UniversalRecipeOutput output1 = getOutput();
		for(int i=0; i<recipeInput.getItemInputs().size();i++)
		{
			this.input.consume(recipeInput.getItemInputs().get(i));
		}
		this.fluidTank.fill(output1.getFluidOutputs(), true);
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack fluidStack, boolean doDrain) {
		if(fluidTank.getFluid()!=null && fluidTank.getFluid().containsFluid(fluidStack))
		{
			return this.fluidTank.drain(fluidStack, doDrain);
		}
		return null;
	}

	@Override
	public int fill(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return this.fluidTank.fill(arg1, arg2);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) {
		return new FluidTankInfo[] {this.fluidTank.getInfo()};
	}
	
    public boolean needsFluid()
    {
        return this.fluidTank.getFluidAmount() <= this.fluidTank.getCapacity();
    }
    
    public FluidStack getFluidStackfromTank()
    {
    	return this.fluidTank.getFluid();
    }

	public int getTankAmount() 
	{
		return this.fluidTank.getFluidAmount();
	}
	
    public int gaugeLiquidScaled(int i, int index)
    {
        return this.fluidTank.getFluidAmount() <= 0 ? 0 : this.fluidTank.getFluidAmount(index) * i / this.fluidTank.getCapacity();
    }
	public static void addRecipe(UniversalRecipeInput input, UniversalRecipeOutput output) 
	{
		recipeManager.addRecipe(input, output);
	}
	
	public int getNumberOfFluidsInTank() 
	{
		return this.fluidTank.getNumberOfFluids();
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

	public IHLFluidTank getFluidTank() 
	{
		return this.fluidTank;
	}
	
	@Override
	public double getMaxAllowableVoltage() 
	{
		return 64000D;
	}

	@Override
	public double getEnergyAmountThisNodeWant()
	{
    	double voltage = this.getGrid().getSinkVoltage(this);
    	double energy = voltage*voltage/resistance;
		return this.getOutput()!=null?energy>1d?energy:1d:0d;
	}

	@Override
	public void injectEnergyInThisNode(double amount, double voltage) 
	{
    	this.progress+=amount;
	}
}