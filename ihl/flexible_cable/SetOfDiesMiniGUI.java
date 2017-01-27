package ihl.flexible_cable;

import java.awt.event.KeyEvent;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import ihl.IHLMod;
import ihl.interfaces.ItemMiniGUI;
import ihl.utils.IHLUtils;

public class SetOfDiesMiniGUI extends ItemMiniGUI
{
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIIronWorkbench.png");
	private int transverseSectionValue;
	private GuiTextField transverseSectionTextField;
	private int xPos;
	private int yPos;
	
	public SetOfDiesMiniGUI(GuiContainer gui, Slot slot) 
	{
		super(gui, slot);
		transverseSectionValue=slot.getStack().stackTagCompound.getInteger("transverseSection");
		xPos = this.slotBase.xDisplayPosition-18;
		yPos = this.slotBase.yDisplayPosition+18;
		transverseSectionTextField=new GuiTextField(this.guiBase.mc.fontRenderer,  xPos+78, yPos+3, 28, 11);
		transverseSectionTextField.setText(Float.toString(transverseSectionValue/10f));
		transverseSectionTextField.setFocused(true);
	}

	@Override
	public void displayGUI() 
	{
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.guiBase.mc.renderEngine.bindTexture(background);
		this.guiBase.drawTexturedModalRect(xPos, yPos, 0, 202, 126, 25);
		int runnerXPos=xPos+5+114*transverseSectionValue/1350;
		this.guiBase.drawTexturedModalRect(runnerXPos, yPos+18, 126, 202, 3, 5);
		this.guiBase.mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("ihl.transversesection"), xPos+3, yPos+4, 0xFFCC00);
		this.transverseSectionTextField.drawTextBox();
		this.guiBase.mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("mm\u00B2"), xPos+110, yPos+4, 0xFFCC00);
	}

	@Override
	public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) 
	{
		if(mouseX>=xPos+5 && mouseX<=xPos+119 && mouseY>=yPos+17 && mouseY<=yPos+24)
		{
			this.transverseSectionValue=Math.max(1, Math.min(1350,(mouseX-xPos-5)*1350/114));
			this.transverseSectionTextField.setText(Float.toString(transverseSectionValue/10f));
		}
		if(mouseX>=xPos+78 && mouseX<=xPos+78+28 && mouseY>=yPos+3 && mouseY<=yPos+3+11)
		{
			this.transverseSectionTextField.setFocused(true);
		}
		return mouseX>=xPos && mouseX<=xPos+202 && mouseY>=yPos && mouseY<=yPos+25;
	}
	
	@Override
    public boolean handleKeyTyped(char characterTyped, int keyIndex)
    {
        this.transverseSectionTextField.textboxKeyTyped(characterTyped, keyIndex);
        if(keyIndex==KeyEvent.VK_ACCEPT || keyIndex==KeyEvent.VK_ENTER || keyIndex==28 || keyIndex==156)//28  - enter;156 - numpad enter
        {
			this.transverseSectionValue=Math.max(1, Math.min(1350,(int)(IHLUtils.parseFloatSafe(this.transverseSectionTextField.getText(),this.transverseSectionValue)*10f)));
			this.transverseSectionTextField.setText(Float.toString(transverseSectionValue/10f));
			this.transverseSectionTextField.setFocused(false);
			return true;
        }
		return false;
    }

	@Override
	public void onGUIClosed() 
	{
		IHLMod.proxy.sendItemStackNBTTagFromClientToServerPlayer(this.guiBase.mc.thePlayer, this.slotBase.slotNumber, "transverseSection", this.transverseSectionValue);
	}

}
