package ihl.processing.metallurgy;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class AchesonFurnaceGui extends GuiContainer {
	protected static final ResourceLocation achesonFurnaceBackground = new ResourceLocation("ihl", "textures/gui/GUIAchesonFurnace.png");
	private MachineBaseContainer container;

    public AchesonFurnaceGui(MachineBaseContainer machineBaseContainer) {
            super(machineBaseContainer);
            this.container=machineBaseContainer;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(achesonFurnaceBackground);
            int i1;
            if (this.container.tileEntity.getEnergy() > 0D)
            {
                i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(8, 6 + 12 - i1, 179, 12 - i1, 14, i1 + 2);
            }
            if (this.container.tileEntity.progress > 0)
            {
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(17),17);
            	this.drawTexturedModalRect(70,34, 197, 0, i1 + 1, 13);
            }
            fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("ihl.gui.achesonFurnance"), 40, 12, 16768125);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(achesonFurnaceBackground);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
