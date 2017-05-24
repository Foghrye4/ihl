package ihl.processing.metallurgy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.interfaces.IFluidTankVisual;
import ihl.interfaces.IHasTemperature;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.processing.chemistry.ChemicalReactorTileEntity;
import ihl.processing.invslots.IHLInvSlotOutput;
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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class ImpregnatingMachineTileEntity extends TileEntityInventory implements IHasGui,IFluidTankVisual,INetworkTileEntityEventListener, IFluidHandler, IHasTemperature
{
	private final static UniversalRecipeManager recipeManager = new UniversalRecipeManager("tub");
    public final ApparatusProcessableInvSlot input;
    public final IHLInvSlotOutput outputSlot;
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
	public short progress;
	protected short operationLength=Short.MAX_VALUE/2;
	private final IHLFluidTank fluidTank = new IHLFluidTank(8000);
	public short temperature=20;
    public int visibleFluidId = -1;
    public int visibleFluidAmount = 1;
    
	public ImpregnatingMachineTileEntity() {
		super();
	 	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 2);
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
		return IHLUtils.getThisModItemStack("tubBronze");
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> ret = super.getNetworkedFields();
        ret.add("visibleFluidId");
        ret.add("visibleFluidAmount");
        return ret;
    }

    @Override
	public void updateEntityServer()
    {
        if(this.fluidTank.getFluid()!=null)
        {
        	if(this.fluidTank.getLigthestFluid().getFluid().isGaseous())
        	{
            	this.fluidTank.drainLightest(10, true);
        	}
        }
		if(this.fluidTank.getFluid()!=null && (visibleFluidId!=this.fluidTank.getFluid().getFluid().getID() || visibleFluidAmount!=this.fluidTank.getFluidAmount()))
		{
			visibleFluidId = this.fluidTank.getFluid().getFluid().getID();
			visibleFluidAmount = this.fluidTank.getFluidAmount();
       		IC2.network.get().updateTileEntityField(this, "visibleFluidId");
       		IC2.network.get().updateTileEntityField(this, "visibleFluidAmount");
       	}
		else if(this.fluidTank.getFluid()==null && visibleFluidId!=-1)
       	{
			visibleFluidId=-1;
       		IC2.network.get().updateTileEntityField(this, "visibleFluidId");
       	}
   		temperature=(short) (this.fluidTank.getTemperature()-273);
   		IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, fluidTank);
        if (this.canOperate())
        {
            this.setActive(true);
            if (this.progress == 0)
            {
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
            }

            this.progress+=this.operationLength/this.getOutput().getTime();
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
		return "impregnatingMachine";
	}
	
	public float getRenderLiquidLevel()
	{
		return (float)this.visibleFluidAmount/(float)this.fluidTank.getCapacity();
	}

	@Override
	public void onNetworkEvent(int event) 
	{
	}

    public int gaugeProgressScaled(int i)
    {
        return this.progress * i / operationLength;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new ImpregnatingMachineGui(new ImpregnatingMachineContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		this.fluidTank.sortFluidsByDensity();
		return new ImpregnatingMachineContainer(player, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {}

    public boolean canOperate()
    {
    	return getOutput()!=null && this.outputSlot.canAdd(getOutput().getItemOutputs());
    }
    
    public UniversalRecipeOutput getOutput()
    {
    	return ImpregnatingMachineTileEntity.recipeManager.getOutputFor(this.getInput());
    }

	@SuppressWarnings("rawtypes")
	public List[] getInput()
	{
		for(int i=0;i<fluidTank.getNumberOfFluids();i++)
		{
			for(int i1=0;i1<fluidTank.getNumberOfFluids();i1++)
			{
				if(i!=i1)
				{
					List[] rInput = new List[]{Arrays.asList(new FluidStack[]{fluidTank.getFluid(i),fluidTank.getFluid(i1)}), this.input.getItemStackList()};
					if(ImpregnatingMachineTileEntity.recipeManager.getOutputFor(rInput)!=null)
					{
						return rInput;
					}
				}
			}
		}
		return new List[] {Arrays.asList(new FluidStack[]{fluidTank.getFluid()}), this.input.getItemStackList()};
	}
	
	public void operate() 
	{
		UniversalRecipeInput recipeInput = ImpregnatingMachineTileEntity.recipeManager.getRecipeInput(getInput());
		UniversalRecipeOutput output1 = getOutput();
		for(int i=0; i<recipeInput.getItemInputs().size();i++)
		{
			this.input.consume(recipeInput.getItemInputs().get(i));
		}
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
	
	@Override
	public short getFacing()
	{
		return 3;
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

	public static void addQuenchingRecipe(String nameHot, String nameQuenched) 
	{
		addRecipe(new UniversalRecipeInput((new FluidStack[] {new FluidStack(FluidRegistry.WATER, 50)}), (new ItemStack[] {IHLUtils.getThisModItemStack(nameHot)})), new UniversalRecipeOutput(null, (new ItemStack[] {IHLUtils.getThisModItemStack(nameQuenched)}),2));
	}

	public IHLFluidTank getFluidTank() 
	{
		return this.fluidTank;
	}

	public static void addChemicalRecipe(UniversalRecipeInput universalRecipeInput,	UniversalRecipeOutput universalRecipeOutput) 
	{
		addRecipe(universalRecipeInput,universalRecipeOutput);
		ChemicalReactorTileEntity.addRecipe(universalRecipeInput,universalRecipeOutput);
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }

	@Override
	public int getTemperature() 
	{
		return this.fluidTank.getTemperature();
	}

	@Override
	public int getVisibleFluidId() {
		return this.visibleFluidId;
	}

	@Override
	public int getVisibleFluidAmount() {
		return this.visibleFluidAmount;
	}
}