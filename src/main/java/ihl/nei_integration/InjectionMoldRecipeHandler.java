package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.metallurgy.InjectionMoldTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;

public class InjectionMoldRecipeHandler extends MachineRecipeHandler {
	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return null;
	}

	@Override
	protected int[] getInputPosX() {
		return new int[] { 65 - 5 };
	}

	@Override
	protected int[] getInputPosY() {
		return new int[] { 15 - 11, 51 - 11 };
	}

	@Override
	protected int[] getOutputPosX() {
		return new int[] { 101 - 5 };
	}

	@Override
	protected int[] getOutputPosY() {
		return new int[] { 51 - 11 };
	}

	@Override
	public String getRecipeId() {
		return "ihl.casting";
	}

	@Override
	public String getGuiTexture() {
		return "ihl:textures/gui/GUICastingNEI.png";
	}

	@Override
	public String getOverlayIdentifier() {
		return "casting";
	}

	@Override
	public void loadTransferRects() {
		this.transferRects
				.add(new RecipeTransferRect(new Rectangle(64 - 5, 32 - 10, 18, 18), this.getRecipeId(), new Object[0]));
		this.transferRects
				.add(new RecipeTransferRect(new Rectangle(82 - 5, 50 - 10, 18, 18), this.getRecipeId(), new Object[0]));
	}

	@Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList() {
		return InjectionMoldTileEntity.getRecipes();
	}
}
