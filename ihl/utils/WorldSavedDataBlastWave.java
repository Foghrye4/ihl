package ihl.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

public class WorldSavedDataBlastWave extends WorldSavedData {
	Map<Long,Long[][]> data = new HashMap(256);
	public static long memoryUsage=0L;
	
	public WorldSavedDataBlastWave(String name) 
	{
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		if(nbt.hasKey("entryList"))
		{
			NBTTagList entryList=nbt.getTagList("entryList", 10);
			for(int i=0;i<entryList.tagCount();i++)
			{
				NBTTagCompound chunk = entryList.getCompoundTagAt(i);
				long chunkHash = chunk.getLong("chunkHash");
				NBTTagList blastWaveList = chunk.getTagList("blastWaveList", 10);
				Long[][] blastWaveArray= new Long[16][5];
				for(int i1=0;i1<16 || i1<blastWaveList.tagCount();i1++)
				{
					NBTTagCompound blastWave = blastWaveList.getCompoundTagAt(i1);
					blastWaveArray[i1][0]=blastWave.getLong("longNumber");
					blastWaveArray[i1][1]=(long) blastWave.getInteger("sourceX");
					blastWaveArray[i1][2]=(long) blastWave.getInteger("sourceY");
					blastWaveArray[i1][3]=(long) blastWave.getInteger("sourceZ");
					blastWaveArray[i1][4]=(long) blastWave.getInteger("power");
				}
				data.put(chunkHash, blastWaveArray);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		NBTTagList entryList = new NBTTagList();
		Iterator<Entry<Long, Long[][]>> dataESI = data.entrySet().iterator();
		while(dataESI.hasNext())
		{
			Entry<Long, Long[][]> dataEntry = dataESI.next();
			Long[][] bwArray = dataEntry.getValue();
			NBTTagCompound chunk = new NBTTagCompound();
			NBTTagList blastWaveList = new NBTTagList();
			for(int i1=0;i1<16;i1++)
			{
				NBTTagCompound blastWave = new NBTTagCompound();
				blastWave.setLong("longNumber", bwArray[i1][0]);
				blastWave.setInteger("sourceX", bwArray[i1][1].intValue());
				blastWave.setInteger("sourceY", bwArray[i1][2].intValue());
				blastWave.setInteger("sourceZ", bwArray[i1][3].intValue());
				blastWave.setInteger("power", bwArray[i1][4].intValue());
				blastWaveList.appendTag(blastWave);
			}
			chunk.setTag("blastWaveList", blastWaveList);
			chunk.setLong("chunkHash", dataEntry.getKey());
			entryList.appendTag(chunk);
		}
		nbt.setTag("entryList", entryList);
	}

	public void scheduleExplosionEffectsOnChunkLoad(long chunkXZKey, long longNumber, int sourceX, int sourceY, int sourceZ, int power1, int absEBSY) 
	{
		Long[][] waves;
		if(data.containsKey(chunkXZKey))
		{
			waves=data.get(chunkXZKey);
		}
		else
		{
			waves=new Long[16][5];
			data.put(chunkXZKey, waves);
		}
		waves[absEBSY][0]=longNumber;
		waves[absEBSY][1]=(long)sourceX;
		waves[absEBSY][2]=(long)sourceY;
		waves[absEBSY][3]=(long)sourceZ;
		waves[absEBSY][4]=(long)power1;
	}
}
