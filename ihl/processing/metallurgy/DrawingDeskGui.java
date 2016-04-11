package ihl.processing.metallurgy;

import org.lwjgl.opengl.GL11;

import ic2.core.IC2;
import ic2.core.network.NetworkManager;
import ihl.utils.IHLRenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class DrawingDeskGui extends GuiContainer {

	private DrawingDeskContainer container;
	protected static final ResourceLocation tex = new ResourceLocation("ihl", "textures/gui/GUIDrawingDesk.png");

	public DrawingDeskGui(DrawingDeskContainer latheContainer) {
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
            if (this.container.tileEntity.progress > 0)
            {	    	
            	GL11.glEnable(GL11.GL_BLEND);
            	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.99F);
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(38),38);
            	this.drawTexturedModalRect(68, 30, 176, 0, i1 + 1, 10);
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
	
    @Override
	public void onGuiClosed()
    {
    	super.onGuiClosed();
    	this.container.tileEntity.isGuiScreenOpened=false;
		IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, 0);
    }

}
