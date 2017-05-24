package ihl.servitor;

import org.lwjgl.opengl.GL11;

import ihl.IHLModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class SkullItemRender implements IItemRenderer{
	private SkullModel model = new SkullModel();
	private ResourceLocation tex;
	private float scale;
	
public SkullItemRender()
{
	super();
	scale = 1F/80F;
	tex = new ResourceLocation(IHLModInfo.MODID+":textures/items/skull.png");
}

@Override
public void renderItem(ItemRenderType type, ItemStack item, Object... data) 
{
   GL11.glPushMatrix();
	switch(type)
	{
	case EQUIPPED_FIRST_PERSON:
		GL11.glRotatef(80F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0F, 0.18F, 1F);
		GL11.glScalef(1.5F, -1.5F, -1.5F);
		break;
	case ENTITY:
		GL11.glTranslatef(0F, 1.0F, 0F);
		GL11.glScalef(1.0F, -1F, -1F);
		break;
	case INVENTORY:
		GL11.glRotatef(210F, 0F, 1.0F, -0.15F);
		GL11.glTranslatef(0.0F,-0.75F,0.0F);
		GL11.glScalef(1.5F, -1.5F, -1.5F);
		break;
	case EQUIPPED:
		GL11.glTranslatef(0.5F,0.5F,0.25F);
		GL11.glScalef(1.0F, -1F, -1F);
		break;
	default:
		break;
	}
    Minecraft.getMinecraft().renderEngine.bindTexture(tex);
	model.Base.render(scale);
    GL11.glPopMatrix();
}

/**
 * IItemRenderer implementation *
 */
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
}