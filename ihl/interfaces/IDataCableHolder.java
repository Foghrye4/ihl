package ihl.interfaces;

import ihl.datanet.Contact;
import net.minecraft.nbt.NBTTagCompound;

public interface IDataCableHolder extends ICableHolder
{
	boolean addDataCable(NBTTagCompound cable);
	Contact[] getContacts(int chainUID);
	void removeCable(int chainUID);
	Contact getContact(int innerId1);
}
