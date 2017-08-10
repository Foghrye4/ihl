package ihl.crop_harvestors;

import org.lwjgl.opengl.GL11;

import ihl.IHLModInfo;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class BlobRenderFX extends Render{
	private ResourceLocation tex, tex2;
	
public BlobRenderFX()
{
	super();
	tex = new ResourceLocation(IHLModInfo.MODID+":textures/particles/blob.png");
	tex2 = new ResourceLocation(IHLModInfo.MODID+":textures/particles/blobOfResin.png");
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
	if(((BlobEntityFX)entity).fluid==BlobEntityFX.FluidType.RESIN)
	{
		this.renderManager.renderEngine.bindTexture(tex);
	}
	else
	{
		this.renderManager.renderEngine.bindTexture(tex2);
	}
    GL11.glPushMatrix();
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	GL11.glTranslatef((float)x, (float)y, (float)z);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
    Tessellator var9 = Tessellator.instance;
    var9.startDrawingQuads();
    EntityFX var11 = (EntityFX) entity;
    var11.renderParticle(var9, arg4, var3, var7, var4, var5, var6);
    var9.draw();
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
    GL11.glPopMatrix();
}

	@Override
	protected ResourceLocation getEntityTexture(Entity arg0) 
	{
		return tex;
	}
}