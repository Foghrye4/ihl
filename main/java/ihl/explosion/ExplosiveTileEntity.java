package ihl.explosion;

import ic2.core.IC2;
import ihl.IHLMod;
import ihl.utils.IHLMathUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;

public class ExplosiveTileEntity extends TileEntity {

	public int explosionPower = 10000;
	private int explosionType = 1;
	private int detonator_delay = 1;
	private int burn_time = 0;
	public boolean is_detonator_burning = false;

	public void createExplosion(EntityPlayer player) {
		this.invalidate();
		int power = this.explosionPower;
		this.explosionPower = 0;
		IHLMod.explosionHandler.doExplosion(worldObj, xCoord, yCoord, zCoord, IHLMod.explosionHandler.startVectors,
				power);
		IHLMod.proxy.createExplosionEffectFromServer(worldObj, xCoord, yCoord, zCoord,
				32f * IHLMathUtils.sqrt(power / 16384f));
	}

	@Override
	public boolean canUpdate() {
		return IC2.platform.isSimulating();
	}

	@Override
	public void updateEntity() {
		if (is_detonator_burning) {
			IHLMod.proxy.spawnParticleFromServer(4, worldObj, xCoord + 0.5d, yCoord + 1d, zCoord + 0.5d, 0d, 0.2d, 0d,
					1f);
			if (this.burn_time++ > detonator_delay * 20) {
				this.createExplosion(null);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt != null) {
			explosionPower = nbt.getInteger("explosionPower");
			explosionType = nbt.getInteger("explosionType");
			detonator_delay = nbt.getInteger("detonator_delay");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("explosionPower", explosionPower);
		nbt.setInteger("explosionType", explosionType);
		nbt.setInteger("detonator_delay", detonator_delay);
	}

	public boolean ignite(EntityPlayer player) {
		if (player != null && player.getCurrentEquippedItem() != null) {
			for (int oid : OreDictionary.getOreIDs(player.getCurrentEquippedItem())) {
				if (OreDictionary.getOreName(oid).matches("toolLighter")) {
					ignite();
					return false;
				}
			}
		}
		return true;
	}

	public void ignite() {
		IHLMod.proxy.playSoundEffectFromServer(0, this.worldObj, this.xCoord + 0.5D, this.yCoord + 0.5D,
				this.zCoord + 0.5D, 10.0F, 1.0F);
		this.is_detonator_burning = true;
	}
}
