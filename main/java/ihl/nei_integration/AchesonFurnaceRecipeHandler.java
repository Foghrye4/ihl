package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.metallurgy.AchesonFurnaceGui;
import ihl.processing.metallurgy.AchesonFurnanceTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class AchesonFurnaceRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return AchesonFurnaceGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{40-5,40-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{40-11,22-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{98-5};
    }
    
    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{32-11};
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.achesonFurnace";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIAchesonFurnace.png";
    }

    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(57-5, 29-11, 40, 30), this.getRecipeId(), new Object[0]));
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "achesonFurnace";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return AchesonFurnanceTileEntity.getRecipes();
    }
}
