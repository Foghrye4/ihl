package ihl.utils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.core.util.DrawUtil;
import ihl.IHLMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class IHLRenderUtils 
{
   private FloatBuffer colorBuffer;
   private Map<Long, Integer> frameTooltipMap;
   private int displayScaledWidth=-1;
   private int displayScaledHeight=-1;
   private int guiXPos=-1;
   private int guiYPos=-1;
   private int prevDisplayWidth=-1;
   private int prevDisplayHeight=-1;
   private final int guiContainerWidth = 166;
   private final int guiContainerHeight = 176;
   private float lastPlayerYaw;
   private float lastPlayerPitch;
   public double renderMinX=0d;
   public double renderMaxX=1d;
   public double renderMinY=0d;
   public double renderMaxY=1d;
   public double renderMinZ=0d;
   public double renderMaxZ=1d;
   public boolean renderFromInside=false;
   private float rotationPointX;
   private float rotationPointY;
   private float rotationPointZ;
   private double renderPositionX;
   private double renderPositionY;
   private double renderPositionZ;
   private float rotationX;
   private float rotationY;
   private float rotationZ;
   private float scale=1/16f;
   public boolean swapXandZ=false;
   public boolean swapXandY=false;
   public boolean swapYandZ=false;
   public boolean swapRenderBoundsX=false;
   public boolean swapRenderBoundsY=false;
   public boolean swapRenderBoundsZ=false;
   public int scaleFactor=1;
   public static IHLRenderUtils instance;
   
   public IHLRenderUtils()
   {
	   instance=this;
	   colorBuffer = GLAllocation.createDirectFloatBuffer(16);
	   frameTooltipMap = new HashMap<Long, Integer>();
   }
   
   public  void renderIHLFluidTank(IHLFluidTank fluidTank, int x1, int y1, int x2, int y2, float zLevel, int par1, int par2, int xOffset, int yOffset)
   {
      	int liquidHeight = 0;
       	int prevLiquidHeight = 0;
       	int i = y2-y1;
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1f, 1f, 1f, 1f);
       	for(int i2 = 0;i2<fluidTank.getNumberOfFluids();i2++)
       	{
       		FluidStack fluidStack = fluidTank.getFluid(i2); 
       		if(fluidStack!=null)
       		{
       			Fluid fluid = fluidStack.getFluid();
       			if(fluid!=null)
       			{
       				IIcon fluidIcon = fluid.getIcon();
       				if (fluidIcon != null)
       				{
       					liquidHeight = fluidTank.getFluidAmount(i2) * i /fluidTank.getCapacity();
       					DrawUtil.drawRepeated(fluidIcon, x1, y2 - liquidHeight-prevLiquidHeight, x2-x1, liquidHeight, zLevel);
       					prevLiquidHeight+=liquidHeight;
       				}
       			}
       		}
       	}
       	drawIHLFluidTankTooltip(par1, par2, x1+guiXPos-6, y1+guiYPos+6, x2+guiXPos-6, y2+guiYPos+6, fluidTank);
	   }
	   
	   public  void drawMissingEngineTooltip(GuiContainer gui, int par1, int par2, int xPos, int yPos, int xOffset, int yOffset)
	   {
       		gui.drawTexturedModalRect(xPos, yPos, 194, 0, 3, 14);
       		drawTooltip(par1,par2,3,14,xPos+xOffset,yPos+yOffset,StatCollector.translateToLocal("ihl.gui.missing.engine"));
	   }
	   
	   public  void drawWorkspaceElementTooltip(int par1, int par2, int xPos, int yPos, ItemStack workSpaceElement)
	   {
       		drawTooltip(par1,par2,16,16,xPos,yPos,StatCollector.translateToLocal(workSpaceElement.getUnlocalizedName()+".tooltip"));
	   }
	   
	   public  void drawIHLFluidTankTooltip(int par1, int par2, int x1, int y1, int x2, int y2, IHLFluidTank fluidTank)
	   {
		   String fluidListNames="";
		   List<FluidStack> fli = fluidTank.getFluidList();
		   for(int i=fli.size()-1;i>=0;i--)
		   {
			   FluidStack fluidStack = fli.get(i);
			   fluidListNames+=StatCollector.translateToLocal(fluidStack.getUnlocalizedName())+": "+fluidStack.amount+"mB /n ";
		   }
       		drawTooltip(par1,par2,x2-x1,y2-y1,x1,y1,fluidListNames);
	   }
	   
	   public  boolean drawTooltip(int cursorPosX, int cursorPosY, int width, int height, int xPos, int yPos, String text)
	   {
		   updateScreenSize();
		   long key = xPos+yPos*1024;
		   Integer frame=0;
		   if(frameTooltipMap.containsKey(key))
		   {
			   frame=frameTooltipMap.get(key);
		   }
		   boolean showString=true;
		   if(cursorPosX<xPos || cursorPosX>xPos+width||
			   cursorPosY<yPos || cursorPosY>yPos+height)
		   {
			   if(frame>0)
			   {
				   frame-=20;
				   frameTooltipMap.put(key, frame);
			   }
			   showString=false;
		   }
		   else
		   {
			   frame+=10;
			   frameTooltipMap.put(key, frame);
		   }
		   if(frame>0)
		   {
			   	int strokeHeight=15;
			   	int i,x1,x2,y1,y2,tooltipWidth,tooltipHeight;
			   	tooltipWidth=tooltipHeight=0;
			   	String[] splittedText = text.split(" /n ");
			   	for(i=0;i<splittedText.length;i++)
			   	{
			   		if(Minecraft.getMinecraft().fontRenderer.getStringWidth(splittedText[i])+8>tooltipWidth)
			   		{
			   			tooltipWidth=Math.min(frame,Minecraft.getMinecraft().fontRenderer.getStringWidth(splittedText[i])+8);
			   		}
			   	}
			   	tooltipHeight=Math.min(Math.max(frame-tooltipWidth,strokeHeight),strokeHeight*splittedText.length);
			   	x1=cursorPosX-xPos;
			   	x2=x1+tooltipWidth;
			   	y1=cursorPosY-guiYPos+18;
			   	y2=y1+tooltipHeight;
		        GL11.glPushAttrib(16704);
		        GL11.glDisable(GL11.GL_DEPTH_TEST);
		        GL11.glDisable(GL11.GL_TEXTURE_2D);
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		        if(showString)
		        {
				   	drawRectangle(Tessellator.instance, x1,y1,x2,y2,128);
			        GL11.glEnable(GL11.GL_TEXTURE_2D);
		        	for(i=0;i<splittedText.length;i++)
		        	{
		        		if(i<tooltipHeight/strokeHeight)
		        		{
		        			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(splittedText[i], x1+4, y1+i*strokeHeight+4, 16768125);
		        		}
		        	}
		        }
		        else
		        {
				   	drawRectangle(Tessellator.instance, x1,y1,x2,y2,Math.min(128,frame/2));
			        GL11.glEnable(GL11.GL_TEXTURE_2D);
		        }
		        GL11.glPopAttrib();
		        frame=Math.min(tooltipWidth+tooltipHeight,frame);
		        frameTooltipMap.put(key, frame);
		        return true;
		   }
		   return false;
	   }
	   
	    public  void enableAmbientLighting()
	    {
	        GL11.glDisable(GL11.GL_LIGHT0);
	        GL11.glDisable(GL11.GL_LIGHT1);
	        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(1.0f, 1.0f, 1.0f, 1.0F));
	    }
	    
	    public  void disableAmbientLighting()
	    {
	        GL11.glEnable(GL11.GL_LIGHT0);
	        GL11.glEnable(GL11.GL_LIGHT1);
	        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(0.0f, 0.0f, 0.0f, 1.0F));
	    }

	/**
	 * Update and return colorBuffer with the RGBA values passed as arguments
	 */
	private  FloatBuffer setColorBuffer(float par0, float par1, float par2, float par3)
	{
	    colorBuffer.clear();
	    colorBuffer.put(par0).put(par1).put(par2).put(par3);
	    colorBuffer.flip();
	    return colorBuffer;
	}
	
    private  void drawRectangle(Tessellator tessellator, int x1, int y1, int x2, int y2, int color)
    {
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(color >>> 24 & 255, color >>> 16 & 255, color >>> 8 & 255, color & 255);
        tessellator.addVertex(x2, y1, 300.0D);
        tessellator.addVertex(x1, y1, 300.0D);
        tessellator.addVertex(x1, y2, 300.0D);
        tessellator.addVertex(x2, y2, 300.0D);
        tessellator.draw();
    }
    
    public  void drawThermometerTemperature(long temperature, boolean show)
    {
    	updateScreenSize();
    	long key = 0;
    	int frame=0;
	   if(frameTooltipMap.containsKey(key))
	   {
		   frame=frameTooltipMap.get(key);
	   }
	   if(frame<=0)
	   {
		   updatePlayerView();
	   }
	   boolean showString=true;
	   if(show && frame<=122)
	   {
		   frame+=1;
		   frameTooltipMap.put(key, frame);
	   }
	   else
	   {
			if(frame>0)
		   {
			   frame-=2;
			   frameTooltipMap.put(key, frame);	
		   }
		   showString=false;
	   }
	   if(frame>0)
	   {
		   int width=Math.min(frame, 122);
		   int height=Math.min(frame, 48);
		   int xShift = Math.round(displayScaledWidth/100*(lastPlayerYaw - Minecraft.getMinecraft().renderViewEntity.prevRotationYaw));
		   int yShift = Math.round(displayScaledHeight/64*(lastPlayerPitch - Minecraft.getMinecraft().renderViewEntity.prevRotationPitch));
		   int xPos = displayScaledWidth/2+xShift;
		   int yPos = displayScaledHeight/2-48+yShift;
	       GL11.glEnable(GL11.GL_BLEND);
	       GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		   drawTexturedModalRect(xPos,yPos,0,0,width,height);
	       GL11.glDisable(GL11.GL_DEPTH_TEST);
		   if(showString)
		   {
			   Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(temperature+"\u00B0K", xPos+35, yPos+3, 16768125);
		   }
		   GL11.glEnable(GL11.GL_DEPTH_TEST);
		   GL11.glDisable(GL11.GL_BLEND);
	   }
    }    
    
    public void drawTexturedModalRect(int x1, int y1, int u, int v, int x2, int y2) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1f, 1f, 1f, 0.5f);
        tessellator.addVertexWithUV(x1 + 0, y1 + y2, 300D, (u + 0) * f, (v + y2) * f1);
        tessellator.addVertexWithUV(x1 + x2, y1 + y2, 300D, (u + x2) * f, (v + y2) * f1);
        tessellator.addVertexWithUV(x1 + x2, y1 + 0, 300D, (u + x2) * f, (v + 0) * f1);
        tessellator.addVertexWithUV(x1 + 0, y1 + 0, 300D, (u + 0) * f, (v + 0) * f1);
        tessellator.draw();
    }

    
    public void updateScreenSize()
    {
        if(prevDisplayHeight!=Minecraft.getMinecraft().displayHeight || prevDisplayWidth!=Minecraft.getMinecraft().displayWidth)
        {
			   	ScaledResolution var2 = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
	            displayScaledWidth = var2.getScaledWidth();
	            displayScaledHeight = var2.getScaledHeight();
	            scaleFactor=var2.getScaleFactor();
	            guiXPos = (displayScaledWidth - guiContainerWidth)/2;
	            guiYPos = (displayScaledHeight - guiContainerHeight)/2;
	            prevDisplayWidth=Minecraft.getMinecraft().displayWidth;
	            prevDisplayHeight=Minecraft.getMinecraft().displayHeight;
        }
    }
    
    public  void updatePlayerView()
    {
    	lastPlayerYaw = Minecraft.getMinecraft().renderViewEntity.prevRotationYaw;
    	lastPlayerPitch = Minecraft.getMinecraft().renderViewEntity.prevRotationPitch;
    }
    
    public void drawKnee(double xPos, double yPos, double zPos, ForgeDirection direction12, ForgeDirection direction22, double radius1, double radius2, IIcon icon)
    {	
    	ForgeDirection direction1=direction12;
    	ForgeDirection direction2=direction22;
    	if(this.swapRenderBoundsX)
    	{
    		if(direction1.offsetX!=0)
    		{
    			direction1=direction1.getOpposite();
    		}
    		if(direction2.offsetX!=0)
    		{
    			direction2=direction2.getOpposite();
    		}
    	}
    	if(this.swapRenderBoundsY)
    	{
    		if(direction1.offsetY!=0)
    		{
    			direction1=direction1.getOpposite();
    		}
    		if(direction2.offsetY!=0)
    		{
    			direction2=direction2.getOpposite();
    		}
    	}
    	if(this.swapRenderBoundsZ)
    	{
    		if(direction1.offsetZ!=0)
    		{
    			direction1=direction1.getOpposite();
    		}
    		if(direction2.offsetZ!=0)
    		{
    			direction2=direction2.getOpposite();
    		}
    	}
    	double[][] outervertexes1 = new double[8][3];
    	double[][] innervertexes1 = new double[8][3];
    	double[][] outervertexes2 = new double[8][3];
    	double[][] innervertexes2 = new double[8][3];
        int i=0;
        for(i=0;i<8;i++)
        {
        	if(direction1.equals(direction2))
        	{
            	switch(direction1)
            	{
            		case UP:
        	        	outervertexes1[i] = new double[] {0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	innervertexes1[i] = new double[] {0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	outervertexes2[i] = new double[] {0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	innervertexes2[i] = new double[] {0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	break;
            		case DOWN:
        	        	outervertexes1[i] = new double[] {0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	innervertexes1[i] = new double[] {0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.5F*radius1*radius2*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	outervertexes2[i] = new double[] {0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	innervertexes2[i] = new double[] {0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	break;
            		case SOUTH:
        	        	outervertexes1[i] = new double[] {0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i), 1d};
        	        	innervertexes1[i] = new double[] {0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 1d};
        	        	outervertexes2[i] = new double[] {0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i), 0d};
        	        	innervertexes2[i] = new double[] {0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*radius1*radius2*(float)Math.sin(2D*Math.PI/8D*i), 0d};
        	        	break;
            		case NORTH:
        	        	outervertexes1[i] = new double[] {0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i), 1d};
        	        	innervertexes1[i] = new double[] {0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*radius1*radius2*(float)Math.sin(2D*Math.PI/8D*i), 1d};
        	        	outervertexes2[i] = new double[] {0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i), 0d};
        	        	innervertexes2[i] = new double[] {0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0d};
        	        	break;
            		case EAST:
        	        	outervertexes1[i] = new double[] {0d, 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	innervertexes1[i] = new double[] {0d, 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	outervertexes2[i] = new double[] {1d, 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	innervertexes2[i] = new double[] {1d, 0.5F+0.5F*radius1*radius2*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	break;
            		case WEST:
        	        	outervertexes1[i] = new double[] {0d, 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	innervertexes1[i] = new double[] {0d, 0.5F+0.5F*radius2*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+radius1*0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	outervertexes2[i] = new double[] {1d, 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	innervertexes2[i] = new double[] {1d, 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i)};
        	        	break;
            		default:
        	        	outervertexes1[i] = new double[] {0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	innervertexes1[i] = new double[] {0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	outervertexes2[i] = new double[] {0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	innervertexes2[i] = new double[] {0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
        	        	break;
            	}
        		
        	}
        	else
        	{
	        	switch(direction1)
	        	{
	        		case UP:
	    	        	outervertexes1[i] = new double[] {0.5F+0.25F*radius2*(float)Math.cos(2D*Math.PI/8D*i+Math.PI), 1d, 0.5F-0.25F*radius2*(float)Math.sin(2D*Math.PI/8D*i+Math.PI)};
	    	        	innervertexes1[i] = new double[] {0.5F+radius1*radius2*0.25F*(float)Math.cos(2D*Math.PI/8D*i+Math.PI), 1d, 0.5F-0.25F*radius1*radius2*(float)Math.sin(2D*Math.PI/8D*i+Math.PI)};
	    	        	break;
	        		case DOWN:
	        			outervertexes1[i] = new double[] {0.5F+0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.25F*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	innervertexes1[i] = new double[] {0.5F+radius1*0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0d, 0.5F+0.25F*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	break;
	        		case SOUTH:
	    	        	outervertexes1[i] = new double[] {0.5F+0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*(float)Math.sin(2D*Math.PI/8D*i), 1d};
	    	        	innervertexes1[i] = new double[] {0.5F+radius1*0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 1d};
	    	        	break;
	        		case NORTH:
	    	        	outervertexes1[i] = new double[] {0.5F+0.25F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*radius2*(float)Math.sin(2D*Math.PI/8D*i), 1d};
	    	        	innervertexes1[i] = new double[] {0.5F+radius1*radius2*0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*radius1*radius2*(float)Math.sin(2D*Math.PI/8D*i), 1d};
	    	        	break;
	        		case EAST:
	    	        	outervertexes1[i] = new double[] {0d, 0.5F+0.25F*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+0.25F*(float)Math.cos(2D*Math.PI/8D*i)};
	    	        	innervertexes1[i] = new double[] {0d, 0.5F+0.25F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+radius1*0.25F*(float)Math.cos(2D*Math.PI/8D*i)};
	    	        	break;
	        		case WEST:
	    	        	outervertexes1[i] = new double[] {0d, 0.5F+0.25F*radius2*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+0.52F*radius2*(float)Math.cos(2D*Math.PI/8D*i)};
	    	        	innervertexes1[i] = new double[] {0d, 0.5F+0.25F*radius2*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+radius1*0.25F*radius2*(float)Math.cos(2D*Math.PI/8D*i)};
	    	        	break;
	        		default:
	    	        	outervertexes1[i] = new double[] {0d, 0.5F+0.25F*radius2*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+0.52F*radius2*(float)Math.cos(2D*Math.PI/8D*i)};
	    	        	innervertexes1[i] = new double[] {0d, 0.5F+0.25F*radius2*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0.5F+radius1*0.25F*radius2*(float)Math.cos(2D*Math.PI/8D*i)};
	    	        	break;
	        	}
	        	
	        	switch(direction2)
	        	{
	        		case UP:
	    	        	outervertexes2[i] = new double[] {0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	innervertexes2[i] = new double[] {0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	break;
	        		case DOWN:
	    	        	outervertexes2[i] = new double[] {0.5F+0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	innervertexes2[i] = new double[] {0.5F+radius1*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	break;
	        		case WEST:
	    	        	outervertexes2[i] = new double[] {1d, 0.5F+0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F-0.25F*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	innervertexes2[i] = new double[] {1d, 0.5F+radius1*0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F-0.25F*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	break;
	        		case EAST:
	    	        	outervertexes2[i] = new double[] {0d, 0.5F+0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	innervertexes2[i] = new double[] {0d, 0.5F+radius1*0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	break;
	        		case NORTH:
	    	        	outervertexes2[i] = new double[] {0.5F+0.25F*(float)Math.cos(2D*Math.PI/8D*i), 1d-0.5F-0.25F*(float)Math.sin(2D*Math.PI/8D*i), 1d};
	    	        	innervertexes2[i] = new double[] {0.5F+radius1*0.25F*(float)Math.cos(2D*Math.PI/8D*i), 1d-0.5F-0.25F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 1d};
	    	        	break;
	        		case SOUTH:
	    	        	outervertexes2[i] = new double[] {0.5F+0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*(float)Math.sin(2D*Math.PI/8D*i), 0d};
	    	        	innervertexes2[i] = new double[] {0.5F+radius1*0.25F*(float)Math.cos(2D*Math.PI/8D*i), 0.5F+0.25F*radius1*(float)Math.sin(2D*Math.PI/8D*i), 0d};
	    	        	break;
	        		default:
	    	        	outervertexes2[i] = new double[] {0.5F+0.5F*radius2*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	innervertexes2[i] = new double[] {0.5F+radius1*radius2*0.5F*(float)Math.cos(2D*Math.PI/8D*i), 1d, 0.5F+0.5F*radius2*radius1*(float)Math.sin(2D*Math.PI/8D*i)};
	    	        	break;
	        	}

        	}
        }
    	double[][][] quadList = new double[32][4][3];
        for(i=0;i<32;i++)
        {
			if(i<7)
        	{
	        	quadList[i]=new double[][] {outervertexes1[i],outervertexes1[i+1],innervertexes1[i+1],innervertexes1[i]};
        	}
        	else if(i==7)
        	{
	        	quadList[i]=new double[][] {outervertexes1[i],outervertexes1[0],innervertexes1[0],innervertexes1[i]};
        	}
        	else if(i<15)
        	{
	        	quadList[i]=new double[][] {innervertexes2[i-8],innervertexes2[i+1-8],outervertexes2[i+1-8],outervertexes2[i-8]};
        	}
        	else if(i==15)
        	{
	        	quadList[i]=new double[][] {innervertexes2[i-8],innervertexes2[0],outervertexes2[0],outervertexes2[i-8]};
        	}
        	else if(i<23)
        	{
	        	quadList[i]=new double[][] {outervertexes1[i-16],outervertexes2[i-16],outervertexes2[i-16+1],outervertexes1[i-16+1]};
        	}
        	else if(i==23)
        	{
	        	quadList[i]=new double[][] {outervertexes1[i-16],outervertexes2[i-16],outervertexes2[0],outervertexes1[0]};
        	}
        	else if(i<31)
        	{
	        	quadList[i]=new double[][] {innervertexes1[i-24+1],innervertexes2[i-24+1], innervertexes2[i-24], innervertexes1[i-24]};
        	}
        	else if(i==31)
        	{
	        	quadList[i]=new double[][] {innervertexes1[0],innervertexes2[0],innervertexes2[i-24],innervertexes1[i-24]};
        	}
        }
        for(i=0;i<32;i++)
        {
			this.drawSquare(xPos, yPos, zPos, quadList[i], icon);
        }

    	
    }
    public void drawPipe(double xPos, double yPos, double zPos, ForgeDirection direction1, double radius1, double radius2, IIcon icon)
    {	
    	this.drawKnee(xPos, yPos, zPos, direction1, direction1, radius1, radius2, icon);
    }
    
    public void drawSquare(double xPos, double yPos, double zPos, double[][] vertexes, IIcon icon)
    {
        double u1 = icon.getInterpolatedU(this.renderMinZ * 16.0D);
        double u2 = icon.getInterpolatedU(this.renderMaxZ * 16.0D);
        double v2 = icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
        double v1 = icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
        {
            u1 = icon.getMinU();
            u2 = icon.getMaxU();
        }
        if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
        {
            v2 = icon.getMinV();
            v1 = icon.getMaxV();
        }
        this.drawSquare(xPos, yPos, zPos, u1, u2, v1, v2, vertexes);
    }
    

    public void drawSquare(double xPos, double yPos, double zPos, double u1, double u2, double v1, double v2, double[][] vertexes)
    {
        Tessellator var9 = Tessellator.instance;
        double[] us = new double[]{u1,u1,u2,u2};
        double[] vs = new double[]{v1,v2,v2,v1};
        double xDelta=this.renderMaxX-this.renderMinX;
        double yDelta=this.renderMaxY-this.renderMinY;
        double zDelta=this.renderMaxZ-this.renderMinZ;
        int startFrom=0;
        int endTo=vertexes.length-1;
        if(this.renderFromInside)
        {
        	startFrom=vertexes.length-1;
            endTo=0;
        }
        for(int i=startFrom;(i<=endTo&&!this.renderFromInside)||(i>=endTo&&this.renderFromInside);i+=(endTo-startFrom)/3)
        {
        	double vX=vertexes[i][0]*xDelta+this.renderMinX;
        	double vY=vertexes[i][1]*yDelta+this.renderMinY;
        	double vZ=vertexes[i][2]*zDelta+this.renderMinZ;
        	double[] vYZ=null;
        	double[] vXZ=null;
        	double[] vXY=null;
        	if(this.rotationX!=0)
        	{
        		vYZ = this.rotateCoordinateByAngle(vY-(this.rotationPointY-8f)*scale, vZ-(this.rotationPointZ+8f)*scale, this.rotationX);
        		vY=vYZ[0]+(this.rotationPointY-8f)*scale;
        		vZ=vYZ[1]+(this.rotationPointZ+8f)*scale;
        	}
        	if(this.rotationY!=0)
        	{
        		vXZ = this.rotateCoordinateByAngle(vX-(this.rotationPointX+8f)*scale, vZ-(this.rotationPointZ+8f)*scale, this.rotationY);
        		vX=vXZ[0]+(this.rotationPointX+8f)*scale;
        		vZ=vXZ[1]-(this.rotationPointZ+8f)*scale;
        	}
        	if(this.rotationZ!=0)
        	{
        		vXY = this.rotateCoordinateByAngle(vX-(this.rotationPointX+8f)*scale, vY-(this.rotationPointY-8f)*scale, this.rotationZ);
        		vX=vXY[0]+(this.rotationPointX+8f)*scale;
        		vY=vXY[1]+(this.rotationPointY-8f)*scale;
        		if(swappingAxisOrBoundsAffectRotationOnAxisZ())
        		{
        			vY-=(this.renderMaxZ-(this.rotationPointZ+8f))*scale*Math.sin(this.rotationZ);
        		}
        	}
        	if(this.swapXandY)
        	{
        		double var0 = vX;
        		vX=vY;
        		vY=var0;
        	}
        	if(this.swapXandZ)
        	{
        		double var0 = vX;
        		vX=vZ;
        		vZ=var0;
        	}
        	if(this.swapYandZ)
        	{
        		double var0 = vY;
        		vY=vZ;
        		vZ=var0;
        	}
    	 	var9.addVertexWithUV(xPos+vX, yPos+vY, zPos+vZ, us[i], vs[i]);
        }
    }

	
	public void setRenderBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		if(this.swapRenderBoundsX)
		{
	        this.renderMinX = 1-maxX;
	        this.renderMaxX = 1-minX;
		}
		else
		{
	        this.renderMinX = minX;
	        this.renderMaxX = maxX;
		}
		if(this.swapRenderBoundsY)
		{
			this.renderMinY = 1-maxY;
        	this.renderMaxY = 1-minY;
		}
		else
		{
			this.renderMinY = minY;
        	this.renderMaxY = maxY;
		}
		if(this.swapRenderBoundsZ)
		{
	        this.renderMinZ = 1-maxZ;
	        this.renderMaxZ = 1-minZ;
		}
		else
		{
	        this.renderMinZ = minZ;
	        this.renderMaxZ = maxZ;
		}
	}
	
	public void setRotationPoint(float rX, float rY, float rZ)
	{
		this.rotationPointX=rX;
		this.rotationPointY=rY;
		this.rotationPointZ=rZ;
	}
	
	public void setPosition(double xPos, double yPos, double zPos)
	{
		this.renderPositionX=xPos;
		this.renderPositionY=yPos;
		this.renderPositionZ=zPos;
	}
	
	public void drawPipe(float fx,float fy,float fz, int xSize,int ySize,int zSize, float radius1, float radius2, ForgeDirection direction, IIcon icon)
	{
		this.setRenderBoundsFromModel(fx, fy, fz, xSize, ySize, zSize);
		this.drawPipe(renderPositionX,renderPositionY, renderPositionZ, direction, radius1, radius2, icon);
	}
	
	public void drawKnee(float fx,float fy,float fz, int xSize,int ySize,int zSize, float radius1, float radius2, ForgeDirection direction1, ForgeDirection direction2, IIcon icon)
	{
		this.setRenderBoundsFromModel(fx, fy, fz, xSize, ySize, zSize);
		this.drawKnee(renderPositionX,renderPositionY, renderPositionZ, direction1, direction2, radius1, radius2, icon);
	}
	
	public void drawBox(float fx,float fy,float fz, int xSize,int ySize,int zSize, Block block, int meta, RenderBlocks blockRenderer)
	{
		this.setRenderBoundsFromModel(fx, fy, fz, xSize, ySize, zSize);
		blockRenderer.renderStandardBlock(block, (int)this.renderPositionX, (int)this.renderPositionY, (int)this.renderPositionZ);
	}
	
	public void setRenderBoundsFromModel(float fx,float fy,float fz, int xSize,int ySize,int zSize)
	{
		float boundMinX=(-this.rotationPointX+8f-fx-xSize)*scale;
		float boundMaxX=(-this.rotationPointX+8f-fx)*scale;
		float boundMinZ=(-this.rotationPointZ+8f-fz-zSize)*scale;
		float boundMaxZ=(-this.rotationPointZ+8f-fz)*scale;
		float boundMinY=(this.rotationPointY-8f-fy-ySize)*scale;
		float boundMaxY=(this.rotationPointY-8f-fy)*scale;
		this.setRenderBounds(boundMinX, boundMinY, boundMinZ, boundMaxX, boundMaxY, boundMaxZ);
	}
	
	public double[] rotateCoordinateByAngle(double coord1, double coord2, double angle)
	{
		double r = Math.sqrt(coord2*coord2+coord1*coord1);
		double alpha0=Math.asin(coord2/r);
		if(coord1<0d)
		{
			alpha0=Math.PI-Math.asin(coord2/r);
		}
		double alpha2=alpha0+angle;
		double coord21=Math.sin(alpha2)*r;
		double coord11=Math.cos(alpha2)*r;
		return new double[]{coord11,coord21};
	}
	
	public void setRotation(float rotationX1, float rotationY1, float rotationZ1) 
	{
		if(this.swapRenderBoundsX)
		{
			this.rotationX=-rotationX1;
		}
		else
		{
			this.rotationX=rotationX1;
		}
		if(this.swapRenderBoundsY)
		{
			this.rotationY=-rotationY1;
		}
		else
		{
			this.rotationY=rotationY1;
		}
		if(swappingAxisOrBoundsAffectRotationOnAxisZ())
		{
			this.rotationZ=-rotationZ1;
		}
		else
		{
			this.rotationZ=rotationZ1;
		}
	}
	
	public void reset()
	{
		this.renderFromInside=false;
		this.swapXandZ=false;
		this.swapXandY=false;
		this.swapYandZ=false;
		this.swapRenderBoundsX=false;
		this.swapRenderBoundsY=false;
		this.swapRenderBoundsZ=false;
		this.setRotation(0f, 0f, 0f);
		this.setRotationPoint(0f, 0f, 0f);
	}
	
	private boolean swappingAxisOrBoundsAffectRotationOnAxisZ()
	{
		return (this.swapRenderBoundsZ && !this.swapXandZ && !this.swapYandZ)||
				(this.swapRenderBoundsX && this.swapXandZ) ||
				(this.swapRenderBoundsY && this.swapYandZ);
	}
	
	public List<String> splitStringByWidth(String string, int stringWidth)
	{
		List<String> output = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String[] splittedBySpaces = string.split(" ");
		for(String word:splittedBySpaces)
		{
			if(!word.contains("/n") && getStringWidth(sb)+getStringWidth(word)<stringWidth)
			{
				sb.append(word);
				sb.append(" ");
			}
			else
			{
				output.add(sb.toString());
				sb.delete(0, sb.length());
				if(!word.contains("/n"))
				{
					sb.append(word);
					sb.append(" ");
				}
				else
				{
					sb.append("     ");
				}
			}
		}
		output.add(sb.toString());
		return output;
	}
	
	public int getStringWidth(StringBuffer sb)
	{
		return getStringWidth(sb.toString());
	}
	
	public int getStringWidth(String sb)
	{
		return Minecraft.getMinecraft().fontRenderer.getStringWidth(sb);
	}
	
	@SubscribeEvent
	public void drawBlockSelectionBox(DrawBlockHighlightEvent event)
	{
		if(event.target!=null && event.target.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK))
		{
			WorldClient world = Minecraft.getMinecraft().theWorld;
			TileEntity te = world.getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ);
			if(te!=null)
			{
				IHLMod.proxy.renderTESpecialSelectionBox(te, event.player,event.currentItem,event.target,event.partialTicks) ;
			}
		}
	}

}
