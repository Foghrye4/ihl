package ihl.model;

import ihl.items_blocks.FlexibleCableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class FlexibleCableItemRender implements IItemRenderer {
	private ModelTube model;
	private ModelTube modelThin;
	private ModelTube modelLong;
	private ModelTube modelThinLong;
	private final int segmentsAmount = 19;
	private final float spiralStepAngle = 1f;
	private final float scale = 1 / 32f;
	private final int[] displayListCache = new int[4];

	public FlexibleCableItemRender() {
		float vOffset = 16f;
		int longFragmensSize = 24;
		model = new ModelTube(null, 0, 0, -2F, -3F, -2F + vOffset, 4, 6, 4, 0f, 0.5f, 0.99f, ForgeDirection.UP);
		modelLong = new ModelTube(null, 0, 0, -2F, -3F, -2F + vOffset, 4, longFragmensSize, 4, 0f, 0f, 0.99f,
				ForgeDirection.UP);
		modelThin = new ModelTube(null, 0, 0, -1F, -3F, -1F + vOffset, 2, 6, 2, 0f, 0.5f, 0.99f, ForgeDirection.UP);
		modelThinLong = new ModelTube(null, 0, 0, -1F, -3F, -1F + vOffset, 2, longFragmensSize, 2, 0f, 0.5f, 0.99f,
				ForgeDirection.UP);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
		case ENTITY:
			return true;
		case EQUIPPED:
			return true;
		case EQUIPPED_FIRST_PERSON:
			return true;
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glScalef(1F, -1F, -1F);
		switch (type) {
		case ENTITY:
			GL11.glTranslatef(0, -1.0F, 0);
			this.rendercoil(stack);
			break;
		case EQUIPPED:
			GL11.glTranslatef(0.25F, -2F, -0.75F);
			this.rendercoil(stack);
			break;
		case EQUIPPED_FIRST_PERSON:
			RenderPlayer var25;
			EntityClientPlayerMP var3 = Minecraft.getMinecraft().thePlayer;
			Minecraft.getMinecraft().getTextureManager().bindTexture(var3.getLocationSkin());
			var25 = (RenderPlayer) RenderManager.instance.getEntityRenderObject(Minecraft.getMinecraft().thePlayer);
			GL11.glRotatef(135F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(35F, 0.0F, 1.0F, 0.0F);
			var25.renderFirstPersonArm(Minecraft.getMinecraft().thePlayer);
			GL11.glRotatef(-35F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-135F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, 0.2F, 0F);
			GL11.glTranslatef(-0.25F, -1.0F, 0F);
			this.rendercoil(stack);
			break;
		case INVENTORY:
			GL11.glTranslatef(0.0F, -0.2F, 0F);
			this.rendercoil(stack);
			break;
		default:
		}
		GL11.glPopMatrix();
	}

	private void rendercoil(ItemStack stack) {
		int type = this.getCableType(stack);
		if (this.displayListCache[type] != 0) {
			GL11.glCallList(displayListCache[type]);
		} else {

			this.displayListCache[type] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(displayListCache[type], 4864 /* GL_COMPILE */);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			int saMax = this.segmentsAmount * 2 + this.segmentsAmount / 2;
			for (int i = 0; i <= saMax; i++) {
				GL11.glRotatef(-spiralStepAngle, 0f, 0f, 1f);
				GL11.glRotatef(360f / this.segmentsAmount, 1f, 0f, 0f);
				GL11.glTranslatef(0.004f, 0f, 0f);
				GL11.glRotatef(spiralStepAngle, 0f, 0f, 1f);
				GL11.glColor3f(1f, 1f, 1f);
				if (i == 0) {
					if (isNoInsulation(stack)) {
						if (FlexibleCableItem.instance.yellowColoredWires
								.contains(FlexibleCableItem.instance.getMaterial(stack))) {
							GL11.glColor3f(1f, 0.75f, 0.1f);
						} else {
							GL11.glColor3f(0.65f, 0.65f, 0.67f);
						}
						this.modelThinLong.render(Tessellator.instance, scale);

					} else {
						this.modelLong.render(Tessellator.instance, scale);
					}
				} else if (i == saMax) {
					GL11.glTranslatef(0.0F, -0.55F, 0F);
					if (isNoInsulation(stack)) {
						if (FlexibleCableItem.instance.yellowColoredWires
								.contains(FlexibleCableItem.instance.getMaterial(stack))) {
							GL11.glColor3f(1f, 0.75f, 0.1f);
						} else {
							GL11.glColor3f(0.65f, 0.65f, 0.67f);
						}
						this.modelThinLong.render(Tessellator.instance, scale);

					} else {
						this.modelLong.render(Tessellator.instance, scale);
					}
					GL11.glTranslatef(0.0F, 0.55F, 0F);
				} else {
					if (isNoInsulation(stack)) {
						if (FlexibleCableItem.instance.yellowColoredWires
								.contains(FlexibleCableItem.instance.getMaterial(stack))) {
							GL11.glColor3f(1f, 0.75f, 0.1f);
						} else {
							GL11.glColor3f(0.65f, 0.65f, 0.67f);
						}
						this.modelThin.render(Tessellator.instance, scale);

					} else {
						this.model.render(Tessellator.instance, scale);
					}
				}
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEndList();
		}
	}

	private int getCableType(ItemStack stack) {
		if (isNoInsulation(stack)) {
			if (FlexibleCableItem.instance.yellowColoredWires.contains(FlexibleCableItem.instance.getMaterial(stack))) {
				return 1;
			} else {
				return 2;
			}
		}
		return 0;
	}

	private boolean isNoInsulation(ItemStack stack) {
		return stack.stackTagCompound != null && FlexibleCableItem.instance.getInsulationMaterial(stack).equals("null");
	}
}