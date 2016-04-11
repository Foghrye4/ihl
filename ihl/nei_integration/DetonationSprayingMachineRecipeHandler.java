package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.Map;

import codechicken.lib.gui.GuiDraw;
import ihl.processing.metallurgy.DetonationSprayingMachineGui;
import ihl.processing.metallurgy.DetonationSprayingMachineTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;


public class DetonationSprayingMachineRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return DetonationSprayingMachineGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{10-5,98-5,117-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{17-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{10-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{53-11};
    }

    @Override
	public String getRecipeName()
    {
        return "Detonation Spraying Machine";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.detonationSprayingMachine";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIDetonationSprayingMachine.png";
    }
    
    @Override
    public void drawBackground(int i)
    {
    	super.drawBackground(i);
        GuiDraw.drawTexturedModalRect(9-5, 34-11, 176, 0, 18, 36);
    }
    
    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(30-5,0, 60, 30), this.getRecipeId(), new Object[0]));
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "detonationSprayingMachine";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return DetonationSprayingMachineTileEntity.getRecipes();
    }
}
