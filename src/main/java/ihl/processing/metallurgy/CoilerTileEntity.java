package ihl.processing.metallurgy;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlotOutput;
import ihl.interfaces.IWire;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CoilerTileEntity extends BasicElectricMotorTileEntity implements IProductionLine{

    public final InvSlotOutput output;
    private int activeTimer=0;
    public boolean hasCoil=false;
	
	public CoilerTileEntity()
	{
		super();
		this.output = new InvSlotOutput(this, "output", 1, 1);
	}
	

	@Override
	public String getInventoryName() {
		return "Coiler";
	}
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = super.getNetworkedFields();
		fields.add("hasCoil");
		return fields;
    }
	
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player)
	{
		return IHLUtils.getThisModItemStack("coiler");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean arg1) {
		return new CoilerGui(new CoilerContainer(player, this));
	}
	
	@Override
	public ContainerBase<?> getGuiContainer(EntityPlayer player) 
	{
		return new CoilerContainer(player, this);
	}
	
	@Override
	public boolean canOperate()
	{
		return false;
	}
    
    @Override
	public void updateEntityServer()
    {
        super.updateEntityServer();
        if(activeTimer>0)
        {
        	activeTimer--;
        }
        else
        {
        	setActive(false);
        }
        if(this.output.isEmpty() && hasCoil==true)
        {
			this.hasCoil=false;
			IC2.network.get().updateTileEntityField(this, "hasCoil");
        }
        else if(!this.output.isEmpty() && hasCoil==false)
        {
			this.hasCoil=true;
			IC2.network.get().updateTileEntityField(this, "hasCoil");
        }
    }

	@Override
	public void operate() 
	{}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List[] getInput() 
	{
		return null;
	}

	@Override
	public boolean canProcess(ItemStack cable) {
    	if(this.energy>1D && cable.getItem() instanceof IWire)
    	{
    		if(this.output.isEmpty())
    		{
    			return true;
    		}
    		else if(this.output.get().getItem() instanceof IWire)
    		{
    			return ((IWire)this.output.get().getItem()).isSameWire(this.output.get(), cable);
    		}
    		else
    		{
    			return this.output.get().isItemEqual(cable);
    		}
    	}
    	return false;
	}


	@Override
	public void process(ItemStack cable) {
	  	if(cable.getItem() instanceof IWire)
    	{
    		this.energy-=1D;
    		if(this.output.isEmpty())
    		{
    			setActive(true);
    			activeTimer=800;
    			this.output.put(cable);
    			this.hasCoil=true;
    			IC2.network.get().updateTileEntityField(this, "hasCoil");
    		}
    		else
    		{
    			setActive(true);
    			activeTimer=800;
    			int length = this.output.get().stackTagCompound.getInteger(((IWire)this.output.get().getItem()).getTag());
    			int fullLength = this.output.get().stackTagCompound.getInteger(((IWire)this.output.get().getItem()).getTagSecondary());
    			int lengthToAdd = cable.stackTagCompound.getInteger(((IWire)cable.getItem()).getTag());
    			int fullLengthToAdd = cable.stackTagCompound.getInteger(((IWire)cable.getItem()).getTagSecondary());
    			this.output.get().stackTagCompound.setInteger(((IWire)this.output.get().getItem()).getTag(), length+lengthToAdd);
    			this.output.get().stackTagCompound.setInteger(((IWire)this.output.get().getItem()).getTagSecondary(),fullLength+fullLengthToAdd);
    		}
    	}
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass==0;
    }

}
