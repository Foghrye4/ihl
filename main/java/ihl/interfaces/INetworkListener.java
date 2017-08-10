package ihl.interfaces;

import io.netty.buffer.ByteBufInputStream;
import net.minecraft.entity.player.EntityPlayerMP;

public interface INetworkListener 
{
	void recieveData(ByteBufInputStream byteBufInputStream);
	int getId();
	boolean isInvalid();
	void registerAndSendData(EntityPlayerMP player);
}
