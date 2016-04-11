package ihl.interfaces;

import ihl.datanet.Contact;
import ihl.processing.invslots.InvSlotSignalProcessor;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IDataCableHolder extends ICableHolder
{
	boolean addDataCable(NBTTagCompound cable);
	Contact[] getContacts(int chainUID);
	void removeCable(int chainUID);
	Contact getContact(int innerId1);
}
