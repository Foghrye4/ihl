package ihl.servitor;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FlameRenderFX extends Render{
	private ResourceLocation tex;
	
public FlameRenderFX(String textureLocation)
{
	super();
	tex = new ResourceLocation(textureLocation);
}

@Override
public void doRender(Entity entity, double x, double y, double z,
		float arg4, float arg5) 
{
    float var3 = ActiveRenderInfo.rotationX;
    float var4 = ActiveRenderInfo.rotationZ;
    float var5 = ActiveRenderInfo.rotationYZ;
    float var6 = ActiveRenderInfo.rotationXY;
    float var7 = ActiveRenderInfo.rotationXZ;
    EntityFX.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * arg4;
    EntityFX.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * arg4;
    EntityFX.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * arg4;
	this.renderManager.renderEngine.bindTexture(tex);
    GL11.glPushMatrix();
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	GL11.glTranslatef((float)x, (float)y, (float)z);
    GL11.glDepthMask(false);
    GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    Tessellator var9 = Tessellator.instance;
    var9.startDrawingQuads();
    EntityFX var11 = (EntityFX) entity;
    var11.renderParticle(var9, arg4, var3, var7, var4, var5, var6);
    var9.draw();
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