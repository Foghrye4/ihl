package ihl.processing.metallurgy;

import org.lwjgl.opengl.GL11;

import ihl.utils.IHLRenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class CoilerGui extends GuiContainer {

	private CoilerContainer container;
	protected static final ResourceLocation tex = new ResourceLocation("ihl", "textures/gui/GUICoiler.png");

	public CoilerGui(CoilerContainer latheContainer) {
		super(latheContainer);
		container = latheContainer;
	}
	

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("ihl.gui.coiler"), 40, 12, 16768125);
            this.mc.renderEngine.bindTexture(tex);
            int i1;
            if (this.container.tileEntity.getEnergy() > 0D)
            {
                i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(39, 55 + 12 - i1, 179, 12 - i1, 14, i1 + 2);
            }
            if(!this.container.tileEntity.engine.correctContent())
            {
            	IHLRenderUtils.instance.drawMissingEngineTooltip(this, par1, par2, 47, 56, xOffset, yOffset);
            }
       		IHLRenderUtils.instance.drawTooltip(par1,par2,9,11,xOffset,yOffset,StatCollector.translateToLocal("ihl.coiler.tip"));
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
