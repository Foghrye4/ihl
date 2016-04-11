package ihl.flexible_cable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ic2.core.network.NetworkManager;
import ihl.interfaces.IItemHasMiniGUI;
import ihl.interfaces.ItemMiniGUI;
import ihl.utils.IHLRenderUtils;

@SideOnly(Side.CLIENT)
public class IronWorkbenchGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIIronWorkbench.png");
	private IronWorkbenchContainer container;
	private ItemMiniGUI miniGui;
	private static final short outputslotoffset=66;

    public IronWorkbenchGui (IronWorkbenchContainer container1) {
            //the container is instanciated and passed to the superclass for handling
            super(container1);
            this.container=container1;
            this.ySize=IronWorkbenchContainer.height;
    }
    
    @Override
	public void initGui()
    {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) 
    {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        for(int row = 1;row<6;row++)
        {
       		if(!this.container.tileEntity.workspaceElements.ready[row] && this.container.tileEntity.workspaceElements.get(row)!=null)
       		{
       			IHLRenderUtils.instance.drawWorkspaceElementTooltip(par1, par2, 8+x, 8+y+row*18, this.container.tileEntity.workspaceElements.get(row));
       		}
        }
		if(miniGui!=null)
		{
			miniGui.displayGUI();
		}
    }
    
    @Override
    protected void keyTyped(char characterTyped, int keyIndex)
    {
    	super.keyTyped(characterTyped, keyIndex);
		if(miniGui!=null && miniGui.handleKeyTyped(characterTyped, keyIndex))
		{
			miniGui.onGUIClosed();
			miniGui=null;
		}
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
		if(miniGui==null || !miniGui.handleMouseClick(mouseX-x, mouseY-y, mouseButton))
		{
			if(miniGui!=null)
			{
				miniGui.onGUIClosed();
				miniGui=null;
			}
	    	super.mouseClicked(mouseX, mouseY, mouseButton);
		}
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton)
    {
    	super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
		if(miniGui!=null)
		{
			miniGui.handleMouseClick(mouseX-x, mouseY-y, mouseButton);
		}
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(background);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
            if (this.container.tileEntity.progress > 0)
            {
                int progressLevel=Math.min(Math.round(this.container.tileEntity.progress*16.0F/this.container.tileEntity.maxProgress),16);
                int frameNum=0;
                short slotRecipeNum=this.container.tileEntity.output.slotRecipe[0];
                for(int row = 0;row<6;row++)
           	   	{
                   for (int col = 0; col<3; ++col)
                   {
             		   if(slotRecipeNum!=this.container.tileEntity.output.slotRecipe[col+row*3])
             		   {
             			 frameNum++;
             			 slotRecipeNum=this.container.tileEntity.output.slotRecipe[col+row*3];
             		   }
             		   if(this.container.tileEntity.currentSlot==slotRecipeNum)
             		   {
             			   int i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(18),18);
             			   this.drawTexturedModalRect(115+x+col*18, 7+y+row*18, 176, 72,i1,18);
             			   this.drawTexturedModalRect(115+x+col*18, 7+y+row*18, 176+getFrameX(frameNum), getFrameY(frameNum),18,18);
             		   }
             	   }
                }
            }
            else
            {
               int frameNum=0;
               short slotRecipeNum=this.container.tileEntity.output.slotRecipe[0];
           	   for(int row = 0;row<6;row++)
           	   {
                 for (int col = 0;col<3; ++col)
                 {
             		   if(slotRecipeNum!=this.container.tileEntity.output.slotRecipe[col+row*3])
             		   {
             			 frameNum++;
             			 slotRecipeNum=this.container.tileEntity.output.slotRecipe[col+row*3];
             		   }
             		   if(this.container.tileEntity.output.get(col+row*3)!=null)
             		   {
             			   this.drawTexturedModalRect(115+x+col*18, 7+y+row*18, 176+getFrameX(frameNum), getFrameY(frameNum),18,18);
             		   }
             	   }
                }
            }
           for(int row = 1;row<6;row++)
           {
       			   if(!this.container.tileEntity.workspaceElements.ready[row])
       			   {
       				   this.drawTexturedModalRect(20+x, 9+y+row*18, 194, 74,3,14);
      			   }
           }
    }
    
    @Override
	public void onGuiClosed()
    {
    	super.onGuiClosed();
    	this.container.tileEntity.isGuiScreenOpened=false;
		IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, 16);
    }
    
    @Override
    public void handleMouseClick(Slot slot,int slotNumber,int mouseButton/*0=left 1=right*/,int arg3)
    {
    	if(miniGui==null  && slot!=null && slot.slotNumber>=outputslotoffset && slot.slotNumber<outputslotoffset+18)
    	{
    		if(mouseButton==0)
    		{
        		IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, slot.slotNumber-outputslotoffset);
    		}
    		else if(mouseButton==1)
    		{
    			if(slot.getHasStack() && slot.getStack().getItem() instanceof IItemHasMiniGUI)
    			{
    				if(miniGui!=null)
    				{
    					miniGui.onGUIClosed();
    				}
    				miniGui = ((IItemHasMiniGUI)slot.getStack().getItem()).getMiniGUI(this, slot);
    			}
    		}
    	}
    	super.handleMouseClick(slot, slotNumber, mouseButton, arg3);
    }
    
    private int getFrameX(int number)
    {
    	return (number % 4) * 18;
    }
    private int getFrameY(int number)
    {
    	return ((number>>2) & 3)*18;
    }

}