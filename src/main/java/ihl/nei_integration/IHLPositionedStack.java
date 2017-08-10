package ihl.nei_integration;

import java.util.Arrays;
import java.util.List;

import codechicken.nei.PositionedStack;
import ic2.core.Ic2Items;
import ic2.core.item.ItemFluidCell;
import ihl.recipes.RecipeOutputItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IHLPositionedStack extends PositionedStack {

	public final float sQuantity;
	public IHLPositionedStack(RecipeOutputItemStack object, int x, int y) 
	{
		super(object.itemStack.copy(), x, y);
		sQuantity=object.quantity;
	}
	
	public IHLPositionedStack(FluidStack fluidStack, int x, int y) 
	{
		super(Ic2Items.FluidCell.copy(), x, y);
    	ItemStack fluidCellStack = this.items[0];
    	((ItemFluidCell)fluidCellStack.getItem()).fill(fluidCellStack, fluidStack, true);
		sQuantity=fluidStack.amount/1000f;
		this.item=this.items[0]=fluidCellStack;
	}

	
	public IHLPositionedStack(List<FluidStack> list, int x, int y) {
		super(Ic2Items.FluidCell.copy(),x,y);
		sQuantity = list.get(0).amount/1000f;
		this.items = new ItemStack[list.size()];
		for(int i=0;i<list.size();i++){
	    	ItemStack fluidCellStack = Ic2Items.FluidCell.copy();
	    	((ItemFluidCell)fluidCellStack.getItem()).fill(fluidCellStack, list.get(i), true);
			this.items[i] = fluidCellStack;
		}
		this.item=this.items[0];
	}


}
