package ihl.interfaces;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

public abstract class ItemMiniGUI {
	protected final GuiContainer guiBase;
	protected final Slot slotBase;

	protected final int TEXT_BOX_POSX = 50;
	protected final int TEXT_BOX_POSY = 16;
	protected final int TEXT_BOX_WIDTH = 42;
	protected final int UNITS_LABEL_POSX = 105;
	protected final int HEIGHT = 39;
	protected final int RUNNER_POSY = 30;

	public ItemMiniGUI(GuiContainer gui, Slot slot) {
		guiBase = gui;
		slotBase = slot;
	}

	public abstract void displayGUI();

	public abstract boolean handleMouseClick(int mouseButton, int mouseX, int mouseY);

	public abstract boolean handleKeyTyped(char characterTyped, int keyIndex);

	public abstract void onGUIClosed();
}
