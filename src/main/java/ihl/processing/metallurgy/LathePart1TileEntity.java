package ihl.processing.metallurgy;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ihl.interfaces.IWorkspaceElement;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class LathePart1TileEntity extends BasicElectricMotorTileEntity implements IWorkspaceElement{

	public boolean ready=false;
	
	public LathePart1TileEntity()
	{
		super();
	}
	

	@Override
	public String getInventoryName() {
		return "Lathe";
	}
    
	@Override
	public void operate() 
	{
		ready=true;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("lathePart1");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new LatheGui(new LatheContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new LatheContainer(player, this);
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List[] getInput() 
	{
		return null;
	}

	@Override
	public boolean canOperate()
	{
		TileEntity te = worldObj.getTileEntity(xCoord+ForgeDirection.getOrientation(getFacing()).offsetX,yCoord,zCoord+ForgeDirection.getOrientation(getFacing()).offsetZ);
		if(te!=null && te instanceof LathePart2TileEntity)
		{
	        return !ready;
		}
		return false;
	}

	@Override
	public void onGuiClosed(EntityPlayer arg0) {}
	
	@Override
	public boolean canBeUsed() 
	{
        return ready;
	}

	@Override
	public void use() 
	{
		ready=false;
	}

	@Override
	public boolean getIsInvalid() 
	{
		return this.isInvalid();
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }
}
