package ihl.processing.metallurgy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ic2.core.ContainerBase;
import ic2.core.block.invslot.InvSlot.Access;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExtruderTileEntity extends BasicElectricMotorTileEntity{

    public final ApparatusProcessableInvSlot input;
    public final ApparatusProcessableInvSlot input2;
	private int processTimer=0;
	private FluidTank fluidTank = new FluidTank(1000);
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("extruder");

	public ExtruderTileEntity()
	{
		super();
		input = new ApparatusProcessableInvSlot(this, "input", 1, Access.IO, 1, 64);
		input2 = new ApparatusProcessableInvSlot(this, "input2", 2, Access.IO, 1, 64);
	}
	
	
	
	@Override
    public List<String> getNetworkedFields()
    {
		return super.getNetworkedFields();
    }
	

	@Override
	public String getInventoryName() {
		return "Extruder";
	}
	
	   @Override
	public void updateEntityServer()
	    {
	        super.updateEntityServer();
			ForgeDirection dir = ForgeDirection.getOrientation(getFacing());
			TileEntity te = worldObj.getTileEntity(xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ);
			if(checkCorrectAccembly() && this.fluidTank.getFluidAmount()>0 && this.processTimer++>20)
			{
				this.processTimer=0;
				((IFluidHandler)te).fill(ForgeDirection.getOrientation(getFacing()), this.fluidTank.drain(this.fluidTank.getCapacity(), true), true);
			}

	    }
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("extruder");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new ExtruderGui(new ExtruderContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new ExtruderContainer(player, this);
	}
	
	@Override
	public void operate() 
	{
		UniversalRecipeInput ri = ExtruderTileEntity.recipeManager.getRecipeInput(getInput());
		this.fluidTank.fill(getOutput().getFluidOutputs().get(0), true);
		this.input.consume(ri.getItemInputs().get(0));
		this.input2.consume(ri.getItemInputs().get(1));
	}
	
    public UniversalRecipeOutput getOutput()
    {
    	return ExtruderTileEntity.recipeManager.getOutputFor(this.getInput());
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List[] getInput()
	{
		return new List[]{null,Arrays.asList(new ItemStack[] {input.get(),input2.get()})};
	}
	
	@Override
	public boolean canOperate()
	{
		return this.fluidTank.getFluidAmount()<this.fluidTank.getCapacity() && this.getOutput()!=null && checkCorrectAccembly();
	}
	
	private boolean checkCorrectAccembly()
	{
		TileEntity te = worldObj.getTileEntity(xCoord+ForgeDirection.getOrientation(getFacing()).offsetX,yCoord+ForgeDirection.getOrientation(getFacing()).offsetY,zCoord+ForgeDirection.getOrientation(getFacing()).offsetZ);
		return te instanceof IFluidHandler && ((IFluidHandler)te).canFill(ForgeDirection.getOrientation(getFacing()), FluidRegistry.getFluid("molten.rubberwithsulfur"));
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) {}
		
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

	public static void addRecipe(ItemStack input, ItemStack input2, FluidStack output) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput(null, new ItemStack[] {input,input2}), new UniversalRecipeOutput(new FluidStack [] {output},null,20));
	}
	

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
	
}
