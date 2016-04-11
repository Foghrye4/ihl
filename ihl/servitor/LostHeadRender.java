package ihl.servitor;

import ihl.IHLModInfo;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class LostHeadRender extends Render{
	private SkullModel model = new SkullModel();
	private ResourceLocation tex;
	private float scale;
	
public LostHeadRender()
{
	super();
	scale = 1F/80F;
	tex = new ResourceLocation(IHLModInfo.MODID+":textures/items/skull.png");
}

@Override
public void doRender(Entity entity, double x, double y, double z,
		float arg4, float arg5) 
{
	bindTexture(tex);
	GL11.glPushMatrix();
	GL11.glTranslated(x, y, z);
	GL11.glScalef(1.0F, -1F, -1F);
	GL11.glRotated(entity.rotationYaw+180D, 0D, 1.0D, 0D);
	model.Base.render(scale);
	GL11.glPopMatrix();
}

	@Override
	protected ResourceLocation getEntityTexture(Entity arg0) 
	{
		return tex;
	}
}