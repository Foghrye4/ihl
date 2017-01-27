package ihl.explosion;

import ihl.IHLMod;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Explosion;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ChunkAndWorldLoadEventHandler 
{
	public static ChunkAndWorldLoadEventHandler instance;
	public ChunkAndWorldLoadEventHandler()
	{
		instance=this;
	}
	
	@SubscribeEvent
	public void onChunkLoadEvent(net.minecraftforge.event.world.ChunkEvent.Load event)
	{
		if(event.getChunk().isChunkLoaded && IHLMod.explosionHandler.blastWaveByDimensionId.containsKey(event.world.provider.dimensionId))
		{
			WorldSavedDataBlastWave bwdata = IHLMod.explosionHandler.blastWaveByDimensionId.get(event.world.provider.dimensionId);
			long cc = ChunkCoordIntPair.chunkXZ2Int(event.getChunk().xPosition, event.getChunk().zPosition);
			if(bwdata.data.containsKey(cc))
			{
				Set<Integer[]> bwArraySet = bwdata.data.remove(cc);
				int[] svset = new int[bwArraySet.size()];
				int[] directionMask = new int[3];
				int i=0;
				Iterator<Integer[]> bwArrayI = bwArraySet.iterator();
				Integer[] bwArray = null;
				while(bwArrayI.hasNext())
				{
					bwArray=bwArrayI.next();
					svset[i]=bwArray[0];
					i++;
					directionMask[0]=bwArray[5];
					directionMask[1]=bwArray[6];
					directionMask[2]=bwArray[7];
					IHLMod.explosionHandler.setPower(bwArray[0], bwArray[4]);
				}
/*				IHLMod.log.info("Doing schleduled explosion. Direction: ("+
						directionMask[0]+";"+directionMask[1]+";"+directionMask[2]+") "+
						"svset.length"+svset.length);*/
				IHLMod.explosionHandler.doExplosion(event.world, bwArray[1], bwArray[2], bwArray[3], svset, directionMask, new Explosion(event.world, null, bwArray[1], bwArray[2], bwArray[3], 100f));
				IHLMod.explosionHandler.sendChunkUpdateToPlayersInExplosionAffectedZone(event.world, bwArray[1], bwArray[2], bwArray[3]);
			}
		}
	}

	@SubscribeEvent
	public void onWorldLoadEvent(net.minecraftforge.event.world.WorldEvent.Load event)
	{
		WorldSavedDataBlastWave blastWave = (WorldSavedDataBlastWave) event.world.mapStorage.loadData(WorldSavedDataBlastWave.class, "blastWave");
		if(blastWave!=null)
		{
			IHLMod.explosionHandler.blastWaveByDimensionId.put(event.world.provider.dimensionId,blastWave);
		}
	}

	@SubscribeEvent
	public void onWorldSaveEvent(net.minecraftforge.event.world.WorldEvent.Save event)
	{
		if(IHLMod.explosionHandler.blastWaveByDimensionId.containsKey(event.world.provider.dimensionId))
		{
			event.world.mapStorage.setData("blastWave",IHLMod.explosionHandler.blastWaveByDimensionId.get(event.world.provider.dimensionId));
		}
	}
}
