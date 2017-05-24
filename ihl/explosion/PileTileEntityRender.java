package ihl.explosion;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;

import ihl.IHLMod;
import ihl.utils.IHLItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class PileTileEntityRender extends TileEntitySpecialRenderer {

	private int fb = -1;
	private final int textureWidth = 64;
	private final int textureHeight = 64;
	private final PileBlockRender pileBlockRender;
	public final Map<Long, Integer> subIconIdMap = new HashMap<Long, Integer>();
	private int nextAvailableId = -1;
	private boolean framebufferReady = false;
	private Minecraft mc;
	private final IHLItemRenderer ihlItemRenderer = new IHLItemRenderer(false);
	private final Random random = new Random();

	public PileTileEntityRender(PileBlockRender pileBlockRender1) {
		pileBlockRender = pileBlockRender1;
		mc = Minecraft.getMinecraft();
	}

	public void renderAModelAt(PileTileEntity tile, double x, double y, double z, float f) {
		ItemStack stack = tile.content;
		if (!framebufferReady) {
			this.generateFrameBuffer();
			Minecraft.getMinecraft().entityRenderer.setupCameraTransform(f, 0);
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		} else if (stack != null) {
			long hash = this.pileBlockRender.getItemStackHash(stack);
			if (!subIconIdMap.containsKey(hash)) {

				subIconIdMap.put(hash, ++nextAvailableId);
				this.preparetexture();
				this.drawTexture(tile, nextAvailableId);
				Minecraft.getMinecraft().entityRenderer.setupCameraTransform(f, 0);
				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
				tile.updateBlockRender();
			}
		}
	}

	private void generateFrameBuffer() {
		fb = GL30.glGenFramebuffers();
		this.preparetexture();
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			IHLMod.log.error("Something went wrong while creating frame buffer!");
			IHLMod.log.error(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER));
		} else {
			IHLMod.log.info("FrameBuffer loaded correctly!");
		}
		this.framebufferReady = true;
	}

	private void preparetexture() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fb);
		int texture = Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.locationBlocksTexture)
				.getGlTextureId();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
	}

	private void drawTexture(PileTileEntity tile, int subIconId) {
		IIcon picon = PileBlock.instance.getIcon(0, 0);
		float minu = picon.getMinU();
		float minv = picon.getMinV();
		float maxu = picon.getMaxU();
		float maxv = picon.getMaxV();
		int iconwidth = picon.getIconWidth();
		int iconheight = picon.getIconHeight();
		float du = maxu - minu;
		float dv = maxv - minv;
		int iconNumU = picon.getIconWidth()/this.textureWidth;
		int posu = (int) (minu * iconwidth / du) + subIconId % iconNumU * textureWidth;
		int posv = (int) (minv * iconheight / dv) + subIconId / iconNumU * textureHeight;

		GL11.glViewport(posu, posv, textureWidth, textureHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(-1d, 1d, -1d, 1d, 0.05F, this.mc.gameSettings.renderDistanceChunks * 32F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		ItemStack stack = tile.content;
		GL11.glTranslatef(0, 0, -1f);
		GLU.gluLookAt(0, 0, 0, -32f/* x reference */, 0f/* y reference */, 0f/* z reference */, 0.0f, 1.0f, 0.0f);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glRotatef(90f, 0, 1f, 0);
		GL11.glScalef(2f, 2f, 2f);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0xf0);
		if (stack.getItem() instanceof ItemBlock) {
			Block block = ((ItemBlock) stack.getItem()).field_150939_a;
			IIcon icon = block.getIcon(0, stack.getItemDamage());
			double u1 = (double) icon.getInterpolatedU(0D);
			double u2 = (double) icon.getInterpolatedU(16.0D);
			double v1 = (double) icon.getInterpolatedV(0D);
			double v2 = (double) icon.getInterpolatedV(16.0D);
			Tessellator tessellator = Tessellator.instance;
			bindTexture(TextureMap.locationBlocksTexture);
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_F(1f, 1f, 1f);
			tessellator.addVertexWithUV(-0.5, -0.5, 0, u1, v1);
			tessellator.addVertexWithUV(0.5, -0.5, 0, u1, v2);
			tessellator.addVertexWithUV(0.5, 0.5, 0, u2, v2);
			tessellator.addVertexWithUV(-0.5, 0.5, 0, u2, v1);
			tessellator.draw();
		}
		for (int i = 0; i < 128; i++) {
			GL11.glPushMatrix();
			float tx = random.nextFloat() - 0.5f;
			float ty = random.nextFloat() - 0.5f;
			GL11.glTranslatef(tx, ty, 0);
			this.ihlItemRenderer.doRender(RenderManager.instance, stack, 0f, 0f, 0f);
			GL11.glTranslatef(-tx, -ty, 0);
			GL11.glPopMatrix();
		}
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
	
	public float getSubIconMinU(int index)
	{
		IIcon picon = PileBlock.instance.getIcon(0, 0);
		int iconNumU = picon.getIconWidth()/this.textureWidth;
		float minu = picon.getMinU();
		float maxu = picon.getMaxU();
		float du = (maxu - minu)/iconNumU;
		return minu + index%iconNumU*du;
	}
	
	public float getSubIconMinV(int index)
	{
		IIcon picon = PileBlock.instance.getIcon(0, 0);
		int iconNumU = picon.getIconWidth()/this.textureWidth;
		int iconNumV = picon.getIconHeight()/this.textureHeight;
		float minv = picon.getMinV();
		float maxv = picon.getMaxV();
		float dv = (maxv - minv)/iconNumV;
		return minv + index/iconNumU*dv;
	}
	public float getSubIconDU(int index)
	{
		IIcon picon = PileBlock.instance.getIcon(0, 0);
		int iconNumU = picon.getIconWidth()/this.textureWidth;
		float minu = picon.getMinU();
		float maxu = picon.getMaxU();
		float du = (maxu - minu)/iconNumU;
		return du;
	}
	
	public float getSubIconDV(int index)
	{
		IIcon picon = PileBlock.instance.getIcon(0, 0);
		int iconNumV = picon.getIconHeight()/this.textureHeight;
		float minv = picon.getMinV();
		float maxv = picon.getMaxV();
		float dv = (maxv - minv)/iconNumV;
		return dv;
	}

	
	
	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
		this.renderAModelAt((PileTileEntity) par1TileEntity, par2, par4, par6, par8);
	}

}