package ihl.nei_integration;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;

public abstract class IHLRecipeHandler extends TemplateRecipeHandler
{
    protected int ticks;

    @Override
	public abstract String getRecipeName();

    public abstract String getRecipeId();

    @Override
	public abstract String getGuiTexture();

    @Override
	public abstract String getOverlayIdentifier();

    public abstract Map<IRecipeInput, RecipeOutput> getRecipeList();

    @Override
	public void drawBackground(int i)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 140, 65);
    }

    @Override
	public void drawExtras(int i)
    {
        float f = this.ticks >= 20 ? (this.ticks - 20) % 20 / 20.0F : 0.0F;
        this.drawProgressBar(74, 23, 176, 14, 25, 16, f, 0);
        f = this.ticks <= 20 ? this.ticks / 20.0F : 1.0F;
        this.drawProgressBar(51, 25, 176, 0, 14, 14, f, 3);
    }

    @Override
	public void onUpdate()
    {
        super.onUpdate();
        ++this.ticks;
    }

    @Override
	public void loadTransferRects()
    {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 25, 16), this.getRecipeId(), new Object[0]));
    }

    @Override
	public void loadCraftingRecipes(String outputId, Object ... results)
    {
        if (outputId.equals(this.getRecipeId()))
        {
            Iterator<Entry<IRecipeInput, RecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();

            while (i$.hasNext())
            {
                Entry<IRecipeInput, RecipeOutput> entry = i$.next();
                this.arecipes.add(new IHLRecipeHandler.CachedIORecipe((IRecipeInput)entry.getKey(), (RecipeOutput)entry.getValue()));
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
        Iterator<Entry<IRecipeInput, RecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();

        while (i$.hasNext())
        {
            Entry<IRecipeInput, RecipeOutput> entry = i$.next();
            Iterator<ItemStack> i$1 = ((RecipeOutput)entry.getValue()).items.iterator();

            while (i$1.hasNext())
            {
                ItemStack output = i$1.next();

                if (NEIServerUtils.areStacksSameTypeCrafting(output, result))
                {
                    this.arecipes.add(new IHLRecipeHandler.CachedIORecipe((IRecipeInput)entry.getKey(), (RecipeOutput)entry.getValue()));
                    break;
                }
            }
        }
    }

    @Override
	public void loadUsageRecipes(ItemStack ingredient)
    {
        Iterator<Entry<IRecipeInput, RecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();

        while (i$.hasNext())
        {
            Entry<IRecipeInput, RecipeOutput> entry = i$.next();

            if (((IRecipeInput)entry.getKey()).matches(ingredient))
            {
                this.arecipes.add(new IHLRecipeHandler.CachedIORecipe((IRecipeInput)entry.getKey(), (RecipeOutput)entry.getValue()));
            }
        }
    }

    protected int getInputPosX()
    {
        return 51;
    }

    protected int getInputPosY()
    {
        return 6;
    }

    protected int getOutputPosX()
    {
        return 111;
    }

    protected int getOutputPosY()
    {
        return 24;
    }

    protected boolean isOutputsVertical()
    {
        return true;
    }

    public class CachedIORecipe extends CachedRecipe
    {
        private final List<PositionedStack> ingredients = new ArrayList<PositionedStack>();
        private final PositionedStack output;
        private final List<PositionedStack> otherStacks = new ArrayList<PositionedStack>();

        @Override
		public List<PositionedStack> getIngredients()
        {
            return this.getCycledIngredients(IHLRecipeHandler.this.cycleticks / 20, this.ingredients);
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

        public CachedIORecipe(ItemStack input, ItemStack output1)
        {
            super();

            if (input == null)
            {
                throw new NullPointerException("Input must not be null (recipe " + input + " -> " + output1 + ").");
            }
            else if (output1 == null)
            {
                throw new NullPointerException("Output must not be null (recipe " + input + " -> " + output1 + ").");
            }
            else
            {
                this.ingredients.add(new PositionedStack(input, IHLRecipeHandler.this.getInputPosX(), IHLRecipeHandler.this.getInputPosY()));
                this.output = new PositionedStack(output1, IHLRecipeHandler.this.getOutputPosX(), IHLRecipeHandler.this.getOutputPosY());
            }
        }

        public CachedIORecipe(IRecipeInput input, RecipeOutput output1)
        {
            super();

            if (input == null)
            {
                throw new NullPointerException("Input must not be null (recipe " + input + " -> " + output1 + ").");
            }
            else if (output1 == null)
            {
                throw new NullPointerException("Output must not be null (recipe " + input + " -> " + output1 + ").");
            }
            else if (output1.items.isEmpty())
            {
                throw new IllegalArgumentException("Output must not be empty (recipe " + input + " -> " + output1 + ").");
            }
            else if (output1.items.contains((Object)null))
            {
                throw new IllegalArgumentException("Output must not contain null (recipe " + input + " -> " + output1 + ").");
            }
            else
            {
                ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                Iterator<ItemStack> i = input.getInputs().iterator();

                while (i.hasNext())
                {
                    ItemStack item = i.next();
                    items.add(StackUtil.copyWithSize(item, input.getAmount()));
                }

                this.ingredients.add(new PositionedStack(items, IHLRecipeHandler.this.getInputPosX(), IHLRecipeHandler.this.getInputPosY()));
                this.output = new PositionedStack(output1.items.get(0), IHLRecipeHandler.this.getOutputPosX(), IHLRecipeHandler.this.getOutputPosY());

                for (int var7 = 1; var7 < output1.items.size(); ++var7)
                {
                    if (IHLRecipeHandler.this.isOutputsVertical())
                    {
                        this.otherStacks.add(new PositionedStack(output1.items.get(var7), IHLRecipeHandler.this.getOutputPosX(), IHLRecipeHandler.this.getOutputPosY() + var7 * 18));
                    }
                    else
                    {
                        this.otherStacks.add(new PositionedStack(output1.items.get(var7), IHLRecipeHandler.this.getOutputPosX() + var7 * 18, IHLRecipeHandler.this.getOutputPosY()));
                    }
                }
            }
        }
    }
}
