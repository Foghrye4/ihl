package ihl.enviroment;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLMod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

@SideOnly(value = Side.CLIENT)
public class LightHandler {

	private int[][] directionMasks;
	private int[][] vectors;
	private int bits;
	private int halfValue;

	private int lightBitsPerDimension = 10;
	private int maxLightRadius = (1 << lightBitsPerDimension - 1) - 1;
	private int bitmask = (1 << lightBitsPerDimension) - 1;
	public final Set<LightSource> lightSources = new HashSet<LightSource>();

	public LightHandler() {
		this.directionMasks = IHLMod.explosionHandler.directionMasks;
		this.vectors = IHLMod.explosionHandler.vectors;
		this.bits = IHLMod.explosionHandler.bits;
		this.halfValue = IHLMod.explosionHandler.halfValue;
	}

	public int encodeXYZ(int x, int y, int z) {
		return x + maxLightRadius << lightBitsPerDimension * 2 | y + maxLightRadius << lightBitsPerDimension
				| z + maxLightRadius;
	}

	public int[] decodeXYZ(int l) {
		return new int[] { (l >>> lightBitsPerDimension * 2) - maxLightRadius,
				((l >>> lightBitsPerDimension) & bitmask) - maxLightRadius, (l & bitmask) - maxLightRadius };
	}

	public LightSource calculateOmniLightSource(World world, int sourceX, int sourceY, int sourceZ, int power, int red,
			int green, int blue) {
		LightSource lightSource = new LightSource(sourceX, sourceY, sourceZ, red, green, blue, power);
		int[] borders = { sourceX, sourceY, sourceZ, sourceX, sourceY, sourceZ };
		int[] evSource = { sourceX, sourceY, sourceZ };
		int[] lightSourceXYZ = { sourceX, sourceY, sourceZ };
		for (int i = 0; i < directionMasks.length; i++) {
			int[] directionMask = directionMasks[i];
			this.litBlocksAndGetDescendants(world, evSource, lightSourceXYZ, lightSource.illuminatedBlocks, 0, power,
					directionMask, borders);
		}
		lightSource.setBorders(borders[0], borders[1], borders[2], borders[3], borders[4], borders[5]);
		return lightSource;
	}

	private void litBlocksAndGetDescendants(World world, int[] evSource, int[] lightSource, BitSet illuminatedBlocksSet,
			int ev, int power, int[] directionMask, int[] borders) {
		power = this.getNewPower(world, ev, evSource, lightSource, power, directionMask, illuminatedBlocksSet, borders);
		power = (power<<1)/3 - 1;
		if (power > 1) {
			if (vectors[ev][0] == 0) {
				int[] xyz = IHLMod.explosionHandler.decodeXYZ(ev);
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
				int ev2 = IHLMod.explosionHandler.encodeXYZ(xyz[0], xyz[1], xyz[2]);
				int[] nextEVSource = { evSource[0] + xb * halfValue * directionMask[0],
						evSource[1] + yb * halfValue * directionMask[1],
						evSource[2] + zb * halfValue * directionMask[2] };
				litBlocksAndGetDescendants(world, nextEVSource, lightSource, illuminatedBlocksSet, ev2, power,
						directionMask, borders);
			} else {
				for (int d1 : this.vectors[ev]) {
					if (d1 != 0) {
						litBlocksAndGetDescendants(world, evSource, lightSource, illuminatedBlocksSet, d1, power,
								directionMask, borders);
					}
				}
			}
		}

	}

	private int getNewPower(World world, int ev, int[] evSource, int[] lightSource, int power, int[] directionMask,
			BitSet illuminatedBlocksSet, int[] borders) {
		int power1 = power;
		int[] xyz = IHLMod.explosionHandler.decodeXYZ(ev);
		int absX = xyz[0] * directionMask[0] + evSource[0];
		int absY = xyz[1] * directionMask[1] + evSource[1];
		int absZ = xyz[2] * directionMask[2] + evSource[2];
		if (absX < borders[0]) {
			borders[0] = absX;
		} else if (absY < borders[1]) {
			borders[1] = absY;
		} else if (absZ < borders[2]) {
			borders[2] = absZ;
		} else if (absX > borders[3]) {
			borders[3] = absX;
		} else if (absY > borders[4]) {
			borders[4] = absY;
		} else if (absZ > borders[5]) {
			borders[5] = absZ;
		}
		Block block = world.getBlock(absX, absY, absZ);
		if (block.equals(Blocks.air) || block.isAir(world, absX, absY, absZ)) {
			return power;
		}
		power1 *= world.getBlockLightOpacity(absX, absY, absZ) / 16;
		int lightBitAddress = this.encodeXYZ(absX - lightSource[0], absY - lightSource[1], absZ - lightSource[2]);
		illuminatedBlocksSet.set(lightBitAddress);
		return power1;
	}

	public void addLightSource(LightSource lightSource) {
		System.out.println("Adding light source. Borders:");
		System.out.println("from " + lightSource.fromX + ";" + lightSource.fromY + ";" + lightSource.fromZ);
		System.out.println("to " + lightSource.toX + ";" + lightSource.toY + ";" + lightSource.toZ);
		this.lightSources.add(lightSource);
	}

	public void removeLightSource(LightSource lightSource) {
		this.lightSources.remove(lightSource);
	}
}
