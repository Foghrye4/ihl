package ihl.tunneling_shield;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ihl.IHLModInfo;

public class DriverRender extends TileEntitySpecialRenderer{
private DriverModel model = new DriverModel();
private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/shield.png");

public DriverRender(){}

public void renderAModelAt(DriverTileEntity tile, double d, double d1, double d2, float f) {
int rotation = 0;
if(tile.getWorldObj() != null)
{
	switch (tile.getFacing())
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
bindTexture(tex); //texture
GL11.glPushMatrix();
GL11.glTranslatef((float)d + 0.5F, (float)d1 + 1.5F, (float)d2 + 0.5F);
GL11.glScalef(1.0F, -1F, -1F);
GL11.glRotatef(rotation*90, 0.0F, 1.0F, 0.0F);
model.Base.render(1.0F/16.0F);
GL11.glPopMatrix(); //end
}

@Override
public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
{
this.renderAModelAt((DriverTileEntity)par1TileEntity, par2, par4, par6, par8);
}
}