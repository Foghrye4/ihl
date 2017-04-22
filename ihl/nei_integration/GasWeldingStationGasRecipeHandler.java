package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.metallurgy.GasWeldingStationGui;
import ihl.processing.metallurgy.GasWeldingStationTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;


public class GasWeldingStationGasRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return GasWeldingStationGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{44-5,62-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{15-11,51-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{62-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{15-11,33-11};
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(80-5, 15-11, 50, 50), this.getRecipeId(), new Object[0]));
    }
    
    @Override
	public String getRecipeId()
    {
        return "ihl.gasWeldingStationGas";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIGasWeldingStation.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "gasWeldingStationGas";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return GasWeldingStationTileEntity.getGasRecipes();
    }
}
