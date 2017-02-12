package ihl.processing.metallurgy;

import java.util.List;
import java.util.Map;

import ic2.core.ContainerBase;
import ihl.IHLMod;
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

public class MuffleFurnanceTileEntity extends MachineBaseTileEntity {

	protected static UniversalRecipeManager recipeManager = new UniversalRecipeManager("mufflefurnace");

	public MuffleFurnanceTileEntity() {
		super(2);
		this.input.setStackSizeLimit(32);
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
		if (this.getOutput() != null) {
			UniversalRecipeOutput routput = recipeManager
					.getOutputFor(null, this.input.getItemStackList(), true, true);
			List<RecipeOutputItemStack> output = routput.getItemOutputs();
			for (int i = 0; i < this.input.size(); i++) {
				if (i < output.size() && output.get(i) != null) {
					ItemStack outStack = output.get(i).itemStack.copy();
					outStack.stackSize = Math.round(output.get(i).quantity);
					if (this.input.get(i) != null) {
						outStack.stackTagCompound = this.input.get(i).stackTagCompound;
					}
					this.input.put(i, outStack);
				}
				if (this.input.get(i) != null && this.input.get(i).stackSize <= 0)
					this.input.put(i, null);
			}
		}
	}

	public static void addRecipe(ItemStack input1, ItemStack output) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new ItemStack[] { input1 })),
				new UniversalRecipeOutput(null, (new ItemStack[] { output }), 20));
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

	@Override
	public UniversalRecipeOutput getOutput() {
		return MuffleFurnanceTileEntity.recipeManager.getOutputFor(null, this.input.getItemStackList(), false, false);
	}

	public static void addRecipe(UniversalRecipeInput universalRecipeInput,
			UniversalRecipeOutput universalRecipeOutput) {
		recipeManager.addRecipe(universalRecipeInput, universalRecipeOutput);
	}

	public static void addRecipe(ItemStack input1, ItemStack output, float f) {
		recipeManager.addRecipe(new UniversalRecipeInput(null, (new ItemStack[] { input1 })), new UniversalRecipeOutput(
				null, (new RecipeOutputItemStack[] { new RecipeOutputItemStack(output, f) }), 20));
	}
}
