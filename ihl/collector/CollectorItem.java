package ihl.collector;

import java.util.LinkedList;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;

public class CollectorItem extends Item implements IElectricItem, IItemHudInfo {

    protected int tier=1;
    protected int maxCharge=20000;
	protected int minCharge=100;
	private int transferLimit = 100;

	public CollectorItem()
    {
        super();
        this.setMaxDamage(27);
	   	this.setFull3D();
        this.setCreativeTab(IHLCreativeTab.tab);
        this.maxStackSize=1;
    }
	
    @Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
    	if(ElectricItem.manager.use(itemStack, this.minCharge, entityPlayer))
    	{
    		if(!world.isRemote)
    		{
    			MovingObjectPosition mop = this.returnMOPFromPlayer(entityPlayer, world);
    			if(mop!=null)
    			{
    				int x = mop.blockX;
    				int y = mop.blockY;
    				int z = mop.blockZ;
    				if(this.spawnEntityInWorld(world, itemStack, x,y,z))
    				{
    					itemStack.stackSize--;
					}
    			}
    		}
    	}
		return itemStack;
    }

	public boolean spawnEntityInWorld(World world, ItemStack itemStack,int x, int  y ,int z)
    {
		CollectorEntity se = new CollectorEntity(world,x+0.5D, y+1D, z+0.5D);
		se.setEnergy(ElectricItem.manager.getCharge(itemStack), this.getMaxCharge(itemStack));
    	if(itemStack.stackTagCompound!=null)
    	{
    		se.hopperx = itemStack.stackTagCompound.getInteger("hopperx");
    		se.hoppery = itemStack.stackTagCompound.getInteger("hoppery");
    		se.hopperz = itemStack.stackTagCompound.getInteger("hopperz");
    	}
    	else
    	{
    		se.hopperx = x;
    		se.hoppery = y;
    		se.hopperz = z;
    	}
		return world.spawnEntityInWorld(se);
    }

	@Override
   	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":itemElectricHandpump");
    }
	

    @Override
	public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2)
    {
        return false;
    }
    
    
    public MovingObjectPosition returnMOPFromPlayer(EntityPlayer entityplayer, World world)
    {
        float f = 1.0F;
        float f1 = entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * f;
        float f2 = entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f;
        double d = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * f;
        double d1 = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * f + entityplayer.getEyeHeight();

        if (world.isRemote)
        {
            d1 -= entityplayer.getDefaultEyeHeight();
        }

        double d2 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * f;
        Vec3 vec3d = Vec3.createVectorHelper(d, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.01745329F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.01745329F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.01745329F);
        float f6 = MathHelper.sin(-f1 * 0.01745329F);
        float f7 = f4 * f5;
        float f9 = f3 * f5;
        double d3 = 5.0D;
        Vec3 vec3d1 = vec3d.addVector(f7 * d3, f6 * d3, f9 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec3d, vec3d1, true);

        if (movingobjectposition == null)
        {
            return null;
        }

        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            return movingobjectposition;
        }
        return null;
    }
    

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public Item getChargedItem(ItemStack itemStack) {
		return this;
	}

	@Override
	public Item getEmptyItem(ItemStack itemStack) {
		return this;
	}

	@Override
	public double getMaxCharge(ItemStack itemStack) {
		return this.maxCharge;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return this.tier;
	}

	@Override
	public double getTransferLimit(ItemStack itemStack) {
		return this.transferLimit;
	}

	@Override
	public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList info = new LinkedList();
        info.add(ElectricItem.manager.getToolTip(itemStack));
        info.add("Power Tier: " + this.tier);
        return info;
	}
	
    @Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b)
    {
        info.add("PowerTier: " + this.tier);
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList)
    {
        itemList.add(this.getItemStack(this.maxCharge));
        itemList.add(this.getItemStack(0));
    }
    
    public ItemStack getItemStack(int charge)
    {
        ItemStack ret = new ItemStack(this);
        ElectricItem.manager.charge(ret, charge, this.maxCharge, true, false);
        return ret;
    }


    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (this.maxCharge-ElectricItem.manager.getCharge(stack))/this.maxCharge;
    }
   
    
}
