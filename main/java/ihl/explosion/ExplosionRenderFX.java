package ihl.explosion;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ExplosionRenderFX extends Render{
	private ResourceLocation tex;
	
public ExplosionRenderFX(String textureLocation)
{
	super();
	tex = new ResourceLocation(textureLocation);
}

@Override
public void doRender(Entity entity, double x, double y, double z,
		float interFrame, float arg5) 
{
    float rotationX = ActiveRenderInfo.rotationX;
    float rotationZ = ActiveRenderInfo.rotationZ;
    float rotationYZ = ActiveRenderInfo.rotationYZ;
    float rotationXY = ActiveRenderInfo.rotationXY;
    float rotationXZ = ActiveRenderInfo.rotationXZ;
	this.renderManager.renderEngine.bindTexture(tex);
    GL11.glPushMatrix();
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	GL11.glTranslatef((float)x, (float)y, (float)z);
    GL11.glDepthMask(false);
    GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    EntityFX entityFX = (EntityFX) entity;
    entityFX.renderParticle(tessellator, interFrame, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
    tessellator.draw();
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glDepthMask(true);
    GL11.glPopMatrix();
}

	@Override
	protected ResourceLocation getEntityTexture(Entity arg0) 
	{
		return tex;
	}
}