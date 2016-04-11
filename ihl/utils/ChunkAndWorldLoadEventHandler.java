package ihl.utils;

import ihl.IHLMod;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
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
				Long[][] bwArray = bwdata.data.remove(cc);
				Set<Long> svset = new HashSet(16);
				int sourceIndex=0;
				for(int i1=0;i1<16;i1++)
				{
					if(bwArray[i1][0]!=null && !bwArray[i1][0].equals(0))
					{
						sourceIndex=i1;
						svset.add(bwArray[i1][0]);
						IHLMod.explosionHandler.setPower(bwArray[i1][0], bwArray[i1][4].intValue());
					}
				}
				IHLMod.explosionHandler.doExplosion(event.world, bwArray[sourceIndex][1].intValue(), bwArray[sourceIndex][2].intValue(), bwArray[sourceIndex][3].intValue(), svset);
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
