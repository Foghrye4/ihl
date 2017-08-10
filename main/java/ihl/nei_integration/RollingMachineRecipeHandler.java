package ihl.nei_integration;

import java.util.Map;

import ihl.processing.metallurgy.RollingMachineGui;
import ihl.processing.metallurgy.RollingMachinePart1TileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class RollingMachineRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return RollingMachineGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{54-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{25-11,34-11,53-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{105-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{25-11};
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.rollingMachine";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIRollingMachine.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "rollingMachine";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return RollingMachinePart1TileEntity.getRecipes();
    }
}
