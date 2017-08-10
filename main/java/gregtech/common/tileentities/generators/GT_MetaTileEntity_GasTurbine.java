/*  1:   */ package gregtech.common.tileentities.generators;
/*  2:   */ 
/*  3:   */ import gregtech.api.GregTech_API;
import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.Textures;
/*  4:   */ import gregtech.api.interfaces.ITexture;
/*  5:   */ import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
/*  6:   */ import gregtech.api.metatileentity.MetaTileEntity;
/*  7:   */ import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicGenerator;
/*  8:   */ import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
/* 10:   */ 
/* 11:   */ public class GT_MetaTileEntity_GasTurbine
/* 12:   */   extends GT_MetaTileEntity_BasicGenerator
/* 13:   */ {

	public int mEfficiency;
/* 14:   */   @Override
public boolean isOutputFacing(byte aSide)
/* 15:   */   {
/* 16:12 */     return aSide == getBaseMetaTileEntity().getFrontFacing();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public GT_MetaTileEntity_GasTurbine(int aID, String aName, String aNameRegional, int aTier)
/* 20:   */   {
/* 21:15 */     super(aID, aName, aNameRegional, aTier, "Requires flammable Gasses", new ITexture[0]);
onConfigLoad();
/* 22:   */   }
/* 23:   */   
/* 24:   */   public GT_MetaTileEntity_GasTurbine(String aName, int aTier, String aDescription, ITexture[][][] aTextures)
/* 25:   */   {
/* 26:19 */     super(aName, aTier, aDescription, aTextures);
onConfigLoad();
/* 27:   */   }
/* 28:   */   
/* 29:   */   @Override
public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity)
/* 30:   */   {
/* 31:24 */     return new GT_MetaTileEntity_GasTurbine(this.mName, this.mTier, this.mDescription, this.mTextures);
/* 32:   */   }
/* 33:   */   
/* 34:   */   @Override
public GT_Recipe.GT_Recipe_Map getRecipes()
/* 35:   */   {
/* 36:29 */     return GT_Recipe.GT_Recipe_Map.sTurbineFuels;
/* 37:   */   }
/* 38:   */   
/* 39:   */   @Override
public int getCapacity()
/* 40:   */   {
/* 41:34 */     return 16000;
/* 42:   */   }

public void onConfigLoad()
/* 39:   */   {
				this.mEfficiency =GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "GasTurbine.efficiency.tier."+this.mTier, (100 - this.mTier * 10));
}

/* 43:   */   
/* 44:   */   @Override
public int getEfficiency()
/* 45:   */   {
/* 46:39 */     return this.mEfficiency;
/* 47:   */   }
/* 48:   */   
/* 49:   */   @Override
public ITexture[] getFront(byte aColor)
/* 50:   */   {
/* 51:42 */     return new ITexture[] { super.getFront(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_FRONT), Textures.BlockIcons.OVERLAYS_ENERGY_OUT[this.mTier] };
/* 52:   */   }
/* 53:   */   
/* 54:   */   @Override
public ITexture[] getBack(byte aColor)
/* 55:   */   {
/* 56:43 */     return new ITexture[] { super.getBack(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_BACK) };
/* 57:   */   }
/* 58:   */   
/* 59:   */   @Override
public ITexture[] getBottom(byte aColor)
/* 60:   */   {
/* 61:44 */     return new ITexture[] { super.getBottom(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_BOTTOM) };
/* 62:   */   }
/* 63:   */   
/* 64:   */   @Override
public ITexture[] getTop(byte aColor)
/* 65:   */   {
/* 66:45 */     return new ITexture[] { super.getTop(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_TOP) };
/* 67:   */   }
/* 68:   */   
/* 69:   */   @Override
public ITexture[] getSides(byte aColor)
/* 70:   */   {
/* 71:46 */     return new ITexture[] { super.getSides(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_SIDE) };
/* 72:   */   }
/* 73:   */   
/* 74:   */   @Override
public ITexture[] getFrontActive(byte aColor)
/* 75:   */   {
/* 76:47 */     return new ITexture[] { super.getFrontActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_FRONT_ACTIVE), Textures.BlockIcons.OVERLAYS_ENERGY_OUT[this.mTier] };
/* 77:   */   }
/* 78:   */   
/* 79:   */   @Override
public ITexture[] getBackActive(byte aColor)
/* 80:   */   {
/* 81:48 */     return new ITexture[] { super.getBackActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_BACK_ACTIVE) };
/* 82:   */   }
/* 83:   */   
/* 84:   */   @Override
public ITexture[] getBottomActive(byte aColor)
/* 85:   */   {
/* 86:49 */     return new ITexture[] { super.getBottomActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_BOTTOM_ACTIVE) };
/* 87:   */   }
/* 88:   */   
/* 89:   */   @Override
public ITexture[] getTopActive(byte aColor)
/* 90:   */   {
/* 91:50 */     return new ITexture[] { super.getTopActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_TOP_ACTIVE) };
/* 92:   */   }
/* 93:   */   
/* 94:   */   @Override
public ITexture[] getSidesActive(byte aColor)
/* 95:   */   {
/* 96:51 */     return new ITexture[] { super.getSidesActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.GAS_TURBINE_SIDE_ACTIVE) };
/* 97:   */   }
/* 98:   */ }


/* Location:           F:\Torrent\minecraft\jd-gui-0.3.6.windows\gregtech_1.7.10-5.07.07-dev.jar
 * Qualified Name:     gregtech.common.tileentities.generators.GT_MetaTileEntity_GasTurbine
 * JD-Core Version:    0.7.0.1
 */