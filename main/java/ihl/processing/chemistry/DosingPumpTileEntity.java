package ihl.processing.chemistry;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.processing.metallurgy.BasicElectricMotorTileEntity;
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

public class DosingPumpTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler {
	public final InvSlotConsumableLiquidIHL drainInputSlot;
	public final InvSlotConsumableLiquidIHL fillInputSlot;
	public final InvSlotOutput emptyFluidItemsSlot;
	private final IHLFluidTank fluidTank = new IHLFluidTank(8000);
	public int fluidAmountSetpoint = 8000;
	private boolean prevIsPowered = false;
	private boolean tickFree=false;

	public DosingPumpTileEntity() {
		super();
		this.drainInputSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", -1, InvSlot.Access.I, 1,
				InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
		this.fillInputSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", -1, InvSlot.Access.I, 1,
				InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 1);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
		this.fluidAmountSetpoint = nbttagcompound.getInteger("fluidAmountSetpoint");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagCompound fluidTankTag = new NBTTagCompound();
		this.fluidTank.writeToNBT(fluidTankTag);
		nbttagcompound.setTag("fluidTank", fluidTankTag);
		nbttagcompound.setInteger("fluidAmountSetpoint", this.fluidAmountSetpoint);
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing() != side;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("dosingPump");
	}

	@Override
	public boolean enableUpdateEntity() {
		return IC2.platform.isSimulating();
	}

	@Override
	public void updateEntityServer() {
		super.updateEntityServer();
		this.tickFree = true;
		IHLUtils.handleFluidSlotsBehaviour(fillInputSlot, drainInputSlot, emptyFluidItemsSlot, fluidTank);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int amount, boolean doDrain) {
		switch (from) {
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

	// 1.7.10 API
	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return true;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid arg1) {
		return !direction.equals(ForgeDirection.getOrientation(this.getFacing()));
	}

	@Override
	public String getInventoryName() {
		return "dosingPump";
	}

	public float getRenderLiquidLevel() {
		return (float) this.fluidTank.getFluidAmount() / (float) this.fluidTank.getCapacity();
	}

	@Override
	public int gaugeProgressScaled(int i) {
		return this.progress * i / operationLength;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new DosingPumpGui(new DosingPumpContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		this.fluidTank.sortFluidsByDensity();
		return new DosingPumpContainer(player, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {
	}

	@Override
	public boolean canOperate() {
		return false;
	}

	@Override
	public void operate() {
		int fluidAmountToDrain = fluidAmountSetpoint;
		ForgeDirection dir = ForgeDirection.getOrientation(this.getFacing());
		TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
		if (te instanceof IFluidHandler) {
			IFluidHandler fhte = (IFluidHandler) te;
			for (int i = 0; i < this.fluidTank.getNumberOfFluids(); i++) {
				FluidStack drained = this.fluidTank.drain(fluidAmountToDrain, true);
				fluidAmountToDrain -= drained.amount;
				if (fhte.canFill(dir, drained.getFluid())) {
					fhte.fill(dir, drained, true);
				}
				if (fluidAmountToDrain <= 0) {
					break;
				}
			}
		}
		this.energy-=this.energyConsume/10;
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack fluidStack, boolean doDrain) {
		if (fluidTank.getFluid() != null && fluidTank.getFluid().containsFluid(fluidStack)) {
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
		return new FluidTankInfo[] { this.fluidTank.getInfo() };
	}

	public boolean needsFluid() {
		return this.fluidTank.getFluidAmount() <= this.fluidTank.getCapacity();
	}

	public FluidStack getFluidStackfromTank() {
		return this.fluidTank.getFluid();
	}

	public int getTankAmount() {
		return this.fluidTank.getFluidAmount();
	}

	public int gaugeLiquidScaled(int i, int index) {
		return this.fluidTank.getFluidAmount() <= 0 ? 0
				: this.fluidTank.getFluidAmount(index) * i / this.fluidTank.getCapacity();
	}

	public int getNumberOfFluidsInTank() {
		return this.fluidTank.getNumberOfFluids();
	}

	public IHLFluidTank getFluidTank() {
		return this.fluidTank;
	}

	@Override
	public List<?>[] getInput() {
		return null;
	}

	public void setPowered(boolean isPowered) {
		if (isPowered && !prevIsPowered && this.energy > 0 && this.tickFree) {
			this.operate();
		}
		prevIsPowered = isPowered;
		this.tickFree = false; // Only one operation per tick max
	}

}