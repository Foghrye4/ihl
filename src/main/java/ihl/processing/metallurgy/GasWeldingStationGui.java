package ihl.processing.metallurgy;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ihl.utils.IHLRenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GasWeldingStationGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIGasWeldingStation.png");
	private GasWeldingStationContainer container;
	private int outputslotoffset=45;

    public GasWeldingStationGui (GasWeldingStationContainer detonationSprayingMachineContainer) {
            //the container is instanciated and passed to the superclass for handling
            super(detonationSprayingMachineContainer);
            this.container=detonationSprayingMachineContainer;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        int xOffset = (this.width - xSize) / 2;
        int yOffset = (this.height - ySize) / 2;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int i1;
            if (this.container.tileEntity.progress2 > 0)
            {
            	i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(27),27);
            	this.drawTexturedModalRect(90, 30, getFrameX(i1), getFrameY(i1),24,24);
            }
            if (this.container.tileEntity.flammableGasTank.getFluid()!=null && this.container.tileEntity.flammableGasTank.getFluidAmount() > 0)
            {
            	IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.flammableGasTank, 64, 28, 76, 67, zLevel, par1, par2, xOffset, yOffset);
            }
            if (this.container.tileEntity.oxygenTank.getFluid()!=null && this.container.tileEntity.oxygenTank.getFluidAmount() > 0)
            {
            	IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.oxygenTank, 28, 28, 40, 67, zLevel, par1, par2, xOffset, yOffset);
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
    
    @Override
    public void handleMouseClick(Slot slot,int arg1,int arg2,int arg3)
    {
    	if(slot!=null && slot.slotNumber>=outputslotoffset && slot.slotNumber<outputslotoffset+8)
    	{
    		IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, slot.slotNumber-outputslotoffset+1);
    	}
       	super.handleMouseClick(slot, arg1, arg2, arg3);
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