package ihl.flexible_cable;

import java.awt.event.KeyEvent;

import org.lwjgl.opengl.GL11;

import ihl.IHLMod;
import ihl.interfaces.ItemMiniGUI;
import ihl.utils.IHLUtils;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class SetOfDiesMiniGUI extends ItemMiniGUI {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIIronWorkbench.png");
	private int transverseSectionValue;
	private GuiTextField transverseSectionTextField;
	private int xPos;
	private int yPos;

	public SetOfDiesMiniGUI(GuiContainer gui, Slot slot) {
		super(gui, slot);
		transverseSectionValue = slot.getStack().stackTagCompound.getInteger("transverseSection");
		xPos = this.slotBase.xDisplayPosition - 18;
		yPos = this.slotBase.yDisplayPosition + 18;
		transverseSectionTextField = new GuiTextField(this.guiBase.mc.fontRenderer, xPos + TEXT_BOX_POSX, yPos + TEXT_BOX_POSY, TEXT_BOX_WIDTH,
				11);
		transverseSectionTextField.setText(Float.toString(transverseSectionValue / 10f));
		transverseSectionTextField.setFocused(true);
	}

	@Override
	public void displayGUI() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		this.guiBase.mc.renderEngine.bindTexture(background);
		this.guiBase.drawTexturedModalRect(xPos, yPos, 0, 202, 126, HEIGHT);
		int runnerXPos = xPos + 5 + 114 * transverseSectionValue / 1350;
		this.guiBase.drawTexturedModalRect(runnerXPos, yPos + RUNNER_POSY, 126, 202, 3, 5);
		this.guiBase.mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("ihl.transversesection"),
				xPos + 3, yPos + 4, 0xFFCC00);
		this.transverseSectionTextField.drawTextBox();
		this.guiBase.mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("mm\u00B2"), xPos + UNITS_LABEL_POSX,
				yPos + 18, 0xFFCC00);
	}

	@Override
	public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (mouseX >= xPos + 5 && mouseX <= xPos + 119 && mouseY >= yPos + RUNNER_POSY - 1 && mouseY <= yPos + HEIGHT) {
			this.transverseSectionValue = Math.max(1, Math.min(1350, (mouseX - xPos - 5) * 1350 / 114));
			this.transverseSectionTextField.setText(Float.toString(transverseSectionValue / 10f));
		}
		if (mouseX >= xPos + TEXT_BOX_POSX && mouseX <= xPos + TEXT_BOX_POSX + TEXT_BOX_WIDTH && mouseY >= yPos + TEXT_BOX_POSY && mouseY <= yPos + TEXT_BOX_POSY + 11) {
			this.transverseSectionTextField.setFocused(true);
		}
		return mouseX >= xPos && mouseX <= xPos + 202 && mouseY >= yPos && mouseY <= yPos + HEIGHT;
	}

	@Override
	public boolean handleKeyTyped(char characterTyped, int keyIndex) {
		this.transverseSectionTextField.textboxKeyTyped(characterTyped, keyIndex);
		// 28 - enter; 156 - numpad enter
		if (keyIndex == KeyEvent.VK_ACCEPT || keyIndex == KeyEvent.VK_ENTER || keyIndex == 28 || keyIndex == 156) {
			this.transverseSectionValue = Math.max(1, Math.min(1350, (int) (IHLUtils
					.parseFloatSafe(this.transverseSectionTextField.getText(), this.transverseSectionValue) * 10f)));
			this.transverseSectionTextField.setText(Float.toString(transverseSectionValue / 10f));
			this.transverseSectionTextField.setFocused(false);
			return true;
		}
		return false;
	}

	@Override
	public void onGUIClosed() {
		IHLMod.proxy.sendItemStackNBTTagFromClientToServerPlayer(this.guiBase.mc.thePlayer, this.slotBase.slotNumber,
				"transverseSection", this.transverseSectionValue);
	}

}
