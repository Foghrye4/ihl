package ihl.enviroment;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import ihl.IHLModInfo;

public class LightBulbRender extends TileEntitySpecialRenderer{
private LightBulbModel model = new LightBulbModel();
private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/lightBulb.png");
private final float scale=1F/16F;

	public LightBulbRender() {}


	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float par8)
	{
		LightBulbTileEntity cte = (LightBulbTileEntity)tile;
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
		model.BaseON.rotateAngleY=(float) (rotation*Math.PI/2);
		model.BaseON.rotateAngleX=(float) (rotationz*Math.PI/2);
		model.Base.rotateAngleY=(float) (rotation*Math.PI/2);
		model.Base.rotateAngleX=(float) (rotationz*Math.PI/2);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		bindTexture(tex);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		if(cte.getActive())
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			model.BaseON.render(scale);
		}
		else
		{
			model.Base.render(scale);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix(); //end

	}
}