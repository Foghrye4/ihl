package ihl.collector;

import ihl.IHLModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class CollectorItemRender implements IItemRenderer{
	private CollectorModel model = new CollectorModel();
	private ResourceLocation tex;
	private float scale;

public CollectorItemRender(boolean heavy)
{
	super();
	if(heavy)
	{
		scale = 1F/80F;
		tex = new ResourceLocation(IHLModInfo.MODID+":textures/entities/collectorHeavy.png");
	}
	else
	{
		scale = 1F/100F;
		tex = new ResourceLocation(IHLModInfo.MODID+":textures/entities/collector.png");
	}
}

@Override
public void renderItem(ItemRenderType type, ItemStack item, Object... data) 
{
	int levelBar = Math.max(0,model.Indicator.cubeList.size()-2-Math.round((float)(27-item.getItemDamageForDisplay())*(float)model.Indicator.cubeList.size()/27F));
    RenderPlayer var25;
    EntityClientPlayerMP var3 = Minecraft.getMinecraft().thePlayer;
	GL11.glPushMatrix();
	switch(type)
	{
	case EQUIPPED_FIRST_PERSON:
		GL11.glTranslatef(0F, 0.5F, 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(var3.getLocationSkin());
		var25 = (RenderPlayer)RenderManager.instance.getEntityRenderObject(Minecraft.getMinecraft().thePlayer);
		GL11.glRotatef(135F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(35F, 0.0F, 1.0F, 0.0F);
		var25.renderFirstPersonArm(Minecraft.getMinecraft().thePlayer);
		GL11.glRotatef(-35F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-135F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -1.0F, 0.5F);
		break;
	case ENTITY:
		GL11.glTranslatef(0F, 1.0F, 0F);
		GL11.glScalef(1.0F, -1F, -1F);
		break;
	case INVENTORY:
		GL11.glScalef(1.0F, -1F, -1F);
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
	for(int i=0;i<4;i++)
	{
		model.Eleron.rotateAngleY=i*(float)Math.PI/2;
		model.Eleron.render(scale);
	}
	OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0xf0);
	for(int i=0;i<4;i++)
	{
		GL11.glRotatef(i* 90F, 0.0F, 1.0F, 0.0F);
		model.Flares.render(scale);
		ModelBox box = (ModelBox) model.Indicator.cubeList.get(levelBar);
		box.render(Tessellator.instance,scale);
	}
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