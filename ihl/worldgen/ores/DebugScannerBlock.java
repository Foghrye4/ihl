package ihl.worldgen.ores;

import ihl.IHLCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DebugScannerBlock extends Block implements ITileEntityProvider{
	
		public DebugScannerBlock(Material material) 
		{
			super(material);
	        this.setCreativeTab(IHLCreativeTab.tab);
		}

		@Override
		public TileEntity createNewTileEntity(World world, int var2) {
			return new DebugScannerTileEntity();
		}
		
		@Override
		public boolean hasTileEntity(int metadata)
		{
		    return true;
		}
		
		@Override
		public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer entityPlayer,int i,float pos_x,float pos_y,float pos_z){
			TileEntity te = world.getTileEntity(x,y,z);
			if(te instanceof DebugScannerTileEntity)
	        	{
				DebugScannerTileEntity bte = (DebugScannerTileEntity)te;
					if (bte == null || entityPlayer.isSneaking()) {
	        			return false;
	        		}
	        		else
	        		{
	        			return bte.getGui(entityPlayer);
	        		}
	        	}
			 return false;
		}
}
