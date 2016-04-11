package ihl.datanet;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ic2.core.network.NetworkManager;
import ihl.collector.GuiMultiTextureButton;

@SideOnly(Side.CLIENT)
public class RedstoneSignalConverterGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIRedstoneSignalConverter.png");
	private RedstoneSignalConverterContainer container;
	private GuiInvisibleButton batterySlotPlus;
	private GuiInvisibleButton batterySlotMinus;
	private GuiInvisibleButton[] sensorsEmittersPlus = new GuiInvisibleButton[6];
	private GuiInvisibleButton[] sensorsEmittersMinus = new GuiInvisibleButton[6];
	private GuiInvisibleButton[][] cableContacts = new GuiInvisibleButton[4][8];
	public Map<Integer, Integer[]> linksCoordinatesMap = new HashMap<Integer,Integer[]>();
	private GuiMultiTextureButton removeLast;
	private GuiMultiTextureButton removeAll;
	private int timer=10;
    
    public RedstoneSignalConverterGui (RedstoneSignalConverterContainer container1) 
    {
            super(container1);
            this.container=container1;
            this.ySize=RedstoneSignalConverterContainer.height;
            this.xSize=RedstoneSignalConverterContainer.width;
    }
    
    @Override
    public void initGui()
    {
    	super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        int i,i1;
        batterySlotPlus = new GuiInvisibleButton(0, x+24, y+121, 3, 3, linksCoordinatesMap, buttonList);
        batterySlotMinus = new GuiInvisibleButton(1, x+24, y+124, 3, 3, linksCoordinatesMap, buttonList);
        for(i=0;i<sensorsEmittersPlus.length;i++)
        {
        	sensorsEmittersPlus[i]=new GuiInvisibleButton(i+2, x+24, y+13+18*i, 3, 3, linksCoordinatesMap, buttonList);
        }
        for(i=0;i<sensorsEmittersMinus.length;i++)
        {
        	sensorsEmittersMinus[i]=new GuiInvisibleButton(i+sensorsEmittersPlus.length+2, x+24, y+16+18*i, 3, 3, linksCoordinatesMap, buttonList);
        }
        for(i=0;i<cableContacts.length;i++)
        {
            for(i1=0;i1<cableContacts[i].length;i1++)
            {
            	cableContacts[i][i1]=new GuiInvisibleButton(i*8+i1+sensorsEmittersPlus.length+sensorsEmittersMinus.length+2, x+97, y+11+i1*3+i*25, 6, 3, linksCoordinatesMap, buttonList);
            }
        }
        removeLast = new GuiMultiTextureButton(256, x+171, y+4, 23, 17, background, 171, 4, 170, 3);//256, x+171, y+4, 23, 17, background, 171, 4, 170, 3
        removeAll = new GuiMultiTextureButton(257, x+171, y+22, 23, 17, background, 171, 22, 170, 21);
        this.buttonList.add(removeLast);
        this.buttonList.add(removeAll);
    }

    private void drawLink(int integer,int index) 
    {
    	Integer[] from = this.linksCoordinatesMap.get((integer>>8) & 255);
    	Integer[] to = this.linksCoordinatesMap.get(integer & 255);
    	this.drawLink(from[0], from[1], to[0], to[1], index);
	}

	@Override
    public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        IC2.network.get().initiateClientTileEntityEvent(this.container.tileEntity, button.id);
        if (button.id == removeLast.id)
        {
        	//removeLast.isActive=true;
        	timer=10;
        }
        if (button.id == removeAll.id)
        {
        	removeAll.isActive=true;
        	timer=10;
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
    		
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        	GL11.glEnable(GL11.GL_BLEND);
        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);            this.mc.renderEngine.bindTexture(background);
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
            for(int i = 0;i<this.container.tileEntity.links.size();i++)
            {
            	drawLink(this.container.tileEntity.links.get(i),i);
            }
            if(this.container.tileEntity.prevButtonPressed!=-1)
            {
            	Integer[] from = this.linksCoordinatesMap.get(this.container.tileEntity.prevButtonPressed);
            	this.drawLink(from[0], from[1], par2, par3, 20);
            }
            if (this.container.tileEntity.energy > 0)
            {
                int i1 = Math.min(this.container.tileEntity.getGUIEnergy(12),12);
                this.drawTexturedModalRect(x+184, y+119 + 12 - i1, 202, 12 - i1, 14, i1 + 2);
            }
            for(int i=0;i<this.container.tileEntity.cablesAmount;i++)
            {
               this.drawTexturedModalRect(x+98, y+12 + 25*i, 198, 13, 4, 25);
            }
            if(timer>0)
            {
            	timer--;
            }
            if(timer==1)
            {
            	//removeLast.isActive=false;
            	removeAll.isActive=false;
            }
    }
    
    private void drawLink(int fromX, int fromY, int toX, int toY, int index)
    {
    	int verticalLineOffset = 3*index+3;
        this.drawTexturedModalRect(Math.min(fromX+verticalLineOffset,toX), toY-1, 0, 253, Math.abs(fromX+verticalLineOffset-toX)+(toX<=fromX+verticalLineOffset?1:0), 3);
    	this.drawTexturedModalRect(fromX+verticalLineOffset-1, Math.min(fromY, toY), 253, 0, 3, Math.abs(fromY-toY)+(toY<fromY?1:0));
        this.drawTexturedModalRect(fromX, fromY-1, 0, 253, verticalLineOffset, 3);
    }
    
}