package ihl.nei_integration;

import java.util.Map;

import ihl.processing.metallurgy.VulcanizationExtrudingMoldTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class VulcanizationExtrudingMoldRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return null;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{41-5,16-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{17-11,46-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{123-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{46-11};
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.vulcanizationExtrudingMold";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIVulcanizationExtrudingMoldNEI.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "vulcanizationExtrudingMold";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return VulcanizationExtrudingMoldTileEntity.getRecipes();
    }
}
