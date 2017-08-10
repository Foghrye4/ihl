package ihl.worldgen;

import ihl.utils.IHLMathUtils;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGeneratorVein extends WorldGeneratorBase {

	public WorldGeneratorVein(Block oreIn, Block[] replaceableBlocksIn) {
		super(oreIn, replaceableBlocksIn);
	}

	@Override
	protected void replaceBlocks(World world, int[] centralPOI, int[] surroundPOI, int startX, int startZ) {
		int x = centralPOI[0], y = centralPOI[1], z = centralPOI[2];
		while (x < 16 && x >= 0 && y >= 0 && y < world.getActualHeight() && z >= 0 && z < 16) {
			this.replace(world, x + startX, y, z + startZ, ore);
			x += IHLMathUtils.sign(surroundPOI[0] - x);
			y += IHLMathUtils.sign(surroundPOI[1] - y);
			z += IHLMathUtils.sign(surroundPOI[2] - z);
			if (IHLMathUtils.sign(surroundPOI[0] - x) == 0 && IHLMathUtils.sign(surroundPOI[1] - y) == 0
					&& IHLMathUtils.sign(surroundPOI[2] - z) == 0)
				break;
		}
	}
}
