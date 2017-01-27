package ihl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ihl.recipes.IRecipeInputFluid;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class IHLFluidTank implements IFluidTank
{
    private final List<FluidStack> fluidList = new ArrayList<FluidStack>();
    private final int capacity;
    private int temperature=293;

    public IHLFluidTank(int capacity)
    {
        this.capacity = capacity;
    }

    public IHLFluidTank(int capacity, boolean isOpenVessel1)
    {
        this.capacity = capacity;
    }
    
    public IHLFluidTank readFromNBT(NBTTagCompound nbt)
    {
        if (!nbt.hasKey("Empty"))
        {
    		NBTTagList fluidList1 = nbt.getTagList("fluids", 10);
            for(int i=0;i<fluidList1.tagCount();i++)
            {
            	NBTTagCompound fluidNBT1 = fluidList1.getCompoundTagAt(i);
            	FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidNBT1);
                if (fluid != null)
                {
                	fluidList.add(fluid);
                }
            }
            temperature=nbt.getInteger("temperature");
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (!fluidList.isEmpty())
        {
        	NBTTagList fluids = new NBTTagList();
        	Iterator<FluidStack> fli = fluidList.iterator();
			while(fli.hasNext())
        	{
	        	FluidStack fluid=fli.next();
        		if(fluid!=null)
        		{
        			NBTTagCompound fluidNBT1 = new NBTTagCompound();
                	fluid.writeToNBT(fluidNBT1);
                	fluids.appendTag(fluidNBT1);
        		}
        	}
			nbt.setTag("fluids", fluids);
			nbt.setInteger("temperature", temperature);
        }
        else
        {
            nbt.setString("Empty", "");
        }
        return nbt;
    }

    /* IFluidTank */
    @Override
    public FluidStack getFluid()
    {
    	if(this.fluidList.isEmpty())
    	{
    		return null;
    	}
        return this.fluidList.get(0);
    }
    
    public FluidStack getLigthestFluid()
    {
    	if(this.fluidList.isEmpty())
    	{
    		return null;
    	}
        return this.fluidList.get(this.fluidList.size()-1);
    }

    @Override
    public int getFluidAmount()
    {
    	int amount=0;
    	Iterator<FluidStack> fli = fluidList.iterator();
		while(fli.hasNext())
    	{
        	FluidStack fluid=fli.next();
        	if(fluid!=null)
        	{
            	amount+=fluid.amount;
        	}
    	}
        return amount;
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return new FluidTankInfo(this);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource == null || resource.getFluid() == null || resource.amount<=0)
        {
            return 0;
        }
        if (!doFill)
        {
            if (fluidList.isEmpty())
            {
                return Math.min(capacity, resource.amount);
            }
            return Math.min(capacity - this.getFluidAmount(), resource.amount);
        }
		if (fluidList.isEmpty())
        {
			FluidStack fluid = copyWithSize(resource,Math.min(capacity, resource.amount));
			fluidList.add(fluid);
			this.temperature=fluid.getFluid().getTemperature();
            return fluid.amount;
        }
		this.temperature=alignTemperatures(resource);
		FluidStack fluid = getFluidStackWithSameFluid(resource);
		if(fluid!=null)
		{
			int amount1=Math.min(capacity - this.getFluidAmount(), resource.amount);
			fluid.amount+=amount1;
			return amount1;
		}
		fluid = copyWithSize(resource,Math.min(capacity, resource.amount));
		fluidList.add(fluid);
    	this.sortFluidsByDensity();
        return fluid.amount;
    }

    private int alignTemperatures(FluidStack resource) 
    {
    	int amountOfFluidInTank = this.getFluidAmount();
		int averageT=(this.temperature*amountOfFluidInTank+resource.getFluid().getTemperature()*resource.amount)/(amountOfFluidInTank+resource.amount);
		return averageT;
	}

	@Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (fluidList.isEmpty())
        {
            return null;
        }
		FluidStack fstack = this.getFluid().copy();
		fstack.amount=maxDrain;
		return this.drain(fstack, doDrain);
    }
	

	public FluidStack drainLightest(int maxDrain, boolean doDrain) 
	{
        if (fluidList.isEmpty())
        {
            return null;
        }
		FluidStack fstack = this.getLigthestFluid().copy();
		fstack.amount=maxDrain;
		return this.drain(fstack, doDrain);
	}
	
	public FluidStack drain(Object fluidStack, boolean doDrain) 
	{
        if (fluidList.isEmpty())
        {
            return null;
        }
        int drained = 0;
        if(fluidStack instanceof FluidStack)
        {
        	drained = ((FluidStack)fluidStack).amount;
        }
        else
        {
        	drained = ((IRecipeInputFluid)fluidStack).getAmount();
        }
        FluidStack fluid = this.getFluidStackWithSameFluid(fluidStack);
        if (fluid==null)
        {
            return null;
        }
        if (fluid.amount < drained)
        {
            drained = fluid.amount;
        }
		FluidStack stack = copyWithSize(fluid,drained);
        if (doDrain)
        {
            fluid.amount -= drained;
            if (fluid.amount <= 0)
            {
            	this.fluidList.remove(fluid);
                fluid = null;
            }
        }
        return stack;
       }
    
    public FluidStack getFluidStackWithSameFluid(Object fluidStack)
    {
    	Iterator<FluidStack> fli = fluidList.iterator();
		while(fli.hasNext())
    	{
        	FluidStack fluid=fli.next();
        	if(fluid!=null)
        	{
            	if(fluidStack instanceof FluidStack)
            	{
            		if(fluid.isFluidEqual((FluidStack)fluidStack))
                	{
                		return fluid;
                	}
            	}
            	else
            	{
            		if(((IRecipeInputFluid)fluidStack).matches(fluid))
            		{
                		return fluid;
            		}
            	}
        	}
    	}
		return null;
    }
    
	public int getNumberOfFluids() 
	{
		return this.fluidList.size();
	}

	public void setFluidAmount(int amount1, int index) 
	{
		if(this.fluidList.size()<=index)
		{
			while(this.fluidList.size()<=index)
			{
				this.fluidList.add(new FluidStack(FluidRegistry.WATER,1));
			}
		}
		this.fluidList.get(index).amount=amount1;
	}
	
	public int getFluidAmount(int index) 
	{
		if(this.fluidList.size()<=index || this.fluidList.get(index)==null)
		{
			return 0;
		}
		return this.fluidList.get(index).amount;
	}

	public int getFluidID(int i) 
	{
		if(this.fluidList.get(i)==null)
		{
			return -1;
		}
		return this.fluidList.get(i).getFluid().getID();
	}
	
	public void sortFluidsByDensity()
	{
		Map<Integer, FluidStack> sortMap = new HashMap<Integer, FluidStack>();
		int[] keysArray = new int[fluidList.size()];
    	Iterator<FluidStack> fli = fluidList.iterator();
		while(fli.hasNext())
    	{
        	FluidStack fluid=fli.next();
        	if(fluid==null)
        	{
        		return;
        	}
        		int key = Math.round(IHLFluid.getRealDensity(fluid.getFluid())*100F);
        		while(sortMap.containsKey(key))
        		{
        			key++;
        		}
        		sortMap.put(key, fluid);
        		keysArray[fluidList.indexOf(fluid)]=key;
    	}
		Arrays.sort(keysArray);
		ArrayList<FluidStack> newFluidList = new ArrayList<FluidStack>();
		for(int i=keysArray.length-1;i>=0;i--)
		{
			newFluidList.add(sortMap.get(keysArray[i]));
		}
		this.fluidList.clear();
		this.fluidList.addAll(newFluidList);
	}

	public FluidStack getFluid(int i) 
	{
		return this.fluidList.get(i);
	}

	public void setTag(String string, int t1_1) 
	{
		if(this.getFluid().tag==null)
		{
			this.getFluid().tag=new NBTTagCompound();
		}
		this.getFluid().tag.setInteger(string, t1_1);
	}

	public void setEmpty() 
	{
		this.fluidList.clear();
	}

	public int getTemperature() 
	{
		return this.temperature;
	}

	public void setTemperature(int t1) 
	{
		 this.temperature=t1;
	}

	public List<FluidStack> getFluidList() 
	{
		return this.fluidList;
	}

	public void drain(List<?> fluidInputs, boolean doDrain) 
	{
		if(fluidInputs!=null && !fluidInputs.isEmpty())
		{
			Iterator<?> fsi = fluidInputs.iterator();
			while(fsi.hasNext())
			{
				this.drain(fsi.next(), doDrain);
			}
		}
	}

	public void fill(List<FluidStack> fluidOutputs, boolean doFill) 
	{
		if(fluidOutputs!=null && !fluidOutputs.isEmpty())
		{
			Iterator<FluidStack> fsi = fluidOutputs.iterator();
			while(fsi.hasNext())
			{
				this.fill(fsi.next(), doFill);
			}
		}
	}
	
	private FluidStack copyWithSize(FluidStack resource, int amount1)
	{
		FluidStack fluid = resource.copy();
		fluid.amount=amount1;
		if(resource.tag!=null)
		{
			fluid.tag=(NBTTagCompound) resource.tag.copy();
		}
        return fluid;
	}

	public FluidStack drain(IRecipeInputFluid fluidStack, int amount, boolean doDrain) 
	{
        if (fluidList.isEmpty())
        {
            return null;
        }
        int drained = amount;
        FluidStack fluid = this.getFluidStackWithSameFluid(fluidStack);
        if (fluid==null)
        {
            return null;
        }
        if (fluid.amount < drained)
        {
            drained = fluid.amount;
        }
		FluidStack stack = copyWithSize(fluid,drained);
        if (doDrain)
        {
            fluid.amount -= drained;
            if (fluid.amount <= 0)
            {
            	this.fluidList.remove(fluid);
                fluid = null;
            }
        }
        return stack;
	}
	
	public void checkCorrectState()
	{
		if(!this.fluidList.isEmpty())
		{
			Iterator<FluidStack> fsi=this.fluidList.iterator();
			while(fsi.hasNext())
			{
				FluidStack fs = fsi.next();
				if(fs.amount<=0)
				{
					fsi.remove();
				}
			}
		}
	}
}
