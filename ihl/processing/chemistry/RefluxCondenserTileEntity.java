package ihl.processing.chemistry;

import java.util.ArrayList;
import java.util.List;

import ic2.api.network.INetworkDataProvider;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.network.NetworkManager;
import ihl.utils.IHLUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class RefluxCondenserTileEntity extends TileEntity implements IWrenchable, INetworkDataProvider, IFluidHandler{

	private short facing=2;
	private short lastFacing=2;
	public FractionatorBottomTileEntity columnBottom;
	
	public RefluxCondenserTileEntity()
	{
		super();
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = new ArrayList();
		fields.add("facing");
		return fields;
    }
	
    @Override
	public void updateEntity()
    {
        super.updateEntity();
        if(lastFacing!=facing)
        {
			IC2.network.get().updateTileEntityField(this, "facing");
			lastFacing=facing;
        }
    }
	

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=side;
	}


	@Override
	public short getFacing() {
		return this.facing;
	}


	@Override
	public void setFacing(short facing1) 
	{
		facing=(short) Math.max(2,facing1);
		if(IC2.platform.isSimulating())
		{
			IC2.network.get().updateTileEntityField(this, "facing");
		}
	}


	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}


	@Override
	public float getWrenchDropRate() {
		return 1F;
	}


	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("refluxCondenser");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        facing=nbttagcompound.getShort("facing");
    }

	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("facing", facing);
    }

	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection direction, Fluid arg1) {
		return direction.equals(ForgeDirection.UP);
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection arg0, int arg1, boolean arg2) {
		return null;
	}

	@Override
	public int fill(ForgeDirection direction, FluidStack fluidStack, boolean doFill) {
		if(fluidStack!=null && fluidStack.getFluid()!=null && this.canFill(direction, fluidStack.getFluid()) && columnBottom!=null)
		{
			return columnBottom.fill(direction, fluidStack, doFill);
		}
		return 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) 
	{
		return columnBottom.getTankInfo(arg0);
	}

}
