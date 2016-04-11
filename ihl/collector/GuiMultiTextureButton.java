package ihl.collector;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiMultiTextureButton extends GuiButton {

    private ResourceLocation texture;
    private int textureX;
    private int textureY;
    private int textureActiveX;
    private int textureActiveY;
    public boolean isActive=false;
    
	public GuiMultiTextureButton(int id1, int x, int y, int w, int h,
			ResourceLocation texture1, int textureX1, int textureY1, int textureActiveX1, int textureActiveY1) {
        super(id1, x, y, w, h, "");
        texture=texture1;
        textureX=textureX1;
        textureY=textureY1;
        textureActiveX=textureActiveX1;
        textureActiveY=textureActiveY1;
        
	}
	
	@Override
   public void drawButton(Minecraft minecraft, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(this.texture);
        if(this.isActive)
        {
        	this.drawTexturedModalRect(this.xPosition, this.yPosition, this.textureActiveX, this.textureActiveY, this.width, this.height);
        }
        else
        {
            this.drawTexturedModalRect(this.xPosition, this.yPosition, this.textureX, this.textureY, this.width, this.height);
        }
    }

}
