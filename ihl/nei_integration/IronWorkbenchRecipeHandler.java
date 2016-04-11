package ihl.nei_integration;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.IRecipeInput;
import ic2.core.Ic2Items;
import ihl.IHLMod;
import ihl.recipes.IronWorkbenchRecipe;
import ihl.utils.IHLUtils;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import ihl.flexible_cable.IronWorkbenchGui;
import ihl.flexible_cable.IronWorkbenchTileEntity;
import ihl.interfaces.IWire;

public class IronWorkbenchRecipeHandler extends TemplateRecipeHandler
{
		private static final int workspaceItemsPosX=8-7;
		private static final int workspaceItemsPosY=8-7;
		private static final int toolsPosX=26-7;
		private static final int toolsPosY=8-7;
		private static final int materialsPosX=65-7;
		private static final int materialsPosY=8-7;
		private static final int outputPosX=116-7;
		private static final int outputPosY=8-7;
	
	   @Override
	public Class <? extends GuiContainer > getGuiClass()
	    {
	        return IronWorkbenchGui.class;
	    }

	   	@Override
		public String getRecipeName()
	    {
	        return "Iron workbench";
	    }
	   	
		@Override
		public int recipiesPerPage() 
		{
			return 1;
		}

	    public String getRecipeId()
	    {
	        return "ihl.ironWorkbench";
	    }

	    @Override
		public String getGuiTexture()
	    {
	        return "ihl:textures/gui/GUIIronWorkbench.png";
	    }

	    @Override
		public String getOverlayIdentifier()
	    {
	        return "ironWorkbench";
	    }

	    public List<IronWorkbenchRecipe> getRecipeList()
	    {
	    	return IronWorkbenchTileEntity.recipes;
	    }

  
    @Override
	public void drawBackground(int recipeNumber)
    {
    	IronWorkbenchRecipeHandler.CachedIORecipe recipe = (CachedIORecipe) this.arecipes.get(recipeNumber);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 7, 7, 162, 108);
    }

    @Override
	public void drawExtras(int i)
    {
    	/*
        float f = this.ticks >= 20 ? (float)((this.ticks - 20) % 20) / 20.0F : 0.0F;
        this.drawProgressBar(74, 23, 176, 14, 25, 16, f, 0);
        f = this.ticks <= 20 ? (float)this.ticks / 20.0F : 1.0F;
        this.drawProgressBar(51, 25, 176, 0, 14, 14, f, 3);
        */
    }

    @Override
	public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(104-10, 9-10, 15, 108), this.getRecipeId(), new Object[0]));
    }

    @Override
	public void loadCraftingRecipes(String outputId, Object ... results)
    {
        if (outputId.equals(this.getRecipeId()))
        {
            Iterator<IronWorkbenchRecipe> i$ = this.getRecipeList().iterator();

            while (i$.hasNext())
            {
            	IronWorkbenchRecipe entry = i$.next();
                this.arecipes.add(new IronWorkbenchRecipeHandler.CachedIORecipe(entry));
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
	public void loadCraftingRecipes(ItemStack result)
    {
        Iterator<IronWorkbenchRecipe> i$ = this.getRecipeList().iterator();

        while (i$.hasNext())
        {
        	IronWorkbenchRecipe entry = i$.next();
            Iterator i$1 = entry.outputs.iterator();
            while (i$1.hasNext())
            {
                ItemStack output = (ItemStack)i$1.next();
                if (NEIServerUtils.areStacksSameTypeCrafting(output, result))
                {
                    this.arecipes.add(new IronWorkbenchRecipeHandler.CachedIORecipe(entry));
                    break;
                }
            }
        }
    }
    
    @Override
	public void loadUsageRecipes(ItemStack ingredient)
    {
        Iterator<IronWorkbenchRecipe> i$ = this.getRecipeList().iterator();
        while (i$.hasNext())
        {
        	IronWorkbenchRecipe entry = i$.next();
        	if(entry.workspaceElements!=null && !entry.workspaceElements.isEmpty())
        	{
        		Iterator i$1 = entry.workspaceElements.iterator();
            	while (i$1.hasNext())
            	{
            		ItemStack output = (ItemStack)i$1.next();
                	if (NEIServerUtils.areStacksSameTypeCrafting(output, ingredient) || IHLUtils.isItemsHaveSameOreDictionaryEntry(output, ingredient) || output.getItem()==ingredient.getItem())
                	{
                    	this.arecipes.add(new IronWorkbenchRecipeHandler.CachedIORecipe(entry));
                    	break;
                	}
            	}
        	}
        	
        	if(entry.tools!=null && !entry.tools.isEmpty())
        	{
        		Iterator<IRecipeInput>  i$1 = entry.tools.iterator();
            	while (i$1.hasNext())
            	{
            		IRecipeInput output = i$1.next();
                	if (output.matches(ingredient))
                	{
                    	this.arecipes.add(new IronWorkbenchRecipeHandler.CachedIORecipe(entry));
                    	break;
                	}
            	}
        	}
        	if(entry.materials!=null && !entry.materials.isEmpty())
        	{
            Iterator<IRecipeInput> i$2 = entry.materials.iterator();
            while (i$2.hasNext())
            {
            		IRecipeInput output = i$2.next();
                	if (output.matches(ingredient))
                	{
                    	this.arecipes.add(new IronWorkbenchRecipeHandler.CachedIORecipe(entry));
                    	break;
                	}
            	}
        	}
        }
    }

    public class CachedIORecipe extends CachedRecipe
    {
        private final List<PositionedStack> ingredients = new ArrayList();
        private final PositionedStack output;
        private final List<PositionedStack> otherStacks = new ArrayList();

        @Override
		public List<PositionedStack> getIngredients()
        {
        	return this.getCycledIngredients(IronWorkbenchRecipeHandler.this.cycleticks / 20, this.ingredients);
        }

        @Override
		public PositionedStack getResult()
        {
            return this.output;
        }

        @Override
		public List<PositionedStack> getOtherStacks()
        {
            return this.otherStacks;
        }

        public CachedIORecipe(IronWorkbenchRecipe recipe)
        {
            super();
            if (recipe == null)
            {
                throw new NullPointerException("Recipe must not be null.");
            }
            else
            {
            	this.ingredients.add(new PositionedStack(IHLUtils.getThisModItemStack("ironWorkbench"), workspaceItemsPosX, workspaceItemsPosY));
                if(recipe.workspaceElements!=null && !recipe.workspaceElements.isEmpty())
                {
                    Iterator i = recipe.workspaceElements.iterator();
                    int index = 1;
                    while (i.hasNext())
                    {
                    	int x = workspaceItemsPosX;
                    	int y = workspaceItemsPosY + index * 18;
                        ItemStack stack = (ItemStack)i.next();
                        this.ingredients.add(new PositionedStack(stack, x, y));
                    	index++;
                    }
                }
                if(recipe.tools!=null && !recipe.tools.isEmpty())
                {
                    Iterator i = recipe.tools.iterator();
                    int index = 0;
                    while (i.hasNext())
                    {
                    	int x = toolsPosX + (index % 2) * 18;
                    	int y = toolsPosY + index / 2 * 18;
                    	IRecipeInput rInput = (IRecipeInput)i.next();
                        this.ingredients.add(new PositionedStack(rInput.getInputs(), x, y));
                    	index++;
                    }
                }
                
                if(recipe.materials!=null && !recipe.materials.isEmpty())
                {
                    Iterator i = recipe.materials.iterator();
                    int index = 0;
                    while (i.hasNext())
                    {
                    	int x = materialsPosX + (index % 2) * 18;
                    	int y = materialsPosY + index / 2 * 18;
                    	IRecipeInput rInput = (IRecipeInput)i.next();
                        Iterator<ItemStack> rInputsi = rInput.getInputs().iterator();
                        List<ItemStack> itemInputs = new ArrayList();
                        while(rInputsi.hasNext())
                        {
                        	ItemStack stack = rInputsi.next().copy();
                        	if(!(stack.getItem() instanceof IWire))
                        	{
                        		stack.stackSize=rInput.getAmount();
                        	}
                        	itemInputs.add(stack);
                        }
                        this.ingredients.add(new PositionedStack(itemInputs, x, y));
                    	index++;
                    }
                }
                this.output=new PositionedStack(recipe.outputs.get(0), outputPosX, outputPosY);
                Iterator i = recipe.outputs.iterator();
                int index = 0;
                while (i.hasNext())
                {
                  	int x = outputPosX + (index % 3) * 18;
                   	int y = outputPosY + index / 3 * 18;
                    ItemStack stack = (ItemStack)i.next();
                    if(index>0)
                    {
                        this.otherStacks.add(new PositionedStack(stack, x, y));
                    }
                   	index++;
                }
            }
        }

    }
}
