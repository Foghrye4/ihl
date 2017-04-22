package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import ihl.processing.chemistry.ElectricEvaporatorGui;
import ihl.processing.chemistry.EvaporatorTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;


public class ElectricEvaporatorRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return ElectricEvaporatorGui.class;
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
	public String getRecipeId()
    {
        return "ihl.electricEvaporator";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIElectricEvaporator.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "electricEvaporator";
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
