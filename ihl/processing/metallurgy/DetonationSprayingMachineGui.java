package ihl.processing.metallurgy;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class DetonationSprayingMachineGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIDetonationSprayingMachine.png");
	private static final String hint = StatCollector.translateToLocal("ihl.dsmhint");

    public DetonationSprayingMachineGui (DetonationSprayingMachineContainer detonationSprayingMachineContainer) {
            //the container is instanciated and passed to the superclass for handling
            super(detonationSprayingMachineContainer);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.renderEngine.bindTexture(background);
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        	Iterator<String> noteListIterator = this.mc.fontRenderer.listFormattedStringToWidth(hint, 140).iterator();
        	int yTextPos=40;
        	while(noteListIterator.hasNext())
        	{
            	GuiDraw.fontRenderer.drawStringWithShadow(noteListIterator.next(), x+15, y+yTextPos, 16777215);
            	yTextPos+=10;
        	}
    }
}