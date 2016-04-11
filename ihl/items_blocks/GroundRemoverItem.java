package ihl.items_blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.worldgen.ores.BlockOre;

public class GroundRemoverItem extends Item{
    
	private String registryName;
	private final Set<Block> removableBlockSet = new HashSet();

	public GroundRemoverItem(String registryName1) 
	{
		super();
		this.registryName=registryName1;
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setMaxStackSize(1);
		this.setUnlocalizedName(registryName);
		removableBlockSet.add(Blocks.sandstone);
		removableBlockSet.add(Blocks.sand);
		removableBlockSet.add(Blocks.stone);
		removableBlockSet.add(Blocks.flowing_water);
		removableBlockSet.add(Blocks.flowing_lava);
		removableBlockSet.add(Blocks.water);
		removableBlockSet.add(Blocks.lava);
		removableBlockSet.add(Blocks.clay);
		removableBlockSet.add(Blocks.gravel);
		removableBlockSet.add(Blocks.dirt);
	}
	
	public GroundRemoverItem()
	{
		super();
	}
			
	public static void init()
	{
		//GameRegistry.registerItem(new GroundRemoverItem("groundRemover"),"groundRemover");
	}
	
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
    	info.add("Vanilla block remover tool");
    }
	@Override
   	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":vacuumSwitch");
    }
	
    @Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem)
    {
    	if(entity instanceof EntityPlayer && isCurrentItem && MinecraftServer.getServer().getTickCounter()%100==99)
    	{
    		int x = (int)entity.posX;
    		int y = (int)entity.posY;
    		int z = (int)entity.posZ;
    		for(int ix = x-16;ix < x+16;ix++)
    		{
        		for(int iz = z-16;iz < z+16;iz++)
        		{
            		for(int iy = 4;iy < y;iy++)
            		{
            			if(!(world.getBlock(ix, iy, iz) instanceof BlockOre || world.getBlock(ix, iy, iz) instanceof IHLFluidBlock))
            			{
            				world.setBlockToAir(ix, iy, iz);
            			}
            		}
        		}
    		}
    	}
    }
}
