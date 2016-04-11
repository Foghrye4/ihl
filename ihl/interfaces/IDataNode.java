package ihl.interfaces;

import ihl.processing.invslots.InvSlotSignalProcessor;

import java.util.Set;

public interface IDataNode 
{
	int getAttachedSlotNumber();
	int getDataGridID();
	void setDataGrid(int newGridID);
	Set<IDataNode> getConnectedDataNodes();
	int getXPos();
	int getYPos();
	int getZPos();
	int getInnerId();
	boolean isIndirectlyConnectedWithContact(int id);
	void checkAttachedSlots();
	InvSlotSignalProcessor getAttachedSlot();
	int getType();
}
