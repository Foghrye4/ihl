package ihl.model;

import ic2.api.tile.IWrenchable;
import ihl.utils.IHLRenderUtils;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class RefluxCondenserBlockRender implements ISimpleBlockRenderingHandler
{
    public static int renderId;
    
    public RefluxCondenserBlockRender()
    {
    	renderId = RenderingRegistry.getNextAvailableRenderId();
    }

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) 
	{   
		Tessellator tessellator = Tessellator.instance;
		IHLRenderUtils.instance.reset();
    	GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    	tessellator.startDrawingQuads();
		IIcon icon = block.getIcon(0, 0);
		IHLRenderUtils.instance.setPosition(0, 0, 0);
        IHLRenderUtils.instance.setRotationPoint(0F, 16F, 0F);
        IHLRenderUtils.instance.setRotation(0F, 0f, 0F);
        IHLRenderUtils.instance.drawPipe(-2F, 7F, -2F, 4, 1, 4, 0.6f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawKnee(-8F, 2F, -3F, 5, 6, 6, .8F, 1F, ForgeDirection.UP, ForgeDirection.EAST,icon);
        IHLRenderUtils.instance.drawPipe(3.5F, -7F, -1.5F, 4, 3, 3, 0.8f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(7F, -7.5F, -2F, 1, 4, 4, 0.6f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(7F, 3F, -2F, 1, 4, 4, 0.6f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-3F, 3.5F, -1.5F, 10, 3, 3, .8F, 1F, ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-2F, -8F, -2F, 4, 1, 4, 0.6f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-1.5F, -7F, -1.5F, 3, 2, 3, 0.8f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-1.5F, 5F, -1.5F, 3, 2, 3, 0.8f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-2F, -2F, -8F, 4, 4, 1, 0.6f, 1f,ForgeDirection.NORTH,icon);
        IHLRenderUtils.instance.drawPipe(-1.5F, -1.5F, -7F, 3, 3, 7, 0.8f, 1f,ForgeDirection.NORTH,icon);
        IHLRenderUtils.instance.setRotationPoint(3F, 16F, 0F);
        IHLRenderUtils.instance.setRotation(0F, 0f, -0.5F);
        IHLRenderUtils.instance.drawPipe(1F, -4F, -4F, 2, 8, 8, 0f, 0.8f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-9F, -4F, -4F, 10, 8, 8, 0.8f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-11F, -4F, -4F, 2, 8, 8, 0f, 0.8f,ForgeDirection.EAST,icon);
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta, RenderBlocks blockRenderer) 
	{
		IHLRenderUtils.instance.reset();
		TileEntity tile = blockAccess.getTileEntity(x, y, z);
		if(tile.getWorldObj() != null)
		{
			switch (((IWrenchable)tile).getFacing())
			{
			case 5:
				IHLRenderUtils.instance.renderFromInside=true;
				IHLRenderUtils.instance.swapXandZ=true;
				IHLRenderUtils.instance.swapRenderBoundsZ=true;
				break;
			case 3:
				IHLRenderUtils.instance.swapRenderBoundsX=true;
				IHLRenderUtils.instance.swapRenderBoundsZ=true;
				break;
			case 4:
				IHLRenderUtils.instance.renderFromInside=true;
				IHLRenderUtils.instance.swapXandZ=true;
				IHLRenderUtils.instance.swapRenderBoundsX=true;
				break;
			default:
			}
		}
        Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
        int colorMultiplier = block.colorMultiplier(blockAccess, x, y, z);
        float red = (colorMultiplier >> 16 & 255) / 255.0F;
        float green = (colorMultiplier >> 8 & 255) / 255.0F;
        float blue = (colorMultiplier & 255) / 255.0F;
        Tessellator.instance.setColorOpaque_F(red, green, blue);
        IIcon icon = block.getBlockTextureFromSide(0);
        IHLRenderUtils.instance.setPosition(x, y, z);
        IHLRenderUtils.instance.setRotationPoint(0F, 16F, 0F);
        IHLRenderUtils.instance.setRotation(0F, 0f, 0F);
        IHLRenderUtils.instance.drawPipe(-2F, 7F, -2F, 4, 1, 4, 0.6f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawKnee(-8F, 2F, -3F, 5, 6, 6, .8F, 1F, ForgeDirection.UP, ForgeDirection.EAST,icon);
        IHLRenderUtils.instance.drawPipe(3.5F, -7F, -1.5F, 4, 3, 3, 0.8f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(7F, -7.5F, -2F, 1, 4, 4, 0.6f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(7F, 3F, -2F, 1, 4, 4, 0.6f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-3F, 3.5F, -1.5F, 10, 3, 3, .8F, 1F, ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-2F, -8F, -2F, 4, 1, 4, 0.6f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-1.5F, -7F, -1.5F, 3, 2, 3, 0.8f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-1.5F, 5F, -1.5F, 3, 2, 3, 0.8f, 1f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-2F, -2F, -8F, 4, 4, 1, 0.6f, 1f,ForgeDirection.NORTH,icon);
        IHLRenderUtils.instance.drawPipe(-1.5F, -1.5F, -7F, 3, 3, 7, 0.8f, 1f,ForgeDirection.NORTH,icon);
        IHLRenderUtils.instance.setRotationPoint(3F, 16F, 0F);
        IHLRenderUtils.instance.setRotation(0F, 0f, -0.5F);
        IHLRenderUtils.instance.drawPipe(1F, -4F, -4F, 2, 8, 8, 0f, 0.8f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-9F, -4F, -4F, 10, 8, 8, 0.8f, 1f,ForgeDirection.WEST,icon);
        IHLRenderUtils.instance.drawPipe(-11F, -4F, -4F, 2, 8, 8, 0f, 0.8f,ForgeDirection.EAST,icon);
        return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int arg0) 
	{
		return true;
	}

	@Override
	public int getRenderId() {
		return renderId;
	}
}
