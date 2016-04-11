package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import codechicken.lib.gui.GuiDraw;
import ihl.processing.metallurgy.MuffleFurnaceGui;
import ihl.processing.metallurgy.MuffleFurnanceTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class MuffleFurnaceRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return MuffleFurnaceGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{51-5,69-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{34-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{105-5,123-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{34-11};
    }

    @Override
	public String getRecipeName()
    {
        return "Muffle furnace";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.muffleFurnace";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIMuffleFurnace.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "muffleFurnace";
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(40-5, 55-11, 55, 15), this.getRecipeId(), new Object[0]));
    }

    
    @Override
    public void drawBackground(int i)
    {
    	super.drawBackground(i);
        GuiDraw.drawTexturedModalRect(86-5, 33-11, 176, 14, 54, 18);
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return MuffleFurnanceTileEntity.getRecipes();
    }
}
