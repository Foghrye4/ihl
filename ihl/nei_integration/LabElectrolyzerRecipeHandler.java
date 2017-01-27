package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;
import ihl.processing.chemistry.LabElectrolyzerGui;
import ihl.processing.chemistry.LabElectrolyzerTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class LabElectrolyzerRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return LabElectrolyzerGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{42-5,60-5,78-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{15-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{8-5,87-5,106-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{15-11,51-11,15-11};
    }


    @Override
	public String getRecipeName()
    {
        return "Lab electrolyzer";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.labElectrolyzer";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUILabElectrolyzer.png";
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(7-5,32-10, 18, 18), this.getRecipeId(), new Object[0]));
        this.transferRects.add(new RecipeTransferRect(new Rectangle(86-5,32-10, 37, 18), this.getRecipeId(), new Object[0]));
    }


    @Override
	public String getOverlayIdentifier()
    {
        return "labElectrolyzer";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return LabElectrolyzerTileEntity.getRecipes();
    }
}
