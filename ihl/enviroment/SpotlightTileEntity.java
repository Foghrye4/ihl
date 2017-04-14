package ihl.enviroment;

import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.IC2;
import ihl.ClientProxy;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.handpump.XYZ;
import ihl.utils.IHLUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class SpotlightTileEntity extends LightBulbTileEntity implements INetworkTileEntityEventListener {
	public float directionX = 0f;
	public float directionY = -1f;
	public float directionZ = 0f;
	public float prevDirectionX = 0f;
	public float prevDirectionY = -1f;
	public float prevDirectionZ = 0f;
	public float rotationPitch = 0f;
	public float rotationYaw = 0f;
	public float prevRotationPitch = 0f;
	public float prevRotationYaw = 0f;
	boolean needLightTargetUpdate = false;

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		directionX = nbttagcompound.getFloat("directionX");
		directionY = nbttagcompound.getFloat("directionY");
		directionZ = nbttagcompound.getFloat("directionZ");
		this.rotationPitch = this.getVectorPitchAngle(directionX,directionY,directionZ);
		this.rotationYaw = this.getVectorYawAngle(directionX,directionY,directionZ);
		needLightTargetUpdate = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setDouble("directionX", directionX);
		nbttagcompound.setDouble("directionY", directionY);
		nbttagcompound.setDouble("directionZ", directionZ);
	}

	@Override
	public List<String> getNetworkedFields() {
		List<String> list = super.getNetworkedFields();
		list.add("needLightTargetUpdate");
		list.add("rotationPitch");
		list.add("rotationYaw");
		list.add("directionX");
		list.add("directionY");
		list.add("directionZ");
		return list;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this.worldObj.isRemote) {
			if(this.directionX!=this.prevDirectionX || 
					this.directionY!=this.prevDirectionY ||
					this.directionZ!=this.prevDirectionZ){
				updateLightState();
				this.prevDirectionX=this.directionX;
				this.prevDirectionY=this.directionY;
				this.prevDirectionZ=this.directionZ;
			}
		} else if (needLightTargetUpdate) {
			this.updateLightState();
			needLightTargetUpdate = false;
		}
		if (!this.worldObj.isRemote) {
			if (this.prevRotationPitch != this.rotationPitch) {
				IC2.network.get().updateTileEntityField(this, "rotationPitch");
				this.prevRotationPitch = this.rotationPitch;
			}
			if (this.prevRotationYaw != this.rotationYaw) {
				IC2.network.get().updateTileEntityField(this, "rotationYaw");
				this.prevRotationYaw = this.rotationYaw;
			}
		}
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("spotlight");
	}

	public void setDirectionVector(EntityLivingBase player) {
		ForgeDirection dir = ForgeDirection.getOrientation(this.getFacing());
		Vec3 lookVec = player.getLookVec();
		if (lookVec.xCoord * dir.offsetX + lookVec.yCoord * dir.offsetY + lookVec.zCoord * dir.offsetZ < 0) {
			double x = player.posX - this.xCoord - 0.5D;
			double y = player.posY + player.getEyeHeight() - this.yCoord - 0.5D;
			double z = player.posZ - this.zCoord - 0.5D;
			double d = Math.sqrt(x * x + y * y + z * z);
			directionX = (float) (x / d);
			directionY = (float) (y / d);
			directionZ = (float) (z / d);
		} else {
			Vec3 plook = player.getLookVec();
			directionX = (float) (plook.xCoord);
			directionY = (float) (plook.yCoord);
			directionZ = (float) (plook.zCoord);
		}
		IC2.network.get().updateTileEntityField(this, "directionX");
		IC2.network.get().updateTileEntityField(this, "directionY");
		IC2.network.get().updateTileEntityField(this, "directionZ");
		this.rotationPitch = this.getVectorPitchAngle(directionX,directionY,directionZ);
		this.rotationYaw = this.getVectorYawAngle(directionX,directionY,directionZ);
		this.needLightTargetUpdate = true;
		IC2.network.get().initiateTileEntityEvent(this, 0, true);
	}

	private float getVectorPitchAngle(float x, float y, float z) {
		switch (this.getFacing()) {
		case 0:
			return (float) -(Math.abs(Math.asin(z)) + Math.abs(Math.asin(x)));
		case 1:
			return (float) (Math.abs(Math.asin(z)) + Math.abs(Math.asin(x)));
		case 2:
			return (float) (Math.abs(Math.asin(y)) + Math.abs(Math.asin(x)));
		case 3:
			return (float) (Math.abs(Math.asin(y)) + Math.abs(Math.asin(x)));
		case 4:
			return (float) (Math.abs(Math.asin(y)) + Math.abs(Math.asin(z)));
		case 5:
			return (float) (Math.abs(Math.asin(y)) + Math.abs(Math.asin(z)));
		default:
			return (float) (-Math.asin(y));
		}
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	protected LightSource createLightSource(int red, int green, int blue) {
		return ((ClientProxy) IHLMod.proxy).getLightHandler().calculateLightSource(worldObj, xCoord, yCoord, zCoord,
				256, red, green, blue, new double[] { this.xCoord + 0.5f, this.yCoord + 0.5f, this.zCoord + 0.5f,
						this.directionX, this.directionY, this.directionZ, 0.8d });
	}

	private float getVectorYawAngle(float x, float y, float z) {
		switch (this.getFacing()) {
		case 0:
			if (z >= 0) {
				return (float) (Math.acos(x / Math.sqrt(x * x + z * z)) - Math.PI / 2);
			} else {
				return (float) (-Math.acos(x / Math.sqrt(x * x + z * z)) - Math.PI / 2);
			}
		case 1:
			if (z >= 0) {
				return (float) (Math.acos(x / Math.sqrt(x * x + z * z)) - Math.PI / 2);
			} else {
				return (float) (-Math.acos(x / Math.sqrt(x * x + z * z)) - Math.PI / 2);
			}
		case 2:
			if (y >= 0) {
				return (float) (-Math.acos(x / Math.sqrt(x * x + y * y)) - Math.PI / 2);
			} else {
				return (float) (Math.acos(x / Math.sqrt(x * x + y * y)) - Math.PI / 2);
			}
		case 3:
			if (y >= 0) {
				return (float) (-Math.acos(x / Math.sqrt(x * x + y * y)) - Math.PI / 2);
			} else {
				return (float) (Math.acos(x / Math.sqrt(x * x + y * y)) - Math.PI / 2);
			}
		case 4:
			if (y >= 0) {
				return (float) (-Math.acos(z / Math.sqrt(z * z + y * y)) - Math.PI / 2);
			} else {
				return (float) (Math.acos(z / Math.sqrt(z * z + y * y)) - Math.PI / 2);
			}
		case 5:
			if (y >= 0) {
				return (float) (-Math.acos(z / Math.sqrt(z * z + y * y)) - Math.PI / 2);
			} else {
				return (float) (Math.acos(z / Math.sqrt(z * z + y * y)) - Math.PI / 2);
			}
		default:
			return 0f;
		}
	}

	@Override
	public void onNetworkEvent(int event) {
		this.worldObj.playSound(xCoord + 0.5d, yCoord + 0.5d, zCoord + 0.5d, IHLModInfo.MODID + ":spotlightRotating",
				10F, 1f, true);
	}
}

/*
 * -Y DOWN(0, -1, 0) 0 /* +Y UP(0, 1, 0) 1 /* -Z NORTH(0, 0, -1) 2 /* +Z
 * SOUTH(0, 0, 1) 3 /* -X WEST(-1, 0, 0), 4 /* +X EAST(1, 0, 0), 5
 * //VALID_DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};
 */
