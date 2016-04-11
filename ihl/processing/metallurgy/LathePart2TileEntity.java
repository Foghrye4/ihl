package ihl.processing.metallurgy;

import java.util.ArrayList;
import java.util.List;

import ic2.api.network.INetworkDataProvider;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.network.NetworkManager;
import ihl.utils.IHLUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class LathePart2TileEntity extends TileEntity implements IWrenchable, INetworkDataProvider{

	private short facing=2;
	private short lastFacing=2;
	
	public LathePart2TileEntity()
	{
		super();
	}
	
    public boolean enableUpdateEntity()
    {
        return IC2.platform.isSimulating();
    }
	
	@Override
    public List<String> getNetworkedFields()
    {
		List<String> fields = new ArrayList();
		fields.add("facing");
		return fields;
    }
	
    @Override
	public void updateEntity()
    {
        super.updateEntity();
        if(lastFacing!=facing)
        {
			IC2.network.get().updateTileEntityField(this, "facing");
			lastFacing=facing;
        }
    }
	

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing()!=side;
	}


	@Override
	public short getFacing() {
		return this.facing;
	}


	@Override
	public void setFacing(short facing1) 
	{
		facing=(short) Math.max(2,facing1);
		if(IC2.platform.isSimulating())
		{
			IC2.network.get().updateTileEntityField(this, "facing");
			lastFacing=facing;
		}
	}


	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}


	@Override
	public float getWrenchDropRate() {
		return 1F;
	}


	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("lathePart2");
	}
	
	@Override
	   public void readFromNBT(NBTTagCompound nbttagcompound)
	    {
	        super.readFromNBT(nbttagcompound);
	        facing=nbttagcompound.getShort("facing");
	    }

	@Override
	    public void writeToNBT(NBTTagCompound nbttagcompound)
	    {
	        super.writeToNBT(nbttagcompound);
	        nbttagcompound.setShort("facing", facing);
	    }
}
