package ihl.enviroment;

import java.util.BitSet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.ClientProxy;
import ihl.IHLMod;

@SideOnly(value=Side.CLIENT)
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

	public LightSource(int centerX1, int centerY1, int centerZ1, 
			int red1, int green1, int blue1, int power1) {
		centerX = centerX1;
		centerY = centerY1;
		centerZ = centerZ1;
		red = red1;
		green = green1;
		blue = blue1;
		power = power1;
	}
	
	public void setBorders(int fromX1, int fromY1, int fromZ1,
			int toX1, int toY1, int toZ1)
	{
		fromX=fromX1; 
		fromY=fromY1; 
		fromZ=fromZ1;
		toX=toX1;
		toY=toY1;
		toZ=toZ1;
	}

	public boolean isBlockIlluminated(int x, int y, int z) {
		if(x<fromX || x>toX || y<fromY || y>toY || z<fromZ || z>toZ)
		{
			return false;
		}
		else
		{
			int rx = x - centerX;
			int ry = y - centerY;
			int rz = z - centerZ;
			int l = ((ClientProxy)IHLMod.proxy).getLightHandler().encodeXYZ(rx, ry, rz);
			return illuminatedBlocks.get(l);
		}
	}

	public int[] getLightValue(int x, int y, int z, int[] normal) {
		int dx = centerX-x;
		int dy = centerY-y;
		int dz = centerZ-z;
		int d = dx*dx+dy*dy+dz*dz;
		dx=normal[0]*dx;
		dy=normal[1]*dy;
		dz=normal[2]*dz;
		dx=dx>0?(dx<<16)/d:0;
		dy=dy>0?(dy<<16)/d:0;
		dz=dz>0?(dz<<16)/d:0;
		int brightness = Math.min(power*(dx+dy+dz)>>16,16);
		return new int[]{brightness, this.red, this.blue, this.green};
	}
}
