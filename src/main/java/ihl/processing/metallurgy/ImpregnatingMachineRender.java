package ihl.processing.metallurgy;

import org.lwjgl.opengl.GL11;

import ihl.interfaces.IFluidTankVisual;
import ihl.model.FluidRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ImpregnatingMachineRender extends TileEntitySpecialRenderer {

	private float cap;

	public ImpregnatingMachineRender(float capIn) {
		cap = capIn;
	}

	public void renderAModelAt(IFluidTankVisual tile, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		float rotation = 0F;
		GL11.glRotatef(-rotation, 0F, 1F, 0F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glTranslatef(0F, -1.0F, 0F);
		GL11.glPopMatrix();
		if (tile.getVisibleFluidId() == -1 || tile.getVisibleFluidAmount() <= 0) {
			return;
		}
		FluidStack fluidStack = new FluidStack(FluidRegistry.getFluid(tile.getVisibleFluidId()),
				tile.getVisibleFluidAmount());
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x, (float) y + 0.1F, (float) z);
		GL11.glScalef(0.99F, 0.8F * tile.getRenderLiquidLevel(), 0.99F);
		int[] displayLists = FluidRenderer.getLiquidDisplayLists(fluidStack);
		if (displayLists != null) {
			float level = Math.min(fluidStack.amount, cap) / cap;
			bindTexture(FluidRenderer.getFluidSheet(fluidStack));
			GL11.glCallList(displayLists[(int) (level * (FluidRenderer.DISPLAY_STAGES - 1))]);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopAttrib();
		GL11.glPopMatrix();

	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		this.renderAModelAt((IFluidTankVisual) tile, par2, par4, par6, par8);
	}
}