package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.chemistry.PrecipitatorCondenserTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;

public class PrecipitatorCondenserRecipeHandler extends MachineRecipeHandler {
	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return null;
	}

	@Override
	protected int[] getInputPosX() {
		return new int[] { 6 - 5, 6 - 5, 106 - 5 };
	}

	@Override
	protected int[] getInputPosY() {
		return new int[] { 33 - 11, 53 - 11 };
	}

	@Override
	protected int[] getOutputPosX() {
		return new int[] { 75 - 5 };
	}

	@Override
	protected int[] getOutputPosY() {
		return new int[] { 53 - 11, 31 - 11 };
	}

	@Override
	public String getRecipeName() {
		return "Precipitator/Condenser";
	}

	@Override
	public String getRecipeId() {
		return "ihl.precipitatorCondenser";
	}

	@Override
	public String getGuiTexture() {
		return "ihl:textures/gui/GUICondenserNEI.png";
	}

	@Override
	public String getOverlayIdentifier() {
		return "condenser";
	}

	@Override
	public void loadTransferRects() {
		this.transferRects
				.add(new RecipeTransferRect(new Rectangle(23 - 5, 10 - 10, 50, 58), this.getRecipeId(), new Object[0]));
	}

	@Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList() {
		return PrecipitatorCondenserTileEntity.getRecipes();
	}
}
