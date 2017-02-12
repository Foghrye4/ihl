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

public class ExplosionVectorBlockV2 {
	final Set<Integer> startVectors = new HashSet<Integer>();
	private final int[][] directionMasks = new int[8][3];
	private final int bits = IHLMod.config.explosionVectorSizeBits;
	private final int maxValue = (1 << bits) - 1;
	private final int halfValue = (1 << bits - 1) - 1;
	private final int maxArraySize = 1 << bits * 3;
	private final int[][] vectors = new int[maxArraySize][2];
	private final Set<Chunk> chunksToUpdate = new HashSet<Chunk>(64);
	private final Map<Integer, ItemStack> cachedDrops = new HashMap<Integer, ItemStack>(128);
	final Map<Integer, WorldSavedDataBlastWave> blastWaveByDimensionId = new HashMap<Integer, WorldSavedDataBlastWave>();
	private final Map<ExtendedBlockStorage, Entity[]> cachedEntities = new HashMap<ExtendedBlockStorage, Entity[]>();
	public boolean isCalculating = false;

	public ExplosionVectorBlockV2() {
		this.precalculateExplosion();
		startVectors.add(0);
		directionMasks[0] = new int[] { 1, 1, 1 };
		directionMasks[1] = new int[] { -1, 1, 1 };
		directionMasks[2] = new int[] { 1, -1, 1 };
		directionMasks[3] = new int[] { 1, 1, -1 };
		directionMasks[4] = new int[] { -1, -1, 1 };
		directionMasks[5] = new int[] { 1, -1, -1 };
		directionMasks[6] = new int[] { -1, 1, -1 };
		directionMasks[7] = new int[] { -1, -1, -1 };
	}

	private int encodeXYZ(int x, int y, int z) {
		return x << bits * 2 | y << bits | z;
	}

	private int[] decodeXYZ(int l) {
		return new int[] { l >> bits * 2, l >> bits & maxValue, l & maxValue };
	}

	public void precalculateExplosion() {
		for (int levelRadius = 1; levelRadius <= this.maxValue; levelRadius++)
			for (int ix = 0; ix <= levelRadius; ix++)
				for (int iy = 0; iy <= levelRadius; iy++)
					for (int iz = (ix == levelRadius || iy == levelRadius) ? 0 : levelRadius; iz <= levelRadius; iz++) {
						int vxyz = encodeXYZ(ix, iy, iz);
						int[] prevXYZ = new int[] { ix, iy, iz };
						reduceCoordinate(prevXYZ);
						int pvxyz = encodeXYZ(prevXYZ[0], prevXYZ[1], prevXYZ[2]);
						findFreeSpace(pvxyz, vxyz);
					}
	}

	private void findFreeSpace(int pvxyz, int vxyz) {
		if (vectors[pvxyz][0] == 0) {
			vectors[pvxyz][0] = vxyz;
		} else if (vectors[pvxyz][1] == 0) {
			vectors[pvxyz][1] = vxyz;
		} else {
			findFreeSpace(vectors[pvxyz][0], vxyz);
		}
	}

	private void reduceCoordinate(int[] pxyz) {

		if (pxyz[0] >= pxyz[1] && pxyz[0] >= pxyz[2] && pxyz[0] > 0) {
			pxyz[0]--;
		}
		if (pxyz[1] >= pxyz[0] && pxyz[1] >= pxyz[2] && pxyz[1] > 0) {
			pxyz[1]--;
		}
		if (pxyz[2] >= pxyz[1] && pxyz[2] >= pxyz[0] && pxyz[2] > 0) {
			pxyz[2]--;
		}
	}

	public void breakBlocksAndGetDescendants(World world, int sourceX, int sourceY, int sourceZ, Explosion explosion,
			int ev, int power, int[] directionMask) {
		power = this.getNewPowerAndProcessBlocks(world, ev, sourceX, sourceY, sourceZ, explosion, power, directionMask);
		power = (int) (power * 0.94) - 1;
		if (power > 1) {
			if (this.vectors[ev][0] == 0) {
				int[] xyz = decodeXYZ(ev);
				int xb = xyz[0] >> bits - 1;
				int yb = xyz[1] >> bits - 1;
				int zb = xyz[2] >> bits - 1;
				int hashb = xb << 2 | yb << 1 | zb;
				xyz[0] -= xb * halfValue;
				xyz[1] -= yb * halfValue;
				xyz[2] -= zb * halfValue;
				if (hashb == 0 || xb > 1 || yb > 1 || zb > 1) {
					throw new ArithmeticException("End vectors shall be higher than half value");
				}
				int ev2 = encodeXYZ(xyz[0], xyz[1], xyz[2]);
				breakBlocksAndGetDescendants(world, sourceX + xb * halfValue * directionMask[0],
						sourceY + yb * halfValue * directionMask[1], sourceZ + zb * halfValue * directionMask[2],
						explosion, ev2, power, directionMask);
			} else {
				for (int d1 : this.vectors[ev]) {
					if (d1 != 0) {
						breakBlocksAndGetDescendants(world, sourceX, sourceY, sourceZ, explosion, d1, power,
								directionMask);
					}
				}
			}
		}
	}

	private int getNewPowerAndProcessBlocks(World world, int ev, int sourceX, int sourceY, int sourceZ,
			Explosion explosion, int power2, int[] directionMask) {
		int power1 = power2;
		int[] xyz = decodeXYZ(ev);
		int absX = xyz[0] * directionMask[0] + sourceX;
		int absY = xyz[1] * directionMask[1] + sourceY;
		int absZ = xyz[2] * directionMask[2] + sourceZ;
		if (absY < 0 || absY >= 256) {
			return 0;
		}
		int absEBSX = absX >> 4;
		int absEBSZ = absZ >> 4;
		if (world.getChunkProvider().chunkExists(absEBSX, absEBSZ)) {
			if (world.getChunkProvider().provideChunk(absEBSX, absEBSZ).getTopFilledSegment() + 24 >= absY) {
				int remainingPower = this.tryDestroyBlock(world, absX, absY, absZ, sourceX, sourceY, sourceZ, power1,
						explosion);
				return remainingPower;
			} else {
				return 0;
			}
		} else {
			WorldSavedDataBlastWave blastWave = null;
			int dimensionId = world.provider.dimensionId;
			if (this.blastWaveByDimensionId.containsKey(dimensionId)) {
				blastWave = this.blastWaveByDimensionId.get(dimensionId);
			} else {
				blastWave = new WorldSavedDataBlastWave("blastWave");
				this.blastWaveByDimensionId.put(dimensionId, blastWave);
			}
			long chunkXZKey = ChunkCoordIntPair.chunkXZ2Int(absEBSX, absEBSZ);
			blastWave.scheduleExplosionEffectsOnChunkLoad(chunkXZKey, ev, sourceX, sourceY, sourceZ, power1,
					directionMask);
			return 0;
		}
	}

	public int tryDestroyBlock(World world, int absX, int absY, int absZ, int sourceX, int sourceY, int sourceZ,
			int power, Explosion explosion) {
		Chunk chunk = world.getChunkProvider().provideChunk(absX >> 4, absZ >> 4);
		ExtendedBlockStorage ebs = this.getEBS(chunk, absX, absY, absZ);
		if (ebs == null) {
			return power;
		} else {
			Block block = ebs.getBlockByExtId(absX & 15, absY & 15, absZ & 15);
			if (block.getBlockHardness(world, absX, absY, absZ) < 0) {
				return 0;
			} else if (absX == sourceX && absY == sourceY && absZ == sourceZ) {
				int array_index = (absY & 15) << 8 | (absZ & 15) << 4 | (absX & 15);
				if (ebs.getBlockLSBArray()[array_index] != 0 && ebs.getBlockMSBArray() != null
						&& ebs.getBlockMSBArray().get(absX & 15, absY & 15, absZ & 15) != 0) {
					ebs.blockRefCount--;
				}
				ebs.getBlockLSBArray()[array_index] = 0;
				if (ebs.getBlockMSBArray() != null) {
					ebs.getBlockMSBArray().set(absX & 15, absY & 15, absZ & 15, 0);
				}
				return power;
			} else {
				int remainingPower = power
						- (int) (block.getExplosionResistance(null, world, absX, absY, absZ, sourceX, sourceY, sourceZ)
								* 10f + 0.5f);
				if (remainingPower >= 0) {
					int array_index = (absY & 15) << 8 | (absZ & 15) << 4 | (absX & 15);
					if (ebs.getBlockLSBArray()[array_index] != 0 && ebs.getBlockMSBArray() != null
							&& ebs.getBlockMSBArray().get(absX & 15, absY & 15, absZ & 15) != 0) {
						ebs.blockRefCount--;
					}
					ebs.getBlockLSBArray()[array_index] = 0;
					if (ebs.getBlockMSBArray() != null) {
						ebs.getBlockMSBArray().set(absX & 15, absY & 15, absZ & 15, 0);
					}
					List<ItemStack> dropsList = block.getDrops(world, absX, absY, absZ,
							ebs.getExtBlockMetadata(absX & 15, absY & 15, absZ & 15), 0);
					Iterator<ItemStack> drops = dropsList.iterator();
					while (drops.hasNext()) {
						ItemStack drop = drops.next();
						int key = Item.getIdFromItem(drop.getItem()) ^ (drop.getItemDamage() << 16);
						if (this.cachedDrops.containsKey(key)) {
							this.cachedDrops.get(key).stackSize += drop.stackSize;
						} else {
							this.cachedDrops.put(key, drop);
						}
					}
					Entity[] entities = this.getFromOrCreateCache(world, ebs, absX, absY, absZ);
					if (entities != null && entities[array_index] != null) {
						entities[array_index].attackEntityFrom(DamageSource.setExplosionSource(explosion), power / 10f);
					}
				} else {
					block.onNeighborBlockChange(world, absX, absY, absZ, block);
					if ((++absY & 15) != 0) {
						int array_index = (absY & 15) << 8 | (absZ & 15) << 4 | (absX & 15);
						if (ebs.getBlockLSBArray()[array_index] == 0 && (ebs.getBlockMSBArray() == null
								|| ebs.getBlockMSBArray().get(absX & 15, absY & 15, absZ & 15) == 0)) {
							this.placeDrops(world, absX, absY, absZ);
						}
					}
				}
				return remainingPower;
			}
		}
	}

	public Entity[] getFromOrCreateCache(World world, ExtendedBlockStorage ebs, int absX, int absY, int absZ) {
		Entity[] entities = this.cachedEntities.get(ebs);
		if (entities == null) {
			Chunk chunk = world.getChunkProvider().provideChunk(absX >> 4, absZ >> 4);
			List<Entity> eList = this.getEntityList(chunk, absX, absY, absZ);
			if (eList != null && !eList.isEmpty()) {
				entities = new Entity[4096];
				Iterator<Entity> eListI = eList.iterator();
				this.cachedEntities.put(ebs, entities);
				while (eListI.hasNext()) {
					Entity entity = eListI.next();
					int entityX = (int) entity.boundingBox.minX;
					int entityY = (int) entity.boundingBox.minY;
					int entityZ = (int) entity.boundingBox.minZ;
					int rx = entityX & 15;
					int ry = entityY & 15;
					int rz = entityZ & 15;
					int array_index = ry << 8 | rz << 4 | rx;
					entities[array_index] = entity;
				}
			}
		}
		return entities;
	}

	public ExtendedBlockStorage getEBS(Chunk chunk, int absX, int absY, int absZ) {
		ExtendedBlockStorage[] ebsA = chunk.getBlockStorageArray();
		ExtendedBlockStorage ebs = ebsA[absY >> 4];
		if (ebs != null) {
			this.chunksToUpdate.add(chunk);
		}
		return ebs;
	}

	@SuppressWarnings("unchecked")
	public List<Entity> getEntityList(Chunk chunk, int absX, int absY, int absZ) {
		return chunk.entityLists[absY >> 4];
	}

	private void placeDrops(World world, int x, int y, int z) {
		Iterator<Entry<Integer, ItemStack>> di = this.cachedDrops.entrySet().iterator();
		if (di.hasNext()) {
			Entry<Integer, ItemStack> cde = di.next();
			ItemStack stack = cde.getValue();
			if (stack != null && stack.getItem() != null && stack.stackSize > 0) {
				if (stack.stackSize <= stack.getMaxStackSize()) {
					if (stack.stackSize > 0) {
						PileTileEntity pte = new PileTileEntity();
						pte.xCoord = x;
						pte.yCoord = y;
						pte.zCoord = z;
						pte.setWorldObj(world);
						pte.validate();
						pte.setContent(stack);
						IHLUtils.setBlockAndTileEntityRaw(world, x, y, z, PileBlock.instance, pte);
					}
					di.remove();
				} else {
					ItemStack stack1 = stack.copy();
					stack1.stackSize = stack.getMaxStackSize();
					PileTileEntity pte = new PileTileEntity();
					pte.content = stack1;
					IHLUtils.setBlockAndTileEntityRaw(world, x, y, z, PileBlock.instance, pte);
					stack.stackSize -= stack.getMaxStackSize();
				}
			}
		}
	}

	public void sendChunkUpdateToPlayersInExplosionAffectedZone(World world, int sourceX, int sourceY, int sourceZ) {
		Iterator<Chunk> ci = this.chunksToUpdate.iterator();
		while (ci.hasNext()) {
			Chunk chunk = ci.next();
			chunk.generateSkylightMap();
			Arrays.fill(chunk.updateSkylightColumns, true);
			chunk.func_150804_b(false);
		}
		List<Chunk> chunks = new ArrayList<Chunk>();
		chunks.addAll(this.chunksToUpdate);
		for (Object player : world.playerEntities) {
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				playerMP.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(chunks));
			}
		}
		this.chunksToUpdate.clear();
	}

	public void doExplosion(World world, int sourceX, int sourceY, int sourceZ, final Set<Integer> startVectors1,
			int startPower, int[] directionMask, Explosion explosion) {
		for (int sv : startVectors1) {
			this.breakBlocksAndGetDescendants(world, sourceX - (directionMask[0] < 0 ? 1 : 0),
					sourceY - (directionMask[1] < 0 ? 1 : 0), sourceZ - (directionMask[2] < 0 ? 1 : 0), explosion, sv,
					startPower, directionMask);
		}
		// Free and clean resources
		this.cachedDrops.clear();
		this.cachedEntities.clear();
	}

	public void doExplosion(World world, int sourceX, int sourceY, int sourceZ, final Set<Integer> startVectors1,
			int startPower) {
		IHLMod.log.info("Starting explosion server");
		isCalculating = true;
		Explosion explosion = new Explosion(world, null, sourceX, sourceY, sourceZ, 100f);
		for (int[] directionMask : directionMasks) {
			this.doExplosion(world, sourceX, sourceY, sourceZ, startVectors1, startPower, directionMask, explosion);
		}
		sendChunkUpdateToPlayersInExplosionAffectedZone(world, sourceX, sourceY, sourceZ);
		isCalculating = false;
	}
}