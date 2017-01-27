package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import ic2.core.item.ItemFluidCell;
import ihl.processing.chemistry.CryogenicDistillerGui;
import ihl.processing.chemistry.CryogenicDistillerTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;


public class CryogenicDistillerRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return CryogenicDistillerGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{103-5};
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(102-5, 32-10, 18, 18), this.getRecipeId(), new Object[0]));
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{51-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{123-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{33-11,51-11};
    }


    @Override
	public String getRecipeName()
    {
        return "Cryogenic distiller";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.cryogenicDistiller";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUICryogenicDistiller.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "cryogenicDistiller";
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void drawExtras(int recipeNumber)
    {
    	MachineRecipeHandler.CachedIORecipe recipe = (MachineRecipeHandler.CachedIORecipe) this.arecipes.get(recipeNumber);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(recipe.specialConditions)
        {
            GuiDraw.drawTexturedModalRect(0, 0, 0, 166, 97, 69);
            String note = StatCollector.translateToLocal("ihl.as_byproduct_of_processing_of");
           	FluidStack fstack = ((ItemFluidCell)recipe.getIngredients().get(0).item.getItem()).getFluid(recipe.getIngredients().get(0).item);
           	note+=" "+ fstack.getLocalizedName();
           	note+=" "+ StatCollector.translateToLocal("ihl.in_chemical_reactor");
        	Iterator<String> noteListIterator = GuiDraw.fontRenderer.listFormattedStringToWidth(note, 90).iterator();
        	int yTextPos=0;
        	while(noteListIterator.hasNext())
        	{
            	GuiDraw.fontRenderer.drawStringWithShadow(noteListIterator.next(), 0, yTextPos, 16777215);
            	yTextPos+=10;
        	}
        	GuiDraw.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("ihl.check_usage"), 0, 44, 16777215);
        }
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return CryogenicDistillerTileEntity.getRecipes();
    }
}
