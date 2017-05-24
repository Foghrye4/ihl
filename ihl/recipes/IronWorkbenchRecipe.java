package ihl.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ihl.interfaces.IWire;
import ihl.utils.IHLUtils;
import net.minecraft.item.ItemStack;

public class IronWorkbenchRecipe {
	public List<ItemStack> workspaceElements=new ArrayList<ItemStack>();
	public List<IRecipeInput> tools=new ArrayList<IRecipeInput>();
	public List<IRecipeInput> materials=new ArrayList<IRecipeInput>();
	public List<ItemStack> outputs = new ArrayList<ItemStack>(); 
	
	public IronWorkbenchRecipe(List<?> tools1, List<?> materials1, List<ItemStack> output1_1)
	{
		if(tools1!=null)
		{
			Iterator<?> iTools1 = tools1.iterator();
			while(iTools1.hasNext())
			{
				Object tool = iTools1.next();
				if(tool instanceof ItemStack)
				{
					ItemStack stack = (ItemStack) tool;
					String oreDictName = IHLUtils.getFirstOreDictNameExcludingTagAny(stack);
					if(!oreDictName.isEmpty() && oreDictName.length()>3)
					{
						tools.add(new RecipeInputOreDict(oreDictName));
					}
					else
					{
						tools.add(new RecipeInputItemStack(stack));
					}
				}
				else 
				{
					tools.add((IRecipeInput) tool);
				}
			}
		}
		Iterator<?> iMaterials1 = materials1.iterator();
		while(iMaterials1.hasNext())
		{
			Object material = iMaterials1.next();
			if(material instanceof ItemStack)
			{
				ItemStack stack = (ItemStack) material;
				String oreDictName = IHLUtils.getFirstOreDictNameExcludingTagAny(stack);
				if(stack.getItem() instanceof IWire)
				{
					materials.add(new RecipeInputWire(stack));
				}
				else if(!oreDictName.isEmpty() && oreDictName.length()>3)
				{
					materials.add(new RecipeInputOreDict(oreDictName,stack.stackSize));
				}
				else
				{
					materials.add(new RecipeInputItemStack(stack));
				}
			}
			else
			{
				materials.add((IRecipeInput) material);
			}

		}
		Iterator<ItemStack> iOutput = output1_1.iterator();
		while(iOutput.hasNext())
		{
			ItemStack outputStack = iOutput.next();
			if(outputStack==null)
				throw new NullPointerException("Output shall not contain null.");
			this.outputs.add(outputStack);
		}
		if(tools.size()>8 || materials.size()>12)
		{
			throw new IllegalArgumentException("Iron workbench recipe cannot contain more than 8 tools or more than 12 materials!");
		}
	}
	
	public IronWorkbenchRecipe(List<?> asList, List<?> asList2,	List<ItemStack> asList3, List<ItemStack> workspaceElements1) {
		this(asList, asList2, asList3);
		if(workspaceElements1!=null)
		{
			this.workspaceElements.addAll(workspaceElements1);
		}
	}

	public boolean isTool(ItemStack tool1)
	{
		if(tools!=null && !tools.isEmpty())
		{
			if(tool1==null)
			{
				return false;
			}
			Iterator<IRecipeInput> i1 = tools.iterator();
			while(i1.hasNext())
			{
				IRecipeInput tool = i1.next();
				if(tool.matches(tool1))
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean isCanBeCrafted(List<ItemStack> tools1, List<ItemStack> materials1, List<ItemStack> workspaceElements1)
	{
		if(workspaceElements!=null && !workspaceElements.isEmpty())
		{
			if(workspaceElements1==null||workspaceElements1.isEmpty())
			{
				return false;
			}
			Iterator<ItemStack> i1 = workspaceElements.iterator();
			while(i1.hasNext())
			{
				ItemStack tool = i1.next();
				if(!this.isItemStackInList(tool, workspaceElements1))
				{
					return false;
				}
			}
		}
		
			if(tools!=null && !tools.isEmpty())
			{
				if(tools1==null||tools1.isEmpty())
				{
					return false;
				}
				Iterator<IRecipeInput> i1 = tools.iterator();
				while(i1.hasNext())
				{
					IRecipeInput tool = i1.next();
					if(!this.isItemStackInList(tool, tools1))
					{
						return false;
					}
				}
			}
			if(materials!=null && !materials.isEmpty())
			{
				Iterator<IRecipeInput> i1 = materials.iterator();
				while(i1.hasNext())
				{
					IRecipeInput material = i1.next();
					if(!this.isItemStackInList(material, materials1))
					{
						return false;
					}
				}
			}
			return true;
	}

	private boolean isItemStackInList(IRecipeInput tool, List<ItemStack> tools1) {
		Iterator<ItemStack> it = tools1.iterator();
		while(it.hasNext())
		{
			ItemStack tool2 = it.next();
			if(tool.matches(tool2))
			{
				if(tool2.getItem() instanceof IWire)
				{
					if(IHLUtils.getWireLength(tool2)>=tool.getAmount())
					{
						return true;
					}
				}
				else if(tool2.stackSize>=tool.getAmount())
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean isItemStackInList(ItemStack tool, List<ItemStack> tools1)
	{
		Iterator<ItemStack> it = tools1.iterator();
		while(it.hasNext())
		{
			ItemStack tool2 = it.next();
			if(IHLUtils.isItemStacksIsEqual(tool,tool2,true))
			{
				if(tool2.getItem() instanceof IWire)
				{
					if(IHLUtils.getWireLength(tool2)>=IHLUtils.getWireLength(tool))
					{
						return true;
					}
				}
				else if(tool2.stackSize>=tool.stackSize)
				{
					return true;
				}
			}
		}
		return false;
	}

}
