package ihl.worldgen;

import ihl.utils.IHLMathUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGeneratorSurfaceLake extends WorldGeneratorBase {

	public WorldGeneratorSurfaceLake(Block oreIn, Block[] replaceableBlocksIn) {
		super(oreIn, replaceableBlocksIn);
	}

	@Override
	protected void replaceBlocks(World world, int[] centralPOI, int[] surroundPOI, int startX, int startZ) {
		BiomeGenBase biome = world.getBiomeGenForCoords(startX, startZ);
		if (biome.temperature < 1.9f || biome.rainfall != 0f) {
			return;
		}
		int x = centralPOI[0], y = centralPOI[1], z = centralPOI[2];
		for (int ix = x; ix < 16 && ix >= 0; ix += IHLMathUtils.sign(surroundPOI[0] - x)) {
			int y2 = y += IHLMathUtils.sign(surroundPOI[1] - world.getActualHeight() / 4 - y);
			for (int iz = z; iz < 16 && iz >= 0; iz += IHLMathUtils.sign(surroundPOI[2] - z)) {
				y2 += IHLMathUtils.sign(surroundPOI[1] - world.getActualHeight() / 4 - y2);
				int dx = ix-x;
				int dz = iz-z;
				int d = dx*dx+dz*dz;
				if (d < 64 && y2 > world.getActualHeight() * 3 / 8
						&& world.getBlock(ix + startX, 63, iz + startZ) == Blocks.air) {
					this.replace(world, ix + startX, 62, iz + startZ, ore);
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
