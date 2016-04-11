package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import codechicken.nei.PositionedStack;
import ihl.processing.metallurgy.AchesonFurnaceGui;
import ihl.processing.metallurgy.AchesonFurnanceTileEntity;
import ihl.recipes.RecipeInputOreDictionaryList;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.inventory.GuiContainer;


public class AchesonFurnaceRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return AchesonFurnaceGui.class;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{34-5,34-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{41-11,23-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{107-5,107-5};
    }
    
    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{41-11,23-11};
    }

    @Override
	public String getRecipeName()
    {
        return "Acheson Furnace";
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.achesonFurnace";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUIAchesonFurnaceNEI.png";
    }

    @Override
    public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(40-5, 55-11, 90, 30), this.getRecipeId(), new Object[0]));
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "achesonFurnace";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return AchesonFurnanceTileEntity.getRecipes();
    }
    
    @Override
    public List<PositionedStack> getAdditionalIngredients()
    {
    	List<PositionedStack> ps = new ArrayList();
    	RecipeInputOreDictionaryList ari = new RecipeInputOreDictionaryList(new String[] {"stickCoal","stickGraphite"});
    	ps.add(new PositionedStack(ari.getInputs(),16-5,41-11));
    	ps.add(new PositionedStack(ari.getInputs(),52-5,41-11));
    	ps.add(new PositionedStack(IHLUtils.getOreDictItemStack("stickGraphite"),89-5,41-11));
    	ps.add(new PositionedStack(IHLUtils.getOreDictItemStack("stickGraphite"),125-5,41-11));
        return ps;
    }
    
}
