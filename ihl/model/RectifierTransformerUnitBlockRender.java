package ihl.model;

import ic2.api.tile.IWrenchable;
import ihl.items_blocks.MachineBaseBlock;
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

public class RectifierTransformerUnitBlockRender implements ISimpleBlockRenderingHandler
{
    public static int renderId;
    
    public RectifierTransformerUnitBlockRender()
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
		for(int i=0;i<=43;i++)
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
		

		this.setBounds(44, renderblocks, block);
		renderblocks.renderFaceXNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceXPos(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceZNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceZPos(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceYNeg(block,0, 0, 0, block.getIcon(5, 0));
		
		this.setBounds(45, renderblocks, block);
		renderblocks.renderFaceXNeg(block,0, 0, 0, block.getIcon(5, 0));
		renderblocks.renderFaceXPos(block,0, 0, 0, block.getIcon(5, 0));
		renderblocks.renderFaceZNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceZPos(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceYNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceYPos(block,0, 0, 0, block.getIcon(0, 0));
		
		this.setBounds(46, renderblocks, block);
		renderblocks.renderFaceXNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceXPos(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceZNeg(block,0, 0, 0, block.getIcon(5, 0));
		renderblocks.renderFaceZPos(block,0, 0, 0, block.getIcon(5, 0));
		renderblocks.renderFaceYNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceYPos(block,0, 0, 0, block.getIcon(0, 0));
		
		this.setBounds(47, renderblocks, block);
		renderblocks.renderFaceXNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceXPos(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceZNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceZPos(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceYNeg(block,0, 0, 0, block.getIcon(0, 0));
		renderblocks.renderFaceYPos(block,0, 0, 0, block.getIcon(4, 0));
		

        IHLRenderUtils.instance.setPosition(0, 0, 0);
        IHLRenderUtils.instance.setRotationPoint(0F, 16F, 0F);
        IHLRenderUtils.instance.setRotation(0F, 0f, 0F);

		IIcon icon = block.getIcon(1, 0);
        IHLRenderUtils.instance.drawPipe(-6F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-6F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(-6F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

        IHLRenderUtils.instance.drawPipe(2F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(2F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
        IHLRenderUtils.instance.drawPipe(2F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

        icon = block.getIcon(2, 0);
		IHLRenderUtils.instance.drawPipe(-4.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
        icon = block.getIcon(3, 0);
        IHLRenderUtils.instance.drawPipe(3.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);

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
		for(int i=0;i<=43;i++)
		{
			this.setBounds(i, renderblocks, block);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		
		this.setBounds(44, renderblocks, block);
		renderblocks.renderFaceXNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceXPos(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceZNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceZPos(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceYNeg(block, x, y, z, block.getIcon(5, 0));
		
		this.setBounds(45, renderblocks, block);
		renderblocks.renderFaceXNeg(block, x, y, z, block.getIcon(5, 0));
		renderblocks.renderFaceXPos(block, x, y, z, block.getIcon(5, 0));
		renderblocks.renderFaceZNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceZPos(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceYNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceYPos(block, x, y, z, block.getIcon(0, 0));
		
		this.setBounds(46, renderblocks, block);
		renderblocks.renderFaceXNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceXPos(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceZNeg(block, x, y, z, block.getIcon(5, 0));
		renderblocks.renderFaceZPos(block, x, y, z, block.getIcon(5, 0));
		renderblocks.renderFaceYNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceYPos(block, x, y, z, block.getIcon(0, 0));
		
		this.setBounds(47, renderblocks, block);
		renderblocks.renderFaceXNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceXPos(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceZNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceZPos(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceYNeg(block, x, y, z, block.getIcon(0, 0));
		renderblocks.renderFaceYPos(block, x, y, z, block.getIcon(4, 0));
		
        IHLRenderUtils.instance.setPosition(x, y, z);
        IHLRenderUtils.instance.setRotationPoint(0F, 16F, 0F);
        IHLRenderUtils.instance.setRotation(0F, 0f, 0F);

		IIcon icon = block.getIcon(1, 0);
        
        TileEntity tile = blockAccess.getTileEntity(x, y, z);
		if(tile!=null && tile.getWorldObj() != null)
		{
			switch (((IWrenchable)tile).getFacing())
			{
			case 2:
		        IHLRenderUtils.instance.drawPipe(-6F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-6F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-6F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        IHLRenderUtils.instance.drawPipe(2F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(2F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(2F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        icon = block.getIcon(2, 0);
				IHLRenderUtils.instance.drawPipe(-4.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
		        icon = block.getIcon(3, 0);
		        IHLRenderUtils.instance.drawPipe(3.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
				break;
			case 5:
		        IHLRenderUtils.instance.drawPipe(-2F, -14F, -6F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -12F, -6F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -10F, -6F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        IHLRenderUtils.instance.drawPipe(-2F, -14F, 2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -12F, 2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -10F, 2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        icon = block.getIcon(2, 0);
				IHLRenderUtils.instance.drawPipe(-0.5F, -16F, -4.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
		        icon = block.getIcon(3, 0);
		        IHLRenderUtils.instance.drawPipe(-0.5F, -16F, 3.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
				break;
			case 3:
		        IHLRenderUtils.instance.drawPipe(-6F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-6F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-6F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        IHLRenderUtils.instance.drawPipe(2F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(2F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(2F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        icon = block.getIcon(3, 0);
				IHLRenderUtils.instance.drawPipe(-4.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
		        icon = block.getIcon(2, 0);
		        IHLRenderUtils.instance.drawPipe(3.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
				break;
			case 4:
		        IHLRenderUtils.instance.drawPipe(-2F, -14F, -6F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -12F, -6F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -10F, -6F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        IHLRenderUtils.instance.drawPipe(-2F, -14F, 2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -12F, 2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-2F, -10F, 2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        icon = block.getIcon(3, 0);
				IHLRenderUtils.instance.drawPipe(-0.5F, -16F, -4.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
		        icon = block.getIcon(2, 0);
		        IHLRenderUtils.instance.drawPipe(-0.5F, -16F, 3.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
				break;
			default:
		        IHLRenderUtils.instance.drawPipe(-6F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-6F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(-6F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        IHLRenderUtils.instance.drawPipe(2F, -14F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(2F, -12F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);
		        IHLRenderUtils.instance.drawPipe(2F, -10F, -2F, 4, 2, 4, 0f, 0.5f,ForgeDirection.UP,icon);

		        icon = block.getIcon(2, 0);
				IHLRenderUtils.instance.drawPipe(-4.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
		        icon = block.getIcon(3, 0);
		        IHLRenderUtils.instance.drawPipe(3.5F, -16F, -0.5F, 1, 2, 1, 0f, 1f, ForgeDirection.UP,icon);
				break;
			}
		}
        return true;
	}
	
	private void setBounds(int index, RenderBlocks renderblocks, Block block)
	{
		double rt = 1/11d;
		switch(index)
		{
		case 0:
			renderblocks.setRenderBounds(rt, 0d, 0d, rt*2, 1d, rt);
			break;
		case 1:
			renderblocks.setRenderBounds(rt*3, rt*8, 0d, rt*4, 1d, rt);
			break;
		case 2:
			renderblocks.setRenderBounds(rt*5, rt*8, 0d, rt*6, 1d, rt);
			break;
		case 3:
			renderblocks.setRenderBounds(rt*7, rt*8, 0d, rt*8, 1d, rt);
			break;
		case 4:
			renderblocks.setRenderBounds(rt*3, 0d, 0d, rt*4, rt*3, rt);
			break;
		case 5:
			renderblocks.setRenderBounds(rt*5, 0d, 0d, rt*6, rt*3, rt);
			break;
		case 6:
			renderblocks.setRenderBounds(rt*7, 0d, 0d, rt*8, rt*3, rt);
			break;
		case 7:
			renderblocks.setRenderBounds(rt*9, 0d, 0d, rt*10, 1d, rt);
			break;

		case 8:
			renderblocks.setRenderBounds(rt, 0d, rt*10, rt*2, 1d, 1d);
			break;
		case 9:
			renderblocks.setRenderBounds(rt*3, rt*8, rt*10, rt*4, 1d, 1d);
			break;
		case 10:
			renderblocks.setRenderBounds(rt*5, rt*8, rt*10, rt*6, 1d, 1d);
			break;
		case 11:
			renderblocks.setRenderBounds(rt*7, rt*8, rt*10, rt*8, 1d, 1d);
			break;
		case 12:
			renderblocks.setRenderBounds(rt*3, 0d, rt*10, rt*4, rt*3, 1d);
			break;
		case 13:
			renderblocks.setRenderBounds(rt*5, 0d, rt*10, rt*6, rt*3, 1d);
			break;
		case 14:
			renderblocks.setRenderBounds(rt*7, 0d, rt*10, rt*8, rt*3, 1d);
			break;
		case 15:
			renderblocks.setRenderBounds(rt*9, 0d, rt*10, rt*10, 1d, 1d);
			break;

		case 16:
			renderblocks.setRenderBounds(0d, 0d, rt, rt, 1d, rt*2);
			break;
		case 17:
			renderblocks.setRenderBounds(0d,rt*8, rt*3,rt,1d,  rt*4);
			break;
		case 18:
			renderblocks.setRenderBounds(0d,rt*8, rt*5,rt, 1d,  rt*6);
			break;
		case 19:
			renderblocks.setRenderBounds(0d,rt*8, rt*7,rt,1d,  rt*8);
			break;
		case 20:
			renderblocks.setRenderBounds(0d,0d, rt*3, rt, rt*3,rt*4);
			break;
		case 21:
			renderblocks.setRenderBounds(0d,0d, rt*5,rt, rt*3, rt*6);
			break;
		case 22:
			renderblocks.setRenderBounds(0d,0d, rt*7,  rt,rt*3, rt*8);
			break;
		case 23:
			renderblocks.setRenderBounds(0d,0d, rt*9,rt,1d,  rt*10);
			break;

		case 24:
			renderblocks.setRenderBounds(rt*10, 0d, rt, 1d, 1d, rt*2);
			break;
		case 25:
			renderblocks.setRenderBounds(rt*10, rt*8, rt*3, 1d, 1d, rt*4);
			break;
		case 26:
			renderblocks.setRenderBounds(rt*10, rt*8, rt*5,1d, 1d, rt*6);
			break;
		case 27:
			renderblocks.setRenderBounds(rt*10, rt*8, rt*7, 1d, 1d, rt*8);
			break;
		case 28:
			renderblocks.setRenderBounds(rt*10, 0d, rt*3, 1d, rt*3, rt*4);
			break;
		case 29:
			renderblocks.setRenderBounds(rt*10, 0d, rt*5,1d, rt*3, rt*6);
			break;
		case 30:
			renderblocks.setRenderBounds(rt*10, 0d, rt*7,1d, rt*3, rt*8);
			break;
		case 31:
			renderblocks.setRenderBounds(rt*10, 0d, rt*9, 1d, 1d, rt*10);
			break;

		case 32:
			renderblocks.setRenderBounds(rt, 0d, rt*3, rt*3, rt, rt*4);
			break;
		case 33:
			renderblocks.setRenderBounds(rt, 0d, rt*5, rt*3, rt, rt*6);
			break;
		case 34:
			renderblocks.setRenderBounds(rt, 0d, rt*7, rt*3, rt, rt*8);
			break;
		case 35:
			renderblocks.setRenderBounds(rt*8, 0d, rt*3, rt*10, rt, rt*4);
			break;
		case 36:
			renderblocks.setRenderBounds(rt*8, 0d, rt*5, rt*10, rt, rt*6);
			break;
		case 37:
			renderblocks.setRenderBounds(rt*8, 0d, rt*7, rt*10, rt, rt*8);
			break;

		case 38:
			renderblocks.setRenderBounds(rt*3, 0d, rt, rt*4, rt, rt*3);
			break;
		case 39:
			renderblocks.setRenderBounds(rt*5, 0d, rt, rt*6, rt, rt*3);
			break;
		case 40:
			renderblocks.setRenderBounds(rt*7, 0d, rt, rt*8, rt, rt*3);
			break;
		case 41:
			renderblocks.setRenderBounds(rt*3, 0d, rt*8, rt*4, rt, rt*10);
			break;
		case 42:
			renderblocks.setRenderBounds(rt*5, 0d, rt*8, rt*6, rt, rt*10);
			break;
		case 43:
			renderblocks.setRenderBounds(rt*7, 0d, rt*8, rt*8, rt, rt*10);
			break;
			
		case 44:
			renderblocks.setRenderBounds(rt*3, 0d, rt*3, rt*8, rt, rt*8);
			break;

		case 45:
			renderblocks.setRenderBounds(0d, rt*3, rt*3, 1d, rt*8, rt*8);
			break;

		case 46:
			renderblocks.setRenderBounds(rt*3, rt*3, 0d, rt*8, rt*8, 1d);
			break;

		case 47:
			renderblocks.setRenderBounds(rt, rt, rt, rt*10, 1d, rt*10);
			break;
	
		}
	}

	@Override
	public boolean shouldRender3DInInventory(int arg0) 
	{
		return true;
	}
}
