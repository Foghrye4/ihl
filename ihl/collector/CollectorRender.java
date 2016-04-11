package ihl.collector;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ihl.IHLModInfo;
import ihl.utils.IHLItemRenderer;

public class CollectorRender extends Render{
	
	private CollectorModel model = new CollectorModel();
	private ResourceLocation tex;
	private float scale;
	private int blink = 0;
	private IHLItemRenderer itemRenderer;
    
	public CollectorRender(boolean heavy) 
	{
		super();
		this.itemRenderer=new IHLItemRenderer();
		if(heavy)
		{
			scale = 1F/80F;
			tex = new ResourceLocation(IHLModInfo.MODID+":textures/entities/collectorHeavy.png");
		}
		else
		{
			scale = 1F/100F;
			tex = new ResourceLocation(IHLModInfo.MODID+":textures/entities/collector.png");
		}
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float arg4, float arg5) 
	{	
			if(blink<30)
			{
				blink++;
			}
			else
			{
				blink=0;
			}
			int levelBar=0;
			if(entity instanceof CollectorHeavyEntity)
			{
				CollectorHeavyEntity cEntity = (CollectorHeavyEntity) entity;
				levelBar = Math.max(0,model.Indicator.cubeList.size()-1-Math.round(cEntity.getRenderEnergyLevel()*model.Indicator.cubeList.size()));
				for(int col=0;col<2;col++)
				{
					for(int row=0;row<2;row++)
					{
						ItemStack stack = cEntity.getVisibleItemStack(col+row*2);
						if(stack!=null)
						{
							this.itemRenderer.doRender(this.renderManager, stack, x-0.125D+0.25D*col, y, z-0.125D+0.25D*row);
						}
					}
				}

			}
			else if(entity instanceof CollectorEntity)
			{
				CollectorEntity cEntity = (CollectorEntity) entity;
				levelBar = Math.max(0,model.Indicator.cubeList.size()-1-Math.round(cEntity.getRenderEnergyLevel()*model.Indicator.cubeList.size()));
				ItemStack stack = cEntity.getVisibleItemStack();
				if(stack!=null)
				{
					this.itemRenderer.doRender(this.renderManager, stack, x, y, z);
				}
			}
			bindTexture(tex);
			GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y+0.25F, (float)z);
			GL11.glScalef(1.0F, -1F, -1F);
			GL11.glRotated(entity.rotationYaw+90D, 0D, 1.0D, 0D);
			model.Base.render(scale);
			for(int i=0;i<4;i++)
			{
				model.Eleron.rotateAngleY=i*(float)Math.PI/2;
				model.Eleron.render(scale);
			}
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0xf0);
			for(int i=0;i<4;i++)
			{
				GL11.glRotatef(i* 90F, 0.0F, 1.0F, 0.0F);
				model.Flares.render(scale);
				if(levelBar<model.Indicator.cubeList.size()-8 || blink > 15)
				{
					ModelBox box = (ModelBox) model.Indicator.cubeList.get(levelBar);
					box.render(Tessellator.instance,scale);
				}
				else if(blink < 15)
				{
					ModelBox box = (ModelBox) model.Indicator.cubeList.get(model.Indicator.cubeList.size()-1);
					box.render(Tessellator.instance,scale);
				}
			}
			GL11.glPopMatrix(); //end

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity arg0) {
		return this.tex;
	}
	
}