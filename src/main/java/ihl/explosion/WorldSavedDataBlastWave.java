package ihl.explosion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

public class WorldSavedDataBlastWave extends WorldSavedData {
	Map<Long,Set<Integer[]>> data = new HashMap<Long,Set<Integer[]>>(256);
	
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
				long chunkXZKey = chunk.getLong("chunkXZKey");
				NBTTagList blastWaveList = chunk.getTagList("blastWaveList", 10);
				Set<Integer[]> blastWaveArraySet= new HashSet<Integer[]>();
				for(int i1=0;i1<blastWaveList.tagCount();i1++)
				{
					NBTTagCompound blastWave = blastWaveList.getCompoundTagAt(i1);
					Integer[] blastWaveArray = new Integer[5];
					blastWaveArray[0]=blastWave.getInteger("ev");
					blastWaveArray[1]=blastWave.getInteger("sourceX");
					blastWaveArray[2]=blastWave.getInteger("sourceY");
					blastWaveArray[3]=blastWave.getInteger("sourceZ");
					blastWaveArray[4]=blastWave.getInteger("power");
					blastWaveArray[5]=blastWave.getInteger("directionX");
					blastWaveArray[6]=blastWave.getInteger("directionY");
					blastWaveArray[7]=blastWave.getInteger("directionZ");
					blastWaveArraySet.add(blastWaveArray);
				}
				data.put(chunkXZKey, blastWaveArraySet);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		NBTTagList entryList = new NBTTagList();
		Iterator<Entry<Long, Set<Integer[]>>> dataESI = data.entrySet().iterator();
		while(dataESI.hasNext())
		{
			Entry<Long, Set<Integer[]>> dataEntry = dataESI.next();
			Iterator<Integer[]> bwArrayI = dataEntry.getValue().iterator();
			NBTTagCompound chunk = new NBTTagCompound();
			NBTTagList blastWaveList = new NBTTagList();
			while(bwArrayI.hasNext())
			{
				NBTTagCompound blastWave = new NBTTagCompound();
				Integer[] bwArray = bwArrayI.next();
				blastWave.setInteger("ev", bwArray[0]);
				blastWave.setInteger("sourceX", bwArray[1]);
				blastWave.setInteger("sourceY", bwArray[2]);
				blastWave.setInteger("sourceZ", bwArray[3]);
				blastWave.setInteger("power", bwArray[4]);
				blastWave.setInteger("directionX", bwArray[5]);
				blastWave.setInteger("directionY", bwArray[6]);
				blastWave.setInteger("directionZ", bwArray[7]);
				blastWaveList.appendTag(blastWave);
			}
			chunk.setTag("blastWaveList", blastWaveList);
			chunk.setLong("chunkHash", dataEntry.getKey());
			entryList.appendTag(chunk);
		}
		nbt.setTag("entryList", entryList);
	}

	public void scheduleExplosionEffectsOnChunkLoad(long chunkXZKey, int ev, int sourceX, int sourceY, int sourceZ, int power1, int[] directionMask) 
	{
		Set<Integer[]> waves;
		if(data.containsKey(chunkXZKey))
		{
			waves=data.get(chunkXZKey);
		}
		else
		{
			waves=new HashSet<Integer[]>(1024);
			data.put(chunkXZKey, waves);
		}
		Integer[] wave = new Integer[8];
		wave[0]=ev;
		wave[1]=sourceX;
		wave[2]=sourceY;
		wave[3]=sourceZ;
		wave[4]=power1;
		wave[5]=directionMask[0];
		wave[6]=directionMask[1];
		wave[7]=directionMask[2];
		waves.add(wave);
	}
}
