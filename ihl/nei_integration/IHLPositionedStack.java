package ihl.nei_integration;

import codechicken.nei.PositionedStack;
import ihl.recipes.RecipeOutputItemStack;

public class IHLPositionedStack extends PositionedStack {

	public final float sQuantity;
	public IHLPositionedStack(RecipeOutputItemStack object, int x, int y) 
	{
		super(object.itemStack.copy(), x, y);
		sQuantity=object.quantity;
	}

}
