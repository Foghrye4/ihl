package ihl.datanet;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import ihl.processing.invslots.SlotRedstoneConverterCableInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class RedstoneSignalConverterContainer extends ContainerBase {

    protected RedstoneSignalConverterTileEntity tileEntity;
    private int lastLinksAmount = -1;
    private int lastCablesAmount = -1;
    private short lastEnergy = -1;
    private int prevButtonPressed1 = -1;
    public final static int height=224;
    public final static int width=198;
    public List<Integer> links;
    
    public RedstoneSignalConverterContainer(EntityPlayer entityPlayer, RedstoneSignalConverterTileEntity tileEntity1){
        super(tileEntity1);
        this.tileEntity = tileEntity1;
        links=this.tileEntity.links;
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
        for (col = 0; col < tileEntity1.sensorEmitterSlots.size(); ++col)
        {
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.sensorEmitterSlots, col, 8, 8+col*18));
        }
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.dischargeSlot, 0, 8, 116));
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tileEntity.isUseableByPlayer(var1);
	}
	

    @Override
	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            if (this.tileEntity.links.size() != this.lastLinksAmount)
            {
                IC2.network.get().sendContainerField(this, "links");
            }
            if (this.tileEntity.energy != this.lastEnergy)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.energy);
            }
            if (this.tileEntity.cableList.size() != this.lastCablesAmount)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.cableList.size());
            }
            if (this.tileEntity.prevButtonPressed != this.prevButtonPressed1)
            {
                icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.prevButtonPressed);
            }
        }
        this.lastCablesAmount=this.tileEntity.cableList.size();
        this.lastLinksAmount=this.tileEntity.links.size();
        this.lastEnergy=this.tileEntity.energy;
        this.prevButtonPressed1 = this.tileEntity.prevButtonPressed;

    }
    
    @Override
	public void updateProgressBar(int index, int value)
    {
        super.updateProgressBar(index, value);
        switch (index)
        {
            case 1:
                this.tileEntity.energy=(short) value;
                break;
            case 2:
                this.tileEntity.cablesAmount=(short) value;
                break;
            case 3:
                this.tileEntity.prevButtonPressed=(short) value;
                break;
        }
    }
}
