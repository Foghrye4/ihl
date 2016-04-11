package ihl.model;

import ihl.items_blocks.MachineBaseBlock;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ImpregnatingMachineBlockRender implements ISimpleBlockRenderingHandler
{
    public static int renderId;
    
    public ImpregnatingMachineBlockRender()
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
        float var11 = 0.0625F;
        IIcon innerSideIcon = block.getBlockTextureFromSide(0);
        if(block instanceof MachineBaseBlock)
        {
        	innerSideIcon = ((MachineBaseBlock)block).getAdditionalIconsForBlockRenderer(0);
        }
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 1.0F - var11, innerSideIcon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, var11-1.0F, innerSideIcon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 1.0F - var11, 0.0D, 0.0D, innerSideIcon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, var11-1.0F, 0.0D, 0.0D, innerSideIcon);
        tessellator.draw();
        IIcon innerBottomIcon = block.getBlockTextureFromSide(0);
        if(block instanceof MachineBaseBlock)
        {
        	innerBottomIcon = ((MachineBaseBlock)block).getAdditionalIconsForBlockRenderer(1);
        }
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, var11-1.0F, 0.0D, innerBottomIcon);
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta, RenderBlocks blockRenderer) 
	{
		blockRenderer.renderStandardBlock(block, x, y, z);
        Tessellator var5 = Tessellator.instance;
        var5.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
        int var6 = block.colorMultiplier(blockAccess, x, y, z);
        float var7 = (var6 >> 16 & 255) / 255.0F;
        float var8 = (var6 >> 8 & 255) / 255.0F;
        float var9 = (var6 & 255) / 255.0F;
        float var11;
        var5.setColorOpaque_F(var7, var8, var9);
        IIcon innerSideIcon = block.getBlockTextureFromSide(0);
        if(block instanceof MachineBaseBlock)
        {
        	innerSideIcon = ((MachineBaseBlock)block).getAdditionalIconsForBlockRenderer(0);
        }
        var11 = 0.0625F;
        blockRenderer.renderFaceXPos(block, x - 1.0F + var11, y, z, innerSideIcon);
        blockRenderer.renderFaceXNeg(block, x + 1.0F - var11, y, z, innerSideIcon);
        blockRenderer.renderFaceZPos(block, x, y, z - 1.0F + var11, innerSideIcon);
        blockRenderer.renderFaceZNeg(block, x, y, z + 1.0F - var11, innerSideIcon);
        IIcon innerBottomIcon = block.getBlockTextureFromSide(0);
        if(block instanceof MachineBaseBlock)
        {
        	innerBottomIcon = ((MachineBaseBlock)block).getAdditionalIconsForBlockRenderer(1);
        }
        blockRenderer.renderFaceYPos(block, x, y - 1.0F + var11, z, innerBottomIcon);
        return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int arg0) 
	{
		return true;
	}
}
