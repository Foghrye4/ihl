package ihl.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import ihl.ClientProxy;
import ihl.IHLMod;
import ihl.enviroment.LightSource;

@SideOnly(value=Side.CLIENT)
public class RenderBlocksExt extends RenderBlocks {

	public static RenderBlocksExt instance;
	private final int[] YNEG = new int[] { 0, -1, 0 };
	private final int[] YPOS = new int[] { 0, 1, 0 };
	private final int[] XNEG = new int[] { -1, 0, 0 };
	private final int[] XPOS = new int[] { 1, 0, 0 };
	private final int[] ZNEG = new int[] { 0, 0, -1 };
	private final int[] ZPOS = new int[] { 0, 0, 1 };
	private final int BRIGHT = 0x0000FF;
	private final int DARK = 0x000000;

	public RenderBlocksExt(IBlockAccess blockAccess) {
		super(blockAccess);
		instance = this;
	}

	private void transformColour(int x, int y, int z, int[] normal) {
		/*		for (LightSource lightSource : ((ClientProxy)IHLMod.proxy).getLightHandler().lightSources) {
			if (lightSource.isBlockIlluminated(x, y, z)) {
				int[] lightValue = lightSource.getLightValue((int) x, (int) y, (int) z, normal);
				System.out.println("this.brightnessBottomRight="+this.brightnessBottomRight);
				this.brightnessTopLeft |= lightValue[0];
				this.brightnessBottomLeft |= lightValue[0];
				this.brightnessTopRight |= lightValue[0];
				this.brightnessBottomRight |= lightValue[0];
				this.colorRedTopLeft *= (255 - lightValue[0]) * lightValue[1] / 255 / 255f;
				this.colorRedBottomLeft *= (255 - lightValue[0]) * lightValue[1] / 255 / 255f;
				this.colorRedTopRight *= (255 - lightValue[0]) * lightValue[1] / 255 / 255f;
				this.colorRedBottomRight *= (255 - lightValue[0]) * lightValue[1] / 255 / 255f;
				this.colorBlueTopLeft *= (255 - lightValue[0]) * lightValue[2] / 255 / 255f;
				this.colorBlueBottomLeft *= (255 - lightValue[0]) * lightValue[2] / 255 / 255f;
				this.colorBlueTopRight *= (255 - lightValue[0]) * lightValue[2] / 255 / 255f;
				this.colorBlueBottomRight *= (255 - lightValue[0]) * lightValue[2] / 255 / 255f;
				this.colorGreenTopLeft *= (255 - lightValue[0]) * lightValue[3] / 255 / 255f;
				this.colorGreenBottomLeft *= (255 - lightValue[0]) * lightValue[3] / 255 / 255f;
				this.colorGreenTopRight *= (255 - lightValue[0]) * lightValue[3] / 255 / 255f;
				this.colorGreenBottomRight *= (255 - lightValue[0]) * lightValue[3] / 255 / 255f;
			}
		}*/
	}

	@Override
	public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour((int) x, (int) y, (int) z, YNEG);
		super.renderFaceYNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceYPos(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour((int) x, (int) y, (int) z, YPOS);
		super.renderFaceYPos(block, x, y, z, icon);
	}

	@Override
	public void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour((int) x, (int) y, (int) z, ZNEG);
		super.renderFaceZNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceZPos(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour((int) x, (int) y, (int) z, ZPOS);
		super.renderFaceZPos(block, x, y, z, icon);
	}

	@Override
	public void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour((int) x, (int) y, (int) z, XNEG);
		super.renderFaceXNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceXPos(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour((int) x, (int) y, (int) z, XPOS);
		super.renderFaceXPos(block, x, y, z, icon);
	}
}
