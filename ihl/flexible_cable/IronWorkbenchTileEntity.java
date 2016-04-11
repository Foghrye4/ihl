package ihl.flexible_cable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot.Access;
import ihl.IHLMod;
import ihl.interfaces.IWire;
import ihl.items_blocks.FlexibleCableItem;
import ihl.processing.chemistry.GaedesMercuryRotaryPumpTileEntity;
import ihl.processing.metallurgy.GasWeldingStationTileEntity;
import ihl.recipes.IronWorkbenchRecipe;
import ihl.recipes.RecipeInputDie;
import ihl.recipes.RecipeInputObjectInstance;
import ihl.utils.IHLUtils;

public class IronWorkbenchTileEntity extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener, INetworkTileEntityEventListener{

	public static List<IronWorkbenchRecipe> recipes = new ArrayList();
	public int progress;
	public int currentSlot=-1;
	public final int maxProgress;
    public final InvSlotTool tools;
    public final InvSlotWorkspaceElement workspaceElements;
    public final InvSlotProcessableIronWorkbench inputMaterial;
    public final InvSlotOutputInProgress output;
	public boolean isGuiScreenOpened=false;
	private boolean startProcess=false;
	private boolean outputDefined=false;
	private EntityPlayer crafter;
	public ContainerBase<?> container;
	private Map<Integer, IronWorkbenchRecipe> slotRecipeMap = new HashMap();
    
    public IronWorkbenchTileEntity()
    {
    	this.maxProgress=80;
    	this.workspaceElements = new InvSlotWorkspaceElement(this, "workspaceElements", 3, Access.NONE, 6);
    	this.tools=new InvSlotTool(this, "tools", 0, Access.IO, 12);
    	this.inputMaterial=new InvSlotProcessableIronWorkbench(this, "input", 1, Access.IO, 12);
    	this.output=new InvSlotOutputInProgress(this, "output", 2, 18);
    }
    
    public static void addRecipe(IronWorkbenchRecipe recipe)
    {
    	IronWorkbenchTileEntity.recipes.add(recipe);
    }
    
	@Override
	public String getInventoryName() 
	{
		return "ironWorkbench";
	}
	

	@Override
    public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("ironWorkbench");
	}
    
	@Override
	public void updateEntityServer()
	{
		if(this.isGuiScreenOpened)
		{
			if(this.output.isEmpty() && !outputDefined)
			{
				this.workspaceElements.reset();
				Iterator<IronWorkbenchRecipe> iwri=IronWorkbenchTileEntity.recipes.iterator();
				while(iwri.hasNext())
				{
					IronWorkbenchRecipe recipe = iwri.next();
					if(recipe.isCanBeCrafted(this.tools.getItemStackList(), this.inputMaterial.getItemStackList(), this.workspaceElements.getItemStackList()))
					{
						if(recipe.workspaceElements==null || recipe.workspaceElements.isEmpty() || this.workspaceElements.containsAndCanUse(recipe.workspaceElements))
						{
							List<ItemStack> newOutputs = recipe.outputs;
							for(IRecipeInput rinput:recipe.tools)
							{
								if(rinput instanceof RecipeInputDie)
								{
									newOutputs = ((RecipeInputDie)rinput).transformOutput(this.getMatchedItemStack(rinput),recipe.outputs);
								}
							}
							int slot = this.output.put(newOutputs);
							if(slot<0)break;
							slotRecipeMap.put(slot, recipe);
							this.startProcess=false;
						}
					}
				}
				List<ItemStack> processingMaterials = new ArrayList<ItemStack>();
				for(int i=0; i<this.inputMaterial.size();i++)
				{
					ItemStack stack = this.inputMaterial.get(i);
					if(stack!=null && stack.getItem() instanceof IWire)
					{
						if(stack.stackTagCompound==null)
						{
							stack.stackTagCompound=new NBTTagCompound();
						}
						int fullLength = this.getFullLengthOfSameWires(stack);
						List<RecipeInputObjectInstance> list = this.getListOfSameWires(stack);
						ItemStack result = stack.copy();
						result.stackTagCompound.setInteger("length", fullLength);
						result.stackTagCompound.setInteger("fullLength", fullLength);
						IronWorkbenchRecipe recipe = new IronWorkbenchRecipe(null, list, Arrays.asList(new ItemStack[] {result}));
						int slot = this.output.put(recipe.outputs);
						if(slot<0)break;
						slotRecipeMap.put(slot, recipe);
						this.startProcess=false;
						break;
					}
				}
				outputDefined=true;
			}
			else if(!this.output.isEmpty())
			{
				List<Integer> crafterEmptyInventorySlotsList = getCrafterEmptyInventorySlotsList();
					if(startProcess && crafterEmptyInventorySlotsList.size()>=this.slotRecipeMap.get(currentSlot).outputs.size())
					{
						if(++this.progress>=this.maxProgress)
						{
							IronWorkbenchRecipe crecipe = this.slotRecipeMap.get(currentSlot);
							List<ItemStack> opts = this.output.getRecipeOutputs(currentSlot);
							int multiplier = this.inputMaterial.getMultiplier(crecipe.materials);
							Iterator<ItemStack> optsi = opts.iterator();
							Iterator<Integer> emptySlotsIterator = crafterEmptyInventorySlotsList.iterator();
							while(optsi.hasNext())
							{
								int slot = emptySlotsIterator.next();
								ItemStack stack = optsi.next();
								this.crafter.inventory.mainInventory[slot]=stack.copy();
								if(stack.getItem() instanceof IWire)
								{
									System.out.println(multiplier);
									this.crafter.inventory.mainInventory[slot]=IHLUtils.getWireItemStackCopyWithLengthMultiplied(stack,multiplier);
								}
								else
								{
									this.crafter.inventory.mainInventory[slot]=stack.copy();
									this.crafter.inventory.mainInventory[slot].stackSize*=multiplier;
								}
							}
          					this.crafter.inventoryContainer.detectAndSendChanges();
          					this.inputMaterial.substract(crecipe.materials, multiplier);
							this.tools.damage(crecipe.tools);
							if(!crecipe.workspaceElements.isEmpty())
							{
								this.workspaceElements.use(crecipe.workspaceElements);
							}
							this.resetOutput();
						}
					}
			}
		}
	}

	private ItemStack getMatchedItemStack(IRecipeInput rinput) 
	{
		for(ItemStack tool:this.tools.getItemStackList())
		{
			if(rinput.matches(tool))
			{
				return tool;
			}
		}
		for(ItemStack material:this.inputMaterial.getItemStackList())
		{
			if(rinput.matches(material))
			{
				return material;
			}
		}

		return null;
	}

	private List<Integer> getCrafterEmptyInventorySlotsList()
	{
		List<Integer> list = new ArrayList<Integer>();
		if(this.crafter!=null)
		{
			for (int var1 = 0; var1 < this.crafter.inventory.mainInventory.length; ++var1)
        	{
            	if (this.crafter.inventory.mainInventory[var1] == null)
            	{
            		list.add(var1);
            	}
        	}
		}
		return list;
	}
	
    private List<RecipeInputObjectInstance> getListOfSameWires(ItemStack stack1) {
    	List<RecipeInputObjectInstance> list = new ArrayList();
		for(int i=0; i<this.inputMaterial.size();i++)
		{
			ItemStack stack = this.inputMaterial.get(i);
			if(stack!=null && ((IWire)stack1.getItem()).isSameWire(stack1, stack))
			{
				list.add(new RecipeInputObjectInstance(stack));
			}
		}
    	return list;
	}

	private int getFullLengthOfSameWires(ItemStack stack1) {
    	int fullLength=0;
		for(int i=0; i<this.inputMaterial.size();i++)
		{
			ItemStack stack = this.inputMaterial.get(i);
			if(stack!=null && ((IWire)stack1.getItem()).isSameWire(stack1, stack))
			{
				fullLength += IHLUtils.getWireLength(stack);
			}
		}
    	return fullLength;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new IronWorkbenchGui(new IronWorkbenchContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		resetOutput();
		this.isGuiScreenOpened=true;
		this.crafter=player;
		container = new IronWorkbenchContainer(player, this);
		return container;
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) 
	{
		this.isGuiScreenOpened=false;
	}

	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		if(event==16)
		{
			this.isGuiScreenOpened=false;
			this.crafter=null;
			this.container=null;
			return;
		}
		for(int i=event;i>=0;i--)
		{
			if(this.slotRecipeMap.containsKey(i))
			{
				if(!this.slotRecipeMap.get(i).isCanBeCrafted(this.tools.getItemStackList(), this.inputMaterial.getItemStackList(), this.workspaceElements.getItemStackList()))
				{
					resetOutput();
				}
				else
				{
					this.currentSlot=i;
					this.startProcess=true;
					return;
				}
			}
		}
	}

	public void resetOutput() 
	{
		this.output.clear();
		this.slotRecipeMap.clear();
		this.progress=0;
		this.startProcess=false;
		this.currentSlot=-1;
		this.outputDefined=false;
	}
	

	public void dropContents() {
		for(int i=0;i<this.tools.size();i++)
		{
			if(this.tools.get(i)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.tools.get(i)));
		}
		for(int i=0;i<this.inputMaterial.size();i++)
		{
			if(this.inputMaterial.get(i)!=null)this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord, this.yCoord+1, this.zCoord, this.inputMaterial.get(i)));
		}
	}

	@Override
	public void onNetworkEvent(int event) 
	{	

	}

    public int gaugeProgressScaled(int i)
    {
        return this.progress * i / this.maxProgress;
    }
    
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }

	public static void removeRecipeByOutput(List<ItemStack> recipeOutputsItems) 
	{
		Iterator<IronWorkbenchRecipe> ri = recipes.iterator();
		while(ri.hasNext())
		{
			IronWorkbenchRecipe recipe = ri.next();
			boolean removeEntry=false;
			Iterator<ItemStack> roi = recipe.outputs.iterator();
			while(roi.hasNext())
			{
				if(IHLUtils.isItemStacksIsEqual(recipeOutputsItems.get(0), roi.next(), true))
				{
					removeEntry=true;
				}
			}
			if(removeEntry)
			{
				ri.remove();
			}
		}

	}

	public static void removeRecipeByInput(List<IRecipeInput> recipeInputsTools1,List<IRecipeInput> recipeInputsItems1,List<ItemStack> recipeInputsMachines) {
		List<ItemStack> recipeInputsTools = IHLUtils.convertRecipeInputToItemStackList(recipeInputsTools1);
		List<ItemStack> recipeInputsItems = IHLUtils.convertRecipeInputToItemStackList(recipeInputsItems1);
		Iterator<IronWorkbenchRecipe> ri = recipes.iterator();
		while(ri.hasNext())
		{
			IronWorkbenchRecipe recipe = ri.next();
			if(recipe.isCanBeCrafted(recipeInputsTools, recipeInputsItems, recipeInputsMachines))
			{
				ri.remove();
			}
		}

	}

}
