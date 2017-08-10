package ihl.processing.metallurgy;
import org.lwjgl.opengl.GL11;

import ic2.api.tile.IWrenchable;
import ihl.IHLModInfo;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RollingMachineRender extends TileEntitySpecialRenderer{
private RollingMachinePart1Model model = new RollingMachinePart1Model();
private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/coiler.png");
private final float scale=1F/16F;

	public RollingMachineRender() {}


	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float par8)
	{
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
		else
		{
			return;
		}
		RollingMachinePart1TileEntity cte = (RollingMachinePart1TileEntity)tile;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		GL11.glRotatef(rotation*90, 0.0F, 1.0F, 0.0F);
		
		bindTexture(tex);
		model.Base.render(scale);
		if(cte.getActive())
		{
			model.RotatingPart1.rotateAngleZ-=0.01F;
			model.RotatingPart2.rotateAngleZ+=0.01F;
			model.RotatingPart1.render(scale);
			model.RotatingPart2.render(scale);
			model.MotorPart1.rotateAngleZ-=0.03F;
			model.MotorPart1.render(scale);
			model.MotorPart2.render(scale);
			model.Plate.offsetX+=0.001F;
			if(model.Plate.offsetX>1F){model.Plate.offsetX=0;}
			model.Plate.render(scale);
		}
		else
		{
			model.Plate.offsetX=0F;
			if(cte.assembled)
			{
				model.RotatingPart1.render(scale);
				model.RotatingPart2.render(scale);
			}
			model.MotorPart1.render(scale);
			model.MotorPart2.render(scale);
		}
		GL11.glPopMatrix(); //end

	}
}