package ihl.explosion;

import ic2.core.IC2;
import ihl.IHLMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class PileTileEntity extends TileEntity {
	public ItemStack content = new ItemStack(Blocks.sandstone);
	private boolean firstTick = true;

	@Override
	public boolean canUpdate() {
		return IC2.platform.isRendering();
	}

	@Override
	public void updateEntity() {
		if (firstTick && this.content != null) {
			if (this.worldObj.isRemote) {
				IHLMod.proxy.requestTileEntityInitdataFromClientToServer(xCoord, yCoord, zCoord);
			} else {
				checkAndFall();
			}
			firstTick = false;
		}
	}

	public void setContent(ItemStack other) {
		this.content = other;
	}

	public boolean checkAndFall() {
		Block underblock = this.worldObj.getBlock(xCoord, yCoord - 1, zCoord);
		if (underblock.isAir(worldObj, xCoord, yCoord - 1, zCoord) || underblock == Blocks.air
				|| underblock.getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord - 1, zCoord) == null) {
			IHLEntityFallingPile fallingPile = new IHLEntityFallingPile(worldObj);
			fallingPile.setPosition(xCoord + 0.5d, yCoord + 0.5d, zCoord + 0.5d);
			fallingPile.setEntityItemStack(content);
			this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			this.worldObj.spawnEntityInWorld(fallingPile);
			return false;
		}
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.content = ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("content"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagCompound contentNBT = new NBTTagCompound();
		this.content.writeToNBT(contentNBT);
		nbttagcompound.setTag("content", contentNBT);
	}

	public void updateBlockRender() {
		Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
		if (checkAndFall()) {
			Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		}
	}
}
