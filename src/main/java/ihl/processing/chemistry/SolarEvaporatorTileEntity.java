package ihl.processing.chemistry;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ihl.interfaces.IFluidTankVisual;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumSkyBlock;

public class SolarEvaporatorTileEntity extends EvaporatorTileEntity implements IFluidTankVisual {
    public int visibleFluidId = -1;
    public int visibleFluidAmount = 1;
    
	public SolarEvaporatorTileEntity(){
		super();
    	this.fuelSlot = null;
    	this.maxProgress = 4500;
	}
	
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> ret = super.getNetworkedFields();
        ret.add("visibleFluidId");
        ret.add("visibleFluidAmount");
        return ret;
    }
	
    @Override
	public void updateEntityServer()
    {
    	super.updateEntityServer();
		if(this.fluidTank.getFluid()!=null && (visibleFluidId!=this.fluidTank.getFluid().getFluid().getID() || visibleFluidAmount!=this.fluidTank.getFluidAmount()))
		{
			visibleFluidId = this.fluidTank.getFluid().getFluid().getID();
			visibleFluidAmount = this.fluidTank.getFluidAmount();
       		IC2.network.get().updateTileEntityField(this, "visibleFluidId");
       		IC2.network.get().updateTileEntityField(this, "visibleFluidAmount");
       	}
		else if(this.fluidTank.getFluid()==null && visibleFluidId!=-1)
       	{
			visibleFluidId=-1;
       		IC2.network.get().updateTileEntityField(this, "visibleFluidId");
       	}
    }
	@Override
    public boolean isBurning()
    {
		return this.worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, xCoord, yCoord, zCoord)>=15;
    }
	
    @Override
	public ContainerBase<? extends EvaporatorTileEntity> getGuiContainer(EntityPlayer entityPlayer)
    {
        return new SolarEvaporatorContainer(entityPlayer, this);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin)
    {
        return new SolarEvaporatorGui(new SolarEvaporatorContainer(entityPlayer, this));
    }
    
	@Override
	public short getFacing()
	{
		return 3;
	}
	
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("solarEvaporator");
	}

	@Override
	public int getVisibleFluidId() {
		return this.visibleFluidId;
	}

	@Override
	public int getVisibleFluidAmount() {
		return this.visibleFluidAmount;
	}

	@Override
	public float getRenderLiquidLevel()
	{
		return (float)this.visibleFluidAmount/(float)this.fluidTank.getCapacity()/2;
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
}
