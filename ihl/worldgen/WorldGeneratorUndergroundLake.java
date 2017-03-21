package ihl.worldgen;

import ihl.utils.IHLMathUtils;
import net.minecraft.block.Block;
import net.minecraft.world.World;

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
			for (int iz = z; iz < 16 && iz >= 0; iz += IHLMathUtils.sign(surroundPOI[2] - z)) {
				y2 += IHLMathUtils.sign(surroundPOI[1] - world.getActualHeight() / 4 - y2);
				if (y2 > 1) {
					this.replace(world, ix + startX, y2 + 1, iz + startZ, clayBlock);
					for (int iy = y2; iy > 0; iy--) {
						this.replace(world, ix + startX, iy, iz + startZ, ore);
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
