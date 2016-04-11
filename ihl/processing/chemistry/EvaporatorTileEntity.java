package ihl.processing.chemistry;

import java.util.Arrays;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import ic2.api.item.IC2Items;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.IHLMod;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;

public class EvaporatorTileEntity extends TileEntityLiquidTankInventory implements IHasGui
{
    public final IHLInvSlotOutput outputSlot;
    public InvSlot fuelSlot;
    public final InvSlotConsumableLiquidIHL fluidItemsSlot;
    public final InvSlotConsumableLiquidIHL fillItemsSlot;
    public final InvSlotOutput emptyFluidItemsSlot;
    
    public short progress = 0;
    public short maxProgress = 450;
    public int fuel = 0;
    public int maxFuel = 0;
    protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("evaporator");
    
    public EvaporatorTileEntity()
    {
    	super(8);
    	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 1);
    	this.fuelSlot = new InvSlotConsumableFuel(this, "fuel", 1, 1, true);
    	this.fluidItemsSlot = new InvSlotConsumableLiquidIHL(this, "drainInput", 2, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain);
    	this.fillItemsSlot = new InvSlotConsumableLiquidIHL(this, "fillInput", 4, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Fill);
    	this.emptyFluidItemsSlot = new InvSlotOutput(this, "fluidCellsOutput", 3, 1);
    }
    
    public static void init()
    {
    	addRecipe(new FluidStack(FluidRegistry.getFluid("fluidrubbertreesap"),200), IC2Items.getItem("resin"));
    }
    
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);

        try
        {
            this.fuel = nbttagcompound.getInteger("fuel");
        }
        catch (Throwable var4)
        {
            this.fuel = nbttagcompound.getShort("fuel");
        }

        try
        {
            this.maxFuel = nbttagcompound.getInteger("maxFuel");
        }
        catch (Throwable var3)
        {
            this.maxFuel = nbttagcompound.getShort("maxFuel");
        }

        this.progress = nbttagcompound.getShort("progress");
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("fuel", this.fuel);
        nbttagcompound.setInteger("maxFuel", this.maxFuel);
        nbttagcompound.setShort("progress", this.progress);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=(short)side && side!=0 && side!=1;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		if(this.outputSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.outputSlot.get()));
		if(this.fuelSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.fuelSlot.get()));
		if(this.emptyFluidItemsSlot.get()!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.emptyFluidItemsSlot.get()));
		return new ItemStack(IHLMod.evaporatorBlock,1);
	}
    
    public int gaugeProgressScaled(int i)
    {
        return this.progress * i / maxProgress;
    }

    public int gaugeFuelScaled(int i)
    {
        if (this.maxFuel == 0)
        {
            this.maxFuel = this.fuel;

            if (this.maxFuel == 0)
            {
                this.maxFuel = 160;
            }
        }

        return this.fuel * i / this.maxFuel;
    }

    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }

    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFluid())
        {
       		IHLUtils.handleFluidSlotsBehaviour(fillItemsSlot, fluidItemsSlot, emptyFluidItemsSlot, fluidTank);
        }
        if (this.fuel <= 0 && this.canOperate())
        {
            this.fuel = this.maxFuel = ((InvSlotConsumableFuel) this.fuelSlot).consumeFuel();

            if (this.fuel > 0)
            {
                needsInvUpdate = true;
            }
        }

        if (this.isBurning() && this.canOperate())
        {
            ++this.progress;

            if (this.progress >= maxProgress)
            {
                this.progress = 0;
                this.operate();
                needsInvUpdate = true;
            }
        }
        else
        {
            this.progress = 0;
        }

        if (this.fuel > 0)
        {
            --this.fuel;
        }

        if (this.getActive() != this.isBurning())
        {
            this.setActive(this.isBurning());
            needsInvUpdate = true;
        }
    }
    
    public void operate()
    {
    	UniversalRecipeOutput output = this.getOutput();
    	if(output!=null && !output.getItemOutputs().isEmpty())
    	{
    		this.outputSlot.add(this.getOutput().getItemOutputs().get(0).copy());
    	}
    	this.fluidTank.drain(recipeManager.getRecipeInput(Arrays.asList(new FluidStack [] {this.fluidTank.getFluid()}),null).getFluidInputs().get(0).getAmount(), true);
    }

    public boolean isBurning()
    {
        return this.fuel > 0;
    }

    public boolean canOperate()
    {
        if (this.fluidTank.getFluid()==null)
        {
            return false;
        }
        else
        {
            UniversalRecipeOutput output = recipeManager.getOutputFor(Arrays.asList(new FluidStack [] {this.fluidTank.getFluid()}),null, false, false);
            return output == null ? false : (this.outputSlot.canAdd(output.getItemOutputs()) ? true : false);
        }
    }

    /**
     * Returns the name of the inventory
     */
    @Override
	public String getInventoryName()
    {
        return "Solid fuel evaporator";
    }

    @Override
	public ContainerBase getGuiContainer(EntityPlayer entityPlayer)
    {
        return new EvaporatorContainer(entityPlayer, this);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin)
    {
        return new EvaporatorGui(new EvaporatorContainer(entityPlayer, this));
    }

    @Override
	public void onGuiClosed(EntityPlayer entityPlayer) {}

	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection arg0, Fluid fluid1) {
		return true;
	}
	
    public boolean getGui(EntityPlayer player)
    {
			return this instanceof IHasGui ? (IC2.platform.isSimulating() ? IC2.platform.launchGui(player, this) : true) : false;
    }
    
    public int mX()
	{
		switch(this.getFacing())
		{
		case 4:
		return -1;
		case 5:
		return 1;
		default:
		return 0;
		}
	}
    
    public int mZ()
	{
		switch(this.getFacing())
		{
		case 2:
		return -1;
		case 3:
		return 1;
		default:
		return 0;
		}
	}
    
    public static void addRecipe(FluidStack input, ItemStack output)
    {
        recipeManager.addRecipe(new UniversalRecipeInput(Arrays.asList(new FluidStack [] {input}),null), new UniversalRecipeOutput(null, Arrays.asList(new ItemStack[] {output}),20));
    }
    
    public UniversalRecipeOutput getOutput()
    {
        if (this.fluidTank.getFluid()==null)
        {
            return null;
        }
        else
        {
            UniversalRecipeOutput output = recipeManager.getOutputFor(Arrays.asList(new FluidStack [] {this.fluidTank.getFluid()}),null, false, false);
            return output == null ? null : (this.outputSlot.canAdd(output.getItemOutputs()) ? output : null);
        }
    }

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}
}