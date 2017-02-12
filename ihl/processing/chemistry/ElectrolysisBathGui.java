package ihl.processing.chemistry;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import ihl.utils.GuiMultiTextureButton;
import ihl.utils.IHLRenderUtils;

@SideOnly(Side.CLIENT)
public class ElectrolysisBathGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIElectrolysisBath.png");
	private ElectrolysisBathContainer container;
	private GuiMultiTextureButton button1;
	private int timer=10;

    public ElectrolysisBathGui (ElectrolysisBathContainer container1) {
            //the container is instanciated and passed to the superclass for handling
            super(container1);
            this.container=container1;
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
    {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        button1=new GuiMultiTextureButton(0, x+97, y+58, 18, 18, background,97,58,238,238);
        this.buttonList.add(button1);
    }
	
    @Override
	public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, button.id);
        if (button.id == 0)
        {
        	button1.isActive=true;
        	timer=10;
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int i1;
            if (this.container.tileEntity.progress > 0)
            {	    	
            	GL11.glEnable(GL11.GL_BLEND);
            	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(27),27);
            	this.drawTexturedModalRect(121, 33, getFrameX(i1), getFrameY(i1),24,24);
            }
            if (this.container.tileEntity.getTankAmount() > 0)
            {
            	IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.getFluidTank(), 32, 11, 89, 72, zLevel, par1, par2, xOffset, yOffset);
            }
            this.mc.renderEngine.bindTexture(background);
        	this.drawTexturedModalRect(60, 3, 252, 0,4,48);
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
            	button1.isActive=false;
            }
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