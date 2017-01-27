package ihl.flexible_cable;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import ihl.utils.IHLItemRenderer;

public class IronWorkbenchRender extends TileEntitySpecialRenderer{
private IHLItemRenderer itemRenderer=new IHLItemRenderer(true);

public IronWorkbenchRender(){}

public void renderAModelAt(IronWorkbenchTileEntity tile, double x, double y, double z, float f) {
	GL11.glPushMatrix();
	GL11.glTranslatef((float)x + 0.5F, (float)y+0.5F, (float)z + 0.5F);
	GL11.glRotatef(90f, 1F, 0F, 0F);
	int index = 0;
	for (int i = 0; i < tile.tools.size() && index < 8; i++) {
		if (tile.tools.get(i)!=null) {
			float iy = index >= 4 ? -0.5f : 0f;
			float ix = (index % 2) * 0.4f-0.2f;
			float iz = (index / 2) * 0.4f-0.2f + iy * 1.5f;
			this.itemRenderer.doRender(RenderManager.instance,tile.tools.get(i),ix,iz,iy-0.002f*index);
			index++;
		}
	}
	GL11.glPopMatrix();
}

	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderAModelAt((IronWorkbenchTileEntity)par1TileEntity, par2, par4, par6, par8);
	}
}
