package ihl.enviroment;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.IHLMod;
import ihl.utils.IHLMathUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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

	public LightSource calculateLightSource(World world, int sourceX, int sourceY, int sourceZ, int power, int red,
			int green, int blue, double[] ds) {
		LightSource lightSource = new LightSource(sourceX, sourceY, sourceZ, red, green, blue, power);
		int[] borders = { sourceX, sourceY, sourceZ, sourceX, sourceY, sourceZ };
		int[] evSource = { sourceX, sourceY, sourceZ };
		int[] lightSourceXYZ = { sourceX, sourceY, sourceZ };
		for (int i = 0; i < directionMasks.length; i++) {
			int[] directionMask = directionMasks[i];
			this.litBlocksAndGetDescendants(world, evSource, lightSourceXYZ, lightSource.illuminatedBlocks, 0, power>>1,
					directionMask, borders, ds);
		}
		lightSource.setBorders(borders[0], borders[1], borders[2], borders[3], borders[4], borders[5]);
		return lightSource;
	}

	private void litBlocksAndGetDescendants(World world, int[] evSource, int[] lightSource, BitSet illuminatedBlocksSet,
			int ev, int power, int[] directionMask, int[] borders, double[] ds) {
		power = this.getNewPower(world, ev, evSource, lightSource, power, directionMask, illuminatedBlocksSet, borders,
				ds);
		power--;
		if (power > 1) {
			if (vectors[ev].length == 0) {
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
						directionMask, borders, ds);
			} else {
				for (int d1 : this.vectors[ev]) {
						litBlocksAndGetDescendants(world, evSource, lightSource, illuminatedBlocksSet, d1, power,
								directionMask, borders, ds);
				}
			}
		}

	}

	private int getNewPower(World world, int ev, int[] evSource, int[] lightSource, int power, int[] directionMask,
			BitSet illuminatedBlocksSet, int[] borders, double[] ds) {
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
		power1 = power1 * (255 - world.getBlockLightOpacity(absX, absY, absZ)) / 255;
		int lightBitAddress = this.encodeXYZ(absX - lightSource[0], absY - lightSource[1], absZ - lightSource[2]);
		illuminatedBlocksSet.set(lightBitAddress);
		if (ds != null) {
			float dx = (float) (absX - ds[0]);
			float dy = (float) (absY - ds[1]);
			float dz = (float) (absZ - ds[2]);
			float sqd = dx * dx + dy * dy + dz * dz;
			float d = IHLMathUtils.sqrt(sqd);
			float dx1 = (float) (ds[3] * d);
			float dy1 = (float) (ds[4] * d);
			float dz1 = (float) (ds[5] * d);
			float ddx = dx - dx1;
			float ddy = dy - dy1;
			float ddz = dz - dz1;
			float sqr = ddx * ddx + ddy * ddy + ddz * ddz;
			float sqrmax = (float) ds[6] * sqd + 4f;
			if (sqr > sqrmax) {
				return 0;
			}
		}
		return power1;
	}

	public void addLightSource(LightSource lightSource) {
/*		IHLMod.log.info("Added light source at area from "+lightSource.fromX+";"+lightSource.fromY+";"+lightSource.fromZ+
				" to "+lightSource.toX+";"+lightSource.toY+";"+lightSource.toZ);*/
		this.lightSources.add(lightSource);
		Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(
				lightSource.fromX, 
				lightSource.fromY,
				lightSource.fromZ,
				lightSource.toX,
				lightSource.toY,
				lightSource.toZ);
	}

	public void removeLightSource(LightSource lightSource) {
		if(!this.lightSources.remove(lightSource)){
			throw new IllegalArgumentException("Requested light source is not presented.");
		}
/*		IHLMod.log.info("Removing light source at area from "+lightSource.fromX+";"+lightSource.fromY+";"+lightSource.fromZ+
				" to "+lightSource.toX+";"+lightSource.toY+";"+lightSource.toZ);*/
		Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(
				lightSource.fromX, 
				lightSource.fromY,
				lightSource.fromZ,
				lightSource.toX,
				lightSource.toY,
				lightSource.toZ);
	}
}
