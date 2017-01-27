package ihl.processing.invslots;

import java.util.Iterator;

import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.util.StackUtil;
import ihl.utils.IHLUtils;

public class InvSlotConsumableLiquidIHL extends InvSlotConsumableLiquid{

	private OpType opType;
	private String additionalInputs;

	public InvSlotConsumableLiquidIHL(TileEntityInventory base1, String name1,int oldStartIndex1, Access access1, int count,InvSide preferredSide1, OpType opType1) 
	{
		super(base1, name1, oldStartIndex1, access1, count, preferredSide1, opType1);
		opType=opType1;
	}
	
	public InvSlotConsumableLiquidIHL(TileEntityInventory base1, String name1,int oldStartIndex1, Access access1, int count,InvSide preferredSide1, OpType opType1, String additionalInputs1) 
	{
		super(base1, name1, oldStartIndex1, access1, count, preferredSide1, opType1);
		opType=opType1;
		additionalInputs=additionalInputs1;
	}
	
	@Override
    public FluidStack drain(Fluid fluid, int maxAmount, MutableObject<ItemStack> output, boolean simulate)
    {
		if(output!=null)
		{
	        output.setValue((ItemStack)null);
		}

        if (this.opType != InvSlotConsumableLiquid.OpType.Drain && this.opType != InvSlotConsumableLiquid.OpType.Both)
        {
            return null;
        }
        else
        {
            ItemStack stack = this.get();

            if (stack == null)
            {
                return null;
            }
            else if (!FluidContainerRegistry.isFilledContainer(stack))
            {
                if (stack.getItem() instanceof IFluidContainerItem)
                {
                    IFluidContainerItem var9 = (IFluidContainerItem)stack.getItem();

                    if (var9.getFluid(stack) == null)
                    {
                        return null;
                    }
                    else if (fluid != null && var9.getFluid(stack).getFluid() != fluid)
                    {
                        return null;
                    }
                    else if (!this.acceptsLiquid(var9.getFluid(stack).getFluid()))
                    {
                        return null;
                    }
                    else
                    {
                        ItemStack singleStack = StackUtil.copyWithSize(stack, 1);
                        FluidStack fluidStack = var9.drain(singleStack, maxAmount, true);

                        if (fluidStack != null && fluidStack.amount > 0)
                        {
                            if (singleStack.stackSize <= 0)
                            {
                                if (!simulate)
                                {
                                    --stack.stackSize;
                                }
                            }
                            else if (var9.getFluid(singleStack) == null)
                            {
                        		if(output!=null)
                        		{
                        			output.setValue(singleStack);
                        		}
                                if (!simulate)
                                {
                                    --stack.stackSize;
                                }
                            }
                            else
                            {
                                if (stack.stackSize > 1)
                                {
                                    return null;
                                }

                                if (!simulate)
                                {
                                    this.put(singleStack);
                                }
                            }

                            if (stack.stackSize <= 0)
                            {
                                this.put((ItemStack)null);
                            }

                            return fluidStack;
                        }
                        else
                        {
                            return null;
                        }
                    }
                }
                else
                {
                    return null;
                }
            }
            else
            {
                FluidStack container = FluidContainerRegistry.getFluidForFilledItem(stack);

                if (container != null && (fluid == null || fluid == container.getFluid()))
                {
                    if (!this.acceptsLiquid(container.getFluid()))
                    {
                        return null;
                    }
                    else if (container.amount > 0 && container.amount <= maxAmount)
                    {
                        if (stack.getItem().hasContainerItem(stack) && output!=null)
                        {
                            output.setValue(stack.getItem().getContainerItem(stack));
                        }
                        else
                        {
                        	ItemStack emptystack = FluidContainerRegistry.drainFluidContainer(stack);
                        	if(emptystack!=null && output!=null)
                        	{
                        		output.setValue(emptystack);
                        	}
                        }

                        if (!simulate)
                        {
                            --stack.stackSize;

                            if (stack.stackSize <= 0)
                            {
                                this.put((ItemStack)null);
                            }
                        }

                        return container;
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
        }
    }

	@Override
    public boolean accepts(ItemStack stack)
    {
        if (stack == null)
        {
            return true;
        }
        Item item = stack.getItem();
        if (item == null)
        {
            return false;
        }
        else
        {
        	if(this.additionalInputs!=null)
        	{
        		String fon = IHLUtils.getFirstOreDictName(stack);
        		if(fon!=null && fon.contains(additionalInputs))
        		{
        			return true;
        		}
        	}
            if (this.opType == InvSlotConsumableLiquid.OpType.Drain || this.opType == InvSlotConsumableLiquid.OpType.Both)
            {
                FluidStack containerItem = null;
                if (FluidContainerRegistry.isFilledContainer(stack))
                {
                    containerItem = FluidContainerRegistry.getFluidForFilledItem(stack);
                }
                else if (item instanceof IFluidContainerItem)
                {
                    containerItem = ((IFluidContainerItem)item).getFluid(stack);
                }

                if (containerItem != null && containerItem.amount > 0)
                {
                    return true;
                }
            }
            if (this.opType == InvSlotConsumableLiquid.OpType.Fill || this.opType == InvSlotConsumableLiquid.OpType.Both)
            {
                if (FluidContainerRegistry.isEmptyContainer(stack))
                {
                    if (this.getPossibleFluids() == null)
                    {
                        return true;
                    }

                    Iterator<Fluid> containerItem1 = this.getPossibleFluids().iterator();

                    while (containerItem1.hasNext())
                    {
                        Fluid prevFluid = containerItem1.next();

                        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(prevFluid, Integer.MAX_VALUE), stack) != null)
                        {
                            return true;
                        }
                    }
                }
                else if (item instanceof IFluidContainerItem)
                {
                    IFluidContainerItem containerItem2 = (IFluidContainerItem)item;
                    FluidStack prevFluid1 = containerItem2.getFluid(stack);

                    if (prevFluid1 == null || containerItem2.getCapacity(stack) > prevFluid1.amount)
                    {
                        if (this.getPossibleFluids() == null)
                        {
                            return true;
                        }

                        ItemStack singleStack = StackUtil.copyWithSize(stack, 1);
                        Iterator<Fluid> i$ = this.getPossibleFluids().iterator();

                        while (i$.hasNext())
                        {
                            Fluid fluid = i$.next();

                            if (containerItem2.fill(singleStack, new FluidStack(fluid, Integer.MAX_VALUE), false) > 0)
                            {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
    }
    
}
