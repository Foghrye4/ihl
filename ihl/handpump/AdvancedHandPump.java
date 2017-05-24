package ihl.handpump;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.core.Ic2Items;
import ic2.core.util.LiquidUtil;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class AdvancedHandPump extends IHLHandPump {
	
	public AdvancedHandPump()
    {
        super();
        this.maxCharge=IHLMod.config.advancedHandpumpMaxCharge;
        this.operationEUCost=IHLMod.config.advancedHandpumpOperationEUCost;
        this.tier=IHLMod.config.advancedHandpumpTier;
    }
    
	@Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
    	MovingObjectPosition movingobjectposition = IHLUtils.returnMOPFromPlayer(entityPlayer, world);
    	if(movingobjectposition!=null)
    	{
    		int x=movingobjectposition.blockX;
    		int y=movingobjectposition.blockY;
    		int z=movingobjectposition.blockZ;
    		Block block = world.getBlock(x, y, z);
    		if(!block.getMaterial().isLiquid())
    		{
        		y++;
        		block = world.getBlock(x, y, z);
        		if(!block.getMaterial().isLiquid())
        		{
        			return itemStack;
        		}
    		}
			if(world.isRemote && ElectricItem.manager.use(itemStack, this.operationEUCost, entityPlayer))
			{
				entityPlayer.playSound(IHLModInfo.MODID+":handpumpOne", (float) (Math.random()*0.2D+0.6D), (float) (Math.random()*0.1D+0.9D));
    			return itemStack;
			}
			else
    		{
    		BlockWithCoordinates blockXYZ = this.searchSource(world, x, y, z);
    		if(blockXYZ==null)
    		{
    			return itemStack;
    		}
    		block = blockXYZ.block;
    		FluidStack fluid1 = null;
    		if(block instanceof BlockFluidBase)
    		{
    			fluid1 = ((BlockFluidBase)block).drain(world, blockXYZ.x, blockXYZ.y, blockXYZ.z, false);
    		}
    		else if(block==Blocks.water||block==Blocks.flowing_water)
        	{
    			fluid1 = new FluidStack(FluidRegistry.WATER, 1000);
        	}
        	else if(block==Blocks.lava||block==Blocks.flowing_lava)
        	{
    			fluid1 = new FluidStack(FluidRegistry.LAVA, 1000);
        	}
    		int amount2=0;
    		ItemStack stack = this.getItemStackContains(fluid1, entityPlayer);
            if(stack!=null && fluid1 != null)
            {
            	amount2 = LiquidUtil.fillContainerStack(stack, entityPlayer, fluid1, true);
            }
        		if(amount2 >0)
        		{
        			if(ElectricItem.manager.use(itemStack, this.operationEUCost, entityPlayer))
        			{
        				fluid1.amount=Integer.MAX_VALUE;
        				int countFreeAmount = this.countAmountOfEmptyFluidCells(Ic2Items.FluidCell, fluid1, entityPlayer);
        				Set<BlockWithCoordinates> blockXYZSet = this.searchSourcesAndDestroyThem(world, blockXYZ, countFreeAmount/1000);
        				int countDestroedSources = blockXYZSet.size();
        				this.destroyAllSources(world, blockXYZSet);
        				ElectricItem.manager.discharge(itemStack,this.operationEUCost,1,false,false, false);
       					this.fillUniversalFluidCells(fluid1, entityPlayer,countDestroedSources);
           				entityPlayer.inventoryContainer.detectAndSendChanges();
        			}
        			return itemStack;
        		}
        		else if(entityPlayer.inventory.hasItemStack(IC2Items.getItem("cell")))
        		{
        			if(ElectricItem.manager.use(itemStack, this.operationEUCost, entityPlayer))
        			{
        				int countCells = this.countEmptyCells(IC2Items.getItem("cell"), entityPlayer);
        				Set<BlockWithCoordinates> blockXYZSet = this.searchSourcesAndDestroyThem(world, blockXYZ, countCells);
        				int countDestroedSources = blockXYZSet.size();
        				this.destroyAllSources(world, blockXYZSet);
        				ElectricItem.manager.discharge(itemStack,this.operationEUCost,1,false,false, false);
						this.fillFluidCells(fluid1, entityPlayer, countDestroedSources);
						entityPlayer.inventoryContainer.detectAndSendChanges();
            			return itemStack;
        			}
        		}
    	}
    	}
		return itemStack;
    }

    private void destroyAllSources(World world,Set<BlockWithCoordinates> blockXYZSet) 
    {
    	Iterator<BlockWithCoordinates> iterator = blockXYZSet.iterator();
    	while(iterator.hasNext())
    	{
    		BlockWithCoordinates blockXYZ = iterator.next();
    		blockXYZ.setMetadataOrDestroyBlock(world, 1, 7);
    	}
    }

	private void fillUniversalFluidCells(FluidStack fluid1, EntityPlayer entityPlayer,
			int countDestroedSources) {
    	fluid1.amount=countDestroedSources*1000;
    	//System.out.println("countDestroedSources="+countDestroedSources);
    	//System.out.println("fluidStackAmount="+fluid1.amount);
       	a:for (int i=0;i<entityPlayer.inventory.getSizeInventory();i++)
    	{
    		if(entityPlayer.inventory.getStackInSlot(i)!=null)
    		{
    			if(entityPlayer.inventory.getStackInSlot(i).getItem() instanceof IFluidContainerItem)
    			{
    				int amount2 = LiquidUtil.fillContainerStack(entityPlayer.inventory.getStackInSlot(i), entityPlayer, fluid1, false);
    				fluid1.amount-=amount2;
    				while(fluid1.amount>=1000 && entityPlayer.inventory.getStackInSlot(i).stackSize>=1 && amount2>0)
    				{
    					amount2 = LiquidUtil.fillContainerStack(entityPlayer.inventory.getStackInSlot(i), entityPlayer, fluid1, false);
    					fluid1.amount-=amount2;
    				}
    				if(fluid1.amount<1000)
    				{
    					break a;
    				}
    			}
    		}
    	}
	}

	private void fillFluidCells(FluidStack fluid1, EntityPlayer player, int countDestroedSources) {
    	ItemStack cell = IHLFluid.getCell(fluid1.getFluid().getName());
    	if(cell==null)
    	{
    		return;
    	}
		ItemStack emptyCells = IC2Items.getItem("cell").copy();
		emptyCells.stackSize=player.inventory.clearInventory(IC2Items.getItem("cell").getItem(),0)-countDestroedSources;
		cell.stackSize=countDestroedSources;
		IHLUtils.addItemStackToInventory(player, cell);
		if(emptyCells.stackSize>0)
		{
			IHLUtils.addItemStackToInventory(player, emptyCells);
		}
	}

	private Set<BlockWithCoordinates> searchSourcesAndDestroyThem(World world, BlockWithCoordinates blockXYZ, int countCells) 
	{
		int startx = blockXYZ.x;
		int starty = blockXYZ.y;
		int startz = blockXYZ.z;
		int currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
		HashSet<BlockWithCoordinates> outputSet = new HashSet<BlockWithCoordinates>();
		for (int i=0; i<256;i++)
			{
				if(getFlowDecay(world, blockXYZ, startx, starty+1, startz)>=0)
				{
					starty++;
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, blockXYZ, startx+1, starty+1, startz)>=0)
				{
					starty++;
					startx++;						
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, blockXYZ, startx-1, starty+1, startz)>=0)
				{
					starty++;
					startx--;						
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, blockXYZ, startx, starty+1, startz+1)>=0)
				{
					starty++;
					startz++;						
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, blockXYZ, startx, starty+1, startz-1)>=0)
				{
					starty++;
					startz--;						
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				//Start checking neighbor blocks to lower flow decay.
				else if(getFlowDecay(world, blockXYZ, startx-1, starty, startz)<currentFlowDecay&&getFlowDecay(world, startx-1, starty, startz)!=-1)
				{
					startx--;
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				else if(getFlowDecay(world, blockXYZ, startx, starty, startz+1)<currentFlowDecay&&getFlowDecay(world, startx, starty, startz+1)!=-1)
				{
					startz++;
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				else if(getFlowDecay(world, blockXYZ, startx, starty, startz-1)<currentFlowDecay&&getFlowDecay(world, startx, starty, startz-1)!=-1)
				{
					startz--;
					currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				else if(getFlowDecay(world, blockXYZ, startx+1, starty, startz)<currentFlowDecay&&getFlowDecay(world, startx+1, starty, startz)!=-1)
				{
				startx++;
				currentFlowDecay=getFlowDecay(world, blockXYZ, startx, starty, startz);
				}
				else {break;}
			}
		List<XYZ> xyzlist = new ArrayList<XYZ>();
		if(currentFlowDecay==0)
		{
			xyzlist.add(new XYZ(startx, starty, startz));
			outputSet.add(new BlockWithCoordinates(world.getBlock(startx, starty, startz), startx, starty, startz,0));
			int listPos=0;
			for(int i=0;i<=countCells;i++)
			{
				if(getFlowDecay(world, blockXYZ, startx-1, starty, startz)==0 && !xyzlist.contains(new XYZ(startx-1, starty, startz)))
				{
					startx--;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					outputSet.add(new BlockWithCoordinates(world.getBlock(startx, starty, startz), startx, starty, startz,0));
				}
				else if(getFlowDecay(world, blockXYZ, startx, starty, startz+1)==0 && !xyzlist.contains(new XYZ(startx, starty, startz+1)))
				{
					startz++;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					outputSet.add(new BlockWithCoordinates(world.getBlock(startx, starty, startz), startx, starty, startz,0));
				}
				else if(getFlowDecay(world, blockXYZ, startx, starty, startz-1)==0 && !xyzlist.contains(new XYZ(startx, starty, startz-1)))
				{
					startz--;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					outputSet.add(new BlockWithCoordinates(world.getBlock(startx, starty, startz), startx, starty, startz,0));
				}
				else if(getFlowDecay(world, blockXYZ, startx+1, starty, startz)==0 && !xyzlist.contains(new XYZ(startx+1, starty, startz)))
				{
					startx++;
					xyzlist.add(new XYZ(startx, starty, startz));
					listPos=xyzlist.size()-1;
					outputSet.add(new BlockWithCoordinates(world.getBlock(startx, starty, startz), startx, starty, startz,0));
				}
				else 
				{
					if(listPos>0)
					{
						listPos--;
						XYZ xyz = xyzlist.get(listPos);
						startx=xyz.x;
						starty=xyz.y;
						startz=xyz.z;
					}
				}
			}
		}
		return outputSet;	
	}

	private int countEmptyCells(ItemStack fluidCell, EntityPlayer player) {
		int num=0;
    	ItemStack[] inv = player.inventory.mainInventory;
       	for (int i=0;i<=35;i++)
    	{
    		if(inv[i]!=null)
    		{
    			if(inv[i].getItem() == fluidCell.getItem())
    			{
    				if(FluidContainerRegistry.isEmptyContainer(inv[i]))
    				{
    					num+=inv[i].stackSize;
    			    }
    			}
    		}
    	}
       	return num;
	}
	
	private int countAmountOfEmptyFluidCells(ItemStack fluidCell, FluidStack fluid, EntityPlayer entityPlayer) {
		int num=0;
    	for (int i=0;i<entityPlayer.inventory.getSizeInventory();i++)
    	{
    		if(entityPlayer.inventory.getStackInSlot(i)!=null)
    		{
    			if(entityPlayer.inventory.getStackInSlot(i).getItem() instanceof IFluidContainerItem)
    			{
    				int amount = LiquidUtil.fillContainerStack(entityPlayer.inventory.getStackInSlot(i), entityPlayer, fluid, true)*entityPlayer.inventory.getStackInSlot(i).stackSize;
    				num+=amount;
    			}
    		}
    	}
       	return num;
	}
	
	@Override
   	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":itemAdvancedElectricHandpump");
    }
	
    protected int getFlowDecay(World world, BlockWithCoordinates blockXYZ, int x, int y, int z)
    {
    	if(blockXYZ.isSameTypeBlock(world,x,y,z))
    	{
    		return world.getBlockMetadata(x, y, z);
    	}
    	else
    	{
    		return -1;
    	}
    }
	
}
