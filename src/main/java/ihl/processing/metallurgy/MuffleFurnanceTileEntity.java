package ihl.processing.metallurgy;

import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

public class MuffleFurnanceTileEntity extends MachineBaseTileEntity {

	protected static UniversalRecipeManager recipeManager = new UniversalRecipeManager("mufflefurnace");
	public final IHLInvSlotOutput outputSlot;

	public MuffleFurnanceTileEntity() {
		super(2);
	 	this.outputSlot = new IHLInvSlotOutput(this, "output", 0, 1);
	}

	@Override
	public String getStartSoundFile() {
		return null;
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
		return "MuffleFurnance";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new MuffleFurnaceGui(new MuffleFurnanceContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new MuffleFurnanceContainer(player, this);
	}

	@Override
	public boolean canOperate() {
		ItemStack crucible = input.getItemStack(IHLMod.crucible);
		if (crucible != null) {
			return true;
		}
		ItemStack mold = input.getItemStack(IHLUtils.getThisModItem("injectionMold"));
		if (mold != null) {
			return true;
		}
		return getOutput() != null;
	}

	@Override
	public void operate() {
		ItemStack crucible = input.getItemStack(IHLMod.crucible);
		if (crucible != null) {
			((Crucible) crucible.getItem()).processContent(crucible, this);
			return;
		}
		ItemStack mold = input.getItemStack(IHLUtils.getThisModItem("injectionMold"));
		if (mold != null) {
			mold.stackTagCompound.setBoolean("isContainStearin", false);
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

	public static void addRecipe(IRecipeInput recipeInputOreDict, ItemStack output) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new IRecipeInput[] { recipeInputOreDict })),
				new UniversalRecipeOutput(null, (new ItemStack[] { output }), 20));
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

	@Override
	public UniversalRecipeOutput getOutput() {
		return MuffleFurnanceTileEntity.recipeManager.getOutputFor(null, this.input.getItemStackList());
	}

	public static void addRecipe(UniversalRecipeInput universalRecipeInput,
			UniversalRecipeOutput universalRecipeOutput) {
		recipeManager.addRecipe(universalRecipeInput, universalRecipeOutput);
	}

	public static void addRecipe(IRecipeInput recipeInputOreDict, ItemStack output, float f) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new IRecipeInput[] { recipeInputOreDict })), new UniversalRecipeOutput(
				null, (new RecipeOutputItemStack[] { new RecipeOutputItemStack(output, f) }), 20));
	}
}
