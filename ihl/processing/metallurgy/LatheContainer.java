package ihl.processing.metallurgy;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class LatheContainer extends ContainerBase {

	public LathePart1TileEntity tileEntity;
    public int lastProgress = -1;
	private short lastEnergy = -1;
    private final static int height=166;

	public LatheContainer(EntityPlayer entityPlayer,
			LathePart1TileEntity lathePart1TileEntity) {
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
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.engine,0, 22, 55));	
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
	            if (this.tileEntity.getEnergy() != this.lastEnergy)
	            {
	            	icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.getEnergy());
	            }
	        }
	        this.lastProgress = this.tileEntity.progress;
	        this.lastEnergy = (short) this.tileEntity.getEnergy();
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
	        case 1:
	            this.tileEntity.setEnergy(value);
	            break;
	        }
	    }
	    
}
