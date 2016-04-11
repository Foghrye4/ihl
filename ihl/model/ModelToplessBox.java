package ihl.model;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;

public class ModelToplessBox {
	    /**
	     * The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube
	     */
	    private PositionTextureVertex[] vertexPositions;

	    /** An array of 6 TexturedQuads, one for each face of a cube */
	    private TexturedQuad[] quadList;

	    /** X vertex coordinate of lower box corner */
	    public final float posX1;

	    /** Y vertex coordinate of lower box corner */
	    public final float posY1;

	    /** Z vertex coordinate of lower box corner */
	    public final float posZ1;

	    /** X vertex coordinate of upper box corner */
	    public final float posX2;

	    /** Y vertex coordinate of upper box corner */
	    public final float posY2;

	    /** Z vertex coordinate of upper box corner */
	    public final float posZ2;
	    public String field_78247_g;
	    private boolean[] renderFace;
	    private boolean inverted = false;

	    public ModelToplessBox(IHLModelRenderer par1ModelRenderer, int par2, int par3, float par4, float par5, float par6, int par7, int par8, int par9, float par10, boolean inverted1, boolean renderFace1[])
	    {
	    	this.inverted=inverted1;
	    	renderFace=renderFace1;
	        this.posX1 = par4;
	        this.posY1 = par5;
	        this.posZ1 = par6;
	        this.posX2 = par4 + par7;
	        this.posY2 = par5 + par8;
	        this.posZ2 = par6 + par9;
	        this.vertexPositions = new PositionTextureVertex[8];
	        this.quadList = new TexturedQuad[6];
	        float var11 = par4 + par7;
	        float var12 = par5 + par8;
	        float var13 = par6 + par9;
	        par4 -= par10;
	        par5 -= par10;
	        par6 -= par10;
	        var11 += par10;
	        var12 += par10;
	        var13 += par10;

	        if (par1ModelRenderer.mirror)
	        {
	            float var14 = var11;
	            var11 = par4;
	            par4 = var14;
	        }

	        PositionTextureVertex var23 = new PositionTextureVertex(par4, par5, par6, 0.0F, 0.0F);
	        PositionTextureVertex var15 = new PositionTextureVertex(var11, par5, par6, 0.0F, 8.0F);
	        PositionTextureVertex var16 = new PositionTextureVertex(var11, var12, par6, 8.0F, 8.0F);
	        PositionTextureVertex var17 = new PositionTextureVertex(par4, var12, par6, 8.0F, 0.0F);
	        PositionTextureVertex var18 = new PositionTextureVertex(par4, par5, var13, 0.0F, 0.0F);
	        PositionTextureVertex var19 = new PositionTextureVertex(var11, par5, var13, 0.0F, 8.0F);
	        PositionTextureVertex var20 = new PositionTextureVertex(var11, var12, var13, 8.0F, 8.0F);
	        PositionTextureVertex var21 = new PositionTextureVertex(par4, var12, var13, 8.0F, 0.0F);
	        this.vertexPositions[0] = var23;
	        this.vertexPositions[1] = var15;
	        this.vertexPositions[2] = var16;
	        this.vertexPositions[3] = var17;
	        this.vertexPositions[4] = var18;
	        this.vertexPositions[5] = var19;
	        this.vertexPositions[6] = var20;
	        this.vertexPositions[7] = var21;
	        if(inverted)
	        {
	        	this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] {var20, var16, var15, var19}, par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
	        	this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] {var17, var21, var18, var23}, par2, par3 + par9, par2 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
	        	this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] {var15, var23, var18, var19}, par2 + par9, par3, par2 + par9 + par7, par3 + par9, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
	        	this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] {var20, var21, var17, var16}, par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par7, par3, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
	        	this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] {var16, var17, var23, var15}, par2 + par9, par3 + par9, par2 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
	        	this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] {var21, var20, var19, var18}, par2 + par9 + par7 + par9, par3 + par9, par2 + par9 + par7 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
	        }
	        else
	        {
	        	this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] {var19, var15, var16, var20}, par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
		        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] {var23, var18, var21, var17}, par2, par3 + par9, par2 + par9, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
		        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] {var19, var18, var23, var15}, par2 + par9, par3, par2 + par9 + par7, par3 + par9, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
		        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] {var16, var17, var21, var20}, par2 + par9 + par7, par3 + par9, par2 + par9 + par7 + par7, par3, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
		        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] {var15, var23, var17, var16}, par2 + par9, par3 + par9, par2 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
		        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] {var18, var19, var20, var21}, par2 + par9 + par7 + par9, par3 + par9, par2 + par9 + par7 + par9 + par7, par3 + par9 + par8, par1ModelRenderer.textureWidth, par1ModelRenderer.textureHeight);
	        }
	        if (par1ModelRenderer.mirror)
	        {
	            for (int var22 = 0; var22 < this.quadList.length; ++var22)
	            {
	                this.quadList[var22].flipFace();
	            }
	        }
	    }

	    /**
	     * Draw the six sided box defined by this ModelBox
	     */
	    public void render(Tessellator par1Tessellator, float par2)
	    {
	    	for(int i=0;i<this.quadList.length;i++)
	    	{
	    		if(this.renderFace[i])
	    		{
	    			this.quadList[i].draw(par1Tessellator, par2);
	    		}
	    	}
	    }

	    public ModelToplessBox func_78244_a(String par1Str)
	    {
	        this.field_78247_g = par1Str;
	        return this;
	    }
	    
	    public void redefineVertices(double xs[],double ys[],double zs[])
	    {
	    	this.quadList[1].vertexPositions[0].vector3D.xCoord=xs[0];
	    	this.quadList[1].vertexPositions[0].vector3D.yCoord=ys[0];
	    	this.quadList[1].vertexPositions[0].vector3D.zCoord=zs[0];
	    	this.quadList[2].vertexPositions[2].vector3D.xCoord=xs[0];
	    	this.quadList[2].vertexPositions[2].vector3D.yCoord=ys[0];
	    	this.quadList[2].vertexPositions[2].vector3D.zCoord=zs[0];
	    	this.quadList[4].vertexPositions[1].vector3D.xCoord=xs[0];
	    	this.quadList[4].vertexPositions[1].vector3D.yCoord=ys[0];
	    	this.quadList[4].vertexPositions[1].vector3D.zCoord=zs[0];
	    	this.quadList[0].vertexPositions[1].vector3D.xCoord=xs[1];
	    	this.quadList[0].vertexPositions[1].vector3D.yCoord=ys[1];
	    	this.quadList[0].vertexPositions[1].vector3D.zCoord=zs[1];
	    	this.quadList[2].vertexPositions[3].vector3D.xCoord=xs[1];
	    	this.quadList[2].vertexPositions[3].vector3D.yCoord=ys[1];
	    	this.quadList[2].vertexPositions[3].vector3D.zCoord=zs[1];
	    	this.quadList[4].vertexPositions[0].vector3D.xCoord=xs[1];
	    	this.quadList[4].vertexPositions[0].vector3D.yCoord=ys[1];
	    	this.quadList[4].vertexPositions[0].vector3D.zCoord=zs[1];
	    	this.quadList[0].vertexPositions[2].vector3D.xCoord=xs[2];
	    	this.quadList[0].vertexPositions[2].vector3D.yCoord=ys[2];
	    	this.quadList[0].vertexPositions[2].vector3D.zCoord=zs[2];
	    	this.quadList[3].vertexPositions[0].vector3D.xCoord=xs[2];
	    	this.quadList[3].vertexPositions[0].vector3D.yCoord=ys[2];
	    	this.quadList[3].vertexPositions[0].vector3D.zCoord=zs[2];
	    	this.quadList[4].vertexPositions[3].vector3D.xCoord=xs[2];
	    	this.quadList[4].vertexPositions[3].vector3D.yCoord=ys[2];
	    	this.quadList[4].vertexPositions[3].vector3D.zCoord=zs[2];
	    	this.quadList[1].vertexPositions[3].vector3D.xCoord=xs[3];
	    	this.quadList[1].vertexPositions[3].vector3D.yCoord=ys[3];
	    	this.quadList[1].vertexPositions[3].vector3D.zCoord=zs[3];
	    	this.quadList[3].vertexPositions[1].vector3D.xCoord=xs[3];
	    	this.quadList[3].vertexPositions[1].vector3D.yCoord=ys[3];
	    	this.quadList[3].vertexPositions[1].vector3D.zCoord=zs[3];
	    	this.quadList[4].vertexPositions[2].vector3D.xCoord=xs[3];
	    	this.quadList[4].vertexPositions[2].vector3D.yCoord=ys[3];
	    	this.quadList[4].vertexPositions[2].vector3D.zCoord=zs[3];
	    	this.quadList[1].vertexPositions[1].vector3D.xCoord=xs[4];
	    	this.quadList[1].vertexPositions[1].vector3D.yCoord=ys[4];
	    	this.quadList[1].vertexPositions[1].vector3D.zCoord=zs[4];
	    	this.quadList[2].vertexPositions[1].vector3D.xCoord=xs[4];
	    	this.quadList[2].vertexPositions[1].vector3D.yCoord=ys[4];
	    	this.quadList[2].vertexPositions[1].vector3D.zCoord=zs[4];
	    	this.quadList[5].vertexPositions[0].vector3D.xCoord=xs[4];
	    	this.quadList[5].vertexPositions[0].vector3D.yCoord=ys[4];
	    	this.quadList[5].vertexPositions[0].vector3D.zCoord=zs[4];
	    	this.quadList[0].vertexPositions[0].vector3D.xCoord=xs[5];
	    	this.quadList[0].vertexPositions[0].vector3D.yCoord=ys[5];
	    	this.quadList[0].vertexPositions[0].vector3D.zCoord=zs[5];
	    	this.quadList[2].vertexPositions[0].vector3D.xCoord=xs[5];
	    	this.quadList[2].vertexPositions[0].vector3D.yCoord=ys[5];
	    	this.quadList[2].vertexPositions[0].vector3D.zCoord=zs[5];
	    	this.quadList[5].vertexPositions[1].vector3D.xCoord=xs[5];
	    	this.quadList[5].vertexPositions[1].vector3D.yCoord=ys[5];
	    	this.quadList[5].vertexPositions[1].vector3D.zCoord=zs[5];
	    	this.quadList[0].vertexPositions[3].vector3D.xCoord=xs[6];
	    	this.quadList[0].vertexPositions[3].vector3D.yCoord=ys[6];
	    	this.quadList[0].vertexPositions[3].vector3D.zCoord=zs[6];
	    	this.quadList[3].vertexPositions[3].vector3D.xCoord=xs[6];
	    	this.quadList[3].vertexPositions[3].vector3D.yCoord=ys[6];
	    	this.quadList[3].vertexPositions[3].vector3D.zCoord=zs[6];
	    	this.quadList[5].vertexPositions[2].vector3D.xCoord=xs[6];
	    	this.quadList[5].vertexPositions[2].vector3D.yCoord=ys[6];
	    	this.quadList[5].vertexPositions[2].vector3D.zCoord=zs[6];
	    	this.quadList[1].vertexPositions[2].vector3D.xCoord=xs[7];
	    	this.quadList[1].vertexPositions[2].vector3D.yCoord=ys[7];
	    	this.quadList[1].vertexPositions[2].vector3D.zCoord=zs[7];
	    	this.quadList[3].vertexPositions[2].vector3D.xCoord=xs[7];
	    	this.quadList[3].vertexPositions[2].vector3D.yCoord=ys[7];
	    	this.quadList[3].vertexPositions[2].vector3D.zCoord=zs[7];
	    	this.quadList[5].vertexPositions[3].vector3D.xCoord=xs[7];
	    	this.quadList[5].vertexPositions[3].vector3D.yCoord=ys[7];
	    	this.quadList[5].vertexPositions[3].vector3D.zCoord=zs[7];
	    }
}
