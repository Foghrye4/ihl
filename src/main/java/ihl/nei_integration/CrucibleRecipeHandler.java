package ihl.nei_integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import codechicken.nei.PositionedStack;
import ihl.processing.metallurgy.Crucible;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.inventory.GuiContainer;


public class CrucibleRecipeHandler extends MachineRecipeHandler
{
    @Override
	public Class <? extends GuiContainer > getGuiClass()
    {
        return null;
    }
    
    @Override
    protected int[] getInputPosX()
    {
        return new int[]{11-5};
    }

    @Override
    protected int[] getInputPosY()
    {
        return new int[]{15-11};
    }

    @Override
    protected int[] getOutputPosX()
    {
        return new int[]{106-5};
    }

    @Override
    protected int[] getOutputPosY()
    {
        return new int[]{51-11};
    }

    @Override
	public String getRecipeId()
    {
        return "ihl.crucible";
    }

    @Override
	public String getGuiTexture()
    {
        return "ihl:textures/gui/GUICrucibleNEI.png";
    }

    @Override
	public String getOverlayIdentifier()
    {
        return "crucible";
    }

    @Override
	public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList()
    {
        return Crucible.getRecipes();
    }
    
    @Override
    public List<PositionedStack> getAdditionalIngredients()
    {
    	List<PositionedStack> ps = new ArrayList<PositionedStack>();
    	ps.add(new PositionedStack(IHLUtils.getThisModItemStackWithDamage("crucible", 0),61-5,15-11));
    	ps.add(new PositionedStack(IHLUtils.getThisModItemStackWithDamage("crucible", 1),106-5,15-11));
        return ps;
    }
}
