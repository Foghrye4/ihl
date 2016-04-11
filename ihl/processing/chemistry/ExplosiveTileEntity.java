package ihl.processing.chemistry;

import ic2.core.ExplosionIC2;
import ic2.core.block.EntityIC2Explosive;
import ihl.utils.EntityIHLExplosion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ExplosiveTileEntity extends TileEntity {
	
	public int explosionPower=10000;
	public int explosionType=1;

	public void createExplosion(EntityPlayer player) 
	{
		switch(this.explosionType)
		{
			case 0:
		        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, new EntityIC2Explosive(worldObj), xCoord+0.5d, yCoord+0.5d, zCoord+0.5d, this.explosionPower/1000f, 0f, ExplosionIC2.Type.Normal,player, 0);
		        System.out.println("Start");
		        explosion.doExplosion();
		        System.out.println("End");
		        break;
			case 1:
				EntityIHLExplosion ex1 = new EntityIHLExplosion(worldObj, xCoord, yCoord, zCoord,this.explosionPower);
				worldObj.spawnEntityInWorld(ex1);
		        break;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt!=null)
		{
			explosionPower=nbt.getInteger("explosionPower");
			explosionType=nbt.getInteger("explosionType");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("explosionPower",explosionPower);
		nbt.setInteger("explosionType",explosionType);
	}

}
