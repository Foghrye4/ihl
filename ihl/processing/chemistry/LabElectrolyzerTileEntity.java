package ihl.processing.chemistry;

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
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.processing.metallurgy.BasicElectricMotorTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;

public class LabElectrolyzerTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("labelectrolyzer");
    public final IHLInvSlotOutput outputSlot;
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlotAnodeOutput;
    public final InvSlotConsumableLiquidIHL fillInputSlotCathodeOutput;
    public final InvSlotOutput emptyFluidItemsSlot;
	private final IHLFluidTank fluidTank = new IHLFluidTank(2000);
	public final IHLFluidTank fluidTankAnodeOutput = new IHLFluidTank(8000);
	public final IHLFluidTank fluidTankCathodeOutput = new IHLFluidTank(8000);
	public short temperature=20;
    
	public LabElectrolyzerTileEntity() {
		super();
	 	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 2);
        this.drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
        this.fillInputSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
        this.fillInputSlotAnodeOutput = new InvSlotConsumableLiquidIHL(this, "fillInputAnodeOutput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
        this.fillInputSlotCathodeOutput = new InvSlotConsumableLiquidIHL(this, "fillInputCathodeOutput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 3);
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
        this.fluidTankAnodeOutput.readFromNBT(nbttagcompound.getCompoundTag("fluidTankAnodeOutput"));
        this.fluidTankCathodeOutput.readFromNBT(nbttagcompound.getCompoundTag("fluidTankCathodeOutput"));
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", fluidTankTag);

        NBTTagCompound fluidTankAnodeTag = new NBTTagCompound();
        this.fluidTankAnodeOutput.writeToNBT(fluidTankAnodeTag);
        nbttagcompound.setTag("fluidTankAnodeOutput", fluidTankAnodeTag);
        
        NBTTagCompound fluidTankCathodeTag = new NBTTagCompound();
        this.fluidTankCathodeOutput.writeToNBT(fluidTankCathodeTag);
        nbttagcompound.setTag("fluidTankCathodeOutput", fluidTankCathodeTag);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=side;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("labElectrolyzer");
	}
	
    @Override
	public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    

    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
   		IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, fluidTank);
   		IHLUtils.handleFluidSlotsBehaviour(fillInputSlotAnodeOutput, null, emptyFluidItemsSlot, fluidTankAnodeOutput);
   		IHLUtils.handleFluidSlotsBehaviour(fillInputSlotCathodeOutput, null, emptyFluidItemsSlot, fluidTankCathodeOutput);
    }
    
    @Override
    public FluidStack drain(ForgeDirection direction, int amount, boolean doDrain)
    {
    	if(this.canDrain(direction, null))
    	{
    		if(direction.equals(ForgeDirection.getOrientation(this.getFacing()).getRotation(ForgeDirection.UP)))
    		{
    			return this.fluidTankAnodeOutput.drain(amount, doDrain);
    		}
    		if(direction.equals(ForgeDirection.getOrientation(this.getFacing()).getRotation(ForgeDirection.DOWN)))
    		{
    			return this.fluidTankCathodeOutput.drain(amount, doDrain);
    		}
			return this.fluidTank.drain(amount, doDrain);
    	}
    	return null;
    }

	
    //1.7.10 API
	@Override
	public boolean canDrain(ForgeDirection direction, Fluid arg1) {
		return direction.equals(ForgeDirection.getOrientation(this.getFacing()).getRotation(ForgeDirection.UP))||direction.equals(ForgeDirection.getOrientation(this.getFacing()).getRotation(ForgeDirection.DOWN))||direction.equals(ForgeDirection.DOWN);
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid arg1) {
		return direction.equals(ForgeDirection.getOrientation(this.getFacing()).getOpposite());
	}

	@Override
	public String getInventoryName() {
		return "labElectrolizer";
	}
	
    @Override
	public int gaugeProgressScaled(int i)
    {
        return this.progress * i / operationLength;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new LabElectrolyzerGui(new LabElectrolyzerContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		this.fluidTank.sortFluidsByDensity();
		return new LabElectrolyzerContainer(player, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {}

    @Override
	public boolean canOperate()
    {
    	UniversalRecipeOutput output = getOutput();
    	if(output!=null)
    	{
    		if(getOutput().getItemOutputs()!=null && !getOutput().getItemOutputs().isEmpty() && getOutput().getItemOutputs().get(0)!=null)
    		{
    			return this.outputSlot.canAdd(getOutput().getItemOutputs());
    		}
    		else
    		{
        		return this.fluidTankAnodeOutput.getFluidAmount()<this.fluidTankAnodeOutput.getCapacity() && this.fluidTankAnodeOutput.getFluidAmount()<this.fluidTankCathodeOutput.getCapacity();
    		}
    	}
    	return false; 
    }
    
	public UniversalRecipeOutput getOutput()
    {
    	return LabElectrolyzerTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List[] getInput()
	{
		return new List[]{Arrays.asList(new FluidStack[]{fluidTank.getFluid()}),null};
	}
	
	@Override
	public void operate() 
	{
		UniversalRecipeInput recipeInput = LabElectrolyzerTileEntity.recipeManager.getRecipeInput(getInput());
		UniversalRecipeOutput output1 = getOutput();
		this.fluidTank.drain(recipeInput.getFluidInputs().get(0), true);
		if(output1.getFluidOutputs().size()>0)this.fluidTankAnodeOutput.fill(output1.getFluidOutputs().get(0).copy(), true);
		if(output1.getFluidOutputs().size()>1)this.fluidTankCathodeOutput.fill(output1.getFluidOutputs().get(1).copy(), true);
		if(!output1.getItemOutputs().isEmpty() && output1.getItemOutputs().get(0)!=null)this.outputSlot.add(output1.getItemOutputs());
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack fluidStack, boolean doDrain) {
		if(this.fluidTankAnodeOutput.getFluid().containsFluid(fluidStack))
		{
			return this.fluidTankAnodeOutput.drain(fluidStack.amount, doDrain);
		}
		if(this.fluidTankCathodeOutput.getFluid().containsFluid(fluidStack))
		{
			return this.fluidTankCathodeOutput.drain(fluidStack.amount, doDrain);
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

	public static void addRecipe(FluidStack fluidStackInput1, FluidStack fluidStackOutputAnode, FluidStack fluidStackOutputCathode, ItemStack itemStackOutput1) 
	{
		if(itemStackOutput1!=null)
		{
			addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1}), null), new UniversalRecipeOutput((new FluidStack[] {fluidStackOutputAnode, fluidStackOutputCathode}), (new ItemStack[] {itemStackOutput1}),200));
		}
		else
		{
			addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1}), null), new UniversalRecipeOutput((new FluidStack[] {fluidStackOutputAnode, fluidStackOutputCathode}), null,200));
		}
	}

	public IHLFluidTank getFluidTank() 
	{
		return this.fluidTank;
	}

}