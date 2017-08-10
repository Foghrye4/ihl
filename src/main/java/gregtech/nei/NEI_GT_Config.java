/*  1:   */ package gregtech.nei;
/*  2:   */ 
/*  3:   */ import codechicken.nei.api.IConfigureNEI;
/*  5:   */ 
/*  6:   */ public class NEI_GT_Config
/*  7:   */   implements IConfigureNEI
/*  8:   */ {
/*  9: 8 */   public static boolean sIsAdded = true;
/* 10:   */   
/* 11:   */   @Override
public void loadConfig()
/* 12:   */   {
/* 19:26 */     sIsAdded = true;
/* 20:   */   }
/* 21:   */   
/* 22:   */   @Override
public String getName()
/* 23:   */   {
/* 24:31 */     return "GregTech NEI Plugin";
/* 25:   */   }
/* 26:   */   
/* 27:   */   @Override
public String getVersion()
/* 28:   */   {
/* 29:37 */     return "(5.03a)";
/* 30:   */   }
/* 31:   */ }


/* Location:           F:\Torrent\minecraft\jd-gui-0.3.6.windows\gregtech_1.7.10-5.07.07-dev.jar
 * Qualified Name:     gregtech.nei.NEI_GT_Config
 * JD-Core Version:    0.7.0.1
 */