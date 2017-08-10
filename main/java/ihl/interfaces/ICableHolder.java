package ihl.interfaces;

import net.minecraft.entity.EntityLivingBase;

public interface ICableHolder 
{
	double[] getPortPos(EntityLivingBase player);
	boolean isCableRemoved(int chainUniqueID);
	void setCableCheck(boolean b);
}
