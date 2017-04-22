package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import codechicken.lib.gui.GuiDraw;
import ihl.processing.chemistry.FluidizedBedReactorGui;
import ihl.processing.chemistry.FluidizedBedReactorTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;


public class FluidizedBedReactorRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return FluidizedBedReactorGui.class;
    }

    @Override
    protected int[] getInputPosX()
    {
        return new int[]{41-5};
    }
    
    @Override
    protected int[] getInputPosY()
    {
        return new int[]{23-11,41-11};
    }
    
    @Override
    protected int[] getFluidInputPosX()
    {
        return new int[]{102-5,84-5};
    }

    @Override
    protected int[] getFluidInputPosY()
    {
        return new int[]{15-11};
    }
    
    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{76-5};
    }

    @Override
    protected int[] getFluidOutputPosX()
    {
        return new int[]{102-5,84-5};
    }
    
    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{33-11};
    }

    @Override
    protected int[] getFluidOutputPosY()
    {
        return new int[]{51-11,51-11};
    }
    @Override
    public void drawBackground(int i)
    {
    	super.drawBackground(i);
        GuiDraw.drawTexturedModalRect(83-5, 14-11, 101, 14, 18, 18);
        GuiDraw.drawTexturedModalRect(83-5, 50-11, 101, 14, 18, 18);
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.fluidizedBedReactor";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIFluidizedBedReactor.png";
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(58-5,34-10, 17, 13), this.getRecipeId(), new Object[0]));
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "fluidizedBedReactor";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return FluidizedBedReactorTileEntity.getRecipes();
    }
}
