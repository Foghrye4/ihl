package ihl.utils;

import ihl.IHLMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ExplosionVector 
{
	public Set<Long> startVectors = new HashSet();
	private Set<Long> sv;
	private Set<Chunk> chunksToUpdate=new HashSet(64);
	public Map<Long,Set<Long>> vectors = new HashMap(32786);
	public Map<Long,Explosion> explosions = new HashMap();
	public Map<Long,Integer> explosionPower = new HashMap();
	public Map<Long,Float> explosionPowerDampingFactor = new HashMap(32786);
	public Map<Long,ExtendedBlockStorage> cachedEBS = new HashMap(128);
	public Map<Long,Integer> cachedEBSHardness = new HashMap(128);
	public Map<Long,Map<Long,Integer>> cachedEBSDrops = new HashMap(128);
	public Map<Long,List<Entity>> cachedEBSEntity = new HashMap(128);
	public Map<Integer,WorldSavedDataBlastWave> blastWaveByDimensionId = new HashMap();
	
	private Random random = new Random();
	
	public ExplosionVector()
	{
		this.precalculateExplosion();
	}
	
	public void precalculateExplosion()
	{
		int maxExplosionRadius=32;
		for(int levelRadius=1; levelRadius<maxExplosionRadius; levelRadius++)
		{
			for(int ix=-levelRadius;ix<=levelRadius;ix++)
			{
				for(int iy=-levelRadius;iy<=levelRadius;iy++)
				{
					for(int iz=-levelRadius;iz<=levelRadius;iz++)
					{
						{
							long coordinateKey = IHLUtils.encodeXYZ(ix,iy,iz);
							if(!vectors.containsKey(coordinateKey))
							{
								int prevX=ix;
								int prevY=iy;
								int prevZ=iz;
								if(Math.abs(ix)+Math.abs(iy)+Math.abs(iz)>Math.round(levelRadius*1.8f))
								{
									prevX=IHLUtils.reduceVariableByAbsoluteValue(prevX);
									prevY=IHLUtils.reduceVariableByAbsoluteValue(prevY);
									prevZ=IHLUtils.reduceVariableByAbsoluteValue(prevZ);
								}
								else if(Math.abs(ix)<=Math.abs(iy) && Math.abs(ix)<=Math.abs(iz))
								{
									prevY=IHLUtils.reduceVariableByAbsoluteValue(prevY);
									prevZ=IHLUtils.reduceVariableByAbsoluteValue(prevZ);
								}
								else if(Math.abs(iy)<=Math.abs(ix) && Math.abs(iy)<=Math.abs(iz))
								{
									prevX=IHLUtils.reduceVariableByAbsoluteValue(prevX);
									prevZ=IHLUtils.reduceVariableByAbsoluteValue(prevZ);
								}
								else if(Math.abs(iz)<=Math.abs(ix) && Math.abs(iz)<=Math.abs(iy))
								{
									prevY=IHLUtils.reduceVariableByAbsoluteValue(prevY);
									prevX=IHLUtils.reduceVariableByAbsoluteValue(prevX);
								}
								long prevKey = IHLUtils.encodeXYZ(prevX,prevY,prevZ);
								if(prevX==ix && prevY==iy && prevZ==iz && levelRadius>1)
								{
									IHLMod.log.error("Was:"+ix+"/" +iy+"/" +iz+"/");
									IHLMod.log.error("Now:"+prevX+"/" +prevY+"/" +prevZ+"/");
									throw new ArithmeticException("Variables are out of expected range. \n Expected are not equal: "+ix+"="+prevX+" "+iy+"="+prevY+" "+iz+"="+prevZ);
								}
								if(levelRadius==1)
								{
									startVectors.add(coordinateKey);
									this.addElement(coordinateKey, prevX, prevY, prevZ, ix, iy, iz);
								}
								else if(vectors.containsKey(prevKey))
								{
									//prevKey=this.getParentWithLowestDescendantsRate(prevKey, prevX, prevY, prevZ, levelRadius);
									vectors.get(prevKey).add(coordinateKey);
									this.addElement(coordinateKey, prevX, prevY, prevZ, ix, iy, iz);
								}
								else
								{
									IHLMod.log.error("Was:"+ix+"/" +iy+"/" +iz+"/");
									IHLMod.log.error("Now:"+prevX+"/" +prevY+"/" +prevZ+"/");
									IHLMod.log.info("ExplosionVector is missing parent! Help him!");
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void addElement(long coordinateKey, int prevX, int prevY, int prevZ, int ix, int iy, int iz)
	{
		vectors.put(coordinateKey, new HashSet());
		float df = (float)(prevX*prevX+prevY*prevY+prevZ*prevZ+1)/(float)(ix*ix+iy*iy+iz*iz+1);
		explosionPowerDampingFactor.put(coordinateKey,df);
	}

	public void setPower(Set<Long> sv2, int power1) 
	{
		for(long ev:sv2)
		{
			this.setPower(ev, power1);
		}
	}

	public void setPower(Long ev, int power1) {
		this.explosionPower.put(ev, power1);
	}
/*
	public Set<Long> breakBlocksAndGetDescendants(World world, EntityIHLExplosion exploder, Explosion explosion, long longNumber)
	{
		int multiplier = exploder.multiplier;
		int[] xyz = IHLUtils.decodeXYZ(longNumber);
		int power1 = explosionPower.get(longNumber);
		int repeat=0;
		//System.out.println("Power before " + power1);
		if(Math.abs(xyz[0])>=Math.abs(xyz[1]) && Math.abs(xyz[0])>=Math.abs(xyz[2]))
		{
			for(int ix=xyz[0]>0?0:multiplier-1;ix<multiplier && ix>=0;ix=xyz[0]>0?ix+1:ix-1)
			{
				for(int iy=xyz[1]>0?0:multiplier-1;iy<multiplier && iy>=0;iy=xyz[1]>0?iy+1:iy-1)
				{
					for(int iz=xyz[2]>0?0:multiplier-1;iz<multiplier && iz>=0;iz=xyz[2]>0?iz+1:iz-1)
					{
						power1 = this.getNewPowerAndProcessBlocks(world, exploder, explosion, xyz, multiplier, ix, iy, iz, power1);
					}
				}
			}
		}
		else if(Math.abs(xyz[1])>=Math.abs(xyz[0]) && Math.abs(xyz[1])>=Math.abs(xyz[2]))
		{
			for(int iy=xyz[1]>0?0:multiplier-1;iy<multiplier && iy>=0;iy=xyz[1]>0?iy+1:iy-1)
			{
				for(int ix=xyz[0]>0?0:multiplier-1;ix<multiplier && ix>=0;ix=xyz[0]>0?ix+1:ix-1)
				{
					for(int iz=xyz[2]>0?0:multiplier-1;iz<multiplier && iz>=0;iz=xyz[2]>0?iz+1:iz-1)
					{
						power1 = this.getNewPowerAndProcessBlocks(world, exploder, explosion, xyz, multiplier, ix, iy, iz, power1);
					}
				}
			}
		}
		else
		{
			for(int iz=xyz[2]>0?0:multiplier-1;iz<multiplier && iz>=0;iz=xyz[2]>0?iz+1:iz-1)
			{
				for(int ix=xyz[0]>0?0:multiplier-1;ix<multiplier && ix>=0;ix=xyz[0]>0?ix+1:ix-1)
				{
					for(int iy=xyz[1]>0?0:multiplier-1;iy<multiplier && iy>=0;iy=xyz[1]>0?iy+1:iy-1)
					{
						power1 = this.getNewPowerAndProcessBlocks(world, exploder, explosion, xyz, multiplier, ix, iy, iz, power1);
					}
				}
			}
		}
		power1=Math.round(power1*explosionPowerDampingFactor.get(longNumber)-0.5f);
		//System.out.println("Power after " + power1 + " \n repeats "+repeat);
		if(power1<=1 || !vectors.containsKey(longNumber)|| vectors.get(longNumber).isEmpty())
		{
			exploder.effectBorderBlocks.add(longNumber);
			if(xyz[1]<0 || (exploder.y<=6 && xyz[1]<=6))
			{
				exploder.effectBorderBlocksWithLowPosition.add(longNumber);
			}
			return null;
		}
		else
		{
			for(long d1:vectors.get(longNumber))
			{
				explosionPower.put(d1, power1);
			}
			return vectors.get(longNumber);
		}
	} 
	*/
	
	public Set<Long> breakBlocksAndGetDescendantsForEBS(World world, int sourceX,int sourceY,int sourceZ, Explosion explosion, long longNumber)
	{
		int[] xyz = IHLUtils.decodeXYZ(longNumber);
		int power1 = explosionPower.remove(longNumber);
		power1 = this.getNewPowerAndProcessBlocksEBS(world, longNumber, sourceX, sourceY, sourceZ, explosion, xyz, power1);
		power1=Math.round(power1*explosionPowerDampingFactor.get(longNumber)-0.5f);
		if(power1<=1 || !vectors.containsKey(longNumber)|| vectors.get(longNumber).isEmpty())
		{
			return null;
		}
		else
		{
			for(long d1:vectors.get(longNumber))
			{
				explosionPower.put(d1, power1);
			}
			return vectors.get(longNumber);
		}

	}
/*	
	private int getNewPowerAndProcessBlocks(World world, EntityIHLExplosion exploder, Explosion explosion, int xyz[], int multiplier, int ix, int iy, int iz, int power2)
	{
		int power1=power2;
		int x = exploder.x+xyz[0]*multiplier+ix;
		int y = exploder.y+xyz[1]*multiplier+iy;
		int z = exploder.z+xyz[2]*multiplier+iz;
		if(y<=4 || y>256)
		{
			return 0;
		}
		Block block = exploder.getBlock(x, y, z);
		int explosionResistance = Math.round(block.getExplosionResistance(exploder, world, x, y, z, exploder.x, exploder.y, exploder.z)*10f);
		if(explosionResistance>=power1)
		{
			power1=0;
		}
		else
		{
			power1-=Math.round(block.getExplosionResistance(exploder, world, x, y, z, exploder.x, exploder.y, exploder.z)*10f);
			Entity entity = exploder.getEntity(x, y, z);
			if(entity!=null)
			{
				entity.attackEntityFrom(exploder.damageSource, power1/10f);
			}
			block.onBlockDestroyedByExplosion(world, x, y, z, explosion);
			exploder.setBlockToAir(x, y, z);
		}
		return power1;
	}
	*/
	private int getNewPowerAndProcessBlocksEBS(World world, long longNumber, int sourceX, int sourceY, int sourceZ, Explosion explosion, int xyz[], int power2)
	{
		int power1=power2;
		int absEBSX = xyz[0] + (sourceX>>4);
		int absEBSY = xyz[1] + (sourceY>>4);
		int absEBSZ = xyz[2] + (sourceZ>>4);
    	long chunkXZKey = ChunkCoordIntPair.chunkXZ2Int(absEBSX, absEBSZ);
		if(absEBSY<0 || absEBSY>=16)
		{
			return 0;
		}
		if(world.getChunkProvider().chunkExists(absEBSX, absEBSZ))
		{
	    	long absEBShash = IHLUtils.getXYZHash(absEBSX, absEBSY, absEBSZ);
			int explosionResistance = this.getEBSResistance(world, absEBShash, absEBSX, absEBSY, absEBSZ, sourceX, sourceY, sourceZ);
			if(explosionResistance>=power1)
			{
				power1=0;
			}
			else
			{
				power1-=explosionResistance;
				if(this.cachedEBSEntity.containsKey(absEBShash))
				{
					List<Entity> entityList = this.cachedEBSEntity.get(absEBShash);
					for(Object entity:entityList.toArray())
					{
						if(entity!=null)
						{
							((Entity) entity).attackEntityFrom(DamageSource.setExplosionSource(explosion), power1/10f);
						}
					}
				}
				this.onEBSDestroy(world, absEBShash, absEBSX, absEBSY, absEBSZ);
			}
		}
		else
		{
			WorldSavedDataBlastWave blastWave = null;
			int dimensionId = world.provider.dimensionId;
			if(this.blastWaveByDimensionId.containsKey(dimensionId))
			{
				blastWave=this.blastWaveByDimensionId.get(dimensionId);
			}
			else
			{
				blastWave=new WorldSavedDataBlastWave("blastWave");
				this.blastWaveByDimensionId.put(dimensionId, blastWave);
			}
			blastWave.scheduleExplosionEffectsOnChunkLoad(chunkXZKey, longNumber, sourceX, sourceY, sourceZ, power1, absEBSY);
		}
		return power1;
	}
	
	public int getEBSResistance(World world, long absEBShash, int absEBSX, int absEBSY, int absEBSZ, int sourceX, int sourceY, int sourceZ)
	{
		
		if(this.cachedEBSHardness.containsKey(absEBShash))
		{
			return this.cachedEBSHardness.get(absEBShash);
		}
		else
		{
			this.precacheChunk(world, absEBSX,absEBSZ, sourceX, sourceY, sourceZ);
			return this.cachedEBSHardness.containsKey(absEBShash)?this.cachedEBSHardness.get(absEBShash):0;
		}
	}
	
	public void precacheChunk(World world, int absEBSX, int absEBSZ, int sourceX, int sourceY, int sourceZ)
	{
		Chunk chunk = world.getChunkProvider().provideChunk(absEBSX, absEBSZ);
		ExtendedBlockStorage[] ebsA = chunk.getBlockStorageArray();
		for(int y3=0;y3<ebsA.length;y3++)
		{
			ExtendedBlockStorage ebs = ebsA[y3];
			int ebsHardness = 0;
			long absEBShash1 = IHLUtils.getXYZHash(absEBSX, y3, absEBSZ);
			if(ebs!=null && !ebs.isEmpty())
			{
				byte[] lbsArray = ebs.getBlockLSBArray();
				Map<Long,Integer> drops = new HashMap();
				for(int i4=0;i4<lbsArray.length;i4++)
				{
			        int var4 = lbsArray[i4] & 255;
			    	if (ebs.getBlockMSBArray() != null)
			        {
			            var4 |= ebs.getBlockMSBArray().get(i4 & 15, (i4>>8) & 15, (i4>>4) & 15) << 8;
			        }
			        int blockWorldX = (i4 & 15)+(absEBSX<<4);
			        int blockWorldY = ((i4>>8) & 15)+(y3<<4);
			    	int blockWorldZ = ((i4>>4) & 15)+(absEBSZ<<4);
			        Block block = Block.getBlockById(var4);
			    	List<ItemStack> dropsList = (block.getDrops(world, blockWorldX, blockWorldY, blockWorldZ, ebs.getExtBlockMetadata(i4 & 15, (i4>>8) & 15, (i4>>4) & 15), 0));
			    	if(dropsList!=null)
			    	{
			    		for(ItemStack drop:dropsList)
			    		{
							long key = (Item.getIdFromItem(drop.getItem())<<16)|drop.getItemDamage();
			    			if(drops.containsKey(key))
			    			{
			    				int ss = drops.get(key);
				    			drops.put(key, drop.stackSize+ss);
			    			}
			    			else
			    			{
				    			drops.put(key, drop.stackSize);
			    			}
			    		}
			    	}
			    	if(block.getBlockHardness(world, blockWorldX, blockWorldY, blockWorldZ)<0)
			    	{
				        ebsHardness=Integer.MAX_VALUE;
				        break;
			    	}
			    	else
			    	{
				        ebsHardness+=Math.round(block.getExplosionResistance(null, world, blockWorldX, blockWorldY, blockWorldZ, sourceX, sourceY, sourceZ)*10f);
			    	}
				}
				this.cachedEBSDrops.put(absEBShash1, drops);
				this.cachedEBSEntity.put(absEBShash1, chunk.entityLists[y3]);
				this.cachedEBSHardness.put(absEBShash1, ebsHardness);
				this.cachedEBS.put(absEBShash1, ebs);
			}
		}
	} 
	
	public void onEBSDestroy(World world,long absEBShash,int absEBSX,int absEBSY,int absEBSZ) 
	{
		if(this.cachedEBS.containsKey(absEBShash))
		{
			ExtendedBlockStorage ebs = this.cachedEBS.get(absEBShash);
			ebs.setBlockMSBArray(new NibbleArray(4096, 4));
			ebs.setBlockLSBArray(new byte[4096]);
			this.cachedEBS.remove(absEBShash);
			this.cachedEBSDrops.remove(absEBShash);
			this.cachedEBSEntity.remove(absEBShash);
			this.cachedEBSHardness.remove(absEBShash);
			this.chunksToUpdate.add(world.getChunkProvider().provideChunk(absEBSX, absEBSZ));
		}
	}
	
	public void sendChunkUpdateToPlayersInExplosionAffectedZone(World world)
	{
		for(Object player:world.playerEntities)
		{
			if(player instanceof EntityPlayerMP)
			{
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				List chunks = new ArrayList();
				chunks.addAll(this.chunksToUpdate);
				playerMP.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(chunks));
				this.chunksToUpdate.clear();
			}
		}
	}
	
	public void doExplosion(World world, int sourceX, int sourceY, int sourceZ, Set<Long> startVectors1)
    {
		boolean doExplosion=true;
		long sourceHash = IHLUtils.getXYZHash(sourceX, sourceY, sourceZ);
		Explosion explosion = new Explosion(world, null, sourceX, sourceY, sourceZ, 100f);
		explosions.put(sourceHash, explosion);
		while(doExplosion)
		{
			Set<Long> sv2=new HashSet();
    		if(sv==null)
    		{
        		sv = startVectors1;
    		}
   			for(long ev:sv)
   			{
   				Set<Long> sv3 = this.breakBlocksAndGetDescendantsForEBS(world, sourceX, sourceY, sourceZ, explosion, ev);
   				if(sv3!=null)
   				{
   	   				sv2.addAll(sv3);
   				}
 			}
   			if(sv2.isEmpty())
   			{
   				doExplosion=false;
   				sv = null;
   				sendChunkUpdateToPlayersInExplosionAffectedZone(world);
   				break;
   			}
   			else
   			{
   				sv=sv2;
   			}
		}
		

    }


}
