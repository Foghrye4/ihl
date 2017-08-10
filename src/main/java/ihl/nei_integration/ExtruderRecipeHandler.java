package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.metallurgy.ExtruderGui;
import ihl.processing.metallurgy.ExtruderTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ExtruderRecipeHandler extends MachineRecipeHandler {
	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return ExtruderGui.class;
	}

	@Override
	protected int[] getInputPosX() {
		return new int[] { 39 - 5, 57 - 5 };
	}

	@Override
	protected int[] getInputPosY() {
		return new int[] { 32 - 11 };
	}

	@Override
	protected int[] getOutputPosX() {
		return new int[] { 92 - 5 };
	}

	@Override
	protected int[] getOutputPosY() {
		return new int[] { 32 - 11 };
	}

	@Override
	public String getRecipeId() {
		return "ihl.extruder";
	}

	@Override
	public String getGuiTexture() {
		return "ihl:textures/gui/GUIExtruder.png";
	}

	@Override
	public String getOverlayIdentifier() {
		return "extruder";
	}

	@Override
	public void loadTransferRects() {
		this.transferRects
				.add(new RecipeTransferRect(new Rectangle(74 - 5, 31 - 10, 18, 18), this.getRecipeId(), new Object[0]));
	}

	@Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList() {
		return ExtruderTileEntity.getRecipes();
	}
}
