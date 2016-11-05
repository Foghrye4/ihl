package ihl.processing.metallurgy;

import java.util.Arrays;
import java.util.Map;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ihl.IHLMod;
import ihl.processing.chemistry.GaedesMercuryRotaryPumpTileEntity;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.tunneling_shield.IMultiBlock;
import ihl.tunneling_shield.MultiBlockSpacerBlock;
import ihl.utils.IHLFluidTank;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VacuumInductionMeltingFurnaceTileEntity extends MachineBaseTileEntity implements INetworkClientTileEntityEventListener, IMultiBlock{

	protected static UniversalRecipeManager recipeManager = new UniversalRecipeManager("vacuuminductionmeltingfurnace");
	public final IHLFluidTank fluidTank = new IHLFluidTank(864);
	public boolean vacuumPumpConnected=false;
	private GaedesMercuryRotaryPumpTileEntity lastGMRP;
	private int minX,minY,minZ,maxX,maxY,maxZ;
	
	public VacuumInductionMeltingFurnaceTileEntity()
	{
		super(1);
	}
	
	@Override
	public void setFacing(short facing1)
	{
		short facing2 = (short) Math.max(facing1, 2);
		super.setFacing(facing2);
		switch(facing2)
		{
			case 0:
				setupMultiblockStructure(-1,-2,0);
    			break;
			case 1:
				setupMultiblockStructure(-1,0,0);
    			break;
			case 2:
				setupMultiblockStructure(-1,0,-2);
    			break;
			case 3:
				setupMultiblockStructure(-1,0,0);
    			break;
			case 4:
				setupMultiblockStructure(-2,0,-1);
    			break;
			case 5:
				setupMultiblockStructure(0,0,-1);
    			break;
		}
	}
	
	@Override
	public String getStartSoundFile() {
		return null;
	}

	@Override
	public String getLoopSoundFile() {
		return null;
	}

	@Override
	public String getStopSoundFile() {
		return null;
	}

	@Override
	public String getInventoryName() {
		return "VacuumInductionMeltingFurnace";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new VacuumInductionMeltingFurnaceGui(new VacuumInductionMeltingFurnaceContainer(player, this));
	}

	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new VacuumInductionMeltingFurnaceContainer(player, this);
	}
	
	@Override
	public void operate() 
	{
    	ItemStack content = this.input.get();
    	if(content!=null)
    	{
    		UniversalRecipeOutput routput = recipeManager.getOutputFor(null, Arrays.asList(new ItemStack [] {content}),false, false);
    		if(routput!=null && !routput.getFluidOutputs().isEmpty())
    		{
				FluidStack fluidStack = routput.getFluidOutputs().get(0).copy();
				fluidStack.amount *= content.stackSize;
				this.input.clear();
				this.fluidTank.fill(fluidStack, true);
				useVacuumPump();
    		}
    	}
	}
	
	@Override
    public boolean canOperate()
    {
    	return checkSpecialConditionVacuumPump() && getOutput()!=null;
    }
	
	private void useVacuumPump() 
	{
		if(lastGMRP!=null && !lastGMRP.isInvalid() && lastGMRP.ready)
		{
			lastGMRP.ready=false;
		}
	}

	private boolean checkSpecialConditionVacuumPump() 
	{
		if(lastGMRP!=null && !lastGMRP.isInvalid() && lastGMRP.ready)
		{
			this.vacuumPumpConnected=true;
			return true;
		}
		int[] xz = new int [] {0,1,1,0,-1,-1,0};
		for(int i0=-1;i0<=0;i0++)
		{
			for(int i=0;i<xz.length-1;i++)
			{
				TileEntity te = worldObj.getTileEntity(xCoord+xz[i], yCoord+i0, zCoord+xz[i+1]);
				if(te instanceof GaedesMercuryRotaryPumpTileEntity)
				{
					GaedesMercuryRotaryPumpTileEntity gmrp = (GaedesMercuryRotaryPumpTileEntity)te;
					lastGMRP = gmrp;
					this.vacuumPumpConnected=gmrp.ready;
					return gmrp.ready;
				}
			}
		}
		this.vacuumPumpConnected=false;
		return false;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("vacuumInductionMeltingFurnace");
	}
	
	public static void addRecipe(ItemStack input, FluidStack output) 
	{
		recipeManager.addRecipe(new UniversalRecipeInput(null, Arrays.asList(new ItemStack [] {input})),new UniversalRecipeOutput(Arrays.asList(new FluidStack[] {output}),null,20));
	}
	

	public static Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipes() {
		return recipeManager.getRecipes();
	}
	
    @Override
	public UniversalRecipeOutput getOutput()
    {
    	return VacuumInductionMeltingFurnaceTileEntity.recipeManager.getOutputFor(this.getInput(), false, false);
    }
    
	@Override
	public void onNetworkEvent(EntityPlayer player, int event) 
	{
		switch(event)
		{
		case 0:
			TileEntity te = worldObj.getTileEntity(xCoord, yCoord, zCoord);
			if(te instanceof InjectionMoldTileEntity)
			{
				InjectionMoldTileEntity im = (InjectionMoldTileEntity)te;
				im.fill(ForgeDirection.UP, this.fluidTank.drain(this.fluidTank.getCapacity(), true), true);
			}
			break;
		}
	}
	
	   @Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	    {
	        super.readFromNBT(nbttagcompound);
	        try
	        {
	            this.energy = nbttagcompound.getDouble("energy");
	        }
	        catch (Exception var3)//This was blindlessly copied from IC2 source a lot of time ago. And this is not necessary here. And still...
	        {
	            this.energy = nbttagcompound.getInteger("energy");

	            if (this.maxStorage > Integer.MAX_VALUE)
	            {
	                this.energy *= 10.0D;
	            }
	        }
	        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
	    }

	    @Override
		public void writeToNBT(NBTTagCompound nbttagcompound)
	    {
	        super.writeToNBT(nbttagcompound);
	        nbttagcompound.setDouble("energy", this.energy);
	        NBTTagCompound fluidTankTag = new NBTTagCompound();
	        this.fluidTank.writeToNBT(fluidTankTag);
	        nbttagcompound.setTag("fluidTank", fluidTankTag);
	    }
	    
	    private void setupMultiblockStructure(int x0,int y0,int z0)
	    {
			minX=xCoord+x0;
			maxX=xCoord+x0+3;
			minY=yCoord+y0;
			maxY=yCoord+y0+3;
			minZ=zCoord+z0;
			maxZ=zCoord+z0+3;
			for(int ix=xCoord+x0;ix<xCoord+x0+3;ix++)
			{
				for(int iy=yCoord+y0;iy<yCoord+y0+3;iy++)
				{
					for(int iz=zCoord+z0;iz<zCoord+z0+3;iz++)
					{
						if(!(worldObj.getTileEntity(ix, iy, iz) instanceof VacuumInductionMeltingFurnaceTileEntity))
						{
							worldObj.setBlock(ix, iy, iz, IHLMod.multiBlockSpacerBlock);
						}
					}					
				}
			}
			MultiBlockSpacerBlock.teList.add(this);
	    }

		@Override
		public boolean isAPartOfStructure(int x, int y, int z, boolean onBlockBeak) 
		{
			return x>=minX && x<=maxX && y>=minY && y<=maxY && z>=minZ && z<=maxZ;
		}

		@Override
		public boolean getIsInvalid() 
		{
			return this.isInvalid();
		}
	    
}