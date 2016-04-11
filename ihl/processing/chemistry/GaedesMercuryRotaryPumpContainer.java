package ihl.processing.chemistry;

import ic2.core.ContainerBase;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class GaedesMercuryRotaryPumpContainer extends ContainerBase {

    protected GaedesMercuryRotaryPumpTileEntity tileEntity;
    private final static int height=166;
    public short lastEnergy = -1;
    public short lastProgress = -1;
    
    public GaedesMercuryRotaryPumpContainer(EntityPlayer entityPlayer, GaedesMercuryRotaryPumpTileEntity tileEntity1){
        super(tileEntity1);
        this.tileEntity = tileEntity1;
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
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.engine, 0, 61, 36));
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
            if ((short) this.tileEntity.energy != this.lastEnergy)
            {
                icrafting.sendProgressBarUpdate(this, 2, (short) this.tileEntity.energy);
            }
        }

        this.lastProgress = this.tileEntity.progress;
        this.lastEnergy = (short) this.tileEntity.energy;
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
            case 2:
                this.tileEntity.energy=value;
                break;
        }
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
}
