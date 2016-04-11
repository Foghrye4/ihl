package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.chemistry.EvaporatorGui;
import ihl.processing.chemistry.EvaporatorTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class EvaporatorRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return EvaporatorGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{39};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{3};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{112};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{21};
    }

    @Override
	public String getRecipeName()
    {
        return "Evaporator";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.evaporator";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUISolidFuelEvaporator.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "evaporator";
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(99-5,34-10, 17, 13), this.getRecipeId(), new Object[0]));
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return EvaporatorTileEntity.getRecipes();
    }
}
