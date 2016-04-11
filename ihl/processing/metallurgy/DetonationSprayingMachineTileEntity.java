package ihl.processing.metallurgy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.ContainerBase;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot.Access;
import ic2.core.network.NetworkManager;
import ihl.processing.chemistry.ApparatusProcessableInvSlot;
import ihl.recipes.RecipeOutputItemStack;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;

public class DetonationSprayingMachineTileEntity extends TileEntityInventory implements IHasGui, INetworkTileEntityEventListener
{
	protected static final UniversalRecipeManager recipeManager = new UniversalRecipeManager("detonationsprayingmachine");
    public final ApparatusProcessableInvSlot input;
	private AudioSource explosion;
    
	public DetonationSprayingMachineTileEntity() {
		super();
		this.input = new ApparatusProcessableInvSlot(this, "input", 0, Access.IO, 3, 64);
	}
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		return fields;
    }
	
    @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
    }

    @Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
    }
    
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("cannonBronze");
	}
     
	@Override
	public String getInventoryName() {
		return "detonationSprayingMachine";
	}
	
    private int mX()
	{
		switch(this.getFacing())
		{
		case 4:
		return -1;
		case 5:
		return 1;
		default:
		return 0;
		}
	}
	
	private int mZ()
	{
		switch(this.getFacing())
		{
		case 3:
		return 1;
		case 2:
		return -1;
		case 4:
		return 0;
		case 5:
		return 0;
		default:
		return -1;
		}
	}
	
	private short getFacingFromXZ(int x, int z)
	{
		switch(x)
		{
			case -1:
				return (short)4;
			case 1:
				return (short)5;
			default:
				switch(z)
				{
				case 1:
					return (short)3;
				case -1:
					return (short)2;
				default:
					return (short)2;
				}
		}
	}
	

	@Override
	public void onNetworkEvent(int event) 
	{
		worldObj.playSoundEffect(xCoord, yCoord, zCoord, "random.explode", 4.0F, 1.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new DetonationSprayingMachineGui(new DetonationSprayingMachineContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new DetonationSprayingMachineContainer(player, this);
	}

	@Override
	public void onGuiClosed(EntityPlayer player) {}

    public boolean canOperate()
    {
    	return getOutput()!=null;
    }
    
    public UniversalRecipeOutput getOutput()
    {
    	return DetonationSprayingMachineTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }

	public List[] getInput()
	{
		return new List[] {null,this.input.getItemStackList()};
	}
	
    @Override
	public void onLoaded()
    {
        super.onLoaded();
        if (IC2.platform.isRendering() && this.explosion==null)
        {
        	this.explosion = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/MinerOp.ogg",false,false, 0.5F);
        }
    }
    
	
	public void operate() 
	{//diamond file max durability - 128000
	 //file tags - "GT.ToolStats"->"MaxDamage" & "Damage"
		if(this.canOperate())
		{
			List<RecipeOutputItemStack> output1 = DetonationSprayingMachineTileEntity.recipeManager.getOutputFor(getInput(), false, false).getItemOutputs();
			ItemStack resultStack = output1.get(0).itemStack.copy();
			resultStack.stackSize=this.input.get(0).stackSize;
			if(resultStack.stackTagCompound==null)
			{
				resultStack.stackTagCompound=this.input.get(0).stackTagCompound;
			}
			if(resultStack.stackTagCompound!=null && resultStack.stackTagCompound.hasKey("GT.ToolStats"))
			{
				NBTTagCompound gtTagCompound = resultStack.stackTagCompound.getCompoundTag("GT.ToolStats");
				if(gtTagCompound!=null && gtTagCompound.hasKey("MaxDamage"))
				{
					int maxDamage = gtTagCompound.getInteger("MaxDamage");
					int damage = 0;
					if(gtTagCompound.hasKey("Damage"))
					{
						damage = gtTagCompound.getInteger("Damage");
					}
					if(damage<maxDamage-400)
					{
						damage+=400;
						if(maxDamage<128000)
						{
							int dd = (128000-maxDamage)*3/4;
							maxDamage+=dd;
						}
						gtTagCompound.setInteger("Damage",damage);
						gtTagCompound.setInteger("MaxDamage",maxDamage);
						resultStack.stackTagCompound.setTag("GT.ToolStats", gtTagCompound);
					}
					else
					{
						resultStack=null;
					}
				}
			}
			this.input.put(0,resultStack);
			this.input.consume(1,1);
			this.input.consume(2,1);
	        ExplosionIC2 explosion = new ExplosionIC2(worldObj, null, this.xCoord+0.5D, this.yCoord+0.5D, this.zCoord+0.5D, 0.5F, 0.3F, ExplosionIC2.Type.Normal, null, 0);
	        explosion.doExplosion();
            IC2.network.get().initiateTileEntityEvent(this, 0, true);
		}
	}
	
	public static void addRecipe(ItemStack input, ItemStack output) 
	{
		List<IRecipeInput> dsmInputs1 = new ArrayList();
		dsmInputs1.add(new RecipeInputItemStack(input));
		dsmInputs1.add(new RecipeInputOreDict("dustDiamond"));
		dsmInputs1.add(new RecipeInputOreDict("dustGunpowder"));
		recipeManager.addRecipe(new UniversalRecipeInput(null, dsmInputs1), new UniversalRecipeOutput(null,Arrays.asList(new ItemStack[] {output}),1));
	}
	
	public static void addRecipe(UniversalRecipeInput input, UniversalRecipeOutput output) 
	{
		recipeManager.addRecipe(input, output);
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