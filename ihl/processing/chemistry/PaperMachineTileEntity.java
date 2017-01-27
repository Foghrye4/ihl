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
import net.minecraft.tileentity.TileEntity;
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

public class PaperMachineTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("papermachine");
    public final IHLInvSlotOutput outputSlot;
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
	private final IHLFluidTank fluidTank = new IHLFluidTank(8000);
	public short temperature=20;
    
	public PaperMachineTileEntity() {
		super();
	 	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 2);
        this.drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
        this.fillInputSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 1);
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
		return this.getFacing()!=side;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("paperMachine");
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
		return direction.equals(ForgeDirection.UP);
	}

	@Override
	public String getInventoryName() {
		return "chemicalReactor";
	}
	
	public float getRenderLiquidLevel()
	{
		return (float)this.fluidTank.getFluidAmount()/(float)this.fluidTank.getCapacity();
	}

	@Override
	public int gaugeProgressScaled(int i)
    {
        return this.progress * i / operationLength;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new PaperMachineGui(new PaperMachineContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		this.fluidTank.sortFluidsByDensity();
		return new PaperMachineContainer(player, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {}

    @Override
	public boolean canOperate()
    {
    	UniversalRecipeOutput output = getOutput();
    	if(output!=null && this.outputSlot.canAdd(getOutput().getItemOutputs()))
    	{
    		if(output.specialConditions)
    		{
    			return this.checkSpecialConditions();
    		}
    		else
    		{
        		return true;
    		}
    	}
    	return false; 
    }
    
    private boolean checkSpecialConditions() 
    {
    	ForgeDirection dir = ForgeDirection.getOrientation(this.getFacing());
    	TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord, zCoord+dir.offsetZ);
    	if(te instanceof CryogenicDistillerTileEntity)
    	{
    		CryogenicDistillerTileEntity cgte = (CryogenicDistillerTileEntity)te;
    		return cgte.getFacing()==this.getFacing() && cgte.canProcess();
    	}
    	return false;
	}

	public UniversalRecipeOutput getOutput()
    {
    	return PaperMachineTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List[] getInput()
	{
		return new List[] {Arrays.asList(new FluidStack[]{fluidTank.getFluid()}), null};
	}
	
	@Override
	public void operate() 
	{
		UniversalRecipeInput recipeInput = PaperMachineTileEntity.recipeManager.getRecipeInput(getInput());
		UniversalRecipeOutput output1 = getOutput();
		this.fluidTank.drain(recipeInput.getFluidInputs(), true);
		this.fluidTank.fill(output1.getFluidOutputs(), true);
		if(!output1.getItemOutputs().isEmpty() && output1.getItemOutputs().get(0)!=null)this.outputSlot.add(output1.getItemOutputs());
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

	public static void addRecipe(FluidStack fluidStackInput1, ItemStack itemStackOutput1) 
	{
		addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1}), null), new UniversalRecipeOutput(null, (new ItemStack[] {itemStackOutput1}),200));
	}

	public static void addSpecialConditionsRecipe(FluidStack fluidStackInput1, FluidStack fluidStackInput2, ItemStack itemStackInput, FluidStack fluidStackOutput, ItemStack itemStackOutput1, ItemStack itemStackOutput2) 
	{
		addRecipe(new UniversalRecipeInput(new FluidStack[] {fluidStackInput1, fluidStackInput2}, (new ItemStack[] {itemStackInput})), new UniversalRecipeOutput((new FluidStack[] {fluidStackOutput}), (new ItemStack[] {itemStackOutput1, itemStackOutput2}),200, true));
	}
	
	public IHLFluidTank getFluidTank() 
	{
		return this.fluidTank;
	}

}