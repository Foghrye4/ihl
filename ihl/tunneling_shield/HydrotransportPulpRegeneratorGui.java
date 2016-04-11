package ihl.tunneling_shield;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ihl.utils.IHLRenderUtils;

@SideOnly(Side.CLIENT)
public class HydrotransportPulpRegeneratorGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIHydrotransportPulpRegenerator.png");
	private HydrotransportPulpRegeneratorContainer container;

    public HydrotransportPulpRegeneratorGui (HydrotransportPulpRegeneratorContainer container1) {
            //the container is instanciated and passed to the superclass for handling
            super(container1);
            this.container=container1;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int i1;
            if (this.container.tileEntity.getEnergy() > 0D)
            {
            	GL11.glEnable(GL11.GL_BLEND);
            	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
                i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(12, 16 + 12 - i1, 179, 13 - i1, 13, i1 + 1);
            }
            if (this.container.tileEntity.progress > 0)
            {
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(18),18);
            	this.drawTexturedModalRect(73, 34, 197, 0, i1 + 1, 13);
            }
            if (this.container.tileEntity.fluidTank.getFluidAmount() > 0)
            {
            	IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.fluidTank, 57, 16, 69, 63, zLevel, par1, par2, xOffset, yOffset);
            }
            this.mc.renderEngine.bindTexture(background);
            if(!this.container.tileEntity.engine.correctContent())
            {
            	IHLRenderUtils.instance.drawMissingEngineTooltip(this, par1, par2, 26, 33, xOffset, yOffset);
            }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}