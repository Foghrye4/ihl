package ihl.collector;

import ic2.api.item.ElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CollectorHeavyItem extends CollectorItem {

	public CollectorHeavyItem()
    {
        super();
        this.tier=2;
        this.maxCharge=200000;
    }
	
	@Override
	public boolean spawnEntityInWorld(World world, ItemStack itemStack,int x, int  y ,int z)
    {
		CollectorHeavyEntity se = new CollectorHeavyEntity(world,x, y+1, z);
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
}
