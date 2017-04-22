package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.chemistry.LoomGui;
import ihl.processing.chemistry.LoomTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class LoomRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return LoomGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{8-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{44-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{127-5};
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(35-10, 0, 90, 60), this.getRecipeId(), new Object[0]));
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{44-11};
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.loom";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUILoom.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "loom";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return LoomTileEntity.getRecipes();
    }
}
