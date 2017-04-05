package ihl.model;

import ihl.flexible_cable.RectifierTransformerUnitTileEntity;
import ihl.interfaces.ISelectionBoxSpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class RectifierTransformerUnitSelectionBoxSpecialRenderer implements ISelectionBoxSpecialRenderer
{
    @Override
	public void drawSelectionBox(EntityPlayer player, ItemStack currentItem, MovingObjectPosition movingObjectPosition, float partialTick)
    {
        double offsetX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
        double offsetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
        double offsetZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
        RectifierTransformerUnitTileEntity tile = (RectifierTransformerUnitTileEntity) Minecraft.getMinecraft().theWorld.getTileEntity(movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ);
		if(tile!=null && tile.getWorldObj() != null)
		{
	        GL11.glEnable(GL11.GL_BLEND);
	        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	        GL11.glLineWidth(2.0F);
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glDepthMask(false);
	        short side = tile.getSide(player);
	        if(side==0)
	        {
	            GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.8F);
	            RenderGlobal.drawOutlinedBoundingBox(tile.aabb1.getOffsetBoundingBox(-offsetX, -offsetY, -offsetZ), -1);
	            RenderGlobal.drawOutlinedBoundingBox(tile.aabb1_1.getOffsetBoundingBox(-offsetX, -offsetY, -offsetZ), -1);
	        }
	        else if(side==1)
	        {
	            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.8F);
	            RenderGlobal.drawOutlinedBoundingBox(tile.aabb2.getOffsetBoundingBox(-offsetX, -offsetY, -offsetZ), -1);
	            RenderGlobal.drawOutlinedBoundingBox(tile.aabb2_1.getOffsetBoundingBox(-offsetX, -offsetY, -offsetZ), -1);
	        }
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glDepthMask(true);
	        GL11.glDisable(GL11.GL_BLEND);
		}
    }
}
