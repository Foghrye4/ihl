package ihl.processing.chemistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.IHLMod;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.processing.metallurgy.BasicElectricMotorTileEntity;
import ihl.recipes.RecipeOutputItemStack;
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

public class FluidizedBedReactorTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("fluidizedbedreactor");
    public final ApparatusProcessableInvSlot input;
    public final IHLInvSlotOutput outputSlot;
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
	private final IHLFluidTank fluidTank = new IHLFluidTank(8000);
	public short temperature=20;
    
	public FluidizedBedReactorTileEntity() {
		super();
	 	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 1);
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
		return IHLUtils.getThisModItemStack("fluidizedBedReactor");
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
		return true;
	}

	@Override
	public String getInventoryName() {
		return "fluidizedBedReactor";
	}

	@Override
	public int gaugeProgressScaled(int i)
    {
        return this.progress * i / operationLength;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new FluidizedBedReactorGui(new FluidizedBedReactorContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		this.fluidTank.sortFluidsByDensity();
		return new FluidizedBedReactorContainer(player, this);
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
    	return FluidizedBedReactorTileEntity.recipeManager.getOutputFor(this.getInput());
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List[] getInput()
	{
		for(int i=0;i<fluidTank.getNumberOfFluids();i++)
		{
			for(int i1=0;i1<fluidTank.getNumberOfFluids();i1++)
			{
				if(i!=i1)
				{
					List[] rInput = new List[] {Arrays.asList(new FluidStack[]{fluidTank.getFluid(i),fluidTank.getFluid(i1)}), this.input.getItemStackList()};
					if(FluidizedBedReactorTileEntity.recipeManager.getOutputFor(rInput)!=null)
					{
						return rInput;
					}
				}
			}
		}
		return new List[] {Arrays.asList(new FluidStack[]{fluidTank.getLigthestFluid()}), this.input.getItemStackList()};
	}
	
	@Override
	public void operate() 
	{
		UniversalRecipeInput recipeInput = FluidizedBedReactorTileEntity.recipeManager.getRecipeInput(getInput());
		UniversalRecipeOutput output1 = getOutput();
		this.fluidTank.drain(recipeInput.getFluidInputs(), true);
		for (int i = 0; i < recipeInput.getItemInputs().size(); i++) {
			this.input.consume(recipeInput.getItemInputs().get(i));
			if (recipeInput.getItemInputs().get(i).getAmount() == 0) {
				for (int i1 = 0; i1 < this.input.size(); i1++) {
					ItemStack stack = this.input.get(i1);
					if (stack != null && recipeInput.getItemInputs().get(i).matches(stack)) {
						if (stack.stackTagCompound == null) {
							stack.stackTagCompound = new NBTTagCompound();
						}
						stack.stackTagCompound.setInteger("catalyst_uses",
								stack.stackTagCompound.getInteger("catalyst_uses") + 1);
						if (stack.stackTagCompound.getInteger("catalyst_uses") > IHLMod.config.maxCatalystUses){
							stack.stackTagCompound.setInteger("catalyst_uses", 0);
							if(--stack.stackSize<=0)
								this.input.put(i1, null);
						}
					}
				}
			}
		}
		if(output1.getFluidOutputs().size()>0)this.fluidTank.fill(output1.getFluidOutputs().get(0).copy(), true);
		if(output1.getFluidOutputs().size()>1)this.fluidTank.fill(output1.getFluidOutputs().get(1).copy(), true);
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

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() 
	{
		return recipeManager.getRecipes();
	}

	public static void addRecipe(FluidStack fluidStackInput1, ItemStack itemStackInput, FluidStack fluidStackOutput, ItemStack itemStackOutput1) 
	{
		if(fluidStackOutput!=null)
		{
			addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1}), (new ItemStack[] {itemStackInput})), new UniversalRecipeOutput((new FluidStack[] {fluidStackOutput}), (new ItemStack[] {itemStackOutput1}),200));
		}
		else
		{
			addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1}), (new ItemStack[] {itemStackInput})), new UniversalRecipeOutput(null, (new ItemStack[] {itemStackOutput1}),200));
		}
	}
	
	public IHLFluidTank getFluidTank() 
	{
		return this.fluidTank;
	}

	public static void addRecipe(FluidStack fluidStackInput1, ItemStack itemStackInput, FluidStack fluidStackOutput, RecipeOutputItemStack itemStackOutput1) {
		if(fluidStackOutput!=null)
		{
			addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1}), (new ItemStack[] {itemStackInput})), new UniversalRecipeOutput((new FluidStack[] {fluidStackOutput}), (new RecipeOutputItemStack[] {itemStackOutput1}),200));
		}
		else
		{
			addRecipe(new UniversalRecipeInput((new FluidStack[] {fluidStackInput1}), (new ItemStack[] {itemStackInput})), new UniversalRecipeOutput(null, (new RecipeOutputItemStack[] {itemStackOutput1}),200));
		}
	}

}