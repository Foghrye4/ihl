package ihl.processing.chemistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.IRecipeInput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LeadOvenTileEntity extends TileEntityInventory implements IHasGui, IFluidHandler {
	public final InvSlotConsumableFuel fuelSlot;
	public final ApparatusProcessableInvSlot inputSlot;
	public final IHLInvSlotOutput outputSlot;
	public final IHLFluidTank inputTank = new IHLFluidTank(1000);
	public final IHLFluidTank outputTank = new IHLFluidTank(1000);
	public final InvSlotConsumableLiquidIHL drainInputSlot;
	public final InvSlotConsumableLiquidIHL fillInputSlot;
	public final InvSlotOutput emptyFluidItemsSlot;
	public final InvSlotConsumableLiquidIHL drainInputSlot2;
	public final InvSlotConsumableLiquidIHL fillInputSlot2;
	public short progress = 0;
	public final short maxProgress = 160;
	public int fuel = 0;
	public int maxFuel = 0;
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("chemicaloven");

	public LeadOvenTileEntity() {
		super();
		this.fuelSlot = new InvSlotConsumableFuel(this, "fuel", 1, 1, true);
		this.inputSlot = new ApparatusProcessableInvSlot(this, "input", 2, Access.IO, 2, 64);
		this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 1);
		this.drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1,
				InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
		this.fillInputSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1,
				InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.drainInputSlot2 = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1,
				InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
		this.fillInputSlot2 = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1,
				InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 2);
	}

	public static void addRecipe(UniversalRecipeInput input, UniversalRecipeOutput output) {
		recipeManager.addRecipe(input, output);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.fuel = nbttagcompound.getInteger("fuel");
		this.maxFuel = nbttagcompound.getInteger("maxFuel");
		this.progress = nbttagcompound.getShort("progress");
		this.inputTank.readFromNBT(nbttagcompound.getCompoundTag("inputTank"));
		this.outputTank.readFromNBT(nbttagcompound.getCompoundTag("outputTank"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("fuel", this.fuel);
		nbttagcompound.setInteger("maxFuel", this.maxFuel);
		nbttagcompound.setShort("progress", this.progress);
		NBTTagCompound inputTankTag = new NBTTagCompound();
		this.inputTank.writeToNBT(inputTankTag);
		nbttagcompound.setTag("inputTank", inputTankTag);
		NBTTagCompound outputTankTag = new NBTTagCompound();
		this.outputTank.writeToNBT(outputTankTag);
		nbttagcompound.setTag("outputTank", outputTankTag);
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing() != (short) side && side != 0 && side != 1;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("leadOven");
	}

	public int gaugeProgressScaled(int i) {
		return this.progress * i / maxProgress;
	}

	public int gaugeFuelScaled(int i) {
		if (this.maxFuel == 0) {
			this.maxFuel = this.fuel;

			if (this.maxFuel == 0) {
				this.maxFuel = 160;
			}
		}

		return this.fuel * i / this.maxFuel;
	}

	public boolean enableUpdateEntity() {
		return IC2.platform.isSimulating();
	}

	@Override
	public void updateEntityServer() {
		super.updateEntityServer();
		IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, inputTank);
		IHLUtils.handleFluidSlotsBehaviour(fillInputSlot2, drainInputSlot2, emptyFluidItemsSlot, outputTank);
		if (this.fuel <= 0 && this.canOperate()) {
			this.fuel = this.maxFuel = this.fuelSlot.consumeFuel();
		}

		if (this.isBurning() && this.canOperate()) {
			++this.progress;

			if (this.progress >= maxProgress) {
				this.progress = 0;
				this.operate();
			}
		} else {
			this.progress = 0;
		}

		if (this.fuel > 0) {
			--this.fuel;
		}

		if (this.getActive() != this.isBurning()) {
			this.setActive(this.isBurning());
		}
	}

	public boolean isBurning() {
		return this.fuel > 0;
	}

	public boolean canOperate() {
		return this.getOutput() != null;
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getInventoryName() {
		return "goldOven";
	}

	@Override
	public ContainerBase<LeadOvenTileEntity> getGuiContainer(EntityPlayer entityPlayer) {
		return new LeadOvenContainer(entityPlayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
		return new LeadOvenGui(new LeadOvenContainer(entityPlayer, this));
	}

	@Override
	public void onGuiClosed(EntityPlayer entityPlayer) {
	}

	public UniversalRecipeOutput getOutput() {
		return LeadOvenTileEntity.recipeManager.getOutputFor(this.getInput());
	}

	@SuppressWarnings("rawtypes")
	public List[] getInput() {
		if (this.inputSlot.get(1) != null) {
			return new List[] { this.inputTank.getFluidList(),
					Arrays.asList(new ItemStack[] { this.inputSlot.get(0), this.inputSlot.get(1) }) };
		}
		return new List[] { this.inputTank.getFluidList(), Arrays.asList(new ItemStack[] { this.inputSlot.get() }) };
	}

	public void operate() {
		UniversalRecipeInput rinput = LeadOvenTileEntity.recipeManager.getRecipeInput(getInput());
		List<IRecipeInput> rinputItems = rinput.getItemInputs();
		UniversalRecipeOutput routput = LeadOvenTileEntity.recipeManager.getOutputFor(getInput());
		List<FluidStack> output2 = routput.getFluidOutputs();
		if (!output2.isEmpty()) {
			this.outputTank.fill(output2.get(0), true);
		}
		this.inputTank.drain(rinput.getFluidInputs(), true);
		List<RecipeOutputItemStack> itemOutputs = routput.getItemOutputs();
		if (itemOutputs != null && !itemOutputs.isEmpty()) {
			this.outputSlot.add(itemOutputs);
		}
		for (int i = 0; i < rinputItems.size(); i++) {
			this.inputSlot.consume(rinputItems.get(i));
		}

	}

	@Override
	public FluidStack drain(ForgeDirection from, int amount, boolean doDrain) {
		switch (from) {
		case UP:
			return this.outputTank.drainLightest(amount, doDrain);
		case NORTH:
			return this.outputTank.drainLightest(amount, doDrain);
		case SOUTH:
			return this.outputTank.drainLightest(amount, doDrain);
		case WEST:
			return this.outputTank.drainLightest(amount, doDrain);
		case EAST:
			return this.outputTank.drainLightest(amount, doDrain);
		case DOWN:
			return this.outputTank.drain(amount, doDrain);
		default:
			return this.outputTank.drain(amount, doDrain);
		}
	}

	// 1.7.10 API
	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return true;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid arg1) {
		return direction.equals(ForgeDirection.getOrientation(this.getFacing()).getOpposite());
	}

	public static void addRecipe(UniversalRecipeInput input, FluidStack fluidStackWithSize) {
		recipeManager.addRecipe(input, new UniversalRecipeOutput((new FluidStack[] { fluidStackWithSize }), null, 20));
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

	public static void addRecipe(IRecipeInput recipeInputOreDict, FluidStack fluidStackOutput, ItemStack output) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new IRecipeInput[] { recipeInputOreDict })),
				new UniversalRecipeOutput((new FluidStack[] { fluidStackOutput }), (new ItemStack[] { output }), 20));
	}

	public static void addRecipe(IRecipeInput input, ItemStack output) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new IRecipeInput[] { input })),
				new UniversalRecipeOutput(null, (new ItemStack[] { output }), 20));
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack fluidStack, boolean doDrain) {
		if (outputTank.getFluid() != null && outputTank.getFluid().containsFluid(fluidStack)) {
			return this.outputTank.drain(fluidStack, doDrain);
		}
		return null;
	}

	@Override
	public int fill(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return this.inputTank.fill(arg1, arg2);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) {
		return new FluidTankInfo[] { this.inputTank.getInfo() };
	}
}