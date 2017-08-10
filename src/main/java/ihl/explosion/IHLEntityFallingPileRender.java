package ihl.explosion;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import ihl.utils.IHLItemRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class IHLEntityFallingPileRender extends Render{
	
	private final IHLItemRenderer itemRenderer;
	private final Random random = new Random(400);
    
	public IHLEntityFallingPileRender() 
	{
		super();
		this.itemRenderer=new IHLItemRenderer(true);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float arg4, float arg5) 
	{	
		GL11.glTranslated(x, y, z);
		random.setSeed(entity.getEntityId());
		IHLEntityFallingPile fp = (IHLEntityFallingPile) entity;
		ItemStack stack = fp.getEntityItem();
		for (int i = 0; i < 16; i++) 
		{
			GL11.glPushMatrix();
			GL11.glRotatef(90f,random.nextFloat()*2f-1f,random.nextFloat()*2f-1f,random.nextFloat()*2f-1f);
			float tx = random.nextFloat()-0.5f;
			float ty = random.nextFloat()-0.5f;
			float tz = random.nextFloat()-0.5f;
			GL11.glTranslatef(tx, ty, tz);
			itemRenderer.doRender(RenderManager.instance,stack,	0,	0,	0);
			GL11.glTranslatef(-tx, -ty, tz);
			GL11.glPopMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity arg0) {
		return TextureMap.locationItemsTexture;
	}
	
}