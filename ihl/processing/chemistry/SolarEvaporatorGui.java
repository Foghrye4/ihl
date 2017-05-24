package ihl.processing.chemistry;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(Side.CLIENT)
public class SolarEvaporatorGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl",
			"textures/gui/GUISolarEvaporator.png");
	private SolarEvaporatorContainer container;

	public SolarEvaporatorGui(SolarEvaporatorContainer container1) {
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
		if (this.container.tileEntity.getActive()) {
			this.drawTexturedModalRect(9, 16, 176, 0, 14, 14);
		}
		if (this.container.tileEntity.progress > 0) {
			i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(18), 18);
			this.drawTexturedModalRect(99, 34, 198, 0, i1 + 1, 13);
		}

		if (this.container.tileEntity.getTankAmount() > 0) {
			FluidStack fluidStack = this.container.tileEntity.getFluidTank().getFluid();
			if (fluidStack != null) {
				Fluid fluid = fluidStack.getFluid();
				if (fluid != null) {

					IIcon fluidIcon = fluid.getIcon();

					if (fluidIcon != null) {
						this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
						int liquidHeight = this.container.tileEntity.gaugeLiquidScaled(47);
						DrawUtil.drawRepeated(fluidIcon, (82), 16 + 47 - liquidHeight, 12.0D, liquidHeight,
								this.zLevel);
						this.mc.renderEngine.bindTexture(background);
					}
				}
				String tooltip = StatCollector.translateToLocal(fluidStack.getFluid().getUnlocalizedName()) + ": "
						+ fluidStack.amount + "mB";
				GuiTooltipHelper.drawAreaTooltip(par1 - 90, par2 - 32, tooltip, xOffset - 8, yOffset - 15, xOffset + 2,
						yOffset + 30);
			}
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