package ihl.utils;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerWakeUpEventHandler 
{
    @SubscribeEvent
    public void onPlayerWakeUpEvent(net.minecraftforge.event.entity.player.PlayerWakeUpEvent event)
    {
    	if(event.entityPlayer instanceof EntityPlayer && !event.entityPlayer.worldObj.isRemote && !event.entityLiving.getEntityData().hasKey("ihlGuidebookRecieved"))
    	{
    		event.entityPlayer.entityDropItem(IHLUtils.getThisModItemStack("guidebook"), 1f);
    		event.entityPlayer.getEntityData().setBoolean("ihlGuidebookRecieved", true);
    	}
    }
}
