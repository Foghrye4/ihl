package ihl.explosion;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import ihl.utils.IHLMathUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class PileBlockRender implements ISimpleBlockRenderingHandler
{
    public static int renderId;
    public final PileTileEntityRender pileTileEntityRender;

    public PileBlockRender()
    {
    	renderId = RenderingRegistry.getNextAvailableRenderId();
		pileTileEntityRender = new PileTileEntityRender(this);
    }

	@Override
	public int getRenderId() 
	{
		return renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) 
	{
        Tessellator tessellator = Tessellator.instance;
        block.setBlockBoundsForItemRender();
        renderblocks.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderblocks.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderblocks.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderblocks.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderblocks.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderblocks.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderblocks.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta, RenderBlocks blockRenderer) 
	{
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if(te instanceof PileTileEntity)
        {
          	PileTileEntity pte = (PileTileEntity) te;
        	long itemHash = getItemStackHash(pte.content);
        	if(this.pileTileEntityRender.textureIdMap.containsKey(itemHash))
        	{
                Tessellator tessellator = Tessellator.instance;
                tessellator.draw();
        		int texture=this.pileTileEntityRender.textureIdMap.get(itemHash);
       			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
       			int[][][] brightness = new int[3][3][3];
       			this.generateBrightnessMatrix(blockAccess, x, y, z, brightness);
                tessellator.startDrawing(GL11.GL_TRIANGLES);
                tessellator.setColorOpaque_F(1f, 1f, 1f);
                boolean[] blocksViewAround = this.getBlocksViewAround(blockAccess, x, y, z);
                if(		blocksViewAround[0] &&
                		blocksViewAround[1] &&
                		blocksViewAround[2] &&
                		blocksViewAround[3])
                {
                    this.addFlatTop(tessellator, x, y, z, brightness);
                }
                else if(!blocksViewAround[0] &&
                		blocksViewAround[1] &&
                		blocksViewAround[2] &&
                		blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 1, 1, 0, 1, brightness);
                }
                else if(blocksViewAround[0] &&
                		!blocksViewAround[1] &&
                		blocksViewAround[2] &&
                		blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 0, 1, 1, 1, brightness);
                }
                else if(blocksViewAround[0] &&
                		blocksViewAround[1] &&
                		!blocksViewAround[2] &&
                		blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 1, 1, 1, 0, brightness);
                }
                else if(blocksViewAround[0] &&
                		blocksViewAround[1] &&
                		blocksViewAround[2] &&
                		!blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 1, 0, 1, 1, brightness);
                }
                // 2-sided piramid
                else if(blocksViewAround[0] &&
                		blocksViewAround[1] &&
                		!blocksViewAround[2] &&
                		!blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 1, 0, 1, 0, brightness);
                }
                else if(!blocksViewAround[0] &&
                		blocksViewAround[1] &&
                		blocksViewAround[2] &&
                		!blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 1, 0, 0, 1, brightness);
                }
                else if(!blocksViewAround[0] &&
                		!blocksViewAround[1] &&
                		blocksViewAround[2] &&
                		blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 0, 1, 0, 1, brightness);
                }
                else if(blocksViewAround[0] &&
                		!blocksViewAround[1] &&
                		!blocksViewAround[2] &&
                		blocksViewAround[3])
                {
                    this.addSlope(tessellator, x, y, z, 0, 1, 1, 0, brightness);
                }
                else
                {
                    this.addPyramid(tessellator, x, y, z, brightness);
                }
                if(!blockAccess.getBlock(x, y-1, z).isOpaqueCube())
                {
                	this.addBottom(tessellator, x, y, z, brightness);
                }
                tessellator.draw();
        		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        		tessellator.startDrawingQuads();
        	}
        	return true;
        }
        else
        {
            return false;
        }
	}
	
	private boolean[] getBlocksViewAround(IBlockAccess blockAccess, int x, int y, int z)
	{
		//   +Z
		//    ^
		//[ ][0][ ]
		//[3][x][1] ->+X
		//[ ][2][ ]
		return new boolean[] {
				blockAccess.getBlock(x, y, z+1).isOpaqueCube(),
				blockAccess.getBlock(x+1, y, z).isOpaqueCube(),
				blockAccess.getBlock(x, y, z-1).isOpaqueCube(),
				blockAccess.getBlock(x-1, y, z).isOpaqueCube(),
		};
	}

	private void addBottom(Tessellator tessellator, int x, int y, int z, int[][][] brightness)
	{
		double[][] triangle1 = new double[3][3];
		double[][] triangle2 = new double[3][3];
		triangle1[0] = new double[] {0D,0D,1D};
		triangle1[1] = new double[] {0D,0D,0D};
		triangle1[2] = new double[] {1D,0D,0D};
		triangle2[0] = triangle1[0];
		triangle2[1] = triangle1[2];
		triangle2[2] = new double[] {1D,0D,1D};
		this.addTriangle(tessellator, triangle1, x, y, z, ForgeDirection.UP, brightness);
		this.addTriangle(tessellator, triangle2, x, y, z, ForgeDirection.UP, brightness);
	}
	
	private void addFlatTop(Tessellator tessellator, int x, int y, int z, int[][][] brightness)
	{
		double[][] triangle1 = new double[3][3];
		double[][] triangle2 = new double[3][3];
		double height = 0.9D;
		triangle1[0] = new double[] {1D,height,0D};
		triangle1[1] = new double[] {0D,height,0D};
		triangle1[2] = new double[] {0D,height,1D};
		triangle2[0] = triangle1[2];
		triangle2[1] = new double[] {1D,height,1D};
		triangle2[2] = triangle1[0];
		this.addTriangle(tessellator, triangle1, x, y, z, ForgeDirection.UP, brightness);
		this.addTriangle(tessellator, triangle2, x, y, z, ForgeDirection.UP, brightness);
	}
	
	private void addSlope(Tessellator tessellator, int x, int y, int z, int px, int nx, int pz, int nz, int[][][] brightness)
	{
		double[][] triangle1 = new double[3][3];
		double[][] triangle2 = new double[3][3];
		if(px*pz+nx*nz==1)
		{
			triangle1[0] = new double[] {1D,px*nz,0D}; //+X -Z
			triangle1[1] = new double[] {0D,nx*nz,0D}; //-X -Z
			triangle1[2] = new double[] {1D,px*pz,1D}; //-X +Z
			triangle2[0] = triangle1[2];
			triangle2[1] = triangle1[1]; //+X +Z
			triangle2[2] = new double[] {0D,nx*pz,1D};
		}
		else
		{
			triangle1[0] = new double[] {1D,px*nz,0D}; //+X -Z
			triangle1[1] = new double[] {0D,nx*nz,0D}; //-X -Z
			triangle1[2] = new double[] {0D,nx*pz,1D}; //-X +Z
			triangle2[0] = triangle1[2];
			triangle2[1] = new double[] {1D,px*pz,1D}; //+X +Z
			triangle2[2] = triangle1[0];
		}
		this.addTriangle(tessellator, triangle1, x, y, z, ForgeDirection.UP, brightness);
		this.addTriangle(tessellator, triangle2, x, y, z, ForgeDirection.UP, brightness);
	}
	
	private void addPyramid(Tessellator tessellator, int x, int y, int z, int[][][] brightness)
	{
		double[][] triangle1 = new double[3][3];
		double[][] triangle2 = new double[3][3];
		double[][] triangle3 = new double[3][3];
		double[][] triangle4 = new double[3][3];
		double pileHeight = 0.3D;
		triangle1[0] = new double[] {1D,0D,0D};
		triangle1[1] = new double[] {0D,0D,0D};
		triangle1[2] = new double[] {0.5D,pileHeight,0.5D};
		triangle2[0] = triangle1[1];
		triangle2[1] = new double[] {0D,0D,1D};
		triangle2[2] = triangle1[2];
		triangle3[0] = triangle2[1];
		triangle3[1] = new double[] {1D,0D,1D};
		triangle3[2] = triangle1[2];
		triangle4[0] = triangle3[1];
		triangle4[1] = triangle1[0];
		triangle4[2] = triangle1[2];
		this.addTriangle(tessellator, triangle1, x, y, z, ForgeDirection.UP, brightness);
		this.addTriangle(tessellator, triangle2, x, y, z, ForgeDirection.UP, brightness);
		this.addTriangle(tessellator, triangle3, x, y, z, ForgeDirection.UP, brightness);
		this.addTriangle(tessellator, triangle4, x, y, z, ForgeDirection.UP, brightness);
	}
	
	private void addTriangle(Tessellator tessellator, double[][] triangle, int x, int y, int z, ForgeDirection uvmapping, int[][][] brightness)
	{
		int iu = 0;
		int iv = 2;
		if(uvmapping.offsetY == 0)
		{
			iv = 1;
			if(uvmapping.offsetZ == 0)
			{
				iu = 2;
			}
		}
		float[] normal = IHLMathUtils.get_triangle_normal(triangle);
        tessellator.setNormal(normal[0], normal[1], normal[2]);
        tessellator.setBrightness(this.getBrightness(triangle[0], brightness));
    	tessellator.addVertexWithUV(x+triangle[0][0], y+triangle[0][1], z+triangle[0][2], triangle[0][iu], triangle[0][iv]);
        tessellator.setBrightness(this.getBrightness(triangle[1], brightness));
    	tessellator.addVertexWithUV(x+triangle[1][0], y+triangle[1][1], z+triangle[1][2], triangle[1][iu], triangle[1][iv]);
        tessellator.setBrightness(this.getBrightness(triangle[2], brightness));
    	tessellator.addVertexWithUV(x+triangle[2][0], y+triangle[2][1], z+triangle[2][2], triangle[2][iu], triangle[2][iv]);
	}
	
	public long getItemStackHash(ItemStack stack)
	{
		if(stack==null)
		{
			return 0;
		}
    	return ((long)Item.getIdFromItem(stack.getItem())<<31^stack.getItemDamage());
	}
	
	private void generateBrightnessMatrix(IBlockAccess blockAccess, int x, int y, int z, int[][][] brightness)
	{
		for(int ix = -1; ix <= 1; ix++)
		for(int iy = -1; iy <= 1; iy++)
		for(int iz = -1; iz <= 1; iz++)
		{
			Block block = blockAccess.getBlock(ix+x, iy+y, iz+z);
			if(block!=null && block!=Blocks.air)
			{
				brightness[ix+1][iy+1][iz+1] = block.getMixedBrightnessForBlock(blockAccess, ix+x, iy+y, iz+z);
			}
			else
			{
				brightness[ix+1][iy+1][iz+1] = block.getMixedBrightnessForBlock(blockAccess, x, y, z);
			}
		}
	}
	
	private int getBrightness(double v[], int[][][] brightness)
	{
		int x1 = v[0]<0.5d?0:1;
		int x2 = x1+1;
		int y1 = v[1]<0.5d?0:1;
		int y2 = y1+1;
		int z1 = v[2]<0.5d?0:1;
		int z2 = z1+1;
		float dx = v[0]<0.5d?(float)v[0]*2f:(float)v[0]*2f-1f;
		float dy = v[1]<0.5d?(float)v[1]*2f:(float)v[1]*2f-1f;
		float dz = v[2]<0.5d?(float)v[2]*2f:(float)v[2]*2f-1f;
		int brightness1 = brightness[x1][y1][z1];
		int brightness2 = brightness[x2][y2][z2];
		float d = IHLMathUtils.sqrt(dx*dx+dy*dy+dz*dz);
		return (int)(brightness1*(1f-d)*0.8f+brightness2*d*0.8f+brightness[1][1][1]*0.2f) & 16711935;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int arg0) 
	{
		return true;
	}
}
 