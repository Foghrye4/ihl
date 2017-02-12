package ihl.items_blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class IHLBucketHandler 
{
	public IHLBucketHandler() {}
	@SubscribeEvent
    public void onBucketFill(FillBucketEvent event)
    {
        Block block = event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);
        if (block instanceof IHLFluidBlock && event.isCancelable())
        {
            event.setCanceled(true);
        }
    }

}
