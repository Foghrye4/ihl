package ihl.handpump;

import java.util.LinkedList;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IItemHudInfo;
import ic2.core.item.resources.ItemCell;
import ic2.core.util.LiquidUtil;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.IHLMod;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;

public class IHLHandPump extends Item implements IElectricItem, IBoxable, IItemHudInfo {

    protected int tier=1;
	protected int maxCharge=30000;
	protected int operationEUCost=600;
	protected int transferLimit = 2000;
		
	public IHLHandPump()
    {
        super();
        this.setMaxDamage(27);
        this.maxCharge=IHLMod.config.handpumpMaxCharge;
        this.operationEUCost=IHLMod.config.handpumpOperationEUCost;
        this.tier=IHLMod.config.handpumpTier;
        this.setCreativeTab(IHLCreativeTab.tab);
        this.maxStackSize=1;
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
    		else if(blockXYZ.isWaterBlock())
        	{
    			fluid1 = new FluidStack(FluidRegistry.WATER, 1000);
        	}
        	else if(blockXYZ.isLavaBlock())
        	{
    			fluid1 = new FluidStack(FluidRegistry.LAVA, 1000);
        	}
    		int amount2=0;
    		ItemStack stack = this.getItemStackContains(fluid1, entityPlayer);
            if(stack!=null && fluid1 != null)
            {
            	amount2 = LiquidUtil.fillContainerStack(stack, entityPlayer, fluid1, true);
            }
        		if(amount2 > 0)
        		{
        			if(ElectricItem.manager.use(itemStack, this.operationEUCost, entityPlayer) && blockXYZ.setMetadataOrDestroyBlock(world, blockXYZ.meta+1,7))
        			{
        				ElectricItem.manager.discharge(itemStack,this.operationEUCost,1,false,false, false);
                        LiquidUtil.fillContainerStack(stack, entityPlayer, fluid1, false);
           				entityPlayer.inventoryContainer.detectAndSendChanges();
        			}
        			return itemStack;
        		}
        		else if(entityPlayer.inventory.hasItemStack(IC2Items.getItem("cell")))
        		{
        			if(ElectricItem.manager.use(itemStack, this.operationEUCost, entityPlayer) && blockXYZ.setMetadataOrDestroyBlock(world, blockXYZ.meta+1,7))
        			{
        				ElectricItem.manager.discharge(itemStack,this.operationEUCost,1,false,false, false);
						this.fillFluidCell(fluid1, entityPlayer);
						entityPlayer.inventoryContainer.detectAndSendChanges();
            			return itemStack;
        			}
        		}
    		}
    	}
		return itemStack;
    }


   	protected ItemStack getItemStackContains(FluidStack fluidStack, EntityPlayer player) {
       	for (int i=0;i<player.inventory.getSizeInventory();i++)
    	{
    		if(player.inventory.getStackInSlot(i)!=null)
    		{
    			if(player.inventory.getStackInSlot(i).getItem() instanceof IFluidContainerItem)
    			{
    				if(LiquidUtil.fillContainerStack(player.inventory.getStackInSlot(i), player, fluidStack, true) > 0)
    				{
        				return player.inventory.getStackInSlot(i);
    				}
    			}
    		}
    	}
		return null;
	}

	@Override
   	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":itemElectricHandpump");
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
	public double getTransferLimit(ItemStack itemStack) {
		return this.transferLimit;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return this.tier;
	}

	@Override
	public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(itemStack));
        info.add("Power Tier: " + this.tier);
        return info;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b)
    {
        info.add("PowerTier: " + this.tier);
    }

	@Override
	public boolean canBeStoredInToolbox(ItemStack itemstack) {
		return true;
	}

    @Override
	public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2)
    {
        return false;
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
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
    
    protected void fillFluidCell(FluidStack fluid1, EntityPlayer player)
    {
    	ItemStack cell = IHLFluid.getCell(fluid1.getFluid().getName());
    	if(cell==null)
    	{
    		return;
    	}
    	ItemStack[] inv = player.inventory.mainInventory;
       	for (int i=0;i<=35;i++)
    	{
    		if(inv[i]!=null)
    		{
    			if(inv[i].getItem() instanceof ItemCell)
    			{
    				if(inv[i].getItemDamage()==0)
    				{
    				    if (IHLUtils.addItemStackToInventory(player, cell))
    				    {
        					if(inv[i].stackSize<=1)
        					{
        						ItemStack sourceItemStack = inv[i].copy();
        						sourceItemStack.stackSize=player.inventory.clearInventory(sourceItemStack.getItem(),0)-1;
        						if(sourceItemStack.stackSize>0)
        						{
        							player.inventory.addItemStackToInventory(sourceItemStack);
        						}
        					}
        					else
        					{
        						inv[i].stackSize--;
        					}
    				        return;
    			        }
    			    }
    			}
    		}
    	}
    }
   
    protected BlockWithCoordinates searchSource(World world, int startx, int starty, int startz) 
	{
		int currentFlowDecay=getFlowDecay(world, startx, starty, startz);
		for (int i=0; i<256;i++)
			{
				if(getFlowDecay(world, startx, starty+1, startz)>=0)
				{
					starty++;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx+1, starty+1, startz)>=0)
				{
					starty++;
					startx++;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx-1, starty+1, startz)>=0)
				{
					starty++;
					startx--;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx, starty+1, startz+1)>=0)
				{
					starty++;
					startz++;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				
				else if(getFlowDecay(world, startx, starty+1, startz-1)>=0)
				{
					starty++;
					startz--;						
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				//Start checking neighbor blocks to lower flow decay.
				else if(getFlowDecay(world, startx-1, starty, startz)<currentFlowDecay&&getFlowDecay(world, startx-1, starty, startz)!=-1)
				{
					startx--;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else if(getFlowDecay(world, startx, starty, startz+1)<currentFlowDecay&&getFlowDecay(world, startx, starty, startz+1)!=-1)
				{
					startz++;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else if(getFlowDecay(world, startx, starty, startz-1)<currentFlowDecay&&getFlowDecay(world, startx, starty, startz-1)!=-1)
				{
					startz--;
					currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else if(getFlowDecay(world, startx+1, starty, startz)<currentFlowDecay&&getFlowDecay(world, startx+1, starty, startz)!=-1)
				{
				startx++;
				currentFlowDecay=getFlowDecay(world, startx, starty, startz);
				}
				else {break;}
			}
		if(currentFlowDecay==0)
		{
			Block block = world.getBlock(startx, starty, startz);
			return new BlockWithCoordinates(block, startx, starty, startz,0);
		}
		else
		{
			if(currentFlowDecay < 7)
			{
				world.setBlockMetadataWithNotify(startx, starty, startz,currentFlowDecay+1,3);
				return null;
			}
		}
		return null;
	}
    
    protected int getFlowDecay(World par1World, int par2, int par3, int par4)
    {
    	return par1World.getBlock(par2, par3, par4).getMaterial().isLiquid() ? par1World.getBlockMetadata(par2, par3, par4) : -1;
    }
}