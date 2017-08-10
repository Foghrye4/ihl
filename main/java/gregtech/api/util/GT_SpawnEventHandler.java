package gregtech.api.util;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;

public class GT_SpawnEventHandler {

	public static volatile List <int[]> mobReps = new ArrayList();
	
	public GT_SpawnEventHandler(){
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	  @SubscribeEvent
	  public void denyMobSpawn(CheckSpawn event)
	  {
	    if (event.getResult() == Event.Result.ALLOW) {return;}
	    if (event.entityLiving.isCreatureType(EnumCreatureType.monster, false)) 
	       {
	    	for(int[] rep : mobReps){
	    		if(rep[3] == event.entity.worldObj.provider.dimensionId){
	    			TileEntity tTile = event.entity.worldObj.getTileEntity(rep[0], rep[1], rep[2]);

	    		}
	    	}
	    }
	}
}
