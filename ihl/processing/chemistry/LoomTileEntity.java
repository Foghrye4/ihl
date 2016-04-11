package ihl.processing.chemistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IRecipeInput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.processing.invslots.IHLInvSlotOutput;
import ihl.recipes.RecipeInputWire;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LoomTileEntity extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener
{     
	protected static UniversalRecipeManager recipeManager = new UniversalRecipeManager("loom");
	public short progress;
	protected short operationLength=200;
    public final ApparatusProcessableInvSlot input;
    public final IHLInvSlotOutput output;
	boolean isGuiScreenOpened=false;
	
	public LoomTileEntity()
	{
		super();
		input = new ApparatusProcessableInvSlot(this, "input", 0, Access.IO, 1, 64);
		output = new IHLInvSlotOutput(this, "output", 1, 1);
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
	
    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
        if (this.canOperate()  && this.isGuiScreenOpened)
        {
            this.setActive(true);

            if (this.progress == 0)
            {
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
            }
            ++this.progress;            
            if (this.progress >= this.operationLength)
            {
                this.operate();
                this.progress = 0;
                IC2.network.get().initiateTileEntityEvent(this, 2, true);
            }
        }
        else
        {
            if (this.progress != 0 && this.getActive())
            {
                IC2.network.get().initiateTileEntityEvent(this, 1, true);
            }
            if (!this.canOperate())
            {
                this.progress = 0;
            }
            this.setActive(false);
        }
    }

	@Override
	public String getInventoryName() {
		return "Loom";
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("loom");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new LoomGui(new LoomContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		this.isGuiScreenOpened=true;
		return new LoomContainer(player, this);
	}
	
	public void operate() 
	{
		List<IRecipeInput> input1 = LoomTileEntity.recipeManager.getRecipeInput(getInput()).getItemInputs();
		List output1 = LoomTileEntity.recipeManager.getOutputFor(getInput(), false, false).getItemOutputs();
		this.output.add(output1);
		if(input1.get(0) instanceof RecipeInputWire)
		{
			int fiberLength = input1.get(0).getAmount();
			boolean isFiberConsumed = IHLUtils.adjustWireLength(this.input.get(), -fiberLength);
			if(isFiberConsumed)
			{
				this.input.put(null);
			}
		}
		else
		{
			this.input.consume(0, input1.get(0).getAmount());
		}
	}
	
	public List[] getInput()
	{
		return new List[] {null,Arrays.asList(new ItemStack[] {input.get()})};
	}
	
	public boolean canOperate()
	{
		if(LoomTileEntity.recipeManager.getOutputFor(getInput(), false, false)==null) return false;
		List output1 = LoomTileEntity.recipeManager.getOutputFor(getInput(), false, false).getItemOutputs();
		return this.output.canAdd(output1);
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) {}
	
	public static void addRecipe(ItemStack input, ItemStack output) 
	{
		if(input==null || output==null) throw new NullPointerException();
		recipeManager.addRecipe(new UniversalRecipeInput(null,Arrays.asList(new ItemStack[] {input})), new UniversalRecipeOutput(null, Arrays.asList(new ItemStack[] {output}),20));
	}
	
    public int gaugeProgressScaled(int i)
    {
        return this.progress * i / this.operationLength;
    }
    
	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		switch(event)
		{
		case 0:
			this.isGuiScreenOpened=false;
			break;
		}	
	}

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }

}
