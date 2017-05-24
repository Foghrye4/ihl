package ihl.processing.chemistry;

import java.awt.event.KeyEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.ClientProxy;
import ihl.IHLMod;
import ihl.utils.IHLRenderUtils;
import ihl.utils.IHLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class DosingPumpGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIDosingPump.png");
	private DosingPumpContainer container;
	private GuiTextField setpointTextField;
	private int lastFluidAmountSetpoint = -1;
	private final static int TANK_HEIGHT = 58;

	public DosingPumpGui(DosingPumpContainer container1) {
		// the container is instanciated and passed to the superclass for
		// handling
		super(container1);
		this.container = container1;
		setpointTextField = new GuiTextField(Minecraft.getMinecraft().fontRenderer, 106, 34, 62, 16);
		setpointTextField.setText(Integer.toString(this.container.tileEntity.fluidAmountSetpoint));
		setpointTextField.setFocused(true);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		if(lastFluidAmountSetpoint!= this.container.tileEntity.fluidAmountSetpoint){
			setpointTextField.setText(Integer.toString(this.container.tileEntity.fluidAmountSetpoint));
			lastFluidAmountSetpoint= this.container.tileEntity.fluidAmountSetpoint;
		}
		int xOffset = (this.width - xSize) / 2;
		int yOffset = (this.height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(background);
		int i1;
		if (this.container.tileEntity.getEnergy() > 0D) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			i1 = Math.min(this.container.tileEntity.getGUIEnergy(12), 12);
			this.drawTexturedModalRect(12, 15 + 12 - i1, 179, 12 - i1, 14, i1 + 2);
		}
		i1 = TANK_HEIGHT - this.container.tileEntity.fluidAmountSetpoint * TANK_HEIGHT
				/ this.container.tileEntity.getFluidTank().getCapacity();
		this.drawTexturedModalRect(78, 6 + i1, 176, 14, 25, 7);
		if (this.container.tileEntity.getTankAmount() > 0) {
			IHLRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.getFluidTank(), 82, 10, 94,
					67, zLevel, par1, par2, xOffset, yOffset);
		}
   		setpointTextField.drawTextBox();
   		IHLRenderUtils.instance.drawTooltip(par1,par2,9,11,xOffset,yOffset,StatCollector.translateToLocal("ihl.dosingPump.tip"));
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

	@Override
	protected void keyTyped(char characterTyped, int keyIndex) {
		super.keyTyped(characterTyped, keyIndex);
		this.setpointTextField.textboxKeyTyped(characterTyped, keyIndex);
		// 28 - enter; 156 - numpad enter
		if (keyIndex == KeyEvent.VK_ACCEPT || keyIndex == KeyEvent.VK_ENTER || keyIndex == 28 || keyIndex == 156) {
			int fluidAmountSetpoint = (short) Math.max(1,
					Math.min(this.container.tileEntity.getFluidTank().getCapacity(),
							IHLUtils.parseIntSafe(this.setpointTextField.getText(), 100)));
			this.setpointTextField.setText(Integer.toString(fluidAmountSetpoint));
			this.setpointTextField.setFocused(false);
			((ClientProxy)IHLMod.proxy).sendIntegerFieldValueFromClientToServer(fluidAmountSetpoint, "fluidAmountSetpoint", this.container.tileEntity);
		}
	}
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		if (mouseX >= x + setpointTextField.xPosition && 
				mouseX <= x + setpointTextField.xPosition + setpointTextField.width && 
				mouseY >= y + setpointTextField.yPosition && 
				mouseY <= y + setpointTextField.yPosition + setpointTextField.height) {
			setpointTextField.setFocused(true);
		}
	}


}