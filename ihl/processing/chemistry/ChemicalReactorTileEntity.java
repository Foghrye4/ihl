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
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.processing.metallurgy.BasicElectricMotorTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;

public class ChemicalReactorTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("chemicalreactor");
    public final ApparatusProcessableInvSlot input;
    public final IHLInvSlotOutput outputSlot;
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
	private final IHLFluidTank fluidTank = new IHLFluidTank(8000);
	public short temperature=20;
    
	public ChemicalReactorTileEntity() {
		super();
	 	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 2);
        this.drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
        this.fillInputSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 1);
		this.input = new ApparatusProcessableInvSlot(this, "input", 3, Access.IO, 2, 64);
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
		return IHLUtils.getThisModItemStack("chemicalReactor");
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
    	switch(from)
    	{
    	case UP:
        	return this.fluidTank.drainLightest(amount, doDrain);
    	case NORTH:
        	return this.fluidTank.drainLightest(amount, doDrain);
    	case SOUTH:
        	return this.fluidTank.drainLightest(amount, doDrain);
    	case WEST:
        	return this.fluidTank.drainLightest(amount, doDrain);
    	case EAST:
        	return this.fluidTank.drainLightest(amount, doDrain);
    	case DOWN:
        	return this.fluidTank.drain(amount, doDrain);
		default:
        	return this.fluidTank.drain(amount, doDrain);
    	}
    }
	
    //1.7.10 API
	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return true;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid arg1) {
		return direction.equals(ForgeDirection.getOrientation(this.getFacing()).getOpposite());
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
		return new ChemicalReactorGui(new ChemicalReactorContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		this.fluidTank.sortFluidsByDensity();
		return new ChemicalReactorContainer(player, this);
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

	@SuppressWarnings("unchecked")
	public UniversalRecipeOutput getOutput()
    {
    	return ChemicalReactorTileEntity.recipeManager.getOutputFor(this.getInput()[0],this.getInput()[1]);
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List[] getInput()
	{
		for(int i=0;i<fluidTank.getNumberOfFluids();i++)
		{
			for(int i1=0;i1<fluidTank.getNumberOfFluids();i1++)
			{
				if(i!=i1)
				{
					if(ChemicalReactorTileEntity.recipeManager.getOutputFor(Arrays.asList(new FluidStack[]{fluidTank.getFluid(i),fluidTank.getFluid(i1)}), this.input.getItemStackList())!=null)
					{
						return new List[] {Arrays.asList(new FluidStack[]{fluidTank.getFluid(i),fluidTank.getFluid(i1)}), this.input.getItemStackList()};
					}
				}
			}
		}
		return new List[] {Arrays.asList(new FluidStack[]{fluidTank.getFluid()}), this.input.getItemStackList()};
	}
	
	@Override
	@SuppressWarnings({ "unchecked"})
	public void operate() 
	{
		UniversalRecipeInput recipeInput = ChemicalReactorTileEntity.recipeManager.getRecipeInput(this.getInput()[0],this.getInput()[1]);
		UniversalRecipeOutput output1 = getOutput();
		for(int i=0; i<recipeInput.getItemInputs().size();i++)
		{
			this.input.consume(recipeInput.getItemInputs().get(i));
		}
		if(output1.specialConditions)
		{
	    	ForgeDirection dir = ForgeDirection.getOrientation(this.getFacing());
	    	TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX, yCoord, zCoord+dir.offsetZ);
	    	if(te instanceof CryogenicDistillerTileEntity)
	    	{
	    		CryogenicDistillerTileEntity cgte = (CryogenicDistillerTileEntity)te;
	    		cgte.fill(ForgeDirection.getOrientation(this.getFacing()).getOpposite(), recipeInput.getFluidInputs().get(0).getInputs().get(0), true);
	    	}
		}
		this.fluidTank.drain(recipeInput.getFluidInputs(), true);
		this.fluidTank.fill(output1.getFluidOutputs(), true);
		this.outputSlot.add(output1.getItemOutputs());
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

	public static void addRecipe(FluidStack fluidStackInput1, FluidStack fluidStackInput2, ItemStack itemStackInput, FluidStack fluidStackOutput, ItemStack itemStackOutput1, ItemStack itemStackOutput2) 
	{
		addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1, fluidStackInput2}), (new ItemStack[] {itemStackInput})), new UniversalRecipeOutput((new FluidStack[] {fluidStackOutput}), (new ItemStack[] {itemStackOutput1, itemStackOutput2}),200));
	}

	public static void addSpecialConditionsRecipe(FluidStack fluidStackInput1, FluidStack fluidStackInput2, ItemStack itemStackInput, FluidStack fluidStackOutput, ItemStack itemStackOutput1, ItemStack itemStackOutput2) 
	{
		addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1, fluidStackInput2}), (new ItemStack[] {itemStackInput})), new UniversalRecipeOutput((new FluidStack[] {fluidStackOutput}), (new ItemStack[] {itemStackOutput1, itemStackOutput2}),200, true));
	}
	
	public IHLFluidTank getFluidTank() 
	{
		return this.fluidTank;
	}

}