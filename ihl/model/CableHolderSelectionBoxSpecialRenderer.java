package ihl.model;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ihl.flexible_cable.AnchorTileEntity;
import ihl.interfaces.ISelectionBoxSpecialRenderer;
import ihl.utils.IHLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

@SideOnly(value=Side.CLIENT)
public class CableHolderSelectionBoxSpecialRenderer implements ISelectionBoxSpecialRenderer
{
    @Override
	public void drawSelectionBox(EntityPlayer player, ItemStack currentItem, MovingObjectPosition movingObjectPosition, float partialTick)
    {
    	AnchorTileEntity ate = (AnchorTileEntity) Minecraft.getMinecraft().theWorld.getTileEntity(movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ);
    	short facing = IHLUtils.getFacingFromPlayerView(player, true);
    	double[] portPos = ate.energyNetNodes[facing].getPortPos(player);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.8F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        double offsetX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
        double offsetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
        double offsetZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
        double size=0.1d;
        RenderGlobal.drawOutlinedBoundingBox(AxisAlignedBB.getBoundingBox(portPos[0]-size, portPos[1]-size, portPos[2]-size, portPos[0]+size, portPos[1]+size, portPos[2]+size).getOffsetBoundingBox(-offsetX, -offsetY, -offsetZ), -1);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
