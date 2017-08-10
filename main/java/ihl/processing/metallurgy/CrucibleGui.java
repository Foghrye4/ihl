package ihl.processing.metallurgy;

import org.lwjgl.opengl.GL11;

import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class CrucibleGui extends GuiContainer
{
    public CrucibleContainer container;
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUICrucible.png");
	private static final String title = StatCollector.translateToLocal("item.crucible.name");
	
    public CrucibleGui(CrucibleContainer container1)
    {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(title, 68, 0, 6171880);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.mc.renderEngine.bindTexture(background);
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
            if(this.getItemStack().getItemDamage()==1)
            {
                this.drawTexturedModalRect(x+67, y+26, 198, 52, 58, 39);
            }
        	FluidStack fluidStack = this.getItemInstance().getFluid(this.getItemStack()); 
            if (fluidStack!=null && fluidStack.amount > 0)
            {
           		Fluid fluid = fluidStack.getFluid();
            		if(fluid!=null)
            		{
            			IIcon fluidIcon = fluid.getIcon();
            			if (fluidIcon != null)
            			{
            				this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            				int liquidHeight = fluidStack.amount*20/getItemInstance().capacity;
            				DrawUtil.drawRepeated(fluidIcon, x+71, y+ 41 + 20 - liquidHeight, 35.0D, liquidHeight, this.zLevel);
            			}
            		}
                    String tooltip = StatCollector.translateToLocal(fluidStack.getFluid().getName()) + ": " + fluidStack.amount + "mB";
                  GuiTooltipHelper.drawAreaTooltip(par2, par3, tooltip, x-16, y+10, x+16, y+29);
            }
            this.mc.renderEngine.bindTexture(background);
            if(this.getItemStack().getItemDamage()==1)
            {
                this.drawTexturedModalRect(x+68, y+40, 198, 26, 58, 26);
            }
            else
            {
                this.drawTexturedModalRect(x+68, y+40, 198, 0, 58, 26);
            }
    }
    
    private Crucible getItemInstance()
    {
    	return ((Crucible)this.container.box.thisItemStack.getItem());
    }
    
    private ItemStack getItemStack()
    {
    	return this.container.box.thisItemStack;
    }
}