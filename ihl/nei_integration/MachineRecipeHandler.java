package ihl.nei_integration;

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
import ic2.core.Ic2Items;
import ic2.core.item.ItemFluidCell;
import ihl.interfaces.IWire;
import ihl.recipes.IRecipeInputFluid;
import ihl.recipes.RecipeOutputItemStack;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public abstract class MachineRecipeHandler extends TemplateRecipeHandler
{
    protected int ticks;

    @Override
	public String getRecipeName()
    {
        return StatCollector.translateToLocal(getRecipeId());
    }

    public abstract String getRecipeId();

    @Override
	public abstract String getGuiTexture();

    @Override
	public abstract String getOverlayIdentifier();

    public abstract Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList();

    @Override
	public void drawBackground(int i)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(this.getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 140, 65);
    }

    @Override
	public void drawExtras(int recipeNumber)
    {
    	MachineRecipeHandler.CachedIORecipe recipe = (CachedIORecipe) this.arecipes.get(recipeNumber);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        for(PositionedStack stack:recipe.ingredients)
        {
        	if(stack.item.stackSize==0)
        	{
            	GuiDraw.fontRenderer.drawStringWithShadow("0.001", stack.relx+3, stack.rely+9, 16777215);
        	}
        }
        if(recipe.output instanceof IHLPositionedStack)
        {
        	IHLPositionedStack rOutput = (IHLPositionedStack) recipe.output;
        	drawFormattedString(rOutput);
        }
        Iterator<PositionedStack> iOs = recipe.otherStacks.iterator();
        while(iOs.hasNext())
        {
        	PositionedStack rOutPS = iOs.next();
            if(rOutPS instanceof IHLPositionedStack)
            {
            	IHLPositionedStack rOutput = (IHLPositionedStack) rOutPS;
            	drawFormattedString(rOutput);
            }
        }
        GuiDraw.changeTexture(this.getGuiTexture());
    }
    
    public void drawFormattedString(IHLPositionedStack rOutput)
    {
    	if(Math.abs(rOutput.sQuantity-Math.round(rOutput.sQuantity))<0.01f)
    	{
    		if(Math.round(rOutput.sQuantity)!=1)
    		{
        		GuiDraw.fontRenderer.drawStringWithShadow(String.format("%d", Math.round(rOutput.sQuantity)), rOutput.relx+11, rOutput.rely+9, 16777215);
    		}
    	}
    	else
    	{
        	GuiDraw.fontRenderer.drawStringWithShadow(String.format("%.1f", rOutput.sQuantity), rOutput.relx+3, rOutput.rely+9, 16777215);
    	}
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
        //this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 25, 16), this.getRecipeId(), new Object[0]));
    }

    @Override
	public void loadCraftingRecipes(String outputId, Object ... results)
    {
        if (outputId.equals(this.getRecipeId()))
        {
            Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();

            while (i$.hasNext())
            {
            	Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                this.arecipes.add(new MachineRecipeHandler.CachedIORecipe(entry.getKey(), entry.getValue(),getAdditionalIngredients()));
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
        Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(result);
        if(fluidStack==null && result.getItem() instanceof IFluidContainerItem)
        {
        	IFluidContainerItem ifc = (IFluidContainerItem)result.getItem();
        	fluidStack = ifc.getFluid(result); 
        }
        else if(result.getItem() instanceof ItemBlock)
        {
        	Block blockfluid = ((ItemBlock)result.getItem()).field_150939_a;
        	if(blockfluid instanceof BlockFluidBase)
        	{
        		Fluid fluid = ((BlockFluidBase)blockfluid).getFluid();
        		if(fluid!=null)
        		{
        			fluidStack = new FluidStack(fluid,1000);
        		}
        	}
        }
       	if(fluidStack!=null)
      	{
            while (i$.hasNext())
            {
            	Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                Iterator<FluidStack> i$1 = ((UniversalRecipeOutput)entry.getValue()).getFluidOutputs().iterator();
                while (i$1.hasNext())
                {
                     FluidStack output = i$1.next();
                     if (output!=null && output.getFluid()==fluidStack.getFluid())
                     {
                         this.arecipes.add(new MachineRecipeHandler.CachedIORecipe((UniversalRecipeInput)entry.getKey(), (UniversalRecipeOutput)entry.getValue(),getAdditionalIngredients()));
                         break;
                     }
                }
            }
       	}
        else
        {
            while (i$.hasNext())
            {
                 Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
                Iterator<RecipeOutputItemStack> i$1 = (entry.getValue()).getItemOutputs().iterator();

                while (i$1.hasNext())
                {
                    RecipeOutputItemStack output = i$1.next();

                    if (NEIServerUtils.areStacksSameTypeCrafting(output.itemStack, result) || IHLUtils.isItemsHaveSameOreDictionaryEntry(output.itemStack, result))
                    {
                        this.arecipes.add(new MachineRecipeHandler.CachedIORecipe(entry.getKey(), entry.getValue(),getAdditionalIngredients()));
                        break;
                    }
                }
            }
        }
    }
    

    public List<PositionedStack> getAdditionalIngredients()
    {
        return null;
    }

    @Override
	public void loadUsageRecipes(ItemStack ingredient)
    {
        Iterator<Entry<UniversalRecipeInput, UniversalRecipeOutput>> i$ = this.getRecipeList().entrySet().iterator();
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(ingredient);
        if(fluidStack==null && ingredient.getItem() instanceof IFluidContainerItem)
        {
        	IFluidContainerItem ifc = (IFluidContainerItem)ingredient.getItem();
        	fluidStack = ifc.getFluid(ingredient); 
        }
        else if(ingredient.getItem() instanceof ItemBlock)
        {
        	Block blockfluid = ((ItemBlock)ingredient.getItem()).field_150939_a;
        	if(blockfluid instanceof BlockFluidBase)
        	{
        		Fluid fluid = ((BlockFluidBase)blockfluid).getFluid();
        		if(fluid!=null)
        		{
        			fluidStack = new FluidStack(fluid,1000);
        		}
        	}
        }
        
    	if(fluidStack!=null && fluidStack.getFluid()!=null)
    	{
        	while (i$.hasNext())
        	{
        		Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
            	if (((UniversalRecipeInput)entry.getKey()).containFluidStack(fluidStack))
            	{
            		this.arecipes.add(new MachineRecipeHandler.CachedIORecipe(entry.getKey(), entry.getValue(),getAdditionalIngredients()));
            	}
        	}
    	}
        else
        {
        	while (i$.hasNext())
        	{
        		Entry<UniversalRecipeInput, UniversalRecipeOutput> entry = i$.next();
            	if (((UniversalRecipeInput)entry.getKey()).containItemStack(ingredient))
            	{
            		this.arecipes.add(new MachineRecipeHandler.CachedIORecipe(entry.getKey(), entry.getValue(),getAdditionalIngredients()));
            	}
        	}
        }
    }

    protected int[] getFluidInputPosX()
    {
        return null;
    }

    protected int[] getFluidInputPosY()
    {
        return null;
    }

    protected int[] getFluidOutputPosX()
    {
        return null;
    }

    protected int[] getFluidOutputPosY()
    {
        return null;
    }
    
    protected abstract int[] getInputPosX();
    protected abstract int[] getInputPosY();
    protected abstract int[] getOutputPosX();
    protected abstract int[] getOutputPosY();

    public class CachedIORecipe extends CachedRecipe
    {
        private final List<PositionedStack> ingredients = new ArrayList<PositionedStack>();
        public PositionedStack output;
        public final List<PositionedStack> otherStacks = new ArrayList<PositionedStack>();
        public boolean specialConditions=false;

        @Override
		public List<PositionedStack> getIngredients()
        {
        	return this.getCycledIngredients(MachineRecipeHandler.this.cycleticks / 20, this.ingredients);
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

        public CachedIORecipe(UniversalRecipeInput input, UniversalRecipeOutput output1, List<PositionedStack> additionalIngredients)
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
            else if ((output1.getFluidOutputs() == null || output1.getFluidOutputs().isEmpty()) && (output1.getItemOutputs() == null || output1.getItemOutputs().isEmpty()))
            {
                throw new NullPointerException("Output must not be null (recipe " + input + " -> " + output1 + ").");
            }
            else
            {
            	specialConditions=output1.specialConditions;
                ArrayList<List<ItemStack>> items = new ArrayList<List<ItemStack>>();
                ArrayList<List<ItemStack>> fluidItems = new ArrayList<List<ItemStack>>();
                if(input.getFluidInputs()!=null && !input.getFluidInputs().isEmpty())
                {
                    Iterator<?> i = input.getFluidInputs().iterator();
                    while (i.hasNext())
                    {
                    	IRecipeInputFluid fstackRI = (IRecipeInputFluid)i.next();
                    	List<FluidStack> fstackList = fstackRI.getInputs();
                    	List<ItemStack> fContainers = new ArrayList<ItemStack>();
                    	for(FluidStack fstack:fstackList)
                    	{
                            ItemStack stack = Ic2Items.FluidCell.copy();
                            ((ItemFluidCell)Ic2Items.FluidCell.copy().getItem()).fill(stack, fstack, true);
                            if(fstack.amount>1000)
                            {
                            	stack.stackSize=fstack.amount/1000;
                            }
                            fContainers.add(stack);
                    	}
                        fluidItems.add(fContainers);
                    }
                }
                
                if(input.getItemInputs()!=null && !input.getItemInputs().isEmpty())
                {
                    Iterator<?> i = input.getItemInputs().iterator();
                    while (i.hasNext())
                    {
                        IRecipeInput rInput = (IRecipeInput)i.next();
                        Iterator<ItemStack> rInputsi = rInput.getInputs().iterator();
                        List<ItemStack> itemInputs = new ArrayList<ItemStack>();
                        while(rInputsi.hasNext())
                        {
                        	ItemStack stack = rInputsi.next().copy();
                        	if(!(stack.getItem() instanceof IWire))
                        	{
                        		stack.stackSize=rInput.getAmount();
                        	}
                        	itemInputs.add(stack);
                        }
                        items.add(itemInputs);
                    }
                }
                
            	int var7=0;
            	int var8=0;
            	boolean skipOneFluidOutput=false;
                if(output1.getItemOutputs()!=null && !output1.getItemOutputs().isEmpty())
                {
                	int x = MachineRecipeHandler.this.getOutputPosX()[0],y = MachineRecipeHandler.this.getOutputPosY()[0];
                	if(output1.getItemOutputs().get(0)!=null)this.output = new IHLPositionedStack(output1.getItemOutputs().get(0), x, y);
                	Iterator<RecipeOutputItemStack> i = output1.getItemOutputs().iterator();
                	i.next();
                    while (i.hasNext())
                    {
                        RecipeOutputItemStack rOut = i.next();
                        var7++;
                    	if(var7<MachineRecipeHandler.this.getOutputPosX().length)
                    	{
                        	x=MachineRecipeHandler.this.getOutputPosX()[var7];
                    	}
                    	if(var7<MachineRecipeHandler.this.getOutputPosY().length)
                    	{
                        	y=MachineRecipeHandler.this.getOutputPosY()[var7];
                    	}
                        if(rOut!=null)
                        {
                       		this.otherStacks.add(new IHLPositionedStack(rOut, x, y));
                        }
                    }
                }
                else
                {
                	ItemStack fluidCellStack = Ic2Items.FluidCell.copy();
                	((ItemFluidCell)fluidCellStack.getItem()).fill(fluidCellStack, output1.getFluidOutputs().get(0).copy(), true);
                    if(output1.getFluidOutputs().get(0).amount>1000)
                    {
                    	fluidCellStack.stackSize=output1.getFluidOutputs().get(0).amount/1000;
                    }
                    if(MachineRecipeHandler.this.getFluidOutputPosX()!=null)
                    {
                    	this.output = new PositionedStack(fluidCellStack, MachineRecipeHandler.this.getFluidOutputPosX()[0], MachineRecipeHandler.this.getFluidOutputPosY()[0]);
                    }
                    else
                    {
                    	this.output = new PositionedStack(fluidCellStack, MachineRecipeHandler.this.getOutputPosX()[0], MachineRecipeHandler.this.getOutputPosY()[0]);
                    }
                	skipOneFluidOutput=true;
                	var8++;
                }
                
                if(output1.getFluidOutputs()!=null && !output1.getFluidOutputs().isEmpty())
                {
                	int x = MachineRecipeHandler.this.getOutputPosX()[0],y = MachineRecipeHandler.this.getOutputPosY()[0];
                    if(MachineRecipeHandler.this.getFluidOutputPosX()!=null)
                    {
                    	x = MachineRecipeHandler.this.getFluidOutputPosX()[0];
                    	y = MachineRecipeHandler.this.getFluidOutputPosY()[0];
                    }
                    Iterator<?> i = output1.getFluidOutputs().iterator();
                    if(skipOneFluidOutput)
                    {
                    	i.next();
                    }
                    while (i.hasNext())
                    {
                        FluidStack fstack = (FluidStack)i.next();
                        ItemStack stack1 = Ic2Items.FluidCell.copy();
                        ((ItemFluidCell)stack1.getItem()).fill(stack1, fstack, true);
                        if(fstack.amount>1000)
                        {
                        	stack1.stackSize=fstack.amount/1000;
                        }
                        var7++;
                        if(MachineRecipeHandler.this.getFluidOutputPosX()!=null)
                        {
                        	if(var8<MachineRecipeHandler.this.getFluidOutputPosX().length)
                        	{
                            	x=MachineRecipeHandler.this.getFluidOutputPosX()[var8];
                        	}
                        	if(var8<MachineRecipeHandler.this.getFluidOutputPosY().length)
                        	{
                            	y=MachineRecipeHandler.this.getFluidOutputPosY()[var8];
                        	}
                        	var8++;
                        }
                        else
                        {
                        	if(var7<MachineRecipeHandler.this.getOutputPosX().length)
                        	{
                            	x=MachineRecipeHandler.this.getOutputPosX()[var7];
                        	}
                        	if(var7<MachineRecipeHandler.this.getOutputPosY().length)
                        	{
                            	y=MachineRecipeHandler.this.getOutputPosY()[var7];
                        	}
                        }
                        this.otherStacks.add(new PositionedStack(stack1, x, y));
                    }
                }
                
                if(MachineRecipeHandler.this.getFluidInputPosX()!=null)
                {
                	int x = MachineRecipeHandler.this.getFluidInputPosX()[0],y = MachineRecipeHandler.this.getFluidInputPosY()[0];
                    for(int i=0; i<fluidItems.size();i++)
                    {
                    	if(i<MachineRecipeHandler.this.getFluidInputPosX().length)
                    	{
                        	x=MachineRecipeHandler.this.getFluidInputPosX()[i];
                    	}
                    	if(i<MachineRecipeHandler.this.getFluidInputPosY().length)
                    	{
                        	y=MachineRecipeHandler.this.getFluidInputPosY()[i];
                    	}
                        this.ingredients.add(new PositionedStack(fluidItems.get(i), x, y));
                    }
                	x = MachineRecipeHandler.this.getInputPosX()[0];
                	y = MachineRecipeHandler.this.getInputPosY()[0];
                    for(int i=0; i<items.size();i++)
                    {
                    	if(i<MachineRecipeHandler.this.getInputPosX().length)
                    	{
                        	x=MachineRecipeHandler.this.getInputPosX()[i];
                    	}
                    	if(i<MachineRecipeHandler.this.getInputPosY().length)
                    	{
                        	y=MachineRecipeHandler.this.getInputPosY()[i];
                    	}
                        this.ingredients.add(new PositionedStack(items.get(i), x, y));
                    }
                }
                else
                {
                	int x = MachineRecipeHandler.this.getInputPosX()[0],y = MachineRecipeHandler.this.getInputPosY()[0];
                    for(int i=0; i<fluidItems.size()+items.size();i++)
                    {
                    	if(i<MachineRecipeHandler.this.getInputPosX().length)
                    	{
                        	x=MachineRecipeHandler.this.getInputPosX()[i];
                    	}
                    	if(i<MachineRecipeHandler.this.getInputPosY().length)
                    	{
                        	y=MachineRecipeHandler.this.getInputPosY()[i];
                    	}
                    	if(i<fluidItems.size())
                    	{
                            this.ingredients.add(new PositionedStack(fluidItems.get(i), x, y));
                    	}
                    	else
                    	{
                            this.ingredients.add(new PositionedStack(items.get(i-fluidItems.size()), x, y));
                    	}
                    }
                }
                if(additionalIngredients!=null)
                {
                	this.ingredients.addAll(additionalIngredients);
                }
            }
        }
    }
}


