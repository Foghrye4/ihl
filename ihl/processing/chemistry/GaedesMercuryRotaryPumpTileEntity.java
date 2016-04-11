package ihl.processing.chemistry;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ihl.interfaces.IWorkspaceElement;
import ihl.processing.metallurgy.BasicElectricMotorTileEntity;
import ihl.utils.IHLUtils;

public class GaedesMercuryRotaryPumpTileEntity extends	BasicElectricMotorTileEntity implements IWorkspaceElement{

	public boolean ready=false;
	
	public GaedesMercuryRotaryPumpTileEntity()
	{
		this.isGuiScreenOpened=true;
	}
	
	@Override
	public void operate() 
	{
		ready=true;
	}
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) 
	{
		return IHLUtils.getThisModItemStack("gaedesMercuryRotaryPump");
	}

	@Override
	public List[] getInput() 
	{
		return null;
	}

	@Override
	public boolean canOperate() 
	{
		return !ready;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new GaedesMercuryRotaryPumpGui(new GaedesMercuryRotaryPumpContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) {
		return new GaedesMercuryRotaryPumpContainer(player, this);
	}

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
