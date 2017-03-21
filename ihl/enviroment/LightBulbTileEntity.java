package ihl.enviroment;

import java.util.List;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.network.INetworkDataProvider;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ihl.utils.IHLUtils;

public class LightBulbTileEntity extends TileEntity implements IEnergySink, IWrenchable, INetworkDataProvider {
	private boolean active = false;
	private short facing = 0;
	public boolean prevActive = false;
	public short prevFacing = 0;
	private double energy;
	public boolean addedToEnergyNet = false;
	private boolean loaded = false;
	private int ticker;

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.energy = nbttagcompound.getDouble("energy");
		this.facing = nbttagcompound.getShort("facing");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setDouble("energy", this.energy);
		nbttagcompound.setShort("facing", this.facing);
	}

	/**
	 * validates a tile entity
	 */
	@Override
	public void validate() {
		super.validate();
		IC2.tickHandler.addSingleTickCallback(this.worldObj, new ITickCallback() {
			@SuppressWarnings("unchecked")
			@Override
			public void tickCallback(World world) {
				if (!LightBulbTileEntity.this.isInvalid() && world.blockExists(LightBulbTileEntity.this.xCoord,
						LightBulbTileEntity.this.yCoord, LightBulbTileEntity.this.zCoord)) {
					LightBulbTileEntity.this.onLoaded();

					if (LightBulbTileEntity.this.enableUpdateEntity()) {
						world.loadedTileEntityList.add(LightBulbTileEntity.this);
					}
				}
			}
		});
	}

	/**
	 * invalidates a tile entity
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		if (this.loaded) {
			this.onUnloaded();
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (this.loaded) {
			this.onUnloaded();
		}
	}

	public void onLoaded() {
		if (IC2.platform.isSimulating() && !this.addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
		this.loaded = true;
	}

	public void onUnloaded() {
		if (IC2.platform.isSimulating()) {
			if (this.addedToEnergyNet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				this.addedToEnergyNet = false;
			}
			this.active = false;
			this.updateLightState(true);
		}
	}

	@Override
	public final boolean canUpdate() {
		return false;
	}

	@Override
	public void updateEntity() {
		if (++this.ticker % 4 == 0) {
			if (this.prevFacing != facing) {
				this.setFacing(facing);
			}
			if (this.energy > 0) {
				this.energy--;
				this.setActive(true);
			} else {
				this.setActive(false);
			}
		}
	}

	protected void updateLightState(boolean spreadDarkness) {
		if (IC2.platform.isSimulating()) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public boolean enableUpdateEntity() {
		return IC2.platform.isSimulating();
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		switch (direction) {
		case UP:
			return this.getFacing() == 0;
		case DOWN:
			return this.getFacing() == 1;
		case SOUTH:
			return this.getFacing() == 2;
		case NORTH:
			return this.getFacing() == 3;
		case EAST:
			return this.getFacing() == 4;
		case WEST:
			return this.getFacing() == 5;
		default:
			return false;
		}
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	@Override
	public short getFacing() {
		return this.facing;
	}

	@Override
	public void setFacing(short facing1) {
		if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnergyNet = false;
		}
		this.facing = facing1;
		if (IC2.platform.isSimulating()) {
			if (this.prevFacing != facing) {
				IC2.network.get().updateTileEntityField(this, "facing");
			}
		}
		this.prevFacing = facing;
		if (IC2.platform.isSimulating() && !this.addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
	}

	@Override
	public List<String> getNetworkedFields() {
		Vector<String> ret = new Vector<String>(2);
		ret.add("active");
		ret.add("facing");
		return ret;
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return IHLUtils.getThisModItemStack("lightBulb");
	}

	@Override
	public double getDemandedEnergy() {
		if (energy > 10d) {
			return 0d;
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public int getSinkTier() {
		return 1;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		this.energy += amount;
		return 0.0D;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active1) {
		this.active = active1;

		if (this.prevActive != active1) {
			IC2.network.get().updateTileEntityField(this, "active");
			updateLightState(!active1);
		}
		this.prevActive = active1;
	}

	public void setActiveWithoutNotify(boolean active1) {
		this.active = active1;
		this.prevActive = active1;
	}
}
