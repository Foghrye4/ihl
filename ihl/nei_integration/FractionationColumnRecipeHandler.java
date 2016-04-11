package ihl.nei_integration;

import java.util.Map;

import ihl.processing.chemistry.FractionatorBottomTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class FractionationColumnRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return null;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{6-5,61-5,106-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{33-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{76-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{51-11,31-11};
    }


    @Override
	public String getRecipeName()
    {
        return "Fractionation column";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.fractionator";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIFractionationColumn.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "fractionator";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return FractionatorBottomTileEntity.getRecipes();
    }
}
