package ihl.nei_integration;

import java.util.Map;

import ihl.processing.chemistry.ElectrolysisBathGui;
import ihl.processing.chemistry.ElectrolysisBathTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class ElectrolysisBathRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return ElectrolysisBathGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{6-5,98-5,78-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{16-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{6-5,87-5,106-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{52-11,51-11,15-11};
    }


    @Override
	public String getRecipeName()
    {
        return "Electrolysis bath";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.electrolysisBath";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIElectrolysisBath.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "electrolysisBath";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return ElectrolysisBathTileEntity.getRecipes();
    }
}
