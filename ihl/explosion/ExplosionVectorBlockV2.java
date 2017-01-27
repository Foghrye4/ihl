package ihl.explosion;

import ihl.IHLMod;
import ihl.utils.IHLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ExplosionVectorBlockV2 
{
	public int[] startVectors = new int[1];
	private final int[][] directionMasks = new int[8][3];
	private final int bits = 8;
	private final int maxValue = (1<<bits)-1;
	private final int maxArraySize = (1<<bits*3)-1;
	public final int[][] vectors = new int[maxArraySize][2];
	public final float[] explosionPowerDampingFactor = new float[maxArraySize];
	public final int[] explosionPower = new int[maxArraySize];
	public final int[] cachedBlockHardness = new int[maxArraySize];
	public final int[] cachedFluidBlocks = new int[maxArraySize];
	public Map<Integer,List<ItemStack>> cachedBlockDrops = new HashMap<Integer, List<ItemStack>>(128);
	public final List<int[]> dropBlocksPos = new ArrayList<int[]>(64);
	public Set<Chunk> chunksToUpdate=new HashSet<Chunk>(64);
	public Map<Integer,ItemStack> cachedDrops = new HashMap<Integer, ItemStack>(128);
	public Set<Integer> dropBlocks = new HashSet<Integer>();
	public Map<Integer,WorldSavedDataBlastWave> blastWaveByDimensionId = new HashMap<Integer, WorldSavedDataBlastWave>();
	private Entity[] cachedEntities = new Entity[maxArraySize];

	public ExplosionVectorBlockV2()
	{
		this.precalculateExplosion();
		directionMasks[0] = new int[] {1,1,1};
		directionMasks[1] = new int[] {-1,1,1};
		directionMasks[2] = new int[] {1,-1,1};
		directionMasks[3] = new int[] {1,1,-1};
		directionMasks[4] = new int[] {-1,-1,1};
		directionMasks[5] = new int[] {1,-1,-1};
		directionMasks[6] = new int[] {-1,1,-1};
		directionMasks[7] = new int[] {-1,-1,-1};
		Arrays.fill(cachedBlockHardness,-1);
	}
	
	private int encodeXYZ(int x, int y, int z)
	{
		return x<<bits*2|y<<bits|z;
	}
	
	private int[] decodeXYZ(int l)
	{
		return new int[] {l>>bits*2,l>>bits&maxValue,l&maxValue};
	}
	
	public void precalculateExplosion()
	{
		startVectors[0]=0;
		explosionPowerDampingFactor[0]=1f;
		for(int levelRadius=1; levelRadius<this.maxValue; levelRadius++)
		for(int ix=0;ix<=levelRadius;ix++)
		for(int iy=0;iy<=levelRadius;iy++)
		for(int iz=(ix==levelRadius||iy==levelRadius)?0:levelRadius;iz<=levelRadius;iz++)
		{
			int vxyz = encodeXYZ(ix,iy,iz);
			int[] prevXYZ= new int[] {ix,iy,iz};
			reduceCoordinate(prevXYZ, levelRadius);
			int pvxyz = encodeXYZ(prevXYZ[0],prevXYZ[1],prevXYZ[2]);
			findFreeSpace(pvxyz,vxyz);
			float df = (float)(prevXYZ[0]*prevXYZ[0]+prevXYZ[1]*prevXYZ[1]+prevXYZ[2]*prevXYZ[2]+64)/(float)(ix*ix+iy*iy+iz*iz+64);
			explosionPowerDampingFactor[vxyz]=df;
		}
	}
	
	private void findFreeSpace(int pvxyz,int vxyz)
	{
		if(vectors[pvxyz][0]==0)
		{
			vectors[pvxyz][0]=vxyz;
		}
		else if(vectors[pvxyz][1]==0)
		{
			vectors[pvxyz][1]=vxyz;
		}
		else
		{
			findFreeSpace(vectors[pvxyz][0],vxyz);
		}
	}

	private void reduceCoordinate(int[] pxyz, int levelRadius)
	{
		if(Math.abs(pxyz[0])+Math.abs(pxyz[1])+Math.abs(pxyz[2])>Math.round(levelRadius*1.8f))
		{
			pxyz[0]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[0]);
 			pxyz[1]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[1]);
			pxyz[2]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[2]);
		}
		else if(Math.abs(pxyz[0])<=Math.abs(pxyz[1]) && Math.abs(pxyz[0])<=Math.abs(pxyz[2]))
		{
			pxyz[1]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[1]);
			pxyz[2]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[2]);
		}
		else if(Math.abs(pxyz[1])<=Math.abs(pxyz[0]) && Math.abs(pxyz[1])<=Math.abs(pxyz[2]))
		{
			pxyz[0]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[0]);
			pxyz[2]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[2]);
		}
		else if(Math.abs(pxyz[2])<=Math.abs(pxyz[0]) && Math.abs(pxyz[2])<=Math.abs(pxyz[1]))
		{
			pxyz[1]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[1]);
			pxyz[0]=IHLUtils.reduceVariableByAbsoluteValue(pxyz[0]);
		}
	}
	
	public void setPower(int[] sv2, int power1) 
	{
		for(int ev:sv2)
		{
			this.setPower(ev, power1);
		}
	}

	public void setPower(int ev, int power1) {
		this.explosionPower[ev]=power1;
	}

	public int[] breakBlocksAndGetDescendants(World world, int sourceX,int sourceY,int sourceZ, Explosion explosion, int ev, int[] directionMask)
	{
		int power1 = explosionPower[ev];
		power1 = this.getNewPowerAndProcessBlocks(world, ev, sourceX, sourceY, sourceZ, explosion, power1, directionMask);
		power1 = (int)(power1*explosionPowerDampingFactor[ev])-1;
		if(power1<=1 || this.vectors[ev][0]==0)
		{
			return null;
		}
		else
		{
			for(int d1:this.vectors[ev])
			{
				if(d1!=0)
				{
					explosionPower[d1]=power1;
				}
			}
			return this.vectors[ev];
		}
	} 
	
	private int getNewPowerAndProcessBlocks(World world, int ev, int sourceX, int sourceY, int sourceZ, Explosion explosion, int power2, int[] directionMask)
	{
		int power1=power2;
		int[] xyz = decodeXYZ(ev);
		int absX = xyz[0]*directionMask[0] + sourceX;
		int absY = xyz[1]*directionMask[1] + sourceY;
		int absZ = xyz[2]*directionMask[2] + sourceZ;
		if(absY<0 || absY>=256)
		{
			return 0;
		}
    	int absEBSX = absX>>4;
    	int absEBSZ = absZ>>4;
		if(world.getChunkProvider().chunkExists(absEBSX, absEBSZ))
		{
			if(world.getChunkProvider().provideChunk(absEBSX, absEBSZ).getTopFilledSegment()+24>=absY)
			{
				int explosionResistance = this.getBlockResistance(world, ev, absX, absY, absZ, sourceX, sourceY, sourceZ, explosion, directionMask);
				if(explosionResistance>=power1)
				{
					if(this.cachedFluidBlocks[ev]!=0)
					{
						//IHLMod.log.info("Schleduling update for "+Block.getBlockById(this.cachedFluidBlocks[ev]).getUnlocalizedName());
						Block block = Block.getBlockById(this.cachedFluidBlocks[ev]);
						block.onNeighborBlockChange(world, absX, absY, absZ, block);
					}
					else
					{
						this.dropBlocksPos.add(new int[] {absX, absY+1, absZ});
					}
					return 0;
				}
				else
				{
					power1-=explosionResistance;
					this.onBlockDestroy(world, ev, absX, absY, absZ, power1, explosion);
					return power1;
				}
			}
			else
			{
				return 0;
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
	    	long chunkXZKey = ChunkCoordIntPair.chunkXZ2Int(absEBSX, absEBSZ);
			blastWave.scheduleExplosionEffectsOnChunkLoad(chunkXZKey, ev, sourceX, sourceY, sourceZ, power1, directionMask);
			return 0;
		}
	}
	
	public int getBlockResistance(World world, int ev, int absX, int absY, int absZ, int sourceX, int sourceY, int sourceZ, Explosion explosion, int[] directionMask)
	{
		if(this.cachedBlockHardness[ev]!=-1)
		{
			return this.cachedBlockHardness[ev];
		}
		else
		{
			this.precacheEBSBlocks(world, absX, absY, absZ, sourceX, sourceY, sourceZ, explosion, directionMask);
			return this.cachedBlockHardness[ev];
		}
	}

	public void precacheEBSBlocks(World world, int absX, int absY, int absZ, int sourceX, int sourceY, int sourceZ, Explosion explosion, int[] directonMask)
	{
		Chunk chunk = world.getChunkProvider().provideChunk(absX>>4, absZ>>4);
		List<Entity> eList = this.getEntityList(chunk, absX, absY, absZ);
		if(eList!=null)
		{
			Iterator<Entity> eListI = eList.iterator();
			while(eListI.hasNext())
			{
				Entity entity = eListI.next();
				int entityX = (int)entity.boundingBox.minX;
				int entityY = (int)entity.boundingBox.minY;
				int entityZ = (int)entity.boundingBox.minZ;
				int rx = (entityX - sourceX) * directonMask[0];
				int ry = (entityY - sourceY) * directonMask[1];
				int rz = (entityZ - sourceZ) * directonMask[2];
				int ev = this.encodeXYZ(rx, ry, rz);
				if(rx >= 0 && ry >= 0 && rz >= 0 && ev>=0 && ev<this.maxArraySize)
				{
					this.cachedEntities[ev]=entity;
				}
			}
		}
		ExtendedBlockStorage ebs = this.getEBS(chunk, absX, absY, absZ);
		if(ebs==null)
		{
			for(int i4=0;i4<4096;i4++)
			{
				int blockWorldX = (i4 & 15)|(absX & 0xFFFFFFF0);
				int blockWorldY = ((i4>>8) & 15)|(absY & 0xFFFFFFF0);
				int blockWorldZ = ((i4>>4) & 15)|(absZ & 0xFFFFFFF0);
	    	
				int rx = (blockWorldX - sourceX) * directonMask[0];
				int ry = (blockWorldY - sourceY) * directonMask[1];
				int rz = (blockWorldZ - sourceZ) * directonMask[2];
			
				int ev = this.encodeXYZ(rx, ry, rz);
				if(rx >= 0 && ry >= 0 && rz >= 0 && ev>=0 && ev<this.maxArraySize)
				{
					this.cachedBlockHardness[ev]=0;
				}
			}
		}
		else
		{
			byte[] lbsArray = ebs.getBlockLSBArray();
			for(int i4=0;i4<lbsArray.length;i4++)
			{
		        int var4 = lbsArray[i4] & 255;
		    	if (ebs.getBlockMSBArray() != null)
		        {
		            var4 |= ebs.getBlockMSBArray().get(i4 & 15, (i4>>8) & 15, (i4>>4) & 15) << 8;
		        }
		        int blockWorldX = (i4 & 15)|(absX & 0xFFFFFFF0);
		        int blockWorldY = ((i4>>8) & 15)|(absY & 0xFFFFFFF0);
		    	int blockWorldZ = ((i4>>4) & 15)|(absZ & 0xFFFFFFF0);
				int rx = (blockWorldX - sourceX) * directonMask[0];
				int ry = (blockWorldY - sourceY) * directonMask[1];
				int rz = (blockWorldZ - sourceZ) * directonMask[2];
			
				int ev = this.encodeXYZ(rx, ry, rz);
				if(rx >= 0 && ry >= 0 && rz >= 0 && ev>=0 && ev<this.maxArraySize)
				{
			        Block block = Block.getBlockById(var4);
			        if(block.canDropFromExplosion(explosion))
			        {
				    	List<ItemStack> dropsList = (block.getDrops(world, blockWorldX, blockWorldY, blockWorldZ, ebs.getExtBlockMetadata(i4 & 15, (i4>>8) & 15, (i4>>4) & 15), 0));
				    	if(dropsList!=null)
				    	{
							this.cachedBlockDrops.put(ev, dropsList);
				    	}
			        }
			    	if(block.getBlockHardness(world, blockWorldX, blockWorldY, blockWorldZ)<0)
			    	{
						this.cachedBlockHardness[ev]=Integer.MAX_VALUE;
			    	}
			    	else if(ev==0 && directonMask[0]+directonMask[1]+directonMask[2]==3)
			    	{
						this.cachedBlockHardness[ev]=0;
			    	}
			    	else
			    	{
			    		int br=Math.round(block.getExplosionResistance(null, world, blockWorldX, blockWorldY, blockWorldZ, sourceX, sourceY, sourceZ)*10f);
						this.cachedBlockHardness[ev]=br;
			    	}
			    	if(block.getMaterial().isLiquid())
			    	{
			    		this.cachedFluidBlocks[ev]=var4;
			    	}
				}
			}
		}
	} 
	
	public ExtendedBlockStorage getEBS(Chunk chunk, int absX, int absY, int absZ)
	{
		ExtendedBlockStorage[] ebsA = chunk.getBlockStorageArray();
		ExtendedBlockStorage ebs = ebsA[absY>>4];
		if(ebs!=null)
		{
			this.chunksToUpdate.add(chunk);
		}
		return ebs;
	}

	@SuppressWarnings("unchecked")
	public List<Entity> getEntityList(Chunk chunk, int absX, int absY, int absZ)
	{
		return chunk.entityLists[absY>>4];
	}

	
	public void onBlockDestroy(World world, int ev, int absX,int absY,int absZ, int power, Explosion explosion) 
	{
		Chunk chunk = world.getChunkProvider().provideChunk(absX>>4, absZ>>4);
		ExtendedBlockStorage ebs = this.getEBS(chunk, absX, absY, absZ);
		if(ebs==null || ebs.isEmpty())
		{
			return;
		}
       	int array_index = (absY & 15)<<8 | (absZ & 15)<<4 | (absX & 15);
       	if(ebs.getBlockLSBArray()[array_index] != 0 && 
       			ebs.getBlockMSBArray()!=null && 
       			ebs.getBlockMSBArray().get(absX & 15, absY & 15, absZ & 15) != 0)
       	{
    		ebs.blockRefCount--;
       	}
		ebs.getBlockLSBArray()[array_index] = 0;
		if(ebs.getBlockMSBArray()!=null){
			ebs.getBlockMSBArray().set(absX & 15, absY & 15, absZ & 15, 0);
		}
		if(this.cachedBlockDrops.containsKey(ev))
		{
			Iterator<ItemStack> drops = this.cachedBlockDrops.remove(ev).iterator();
			while(drops.hasNext())
			{
				ItemStack drop = drops.next();
				int key = Item.getIdFromItem(drop.getItem())^(drop.getItemDamage()<<16);
				if(this.cachedDrops.containsKey(key))
				{
					this.cachedDrops.get(key).stackSize+=drop.stackSize;
				}
				else
				{
					this.cachedDrops.put(key, drop);
				}
			}
		}
		Entity victim = this.cachedEntities[ev];
		if(victim!=null)
		{
			victim.attackEntityFrom(DamageSource.setExplosionSource(explosion), power/10f);
			this.cachedEntities[ev]=null;
		}
		this.dropBlocks.add(ev);
	}

	private void addDrops(World world, int sourceX, int sourceY, int sourceZ, int[] directionMask) 
	{
		Iterator<Entry<Integer, ItemStack>> di = this.cachedDrops.entrySet().iterator();
		Entry<Integer, ItemStack> cde = di.next();
		while(di.hasNext() && this.dropBlocksPos.size() > 0)
		{
			int[] xyz = this.dropBlocksPos.remove(this.dropBlocksPos.size()-1);
			int x = xyz[0];
			int y = xyz[1];
			int z = xyz[2];
			int rx = (x - sourceX) * directionMask[0];
			int ry = (y - sourceY) * directionMask[1];
			int rz = (z - sourceZ) * directionMask[2];
			int ev = encodeXYZ(rx,ry,rz);
			int ev2 = encodeXYZ(rx,ry-1,rz);
			if(this.dropBlocks.contains(ev) && !this.dropBlocks.contains(ev2))
			{
				ItemStack stack = cde.getValue();
				if(stack!=null && stack.getItem()!=null && stack.stackSize>0)
				{
					if(stack.stackSize <= stack.getMaxStackSize())
					{
						if(stack.stackSize>0)
						{
	                        PileTileEntity pte = new PileTileEntity();
	                        pte.xCoord=x;
	                        pte.yCoord=y;
	                        pte.zCoord=z;
	                        pte.setWorldObj(world);
	                        pte.validate();
	                        pte.setContent(stack);
	                        IHLUtils.setBlockAndTileEntityRaw(world, x, y, z, PileBlock.instance, pte);
    					}
						di.remove();
						if(di.hasNext())
						{
							cde = di.next();
						}
						else
						{
							return;
						}
					}
					else
					{
						ItemStack stack1=stack.copy();
						stack1.stackSize=stack.getMaxStackSize();
	                    PileTileEntity pte = new PileTileEntity();
	                    pte.content=stack1;
	                    IHLUtils.setBlockAndTileEntityRaw(world, x, y, z, PileBlock.instance, pte);
    					stack.stackSize-=stack.getMaxStackSize();
					}
				}
			}
		}
	}
	
	public void sendChunkUpdateToPlayersInExplosionAffectedZone(World world, int sourceX, int sourceY, int sourceZ)
	{
//		Set<Long> clientSideChunkXZKeySet = new HashSet<Long>();
		Iterator<Chunk> ci = this.chunksToUpdate.iterator();
		while(ci.hasNext())
		{
			Chunk chunk = ci.next();
			chunk.generateSkylightMap();
			Arrays.fill(chunk.updateSkylightColumns, true);
			chunk.func_150804_b(false);
//	    	long chunkXZKey = ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition);
//	    	clientSideChunkXZKeySet.add(chunkXZKey);
		}
		List<Chunk> chunks = new ArrayList<Chunk>();
		chunks.addAll(this.chunksToUpdate);
		for(Object player:world.playerEntities)
		{
			if(player instanceof EntityPlayerMP)
			{
				EntityPlayerMP playerMP = (EntityPlayerMP)player;
				playerMP.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(chunks));
			}
		}
//		IHLMod.proxy.sendChunksLightUpdateQuery(world, sourceX, sourceY, sourceZ, clientSideChunkXZKeySet);
		this.chunksToUpdate.clear();
	}

	public void doExplosion(World world, int sourceX, int sourceY, int sourceZ, final int[] startVectors1, int[] directionMask, Explosion explosion)
    {
		boolean doExplosion=true;
		int[] sv = null;
		int svsize = 0;
		while(doExplosion)
		{
			int[] sv2= new int[1<<bits*2];
			int sv2size = 0;
    		if(sv==null)
    		{
        		sv = startVectors1;
        		svsize = startVectors1.length;
    		}
   			for(int i=0;i<svsize;i++)
   			{
   				int[] sv3 = this.breakBlocksAndGetDescendants(world, 
   						sourceX-(directionMask[0]<0?1:0),
   						sourceY-(directionMask[1]<0?1:0),
   						sourceZ-(directionMask[2]<0?1:0),
   						explosion, sv[i],directionMask);
   				if(sv3!=null)
   				{
   					for(int sv3v:sv3)
   					{
   						if(sv3v!=0)
   						{
   	   						sv2[sv2size]=sv3v;
   	   						sv2size++;
   						}
   					}
   				}
 			}
   			if(sv2size==0)
   			{
   				doExplosion=false;
   				sv = null;
   				if(!this.cachedDrops.isEmpty())
   				{
	   				addDrops(world, sourceX, sourceY, sourceZ, directionMask);
   				}
   				break;
   			}
   			else
   			{
   				sv=sv2;
   				svsize=sv2size;
   			}
		}
		// Free and clean resources
		this.cachedBlockDrops.clear();
		this.cachedDrops.clear();
		this.dropBlocks.clear();
		this.dropBlocksPos.clear();
		Arrays.fill(cachedBlockHardness,-1);
		Arrays.fill(cachedFluidBlocks,0);
    }

	public void doExplosion(World world, int sourceX, int sourceY, int sourceZ, final int[] startVectors1)
    {
		IHLMod.log.info("Starting explosion server");
		Explosion explosion = new Explosion(world, null, sourceX, sourceY, sourceZ, 100f);
		for(int[] directionMask:directionMasks)
		{
			this.doExplosion(world, sourceX, sourceY, sourceZ, startVectors1, directionMask, explosion);
		}
		sendChunkUpdateToPlayersInExplosionAffectedZone(world, sourceX, sourceY, sourceZ);
	}
}