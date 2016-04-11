package ihl.worldgen.ores;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class DebugScannerContainer extends ContainerBase {

    protected DebugScannerTileEntity tileEntity;
    public int lastFluidAmount = -1;
    public int lastFuel = -1;
    public short lastProgress = -1;
    public final static int height=256;
    public final static int width=248;
    
    public DebugScannerContainer(EntityPlayer entityPlayer, DebugScannerTileEntity tileEntity1){
        super(tileEntity1);
        this.tileEntity = tileEntity1;
        int col,row;

        for (row = 0; row < 3; ++row)
        {
            for (col = 0; col < 13; ++col)
            {
            	int slotnum = col + row * 13 + 9;
            	if(slotnum < entityPlayer.inventory.getSizeInventory()-4)
            	{
            		this.addSlotToContainer(new Slot(entityPlayer.inventory, slotnum, 8 + col * 18, 196 + row * 18));
            	}
            	else
            	{
            		break;
            	}
            }
        }
        for (col = 0; col < 9; ++col)
        {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, col, 80 + col * 18, 232));
        }
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.itemsSlot, 0, 8, 8));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.itemsSlot, 1, 8+18, 8));
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
