/*  1:   */ package gregtech.common.tileentities.automation;
/*  2:   */ 
/*  3:   */ import gregtech.api.enums.Textures;
/*  4:   */ import gregtech.api.interfaces.ITexture;
/*  5:   */ import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
/*  6:   */ import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
/*  7:   */ import gregtech.api.objects.GT_RenderedTexture;
/*  8:   */ import gregtech.common.gui.GT_Container_SuperBuffer;
/*  9:   */ import gregtech.common.gui.GT_GUIContainer_SuperBuffer;
/* 10:   */ import net.minecraft.entity.player.InventoryPlayer;
/* 11:   */ 
/* 12:   */ public class GT_MetaTileEntity_SuperBuffer
/* 13:   */   extends GT_MetaTileEntity_ChestBuffer
/* 14:   */ {
/* 15:   */   public GT_MetaTileEntity_SuperBuffer(int aID, String aName, String aNameRegional, int aTier)
/* 16:   */   {
/* 17:14 */     super(aID, aName, aNameRegional, aTier, 257, "Buffering up to 256 Stacks");
/* 18:   */   }
/* 19:   */   
/* 20:   */   public GT_MetaTileEntity_SuperBuffer(String aName, int aTier, int aInvSlotCount, String aDescription, ITexture[][][] aTextures)
/* 21:   */   {
/* 22:18 */     super(aName, aTier, aInvSlotCount, aDescription, aTextures);
/* 23:   */   }
/* 24:   */   
/* 25:   */   @Override
public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity)
/* 26:   */   {
/* 27:23 */     return new GT_MetaTileEntity_SuperBuffer(this.mName, this.mTier, this.mInventory.length, this.mDescription, this.mTextures);
/* 28:   */   }
/* 29:   */   
/* 30:   */   @Override
public ITexture getOverlayIcon()
/* 31:   */   {
/* 32:28 */     return new GT_RenderedTexture(Textures.BlockIcons.AUTOMATION_SUPERBUFFER);
/* 33:   */   }
/* 34:   */   
/* 35:   */   @Override
public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity)
/* 36:   */   {
/* 37:33 */     return new GT_Container_SuperBuffer(aPlayerInventory, aBaseMetaTileEntity);
/* 38:   */   }
/* 39:   */   
/* 40:   */   @Override
public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity)
/* 41:   */   {
/* 42:38 */     return new GT_GUIContainer_SuperBuffer(aPlayerInventory, aBaseMetaTileEntity);
/* 43:   */   }
/* 44:   */ }


/* Location:           F:\Torrent\minecraft\jd-gui-0.3.6.windows\gregtech_1.7.10-5.07.07-dev.jar
 * Qualified Name:     gregtech.common.tileentities.automation.GT_MetaTileEntity_SuperBuffer
 * JD-Core Version:    0.7.0.1
 */