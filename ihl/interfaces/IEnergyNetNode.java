package ihl.interfaces;

import java.util.Set;

import ihl.flexible_cable.IHLGrid;
import net.minecraft.nbt.NBTTagCompound;

public interface IEnergyNetNode extends ICableHolder{
	IHLGrid getGrid();
	int getGridID();
	void setGrid(int newGridID);
	double getMaxAllowableVoltage();
	boolean addCable(NBTTagCompound cable);
	Set<NBTTagCompound> getCableList();
	void removeAttachedChains();
	void remove(NBTTagCompound cable);
	double getEnergyAmountThisNodeWant();
	void injectEnergyInThisNode(double amount, double voltage);
}
