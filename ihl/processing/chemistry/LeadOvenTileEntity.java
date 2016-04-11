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
import net.minecraftforge.fluids.IFluidHandler;
import ic2.api.recipe.IRecipeInput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlot.Access;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;

public class LeadOvenTileEntity extends TileEntityLiquidTankInventory implements IHasGui
{
    public final InvSlotConsumableFuel fuelSlot;
    public final ApparatusProcessableInvSlot inputSlot;
    public final IHLInvSlotOutput outputSlot;
    public short progress = 0;
    public final short maxProgress = 160;
    public int fuel = 0;
    public int maxFuel = 0;
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("chemicaloven");
    private ItemStack burnedFuel;
	
    public LeadOvenTileEntity()
    {
    	super(1000);
    	this.fuelSlot = new InvSlotConsumableFuel(this, "fuel", 1, 1, true);
    	this.inputSlot = new ApparatusProcessableInvSlot(this, "input", 2, Access.IO, 2, 64);
    	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 1);
    }
    
    public static void addRecipe(UniversalRecipeInput input,UniversalRecipeOutput output)
    {
    	recipeManager.addRecipe(input, output);
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
        NBTTagCompound gasBufferTag = new NBTTagCompound();
        nbttagcompound.setTag("gasBuffer", gasBufferTag);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) 
	{
		return this.getFacing()!=(short)side && side!=0 && side!=1;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) 
	{
		return IHLUtils.getThisModItemStack("leadOven");
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
        if(this.getFluidTank().getFluid()!=null)
        {
        	FluidStack fs;
        	if(this.maxProgress>0 && this.getOutput()!=null && this.getOutput().getFluidOutputs()!=null && this.getOutput().getFluidOutputs().size()>0)
        	{
        		fs = this.getFluidTank().drain(this.getOutput().getFluidOutputs().get(0).amount/this.maxProgress, true);
        	}
        	else
        	{
        		fs = this.getFluidTank().drain(1000, true);
        	}
        	TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
        	if(te!=null)
        	{
        		ForgeDirection direction = ForgeDirection.DOWN;
        		if(te instanceof IFluidHandler)
        		{
        			((IFluidHandler)te).fill(direction, fs, true);
        		}
        	}
        }
        boolean needsInvUpdate = false;
        if (this.fuel <= 0 && this.canOperate())
        {
        	if(this.fuelSlot.get()!=null)
        	{
        		if(burnedFuel!=null && IHLUtils.isItemsHaveSameOreDictionaryEntry(this.fuelSlot.get(), burnedFuel))
        		{
        			burnedFuel.stackSize++;
        		}
        		else if(burnedFuel==null)
        		{
        			burnedFuel=this.fuelSlot.get().copy();
        			burnedFuel.stackSize=1;
        		}
        	}
            this.fuel = this.maxFuel = this.fuelSlot.consumeFuel();

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
    
    public boolean isBurning()
    {
        return this.fuel > 0;
    }

    public boolean canOperate()
    {
            return this.getOutput()!=null;
    }

    /**
     * Returns the name of the inventory
     */
    @Override
	public String getInventoryName()
    {
        return "goldOven";
    }

    @Override
    public ContainerBase getGuiContainer(EntityPlayer entityPlayer)
    {
        return new LeadOvenContainer(entityPlayer, this);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin)
    {
        return new LeadOvenGui(new LeadOvenContainer(entityPlayer, this));
    }

    @Override
	public void onGuiClosed(EntityPlayer entityPlayer) {}
    
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
    
    public UniversalRecipeOutput getOutput()
    {
    	return LeadOvenTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }

	public List[] getInput()
	{
		if(this.inputSlot.get(1)!=null)
		{
			return new List[] {null, Arrays.asList(new ItemStack[] {this.inputSlot.get(0),this.inputSlot.get(1)})};
		}
		return new List[] {null, Arrays.asList(new ItemStack[] {this.inputSlot.get()})};
	}
	
	public void operate() 
	{
		List<IRecipeInput> rinput = LeadOvenTileEntity.recipeManager.getRecipeInput(getInput()).getItemInputs();
		UniversalRecipeOutput routput = LeadOvenTileEntity.recipeManager.getOutputFor(getInput(), false, false);
		List<FluidStack> output2 = routput.getFluidOutputs();
		if(!output2.isEmpty())
		{
			this.getFluidTank().fill(output2.get(0), true);
		}
		List itemOutputs = routput.getItemOutputs();
		if(itemOutputs!=null && !itemOutputs.isEmpty())
		{
			this.outputSlot.add(itemOutputs);
		}
		for(int i=0; i<rinput.size();i++)
		{
			if(this.burnedFuel!=null && IHLUtils.isItemsHaveSameOreDictionaryEntry(this.fuelSlot.get(), burnedFuel) && this.burnedFuel.stackSize>=rinput.get(i).getAmount())
			{
				this.burnedFuel.stackSize-=rinput.get(i).getAmount();
				if(this.burnedFuel.stackSize<=0)
				{
					this.burnedFuel=null;
				}
			}
			else
			{
				this.inputSlot.consume(i, rinput.get(i).getAmount());
			}
			if(this.inputSlot.get(i)!=null && this.inputSlot.get(i).stackSize<=0)
			{
				this.inputSlot.put(i, null);
			}
		}
		
	}

	@Override
	public boolean canDrain(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	@Override
	public boolean canFill(ForgeDirection arg0, Fluid arg1) {
		return false;
	}

	public static void addRecipe(UniversalRecipeInput input, FluidStack fluidStackWithSize) 
	{
		recipeManager.addRecipe(input, new UniversalRecipeOutput(Arrays.asList(new FluidStack[]{fluidStackWithSize}),null,20));
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

	public static void addRecipe(ItemStack input, FluidStack fluidStackOutput, ItemStack output) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, Arrays.asList(new ItemStack[] {input})),new UniversalRecipeOutput(Arrays.asList(new FluidStack[] {fluidStackOutput}),Arrays.asList(new ItemStack[] {output}),20));
	}

	public static void addRecipe(ItemStack input, ItemStack output) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, Arrays.asList(new ItemStack[] {input})),new UniversalRecipeOutput(null,Arrays.asList(new ItemStack[] {output}),20));
	}
}