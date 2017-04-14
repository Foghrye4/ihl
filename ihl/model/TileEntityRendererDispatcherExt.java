package ihl.model;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.ClientProxy;
import ihl.IHLMod;
import ihl.enviroment.LightSource;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class TileEntityRendererDispatcherExt extends TileEntityRendererDispatcher {

	@Override
	public void renderTileEntity(TileEntity tile, float partialTick) {
		if (tile.getDistanceFrom(this.field_147560_j, this.field_147561_k, this.field_147558_l) < tile
				.getMaxRenderDistanceSquared()) {
			int x = tile.xCoord;
			int y = tile.yCoord;
			int z = tile.zCoord;
			int brightnessBase = this.field_147550_f.getLightBrightnessForSkyBlocks(x, y, z, 0);
			float red = 1f;
			float green = 1f;
			float blue = 1f;
			int[] baseColour = new int[3];
			boolean lightAffected = false;
			for (LightSource lightSource : ((ClientProxy) IHLMod.proxy).getLightHandler().lightSources) {
				if (lightSource.isBlockIlluminated(x, y, z)) {
					lightAffected = true;
					int[] lightValue = lightSource.getLightValue(x, y, z, RenderBlocksExt.NULL_NORMAL[0]);
					if ((brightnessBase & 240) < lightValue[0]) {
						int colourFactor = lightValue[0] - (brightnessBase & 240);
						brightnessBase &= RenderBlocksExt.MASK;
						brightnessBase |= lightValue[0];
						baseColour[0] += lightValue[1] * colourFactor / 255;
						baseColour[1] += lightValue[2] * colourFactor / 255;
						baseColour[2] += lightValue[3] * colourFactor / 255;
					}
				}
			}
			if (lightAffected) {
				RenderBlocksExt.normalizeColour(baseColour);
				red = RenderBlocksExt.mixLightColour(red, baseColour[0]);
				green = RenderBlocksExt.mixLightColour(green, baseColour[1]);
				blue = RenderBlocksExt.mixLightColour(blue, baseColour[2]);
			}
			int j = brightnessBase % 65536;
			int k = brightnessBase / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
			GL11.glColor4f(red, green, blue, 1.0F);
			this.renderTileEntityAt(tile, (double) x - staticPlayerX, (double) y - staticPlayerY,
					(double) z - staticPlayerZ, partialTick);
		}
	}
}
