package ihl.nei_integration;

import java.util.Map;

import ihl.processing.chemistry.PaperMachineGui;
import ihl.processing.chemistry.PaperMachineTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class PaperMachineRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return PaperMachineGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{78-5,78-5-18,122-5-18,122-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{15-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{122-5,122-5-18,78-5-18};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{51-11,51-11,51-11};
    }


    @Override
	public String getRecipeName()
    {
        return "Paper machine";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.paperMachine";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIPaperMachine.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "paperMachine";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return PaperMachineTileEntity.getRecipes();
    }
}
