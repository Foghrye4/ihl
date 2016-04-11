package ihl.interfaces;

import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

public interface INetworkListener 
{
	void recieveData(ByteBufInputStream byteBufInputStream);
	int getId();
	boolean isInvalid();
	void registerAndSendData(EntityPlayerMP player);
}
