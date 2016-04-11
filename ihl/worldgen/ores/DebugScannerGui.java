package ihl.worldgen.ores;

import java.util.List;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DebugScannerGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIDebugScanner.png");
	private DebugScannerContainer container;

    public DebugScannerGui (DebugScannerContainer container1) 
    {
            super(container1);
            this.container=container1;
            this.ySize=DebugScannerContainer.height;
            this.xSize=DebugScannerContainer.width;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;
        List<String> ode = container.tileEntity.oreDictionaryEntries;
        int verticalOffset = 31;
		for(int i=0;i<ode.size();i++)
		{
			int colorIndex = 16777215;
			String string = ode.get(i);
			if(string.startsWith(" "))
			{
				colorIndex = 15658734;
			}
			if(string.startsWith(" -"))
			{
				colorIndex = 14540253;
			}
			if(string.startsWith("    "))
			{
				colorIndex = 13421772;
			}
			 if(string.length()>38)
			 {
				 String string1=string.substring(0,38);
				 String string2=string.substring(38);
				 fontRendererObj.drawStringWithShadow(string1, 9, i*10+verticalOffset, colorIndex);
				 verticalOffset+=10;
				 fontRendererObj.drawStringWithShadow(string2, 9, i*10+verticalOffset, colorIndex);
			 }
			 else
			 {
				 fontRendererObj.drawStringWithShadow(string, 9, i*10+verticalOffset, colorIndex);
			 }
		}
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}