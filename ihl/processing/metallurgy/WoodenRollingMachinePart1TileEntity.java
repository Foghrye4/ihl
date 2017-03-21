package ihl.processing.metallurgy;

import java.util.List;
import java.util.Map;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlot.Access;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.recipes.RecipeOutputItemStack;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WoodenRollingMachinePart1TileEntity extends BasicElectricMotorTileEntity{

    public final ApparatusProcessableInvSlot input;
    public final IHLInvSlotOutput output;
	public boolean assembled;
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("woodenrollingmachine");

	public WoodenRollingMachinePart1TileEntity()
	{
		super();
		input = new ApparatusProcessableInvSlot(this, "input", 1, Access.IO, 3, 64);
		output = new IHLInvSlotOutput(this, "output", 2, 1);
	}
	
    public UniversalRecipeOutput getOutput()
    {
    	return WoodenRollingMachinePart1TileEntity.recipeManager.getOutputFor(this.getInput());
    }
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("assembled");
		return fields;
    }
	

	@Override
	public String getInventoryName() {
		return "WoodenRollingMachine";
	}
	
	   @Override
	public void updateEntityServer()
	    {
	        super.updateEntityServer();
	        if(assembled && !this.checkCorrectAccembly())
	        {
	        	assembled=false;
				IC2.network.get().updateTileEntityField(this, "assembled");
	        }
	        else if(!assembled && this.checkCorrectAccembly())
	        {
	        	assembled=true;
				IC2.network.get().updateTileEntityField(this, "assembled");
	        }
            if (this.getActive() && this.progress == 0 && !this.canOperate())
            {
                this.setActive(false);
            }
	        
            if (!this.getActive() && this.progress>0 && this.canOperate())
            {
                this.setActive(true);
            }

	    }
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("woodenRollingMachinePart1");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new WoodenRollingMachineGui(new WoodenRollingMachineContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new WoodenRollingMachineContainer(player, this);
	}
	
	@Override
	public void operate() 
	{
		List<IRecipeInput> rInput = WoodenRollingMachinePart1TileEntity.recipeManager.getRecipeInput(getInput()).getItemInputs();
		List<RecipeOutputItemStack> rOutput = WoodenRollingMachinePart1TileEntity.recipeManager.getOutputFor(getInput()).getItemOutputs();
		this.output.add(rOutput);
		for(int i=0;i<rInput.size();i++)
		{
			this.input.consume(rInput.get(i));
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List[] getInput()
	{
		return new List[]{null, input.getItemStackList()};
	}
	
	@Override
	public boolean canOperate()
	{
		return this.getOutput()!=null && this.output.canAdd(this.getOutput().getItemOutputs()) && checkCorrectAccembly();
	}
	
	private boolean checkCorrectAccembly()
	{
		TileEntity te = worldObj.getTileEntity(xCoord+ForgeDirection.getOrientation(getFacing()).offsetX,yCoord+ForgeDirection.getOrientation(getFacing()).offsetY,zCoord+ForgeDirection.getOrientation(getFacing()).offsetZ);
		return te!=null && te instanceof WoodenRollingMachinePart2TileEntity && ((WoodenRollingMachinePart2TileEntity)te).getFacing()==this.getFacing();
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) {}
	
	public static void addRecipe(RecipeInputItemStack recipeInputItemStack, RecipeInputItemStack recipeInputItemStack2, RecipeInputOreDict recipeInputOreDict, ItemStack output) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new IRecipeInput[] {recipeInputItemStack,recipeInputItemStack2,recipeInputOreDict})), new UniversalRecipeOutput(null,(new ItemStack[] {output}),20));
	}
	

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

	public static void addRecipe(RecipeInputOreDict recipeInputOreDict, RecipeInputOreDict recipeInputOreDict2, ItemStack output) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new IRecipeInput[] {recipeInputOreDict,recipeInputOreDict2})), new UniversalRecipeOutput(null,(new ItemStack[] {output}),20));
	}

	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
	
}
