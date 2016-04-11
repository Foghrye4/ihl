package ihl.nei_integration;

import ihl.recipes.RecipeOutputItemStack;
import codechicken.nei.PositionedStack;

public class IHLPositionedStack extends PositionedStack {

	public final float sQuantity;
	public IHLPositionedStack(RecipeOutputItemStack object, int x, int y) 
	{
		super(object.itemStack.copy(), x, y);
		sQuantity=object.quantity;
	}

}
