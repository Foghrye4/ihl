package ihl.i_hate_liquids;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class IHLEventHandler {

	public IHLEventHandler() {}
	
	
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) 
	{
		World world = event.world;
		if(!world.isRemote)
		{
			int x = event.x;
			int y = event.y;
			int z = event.z;
			Block block = world.getBlock(x, y+1, z);
			if(block.getMaterial().isLiquid())
			{
				InvisibleMagicanEntity im = new InvisibleMagicanEntity(world, x, y, z);
				world.spawnEntityInWorld(im);
				return;
			}
			block = world.getBlock(x+1, y, z);
			if(block.getMaterial().isLiquid())
			{
				InvisibleMagicanEntity im = new InvisibleMagicanEntity(world, x, y, z);
				world.spawnEntityInWorld(im);
				return;
			}
			block = world.getBlock(x-1, y, z);
			if(block.getMaterial().isLiquid())
			{
				InvisibleMagicanEntity im = new InvisibleMagicanEntity(world, x, y, z);
				world.spawnEntityInWorld(im);
				return;
			}
			block = world.getBlock(x, y, z+1);
			if(block.getMaterial().isLiquid())
			{
				InvisibleMagicanEntity im = new InvisibleMagicanEntity(world, x, y, z);
				world.spawnEntityInWorld(im);
				return;
			}
			block = world.getBlock(x, y, z-1);
			if(block.getMaterial().isLiquid())
			{
				InvisibleMagicanEntity im = new InvisibleMagicanEntity(world, x, y, z);
				world.spawnEntityInWorld(im);
				return;
			}
		}
	}
}
