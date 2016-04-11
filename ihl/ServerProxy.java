package ihl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import ihl.flexible_cable.NodeEntity;
import ihl.interfaces.INetworkListener;
import ihl.items_blocks.FlexibleCableItem;
import ihl.items_blocks.MachineBaseBlock.MachineType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class ServerProxy {

    protected static FMLEventChannel channel;
    public static final int updatePeriod = 1;
    private static final int maxPacketDataLength = 128;
    protected Map<Integer, INetworkListener> entityList = new HashMap();
    protected Set<INetworkListener> entityServerList = new HashSet();
    protected Map<Integer, ByteBuf> delayedEntityDataPacket = new HashMap();
	public Map<Integer,Set<NodeEntity>> nodeEntityRegistry = new HashMap();
    
	public ServerProxy() {}
	
	public void load() throws ParserConfigurationException 
	{
    	if(channel==null)
    	{
    		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(IHLModInfo.MODID);
   	        channel.register(this);
    	}
	}
	public void spawnParticle(int particle, World world, double x, double y, double z, double mx, double my, double mz, float paticleScale){}
	
	public void spawnParticleFromServer(int particle, World world, double x, double y, double z, double mx, double my, double mz, float paticleScale)
	{
		ByteBuf bb = Unpooled.buffer(36); 
		ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(bb);
		try 
		{
			byteBufOutputStream.write(0);
			byteBufOutputStream.write(particle);
			byteBufOutputStream.writeFloat((float) x);
			byteBufOutputStream.writeFloat((float) y);
			byteBufOutputStream.writeFloat((float) z);
			byteBufOutputStream.writeFloat((float) mx);
			byteBufOutputStream.writeFloat((float) my);
			byteBufOutputStream.writeFloat((float) mz);
			byteBufOutputStream.writeFloat(paticleScale);
			channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream.buffer(),IHLModInfo.MODID), new TargetPoint(world.provider.dimensionId, x, y, z, 32d));
			byteBufOutputStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void registerIcons(Pre event) {}
	public void initBlockRenderer(){}
	public Object getRenderForEntityClass(Class<? extends Entity> entityClass) 
	{
		return null;
	}
	public int getGLDisplayList() {return -1;}

    public File getMinecraftDir()
    {
         return new File(".");
    }
	public int shareBlockRendererByMachineType(MachineType type) 
	{
		return 0;
	}

	public void addEntityToList(INetworkListener entity) 
	{
		this.entityList.put(entity.getId(), entity);
	}
	
    public void recieveDelayedDataPacket(INetworkListener listener)
    {
    	ByteBuf data = delayedEntityDataPacket.remove(listener.getId());
    	if(data!=null)
    	{
        	ByteBufInputStream byteBufInputStream = new ByteBufInputStream(data);
        	try 
        	{
				//byteBufInputStream.skipBytes(5);
				listener.recieveData(byteBufInputStream);
	            byteBufInputStream.close();
    			IHLMod.log.debug("Delayed data read.");
			} 
        	catch (IOException e) 
			{
				e.printStackTrace();
			}
    	}
    	else
    	{
			IHLMod.log.debug("Delayed data is null. Entity ID="+listener.getId());
    	}
    }

	public void sendFromServerToAll(FMLProxyPacket fmlProxyPacket) 
	{
		channel.sendToAll(fmlProxyPacket);
	}
	
	public void sendFromServerToPlayer(FMLProxyPacket fmlProxyPacket, EntityPlayerMP player) 
	{
		channel.sendTo(fmlProxyPacket, player);
	}
	
    @SubscribeEvent
	public void onPacketFromClientToServer(FMLNetworkEvent.ServerCustomPacketEvent event) throws IOException
	{
    	ByteBuf data = event.packet.payload();
    	ByteBufInputStream byteBufInputStream = new ByteBufInputStream(data);
        switch(byteBufInputStream.read())
        {
        	case 0:
        		int playerEntityId = byteBufInputStream.readInt();
        		int worldDimensionId = byteBufInputStream.readInt();
        		int containerSlotNumber = byteBufInputStream.readInt();
        		int fieldValue = byteBufInputStream.readInt();
        		String fieldName = byteBufInputStream.readUTF();
        		EntityPlayerMP player = (EntityPlayerMP) MinecraftServer.getServer().worldServerForDimension(worldDimensionId).getEntityByID(playerEntityId);
        		ItemStack stack = ((Slot)player.openContainer.inventorySlots.get(containerSlotNumber)).getStack();
        		stack.stackTagCompound.setInteger(fieldName, fieldValue);
        		player.openContainer.detectAndSendChanges();
    	    	System.out.println("Field now "+stack.stackTagCompound.getInteger(fieldName));
        		break;
        }

        byteBufInputStream.close();

	}
	
    @SubscribeEvent
	public void onPlayerConnectedToServer(PlayerLoggedInEvent event)
    {
    	IHLMod.log.debug("player connected");
    	Iterator<INetworkListener> inli=this.entityServerList.iterator();
    	while(inli.hasNext())
    	{
    		INetworkListener inl = inli.next();
    		if(inl.isInvalid())
    		{
    			inli.remove();
    		}
    		else if(event.player instanceof EntityPlayerMP)
    		{
    			inl.registerAndSendData((EntityPlayerMP)event.player);
    		}
    	}
    }
    
	@SubscribeEvent
	public void onPlayerTeleport(PlayerChangedDimensionEvent event)
	{
		FlexibleCableItem.instance.onPlayerTeleport(event);
    	Iterator<INetworkListener> inli=this.entityServerList.iterator();
    	while(inli.hasNext())
    	{
    		INetworkListener inl = inli.next();
    		if(inl.isInvalid())
    		{
    			inli.remove();
    		}
    		else if(event.player instanceof EntityPlayerMP)
    		{
    			inl.registerAndSendData((EntityPlayerMP)event.player);
    		}
    	}
	}
	
	public void addEntityToServerList(INetworkListener entity) 
	{
		this.entityServerList.add(entity);
	}

	public boolean renderTESpecialSelectionBox(TileEntity te, EntityPlayer player,	ItemStack currentItem, MovingObjectPosition target,	float partialTicks) {
		return false;
	}
	
	public void sendItemStackNBTTagFromClientToServerPlayer(EntityPlayer player, int slotNumber, String fieldName, int fieldValue){}
}
