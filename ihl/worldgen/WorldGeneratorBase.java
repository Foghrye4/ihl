package ihl.worldgen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ihl.utils.IHLUtils;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public abstract class WorldGeneratorBase {
	private final Random random = new Random();
	protected final Block ore;
	private final Set<Block> replaceableBlocks = new HashSet<Block>(2);

	public WorldGeneratorBase(Block oreIn, Block... replaceableBlocksIn) {
		ore = oreIn;
		for (Block block : replaceableBlocksIn) {
			replaceableBlocks.add(block);
		}
	}

	public void generate(World world, int chunkX, int chunkZ) {
		int[] centralPOI = this.getPOI(world, chunkX, chunkZ, 0, 0);
		int[] xz = new int[] { 0, 1, 0, -1, 0 };
		for (int i = 0; i < 4; i++) {
			int[] surroundPOI = this.getPOI(world, chunkX + xz[i], chunkZ + xz[i + 1], xz[i], xz[i + 1]);
			this.replaceBlocks(world, centralPOI, surroundPOI, chunkX << 4, chunkZ << 4);
		}
	}

	protected abstract void replaceBlocks(World world, final int[] centralPOI, final int[] surroundPOI,
			final int startX, final int startZ);

	protected boolean replace(World world, int absX, int absY, int absZ, final Block block) {
		if (!world.getChunkProvider().chunkExists(absX >> 4, absZ >> 4)) {
			throw new IllegalStateException("quered chunk is not yet generated!");
		}
		Chunk chunk = world.getChunkFromBlockCoords(absX, absZ);
		ExtendedBlockStorage ebs = chunk.getBlockStorageArray()[absY >> 4];
		if (ebs != null && replaceableBlocks.contains(ebs.getBlockByExtId(absX & 15, absY & 15, absZ & 15))) {
			IHLUtils.setBlockRaw(ebs, absX & 15, absY & 15, absZ & 15, block);
			return true;
		}
		return false;
	}

	protected boolean replaceAll(World world, int absX, int absY, int absZ, final Block block) {
		if (!world.getChunkProvider().chunkExists(absX >> 4, absZ >> 4)) {
			throw new IllegalStateException("quered chunk is not yet generated!");
		}
		Chunk chunk = world.getChunkFromBlockCoords(absX, absZ);
		ExtendedBlockStorage ebs = chunk.getBlockStorageArray()[absY >> 4];
		if (ebs != null) {
			IHLUtils.setBlockRaw(ebs, absX & 15, absY & 15, absZ & 15, block);
			return true;
		}
		return false;
	}

	private int[] getPOI(World world, int chunkX, int chunkZ, int xOffset, int zOffset) {
		long seed = (long) chunkX << 16 ^ chunkZ << 8 ^ Block.getIdFromBlock(ore);
		random.setSeed(seed);
		return new int[] { random.nextInt(16) + xOffset * 16, random.nextInt(world.getActualHeight()/2),
				random.nextInt(16) + zOffset * 16 };
	}
}
