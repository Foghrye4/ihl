package ihl.tunneling_shield;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ihl.IHLModInfo;

public class DriverRenderEntity extends Render{
	
	private DriverModel model = new DriverModel();
	private ResourceLocation tex = new ResourceLocation(IHLModInfo.MODID+":textures/blocks/shield.png");
	private float rotationAmount=1F;
	private float rotationAmount2=0F;
	public DriverRenderEntity() 
	{
		super();
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float arg4, float arg5) 
	{
		double d;
		double d1;
		double d2;
		if(((DriverEntity)entity).parent!=null)
		{
			DriverTileEntity tile = ((DriverEntity)entity).parent;
			float shift=tile.getModelShiftAmount();
			d=x-entity.lastTickPosX+tile.xCoord;
			d1=y-entity.lastTickPosY+tile.yCoord;
			d2=z-entity.lastTickPosZ+tile.zCoord;
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
			bindTexture(tex);
			GL11.glPushMatrix();
			GL11.glTranslatef((float)d + 0.5F, (float)d1 + 1.5F, (float)d2 + 0.5F);
			GL11.glScalef(1.0F, -1F, -1F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glRotatef(rotation*90, 0.0F, 1.0F, 0.0F);
			if(tile.hasShield && tile.getActive() && tile.shaftDestroyedAtA==0)
			{
				rotationAmount+=0.01F;
			}
			rotationAmount2 += 0.01F;
			if(tile.hasShield)
			{
				if(tile.shaftDestroyedAtA==0)
				{
					model.ShieldB.rotateAngleZ=rotationAmount+(float)Math.PI/8.0F;
					model.ShieldB.offsetZ=shift;
					model.ShieldB.render(1.0F/16.0F);

					model.ShieldC.rotateAngleZ=rotationAmount+3*(float)Math.PI/8.0F;
					model.ShieldC.offsetZ=shift;
					model.ShieldC.render(1.0F/16.0F);
					
					if(tile.advancedShield)
					{
						model.AdvancedShieldA.rotateAngleZ=rotationAmount+(float)Math.PI/4.0F;
						model.AdvancedShieldA.offsetZ=shift;
						model.AdvancedShieldA.render(1.0F/16.0F);
					}
					else
					{
						model.ShieldA.rotateAngleZ=rotationAmount+(float)Math.PI/4.0F;
						model.ShieldA.offsetZ=shift;
						model.ShieldA.render(1.0F/16.0F);
					}

					model.Shield.rotateAngleZ=rotationAmount;
					model.Shield.offsetZ=shift;
					model.Shield.render(1.0F/16.0F);
					GL11.glScalef(1.0F, 1.0F, 1.0F + shift);
					model.ShaftA.offsetZ=0.5F;
					model.ShaftB.offsetZ=0.5F;
					model.ShaftA.rotateAngleZ=rotationAmount;
					model.ShaftB.rotateAngleZ=rotationAmount;
					model.ShaftA.render(1.0F/16.0F);
					model.ShaftB.render(1.0F/16.0F);
				}
				else
				{
					model.ShieldB.rotateAngleZ=rotationAmount+(float)Math.PI/8.0F;
					model.ShieldC.rotateAngleZ=rotationAmount+3*(float)Math.PI/8.0F;
					model.ShieldA.rotateAngleZ=rotationAmount+(float)Math.PI/4.0F;
					model.AdvancedShieldA.rotateAngleZ=rotationAmount+(float)Math.PI/4.0F;
					model.Shield.rotateAngleZ=rotationAmount;
					model.ShaftA.rotateAngleZ=rotationAmount;

					model.ShieldB.offsetZ=shift;
					model.ShieldB.render(1.0F/16.0F);

					model.ShieldC.offsetZ=shift;
					model.ShieldC.render(1.0F/16.0F);
				
					if(tile.advancedShield)
					{
						model.AdvancedShieldA.offsetZ=shift;
						model.AdvancedShieldA.render(1.0F/16.0F);
					}
					else
					{
						model.ShieldA.offsetZ=shift;
						model.ShieldA.render(1.0F/16.0F);
					}
					
					model.Shield.offsetZ=shift;
					model.Shield.render(1.0F/16.0F);
					GL11.glScalef(1.0F, 1.0F, tile.shaftDestroyedAtA*2);
					model.ShaftB.offsetZ=0.5F-0.2F/tile.shaftDestroyedAtA;
					model.ShaftB.rotateAngleZ=rotationAmount2;
					model.ShaftB.render(1.0F/16.0F);
					GL11.glScalef(1.0F, 1.0F, (shift-tile.shaftDestroyedAtB)/tile.shaftDestroyedAtA);
					model.ShaftA.offsetZ=0.5F*(tile.shaftDestroyedAtB+0.5F)/(shift-tile.shaftDestroyedAtB);
					model.ShaftA.render(1.0F/16.0F);
				}
			}
			GL11.glPopMatrix(); //end
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity arg0) {
		return this.tex;
	}
}