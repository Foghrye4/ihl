package ihl.utils;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.IHLMod;
import ihl.interfaces.IEnergyNetNode;
import ihl.interfaces.IMultiPowerCableHolder;
import ihl.interfaces.IWire;
import ihl.metallurgy.constants.*;
import ihl.processing.invslots.InvSlotConsumableLiquidIHL;
import ihl.recipes.IRecipeInputFluid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableObject;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.oredict.OreDictionary;

public class IHLUtils 
{
	private static Map<String,ItemStack> ihlItemStackRegistry = new HashMap<String, ItemStack>();
	private static  final String Digits     = "(\\p{Digit}+)";
	private static  final String HexDigits  = "(\\p{XDigit}+)";
	private static   final String Exp        = "[eE][+-]?"+Digits;
	private static   final String fpRegex    =
	      ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
	       "[+-]?(" + // Optional sign character
	       "NaN|" +           // "NaN" string
	       "Infinity|" +      // "Infinity" string
	       "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
	       "(\\.("+Digits+")("+Exp+")?)|"+
	       "((" +
	        "(0[xX]" + HexDigits + "(\\.)?)|" +
	        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
	        ")[pP][+-]?" + Digits + "))" +
	       "[fFdD]?))" +
	       "[\\x00-\\x20]*");// Optional trailing "whitespace"

	
	public static void registerLocally(String name, ItemStack stack)
	{
		ihlItemStackRegistry.put(name, stack);
	}
	
	public static ItemStack getOreDictItemStack(String name)
	{
		ItemStack ore = OreDictionary.getOres(name).get(0);
		if(ore==null)return null;
		ItemStack orecopy = ore.copy();
		orecopy.stackSize=1;
		return orecopy;
	}
	
	public static boolean hasOreDictionaryEntry(String name)
	{
		return !OreDictionary.getOres(name).isEmpty();
	}
	
	public static Item getOreDictItem(String name)
	{
		return OreDictionary.getOres(name).get(0).getItem();
	}
	
	public static Block getOreDictBlock(String name)
	{
		return Block.getBlockFromItem(OreDictionary.getOres(name).get(0).getItem());
	}
	
	public static ItemStack getOreDictItemStackWithSize(String name, int size)
	{
		ItemStack ore = OreDictionary.getOres(name).get(0);
		if(ore==null)return null;
		ItemStack orecopy = ore.copy();
		orecopy.stackSize=size;
		return orecopy;
	}
	
	public static String getFirstOreDictName(ItemStack stack)
	{
		int[] arrayIDs = OreDictionary.getOreIDs(stack);
		if(arrayIDs.length>0)
		{
			return OreDictionary.getOreName(arrayIDs[0]);
		}
		return "";
	}
	
	public static ItemStack getThisModItemStack(String name)
	{
		if(ihlItemStackRegistry.get(name)!=null)
		{
			return ihlItemStackRegistry.get(name).copy();
		}
		if(GameRegistry.findItem("ihl", name)!=null)
		{
			return new ItemStack(GameRegistry.findItem("ihl", name));
		}
		else if(GameRegistry.findBlock("ihl", name)==null)
		{
			throw new IllegalArgumentException("No such item in item registry: ihl:"+name);
		}
		else
		{
			return new ItemStack(GameRegistry.findBlock("ihl", name));
		}
	}

	public static ItemStack getThisModItemStackWithSize(String name, int i) {
		if(ihlItemStackRegistry.get(name)!=null)
		{
			ItemStack stack = ihlItemStackRegistry.get(name).copy();
			stack.stackSize=i;
			return stack;
		}
		if(GameRegistry.findItem("ihl", name)!=null)
		{
			return new ItemStack(GameRegistry.findItem("ihl", name),i);
		}
		else if(GameRegistry.findBlock("ihl", name)==null)
		{
			throw new IllegalArgumentException("No such item in item registry: ihl:"+name);
		}
		else
		{
			return new ItemStack(GameRegistry.findBlock("ihl", name),i);
		}
	}
	
	public static ItemStack getOtherModItemStackWithDamage(String modname, String name, int damage, int quantity) {
		if(GameRegistry.findItem(modname, name)!=null)
		{
			return new ItemStack(GameRegistry.findItem(modname, name),quantity,damage);
		}
		else if(GameRegistry.findBlock(modname, name)==null)
		{
			return null;
		}
		else
		{
			return new ItemStack(GameRegistry.findBlock(modname, name),quantity,damage);
		}
	}
	
	public static Item getThisModItem(String name) 
	{
		if(GameRegistry.findItem("ihl", name)!=null)
		{
			return GameRegistry.findItem("ihl", name);
		}
		else if(GameRegistry.findBlock("ihl", name)==null)
		{
			throw new IllegalArgumentException("No such item in item registry: ihl:"+name);
		}
		else
		{
			return Item.getItemFromBlock(GameRegistry.findBlock("ihl", name));
		}
	}
	
	public static FluidStack getFluidStackWithSize(String name, int i) {
		if(FluidRegistry.isFluidRegistered(name))
		{
			return FluidRegistry.getFluidStack(name, i);
		}
		else
		{
			throw new IllegalArgumentException("No such fluid: "+name);
		}
	}

	public static Block getThisModBlock(String name) 
	{
		if(GameRegistry.findBlock("ihl", name)==null)
		{
			throw new IllegalArgumentException("No such block in item registry: ihl:"+name);
		}
		else
		{
			return GameRegistry.findBlock("ihl", name);
		}
	}

	public static ItemStack getThisModItemStackWithDamage(String name,
			int value) 
	{
		ItemStack stack = getThisModItemStack(name);
		stack.setItemDamage(value);
		return stack;
	}

	public static boolean adjustWireLength(ItemStack stack, int adjustBy) 
	{
		int length = getWireLength(stack);
		if(length<=0)
		{
			return true;
		}
		else
		{
			int newLength = Math.max(length+adjustBy,0);
			stack.stackTagCompound.setInteger(((IWire)stack.getItem()).getTag(),newLength);
			stack.stackTagCompound.setInteger(((IWire)stack.getItem()).getTagSecondary(),newLength);
			if(newLength==0)
			{
				return true;
			}
			return false;
		}
	}

	public static int getWireLength(ItemStack itemStack) 
	{
		return itemStack.stackTagCompound.getInteger(((IWire)itemStack.getItem()).getTag());
	}
	
	public static ItemStack getThisModWireItemStackWithLength(String name, int i) {
		if(getThisModItemStack(name)!=null)
		{
			ItemStack stack = getThisModItemStack(name);
			if(stack.getItem() instanceof IWire)
			{
				stack.stackTagCompound = new NBTTagCompound();
				stack.stackTagCompound.setInteger(((IWire)stack.getItem()).getTag(),i);
				stack.stackTagCompound.setInteger(((IWire)stack.getItem()).getTagSecondary(),i);
				return stack;
			}
			else
			{
				throw new IllegalArgumentException("ihl:"+name + " is not an instance of IWire.");
			}
		}
		else
		{
			throw new IllegalArgumentException("No such item in item registry: ihl:"+name);
		}
	}

	public static ItemStack getThisModWireItemStackWithLength(ItemStack stack1, int i) {
		ItemStack stack = stack1.copy();
		if(stack1.getItem() instanceof IWire)
		{
			stack.stackTagCompound = new NBTTagCompound();
			stack.stackTagCompound.setInteger(((IWire)stack.getItem()).getTag(),i);
			stack.stackTagCompound.setInteger(((IWire)stack.getItem()).getTagSecondary(),i);
			return stack;
		}
		else
		{
			throw new IllegalArgumentException(stack1.getUnlocalizedName() + " is not an instance of IWire.");
		}
	}
	
	public static boolean isItemsHaveSameOreDictionaryEntry(ItemStack is, ItemStack is1)
	{
		int[] odids1 = OreDictionary.getOreIDs(is);
		int[] odids2 = OreDictionary.getOreIDs(is1);
		if(odids1!=null && odids1.length>0 && odids2!=null && odids2.length>0)
		{
			for(int i1=0;i1<odids1.length;i1++)
			{
				for(int i2=0;i2<odids2.length;i2++)
				{
					if(!OreDictionary.getOreName(odids1[i1]).contains("Any") && odids1[i1]==odids2[i2])
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public static List<ItemStack> getEntryListForOre(String name) 
	{
		ArrayList<ItemStack> outputList = new ArrayList<ItemStack>();
		ArrayList<ItemStack> oreList = OreDictionary.getOres(name);
		Iterator<ItemStack> oreListIterator = oreList.iterator();
		while(oreListIterator.hasNext())
		{
			outputList.add(oreListIterator.next().copy());
		}
		return outputList;
	}

	public static ItemStack getItemStackIfExist(String name) 
	{
		if(hasOreDictionaryEntry(name))
		{
			return getOreDictItemStack(name);
		}
		else
		{
			if(ihlItemStackRegistry.get(name)!=null)
			{
				return ihlItemStackRegistry.get(name).copy();
			}
			if(GameRegistry.findItem("ihl", name)!=null)
			{
				return new ItemStack(GameRegistry.findItem("ihl", name));
			}
			else if(GameRegistry.findBlock("ihl", name)==null)
			{
				return null;
			}
			else
			{
				return new ItemStack(GameRegistry.findBlock("ihl", name));
			}
		}
	}

	public static FluidStack getFluidStackIfExist(String string, int meltingFluidAmount) 
	{
		if(FluidRegistry.isFluidRegistered(string))
		{
			return getFluidStackWithSize(string,meltingFluidAmount);
		}
		return null;
	}

	public static boolean addItemStackToInventory(EntityPlayer player, ItemStack stack) 
	{
    	ItemStack[] inv = player.inventory.mainInventory;
       	for (int i=0;i<=35;i++)
    	{
    		if(inv[i]!=null)
    		{
    			if(inv[i].getItem()==stack.getItem())
    			{
    				if(inv[i].getItemDamage()==stack.getItemDamage() && inv[i].stackSize<inv[i].getMaxStackSize())
    				{
    					inv[i].stackSize+=stack.stackSize;
    					if(inv[i].stackSize>inv[i].getMaxStackSize())
    					{
    						stack.stackSize=inv[i].stackSize-inv[i].getMaxStackSize();
    					}
    					else
    					{
    						return true;
    					}
    			    }
    			}
    		}
    		else
    		{
    			inv[i]=stack;
				return true;
    		}
    	}
		return false;
	}

	public static FluidStack getFluidStackWithSizeChemicallyPure(String name, int amount) 
	{
		FluidStack fstack = getFluidStackWithSize(name, amount);
		fstack.tag=new NBTTagCompound();
		fstack.tag.setBoolean("chemicallyPure", true);
		return fstack;
	}

	public static void removeItemStackFromOreDictionaryEntry(String orename, ItemStack itemStack) 
	{
		ArrayList<ItemStack> orelist = OreDictionary.getOres(orename);
		Iterator<ItemStack> oreListIterator = orelist.iterator();
		ItemStack odstack = null;
		while(oreListIterator.hasNext())
		{
			odstack = oreListIterator.next();
			if(odstack.getItem()==itemStack.getItem())
			{
				break;
			}
			else
			{
				odstack = null;
			}
		}
		if(odstack != null)
		{
			orelist.remove(odstack);
			IHLMod.log.debug("Stack "+odstack.getDisplayName()+" ("+odstack.toString()+")"+" removed from ore entry '"+orename+"'");
		}
	}
	
	public static void addIC2MaceratorRecipe(String input, ItemStack output)
	{
		if(Recipes.macerator.getOutputFor(getOreDictItemStack(input), false)==null)
		{
			((BasicMachineRecipeManager)Recipes.macerator).addRecipe(new RecipeInputOreDict(input), new NBTTagCompound(), true, output);
		}
		else
		{
			//IHLMod.log.info("IC2 macerator recipe for "+input+" already exist. Skipped.");
		}
	}
	
	public static void addIC2MaceratorRecipe(String input, int stacksize, ItemStack output)
	{
		if(Recipes.macerator.getOutputFor(getOreDictItemStackWithSize(input,stacksize), false)==null)
		{
			((BasicMachineRecipeManager)Recipes.macerator).addRecipe(new RecipeInputOreDict(input,stacksize), new NBTTagCompound(), true, output);
		}
		else
		{
			//IHLMod.log.info("IC2 macerator recipe for "+input+" already exist. Skipped.");
		}
	}
	
	public static void addIC2MaceratorRecipe(ItemStack input, ItemStack output)
	{
		if(Recipes.macerator.getOutputFor(input, false)==null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			Recipes.macerator.addRecipe(new RecipeInputItemStack(input), tag, output);
		}
		else
		{
			//IHLMod.log.info("IC2 macerator recipe for "+input.getDisplayName()+" already exist. Skipped.");
		}
	}
	
	public static void addIC2RollingRecipe(ItemStack input, ItemStack output)
	{
		if(Recipes.metalformerRolling.getOutputFor(input, false)==null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			Recipes.metalformerRolling.addRecipe(new RecipeInputItemStack(input), tag, output);
		}
		else
		{
			//IHLMod.log.info("IC2 metal former (rolling) recipe for "+input.getDisplayName()+" already exist. Skipped.");
		}
	}
	
	public static void addIC2RollingRecipe(String input, ItemStack output)
	{
		if(Recipes.metalformerRolling.getOutputFor(getThisModItemStack(input), false)==null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			Recipes.metalformerRolling.addRecipe(new RecipeInputOreDict(input), tag, output);
		}
		else
		{
			//IHLMod.log.info("IC2 metal former (rolling) recipe for "+input+" already exist. Skipped.");
		}
	}

	public static void addIC2ExtrudingRecipe(ItemStack input, ItemStack output)
	{
		if(Recipes.metalformerExtruding.getOutputFor(input, false)==null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			Recipes.metalformerExtruding.addRecipe(new RecipeInputItemStack(input), tag, output);
		}
		else
		{
			//IHLMod.log.info("IC2 metal former (rolling) recipe for "+input+" already exist. Skipped.");
		}
	}

	
	public static void addIC2CentrifugeRecipe(String input, ItemStack output, ItemStack output2)
	{
		if(Recipes.centrifuge.getOutputFor(getOreDictItemStack(input), false)==null)
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("minHeat", 2000);
			Recipes.centrifuge.addRecipe(new RecipeInputOreDict(input), tag, new ItemStack[] {output,output2});
		}
		else
		{
			//IHLMod.log.info("IC2 centrifuge recipe for "+input+" already exist. Skipped.");
		}
	}
	
	public static void damageItemViaNBTTag(ItemStack stack, int amount)
	{
		NBTTagCompound gtTagCompound;
		if(stack.stackTagCompound.hasKey("GT.ToolStats"))
		{
			gtTagCompound = stack.stackTagCompound.getCompoundTag("GT.ToolStats");
		}
		else
		{
			stack.stackSize--;
			return;
		}
		int damage = 0;
		int maxDamage = 0;
		if(gtTagCompound.hasKey("MaxDamage"))
		{
			maxDamage = gtTagCompound.getInteger("MaxDamage");
		}
		else
		{
			stack.stackSize--;
			return;
		}
		
		if(gtTagCompound.hasKey("Damage"))
		{
			damage = gtTagCompound.getInteger("Damage");
		}
		if(damage<maxDamage-amount)
		{
			damage+=amount;
			gtTagCompound.setInteger("Damage",damage);
			gtTagCompound.setInteger("MaxDamage",maxDamage);
			stack.stackTagCompound.setTag("GT.ToolStats", gtTagCompound);
		}
		else
		{
			stack.stackSize--;
		}
	}
	
	public static int getDamageValueViaNBTTag(ItemStack stack)
	{
		NBTTagCompound gtTagCompound = null;
		if(stack!=null && stack.stackTagCompound!=null && stack.stackTagCompound.hasKey("GT.ToolStats"))
		{
			gtTagCompound = stack.stackTagCompound.getCompoundTag("GT.ToolStats");
		}
		else
		{
			return 0;
		}
		if(gtTagCompound!=null && gtTagCompound.hasKey("Damage"))
		{
			return gtTagCompound.getInteger("Damage");
		}
		else
		{
			return 0;
		}
	}
	
	public static int getMaxDamageValueViaNBTTag(ItemStack stack)
	{
		NBTTagCompound gtTagCompound = null;
		if(stack!=null && stack.stackTagCompound!=null && stack.stackTagCompound.hasKey("GT.ToolStats"))
		{
			gtTagCompound = stack.stackTagCompound.getCompoundTag("GT.ToolStats");
		}
		else
		{
			return 0;
		}
		if(gtTagCompound!=null && gtTagCompound.hasKey("MaxDamage"))
		{
			return gtTagCompound.getInteger("MaxDamage");
		}
		else
		{
			return 0;
		}
	}
	
	public static boolean isItemStacksIsEqual(ItemStack stack1, ItemStack stack2, boolean useOreDictionary)
	{
		if(useOreDictionary && isItemsHaveSameOreDictionaryEntry(stack1,stack2))
		{
			return true;
		}
		else
		{
			if(stack1.getItemDamage()==OreDictionary.WILDCARD_VALUE || stack2.getItemDamage()==OreDictionary.WILDCARD_VALUE)
			{
				return stack1.getItem()==stack2.getItem();
			}
			else
			{
				return stack1.getItem()==stack2.getItem() && stack1.getItemDamage()==stack2.getItemDamage();
			}
		}
	}

	public static boolean isItemStacksIsEqual(ItemStack stack1, String stack2name, boolean useOreDictionary) 
	{
		return isItemStacksIsEqual(stack1, getThisModItemStack(stack2name),useOreDictionary);
	}
	
	public static boolean isIRecipeInputMatchesWithAmount(IRecipeInput input, ItemStack stack) 
	{
		if(input.matches(stack))
		{
			if(stack.getItem() instanceof IWire)
			{
				return getWireLength(stack)>=input.getAmount();
			}
			else
			{
				return stack.stackSize>=input.getAmount();
			}
		}
		return false;
	}

	public static boolean reduceItemStackAmountUsingIRecipeInput(IRecipeInput input, ItemStack stack) {
		if(stack.getItem() instanceof IWire)
		{
			return adjustWireLength(stack,-input.getAmount());
		}
		else
		{
			stack.stackSize-=input.getAmount();
			return stack.stackSize<=0;
		}
	}

	public static String getFirstOreDictNameExcludingTagAny(ItemStack stack) {
			int[] arrayIDs = OreDictionary.getOreIDs(stack);
			for(int i=0;i<arrayIDs.length;i++)
			{
				if(!OreDictionary.getOreName(arrayIDs[i]).contains("Any"))
				{
					return OreDictionary.getOreName(arrayIDs[i]);					
				}
			}
			return "";
	}
	
	public static void handleFluidSlotsBehaviour(InvSlotConsumableLiquidIHL fillInputSlot, InvSlotConsumableLiquidIHL drainInputSlot, InvSlotOutput emptyFluidItemsSlot, IFluidTank fluidTank)
	{
        MutableObject<ItemStack> output;
        if (drainInputSlot!=null && !drainInputSlot.isEmpty())
        {
            output = new MutableObject<ItemStack>();
            if(fluidTank.fill(drainInputSlot.drain(null, fluidTank.getCapacity()-fluidTank.getFluidAmount(), output, true),false)>0 && (output.getValue() == null || emptyFluidItemsSlot.canAdd(output.getValue())))
            {
            	fluidTank.fill(drainInputSlot.drain(null, fluidTank.getCapacity()-fluidTank.getFluidAmount(), output, false),true);
            	if(output.getValue()!=null)
            	{
            		emptyFluidItemsSlot.add(output.getValue());
            	}
            }
        }
        if (fillInputSlot!=null && !fillInputSlot.isEmpty())
        {
            output = new MutableObject<ItemStack>();
            if (fillInputSlot.transferFromTank(fluidTank, output, true) && (output.getValue() == null || emptyFluidItemsSlot.canAdd(output.getValue())))
            {
            	fillInputSlot.transferFromTank(fluidTank, output, false);
            	if(output.getValue()!=null)
            	{
            		emptyFluidItemsSlot.add(output.getValue());
            	}
            }
        }
	}
    public static double[] tracePlayerView(EntityLivingBase player)
    {
        float f1 = player.rotationPitch;
        float f2 = player.rotationYaw;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        if(IC2.platform.isSimulating())
        {
        	y += player.getEyeHeight();
        }
        float f3 = MathHelper.cos(-f2 * 0.01745329F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.01745329F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.01745329F);
        float f6 = MathHelper.sin(-f1 * 0.01745329F);
        float f7 = f4 * f5;
        float f9 = f3 * f5;
        double d3 = 1.0D;
        return new double[] {x+f7 * d3, y+f6 * d3, z+f9 * d3};
    }
	
    public static MovingObjectPosition returnMOPFromPlayer(EntityPlayer entityplayer, World world)
    {
        float f1 = entityplayer.rotationPitch;
        float f2 = entityplayer.rotationYaw;
        double x = entityplayer.posX;
        double y =entityplayer.posY + entityplayer.getEyeHeight();

        if (world.isRemote)
        {
            y -= entityplayer.getDefaultEyeHeight();
        }

        double z = entityplayer.posZ;
        Vec3 vec3d = Vec3.createVectorHelper(x, y, z);
        float f3 = MathHelper.cos(-f2 * 0.01745329F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.01745329F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.01745329F);
        float f6 = MathHelper.sin(-f1 * 0.01745329F);
        float f7 = f4 * f5;
        float f9 = f3 * f5;
        double d3 = 5.0D;
        Vec3 vec3d1 = vec3d.addVector(f7 * d3, f6 * d3, f9 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec3d, vec3d1, true);

        if (movingobjectposition == null)
        {
            return null;
        }

        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            return movingobjectposition;
        }
        return null;
    }
    
    public static short getFacingFromPlayerView(EntityLivingBase player, boolean ignoreSneaking)
    {
		int var6 = MathHelper.floor_double(player.rotationPitch * 4.0F / 360.0F + 0.5D) & 3;
		int var7 = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        {
        	if(var6==1)
        	{
        		return 1;
        	}
        	else if(var6==3)
        	{
        		return 0;
        	}
        	else
        	{
        		if(player.isSneaking() && !ignoreSneaking)
	        	{
	        		switch(var7)
	        		{
	        		case 0:
		        		return 3;
					case 1:
		        		return 4;
	        		case 2:
		        		return 2;
	        		case 3:
		        		return 5;
	        		default:
		        		break;
	        		}
	        	}
	        	else
	        	{
	        		switch(var7)
	        		{
	        		case 0:
		        		return 2;
	        		case 1:
		        		return 5;
	        		case 2:
		        		return 3;
	        		case 3:
		        		return 4;
	        		default:
	        			break;
	        		}
	        	}
        	}
        }
		return 3;
    }

	public static int getChainID(ItemStack itemStack) 
	{
		if(itemStack!=null && itemStack.stackTagCompound!=null && itemStack.stackTagCompound.hasKey("chainUID"))
		{
			return itemStack.stackTagCompound.getInteger("chainUID");
		}
		return -1;
	}
	
	public static List<ItemStack> convertRecipeInputToItemStackList(List<IRecipeInput> input)
	{
		Iterator<IRecipeInput> irii=input.iterator();
		List<ItemStack> output = new ArrayList<ItemStack>();
		while(irii.hasNext())
		{
			IRecipeInput iri = irii.next();
			ItemStack stack = iri.getInputs().get(0);
			stack.stackSize=iri.getAmount();
			output.add(stack);
		}
		return output;
	}
	
	public static List<FluidStack> convertRecipeInputToFluidStackList(List<IRecipeInputFluid> input) {
		Iterator<IRecipeInputFluid> irii=input.iterator();
		List<FluidStack> output = new ArrayList<FluidStack>();
		while(irii.hasNext())
		{
			IRecipeInputFluid iri = irii.next();
			FluidStack stack = iri.getInputs().get(0);
			stack.amount=iri.getAmount();
			output.add(stack);
		}
		return output;
	}
	
	public static int[] decodeXYZ(long longNumber)
	{
		return new int[] {(int) ((longNumber>>30) & 0xfff)-256,(int) ((longNumber>>15) & 0xfff)-256,(int) (longNumber & 0xfff)-256};
	}
	
	public static long encodeXYZ(int x,int y,int z)
	{
		return (x+256L)<<30|((y+256L)<<15)|(z+256L);
	}
	
	public static int reduceVariableByAbsoluteValue(int variable)
	{
		if(variable==0)
		{
			return 0;
		}
		else if(variable<0)
		{
			return variable+1;
		}
		else
		{
			return variable-1;
		}
	}
	
	public static long getXYZHash(int x,int y,int z)
	{
	    int sign_bits = (x & 0x80000000) >> 29 | (y & 0x80000000) >> 30 | (z & 0x80000000) >> 31;
		return (long)x<<31 ^ (long)y<<17 ^ (long)z<<3 ^ sign_bits;
	}
	
	public static String trim(String str)
    {
        int len = str.length();
        int start;
        char c;
        for (start = 0; start < len; ++start)
        {
            c = str.charAt(start);
            if (c > 32 && c != 65279)
            {
                break;
            }
        }
        int end;
        for (end = len - 1; end >= start; --end)
        {
            c = str.charAt(end);
            if (c > 32 && c != 65279)
            {
                break;
            }
        }
        return start <= 0 && end >= len - 1 ? str : str.substring(start, end + 1);
    }

	public static int getAmountOf(ItemStack is) 
	{
		if(is.getItem() instanceof IWire)
		{
			return getWireLength(is);
		}
		else
		{
			return is.stackSize;
		}
	}

	public static ItemStack getWireItemStackCopyWithLengthMultiplied(ItemStack stack, int multiplier) 
	{
		ItemStack out = stack.copy();
		adjustWireLength(out,getWireLength(stack)*(multiplier-1));
		return out;
	}

	public static ItemStack getUninsulatedWire(String material, int length, int transverseSection) {
		ItemStack is = getThisModItemStack("copperWire");
		is.stackTagCompound=new NBTTagCompound();
		is.stackTagCompound.setBoolean("firstConnection",false);
    	is.stackTagCompound.setInteger("fullLength", length);
    	is.stackTagCompound.setInteger("length", length);
    	is.stackTagCompound.setBoolean("firstConnection", false);
    	is.stackTagCompound.setString("material", material);
    	is.stackTagCompound.setInteger("transverseSection",transverseSection);
		return is;
	}
	
	public static ItemStack getInsulatedWire(String material, int length, int transverseSection, String insulationMaterial, int insulationThickness) {
		ItemStack is = getUninsulatedWire(material, length, transverseSection);
    	is.stackTagCompound.setString("insulationMaterial",insulationMaterial);
    	is.stackTagCompound.setInteger("insulationThickness",insulationThickness);
    	is.stackTagCompound.setInteger("maxVoltage", getInsulationMaxVoltage(insulationMaterial, insulationThickness));
		return is;
	}
	
	public static long getResistance(NBTTagCompound cable) 
    {
		String material = cable.getString("material");
		int transverseSection = cable.getInteger("transverseSection");
		return ElectricConductor.getResistivity(material)*100L/transverseSection;
	}
	
	public static int getInsulationMaxVoltage(String insulationMaterial, int insulationThickness)
	{
		return Math.min(Insulation.getMaxVoltagePermm(insulationMaterial)*insulationThickness/10,Insulation.getMaxVoltageCap(insulationMaterial));
	}

	public static ItemStack getItemStackWithTag(String unLocalizedName, String tag, int tagValue) {
		ItemStack stack = IHLUtils.getThisModItemStack(unLocalizedName);
		if(stack.stackTagCompound==null)
		{
			stack.stackTagCompound=new NBTTagCompound();
		}
		stack.stackTagCompound.setInteger(tag, tagValue);
		return stack;
	}

	public static boolean isSegmentInsideAABB(AxisAlignedBB collisionBox, double posX, double posY, double posZ, double posX2, double posY2, double posZ2) 
	{
		if(isInsideofBoundingBox(collisionBox,(float)posX,(float)posY,(float)posZ)||isInsideofBoundingBox(collisionBox,(float)posX2,(float)posY2,(float)posZ2))
		{
			return true;
		}
		else
		{
			double minX=Math.min(posX, posX2);
			double maxX=Math.max(posX, posX2);
			double minY=Math.min(posY, posY2);
			double maxY=Math.max(posY, posY2);
			double minZ=Math.min(posZ, posZ2);
			double maxZ=Math.max(posZ, posZ2);
			return !(maxX<collisionBox.minX || minX>collisionBox.maxX ||
					 maxY<collisionBox.minY || minY>collisionBox.maxY ||
					 maxZ<collisionBox.minZ || minZ>collisionBox.maxZ);
		}
	}
	
	public static boolean isInsideofBoundingBox(AxisAlignedBB bb, float xi, float yi,	float zi) 
	{
		return bb.maxX>xi && bb.minX<xi && bb.maxY>yi && bb.minY<yi && bb.maxZ>zi && bb.minZ<zi;
	}

	public static boolean isBlockCanBeReplaced(World world, int x, int y, int z) 
	{
    	Block block = world.getBlock(x, y, z);
		if(block==Blocks.air || block.isAir(world, x, y, z) || block == Blocks.vine || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.snow_layer || block == Blocks.snow)
		{
			return true;
		}
		return false;
	}

	public static void removeChains(IEnergyNetNode te, World world) 
	{
		Set<NBTTagCompound> cableList = te.getCableList();
		Iterator<NBTTagCompound> cli = cableList.iterator();
		while(cli.hasNext())
		{
			NBTTagCompound c = cli.next();
			cli.remove();
			IHLMod.enet.removeCableEntities(c);
			ItemStack is = IHLUtils.getThisModItemStack("copperWire");
			is.stackTagCompound=c;
			double[] pps = te.getPortPos(null);
			EntityItem eitem = new EntityItem(world, pps[0], pps[1], pps[2], is);
			world.spawnEntityInWorld(eitem);
			removeChain(c,null);
		}
		if(te.getGridID()!=-1)
		{
			IHLMod.enet.splitGrids(te.getGridID(), te);
		}
		cableList.clear();
	}
	
	public static void removeChain(NBTTagCompound c, IEnergyNetNode excludeNode) 
	{
   		int x = c.getInteger("connectorX1");
   		int y = c.getInteger("connectorY1");
   		int z = c.getInteger("connectorZ1");
   		int t2DimensionId = c.getInteger("connectorDimensionId1");
   		short facing2 = c.getShort("connectorFacing1");
   		TileEntity t2 = MinecraftServer.getServer().worldServerForDimension(t2DimensionId).getTileEntity(x, y, z);
   		IEnergyNetNode te2;
		if(t2 instanceof IMultiPowerCableHolder)
   		{
   			te2 = ((IMultiPowerCableHolder)t2).getEnergyNetNode(facing2);
   		}
   		else if(t2 instanceof IEnergyNetNode)
   		{
       		te2 = (IEnergyNetNode)t2;
   		}
   		else
   		{
   			return;
   		}
		if(excludeNode!=te2)
		{
			te2.remove(c);	
		}
   		x = c.getInteger("connectorX");
   		y = c.getInteger("connectorY");
   		z = c.getInteger("connectorZ");
   		t2DimensionId = c.getInteger("connectorDimensionId");
   		facing2 = c.getShort("connectorFacing");
   		t2 = MinecraftServer.getServer().worldServerForDimension(t2DimensionId).getTileEntity(x, y, z);
		if(t2 instanceof IMultiPowerCableHolder)
   		{
   			te2 = ((IMultiPowerCableHolder)t2).getEnergyNetNode(facing2);
   		}
   		else if(t2 instanceof IEnergyNetNode)
   		{
       		te2 = (IEnergyNetNode)t2;
   		}
   		else
   		{
   			return;
   		}
		if(excludeNode!=te2)
		{
			te2.remove(c);	
		}
	}
	
	public static boolean isPlayerLookingAt(EntityLivingBase player, AxisAlignedBB aabb)
	{
		double[] pView = tracePlayerView(player);
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        if(IC2.platform.isSimulating())
        {
        	y += player.getEyeHeight();
        }
		if(
		isLineSegmentInterseptRectangle(x,y,pView[0],pView[1],aabb.minX,aabb.minY,aabb.maxX,aabb.maxY) &&
		isLineSegmentInterseptRectangle(x,z,pView[0],pView[2],aabb.minX,aabb.minZ,aabb.maxX,aabb.maxZ) &&
		isLineSegmentInterseptRectangle(z,y,pView[2],pView[1],aabb.minZ,aabb.minY,aabb.maxZ,aabb.maxY))
		{
			return true;
		}
		return false;
	}

	public static boolean isLineSegmentInterseptRectangle(double sx0_1,double sy0_1,double sx1_1,double sy1_1,double rx0_1,double ry0_1,double rx1_1,double ry1_1)
	{
		double sx0=sx0_1;
		double sy0=sy0_1;
		double sx1=sx1_1;
		double sy1=sy1_1;
		double rx0=rx0_1;
		double ry0=ry0_1;
		double rx1=rx1_1;
		double ry1=ry1_1;
		if(sx1_1<sx0_1)
		{
			sx0=sx1_1;
			sy0=sy1_1;
			sx1=sx0_1;
			sy1=sy0_1;
		}
		if(rx1_1<rx0_1)
		{
			rx0=rx1_1;
			ry0=ry1_1;
			rx1=rx0_1;
			ry1=ry0_1;
		}
		double ay = (sy1-sy0)/(sx1-sx0);
		double by = sy1-ay*sx1;
		
		double ax = (sx1-sx0)/(sy1-sy0);
		double bx = sx1-ax*sy1;

		double maxx0 = Math.max(sx0, rx0);
		double minx1 = Math.min(sx1, rx1);
		double maxy0 = Math.max(sy0, ry0);
		double miny1 = Math.min(sy1, ry1);
		double y0 = ay*maxx0+by;
		double y1 = ay*minx1+by;
		
		double x0 = ax*maxy0+bx;
		double x1 = ax*miny1+bx;
		return (ry0<=y0 && ry1>=y0)||(ry0<=y1 && ry1>=y1)||(rx0<=x0 && rx1>=x0)||(rx0<=x1 && rx1>=x1);
	}

	public static float parseFloatSafe(String string, float useSafeValue)
	{
  	  if (Pattern.matches(fpRegex, string))
	      return Float.valueOf(string);
	  else {
		  return useSafeValue;
	  }
	}
	
	public static int parseIntSafe(String string, int useSafeValue)
	{
  	  if (Pattern.matches(fpRegex, string))
	      return Integer.valueOf(string);
	  else {
		  return useSafeValue;
	  }
	}


	public static boolean isBlockRegisteredInOreDictionaryAs(Block block, String string) {
		Iterator<ItemStack> isoi = OreDictionary.getOres(string).iterator();
		while(isoi.hasNext())
		{
			if(Block.getBlockFromItem(isoi.next().getItem())==block)
			{
				return true;
			}
		}
		return false;
	}    
	
	public static void setBlockAndTileEntityRaw(World world, int x, int y, int z, Block block, TileEntity te)
	{
		Chunk chunk = world.getChunkProvider().provideChunk(x>>4, z>>4);
		ExtendedBlockStorage[] ebsA = chunk.getBlockStorageArray();
		ExtendedBlockStorage ebs = ebsA[y>>4];
		if(ebs==null)
		{
			ebs = new ExtendedBlockStorage(y, true);
			ebsA[y>>4] = ebs;
		}
		setBlockRaw(ebs,x & 15,y & 15,z & 15,block);
		te.xCoord=x;
		te.yCoord=y;
		te.zCoord=z;
		te.setWorldObj(world);
		chunk.addTileEntity(te);
	}
	
	public static void setBlockRaw(ExtendedBlockStorage ebs, int x, int y, int z, Block block)
    {
        int l = ebs.blockLSBArray[y << 8 | z << 4 | x] & 255;

        if (ebs.blockMSBArray != null)
        {
            l |= ebs.blockMSBArray.get(x, y, z) << 8;
        }

        Block block1 = Block.getBlockById(l);

        if (block1 != Blocks.air)
        {
            --ebs.blockRefCount;

            if (block1.getTickRandomly())
            {
                --ebs.tickRefCount;
            }
        }

        if (block != Blocks.air)
        {
            ++ebs.blockRefCount;

            if (block.getTickRandomly())
            {
                ++ebs.tickRefCount;
            }
        }

        int i1 = Block.getIdFromBlock(block);
        ebs.blockLSBArray[y << 8 | z << 4 | x] = (byte)(i1 & 255);

        if (i1 > 255)
        {
            if (ebs.blockMSBArray == null)
            {
                ebs.blockMSBArray = new NibbleArray(ebs.blockLSBArray.length, 4);
            }

            ebs.blockMSBArray.set(x, y, z, (i1 & 3840) >> 8);
        }
        else if (ebs.blockMSBArray != null)
        {
            ebs.blockMSBArray.set(x, y, z, 0);
        }
    }

	public static void dumpToFile(Set<String> unlocalisedNames, String filename) {
		try {
			OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(getFile(filename)), "UTF-8");
			BufferedWriter writer = new BufferedWriter(osWriter);
			for(String string:unlocalisedNames)
			{
				writer.append(string);
				writer.newLine();
			}
			writer.close();
	        osWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    private static File getFile(String filename)
    {
        File folder = new File(IHLMod.proxy.getMinecraftDir(), "logs");
        folder.mkdirs();
        return new File(folder, filename);
    }

}
