package ihl.processing.chemistry;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ic2.core.ContainerBase;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.processing.metallurgy.BasicElectricMotorTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;
import net.minecraft.block.Block;
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

public class CryogenicDistillerTileEntity extends BasicElectricMotorTileEntity implements IFluidHandler{

    public final InvSlotConsumableLiquidIHL fillInputSlotInput;
    public final InvSlotOutput fluidItemsSlot;
    public final InvSlotConsumableLiquidIHL fillInputSlotProducts;

	private int processTimer=0;
	private IHLFluidTank fluidTankInput = new IHLFluidTank(8000);
	public IHLFluidTank fluidTankProducts = new IHLFluidTank(8000);
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("cryogenicdistiller");

	public CryogenicDistillerTileEntity()
	{
		super();
		this.isGuiScreenOpened=true;
        this.fillInputSlotInput = new InvSlotConsumableLiquidIHL(this, "fillInputSlotInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
        this.fillInputSlotProducts = new InvSlotConsumableLiquidIHL(this, "fillInputSlotProducts", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill);
		this.fluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 2, 2);
	}
	
	@Override
	public String getInventoryName() {
		return "CryogenicDistiller";
	}
	
	   @Override
	public void updateEntityServer()
	    {
	        super.updateEntityServer();
			ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
			TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ);
			if(this.processTimer++>20 && this.engine.correctContent() && this.energy>=this.energyConsume/this.engine.getEfficiency())
			{
				this.energy-=this.energyConsume/this.engine.getEfficiency();
				this.processTimer=0;
				dir = dir.getOpposite();
				Block block = worldObj.getBlock(xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ);
				if(block!=null && block.isAir(worldObj, xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ) && this.fluidTankInput.getFluidAmount()<1000)
				{
					if(this.fluidTankInput.getFluid()==null || (this.fluidTankInput.getNumberOfFluids()<=1 && this.fluidTankInput.getFluid().getFluid()==FluidRegistry.getFluid("air")))
					{
						this.fluidTankInput.fill(IHLUtils.getFluidStackWithSize("air", 1000-(this.fluidTankInput.getFluid()!=null?this.fluidTankInput.getFluid().amount:0)),true);
					}
				}
				TileEntity teOnTop = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
				if(teOnTop instanceof IFluidHandler && this.fluidTankProducts.getLigthestFluid()!=null)
				{
					IFluidHandler topFH = (IFluidHandler)teOnTop;
					if(topFH.canFill(ForgeDirection.DOWN, this.fluidTankProducts.getLigthestFluid().getFluid()))
					{
						FluidStack fsToDrain = this.fluidTankProducts.getLigthestFluid().copy();
						fsToDrain.amount=topFH.fill(ForgeDirection.DOWN, fsToDrain, true);
						this.fluidTankProducts.drain(fsToDrain, true);
					}
				}
				ForgeDirection orientation = ForgeDirection.getOrientation(this.getFacing());
				TileEntity teOnFront = worldObj.getTileEntity(xCoord+orientation.offsetX, yCoord, zCoord+orientation.offsetZ);
				if(teOnFront instanceof IFluidHandler && this.fluidTankProducts.getFluid()!=null)
				{
					IFluidHandler frontFH = (IFluidHandler)teOnFront;
					if(frontFH.canFill(orientation, this.fluidTankProducts.getFluid().getFluid()))
					{
						FluidStack fsToDrain = this.fluidTankProducts.getFluid().copy();
						fsToDrain.amount=frontFH.fill(orientation, fsToDrain, true);
						this.fluidTankProducts.drain(fsToDrain, true);
					}
				}
			}
			IHLUtils.handleFluidSlotsBehaviour(fillInputSlotProducts, null, fluidItemsSlot, fluidTankProducts);
	    }
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("cryogenicDistiller");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new CryogenicDistillerGui(new CryogenicDistillerContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new CryogenicDistillerContainer(player, this);
	}
	
	@Override
	public void operate() 
	{
        if (!this.fillInputSlotInput.isEmpty() && this.fluidTankInput.getFluid().getFluid()==FluidRegistry.getFluid("air"))
        {
        	IHLUtils.handleFluidSlotsBehaviour(fillInputSlotInput, null, fluidItemsSlot, fluidTankInput);
        }
        else
        {
    		UniversalRecipeInput ri = CryogenicDistillerTileEntity.recipeManager.getRecipeInput(getInput());
    		Iterator<FluidStack> listFluidOutputsIterator = getOutput().getFluidOutputs().iterator();
    		while(listFluidOutputsIterator.hasNext())
    		{
    			this.fluidTankProducts.fill(listFluidOutputsIterator.next(), true);
    		}
    		this.fluidTankInput.drain(ri.getFluidInputs().get(0), true);
        }
	}
	
    public UniversalRecipeOutput getOutput()
    {
    	return CryogenicDistillerTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }
    
	@Override
	public List[] getInput()
	{
		return new List [] {Arrays.asList(new FluidStack [] {this.fluidTankInput.getLigthestFluid()}),null};
	}
	
	@Override
	public boolean canOperate()
	{
		return this.engine.correctContent() && this.getOutput()!=null;
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) {}
	
	@Override
	   public void readFromNBT(NBTTagCompound nbttagcompound)
	    {
	        super.readFromNBT(nbttagcompound);
	        this.fluidTankInput.readFromNBT(nbttagcompound.getCompoundTag("fluidTankInput"));
	        this.fluidTankProducts.readFromNBT(nbttagcompound.getCompoundTag("fluidTankProducts"));
	    }

	@Override
	    public void writeToNBT(NBTTagCompound nbttagcompound)
	    {
	        super.writeToNBT(nbttagcompound);
	        NBTTagCompound fluidTankInputTag = new NBTTagCompound();
	        this.fluidTankInput.writeToNBT(fluidTankInputTag);
	        nbttagcompound.setTag("fluidTankInput", fluidTankInputTag);
	        NBTTagCompound fluidTankProductsTag = new NBTTagCompound();
	        this.fluidTankProducts.writeToNBT(fluidTankProductsTag);
	        nbttagcompound.setTag("fluidTankProducts", fluidTankProductsTag);
	    }

	public static void addRecipe(FluidStack input, FluidStack output, FluidStack output2, boolean specialCondition) 
	{
		if(output2!=null)
		{
			recipeManager.addRecipe(new UniversalRecipeInput(Arrays.asList(new FluidStack[] {input}),null), new UniversalRecipeOutput(Arrays.asList(new FluidStack[] {output,output2}),null,20,specialCondition));
		}
		else
		{
			recipeManager.addRecipe(new UniversalRecipeInput(Arrays.asList(new FluidStack[] {input}),null), new UniversalRecipeOutput(Arrays.asList(new FluidStack[] {output}),null,20,specialCondition));
		}
	}
	

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}
	
	public boolean canProcess()
	{
		return this.engine.correctContent() && this.energy>=this.maxStorage && this.fluidTankInput.getFluidAmount()<1000;
	}

	@Override
	public boolean canDrain(ForgeDirection dir, Fluid arg1) {
		return dir.getOpposite().equals(ForgeDirection.getOrientation(this.getFacing())) || dir.equals(ForgeDirection.UP);
	}

	@Override
	public boolean canFill(ForgeDirection dir, Fluid fluid) {
		return dir.equals(ForgeDirection.getOrientation(this.getFacing()));
	}

	@Override
	public FluidStack drain(ForgeDirection dir, FluidStack fstack, boolean doDrain) {
		if(this.canDrain(dir, null))
		{
			return this.fluidTankProducts.drain(fstack, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection dir, int amount, boolean doDrain) {
		if(this.canDrain(dir, null))
		{
			if(dir.equals(ForgeDirection.UP))
			{
				return this.fluidTankProducts.drainLightest(amount, doDrain);
			}
			return this.fluidTankProducts.drain(amount, doDrain);
		}
		return null;
	}

	@Override
	public int fill(ForgeDirection dir, FluidStack fstack, boolean doFill) 
	{
		if(fstack!=null && fstack.getFluid()!=null && this.canFill(dir, fstack.getFluid()))
		{
			this.fluidTankInput.drain(IHLUtils.getFluidStackWithSize("air", this.fluidTankInput.getCapacity()), true);
			return this.fluidTankInput.fill(fstack, doFill);
		}
		return 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection dir) 
	{
		return new FluidTankInfo[] {this.fluidTankInput.getInfo(),this.fluidTankProducts.getInfo()};
	}
	
}