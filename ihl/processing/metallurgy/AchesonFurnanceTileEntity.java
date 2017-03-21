package ihl.processing.metallurgy;

import java.util.List;
import java.util.Map;

import ic2.api.recipe.IRecipeInput;
import ic2.core.ContainerBase;
import ihl.IHLMod;
import ihl.processing.invslots.IHLInvSlotOutput;
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

public class AchesonFurnanceTileEntity extends MachineBaseTileEntity {

	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("achesonfurnace");
	public final IHLInvSlotOutput outputSlot;

	public AchesonFurnanceTileEntity() {
		super(2);
	 	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 1);
	}

	public static void addRecipe(IRecipeInput input1, IRecipeInput input2, String output1) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, new IRecipeInput[] { input1, input2 }),
				new UniversalRecipeOutput(null, new ItemStack[] { IHLUtils.getThisModItemStack(output1) }, 20));
	}

	@Override
	public String getStartSoundFile() {
		return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
	}

	@Override
	public String getLoopSoundFile() {
		return null;
	}

	@Override
	public String getStopSoundFile() {
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
	public void operate() {
		ItemStack crucible = input.getItemStack(IHLMod.crucible);
		if (crucible != null) {
			((Crucible) crucible.getItem()).processContent(crucible, this);
			return;
		}
		List<RecipeOutputItemStack> output = recipeManager
				.getOutputFor(getInput()).getItemOutputs();
		List<IRecipeInput> rinput = recipeManager.getRecipeInput(getInput()).getItemInputs();
		for (int i = 0; i < rinput.size(); i++) {
				this.input.consume(rinput.get(i));
		}
		this.outputSlot.add(output);
	}

	@Override
	public boolean canOperate() {
		return this.getOutput() != null;
	}

	@Override
	public List<?>[] getInput() {
		return new List[] { null, this.input.getItemStackList() };
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

	@Override
	public UniversalRecipeOutput getOutput() {
		return AchesonFurnanceTileEntity.recipeManager.getOutputFor(this.getInput());
	}
}
