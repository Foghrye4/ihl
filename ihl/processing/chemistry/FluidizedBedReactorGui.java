package ihl.processing.chemistry;

import cpw.mods.fml.relauncher.Side;

import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ihl.utils.IHLRenderUtils;

@SideOnly(Side.CLIENT)
public class FluidizedBedReactorGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl",
			"textures/gui/GUIFluidizedBedReactor.png");
	private FluidizedBedReactorContainer container;
	private int mixerFrame = 0;

	public FluidizedBedReactorGui(FluidizedBedReactorContainer container1) {
		// the container is instanciated and passed to the superclass for
		// handling
		super(container1);
		this.container = container1;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		int xOffset = (this.width - xSize) / 2;
		int yOffset = (this.height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(background);
		int i1;
		if (this.container.tileEntity.getEnergy() > 0D) {
			i1 = Math.min(this.container.tileEntity.getGUIEnergy(12), 12);
			this.drawTexturedModalRect(12, 16 + 12 - i1, 179, 12 - i1, 14, i1 + 2);
		}
		if (this.container.tileEntity.progress > 0) {
			i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(17), 17);
			this.drawTexturedModalRect(58, 34, 198, 0, i1, 13);
			if (mixerFrame++ > 3) {
				mixerFrame = 0;
			}
			this.drawTexturedModalRect(126, 31, 244, 126 + 26 * mixerFrame, 12, 26);
		}
		if (this.container.tileEntity.getTankAmount() > 0) {
			IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.getFluidTank(), 126, 28, 138, 59,
					zLevel, par1, par2, xOffset, yOffset);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(background);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}