package ihl.processing.metallurgy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.ContainerBase;
import ic2.core.block.invslot.InvSlot.Access;
import ihl.IHLMod;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.recipes.RecipeOutputItemStack;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AchesonFurnanceTileEntity extends MachineBaseTileEntity{

    public final ApparatusProcessableInvSlot inputElectrode;
	protected static UniversalRecipeManager recipeManager = new UniversalRecipeManager("achesonfurnace");
	private final RecipeInputOreDict[] validElectrodeTypes=new RecipeInputOreDict[] {new RecipeInputOreDict("stickCoal"),new RecipeInputOreDict("stickGraphite"),new RecipeInputOreDict("plateCoal"),new RecipeInputOreDict("plateGraphite")};
	
	public AchesonFurnanceTileEntity()
	{
		super(2);
		inputElectrode = new ApparatusProcessableInvSlot(this, "inputElectrode", 1, Access.IO, 2, 1);
	}
	
	public static void addRecipe(IRecipeInput input1, IRecipeInput input2, String output1)
	{
		recipeManager.addRecipe(new UniversalRecipeInput(null,Arrays.asList(new IRecipeInput [] {input1,input2})),new UniversalRecipeOutput(null,Arrays.asList(new ItemStack [] {IHLUtils.getThisModItemStack(output1)}),20));
	}

	@Override
    public String getStartSoundFile()
    {
        return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
    }

	@Override
	public String getLoopSoundFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStopSoundFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInventoryName() {
		return "AchesonFurnance";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new AchesonFurnaceGui(new AchesonFurnanceContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new AchesonFurnanceContainer(player, this);
	}
	
	@Override
	public void operate() 
	{
		List<RecipeOutputItemStack> output = AchesonFurnanceTileEntity.recipeManager.getOutputFor(getInput(), true, true).getItemOutputs();
		for(int i=0; i<this.inputElectrode.size();i++)
		{
			ItemStack electrodeIS = this.inputElectrode.get(i);
			if(this.validElectrodeTypes[0].matches(electrodeIS))
			{
				this.inputElectrode.put(i, IHLUtils.getThisModItemStack("stickGraphite"));
			}
			else if(this.validElectrodeTypes[2].matches(electrodeIS))
			{
				this.inputElectrode.put(i, IHLUtils.getThisModItemStack("plateGraphite"));
			}
		}
		for(int i=0; i<this.input.size();i++)
		{
			if(output.size()>i)
			{
				ItemStack stack = output.get(i).itemStack.copy();
				stack.stackSize=Math.round(output.get(i).quantity);
				this.input.put(i,stack);
			}
			if(this.input.get(i)!=null && this.input.get(i).stackSize<=0)
			{
				this.input.put(i, null);
			}
		}
        ItemStack crucible = input.getItemStack(IHLMod.crucible);
        if(crucible!=null)
        {
        	ItemStack stack = ((Crucible)crucible.getItem()).processContent(crucible, this);
        }
	}
	
	@Override
	public boolean canOperate()
	{
		return this.isValidElectrode(this.inputElectrode.get(0)) && 
				this.isValidElectrode(this.inputElectrode.get(1)) && 
				this.getOutput()!=null;
	}
	
	@Override
	public List[] getInput()
	{
		return new List[] {null, this.input.getItemStackList()};
	}
	

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}
	
    @Override
	public UniversalRecipeOutput getOutput()
    {
    	return AchesonFurnanceTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }
    
    private boolean isValidElectrode(ItemStack stack)
    {
    	if(stack!=null)
    	{
        	for(int i=0;i<this.validElectrodeTypes.length;i++)
        	{
        		if(validElectrodeTypes[i].matches(stack))
        		{
        			return true;
        		}
        	}
    	}
    	return false;
    }
}
