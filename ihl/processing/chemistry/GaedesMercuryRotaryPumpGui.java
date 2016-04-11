package ihl.processing.chemistry;

import org.lwjgl.opengl.GL11;

import ihl.utils.IHLRenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class GaedesMercuryRotaryPumpGui extends GuiContainer {

	private GaedesMercuryRotaryPumpContainer container;
	protected static final ResourceLocation tex = new ResourceLocation("ihl", "textures/gui/GUIGaedesMercuryRotaryPump.png");

	public GaedesMercuryRotaryPumpGui(GaedesMercuryRotaryPumpContainer latheContainer) {
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
                i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(51, 37 + 12 - i1, 179, 12 - i1, 14, i1 + 2);
            }
            if (this.container.tileEntity.progress > 0)
            {	    	
            	GL11.glEnable(GL11.GL_BLEND);
            	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(27),27);
            	this.drawTexturedModalRect(17, 30, getFrameX(i1), getFrameY(i1),24,24);
            }
            if(!this.container.tileEntity.engine.correctContent())
            {
                this.mc.renderEngine.bindTexture(tex);
            	IHLRenderUtils.instance.drawMissingEngineTooltip(this, par1, par2, 79, 37, xOffset, yOffset);
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
    
    private int getFrameY(int number)
    {
    	return (number % 10) * 24 + 14;
    }
    
    private int getFrameX(int number)
    {
    	return (number / 10) * 24 + 176;
    }
}
