package ihl.processing.metallurgy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableObject;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.ContainerBase;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.block.invslot.InvSlot.InvSide;
import ihl.IHLMod;
import ihl.items_blocks.IHLTool;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.recipes.IRecipeInputFluid;
import ihl.recipes.RecipeInputDie;
import ihl.recipes.RecipeInputFluidDictionary;
import ihl.recipes.RecipeInputFluidStack;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WireMillTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler{

    public final ApparatusProcessableInvSlot dice;
    public final InvSlotConsumableLiquidIHL fillInputSlot;
    public final InvSlotConsumableLiquidIHL drainInputSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
	public final IHLFluidTank oilFluidTank = new IHLFluidTank(1100);
	public final IHLFluidTank waterFluidTank = new IHLFluidTank(1100);
	public final IHLFluidTank metalFluidTank = new IHLFluidTank(1100);
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("wiremill");
	protected static final UniversalRecipeManager recipeManagerMelting = new UniversalRecipeManager("wiremillMelting");

	public WireMillTileEntity()
	{
		super();
		emptyFluidItemsSlot = new InvSlotOutput(this, "emptyWaterFluidItemsSlot", 2, 1);
		fillInputSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
        drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Drain, "ingot");
		dice = new ApparatusProcessableInvSlot(this, "dice", 3, Access.IO, 1, 64);
		this.isGuiScreenOpened=true;
		this.operationLength=600;
	}
	

	@Override
	public String getInventoryName() {
		return "WireMill";
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("wireMill");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) 
	{
		return new WireMillGui(new WireMillContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		return new WireMillContainer(player, this);
	}
	
	@Override
	public void operate() 
	{	
		UniversalRecipeInput rInput = WireMillTileEntity.recipeManager.getRecipeInput(getInput());
		IRecipeInput rInputDice = rInput.getItemInputs().get(0);
		UniversalRecipeOutput rOutput = this.getOutput();
		ItemStack rOutputItemStack=null;
		if(!rOutput.getItemOutputs().isEmpty())
		{
			rOutputItemStack = rOutput.getItemOutputs().get(0).itemStack.copy();
		}
		List<IRecipeInputFluid> rInputFluidStacks = rInput.getFluidInputs();
		int consumeMoltenMetalAmount=1;
		if(!rInputFluidStacks.isEmpty() && rOutputItemStack!=null && rInputDice!=null && rInputDice instanceof RecipeInputDie)
		{
			if(rInputFluidStacks.size()>=3)
			{
				consumeMoltenMetalAmount=rInputFluidStacks.get(2).getAmount();
			}
			else
			{
				consumeMoltenMetalAmount=rInputFluidStacks.get(0).getAmount();
			}
			if(rOutputItemStack!=null && rInputDice!=null && rInputDice instanceof RecipeInputDie)
			{
				consumeMoltenMetalAmount*=((RecipeInputDie)rInputDice).transformOutput(this.dice.get(), rOutputItemStack);
			}
		}
		if(dice.get()!=null)
		{
			if(dice.get().getItem() instanceof IHLTool)
			{
				IHLUtils.damageItemViaNBTTag(dice.get(), 1);
				if(dice.get().stackSize<=0)
				{
					dice.put(null);
				}
			}
			else
			{
				dice.consume(0, 1);
			}
		}
		if(this.drainInputSlot.get()!=null && !rInput.getItemInputs().isEmpty() && rInput.getItemInputs().get(0).matches(this.drainInputSlot.get()))
		{
			this.drainInputSlot.consume(1);
		}
		if(!rOutput.getFluidOutputs().isEmpty())
		{
			this.metalFluidTank.fill(rOutput.getFluidOutputs().get(0), true);
		}
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing()).getRotation(ForgeDirection.DOWN);
		TileEntity te = worldObj.getTileEntity(xCoord+ForgeDirection.getOrientation(getFacing()).offsetX,yCoord+ForgeDirection.getOrientation(getFacing()).offsetY,zCoord+ForgeDirection.getOrientation(getFacing()).offsetZ);
		if(rOutputItemStack!=null && te instanceof IProductionLine)
		{
			if(!rInputFluidStacks.isEmpty())
			{
				this.oilFluidTank.drain(1, true);
				this.waterFluidTank.drain(1, true);
				this.metalFluidTank.drain(consumeMoltenMetalAmount, true);
			}
			((IProductionLine)te).process(rOutputItemStack);
		}
	}
	
	@Override
	public List[] getInput()
	{
		return new List[]{Arrays.asList(new FluidStack[] {this.oilFluidTank.getFluid(),this.waterFluidTank.getFluid(),this.metalFluidTank.getFluid()}),Arrays.asList(new ItemStack[] {dice.get(),drainInputSlot.get()})};
	}
	
    public UniversalRecipeOutput getOutput()
    {
    	return WireMillTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }
    
    public static void addRecipe(FluidStack moltenMetal, IRecipeInput inputDice,ItemStack output1)
    {	
    	recipeManager.addRecipe(new UniversalRecipeInput(Arrays.asList(new IRecipeInputFluid[] {new RecipeInputFluidDictionary("lubricant", 1),new RecipeInputFluidDictionary("water", 1),new RecipeInputFluidStack(moltenMetal)}),Arrays.asList(new IRecipeInput[] {inputDice})),new UniversalRecipeOutput(null,Arrays.asList(new ItemStack[] {output1}),200));
    }

    public static void addRecipe(String string, FluidStack fluidStack) 
	{
    	recipeManager.addRecipe(new UniversalRecipeInput(null, Arrays.asList(new IRecipeInput [] {new RecipeInputOreDict(string)})),new UniversalRecipeOutput(Arrays.asList(new FluidStack[] {fluidStack}),null,200));
	}

    
	@Override
	public boolean canOperate()
	{
		UniversalRecipeOutput output = this.getOutput();
		if(output!=null)
		{
			ForgeDirection dir = ForgeDirection.getOrientation(getFacing()).getRotation(ForgeDirection.DOWN);
			TileEntity te = worldObj.getTileEntity(xCoord+ForgeDirection.getOrientation(getFacing()).offsetX,yCoord+ForgeDirection.getOrientation(getFacing()).offsetY,zCoord+ForgeDirection.getOrientation(getFacing()).offsetZ);
			if(te instanceof IProductionLine && !output.getItemOutputs().isEmpty())
			{
	        	ItemStack isOut=this.getOutput().getItemOutputs().get(0).itemStack.copy();
	        	if(this.dice.get()!=null)
	        	{
		        	isOut.stackTagCompound.setInteger("transverseSection", this.dice.get().stackTagCompound.getInteger("transverseSection"));
	        	}
				return ForgeDirection.getOrientation(((IProductionLine)te).getFacing()).equals(dir) && ((IProductionLine)te).canProcess(isOut);
			}
			else if(!output.getFluidOutputs().isEmpty())
			{
				FluidStack fstack = output.getFluidOutputs().get(0);
				return metalFluidTank.fill(fstack, false)==fstack.amount;
			}
		}
		return false;
	}

    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.oilFluidTank.readFromNBT(nbttagcompound.getCompoundTag("oilFluidTank"));
        this.waterFluidTank.readFromNBT(nbttagcompound.getCompoundTag("waterFluidTank"));
        this.metalFluidTank.readFromNBT(nbttagcompound.getCompoundTag("metalFluidTank"));
    }
    
    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound oilFluidTankTag = new NBTTagCompound();
        this.oilFluidTank.writeToNBT(oilFluidTankTag);
        nbttagcompound.setTag("oilFluidTank", oilFluidTankTag);
        NBTTagCompound waterFluidTankTag = new NBTTagCompound();
        this.waterFluidTank.writeToNBT(waterFluidTankTag);
        nbttagcompound.setTag("waterFluidTank", waterFluidTankTag);
        NBTTagCompound metalFluidTankTag = new NBTTagCompound();
        this.metalFluidTank.writeToNBT(metalFluidTankTag);
        nbttagcompound.setTag("metalFluidTank", metalFluidTankTag);
    }
    
    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
        FluidStack drainSlotContent = drainInputSlot.drain(null, Integer.MAX_VALUE, null, true);
        if(drainSlotContent!=null)
        {
            String fluidDictionaryName = IHLMod.fluidDictionary.getFluidName(drainSlotContent.getFluid());
            if(fluidDictionaryName!=null && fluidDictionaryName.equals("lubricant"))
            {
                IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, oilFluidTank);
            }
            else if(fluidDictionaryName!=null && fluidDictionaryName.equals("water"))
            {
                IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, waterFluidTank);
            }
            else
            {
                IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, metalFluidTank);
            }
        }
        else
        {
        	if(metalFluidTank.getNumberOfFluids()>0)
        	{
                IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, metalFluidTank);
        	}
        	else if(oilFluidTank.getNumberOfFluids()>0)
        	{
                IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, oilFluidTank);
        	}
        	else
        	{
                IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, waterFluidTank);
        	}
        }

    }
	public int gaugeLiquidScaled(int i) {
		if(this.oilFluidTank.getFluid()!=null)
		{
			return this.oilFluidTank.getFluidAmount() * i /this.oilFluidTank.getCapacity();
		}
		else
		{
			return 0;
		}
	}
	
    @Override
	public int gaugeProgressScaled(int i)
    {
        return this.progress * i / this.operationLength;
    }
    
	public int gaugeWaterScaled(int i) 
	{
		if(this.waterFluidTank.getFluid()!=null)
		{
			return this.waterFluidTank.getFluidAmount() * i /this.waterFluidTank.getCapacity();
		}
		else
		{
			return 0;
		}
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() 
	{
		return recipeManager.getRecipes();
	}


	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return true;
	}


	@Override
	public boolean canFill(ForgeDirection arg0, Fluid arg1) {
		return true;
	}


	@Override
	public FluidStack drain(ForgeDirection direction, FluidStack arg1, boolean arg2) 
	{
    	if(direction.equals(ForgeDirection.DOWN))
    	{
    		return this.metalFluidTank.drain(arg1, arg2);
    	}
    	else if(direction.equals(ForgeDirection.UP))
    	{
    		return this.waterFluidTank.drain(arg1, arg2);
    	}
    	else
    	{
    		return this.oilFluidTank.drain(arg1, arg2);
    	}
	}


	@Override
	public FluidStack drain(ForgeDirection direction, int arg1, boolean arg2) 
	{
    	if(direction.equals(ForgeDirection.DOWN))
    	{
    		return this.metalFluidTank.drain(arg1, arg2);
    	}
    	else if(direction.equals(ForgeDirection.UP))
    	{
    		return this.waterFluidTank.drain(arg1, arg2);
    	}
    	else
    	{
    		return this.oilFluidTank.drain(arg1, arg2);
    	}
	}


	@Override
	public int fill(ForgeDirection direction, FluidStack arg1, boolean arg2) 
	{
    	if(direction.equals(ForgeDirection.DOWN))
    	{
    		return this.metalFluidTank.fill(arg1, arg2);
    	}
    	else if(direction.equals(ForgeDirection.UP))
    	{
    		return this.waterFluidTank.fill(arg1, arg2);
    	}
    	else
    	{
    		return this.oilFluidTank.fill(arg1, arg2);
    	}
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction) 
	{
    	if(direction.equals(ForgeDirection.DOWN))
    	{
    		return new FluidTankInfo[] {this.metalFluidTank.getInfo()};
    	}
    	else if(direction.equals(ForgeDirection.UP))
    	{
    		return new FluidTankInfo[] {this.waterFluidTank.getInfo()};
    	}
    	else
    	{
    		return new FluidTankInfo[] {this.oilFluidTank.getInfo()};
    	}
	}
}
