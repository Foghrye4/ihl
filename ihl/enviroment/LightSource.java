package ihl.enviroment;

import java.util.BitSet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.ClientProxy;
import ihl.IHLMod;
import ihl.utils.IHLMathUtils;
import net.minecraft.world.World;

@SideOnly(value = Side.CLIENT)
public class LightSource {
	private final int centerX;
	private final int centerY;
	private final int centerZ;
	public int fromX;
	public int fromY;
	public int fromZ;
	public int toX;
	public int toY;
	public int toZ;
	private final int red;
	private final int green;
	private final int blue;
	private final int power;
	public final BitSet illuminatedBlocks = new BitSet();

	public LightSource(int centerX1, int centerY1, int centerZ1, int red1, int green1, int blue1, int power1) {
		centerX = centerX1;
		centerY = centerY1;
		centerZ = centerZ1;
		red = red1;
		green = green1;
		blue = blue1;
		power = power1;
	}

	public void setBorders(int fromX1, int fromY1, int fromZ1, int toX1, int toY1, int toZ1) {
		fromX = fromX1;
		fromY = fromY1;
		fromZ = fromZ1;
		toX = toX1;
		toY = toY1;
		toZ = toZ1;
	}

	public boolean isBlockIlluminated(int x, int y, int z) {
		if (x < fromX || x > toX || y < fromY || y > toY || z < fromZ || z > toZ) {
			return false;
		} else {
			int rx = x - centerX;
			int ry = y - centerY;
			int rz = z - centerZ;
			int l = ((ClientProxy) IHLMod.proxy).getLightHandler().encodeXYZ(rx, ry, rz);
			return illuminatedBlocks.get(l);
		}
	}

	public int[] getLightValue(int x, int y, int z, int[] normal) {
		int dx = centerX - x;
		int dy = centerY - y;
		int dz = centerZ - z;
		int d = dx * dx + dy * dy + dz * dz;
		if (d == 0) {
			return new int[] { 0xf0, this.red, this.blue, this.green };
		}
		if (normal[0] + normal[1] + normal[2] == 0) {
			dx = dx < 0 ? -dx : dx;
			dy = dy < 0 ? -dy : dy;
			dz = dz < 0 ? -dz : dz;
		} else {
			dx = normal[0] * dx;
			dy = normal[1] * dy;
			dz = normal[2] * dz;
			dx = dx > 0 ? dx : 0;
			dy = dy > 0 ? dy : 0;
			dz = dz > 0 ? dz : 0;
		}
		int r = power * (dx + dy + dz) / d;
		r = r<0?0:r;
		int brightness = r > 15 ? 15 : r;
		return new int[] { brightness << 4, this.red*r, this.green*r, this.blue*r};
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LightSource) {
			LightSource otherLS = (LightSource) o;
			return this.centerX == otherLS.centerX && this.centerY == otherLS.centerY && this.centerZ == otherLS.centerZ
					&& this.fromX == otherLS.fromX && this.fromY == otherLS.fromY && this.fromZ == otherLS.fromZ
					&& this.toX == otherLS.toX && this.toY == otherLS.toY && this.toZ == otherLS.toZ;
		}
		return false;
	}

	public void provideLight(World world, int x, int y, int z) {
		int dx = x-centerX;//100 10 20
		int dy = y-centerY;
		int dz = z-centerZ;
		int sqd = dx*dx+dy*dy+dz*dz;
		float d = IHLMathUtils.sqrt(sqd);
		float dx1 = dx/d;
		float dy1 = dy/d;
		float dz1 = dz/d;
		float x1 = x+0.5f;
		float y1 = y+0.5f;
		float z1 = z+0.5f;
		for(int i=0;i<64;i++){
			x1+=dx1;
			y1+=dy1;
			z1+=dz1;
			int absX = (int)x1;
			int absY = (int)y1;
			int absZ = (int)z1;
			int ddx = absX-centerX;
			int ddy = absY-centerY;
			int ddz = absZ-centerZ;
			this.illuminatedBlocks.set(((ClientProxy) IHLMod.proxy).getLightHandler().encodeXYZ(ddx, ddy, ddz));
			if (absX < fromX) {
				fromX = absX;
			} else if (absY < fromY) {
				fromY = absY;
			} else if (absZ < fromZ) {
				fromZ = absZ;
			} else if (absX > toX) {
				toX = absX;
			} else if (absY > toY) {
				toY = absY;
			} else if (absZ > toZ) {
				toZ = absZ;
			}
			if(world.getBlockLightOpacity(absX, absY, absZ)>192) {
				break;
			}
		}
	}
	
	public void castShadow(World world, int x, int y, int z) {
		int dx = x-centerX;
		int dy = y-centerY;
		int dz = z-centerZ;
		int sqd = dx*dx+dy*dy+dz*dz;
		float d = IHLMathUtils.sqrt(sqd);
		float dx1 = dx/d;
		float dy1 = dy/d;
		float dz1 = dz/d;
		float x1 = x+0.5f;
		float y1 = y+0.5f;
		float z1 = z+0.5f;
		x1+=dx1;
		y1+=dy1;
		z1+=dz1;
		for(int i=0;i<64;i++){
			x1+=dx1;
			y1+=dy1;
			z1+=dz1;
			int absX = (int)x1;
			int absY = (int)y1;
			int absZ = (int)z1;
			int ddx = absX-centerX;
			int ddy = absY-centerY;
			int ddz = absZ-centerZ;
			this.illuminatedBlocks.clear(((ClientProxy) IHLMod.proxy).getLightHandler().encodeXYZ(ddx, ddy, ddz));
			if (absX < fromX) {
				fromX = absX;
			} else if (absY < fromY) {
				fromY = absY;
			} else if (absZ < fromZ) {
				fromZ = absZ;
			} else if (absX > toX) {
				toX = absX;
			} else if (absY > toY) {
				toY = absY;
			} else if (absZ > toZ) {
				toZ = absZ;
			}
			if(world.getBlockLightOpacity(absX, absY, absZ)>192) {
				break;
			}
		}
	}
}
