package ihl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidDictionary 
{
	private Map<String,List<FluidStack>> nameToStack = new HashMap();
	private Map<Fluid,String> fluidToName = new HashMap();
	public FluidDictionary(){}
	
	public List<FluidStack> getFluids(String fdName)
	{
		return nameToStack.get(fdName);
	}
	
	public void registerFluidStack(String fdName, FluidStack fstack)
	{
		if(nameToStack.containsKey(fdName))
		{
			nameToStack.get(fdName).add(fstack);
		}
		else
		{
			List<FluidStack> list = new ArrayList();
			list.add(fstack);
			nameToStack.put(fdName, list);
		}
		fluidToName.put(fstack.getFluid(), fdName);
	}
	
	public String getFluidName(Fluid f)
	{
		return fluidToName.get(f);
	}
	
}
