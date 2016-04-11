package ihl.enviroment;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import ihl.IHLModInfo;
import ihl.utils.IHLRenderUtils;

public class SpotlightRender extends TileEntitySpecialRenderer{
private SpotlightModel model = new SpotlightModel();
private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/spotlight.png");
private final float scale=1F/16F;

	public SpotlightRender() {}


	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float par8)
	{
		SpotlightTileEntity cte = (SpotlightTileEntity)tile;
		int rotation = 0;
		int rotationz = 0;
		if(tile.getWorldObj() != null)
		{
			switch (cte.getFacing())
			{
			case 0:
				rotationz = 1;
				break;
			case 1:
				rotationz = 3;
				break;
			case 2:
				rotation = 2;
				break;
			case 5:
				rotation = 3;
				break;
			case 3:
				rotation = 0;
				break;
			case 4:
				rotation = 1;
				break;
			default:
				rotation = 0;
			}
		}
		else
		{
			return;
		}
		model.Base.rotateAngleY=(float) (rotation*Math.PI/2);
		model.Base.rotateAngleX=(float) (rotationz*Math.PI/2);
		model.RotatingPart1.rotateAngleY=(float) (rotation*Math.PI/2);
		model.RotatingPart1.rotateAngleX=(float) (rotationz*Math.PI/2);
		model.RotatingPart2.rotateAngleY=(float) (rotation*Math.PI/2);
		model.RotatingPart2.rotateAngleX=(float) (rotationz*Math.PI/2);
		model.RotatingPart3Halo.rotateAngleY=(float) (rotation*Math.PI/2);
		model.RotatingPart3Halo.rotateAngleX=(float) (rotationz*Math.PI/2);
		model.RotatingPart2.rotateAngleX+=cte.rotationPitch;
		model.RotatingPart3Halo.rotateAngleX+=cte.rotationPitch;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		bindTexture(tex);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		model.Base.render(scale);
        GL11.glTranslatef(model.RotatingPart1.rotationPointX*scale, model.RotatingPart1.rotationPointY*scale, model.RotatingPart1.rotationPointZ*scale);
		switch(cte.getFacing())
		{
			case 0:
				GL11.glRotatef(cte.rotationYaw * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				break;
			case 1:
				GL11.glRotatef(cte.rotationYaw * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				break;
			case 2:
				GL11.glRotatef(cte.rotationYaw * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				break;
			case 3:
				GL11.glRotatef(cte.rotationYaw * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				break;
			case 4:
				GL11.glRotatef(cte.rotationYaw * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				break;
			case 5:
				GL11.glRotatef(cte.rotationYaw * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				break;
		}
        GL11.glTranslatef(-model.RotatingPart1.rotationPointX*scale, -model.RotatingPart1.rotationPointY*scale, -model.RotatingPart1.rotationPointZ*scale);
		model.RotatingPart1.render(scale);
		model.RotatingPart2.render(scale);
		if(cte.getActive())
		{
			IHLRenderUtils.instance.enableAmbientLighting();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			model.RotatingPart3Halo.render(scale);
			IHLRenderUtils.instance.disableAmbientLighting();
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix(); //end

	}
}