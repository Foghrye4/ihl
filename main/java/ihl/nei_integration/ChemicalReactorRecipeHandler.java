package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import ihl.processing.chemistry.ChemicalReactorGui;
import ihl.processing.chemistry.ChemicalReactorTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;


public class ChemicalReactorRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return ChemicalReactorGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{104-5,122-5};
    }
    
    @Override
    protected int[] getInputPosY()
    {
        return new int[]{15-11};
    }
    
    @Override
    protected int[] getFluidInputPosX()
    {
        return new int[]{60-5,42-5,24-5};
    }

    @Override
    protected int[] getFluidInputPosY()
    {
        return new int[]{15-11};
    }
    
    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{104-5,122-5};
    }

    @Override
    protected int[] getFluidOutputPosX()
    {
        return new int[]{42-5,60-5};
    }
    
    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{51-11};
    }

    @Override
    protected int[] getFluidOutputPosY()
    {
        return new int[]{51-11};
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.chemicalReactor";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIChemicalReactor.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "chemicalReactor";
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(103-5,32-10, 36, 18), this.getRecipeId(), new Object[0]));
    }
    
    @Override
    public void drawExtras(int recipeNumber)
    {
    	super.drawExtras(recipeNumber);
    	MachineRecipeHandler.CachedIORecipe recipe = (CachedIORecipe) this.arecipes.get(recipeNumber);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.drawTexturedModalRect(103-18-5, 52-11, 246, 226+6*(this.ticks%4),10,6);
        if(recipe.specialConditions)
        {
            GuiDraw.drawTexturedModalRect(0, 32-11, 0, 166,134,18);
        	GuiDraw.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("ihl.use_with_cryogenic_distiller"), 0, 27, 16777215);
        }
    }
    
    @Override
    public void drawBackground(int i)
    {
    	super.drawBackground(i);
        GuiDraw.drawTexturedModalRect(23-5, 14-11, 59, 14, 18, 18);
        GuiDraw.drawTexturedModalRect(41-5, 14-11, 59, 14, 18, 18);
        GuiDraw.drawTexturedModalRect(41-5, 50-11, 59, 50, 18, 18);
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return ChemicalReactorTileEntity.getRecipes();
    }
}
