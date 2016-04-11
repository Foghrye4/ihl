package ihl.crop_harvestors;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BlowerGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIBlower.png");
	private BlowerContainer container;

    public BlowerGui (BlowerContainer container1) {
            //the container is instanciated and passed to the superclass for handling
            super(container1);
            this.container=container1;
    }
    
    @Override
	public void initGui()
    {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
            //the parameters for drawString are: string, x, y, color
    		String title = StatCollector.translateToLocal("tile.blowerBlock.name");
            fontRendererObj.drawStringWithShadow(title, 8, 8, 16767839);
            String airspeed = StatCollector.translateToLocal("ihl.gui.blower");
            String units = StatCollector.translateToLocal("ihl.gui.blowerUnits");
            fontRendererObj.drawString(airspeed, 8, 60, 852037);
            float e = container.tileEntity.getActive()?container.tileEntity.airSpeedBase/10F:0F;
            String eStr = String.valueOf(e);
            int w = this.fontRendererObj.getStringWidth(eStr);
            fontRendererObj.drawString(eStr, 60-w, 71, 16767839);
            fontRendererObj.drawString(units, 62, 71, 852037);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            //charge
            if (this.container.tileEntity.getStored() > 0)
            {
                int chargeLevel=Math.min(Math.round(this.container.tileEntity.getStored()*13.0F/this.container.tileEntity.maxStorage),13);
                this.drawTexturedModalRect(11, 28+13-chargeLevel, xSize, 13-chargeLevel, 7, chargeLevel);
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