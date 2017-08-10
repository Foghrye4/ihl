package ihl.interfaces;

import net.minecraft.entity.player.EntityPlayer;

public interface IMultiPowerCableHolder 
{
	IEnergyNetNode getEnergyNetNode(short facing);
	short getSide(EntityPlayer player);
	void removeAttachedChains();
	boolean isCableRemoved(int chainUniqueID);
}
