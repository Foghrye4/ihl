package ihl.nei_integration;

import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import ihl.flexible_cable.RectifierTransformerUnitTileEntity;

import java.util.List;

public class IHLBlockHighlightHandler implements IHighlightHandler
{
    @Override
    public List<String> handleTextData(ItemStack stack, World world, EntityPlayer player, MovingObjectPosition movingObjectPosition, List<String> currenttip, ItemInfo.Layout layout) 
    {
        TileEntity tile = world.getTileEntity(movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ);
        if(tile instanceof RectifierTransformerUnitTileEntity)
        {
        	RectifierTransformerUnitTileEntity rtu = (RectifierTransformerUnitTileEntity)tile;
            currenttip.add("Current mode: "+EnumChatFormatting.RED+getMode(rtu.mode)+"x V");
            currenttip.add("R.Click: "+EnumChatFormatting.RED+getMode(Math.min(rtu.mode*RectifierTransformerUnitTileEntity.modeMultiplier,RectifierTransformerUnitTileEntity.modeMax))+"x V");
            currenttip.add("IC2 Mode+R.Click: "+EnumChatFormatting.RED+getMode(Math.max(rtu.mode/RectifierTransformerUnitTileEntity.modeMultiplier,RectifierTransformerUnitTileEntity.modeMin))+"x V");
        }
        return currenttip;
    }

    private String getMode(float value)
    {
    	if(value>=1f)
    	{
    		return Integer.toString(Math.round(value));
    	}
    	else
    	{
    		return "1/"+Integer.toString(Math.round(1f/value));
    	}
    }
    @Override
    public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
        return null;
    }
}
