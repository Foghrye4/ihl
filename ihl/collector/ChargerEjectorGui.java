package ihl.collector;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import ic2.core.network.NetworkManager;
import ic2.core.util.GuiTooltipHelper;

@SideOnly(Side.CLIENT)
public class ChargerEjectorGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIChargerEjector.png");
	private ChargerEjectorContainer container;
	private String title = StatCollector.translateToLocal("ihl.gui.charger_ejector");
	private GuiMultiTextureButton button1;
	private GuiMultiTextureButton button2;
	private GuiMultiTextureButton button3;
	private GuiMultiTextureButton button4;
	private int timer=10;
	
    public ChargerEjectorGui (ChargerEjectorContainer container1) {
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
        button1=new GuiMultiTextureButton(0, x+138, y+8, 16, 20, background,183,0,216,0);
        button1.isActive=this.container.tileEntity.autoEject;
        button2=new GuiMultiTextureButton(1, x+155, y+8, 16, 20, background,200,0,233,0);
        button2.isActive=!this.container.tileEntity.autoEject;
        button3=new GuiMultiTextureButton(2, x+138, y+29, 16, 20, background,183,21,216,21);
        button4=new GuiMultiTextureButton(3, x+155, y+29, 16, 20, background,200,21,233,21);
        this.buttonList.add(button1);
        this.buttonList.add(button2);
        this.buttonList.add(button3);
        this.buttonList.add(button4);
    }
    
    @Override
	public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, button.id);
        if (button.id == 2)
        {
        	button3.isActive=true;
        	timer=10;
        }
        if (button.id == 3)
        {
        	button4.isActive=true;
        	timer=10;
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            fontRendererObj.drawString(title, 8, 8, 6171880);
            int e = Math.min(this.container.tileEntity.getStored(), this.container.tileEntity.maxStorage)/1000;
            String eStr = String.valueOf(e);
            int w = this.fontRendererObj.getStringWidth(eStr);
            this.fontRendererObj.drawString(eStr + "kEU", 40-w, 62, 4210752);
            this.fontRendererObj.drawString("/" + this.container.tileEntity.maxStorage/1000 + "kEU", 12, 72, 4210752);
            String tooltip1 = StatCollector.translateToLocal("ihl.gui.charger_ejector_auto_eject");
            String tooltip2 = StatCollector.translateToLocal("ihl.gui.charger_ejector_do_not_auto_eject");
            String tooltip3 = StatCollector.translateToLocal("ihl.gui.charger_ejector_call_collectors");
            String tooltip4 = StatCollector.translateToLocal("ihl.gui.charger_ejector_erase_memory");
            GuiTooltipHelper.drawAreaTooltip(param1-90, param2-32, tooltip1, x+46, y-24, x+62, y-4);
            GuiTooltipHelper.drawAreaTooltip(param1-90, param2-32, tooltip2, x+63, y-24, x+79, y-4);
            GuiTooltipHelper.drawAreaTooltip(param1-90, param2-32, tooltip3, x+46, y-2, x+62, y+18);
            GuiTooltipHelper.drawAreaTooltip(param1-90, param2-32, tooltip4, x+63, y-2, x+79, y+18);
            //GuiTooltiphelper.drawAreaTooltip(param1-90, param2-32, tooltip1, x+120, 16, 132, 34);
            //GuiTooltiphelper.drawAreaTooltip(param1-90, param2-32, tooltip2, x+137, 16, 149, 34);
            //GuiTooltiphelper.drawAreaTooltip(param1-90, param2-32, tooltip3, x+120, 37, 132, 55);
            //GuiTooltiphelper.drawAreaTooltip(param1-90, param2-32, tooltip4, 137, 37, 149, 55);
     }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
            button1.isActive=this.container.tileEntity.autoEject;
            button2.isActive=!this.container.tileEntity.autoEject;
            if(timer>0)
            {
            	timer--;
            }
            if(timer==1)
            {
            	button3.isActive=false;
            	button4.isActive=false;
            }
            //charge
            if (this.container.tileEntity.getStored() > 0)
            {
                int chargeLevel=Math.min(Math.round(this.container.tileEntity.getStored()*23.0F/this.container.tileEntity.maxStorage),23);
                this.drawTexturedModalRect(x+12, y+32+23-chargeLevel, xSize, 23-chargeLevel, 7, chargeLevel);
            }

    }
    
    @Override
	public void onGuiClosed()
    {
    	super.onGuiClosed();
    	this.container.tileEntity.isGuiScreenOpened=false;
    }
}