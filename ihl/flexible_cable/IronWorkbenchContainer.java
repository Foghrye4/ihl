package ihl.flexible_cable;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import ihl.processing.invslots.SlotInvSlotOutputInProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class IronWorkbenchContainer extends ContainerBase {

    protected IronWorkbenchTileEntity tileEntity;
    private int lastProgress1 = -1;
    private int conrolSum = -1;
    private int currentSlot1 = -1;
    private short workspaceReadyStatus1 = -1;
    private static final short slotUpdateOffset=4;
    public final static int height=202;//166
    
    public IronWorkbenchContainer(EntityPlayer entityPlayer, IronWorkbenchTileEntity tileEntity1){
        super(tileEntity1);
        this.tileEntity = tileEntity1;
        int col, row;

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
        for(row=0;row<6;row++)
        {
            for(col=0;col<=1;col++)
            {
            	this.addSlotToContainer(new SlotInvSlot(tileEntity1.tools, row+col*6, 26+col*18, 8+row*18));
            }
        }
       for(row=0;row<6;row++)
       {
           for(col=0;col<=1;col++)
           {
        	   this.addSlotToContainer(new SlotInvSlot(tileEntity1.inputMaterial, row+col*6, 65+col*18, 8+row*18));
           }
       }
       for (row = 0; row<6; ++row)
       {
    	   this.addSlotToContainer(new SlotInvSlotOutputInProgress(tileEntity1.workspaceElements, row, 8, 8+row*18));
       }
	   for(row=0;row<6;row++)
  		{
		   for (col = 0; col<3; ++col)
		   	{
    		   this.addSlotToContainer(new SlotInvSlotOutputInProgress(tileEntity1.output, col+row*3, 116+col*18, 8+row*18));
       		}
       }

    }

    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.tileEntity.progress != this.lastProgress1)
            {
            	icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);

            }
            if (this.tileEntity.currentSlot != this.currentSlot1)
            {
            	icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.currentSlot);

            }
            if(this.tileEntity.workspaceElements.encodeReadyStatus()!=this.workspaceReadyStatus1)
            {
               	icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.workspaceElements.encodeReadyStatus());
            }
            if(this.tileEntity.output.getCheckSum()!=this.conrolSum)
            {
            	for(int i1 = 0; i1<this.tileEntity.output.size();i1++)
            	{
                	icrafting.sendProgressBarUpdate(this, i1+slotUpdateOffset, this.tileEntity.output.slotRecipe[i1]);
            	}
            }

        }
        this.currentSlot1=this.tileEntity.currentSlot;
        this.conrolSum=this.tileEntity.output.getCheckSum();
        this.lastProgress1 = this.tileEntity.progress;
        this.workspaceReadyStatus1=this.tileEntity.workspaceElements.encodeReadyStatus();
    }
    
    @Override
	public void updateProgressBar(int index, int value)
    {
        super.updateProgressBar(index, value);
        if(index>=slotUpdateOffset)
        {
            this.tileEntity.output.slotRecipe[index-slotUpdateOffset]=(short) value;
        }
        switch (index)
        {
        case 0:
            this.tileEntity.progress=value;
            break;
        case 1:
        	this.tileEntity.currentSlot=value;
            break;
        case 2:
        	this.tileEntity.workspaceElements.decodeReadyStatus((short) value);
            break;
        }
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
	
    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
}
