package ihl.nei_integration;

import java.util.Map;

import ihl.processing.chemistry.GoldChimneyKneeTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class ChimneyKneeRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return null;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{6-5,6-5,106-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{33-11,53-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{96-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{35-11,31-11};
    }


    @Override
	public String getRecipeName()
    {
        return "Chimney knee";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.chimneyKnee";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIChimneyKneeNEI.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "chimneyKnee";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return GoldChimneyKneeTileEntity.getRecipes();
    }
}
