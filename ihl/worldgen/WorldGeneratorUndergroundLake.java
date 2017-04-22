package ihl.worldgen;

import ihl.utils.IHLMathUtils;
import ihl.utils.IHLUtils;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class WorldGeneratorUndergroundLake extends WorldGeneratorBase {

	private final Block clayBlock;

	public WorldGeneratorUndergroundLake(Block oreIn, Block clayIn, Block[] replaceableBlocksIn) {
		super(oreIn, replaceableBlocksIn);
		clayBlock = clayIn;
	}

	@Override
	protected void replaceBlocks(World world, int[] centralPOI, int[] surroundPOI, int startX, int startZ) {
		int x = centralPOI[0], y = centralPOI[1] - world.getActualHeight() / 4, z = centralPOI[2];
		for (int ix = x; ix < 16 && ix >= 0; ix += IHLMathUtils.sign(surroundPOI[0] - x)) {
			int y2 = y += IHLMathUtils.sign(surroundPOI[1] - world.getActualHeight() / 4 - y);
			nextZ:for (int iz = z; iz < 16 && iz >= 0; iz += IHLMathUtils.sign(surroundPOI[2] - z)) {
				y2 += IHLMathUtils.sign(surroundPOI[1] - world.getActualHeight() / 4 - y2);
				int dx = ix - x;
				int dz = iz - z;
				int d = dx * dx + dz * dz;
				if (y2 > 1 && d < 64) {
					for (int iy = y2; iy > 0; iy--) {
						int absX = ix + startX;
						int absZ = iz + startZ;
						int absY = iy;
						Chunk chunk = world.getChunkFromBlockCoords(absX, absZ);
						ExtendedBlockStorage ebs = chunk.getBlockStorageArray()[absY >> 4];
						if (ebs != null	&& replaceableBlocks.contains(ebs.getBlockByExtId(absX & 15, absY & 15, absZ & 15))) {
							int[] xyz = new int[] { 0, 0, -1, 0, 0, 1, 0, 0 };
							for (int i = 2; i < xyz.length; i++) {
								int absX2 = absX + xyz[i - 2];
								int absZ2 = absZ + xyz[i];
								if (!world.getChunkProvider().chunkExists(absX2 >> 4, absZ2 >> 4)) {
									continue nextZ;
								}
								this.replaceAllExceptOre(world, absX2, iy + xyz[i - 1], absZ2, clayBlock);
							}
							IHLUtils.setBlockRaw(ebs, absX & 15, absY & 15, absZ & 15, ore);
						}
					}
				}
				if (surroundPOI[2] == z) {
					break;
				}
			}
			if (surroundPOI[0] == x) {
				break;
			}
		}
	}

}
