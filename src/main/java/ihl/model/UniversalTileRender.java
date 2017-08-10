package ihl.model;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.tile.IWrenchable;
import ihl.IHLModInfo;
import ihl.flexible_cable.IronWorkbenchModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class UniversalTileRender extends TileEntitySpecialRenderer{
private ModelBase model = new IronWorkbenchModel();
private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/ironWorkbench.png");
private final float scale=1F/16F;

@SideOnly(value=Side.CLIENT)
public UniversalTileRender(ModelBase model1, ResourceLocation texture)
{
	this.model=model1;
	this.tex=texture;
}

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
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
		GL11.glScalef(1.0F, -1F, -1F);
		GL11.glRotatef(rotation*90, 0.0F, 1.0F, 0.0F);
		
		bindTexture(tex);
    	for(int i = 0;i<model.boxList.size();i++)
    	{
    		if(model.boxList.get(i) instanceof ModelRenderer)
    		{
    			ModelRenderer piece = (ModelRenderer) model.boxList.get(i);
    			piece.render(scale);
    		}
    		else if(model.boxList.get(i) instanceof IHLModelRenderer)
    		{
    			IHLModelRenderer piece = (IHLModelRenderer) model.boxList.get(i);
    			piece.render(scale);
    		}
    	}
	    GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix(); //end

	}
}