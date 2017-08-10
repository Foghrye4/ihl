package ihl.crop_harvestors;
import org.lwjgl.opengl.GL11;

import ihl.IHLModInfo;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class SackRender extends TileEntitySpecialRenderer{
private SackModel model = new SackModel();
private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/sack.png");
private final float maxRenderLiquidLevel=0.5F;
private final float minRenderLiquidLevel=14.5F;
private final float scale=1F/16F;
private float overflow=0F;


public SackRender(){}

public void renderAModelAt(SackTileEntity tile, double d, double d1, double d2, float f) {
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
model.Base.render(scale);
model.Top.render(scale);
model.Rope1.render(scale);
model.Rope2.render(scale);
GL11.glEnable(GL11.GL_BLEND);
GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
GL11.glColor4f(1f,1f,1f,1f);
model.Liquid2.offsetY=model.Liquid.offsetY=(minRenderLiquidLevel-(minRenderLiquidLevel-maxRenderLiquidLevel)*tile.getRenderLiquidLevel())*scale;
if(tile.getRenderLiquidLevel()>0)
{
	if(tile.visibleFluidId!=-1)
	{
		if(tile.visibleFluidId==FluidRegistry.getFluid("fluidrubbertreesap").getID())
		{
			model.Liquid.render(scale);
		}
		else if(tile.visibleFluidId==FluidRegistry.getFluid("spruceresin").getID())
		{
			model.Liquid2.render(scale);
		}
	}
}
if(tile.getRenderLiquidLevel()>0.98F)
{
	if(overflow<0.5F)overflow+=0.001F;
	model.Liquid2_overflow.offsetZ=model.Liquid_overflow.offsetZ=-overflow*scale;
	if(tile.visibleFluidId!=-1)
	{
		if(tile.visibleFluidId==FluidRegistry.getFluid("fluidrubbertreesap").getID())
		{
			model.Liquid_overflow.render(scale);
		}
		else if(tile.visibleFluidId==FluidRegistry.getFluid("spruceresin").getID())
		{
			model.Liquid2_overflow.render(scale);
		}
	}
}
GL11.glDisable(GL11.GL_BLEND);
GL11.glPopMatrix(); //end
}

	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderAModelAt((SackTileEntity)par1TileEntity, par2, par4, par6, par8);
	}
}