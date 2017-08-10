/*  1:   */ package gregtech.common.covers;
/*  2:   */ 
/*  3:   */ import gregtech.api.GregTech_API;
/*  4:   */ import gregtech.api.interfaces.tileentity.ICoverable;
/*  6:   */ 
/*  7:   */ public class GT_Cover_RedstoneReceiverExternal
/*  8:   */   extends GT_Cover_RedstoneWirelessBase
/*  9:   */ {
/* 10:   */   @Override
public int doCoverThings(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity, long aTimer)
/* 11:   */   {
/* 12: 9 */     aTileEntity.setOutputRedstoneSignal(aSide, GregTech_API.sWirelessRedstone.get(Integer.valueOf(aCoverVariable)) == null ? 0 : GregTech_API.sWirelessRedstone.get(Integer.valueOf(aCoverVariable)).byteValue());
/* 13:10 */     return aCoverVariable;
/* 14:   */   }
/* 15:   */   
/* 16:   */   @Override
public boolean manipulatesSidedRedstoneOutput(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity)
/* 17:   */   {
/* 18:15 */     return true;
/* 19:   */   }
/* 20:   */   
/* 21:   */   @Override
public int getTickRate(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity)
/* 22:   */   {
/* 23:20 */     return 1;
/* 24:   */   }
/* 25:   */ }


/* Location:           F:\Torrent\minecraft\jd-gui-0.3.6.windows\gregtech_1.7.10-5.07.07-dev.jar
 * Qualified Name:     gregtech.common.covers.GT_Cover_RedstoneReceiverExternal
 * JD-Core Version:    0.7.0.1
 */