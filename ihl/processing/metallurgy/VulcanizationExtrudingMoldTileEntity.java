package ihl.processing.metallurgy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.network.INetworkDataProvider;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ihl.items_blocks.FlexibleCableItem;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class VulcanizationExtrudingMoldTileEntity extends TileEntity implements IWrenchable, INetworkDataProvider, IEnergySink, IProductionLine, IFluidHandler{

	private short facing=2;
	private short lastFacing=2;
    public double energy;
    public int maxStorage=64;
	private double energyConsume=16D;
	private boolean addedToEnergyNet=false;
	private FluidTank fluidTank = new FluidTank(5000);
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("vulcanizationextrudingmold");

	public VulcanizationExtrudingMoldTileEntity()
	{
		super();
	}
	
    public static void addRecipe(FluidStack inputFluid,ItemStack input,ItemStack output1)
    {
    	recipeManager.addRecipe(new UniversalRecipeInput((new FluidStack[] {inputFluid}),(new ItemStack[] {input})),new UniversalRecipeOutput(null,(new ItemStack[] {output1}),200));
    }
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
    
    @Override
	public void invalidate()
    {
        if (IC2.platform.isSimulating()&&this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.invalidate();
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
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }

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
        if (IC2.platform.isSimulating()&&this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
		facing=(short) Math.max(2,facing1);
		if(IC2.platform.isSimulating())
		{
			IC2.network.get().updateTileEntityField(this, "facing");
			lastFacing=facing;
		}
        if (IC2.platform.isSimulating()&&!this.addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
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
		return IHLUtils.getThisModItemStack("vulcanizationExtrudingMold");
	}
	
	@Override
	   public void readFromNBT(NBTTagCompound nbttagcompound)
	    {
	        super.readFromNBT(nbttagcompound);
	        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
	        facing=nbttagcompound.getShort("facing");
	        energy=nbttagcompound.getDouble("energy");
	    }

	@Override
	    public void writeToNBT(NBTTagCompound nbttagcompound)
	    {
	        super.writeToNBT(nbttagcompound);
	        NBTTagCompound fluidTankTag = new NBTTagCompound();
	        this.fluidTank.writeToNBT(fluidTankTag);
	        nbttagcompound.setTag("fluidTank", fluidTankTag);
	        nbttagcompound.setShort("facing", facing);
	        nbttagcompound.setDouble("energy", this.energy);
	    }
	
	@Override
	public double getDemandedEnergy() 
	{
			return this.maxStorage-this.energy;
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount,
			double voltage) {
			if(this.energy<this.maxStorage)
			{
				this.energy+=amount;
	            return 0.0D;
			}
	        else
	        {
	            return amount;
	        }
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) 
	{
		return true;
	}

	private ItemStack processWire(ItemStack wire, boolean doProcess) {
		UniversalRecipeOutput rOutput = VulcanizationExtrudingMoldTileEntity.recipeManager.getOutputFor(Arrays.asList(new FluidStack[] {this.fluidTank.getFluid()}),Arrays.asList(new ItemStack[] {wire}), false, false);
		if(rOutput==null || !(wire.getItem() instanceof FlexibleCableItem) || this.energy<this.energyConsume)
		{
			return wire;
		}
		UniversalRecipeInput rInput = VulcanizationExtrudingMoldTileEntity.recipeManager.getRecipeInput(Arrays.asList(new FluidStack[] {this.fluidTank.getFluid()}),Arrays.asList(new ItemStack[] {wire}));
		ItemStack cableAsRO = rOutput.getItemOutputs().get(0).itemStack;
		ItemStack cable = wire.copy();
		FlexibleCableItem fci = (FlexibleCableItem) cableAsRO.getItem();
		cable.stackTagCompound.setString("insulationMaterial", fci.getInsulationMaterial(cableAsRO));
		cable.stackTagCompound.setInteger("insulationThickness", fci.getInsulationThickness(cableAsRO));
		cable.stackTagCompound.setInteger("maxVoltage", IHLUtils.getInsulationMaxVoltage(fci.getInsulationMaterial(cableAsRO), fci.getInsulationThickness(cableAsRO)));
		if(doProcess)
		{
			this.energy-=this.energyConsume;
			this.fluidTank.drain(rInput.getFluidInputs().get(0).getAmount()*IHLUtils.getWireLength(cable), true);
		}
		return cable;
	}
	
	@Override
	public boolean canProcess(ItemStack wire) {
		ItemStack cable = this.processWire(wire,false);
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing()).getRotation(ForgeDirection.UP);
		TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ);
		if(te instanceof IProductionLine)
		{
			if(ForgeDirection.getOrientation(((IProductionLine)te).getFacing()).equals(dir.getRotation(ForgeDirection.DOWN)))
			{
				return ((IProductionLine)te).canProcess(cable);
			}
		}
		return false;
	}

	@Override
	public void process(ItemStack wire) {
		ItemStack cable = this.processWire(wire,true);
		ForgeDirection dir = ForgeDirection.getOrientation(getFacing()).getRotation(ForgeDirection.UP);
		TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ);
		if(te instanceof IProductionLine && ForgeDirection.getOrientation(((IProductionLine)te).getFacing()).equals(dir.getRotation(ForgeDirection.DOWN)))
		{
			((IProductionLine)te).process(cable);
		}
	}

	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection arg0, Fluid arg1) {
		return ForgeDirection.getOrientation(this.facing)==arg0;
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
	public int fill(ForgeDirection arg0, FluidStack arg1, boolean arg2) {
		return this.fluidTank.fill(arg1, arg2);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() 
	{
		return recipeManager.getRecipes();
	}
}
