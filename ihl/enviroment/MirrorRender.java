package ihl.enviroment;

import java.nio.IntBuffer;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Project;

import ihl.IHLMod;
import ihl.model.IHLBlockRenderer;
import ihl.utils.IHLRenderUtils;

public class MirrorRender extends TileEntitySpecialRenderer{
private int texture=-1;
private int fb=-1;
private static final IntBuffer textureIDBuffer = BufferUtils.createIntBuffer(1);
private static final float zNear = 1.5f;//1.5f
private static final float zFar = 0.1f;//0.1f
private final int textureWidth=512;
private final int textureHeight=512;
private final int[] xdepth;
private final int[] ydepth;
private final int[] textureU;
private final int[] textureV;
private final Minecraft mc;
private static final boolean useARB = true;

public MirrorRender()
{
	xdepth=new int[] {0,0,1,1};
	ydepth=new int[] {0,1,1,0};
	textureU=new int[] {0,0,1,1};
	textureV=new int[]{0,1,1,0};
	mc=Minecraft.getMinecraft();
}

public void renderAModelAt(MirrorTileEntity tile, double x, double y, double z, float f) {
	Tessellator tessellator=Tessellator.instance;
	float[] mAxis = tile.getMirrorAxis();
	if(texture==-1)
	{
		GL11.glGenTextures(textureIDBuffer);
		texture=textureIDBuffer.get(0);
		fb=GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fb);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, textureWidth, textureHeight, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer)null);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,GL11. GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, textureWidth, textureHeight);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
        IntBuffer drawBuffs = BufferUtils.createIntBuffer(1);
        drawBuffs.put(0, GL30.GL_COLOR_ATTACHMENT0);
        GL20.glDrawBuffers(drawBuffs);// SPme parts of code here mindlessly taken from TheArni/Advanced-Graphics-Processing-Units. https://github.com/TheArni/Advanced-Graphics-Processing-Units/

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) 
        {
        	IHLMod.log.error("Something went wrong while creating frame buffer!");
        	IHLMod.log.error(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER));
        } 
        else 
        {
        	IHLMod.log.info("FrameBuffer loaded correctly!");
        }
	} 
	//Show reflection when:
	//x>-1 when offsetX==-1
	//x<0 when offsetX==1
	if(tile.shouldReflect && 
	((x>-0.8D && mAxis[3]==-1)||
	(x<-0.2 && mAxis[3]==1)||
	(y>-0.8D && mAxis[4]==-1)||
	(y<-0.2D && mAxis[4]==1)||
	(z>-0.8D && mAxis[5]==-1)||
	(z<-0.2D && mAxis[5]==1)))
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fb);
		drawReflection(tile, x, y, z, f);
		mc.entityRenderer.setupCameraTransform(f, 0);
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y , (float) z);
		defineMeshAndTextureCoordinates(tile);
        IHLRenderUtils.instance.enableAmbientLighting();
		drawMirrorFrame(tile);
		IHLRenderUtils.instance.disableAmbientLighting();
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
	}
}
	
	@Override
	public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
	{
		this.renderAModelAt((MirrorTileEntity)par1TileEntity, par2, par4, par6, par8);
	}
	
	private void drawReflection(MirrorTileEntity tile, double x, double y, double z, float f)
	{
		GL11.glViewport(0, 0, textureWidth, textureHeight);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		setMirrorView(tile, x, y, z);
		IHLRenderUtils.instance.enableAmbientLighting();
		mc.renderGlobal.renderSky(f);
		mc.renderGlobal.renderClouds(f);
		IHLRenderUtils.instance.disableAmbientLighting();
		setMirrorView(tile, x, y, z);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		bindTexture(TextureMap.locationBlocksTexture);
		GL11.glTranslatef((-tile.xCoord), (-tile.yCoord), (-tile.zCoord));
		if (tile.displayListCache != -1)
		{
			GL11.glCallList(tile.displayListCache);
			if(tile.needRenderUpdate)
			{
				IHLBlockRenderer.instance.refreshDisplayLists(tile.displayListCache, tile.bwc, tile.chunkCache);
				tile.needRenderUpdate=false;
			}
		}
		else if(tile.chunkCache!=null)
		{
			tile.displayListCache=GLAllocation.generateDisplayLists(1);
			IHLBlockRenderer.instance.refreshDisplayLists(tile.displayListCache, tile.bwc, tile.chunkCache);
		}
		Iterator<Entity> ientity = tile.reflectedEntity.iterator();
		while(ientity.hasNext())
		{
			Entity centity = ientity.next();
			Render render = (Render) IHLMod.proxy.getRenderForEntityClass(centity.getClass());
			if(render!=null)
			{
				render.doRender(centity, centity.prevPosX, centity.prevPosY, centity.prevPosZ, 1f, f);
		        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
				GL11.glColor4f(1f, 1f, 1f, 1f);
			}
		}
		Iterator<TileEntity> itentity = tile.reflectedTileEntity.iterator();
		while(itentity.hasNext())
		{
			TileEntity te = itentity.next();
			if(TileEntityRendererDispatcher.instance.hasSpecialRenderer(te))
			{
				TileEntitySpecialRenderer specialRenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(te);
				specialRenderer.renderTileEntityAt(te, te.xCoord, te.yCoord, te.zCoord, f);
		        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			}
		}
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	private void defineMeshAndTextureCoordinates(MirrorTileEntity tile)
	{
		switch (tile.getFacing())
		{
		case 0://up
			xdepth[0]=1+tile.reflectExtensionRight;
			xdepth[1]=1+tile.reflectExtensionRight;
			xdepth[2]=0;
			xdepth[3]=0;
			ydepth[0]=0;
			ydepth[1]=1+tile.reflectExtensionTop;
			ydepth[2]=1+tile.reflectExtensionTop;
			ydepth[3]=0;
			textureU[0]=0;
			textureU[1]=0;
			textureU[2]=1;
			textureU[3]=1;
			textureV[0]=0;
			textureV[1]=1;
			textureV[2]=1;
			textureV[3]=0;
			break;
		case 1://down
			xdepth[0]=-tile.reflectExtensionRight;
			xdepth[1]=-tile.reflectExtensionRight;
			xdepth[2]=1;
			xdepth[3]=1;
			ydepth[0]=-tile.reflectExtensionTop;
			ydepth[1]=1;
			ydepth[2]=1;
			ydepth[3]=-tile.reflectExtensionTop;
			textureU[0]=0;
			textureU[1]=0;
			textureU[2]=1;
			textureU[3]=1;
			textureV[0]=0;
			textureV[1]=1;
			textureV[2]=1;
			textureV[3]=0;
			break;
		case 2:
			xdepth[0]=0;
			xdepth[1]=0;
			xdepth[2]=1+tile.reflectExtensionRight;
			xdepth[3]=1+tile.reflectExtensionRight;
			ydepth[0]=0;
			ydepth[1]=1+tile.reflectExtensionTop;
			ydepth[2]=1+tile.reflectExtensionTop;
			ydepth[3]=0;
			textureU[0]=0;
			textureU[1]=0;
			textureU[2]=1;
			textureU[3]=1;
			textureV[0]=0;
			textureV[1]=1;
			textureV[2]=1;
			textureV[3]=0;
			break;
		case 3:
			xdepth[0]=-tile.reflectExtensionRight;
			xdepth[1]=1;
			xdepth[2]=1;
			xdepth[3]=-tile.reflectExtensionRight;
			ydepth[0]=0;
			ydepth[1]=0;
			ydepth[2]=1+tile.reflectExtensionTop;
			ydepth[3]=1+tile.reflectExtensionTop;
			textureU[0]=1;
			textureU[1]=0;
			textureU[2]=0;
			textureU[3]=1;
			textureV[0]=0;
			textureV[1]=0;
			textureV[2]=1;
			textureV[3]=1;
			break;
		case 4:
			xdepth[0]=-tile.reflectExtensionRight;
			xdepth[1]=1;
			xdepth[2]=1;
			xdepth[3]=-tile.reflectExtensionRight;
			ydepth[0]=0;
			ydepth[1]=0;
			ydepth[2]=1+tile.reflectExtensionTop;
			ydepth[3]=1+tile.reflectExtensionTop;
			textureU[0]=1;
			textureU[1]=0;
			textureU[2]=0;
			textureU[3]=1;
			textureV[0]=0;
			textureV[1]=0;
			textureV[2]=1;
			textureV[3]=1;
			break;
		case 5:
			xdepth[0]=0;
			xdepth[1]=0;
			xdepth[2]=1+tile.reflectExtensionRight;
			xdepth[3]=1+tile.reflectExtensionRight;
			ydepth[0]=0;
			ydepth[1]=1+tile.reflectExtensionTop;
			ydepth[2]=1+tile.reflectExtensionTop;
			ydepth[3]=0;
			textureU[0]=0;
			textureU[1]=0;
			textureU[2]=1;
			textureU[3]=1;
			textureV[0]=0;
			textureV[1]=1;
			textureV[2]=1;
			textureV[3]=0;
			break;
		default:
		}
		
	}
	
	private void drawMirrorFrame(MirrorTileEntity tile)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GL11.glDisable(GL11.GL_BLEND);
		Tessellator tessellator=Tessellator.instance;
		tessellator.startDrawingQuads(); 
		for(int i=0;i<xdepth.length;i++)
		{	
			switch (tile.getFacing())
			{
			case 0:
				tessellator.addVertexWithUV(xdepth[i], 0.9d, ydepth[i],textureU[i],textureV[i]); 
				break;
			case 1:
				tessellator.addVertexWithUV(xdepth[i], 0.1d, ydepth[i],textureU[i],textureV[i]); 
				break;
			case 2:
				tessellator.addVertexWithUV(xdepth[i], ydepth[i], 0.9d,textureU[i],textureV[i]); 
				break;
			case 3:
				tessellator.addVertexWithUV(xdepth[i], ydepth[i], 0.1d,textureU[i],textureV[i]); 
				break;
			case 4:
				tessellator.addVertexWithUV(0.9d, ydepth[i], xdepth[i],textureU[i],textureV[i]); 
				break;
			case 5:
				tessellator.addVertexWithUV(0.1d, ydepth[i], xdepth[i],textureU[i],textureV[i]); 
				break;
			default:
			}
		}
		tessellator.draw();
	}
	
	private void setMirrorView(MirrorTileEntity tile, double x, double y, double z)
	{
		float mirrorHeightCoorrection, mirrorWidthCoorrection, xShift, yShift, shiftCorrection,yCorrection,xCorrection,zCorrection;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        Project.gluPerspective(65f, (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.mc.gameSettings.renderDistanceChunks * 32F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
		double aspectRatioMirror = Math.sqrt((tile.reflectExtensionTop+1)/(double)(tile.reflectExtensionRight+1));
		double aspectRatio = Display.getHeight()/(double)Display.getWidth()/aspectRatioMirror;
		shiftCorrection = tile.reflectExtensionTop*0.45f+tile.reflectExtensionRight*0.45f-0.5f;
		float kYS = 0.5f;//0.5f;
		float kZS = 0.0f;//0.5f;
		switch(tile.getFacing())
		{
		//swap y and z
			case 0://down facing mirror
				mirrorHeightCoorrection = (tile.reflectExtensionTop+1)*0.5f;
				yShift = (float)(z+mirrorHeightCoorrection)*kYS;
				yCorrection = mirrorHeightCoorrection-yShift*(tile.reflectExtensionTop*0.5f+1f);
				zCorrection = 2f+shiftCorrection-kZS; 
				mirrorWidthCoorrection = (-tile.reflectExtensionRight+1)*0.5f;
				xShift = (float)(-x+mirrorWidthCoorrection)*0.5f-0.5f;
				GL11.glFrustum(-aspectRatio+xShift, aspectRatio+xShift, -aspectRatioMirror+yShift, aspectRatioMirror+yShift, zNear, zFar);
				xCorrection = mirrorWidthCoorrection+xShift*(tile.reflectExtensionRight*0.5f-0.375f/(tile.reflectExtensionRight+1)+1.125f);
				GLU.gluLookAt(xCorrection+tile.reflectExtensionRight, zCorrection, yCorrection, xCorrection+tile.reflectExtensionRight/*x reference*/, -32f+zCorrection/*y reference*/, yCorrection/*z reference*/, 0.0f, 0.0f, 1.0f);
				break;
			case 1://up facing mirror
				mirrorHeightCoorrection = (-tile.reflectExtensionTop+1)*0.5f;
				yShift = (float)(z+mirrorHeightCoorrection)*kYS;
				yCorrection = mirrorHeightCoorrection-yShift*(tile.reflectExtensionTop*0.5f+1f);
				zCorrection = -1f-shiftCorrection-kZS; 
				mirrorWidthCoorrection = (-tile.reflectExtensionRight+1)*0.5f;
				xShift = (float)(x+mirrorWidthCoorrection)*0.5f;
				GL11.glFrustum(-aspectRatio+xShift, aspectRatio+xShift, -aspectRatioMirror+yShift, aspectRatioMirror+yShift, zNear, zFar);
				xCorrection = mirrorWidthCoorrection-xShift*(tile.reflectExtensionRight*0.5f+1f);
				GLU.gluLookAt(xCorrection, zCorrection, yCorrection, xCorrection/*x reference*/, 32f+zCorrection/*y reference*/, yCorrection/*z reference*/, 0.0f, 0.0f, 1.0f);
				break;
			case 2:
				mirrorHeightCoorrection = (tile.reflectExtensionTop+1)*0.5f;
				yShift = (float)(y+mirrorHeightCoorrection)*kYS;
				yCorrection = mirrorHeightCoorrection-yShift*(tile.reflectExtensionTop*0.5f+1f);
				zCorrection = 2f+shiftCorrection-kZS; 
				mirrorWidthCoorrection = (tile.reflectExtensionRight+1)*0.5f;
				xShift = (float)(x+mirrorWidthCoorrection)*0.5f;
				GL11.glFrustum(-aspectRatio+xShift, aspectRatio+xShift, -aspectRatioMirror+yShift, aspectRatioMirror+yShift, zNear, zFar);
				xCorrection = mirrorWidthCoorrection-xShift*(tile.reflectExtensionRight*0.5f+1f);
				GLU.gluLookAt(xCorrection, yCorrection, zCorrection, xCorrection/*x reference*/, yCorrection/*y reference*/, -32f+zCorrection/*z reference*/, 0.0f, 1.0f, 0.0f);
				break;
			case 3:
				mirrorHeightCoorrection = (tile.reflectExtensionTop+1)*0.5f;
				yShift = (float)(y+mirrorHeightCoorrection)*kYS;
				yCorrection = mirrorHeightCoorrection-yShift*(tile.reflectExtensionRight*0.5f+1f);
				zCorrection = -1f-shiftCorrection-kZS; 
				mirrorWidthCoorrection = 0.5f-tile.reflectExtensionRight*0.5f;
				xShift = (float)(-x-mirrorWidthCoorrection)*0.5f;
				GL11.glFrustum(-aspectRatio+xShift, aspectRatio+xShift, -aspectRatioMirror+yShift, aspectRatioMirror+yShift, zNear, zFar);
				xCorrection = mirrorWidthCoorrection+xShift*(tile.reflectExtensionTop*0.5f-0.375f/(tile.reflectExtensionTop+1)+1.125f);
				GLU.gluLookAt(xCorrection, yCorrection, zCorrection, xCorrection/*x reference*/, yCorrection/*y reference*/, 32f+zCorrection/*z reference*/, 0.0f, 1.0f, 0.0f);
				break;
			case 4:
				mirrorHeightCoorrection = (tile.reflectExtensionTop+1)*0.5f;
				yShift = (float)(y+mirrorHeightCoorrection)*kYS;
				yCorrection = mirrorHeightCoorrection-yShift*(tile.reflectExtensionTop*0.5f+1f);
				zCorrection = 2f+shiftCorrection-kZS;
				mirrorWidthCoorrection = 0.5f-tile.reflectExtensionRight*0.5f;
				xShift = (float)(-z-mirrorWidthCoorrection)*0.5f;
				GL11.glFrustum(-aspectRatio+xShift, aspectRatio+xShift, -aspectRatioMirror+yShift, aspectRatioMirror+yShift, zNear, zFar);
				xCorrection = mirrorWidthCoorrection+xShift*(tile.reflectExtensionRight*0.5f-0.375f/(tile.reflectExtensionRight+1)+1.125f);
				GLU.gluLookAt(zCorrection, yCorrection, xCorrection, zCorrection-32f/*x reference*/, yCorrection/*y reference*/, xCorrection/*z reference*/, 0.0f, 1.0f, 0.0f);
				break;
			case 5:
				mirrorHeightCoorrection = (tile.reflectExtensionTop+1)*0.5f;
				yShift = (float)(y+mirrorHeightCoorrection)*kYS;
				yCorrection = mirrorHeightCoorrection-yShift*(tile.reflectExtensionTop*0.5f+1f);
				zCorrection = -1f-shiftCorrection-kZS; 
				mirrorWidthCoorrection = (tile.reflectExtensionRight+1)*0.5f;
				xShift = (float)(z+mirrorWidthCoorrection)*0.5f;
				GL11.glFrustum(-aspectRatio+xShift, aspectRatio+xShift, -aspectRatioMirror+yShift, aspectRatioMirror+yShift, zNear, zFar);
				xCorrection = mirrorWidthCoorrection-xShift*(tile.reflectExtensionRight*0.5f+1f);
				GLU.gluLookAt(zCorrection, yCorrection, xCorrection, zCorrection+32f/*x reference*/, yCorrection/*y reference*/, xCorrection/*z reference*/, 0.0f, 1.0f, 0.0f);
				break;
			default:
				mirrorHeightCoorrection = (tile.reflectExtensionTop+1)*0.5f;
				yShift = (float)(y+mirrorHeightCoorrection)*kYS;
				yCorrection = mirrorHeightCoorrection-yShift*(tile.reflectExtensionTop*0.5f+1f);
				zCorrection = 2f+shiftCorrection-kZS; 
				mirrorWidthCoorrection = (tile.reflectExtensionRight+1)*0.5f;
				xShift = (float)(x+mirrorWidthCoorrection)*0.5f;
				GL11.glFrustum(-aspectRatio+xShift, aspectRatio+xShift, -aspectRatioMirror+yShift, aspectRatioMirror+yShift, zNear, zFar);
				xCorrection = mirrorWidthCoorrection-xShift*(tile.reflectExtensionRight*0.5f+1f);
				GLU.gluLookAt(xCorrection, yCorrection, zCorrection, xCorrection/*x reference*/, yCorrection/*y reference*/, -32f+zCorrection/*z reference*/, 0.0f, 1.0f, 0.0f);
		}
	}
	
	public static void deleteTextures()
	{
		GL11.glDeleteTextures(textureIDBuffer);
	}
}