package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;

import ihl.processing.metallurgy.WireMillGui;
import ihl.processing.metallurgy.WireMillTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class WireMillRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return WireMillGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{117-5,44-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{31-11,14-11};
    }
    
    @Override
    protected int[] getFluidInputPosX()
    {
        return new int[]{8-5,26-5,44-5};
    }

    @Override
    protected int[] getFluidInputPosY()
    {
        return new int[]{14-11};
    }
    

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{152-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{31-11};
    }


    @Override
	public String getRecipeName()
    {
        return "Wire mill";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.wireMill";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIWireMill.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "wireMill";
    }
    
    @Override
	public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(134-5, 33-11, 18, 13), this.getRecipeId(), new Object[0]));
    }
    
    @Override
    public void drawBackground(int i)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 151, 65);
        GuiDraw.drawTexturedModalRect(133-5+18, 30-11, 98+18, 30, 18, 18);
        GuiDraw.drawTexturedModalRect(7-5+18, 13-11, 25+18, 13, 18, 18);
        GuiDraw.drawTexturedModalRect(7-5, 13-11, 25+18, 13, 18, 18);
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return WireMillTileEntity.getRecipes();
    }
}
