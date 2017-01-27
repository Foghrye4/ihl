package ihl.processing.chemistry;

import org.lwjgl.opengl.GL11;

import ihl.utils.IHLRenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class CryogenicDistillerGui extends GuiContainer {

	private CryogenicDistillerContainer container;
	protected static final ResourceLocation tex = new ResourceLocation("ihl", "textures/gui/GUICryogenicDistiller.png");

	public CryogenicDistillerGui(CryogenicDistillerContainer latheContainer) {
		super(latheContainer);
		container = latheContainer;
	}
	

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(tex);
            int i1;
            if (this.container.tileEntity.getEnergy() > 0D)
            {
            	GL11.glEnable(GL11.GL_BLEND);
            	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
                i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(9, 15 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
            }
            if (this.container.tileEntity.progress > 0)
            {
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(18),18);
            	this.drawTexturedModalRect(81, 35, 198, 0, i1 + 1, 13);
            }
            if (this.container.tileEntity.fluidTankProducts.getFluid()!=null && this.container.tileEntity.fluidTankProducts.getFluidAmount() > 0)
            {
            	IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.fluidTankProducts, 125, 28, 137, 67, zLevel, par1, par2, xOffset, yOffset);
            }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(tex);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
