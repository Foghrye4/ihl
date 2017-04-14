package ihl.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import ihl.ClientProxy;
import ihl.IHLMod;
import ihl.enviroment.LightSource;

@SideOnly(value = Side.CLIENT)
public class RenderBlocksExt extends RenderBlocks {

	public static RenderBlocksExt instance;
	final static int[][] NULL_NORMAL = new int[][] { new int[] { 0, 0, 0 },

			new int[] { 1, 0, 0 }, // Top left
			new int[] { 0, 0, 1 }, new int[] { 1, 0, 1 },

			new int[] { -1, 0, 0 }, // Top Right
			new int[] { 0, 0, 1 }, new int[] { -1, 0, 1 },

			new int[] { 1, 0, 0 }, // Bottom Left
			new int[] { 0, 0, -1 }, new int[] { 1, 0, -1 },

			new int[] { -1, 0, 0 }, // Bottom Right
			new int[] { 0, 0, -1 }, new int[] { -1, 0, -1 },

	};
	private final static int[][] YNEG = new int[][] { new int[] { 0, -1, 0 }, // 0

			new int[] { -1, 0, 0 }, // Top left
			new int[] { 0, 0, 1 }, new int[] { -1, 0, 1 },

			new int[] { 1, 0, 0 }, // Top Right
			new int[] { 0, 0, 1 }, new int[] { 1, 0, 1 },

			new int[] { -1, 0, 0 }, // Bottom Left
			new int[] { 0, 0, -1 }, new int[] { -1, 0, -1 },

			new int[] { 1, 0, 0 }, // Bottom Right
			new int[] { 0, 0, -1 }, new int[] { 1, 0, -1 }, };
	private final static int[][] YPOS = new int[][] { new int[] { 0, 1, 0 },

			new int[] { 1, 0, 0 }, // Top left
			new int[] { 0, 0, 1 }, new int[] { 1, 0, 1 },

			new int[] { -1, 0, 0 }, // Top Right
			new int[] { 0, 0, 1 }, new int[] { -1, 0, 1 },

			new int[] { 1, 0, 0 }, // Bottom Left
			new int[] { 0, 0, -1 }, new int[] { 1, 0, -1 },

			new int[] { -1, 0, 0 }, // Bottom Right
			new int[] { 0, 0, -1 }, new int[] { -1, 0, -1 },

	};
	private final static int[][] XNEG = new int[][] { new int[] { -1, 0, 0 }, // 4

			new int[] { 0, 1, 0 }, // Top left
			new int[] { 0, 0, 1 }, new int[] { 0, 1, 1 },

			new int[] { 0, -1, 0 }, // Top Right
			new int[] { 0, 0, 1 }, new int[] { 0, -1, 1 },

			new int[] { 0, 1, 0 }, // Bottom Left
			new int[] { 0, 0, -1 }, new int[] { 0, 1, -1 },

			new int[] { 0, -1, 0 }, // Bottom Right
			new int[] { 0, 0, -1 }, new int[] { 0, -1, -1 }, };
	private final static int[][] XPOS = new int[][] { new int[] { 1, 0, 0 }, // 5

			new int[] { 0, -1, 0 }, // Top left
			new int[] { 0, 0, 1 }, new int[] { 0, -1, 1 },

			new int[] { 0, 1, 0 }, // Top Right
			new int[] { 0, 0, 1 }, new int[] { 0, 1, 1 },

			new int[] { 0, -1, 0 }, // Bottom Left
			new int[] { 0, 0, -1 }, new int[] { 0, -1, -1 },

			new int[] { 0, 1, 0 }, // Bottom Right
			new int[] { 0, 0, -1 }, new int[] { 0, 1, -1 }, };
	private final static int[][] ZNEG = new int[][] { new int[] { 0, 0, -1 }, // 2

			new int[] { -1, 0, 0 }, // Top left
			new int[] { 0, 1, 0 }, new int[] { -1, 1, 0 },

			new int[] { -1, 0, 0 }, // Top Right
			new int[] { 0, -1, 0 }, new int[] { -1, -1, 0 },

			new int[] { 1, 0, 0 }, // Bottom Left
			new int[] { 0, 1, 0 }, new int[] { 1, 1, 0 },

			new int[] { 1, 0, 0 }, // Bottom Right
			new int[] { 0, -1, 0 }, new int[] { 1, -1, 0 },

	};
	private final static int[][] ZPOS = new int[][] { new int[] { 0, 0, 1 }, // 3

			new int[] { -1, 0, 0 }, // Top left
			new int[] { 0, 1, 0 }, new int[] { -1, 1, 0 },

			new int[] { 1, 0, 0 }, // Top Right
			new int[] { 0, 1, 0 }, new int[] { 1, 1, 0 },

			new int[] { -1, 0, 0 }, // Bottom Left
			new int[] { 0, -1, 0 }, new int[] { -1, -1, 0 },

			new int[] { 1, 0, 0 }, // Bottom Right
			new int[] { 0, -1, 0 }, new int[] { 1, -1, 0 }, };
	private final static int BRIGHT = 0xFF00F7;
	final static int MASK = 0xFF0003;

	public RenderBlocksExt(IBlockAccess blockAccess) {
		super(blockAccess);
		instance = this;
	}

	private void transformColour(Block block, int x, int y, int z, int[][] normal) {
		int brightnessBase = block.getMixedBrightnessForBlock(this.blockAccess, x + normal[0][0], y + normal[0][1],
				z + normal[0][2]);
		int brightnessBase1 = brightnessBase;
		int l = block.colorMultiplier(this.blockAccess, x, y, z);
		float redF = (float) (l >> 16 & 255) / 255f;
		float greenF = (float) (l >> 8 & 255) / 255f;
		float blueF = (float) (l & 255) / 255f;
		int baseColourValue = 255;
		int[] baseColour = new int[] { baseColourValue, baseColourValue, baseColourValue };
		int[] topLeft = new int[] { baseColourValue, baseColourValue, baseColourValue };
		int[] topRight = new int[] { baseColourValue, baseColourValue, baseColourValue };
		int[] bottomLeft = new int[] { baseColourValue, baseColourValue, baseColourValue };
		int[] bottomRight = new int[] { baseColourValue, baseColourValue, baseColourValue };
		int brightnessTopLeft1 = this.brightnessTopLeft;
		int brightnessTopRight1 = this.brightnessTopRight;
		int brightnessBottomLeft1 = this.brightnessBottomLeft;
		int brightnessBottomRight1 = this.brightnessBottomRight;
		boolean lightAffected = false;
		for (LightSource lightSource : ((ClientProxy) IHLMod.proxy).getLightHandler().lightSources) {
			if (lightSource.isBlockIlluminated(x, y, z)) {
				lightAffected = true;
				int[] lightValue = lightSource.getLightValue(x, y, z, normal[0]);
				if ((brightnessBase & 240) < lightValue[0]) {
					int colourFactor = lightValue[0] - (brightnessBase & 240);
					if ((brightnessBase1 & 240) < lightValue[0]) {
						brightnessBase1 &= MASK;
						brightnessBase1 |= lightValue[0];
					}
					baseColour[0] += lightValue[1] * colourFactor / 255;
					baseColour[1] += lightValue[2] * colourFactor / 255;
					baseColour[2] += lightValue[3] * colourFactor / 255;
				}
				if (this.enableAO) {
					if (lightSource.isBlockIlluminated(x + normal[1][0], y + normal[1][1], z + normal[1][2])
							&& lightSource.isBlockIlluminated(x + normal[2][0], y + normal[2][1], z + normal[2][2])
							&& lightSource.isBlockIlluminated(x + normal[3][0], y + normal[3][1], z + normal[3][2])) {
						lightValue = lightSource.getLightValue(x + normal[3][0], y + normal[3][1], z + normal[3][2],
								normal[0]);
						if ((this.brightnessTopLeft & 240) < lightValue[0]) {
							int colourFactor = lightValue[0] - (this.brightnessTopLeft & 240);
							if ((brightnessTopLeft1 & 240) < lightValue[0]) {
								brightnessTopLeft1 &= MASK;
								brightnessTopLeft1 |= lightValue[0];
							}
							topLeft[0] += lightValue[1] * colourFactor / 255;
							topLeft[1] += lightValue[2] * colourFactor / 255;
							topLeft[2] += lightValue[3] * colourFactor / 255;
						}
					}
					if (lightSource.isBlockIlluminated(x + normal[4][0], y + normal[4][1], z + normal[4][2])
							&& lightSource.isBlockIlluminated(x + normal[5][0], y + normal[5][1], z + normal[5][2])
							&& lightSource.isBlockIlluminated(x + normal[6][0], y + normal[6][1], z + normal[6][2])) {
						lightValue = lightSource.getLightValue(x + normal[6][0], y + normal[6][1], z + normal[6][2],
								normal[0]);
						if ((this.brightnessTopRight & 240) < lightValue[0]) {
							int colourFactor = lightValue[0] - (this.brightnessTopRight & 240);
							if ((brightnessTopRight1 & 240) < lightValue[0]) {
								brightnessTopRight1 &= MASK;
								brightnessTopRight1 |= lightValue[0];
							}
							topRight[0] += lightValue[1] * colourFactor / 255;
							topRight[1] += lightValue[2] * colourFactor / 255;
							topRight[2] += lightValue[3] * colourFactor / 255;
						}
					}
					if (lightSource.isBlockIlluminated(x + normal[7][0], y + normal[7][1], z + normal[7][2])
							&& lightSource.isBlockIlluminated(x + normal[8][0], y + normal[8][1], z + normal[8][2])
							&& lightSource.isBlockIlluminated(x + normal[9][0], y + normal[9][1], z + normal[9][2])) {
						lightValue = lightSource.getLightValue(x + normal[9][0], y + normal[9][1], z + normal[9][2],
								normal[0]);
						if ((this.brightnessBottomLeft & 240) < lightValue[0]) {
							int colourFactor = lightValue[0] - (this.brightnessBottomLeft & 240);
							if ((brightnessBottomLeft1 & 240) < lightValue[0]) {
								brightnessBottomLeft1 &= MASK;
								brightnessBottomLeft1 |= lightValue[0];
							}
							bottomLeft[0] += lightValue[1] * colourFactor / 255;
							bottomLeft[1] += lightValue[2] * colourFactor / 255;
							bottomLeft[2] += lightValue[3] * colourFactor / 255;
						}
					}
					if (lightSource.isBlockIlluminated(x + normal[10][0], y + normal[10][1], z + normal[10][2])
							&& lightSource.isBlockIlluminated(x + normal[11][0], y + normal[11][1], z + normal[11][2])
							&& lightSource.isBlockIlluminated(x + normal[12][0], y + normal[12][1],
									z + normal[12][2])) {
						lightValue = lightSource.getLightValue(x + normal[12][0], y + normal[12][1], z + normal[12][2],
								normal[0]);
						if ((this.brightnessBottomRight & 240) < lightValue[0]) {
							int colourFactor = lightValue[0] - (this.brightnessBottomRight & 240);
							if ((brightnessBottomRight1 & 240) < lightValue[0]) {
								brightnessBottomRight1 &= MASK;
								brightnessBottomRight1 |= lightValue[0];
							}
							bottomRight[0] += lightValue[1] * colourFactor / 255;
							bottomRight[1] += lightValue[2] * colourFactor / 255;
							bottomRight[2] += lightValue[3] * colourFactor / 255;
						}
					}
				}
			}
		}
		if (lightAffected) {
			normalizeColour(baseColour);
			normalizeColour(topLeft);
			normalizeColour(topRight);
			normalizeColour(bottomLeft);
			normalizeColour(bottomRight);
			redF = mixLightColour(redF, baseColour[0]);
			greenF = mixLightColour(greenF, baseColour[1]);
			blueF = mixLightColour(blueF, baseColour[2]);
			this.colorRedTopLeft = mixLightColour(colorRedTopLeft, topLeft[0]);
			this.colorGreenTopLeft = mixLightColour(colorGreenTopLeft, topLeft[1]);
			this.colorBlueTopLeft = mixLightColour(colorBlueTopLeft, topLeft[2]);
			this.colorRedTopRight = mixLightColour(colorRedTopRight, topRight[0]);
			this.colorGreenTopRight = mixLightColour(colorGreenTopRight, topRight[1]);
			this.colorBlueTopRight = mixLightColour(colorBlueTopRight, topRight[2]);
			this.colorRedBottomLeft = mixLightColour(colorRedBottomLeft, bottomLeft[0]);
			this.colorGreenBottomLeft = mixLightColour(colorGreenBottomLeft, bottomLeft[1]);
			this.colorBlueBottomLeft = mixLightColour(colorBlueBottomLeft, bottomLeft[2]);
			this.colorRedBottomRight = mixLightColour(colorRedBottomRight, bottomRight[0]);
			this.colorGreenBottomRight = mixLightColour(colorGreenBottomRight, bottomRight[1]);
			this.colorBlueBottomRight = mixLightColour(colorBlueBottomRight, bottomRight[2]);
			this.brightnessTopLeft = brightnessTopLeft1;
			this.brightnessTopRight = brightnessTopRight1;
			this.brightnessBottomLeft = brightnessBottomLeft1;
			this.brightnessBottomRight = brightnessBottomRight1;
			Tessellator.instance.setBrightness(brightnessBase1);
			Tessellator.instance.setColorOpaque_F(redF, greenF, blueF);
		}
	}

	static void normalizeColour(int[] colour) {
		int max = colour[0];
		if (max < colour[1])
			max = colour[1];
		if (max < colour[2])
			max = colour[2];
		if (max != 0) {
			colour[0] = colour[0] * 255 / max;
			colour[1] = colour[1] * 255 / max;
			colour[2] = colour[2] * 255 / max;
		} else {
			colour[0] = 255;
			colour[1] = 255;
			colour[2] = 255;
		}
	}

	static float mixLightColour(float original, int colour) {
		return original * colour / 255f;
	}

	@Override
	public void drawCrossedSquares(IIcon icon, double x, double y, double z, float f) {
		this.transformColour(this.blockAccess.getBlock((int) x, (int) y, (int) z), (int) x, (int) y, (int) z,
				NULL_NORMAL);
		super.drawCrossedSquares(icon, x, y, z, f);
	}

	@Override
	public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour(block, (int) x, (int) y, (int) z, YNEG);
		super.renderFaceYNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceYPos(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour(block, (int) x, (int) y, (int) z, YPOS);
		super.renderFaceYPos(block, x, y, z, icon);
	}

	@Override
	public void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour(block, (int) x, (int) y, (int) z, ZNEG);
		super.renderFaceZNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceZPos(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour(block, (int) x, (int) y, (int) z, ZPOS);
		super.renderFaceZPos(block, x, y, z, icon);
	}

	@Override
	public void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour(block, (int) x, (int) y, (int) z, XNEG);
		super.renderFaceXNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceXPos(Block block, double x, double y, double z, IIcon icon) {
		this.transformColour(block, (int) x, (int) y, (int) z, XPOS);
		super.renderFaceXPos(block, x, y, z, icon);
	}
}
