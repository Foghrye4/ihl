package ihl.enviroment;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.event.LaserEvent;
import ic2.core.item.tool.EntityMiningLaser;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class LaserHitMirrorEventHandler 
{
	public LaserHitMirrorEventHandler(){}
	
	@SubscribeEvent
	public void onLaserHit(LaserEvent.LaserHitsBlockEvent event) 
	{
		TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
		if(te instanceof MirrorTileEntity)
		{
			ForgeDirection mirrorDirection = ForgeDirection.getOrientation(((MirrorTileEntity)te).getFacing());
			Entity ls = event.lasershot;
			if((ls.motionX*mirrorDirection.offsetX+ls.motionY*mirrorDirection.offsetY+ls.motionZ*mirrorDirection.offsetZ)<0)
			{
				if(mirrorDirection.offsetX!=0)
				{
					ls.motionX=-ls.motionX;
				}
				if(mirrorDirection.offsetY!=0)
				{
					ls.motionY=-ls.motionY;
				}
				if(mirrorDirection.offsetZ!=0)
				{
					ls.motionZ=-ls.motionZ;
				}
				if(!event.world.isRemote)
				{
					EntityMiningLaser tLaser = new EntityMiningLaser(event.world, event.owner, event.range, event.power, event.blockBreaks, event.explosive, 0, 0, ls.posY);
					tLaser.setPosition(ls.posX, ls.posY, ls.posZ);
					tLaser.setLaserHeading(ls.motionX, ls.motionY, ls.motionZ, 1d);
					ls.setDead();
					event.world.spawnEntityInWorld(tLaser);
				}
				event.setCanceled(true);
			}
		}
	}
}
