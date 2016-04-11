package ihl.processing.chemistry;

import java.util.List;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;

public class CryogenicDistillerContainer extends ContainerBase {

	public CryogenicDistillerTileEntity tileEntity;
    public int lastProgress = -1;
	private short lastEnergy = -1;
	private int lastNumberOfFluids = -1;
	private int lastFluidAmount = -1;
    private final static int height=166;
    public List<FluidStack> fluidTankFluidList;

	public CryogenicDistillerContainer(EntityPlayer entityPlayer,
			CryogenicDistillerTileEntity lathePart1TileEntity) {
		super(lathePart1TileEntity);
		tileEntity=lathePart1TileEntity;
		fluidTankFluidList=tileEntity.fluidTankProducts.getFluidList();
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
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.engine,0, 8, 32));	
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fillInputSlotInput,0, 58, 51));	
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fillInputSlotProducts,0, 103, 51));	
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fluidItemsSlot,0, 58, 15));	
        this.addSlotToContainer(new SlotInvSlot(lathePart1TileEntity.fluidItemsSlot,1, 103, 15));	
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
	            if (this.tileEntity.fluidTankProducts.getFluidAmount() != this.lastFluidAmount || this.tileEntity.fluidTankProducts.getNumberOfFluids() != this.lastNumberOfFluids)
	            {
                    IC2.network.get().sendContainerField(this, "fluidTankFluidList");
	            }
	        }
	        this.lastProgress = this.tileEntity.progress;
	        this.lastNumberOfFluids = this.tileEntity.fluidTankProducts.getNumberOfFluids();
	        this.lastFluidAmount = this.tileEntity.fluidTankProducts.getFluidAmount();
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
