package ihl.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class IronWorkbenchBlockRender implements ISimpleBlockRenderingHandler
{
    public static int renderId;
    
    public IronWorkbenchBlockRender()
    {
    	renderId = RenderingRegistry.getNextAvailableRenderId();
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
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		for(int i=0;i<=5;i++)
		{
			this.setBounds(i, renderblocks, block);
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, 0));
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, 0));
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, 0));
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, 0));
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, 0));
        	tessellator.setNormal(1.0F, 0.0F, 0.0F);
        	renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, 0));
		}
    	tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        renderblocks.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta, RenderBlocks renderblocks) 
	{
        Tessellator var5 = Tessellator.instance;
        var5.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
        int var6 = block.colorMultiplier(blockAccess, x, y, z);
        float var7 = (var6 >> 16 & 255) / 255.0F;
        float var8 = (var6 >> 8 & 255) / 255.0F;
        float var9 = (var6 & 255) / 255.0F;
        var5.setColorOpaque_F(var7, var8, var9);
		for(int i=0;i<=5;i++)
		{
			this.setBounds(i, renderblocks, block);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
        return true;
	}
	
	private void setBounds(int index, RenderBlocks renderblocks, Block block)
	{
		double rt = 1/11d;
		switch(index)
		{
		case 0:
			renderblocks.setRenderBounds(0d, 10*rt, 0d, 1d, 1d, 1d);
			break;
		case 1:
			renderblocks.setRenderBounds(rt*2, 4*rt, rt*2, 9*rt, 5*rt, 9*rt);
			break;
		case 2:
			renderblocks.setRenderBounds(rt, 0d, rt, rt*3, rt*10, rt*3);
			break;
		case 3:
			renderblocks.setRenderBounds(rt*8, 0d, rt, rt*10, rt*10, rt*3);
			break;
		case 4:
			renderblocks.setRenderBounds(rt, 0d, rt*8, rt*3, rt*10, rt*10);
			break;
		case 5:
			renderblocks.setRenderBounds(rt*8, 0d, rt*8, rt*10, rt*10, rt*10);
			break;
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int arg0) 
	{
		return true;
	}
}
