package ihl.collector;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import ihl.utils.IHLItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class GlassBoxRender extends TileEntitySpecialRenderer {
	private IHLItemRenderer itemRenderer=new IHLItemRenderer();
	private Random rand = new Random();
	private float[] randomPosMatrix={rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat()};
	
	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderAModelAt((GlassBoxTileEntity)par1TileEntity, par2, par4, par6, par8);
	}

	private void renderAModelAt(GlassBoxTileEntity te,
			double x, double y, double z, float par8) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y+0.5F, (float)z + 0.5F);
		float rotation=0F;
		for(int i=0;i<te.invSlot.size();i++)
		{
			if(te.invSlot.get(i)!=null)
	    	{
				rotation+=(randomPosMatrix[i*3]-0.5F)*90F; 
				GL11.glRotatef((randomPosMatrix[i*3]-0.5F)*90F, 0F, 1F, 0F);
				if(te.invSlot.get(i).getItem().isFull3D())
				{
					GL11.glTranslatef(0, 0.2F, 0);
		    		this.itemRenderer.doRender(RenderManager.instance,te.invSlot.get(i),randomPosMatrix[i*3]*0.5-0.25F,randomPosMatrix[i*3+1]*0.5-0.25F,randomPosMatrix[i*3+2]*0.5-0.25F);
					GL11.glTranslatef(0, -0.2F, 0);
				}
				else
				{
		    		this.itemRenderer.doRender(RenderManager.instance,te.invSlot.get(i),randomPosMatrix[i*3]*0.5-0.25F,randomPosMatrix[i*3+1]*0.5-0.25F,randomPosMatrix[i*3+2]*0.5-0.25F);
				}
	    	}
		}
		GL11.glPopMatrix(); //end
	}

}
