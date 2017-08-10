package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import ihl.processing.chemistry.LabElectrolyzerGui;
import ihl.processing.chemistry.LabElectrolyzerTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;

public class LabElectrolyzerRecipeHandler extends MachineRecipeHandler {
	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return LabElectrolyzerGui.class;
	}

	@Override
	protected int[] getInputPosX() {
		return new int[] { 63 - 5, 46 - 5, 29 - 5 };
	}

	@Override
	protected int[] getInputPosY() {
		return new int[] { 11 - 10 };
	}

	@Override
	protected int[] getOutputPosX() {
		return new int[] { 29 - 5, 63 - 5, 109 - 5 };
	}

	@Override
	protected int[] getOutputPosY() {
		return new int[] { 47 - 10 };
	}

	@Override
	public String getRecipeId() {
		return "ihl.labElectrolyzer";
	}

	@Override
	public String getGuiTexture() {
		return "ihl:textures/gui/GUILabElectrolyzer.png";
	}

	@Override
	public void loadTransferRects() {
		this.transferRects
				.add(new RecipeTransferRect(new Rectangle(28 - 5, 28 - 10, 18, 18), this.getRecipeId(), new Object[0]));
		this.transferRects
				.add(new RecipeTransferRect(new Rectangle(108 - 5, 32 - 10, 18, 18), this.getRecipeId(), new Object[0]));
	}

	@Override
	public String getOverlayIdentifier() {
		return "labElectrolyzer";
	}
	
    @Override
	public void drawBackground(int recipeNumber)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 10, 140, 54);
        GuiDraw.drawTexturedModalRect(46-5, 10-10, 62, 10, 18, 18);
    }

	@Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList() {
		return LabElectrolyzerTileEntity.getRecipes();
	}
}
