package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.metallurgy.WoodenRollingMachineGui;
import ihl.processing.metallurgy.WoodenRollingMachinePart1TileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class WoodenRollingMachineRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return WoodenRollingMachineGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{123-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{15-11,34-11,53-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{54-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{25-11};
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.woodenRollingMachine";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIWoodenRollingMachine.png";
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74-5,16-11, 46, 48), this.getRecipeId(), new Object[0]));
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "woodenRollingMachine";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return WoodenRollingMachinePart1TileEntity.getRecipes();
    }
}
