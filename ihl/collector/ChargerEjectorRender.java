package ihl.collector;

import org.lwjgl.opengl.GL11;

import ihl.IHLModInfo;
import ihl.utils.IHLItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ChargerEjectorRender extends TileEntitySpecialRenderer {
	private ChargerEjectorModel model = new ChargerEjectorModel();
	private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/chargerEjector.png");
	private int blink=0;
	private IHLItemRenderer itemRenderer=new IHLItemRenderer();
	
	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderAModelAt((ChargerEjectorTileEntity)par1TileEntity, par2, par4, par6, par8);
	}

	private void renderAModelAt(ChargerEjectorTileEntity te,
			double x, double y, double z, float par8) {
		if(blink<60)
		{
			blink++;
		}
		else
		{
			blink=0;
		}
		int[] x0={0,-8,0,8,0,-8,0,8,-4};
		int[] z0={0,0,-8,0,0,0,8,0,-4};
		int[] y0={0,0,0,0,-12,0,0,0,-12};
		int[] x1={0,-8,-8,0,0,-8,-8, 0,-4};
		int[] z1={0, 0, 8,8,8, 8, 0, 0, 4};
		int[] y1={0, 0, 0,0,6, 6, 6, 6,12};
		for(int i=0;i<9;i++)
		{
			this.itemRenderer.doRender(RenderManager.instance, te.chargeSlot.get(i), x+x1[i]/16F+0.75D, y+y1[i]/16F+0.25D, z+z1[i]/16F+0.25D);
		}
		bindTexture(tex); //texture
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 12F/16F, (float)z + 0.5F);
		GL11.glScalef(1.0F, -0.5F, -1F);
		model.Base.render(1.0F/16.0F);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0xf0);
		for(int i=0;i<9;i++)
		{

			GL11.glTranslatef(x0[i]/16F, y0[i]/16F, z0[i]/16F);
			if(te.chargeSlotStatus[i]<=0)
			{
				model.LightOffA.render(1F/16F);
				model.LightOffB.render(1F/16F);
				model.LightOffC.render(1F/16F);
			}
			else if(te.chargeSlotStatus[i]==2)
			{
				model.LightOnA.render(1F/16F);
				model.LightOnB.render(1F/16F);
				model.LightOnC.render(1F/16F);
			}
			else
			{
				if(blink<20)
				{
					model.LightOnA.render(1F/16F);
					model.LightOffB.render(1F/16F);
					model.LightOffC.render(1F/16F);
				}
				else if(blink>=20 && blink<40)
				{
					model.LightOffA.render(1F/16F);
					model.LightOnB.render(1F/16F);
					model.LightOffC.render(1F/16F);
				}
				else
				{
					model.LightOffA.render(1F/16F);
					model.LightOffB.render(1F/16F);
					model.LightOnC.render(1F/16F);
				}
			}
		}
		GL11.glPopMatrix(); //end
	}

}
