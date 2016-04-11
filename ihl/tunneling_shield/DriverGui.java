package ihl.tunneling_shield;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import ic2.core.network.NetworkManager;

@SideOnly(Side.CLIENT)
public class DriverGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIDriver.png");
	private DriverContainer container;

    public DriverGui (DriverContainer container1) {
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
        this.buttonList.add(new GuiButton(0, x+6, y+62, 20, 20, "R"));
        this.buttonList.add(new GuiButton(1, x+6, y+42, 20, 20, "A"));
    }
    
    @Override
	public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        if (button.id == 0)
        {
            IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, 0);
        }
        if (button.id == 1)
        {
            IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, 1);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
            //the parameters for drawString are: string, x, y, color
    		String title = StatCollector.translateToLocal("ihl.gui.ts02");
            fontRendererObj.drawStringWithShadow(title, 8, 8, 16767839);
            String reverseModeOn;
            if(this.container.tileEntity.reverseModeOn)
            {
            	reverseModeOn = StatCollector.translateToLocal("ihl.gui.ts02reverse_on");
                fontRendererObj.drawStringWithShadow(reverseModeOn, 30, 68, 16767839);
            }
            else
            {
            	reverseModeOn = StatCollector.translateToLocal("ihl.gui.ts02reverse_off");
                fontRendererObj.drawString(reverseModeOn, 30, 68, 8947848);
            }
            
            String autoReverseModeOn;
            if(this.container.tileEntity.autoReverseModeOn)
            {
            	autoReverseModeOn = StatCollector.translateToLocal("ihl.gui.ts02auto_reverse_on");
                fontRendererObj.drawStringWithShadow(autoReverseModeOn, 30, 48, 16767839);
            }
            else
            {
            	autoReverseModeOn = StatCollector.translateToLocal("ihl.gui.ts02auto_reverse_off");
                fontRendererObj.drawString(autoReverseModeOn, 30, 48, 8947848);
            }
            //draws "Inventory" or your regional equivalent
            //fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
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