package ihl.processing.chemistry;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class LoomContainer extends ContainerBase<LoomTileEntity> {

	public LoomTileEntity tileEntity;
    public int lastProgress = -1;
    private final static int height=166;

	public LoomContainer(EntityPlayer entityPlayer,
			LoomTileEntity lathePart1TileEntity) {
		super(lathePart1TileEntity);
		tileEntity=lathePart1TileEntity;
        int col;

        for (col = 0; col < 3; ++col)
        {
            for (int col1 = 0; col1 < 9; ++col1)
            {
                this.addSlotToContainer(new Slot(entityPlayer.inventory, col1 + col * 9 + 9, 8 + col1 * 18, height + -82 + col * 18));
            }
        }

        for (col = 0; col < 9; ++col)
        {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, col, 8 + col * 18, height + -24));
        }
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.input, 0, 8, 44));		
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.output, 0, 127, 44));		
	}

	   @Override
	public void detectAndSendChanges()
	    {
	        super.detectAndSendChanges();
	        for (int i = 0; i < this.crafters.size(); ++i)
	        {
	            ICrafting icrafting = (ICrafting)this.crafters.get(i);
	            if (this.tileEntity.progress != this.lastProgress)
	            {
	            	icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
	            }
	        }
	        this.lastProgress = this.tileEntity.progress;
	    }
	    
	    @Override
		public void updateProgressBar(int index, int value)
	    {
	        super.updateProgressBar(index, value);

	        switch (index)
	        {
	        case 0:
	            this.tileEntity.progress=(short) value;
	            break;

	        }
	    }
	    
}
