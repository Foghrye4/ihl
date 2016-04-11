package ihl.processing.metallurgy;

import ic2.core.IC2;
import ic2.core.network.NetworkManager;
import ihl.collector.GuiMultiTextureButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class VacuumInductionMeltingFurnaceGui extends GuiContainer {
	protected static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIVacuumInductionMeltingFurnace.png");
	private VacuumInductionMeltingFurnaceContainer container;
	private GuiMultiTextureButton button1;
	private int timer=10;


    public VacuumInductionMeltingFurnaceGui(VacuumInductionMeltingFurnaceContainer vacuumInductionMeltingFurnaceContainer) {
            //the container is instanciated and passed to the superclass for handling
            super(vacuumInductionMeltingFurnaceContainer);
            this.container=vacuumInductionMeltingFurnaceContainer;
    }

	@Override
	public void initGui()
    {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        button1=new GuiMultiTextureButton(0, x+13, y+55, 27, 27, background,13,55,229,229);
        this.buttonList.add(button1);
    }
    
    @Override
	public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, button.id);
        if (button.id == 0)
        {
        	button1.isActive=false;
        	timer=10;
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            fontRendererObj.drawString(StatCollector.translateToLocal("ihl.gui.muffleFurnace"), 40, 12, 14722081);
            this.mc.renderEngine.bindTexture(background);
            int i1;
            if (this.container.tileEntity.getEnergy() > 0D)
            {
                i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(8, 6 + 12 - i1, 179, 12 - i1, 14, i1 + 2);
            }
            if (this.container.tileEntity.progress > 0)
            {
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(50),50);
        	    GL11.glEnable(GL11.GL_BLEND);
        	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            	this.drawTexturedModalRect(64, 63, 197, 0, i1 + 1, 13);
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
            if(timer>0)
            {
            	timer--;
            }
            if(timer==1)
            {
            	button1.isActive=true;
            }
    }
}
