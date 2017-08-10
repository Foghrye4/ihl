package ihl.trans_dimensional_item_teleporter;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.util.StackUtil;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class TDITFrequencyTransmitter extends Item implements IItemHudInfo {

	IIcon textureDamaged;
	
	public TDITFrequencyTransmitter()
	{
		super();
		this.setCreativeTab(IHLCreativeTab.tab);
		this.maxStackSize=1;
        this.setMaxDamage(0);
	}
	
    @Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if (IC2.platform.isSimulating())
        {
            if (itemstack.getItemDamage() == 0)
            {
                NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);

                if (nbtData.getBoolean("targetSet"))
                {
                    nbtData.setBoolean("targetSet", false);
                    IC2.platform.messagePlayer(entityplayer, "TDIT Frequency Transmitter unlinked", new Object[0]);
                }
            }
            else
            {
                itemstack.setItemDamage(0);
            }
        }

        return itemstack;
    }
	
    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityPlayer, World world, int x, int y, int z, int l, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        
        if (tileEntity instanceof TDITTileEntity && IC2.platform.isSimulating())
        {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
            boolean targetSet = nbtData.getBoolean("targetSet");
            int dimesionID = nbtData.getInteger("dimesionID");
            int targetX = nbtData.getInteger("targetX");
            int targetY = nbtData.getInteger("targetY");
            int targetZ = nbtData.getInteger("targetZ");
            TDITTileEntity tp = (TDITTileEntity)tileEntity;
            
            if (!targetSet)
            {
                targetSet = true;
                dimesionID = world.provider.dimensionId;
                targetX = tp.xCoord;
                targetY = tp.yCoord;
                targetZ = tp.zCoord;
                IC2.platform.messagePlayer(entityPlayer, "TDIT Frequency Transmitter linked to TDIT.", new Object[0]);
            }
            else if (tp.xCoord == targetX && tp.yCoord == targetY && tp.zCoord == targetZ)
            {
                IC2.platform.messagePlayer(entityPlayer, "Can\'t link TDIT to itself.", new Object[0]);
            }
            else if (tp.targetSet && tp.targetX == targetX && tp.targetY == targetY && tp.targetZ == targetZ)
            {
                IC2.platform.messagePlayer(entityPlayer, "TDIT link unchanged.", new Object[0]);
            }
            else
            {
                tp.setTarget(targetX, targetY, targetZ, dimesionID);
                
                TileEntity te1 = MinecraftServer.getServer().worldServerForDimension(dimesionID).getTileEntity(targetX, targetY, targetZ);

                if (te1 instanceof TDITTileEntity)
                {
                	TDITTileEntity tp21 = (TDITTileEntity)te1;

                    if (!tp21.targetSet)
                    {
                        tp21.setTarget(tp.xCoord, tp.yCoord, tp.zCoord, dimesionID);
                    }
                }

                IC2.platform.messagePlayer(entityPlayer, "Teleportation link established.", new Object[0]);
            }

            nbtData.setBoolean("targetSet", targetSet);
            nbtData.setInteger("dimesionID", dimesionID);
            nbtData.setInteger("targetX", targetX);
            nbtData.setInteger("targetY", targetY);
            nbtData.setInteger("targetZ", targetZ);
            itemstack.setItemDamage(1);
            return false;
        }
        else
        {
            return false;
        }
    }
	
	@Override
	public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        if(itemStack.stackTagCompound!=null)
        {
        	NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        	boolean targetSet = nbtData.getBoolean("targetSet");
        	if(targetSet)
        	{
        		int dimesionID = nbtData.getInteger("dimesionID");
            	int targetX = nbtData.getInteger("targetX");
            	int targetY = nbtData.getInteger("targetY");
            	int targetZ = nbtData.getInteger("targetZ");
            	info.add("Dimesion ID: " + dimesionID);
            	info.add("X coord: " + targetX);
            	info.add("Y coord: " + targetY);
            	info.add("Z coord: " + targetZ);
        	}
        	else
        	{
        		info.add("Memory clean");
        	}
        }
        return info;
	}
	
    @Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
    	this.itemIcon=iconRegister.registerIcon(IHLModInfo.MODID + ":itemTDITFT");
    	this.textureDamaged=iconRegister.registerIcon(IHLModInfo.MODID + ":itemTDITFT_1");
    }

    @Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
    	switch(meta)
    	{
    	case 0:
   			return this.itemIcon;
    	case 1:
   			return this.textureDamaged;
   		default:
   			return this.itemIcon;
    	}
    }
}
