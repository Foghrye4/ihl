package ihl.items_blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class IHLItemBlock extends ItemBlock{
	public Map<Integer, String> nameMap = new HashMap<Integer, String>();
	public IHLItemBlock(Block block1) 
	{
	   	super(block1);
	   	this.setFull3D();
		this.setHasSubtypes(true);
	   	this.setCreativeTab(IHLCreativeTab.tab);
	}
	
	 @Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	 {
	        Block block = world.getBlock(x, y, z);

	        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
	        {
	            par7 = 1;
	        }
	        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush)
	        {
	            if (par7 == 0)
	            {
	                --y;
	            }

	            if (par7 == 1)
	            {
	                ++y;
	            }

	            if (par7 == 2)
	            {
	                --z;
	            }

	            if (par7 == 3)
	            {
	                ++z;
	            }

	            if (par7 == 4)
	            {
	                --x;
	            }

	            if (par7 == 5)
	            {
	                ++x;
	            }
	        }

	        if (par1ItemStack.stackSize == 0)
	        {
	            return false;
	        }
	        else if (!player.canPlayerEdit(x, y, z, par7, par1ItemStack))
	        {
	            return false;
	        }
	        else if (y == 255)
	        {
	            return false;
	        }
	        else if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, par7, player, par1ItemStack))
	        {
	            int metadata = par1ItemStack.getItemDamage();
	            int var13 = this.field_150939_a.onBlockPlaced(world, x, y, z, par7, par8, par9, par10, metadata);

	            if (world.setBlock(x, y, z, this.field_150939_a, var13, 3))
	            {
	                if (world.getBlock(x, y, z) == this.field_150939_a)
	                {
	                    this.field_150939_a.onBlockPlacedBy(world, x, y, z, player, par1ItemStack);
	                    this.field_150939_a.onPostBlockPlaced(world, x, y, z, var13);
	                    TileEntity tile=world.getTileEntity(x, y, z);
	                    if(tile instanceof IWrenchable && IC2.platform.isSimulating())
	                    {
	                    	IWrenchable te=(IWrenchable)tile;
	                		int var6 = MathHelper.floor_double(player.rotationPitch * 4.0F / 360.0F + 0.5D) & 3;
	                    	int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
	                    	if(player.isSneaking())
	                    	{
	                        	if(var6==1)
	                        	{
	                        		te.setFacing((short) 0);
	                        	}
	                        	else if(var6==3)
	                        	{
	                        		te.setFacing((short) 1);
	                        	}
	                        	else
	                        	{
	                        		
		                         switch (l)
		                         {
		                             case 0:
		                                 te.setFacing((short)3);
		                                 break;

		                             case 1:
		                                 te.setFacing((short)4);
		                                 break;

		                             case 2:
		                                 te.setFacing((short)2);
		                                 break;

		                             case 3:
		                                 te.setFacing((short)5);
		                         }
	                        	}
	                    	}
	                    	else
	                    	{
	                        	if(var6==1)
	                        	{
	                        		te.setFacing((short) 1);
	                        	}
	                        	else if(var6==3)
	                        	{
	                        		te.setFacing((short) 0);
	                        	}
	                        	else
	                        	{

		                         switch (l)
		                         {
		                             case 0:
		                                 te.setFacing((short)2);
		                                 break;

		                             case 1:
		                                 te.setFacing((short)5);
		                                 break;

		                             case 2:
		                                 te.setFacing((short)3);
		                                 break;

		                             case 3:
		                                 te.setFacing((short)4);
		                         }	                    	}
	                    	}
	                    	}
	                }

	                world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "step.stone", 1.0F, 0.8F);
	                --par1ItemStack.stackSize;
	            }

	            return true;
	        }
	        else
	        {
	            return false;
	        }
	 }
	
		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister par1IconRegister) {
		   	this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":driverItem");
		}
	 
		public Block getBlockContained()
		{
			return this.field_150939_a;
		}
	
    @Override
    @SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab()
    {
        return this.field_150939_a.getCreativeTabToDisplayOn();
    }
    
	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
		if(!nameMap.isEmpty() && nameMap.containsKey(stack.getItemDamage()))
		{
			return nameMap.get(stack.getItemDamage());
		}
		else
		{
			return this.field_150939_a.getUnlocalizedName();
		}
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag)
    {
        if(itemStack.stackTagCompound!=null && itemStack.stackTagCompound.hasKey("resultSuffix"))
        {
        	String result_suffix = itemStack.stackTagCompound.getString("resultSuffix");
        	if(StatCollector.canTranslate("ihl."+result_suffix))
        	{
        		result_suffix = StatCollector.translateToLocal("ihl."+result_suffix);
        	}
        	info.add(StatCollector.translateToLocal("result_of_molding")+result_suffix);
        	if(itemStack.stackTagCompound.hasKey("isContainStearin") && itemStack.stackTagCompound.getBoolean("isContainStearin"))
        	{
            	info.add(StatCollector.translateToLocal("ihl.tooltip.step")+" 1: "+StatCollector.translateToLocal("remove_wax_using_muffle_furnace"));
            	info.add(StatCollector.translateToLocal("ihl.tooltip.step")+" 2: "+StatCollector.translateToLocal("fill_from_top_with_molten_metal"));
            	info.add(StatCollector.translateToLocal("ihl.tooltip.step")+" 3: "+StatCollector.translateToLocal("wait_for_10_seconds"));
            	info.add(StatCollector.translateToLocal("ihl.tooltip.step")+" 4: "+StatCollector.translateToLocal("destroy_mold_to_get_results"));
        		
        	}
        	else
        	{
            	info.add(StatCollector.translateToLocal("ihl.tooltip.step")+" 1: "+StatCollector.translateToLocal("fill_from_top_with_molten_metal"));
            	info.add(StatCollector.translateToLocal("ihl.tooltip.step")+" 2: "+StatCollector.translateToLocal("wait_for_10_seconds"));
            	info.add(StatCollector.translateToLocal("ihl.tooltip.step")+" 3: "+StatCollector.translateToLocal("destroy_mold_to_get_results"));
        	}
            if(itemStack.stackTagCompound.hasKey("detonator_delay"))
            {
            	info.add(StatCollector.translateToLocal("ihl.detonator_delay")+" "+itemStack.stackTagCompound.getInteger("detonator_delay")+StatCollector.translateToLocal("ihl.seconds"));
            }
        }
    }
}
