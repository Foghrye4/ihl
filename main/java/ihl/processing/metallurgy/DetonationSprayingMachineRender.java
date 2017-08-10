package ihl.processing.metallurgy;
import org.lwjgl.opengl.GL11;

import ic2.api.tile.IWrenchable;
import ihl.IHLModInfo;
import ihl.utils.IHLItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class DetonationSprayingMachineRender extends TileEntitySpecialRenderer{
private DetonationSprayingMachineModel model = new DetonationSprayingMachineModel();
private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/detonationSprayingMachine.png");
private final float scale=1F/16F;
private IHLItemRenderer itemRenderer=new IHLItemRenderer(true);

public DetonationSprayingMachineRender(){}

public void renderAModelAt(DetonationSprayingMachineTileEntity tile, double x, double y, double z, float f) {
	GL11.glPushMatrix();
	GL11.glTranslatef((float)x + 0.5F, (float)y+0.5F, (float)z + 0.5F);
	int rotation = 0;
	if(tile.getWorldObj() != null)
	{
		switch (((IWrenchable)tile).getFacing())
		{
		case 2:
			rotation = 0;
			break;
		case 5:
			rotation = 1;
			break;
		case 3:
			rotation = 2;
			break;
		case 4:
			rotation = 3;
			break;
		default:
			rotation = 0;
		}
	}
	GL11.glRotatef(-rotation*90f, 0F, 1F, 0F);
	if(tile.input.get()!=null)
	{
		this.itemRenderer.doRender(RenderManager.instance,tile.input.get(),0F,-0.2F,-0.35F);
	}
	bindTexture(tex); //texture
    GL11.glScalef(1F, -1F, -1F);
    GL11.glTranslatef(0F, -1.0F, 0F);
	model.Base.render(scale);
	GL11.glPopMatrix();
}

	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderAModelAt((DetonationSprayingMachineTileEntity)par1TileEntity, par2, par4, par6, par8);
	}
}