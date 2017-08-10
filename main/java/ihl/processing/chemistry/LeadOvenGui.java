package ihl.processing.chemistry;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.utils.IHLRenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class LeadOvenGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUILeadOven.png");
	private LeadOvenContainer container;

	public LeadOvenGui(LeadOvenContainer container1) {
		// the container is instanciated and passed to the superclass for
		// handling
		super(container1);
		this.container = container1;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(background);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		if (this.container.tileEntity.inputTank.getFluidAmount() > 0) {
			IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.inputTank, 30, 19, 42, 66, zLevel,
					par1, par2, x, y);
		}
		if (this.container.tileEntity.outputTank.getFluidAmount() > 0) {
			IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.outputTank, 133, 19, 145, 66, zLevel,
					par1, par2, x, y);
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
		int i1;
		if (this.container.tileEntity.fuel > 0) {
			i1 = this.container.tileEntity.gaugeFuelScaled(12);
			this.drawTexturedModalRect(x + 56, y + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
		}
		i1 = this.container.tileEntity.gaugeProgressScaled(24);
		this.drawTexturedModalRect(x + 79, y + 34, 176, 14, i1 + 1, 16);
	}
}