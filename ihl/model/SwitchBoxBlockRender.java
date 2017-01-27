package ihl.model;

import ic2.core.Ic2Items;
import ihl.datanet.RedstoneSignalConverterTileEntity;
import ihl.items_blocks.MachineBaseBlock;
import ihl.utils.IHLUtils;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SwitchBoxBlockRender implements ISimpleBlockRenderingHandler
{
    public static int renderId;
    
    public SwitchBoxBlockRender()
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
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta, RenderBlocks blockRenderer) 
	{
		TileEntity te = blockAccess.getTileEntity(x, y, z);
	    IIcon emptyIcon,emitterIcon,sensorIcon = block.getBlockTextureFromSide(0);
	    emptyIcon=emitterIcon=sensorIcon;
	    if(block instanceof MachineBaseBlock)
	    {
	       	emptyIcon = ((MachineBaseBlock)block).getAdditionalIconsForBlockRenderer(0);
	       	emitterIcon = ((MachineBaseBlock)block).getAdditionalIconsForBlockRenderer(1);
	       	sensorIcon = ((MachineBaseBlock)block).getAdditionalIconsForBlockRenderer(2);
	    }
	    IIcon[] sideToTypes = new IIcon[6];
		if(te instanceof RedstoneSignalConverterTileEntity)
		{
			RedstoneSignalConverterTileEntity rscte = (RedstoneSignalConverterTileEntity) te;
			for(int side=0;side<sideToTypes.length;side++)
			{
				if(rscte.sensorEmitterSlots.get(side)==null)
				{
					sideToTypes[side]=emptyIcon;
				}
				else if(IHLUtils.isItemStacksIsEqual(rscte.sensorEmitterSlots.get(side), Ic2Items.detectorCableItem, true)) 
				{
					sideToTypes[side]=emitterIcon;
				}
				else if(IHLUtils.isItemStacksIsEqual(rscte.sensorEmitterSlots.get(side), Ic2Items.splitterCableItem, true)) 
				{
					sideToTypes[side]=sensorIcon;
				}
				else
				{
					sideToTypes[side]=emptyIcon;
				}
			}
		}
		blockRenderer.setRenderBounds(0d, 0d, 0d, 1d, 1d, 1d);
        Tessellator var5 = Tessellator.instance;
        var5.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
        int var6 = block.colorMultiplier(blockAccess, x, y, z);
        float var7 = (var6 >> 16 & 255) / 255.0F;
        float var8 = (var6 >> 8 & 255) / 255.0F;
        float var9 = (var6 & 255) / 255.0F;
        var5.setColorOpaque_F(var7, var8, var9);
        blockRenderer.renderFaceXPos(block, x, y, z, sideToTypes[4]);
        blockRenderer.renderFaceXNeg(block, x, y, z, sideToTypes[5]);
        blockRenderer.renderFaceZPos(block, x, y, z, sideToTypes[2]);
        blockRenderer.renderFaceZNeg(block, x, y, z, sideToTypes[3]);
        blockRenderer.renderFaceYPos(block, x, y, z, sideToTypes[0]);
        blockRenderer.renderFaceYNeg(block, x, y, z, sideToTypes[1]);
        return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int arg0) 
	{
		return true;
	}
}
