package ihl.processing.metallurgy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.interfaces.IWorkspaceElement;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;

public class GasWeldingStationTileEntity extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener, IFluidHandler, IWorkspaceElement
{
	protected static final UniversalRecipeManager acetyleneRecipeManager = new UniversalRecipeManager("gasweldingstation");
    public final ApparatusProcessableInvSlot input;
    public final InvSlotConsumableLiquidIHL fillInputSlotFlammableGas;
    public final InvSlotConsumableLiquidIHL fillInputSlotOxygen;
    public final InvSlotConsumableLiquidIHL drainInputSlotFlammableGas;
    public final InvSlotConsumableLiquidIHL drainInputSlotOxygen;
    public final InvSlotOutput emptyFluidItemsSlot;
	public final IHLFluidTank flammableGasTank = new IHLFluidTank(8000);
	public final IHLFluidTank oxygenTank = new IHLFluidTank(8000);
	public short progress2;
	protected short operationLength=200;
    
	public GasWeldingStationTileEntity() {
		super();
		this.input = new ApparatusProcessableInvSlot(this, "input", 0, Access.IO, 1, 16);
        this.fillInputSlotFlammableGas = new InvSlotConsumableLiquidIHL(this, "fillInputFlammableGas", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
        this.fillInputSlotOxygen = new InvSlotConsumableLiquidIHL(this, "fillInputOxygen", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
        this.drainInputSlotFlammableGas = new InvSlotConsumableLiquidIHL(this, "drainInputFlammableGas", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Drain);
        this.drainInputSlotOxygen = new InvSlotConsumableLiquidIHL(this, "drainInputOxygen", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Drain);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 2);
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		return fields;
    }
	
    @Override
	public void updateEntityServer()
    {
        if (this.canProduceGas())
        {
            ++this.progress2;
            if (this.progress2 >= this.operationLength)
            {
                this.produceGas();
                this.progress2 = 0;
            }
        }
        else
        {
            if (!this.canProduceGas())
            {
                this.progress2 = 0;
            }
        }
   		IHLUtils.handleFluidSlotsBehaviour(fillInputSlotFlammableGas, drainInputSlotFlammableGas, emptyFluidItemsSlot, flammableGasTank);
   		IHLUtils.handleFluidSlotsBehaviour(fillInputSlotOxygen, drainInputSlotOxygen, emptyFluidItemsSlot, oxygenTank);
    }
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.flammableGasTank.readFromNBT(nbttagcompound.getCompoundTag("flammableGasTank"));
        this.oxygenTank.readFromNBT(nbttagcompound.getCompoundTag("oxygenTank"));
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound flammableGasTankNBT = new NBTTagCompound();
        this.flammableGasTank.writeToNBT(flammableGasTankNBT);
        nbttagcompound.setTag("flammableGasTank", flammableGasTankNBT);
        NBTTagCompound oxygenTankNBT = new NBTTagCompound();
        this.oxygenTank.writeToNBT(oxygenTankNBT);
        nbttagcompound.setTag("oxygenTank", oxygenTankNBT);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("gasWeldingStation");
	}
     
	@Override
	public String getInventoryName() {
		return "detonationSprayingMachine";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new GasWeldingStationGui(new GasWeldingStationContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new GasWeldingStationContainer(player, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {}
	
	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
	}

    public boolean canProduceGas()
    {
    	UniversalRecipeOutput rOutput = GasWeldingStationTileEntity.acetyleneRecipeManager.getOutputFor(Arrays.asList(new FluidStack[]{this.flammableGasTank.getFluid()}), Arrays.asList(new ItemStack[]{this.input.get()}), false, false);
    	return rOutput!=null;
    }
    
	public void produceGas()
	{
		if(canProduceGas())
		{
			UniversalRecipeInput rInput = GasWeldingStationTileEntity.acetyleneRecipeManager.getRecipeInput(Arrays.asList(new FluidStack[]{this.flammableGasTank.getFluid()}), Arrays.asList(new ItemStack[]{this.input.get()}));
	    	UniversalRecipeOutput rOutput = GasWeldingStationTileEntity.acetyleneRecipeManager.getOutputFor(Arrays.asList(new FluidStack[]{this.flammableGasTank.getFluid()}), Arrays.asList(new ItemStack[]{this.input.get()}), false, false);
			FluidStack output1 = rOutput.getFluidOutputs().get(0).copy();
			FluidStack output2 = rOutput.getFluidOutputs().get(1).copy();
			this.flammableGasTank.drain(rInput.getFluidInputs().get(0), true);
			this.input.get().stackSize-=rInput.getItemInputs().get(0).getAmount();
			if(this.input.get().stackSize<=0)
			{
				this.input.clear();
			}
			this.flammableGasTank.fill(output1, true);
			this.flammableGasTank.fill(output2, true);
		}
	}
    

	public UniversalRecipeInput getInput()
	{
		return new UniversalRecipeInput(new FluidStack[] {this.flammableGasTank.getLigthestFluid(), this.oxygenTank.getFluid()}, new ItemStack[]{this.input.get(0),this.input.get(1),this.input.get(2)});
	}
	
	public static void addGasRecipe(ItemStack input, FluidStack inputFluid, FluidStack output, FluidStack output2) 
	{
		acetyleneRecipeManager.addRecipe(new UniversalRecipeInput(new FluidStack[] {inputFluid},new ItemStack[] {input}), new UniversalRecipeOutput(new FluidStack [] {output, output2},new ItemStack [] {}, 20));
	}
	
	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getGasRecipes() {
		return acetyleneRecipeManager.getRecipes();
	}
	
    @Override
    public FluidStack drain(ForgeDirection from, int amount, boolean doDrain)
    {
    	FluidStack fstack = this.flammableGasTank.drain(amount, doDrain);
		return fstack;
    }
    
	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack fluidStack, boolean doDrain) {
		if(this.flammableGasTank.getFluid().containsFluid(fluidStack))
		{
			return this.flammableGasTank.drain(fluidStack.amount, doDrain);
		}
		return null;
	}

	@Override
	public int fill(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return this.oxygenTank.fill(arg1, arg2);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) {
		return new FluidTankInfo[] {this.flammableGasTank.getInfo(),this.oxygenTank.getInfo()};
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

	public int gaugeProgressScaled(int i) 
	{
		 return this.progress2 * i / this.operationLength;
	}
	
	public int gaugeFlammableGasScaled(int i) 
	{
		if(this.flammableGasTank.getFluid()!=null)
		{
			return this.flammableGasTank.getFluidAmount() * i /this.flammableGasTank.getCapacity();
		}
		else
		{
			return 0;
		}
	}
	
	public int gaugeOxygenScaled(int i) 
	{
		if(this.oxygenTank.getFluid()!=null)
		{
			return this.oxygenTank.getFluidAmount() * i /this.oxygenTank.getCapacity();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public boolean canBeUsed() 
	{
		return 
				this.flammableGasTank.getFluid()!=null && 
				this.flammableGasTank.getLigthestFluid().getFluid().getName().equals("acetylene") &&
				this.flammableGasTank.getLigthestFluid().amount>=20 &&
				this.oxygenTank.getFluid()!=null && 
				this.oxygenTank.getLigthestFluid().getFluid().getName().equals("oxygen") &&
				this.oxygenTank.getLigthestFluid().amount>=20;
	}

	@Override
	public void use() 
	{
		this.flammableGasTank.drain(IHLUtils.getFluidStackWithSize("acetylene", 20), true);
		this.oxygenTank.drain(IHLUtils.getFluidStackWithSize("oxygen", 20), true);
	}
	
	@Override
	public boolean getIsInvalid()
	{
		return this.isInvalid();
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
}