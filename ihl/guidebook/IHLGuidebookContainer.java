package ihl.guidebook;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import ic2.core.ContainerBase;

public class IHLGuidebookContainer extends ContainerBase<IHLGuidebookInventory>
{
    public IHLGuidebookInventory box;
	public int xSize=256;
	public int ySize=211;
	
    public IHLGuidebookContainer(EntityPlayer entityPlayer, IHLGuidebookInventory box)
    {
        super(box);
        this.box = box;
        int col;
        for (col = 0; col < 4; ++col)
        {
            this.addSlotToContainer(new Slot(box, col, 18 + col * 18, 20));
        }
    }

    @Override
	public void onContainerClosed(EntityPlayer entityPlayer)
    {
        this.box.onGuiClosed(entityPlayer);
        super.onContainerClosed(entityPlayer);
    }
}
