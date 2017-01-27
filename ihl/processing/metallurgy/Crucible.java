package ihl.processing.metallurgy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import ic2.api.item.IItemHudInfo;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.item.IHandHeldInventory;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;

public class Crucible extends Item implements IHandHeldInventory, IFluidContainerItem, IItemHudInfo{

	public int capacity=864;
	private String itemName;
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("crucible");
	
	@SideOnly(Side.CLIENT)
	private IIcon iconHot;

	public Crucible() {
		super();
		this.itemName="crucible";
		this.setUnlocalizedName(itemName);
		GameRegistry.registerItem(this, this.itemName);
		this.setCreativeTab(IHLCreativeTab.tab);
		this.maxStackSize=1;
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setMaxDamage(0);
	}
	
	public static void addRecipe(String string, FluidStack output) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput(null, new IRecipeInput[] {new RecipeInputOreDict(string)}), new UniversalRecipeOutput(new FluidStack[] {output},null,20));
	}

	@Override
	public IHasGui getInventory(EntityPlayer entityPlayer, ItemStack itemStack) 
	{
		return new CrucibleInventory(entityPlayer, itemStack);
	}
	
    @Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (IC2.platform.isSimulating())
        {
            IC2.platform.launchGui(entityPlayer, this.getInventory(entityPlayer, itemStack));
        }
        return itemStack;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b)
    {
        super.addInformation(itemStack, player, info, b);
        FluidStack fs = this.getFluid(itemStack);

        if (fs != null)
        {
            info.add("< " + FluidRegistry.getFluidName(fs) + ", " + fs.amount + " mB >");
        }
        else
        {
            info.add(StatCollector.translateToLocal("ic2.item.FluidContainer.Empty"));
        }
    }

    @Override
	public List<String> getHudInfo(ItemStack itemStack)
    {
        LinkedList<String> info = new LinkedList<String>();
        FluidStack fs = this.getFluid(itemStack);

        if (fs != null)
        {
            info.add("< " + FluidRegistry.getFluidName(fs) + ", " + fs.amount + " mB >");
        }
        else
        {
            info.add(StatCollector.translateToLocal("ic2.item.FluidContainer.Empty"));
        }

        return info;
    }

    @Override
	public FluidStack getFluid(ItemStack stack)
    {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        return FluidStack.loadFluidStackFromNBT(fluidTag);
    }

    public boolean isEmpty(ItemStack stack)
    {
        return this.getFluid(stack) == null;
    }

    @Override
	public int getCapacity(ItemStack container)
    {
        return this.capacity;
    }

    public boolean canfill(Fluid var1)
    {
        return true;
    }
    
    public ItemStack processContent(ItemStack itemStack, IInventory inventoryContainer)
    {
    	CrucibleInventory inventory = new CrucibleInventory(inventoryContainer, itemStack);
    	ItemStack content = inventory.getStackInSlot(0);
    	if(content!=null)
    	{
    		UniversalRecipeOutput routput = recipeManager.getOutputFor(null, Arrays.asList(new ItemStack [] {content}),false, false);
    		if(routput!=null && !routput.getFluidOutputs().isEmpty())
    		{
				FluidStack fluidStack = routput.getFluidOutputs().get(0).copy();
				fluidStack.amount *= content.stackSize;
				itemStack.stackTagCompound = new NBTTagCompound();
				this.fill(itemStack, fluidStack, true);
				return itemStack;
    		}
    	}
    	return itemStack;
    }

    @Override
	public int fill(ItemStack stack, FluidStack resource, boolean doFill)
    {
        if (stack.stackSize != 1)
        {
            return 0;
        }
        else if (resource == null)
        {
            return 0;
        }
        else if (!this.canfill(resource.getFluid()))
        {
            return 0;
        }
        else
        {
            NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
            NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
            FluidStack fs = FluidStack.loadFluidStackFromNBT(fluidTag);

            if (fs == null)
            {
                fs = new FluidStack(resource, 0);
            }

            if (!fs.isFluidEqual(resource))
            {
                return 0;
            }
            else
            {
                int amount = Math.min(this.capacity - fs.amount, resource.amount);

                if (doFill && amount > 0)
                {
                    fs.amount += amount;
                    fs.writeToNBT(fluidTag);
                    nbtTagCompound.setTag("Fluid", fluidTag);
                    if(fs.getFluid().getTemperature()>900)stack.setItemDamage(1);
                }

                return amount;
            }
        }
    }

    @Override
	public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain)
    {
        if (stack.stackSize != 1)
        {
            return null;
        }
        else
        {
            NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
            NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
            FluidStack fs = FluidStack.loadFluidStackFromNBT(fluidTag);

            if (fs == null)
            {
                return null;
            }
            else
            {
                maxDrain = Math.min(fs.amount, maxDrain);

                if (doDrain)
                {
                    fs.amount -= maxDrain;

                    if (fs.amount <= 0)
                    {
                        nbtTagCompound.removeTag("Fluid");
                        stack.setItemDamage(0);
                    }
                    else
                    {
                        fs.writeToNBT(fluidTag);
                        nbtTagCompound.setTag("Fluid", fluidTag);
                    }
                }

                return new FluidStack(fs, maxDrain);
            }
        }
    }
    
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) 
	{
		itemIcon=register.registerIcon(IHLModInfo.MODID + ":crucible");
		iconHot=register.registerIcon(IHLModInfo.MODID + ":crucibleHot");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
    	switch(meta)
    	{
    	case 0:
   			return this.itemIcon;
    	case 1:
   			return this.iconHot;
   		default:
   			return this.itemIcon;
    	}
    }
	
    @Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset)
    {
        if (!IC2.platform.isSimulating())
        {
            return false;
        }
        else if (this.interactWithTank(stack, player, world, x, y, z, side))
        {
            return true;
        }
		return false;
    }
    

    private boolean interactWithTank(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side)
    {
        if (!IC2.platform.isSimulating())
        {
            return false;
        }
        else
        {
            TileEntity te = world.getTileEntity(x, y, z);

            if (!(te instanceof IFluidHandler))
            {
                return false;
            }
            else
            {
                IFluidHandler handler = (IFluidHandler)te;
                ForgeDirection dir = ForgeDirection.getOrientation(side);
                FluidStack fs = this.getFluid(stack);
                int amount;

                if (fs != null && (!player.isSneaking() || fs.amount >= this.capacity))
                {
                    amount = handler.fill(dir, fs, false);

                    if (amount <= 0)
                    {
                        return false;
                    }
                    else
                    {
                        fs = LiquidUtil.drainContainerStack(stack, player, amount, false);

                        if (fs != null && fs.amount > 0)
                        {
                            handler.fill(dir, fs, true);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    amount = fs == null ? this.capacity : this.capacity - fs.amount;
                    FluidStack input = handler.drain(dir, amount, false);

                    if (input != null && input.amount > 0)
                    {
                        amount = LiquidUtil.fillContainerStack(stack, player, input, false);

                        if (amount <= 0)
                        {
                            return false;
                        }
                        else
                        {
                            handler.drain(dir, amount, true);
                            return true;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
    }

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}

}
