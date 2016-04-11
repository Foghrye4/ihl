package ihl.processing.metallurgy;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class MuffleFurnaceGui extends GuiContainer {
	protected static final ResourceLocation muffleFurnaceBackground = new ResourceLocation("ihl", "textures/gui/GUIMuffleFurnace.png");
	private MachineBaseContainer container;

    public MuffleFurnaceGui(MachineBaseContainer machineBaseContainer) {
            //the container is instanciated and passed to the superclass for handling
            super(machineBaseContainer);
            this.container=machineBaseContainer;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(muffleFurnaceBackground);
            int i1;
            if (this.container.tileEntity.getEnergy() > 0D)
            {
                i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(8, 14 + 12 - i1, 179, 12 - i1, 14, i1 + 2);
            }
            if (this.container.tileEntity.progress > 0)
            {
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(50),50);
        	    GL11.glEnable(GL11.GL_BLEND);
        	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            	this.drawTexturedModalRect(44, 57, 197, 0, i1 + 1, 13);
            }
            fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("ihl.gui.muffleFurnace"), 40, 12, 16768125);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(muffleFurnaceBackground);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
