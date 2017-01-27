package ihl.guidebook;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.xml.sax.SAXException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import ihl.IHLMod;
import ihl.datanet.GuiInvisibleButton;
import ihl.utils.IHLRenderUtils;

public class IHLGuidebookGui extends GuiContainer
{
    public IHLGuidebookContainer container;
	private static final ResourceLocation background = new ResourceLocation("ihl", "textures/gui/GUIGuidebook.png");
	public Map<Integer, Integer[]> linksCoordinatesMap = new HashMap<Integer,Integer[]>();
	private String title;
	private String[] localisedContent=new String[2];;// By text block and row
	private ResourceLocation[] resourceLocationCache = new ResourceLocation[16];//By section number
	private int currentSection=0;
	private final int titleX=25;
	private final int titleY=7;
	private final int textBlockWidth=104;
	private final int textBlockX1=20;
	private final int textBlockY1=38;
	private final int textBlockX2=130;
	private final int textBlockY2=5;
	private final int textBlockMaxY=200;
	private int stringHeight=10;
	private int textRowInNextPage=0;
	private int textBlockInNextPage=0;
	private int[] textRowStart=new int[2];
	private int textBlockStart=0;
	private int pictureWidth;
	private int pictureHeight;
	private int maxSection=0;
	
	public IHLGuidebookGui(IHLGuidebookContainer container1) {
		super(container1);
        this.container = container1;
        this.xSize=this.container.xSize;
        this.ySize=this.container.ySize;
	}
	
    @Override
    public void initGui()
    {
    	super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        new GuiInvisibleButton(0, x, y, xSize/2, 162, linksCoordinatesMap, buttonList);
        new GuiInvisibleButton(1, x+xSize/2, y, xSize/2, 162, linksCoordinatesMap, buttonList);
        this.clear();
        try {
			IHLMod.xmlparser.setupGuidebookGUI(this, 0);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	@Override
    public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        switch(button.id)
        {
        	case 0:
        		currentSection--;
        		if(currentSection<0)
        		{
        			currentSection=this.maxSection;
        		}
    			this.textBlockStart=0;
    			this.textRowStart[0]=0;
    			this.textRowStart[1]=0;
    			break;
        	case 1:
        		if(textRowInNextPage==0 && textBlockInNextPage==0)
        		{
            		currentSection++;
        			this.textBlockStart=0;
        			this.textRowStart[0]=0;
        			this.textRowStart[1]=0;
        		}
        		else
        		{
        			this.textBlockStart=textBlockInNextPage;
        			this.textRowStart[textBlockStart]=textRowInNextPage;
        			textRowInNextPage=0;
        			textBlockInNextPage=0;
        		}
    			break;
        }
        this.clear();
        try {
			IHLMod.xmlparser.setupGuidebookGUI(this, currentSection);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public void clear()
	{
		for(int i=0;i<this.container.base.content.length;i++)
		{
			this.container.base.content[i]=null;
		}
		this.title=null;
		this.localisedContent[0]=null;
		this.localisedContent[1]=null;
	}
	
	public void setTitle(String title1) {
		this.title=StatCollector.translateToLocal(title1);
	}
	
	public void setPicture(String string,int width,int height)
	{
		IHLMod.log.debug("setting picture='"+string+"' \n height="+height+" \n width="+width);
		if(this.resourceLocationCache[this.currentSection]==null)
		{
			this.resourceLocationCache[this.currentSection]=new ResourceLocation("ihl",string);
		}
		this.pictureWidth=width;
		this.pictureHeight=height;
	}


	@Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		IHLRenderUtils.instance.updateScreenSize();
        this.drawPage(this.textBlockX1,this.textBlockY1);
    }
	
    private void drawPage(int startX, int startY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.fontRendererObj.drawString(this.title, this.titleX, this.titleY, 0);
        int xPos=startX;
        int yPos=startY;
        if(this.textBlockStart==0)
        {
        	if(this.localisedContent[0]!=null)
        	{
        		List<String> splittedText = IHLRenderUtils.instance.splitStringByWidth(this.localisedContent[0], Math.round(textBlockWidth));
        		for(int i1=this.textRowStart[0]; i1 < splittedText.size(); i1++)
        		{
        			yPos+=stringHeight;
        			if(yPos<this.textBlockMaxY)
        			{
        				this.fontRendererObj.drawString(splittedText.get(i1), Math.round(xPos), Math.round(yPos), 0);
        			}
        			else if(xPos!=this.textBlockX2)
        			{
        				yPos=this.textBlockY2;
        				xPos=this.textBlockX2;
        				this.fontRendererObj.drawString(splittedText.get(i1), Math.round(xPos), Math.round(yPos), 0);
        			}
        			else
					{
        				textRowInNextPage=i1;
        				textBlockInNextPage=0;
        				return;
					}
        		}
        	}
			if(this.resourceLocationCache[this.currentSection]!=null)
			{
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	if(yPos+this.pictureHeight<this.textBlockMaxY)
        		{
                	yPos+=stringHeight;
                	this.mc.renderEngine.bindTexture(this.resourceLocationCache[this.currentSection]);
                	this.drawTexturedModalRect(Math.round(xPos), Math.round(yPos), 0, 0, this.pictureWidth, this.pictureHeight);
                	yPos+=Math.round(this.pictureHeight);
        		}
        		else
        		{
        			yPos=this.textBlockY2;
        			xPos=this.textBlockX2;
                	this.mc.renderEngine.bindTexture(this.resourceLocationCache[this.currentSection]);
                	this.drawTexturedModalRect(Math.round(xPos), Math.round(yPos), 0, 0, this.pictureWidth, this.pictureHeight);
                	yPos+=Math.round(this.pictureHeight);
        		}
        	}
    	
        }
        if(this.localisedContent[1]!=null)
        {
			List<String> splittedText = IHLRenderUtils.instance.splitStringByWidth(this.localisedContent[1], Math.round(textBlockWidth));
            for(int i1=this.textRowStart[1]; i1 < splittedText.size(); i1++)
            {
            	yPos+=stringHeight;
            	if(yPos<this.textBlockMaxY)
            	{
                    this.fontRendererObj.drawString(splittedText.get(i1), Math.round(xPos), Math.round(yPos), 0);
            	}
				else if(xPos!=this.textBlockX2)
				{
					yPos=this.textBlockY2;
					xPos=this.textBlockX2;
                    this.fontRendererObj.drawString(splittedText.get(i1), Math.round(xPos), Math.round(yPos), 0);
				}
				else
				{
					textRowInNextPage=i1;
					textBlockInNextPage=1;
					return;
				}
            }
        }
        GL11.glScalef(1f, 1f, 1f);
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
            
        }

	public void setSectionNumber(int i) 
	{
		this.currentSection=0;
	}

	public void setMaxSectionNumber(int i) 
	{
		this.maxSection=Math.max(i,this.maxSection);
	}

	public int getMaxSectionNumber() 
	{
		return this.maxSection;
	}

	public void addItemStack(ItemStack itemStack) 
	{
		for(int i=0;i<this.container.base.content.length;i++)
		{
			if(this.container.base.content[i]==null)
			{
				this.container.base.content[i]=itemStack;
				break;
			}
		}
	}

	public void addTextBlock(String textContent) {
        for(int i=0; i < this.localisedContent.length; i++)
        {
        	if(this.localisedContent[i]==null)
        	{
            	this.localisedContent[i]=StatCollector.translateToLocal(textContent);
                break;
        	}
        }
	}
}