package ihl.flexible_cable;

import ihl.IHLModInfo;
import ihl.model.ModelTube;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class NodeRender extends Render
{
	
	private ModelTube model;
	private ModelTube modelThin;
	private ResourceLocation tex;
	private float scale;
    
	public NodeRender() 
	{
			super();
			scale = 1F/16F;
			model=new ModelTube(null, 0, 0, -4F, -4F, -3F, 8, 8, 6,0f, 0.5f,0.99f, ForgeDirection.NORTH);
			modelThin=new ModelTube(null, 0, 0, -1F, -1F, -3F, 2, 2, 6,0f, 0f,0.99f, ForgeDirection.NORTH);
			tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/junctionBox.png");
	}

	@Override
	public void doRender(Entity entity, double x1, double y1, double z1, float rotationYaw, float iFrame) 
	{	
			bindTexture(tex);
			GL11.glPushMatrix();
			NodeEntity ne = (NodeEntity) entity;
			float x = (float) (ne.lastTickRenderPosX + (ne.renderPosX-ne.lastTickRenderPosX)*iFrame-RenderManager.renderPosX);
			float y = (float) (ne.lastTickRenderPosY + (ne.renderPosY-ne.lastTickRenderPosY)*iFrame-RenderManager.renderPosY);
			float z = (float) (ne.lastTickRenderPosZ + (ne.renderPosZ-ne.lastTickRenderPosZ)*iFrame-RenderManager.renderPosZ);
			GL11.glTranslatef(x, y, z);
			GL11.glScalef(0.25F, -0.25F, -0.25F);
			int red = ne.colorIndex>>16;
			int green = (ne.colorIndex>>8) & 255;
			int blue = ne.colorIndex & 255;
			for(int i=0;i<ne.n;i+=ne.renderEvery)
			{
				GL11.glRotatef(-ne.rotationPitchArray[i] * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
	            GL11.glRotatef(ne.rotationYawArray[i] * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
	            float cc=ne.rotationYawArray[i]*0.25f;
	            float red1 = Math.min(Math.max(red/255f+cc,red/511f),1f);
	            float green1 = Math.min(Math.max(green/255f+cc,green/511f),1f);
	            float blue1 = Math.min(Math.max(blue/255f+cc,blue/511f),1f);
	            GL11.glColor3f(red1, green1, blue1);
	            if(ne.type==0)
	            {
	            	modelThin.renderCached(Tessellator.instance, scale);
	            }
	            else
	            {
					model.renderCached(Tessellator.instance, scale);
	            }
	            GL11.glRotatef(-ne.rotationYawArray[i] * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
	            GL11.glRotatef(ne.rotationPitchArray[i] * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-ne.translationX[i]*4f, ne.translationY[i]*4f, ne.translationZ[i]*4f);
			}

			GL11.glPopMatrix(); //end
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity arg0) 
	{
		return this.tex;
	}
}
	