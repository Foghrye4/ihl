package gregtech.common.tileentities.generators;

/*  3:   */ import java.util.ArrayList;

import cpw.mods.fml.common.Loader;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.Textures;
/*  4:   */ import gregtech.api.interfaces.ITexture;
/*  5:   */ import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
/*  6:   */ import gregtech.api.metatileentity.MetaTileEntity;
/*  7:   */ import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicGenerator;
/*  8:   */ import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
/* 10:   */ 
/* 11:   */ public class GT_MetaTileEntity_MagicalEnergyAbsorber
/* 12:   */   extends GT_MetaTileEntity_BasicGenerator
/* 13:   */ {

				  public int mEfficiency;
				  public static boolean sAllowMultipleEggs = true;
				  public static GT_MetaTileEntity_MagicalEnergyAbsorber mActiveSiphon = null;
				  public static int sEnergyPerEnderCrystal = 32;
				  public static int sEnergyFromVis = 12800;
				  public static final ArrayList<EntityEnderCrystal> sUsedDragonCrystalList = new ArrayList<EntityEnderCrystal>();
				  public EntityEnderCrystal mTargetedCrystal;
				  public static int sDragonEggEnergyPerTick = 128;
				  public static boolean isThaumcraftLoaded;
				  
/* 14:   */   @Override
public boolean isOutputFacing(byte aSide)
/* 15:   */   {
/* 16:12 */     return aSide == getBaseMetaTileEntity().getFrontFacing();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public GT_MetaTileEntity_MagicalEnergyAbsorber(int aID, String aName, String aNameRegional, int aTier)
/* 20:   */   {
/* 21:15 */     super(aID, aName, aNameRegional, aTier, "Feasts on magic close to it", new ITexture[0]);
				onConfigLoad();
/* 22:   */   }
/* 23:   */   
/* 24:   */   public GT_MetaTileEntity_MagicalEnergyAbsorber(String aName, int aTier, String aDescription, ITexture[][][] aTextures)
/* 25:   */   {
/* 26:19 */     super(aName, aTier, aDescription, aTextures);
				onConfigLoad();
/* 27:   */   }
/* 28:   */   
/* 29:   */   @Override
public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity)
/* 30:   */   {
/* 31:24 */     return new GT_MetaTileEntity_MagicalEnergyAbsorber(this.mName, this.mTier, this.mDescription, this.mTextures);
/* 32:   */   }
/* 33:   */   
/* 34:   */   @Override
public GT_Recipe.GT_Recipe_Map getRecipes()
/* 35:   */   {
/* 36:29 */     return GT_Recipe.GT_Recipe_Map.sMagicFuels;
/* 37:   */   }
/* 38:   */   
/* 39:   */   @Override
public int getCapacity()
/* 40:   */   {
/* 41:34 */     return 16000;
/* 42:   */   }

				public void onConfigLoad()
/* 39:   */   {
				this.mEfficiency =GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "MagicEnergyAbsorber.efficiency.tier."+this.mTier, 100-this.mTier*10);
			    GT_MetaTileEntity_MagicalEnergyAbsorber.sAllowMultipleEggs = GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "MagicEnergyAbsorber.AllowMultipleEggs", false);
			    GT_MetaTileEntity_MagicalEnergyAbsorber.sEnergyPerEnderCrystal = GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "MagicEnergyAbsorber.EnergyPerTick.EnderCrystal", 32);
			    GT_MetaTileEntity_MagicalEnergyAbsorber.sEnergyFromVis = (GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "MagicEnergyAbsorber.EnergyPerVisDivisor", 2500)*10);
			    GT_MetaTileEntity_MagicalEnergyAbsorber.sDragonEggEnergyPerTick = GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "MagicEnergyAbsorber.EnergyPerTick", 2048);
			    GT_MetaTileEntity_MagicalEnergyAbsorber.isThaumcraftLoaded = Loader.isModLoaded("Thaumcraft");
				}
				
			    @SuppressWarnings("unchecked")
				@Override
			    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
			    	if (aBaseMetaTileEntity.isServerSide() && aBaseMetaTileEntity.isAllowedToWork() && aBaseMetaTileEntity.getUniversalEnergyStored() < maxEUOutput() + aBaseMetaTileEntity.getEUCapacity()) {
			    		//Dragon Egg
			    		if(hasEgg()&&aTick%10==0){
			    			getBaseMetaTileEntity().increaseStoredEnergyUnits(sDragonEggEnergyPerTick*getEfficiency()*10, false);
			    			if ((mActiveSiphon != this) && (!sAllowMultipleEggs)) {
			    		          if ((mActiveSiphon == null) || (mActiveSiphon.getBaseMetaTileEntity() == null) || (mActiveSiphon.getBaseMetaTileEntity().isInvalidTileEntity()) || (!mActiveSiphon.hasEgg())) {
			    		            mActiveSiphon = this;
			    		          } else {
			    		            getBaseMetaTileEntity().doExplosion(Integer.MAX_VALUE);
			    		          }
			    		     }
			    		}
			    		//Energyzed node
			    		//EnderCrystal
			    		
			    	      if (sEnergyPerEnderCrystal > 0) {
			    	          if (this.mTargetedCrystal == null)
			    	          {
			    	            ArrayList<EntityEnderCrystal> tList = (ArrayList<EntityEnderCrystal>)getBaseMetaTileEntity().getWorld().getEntitiesWithinAABB(EntityEnderCrystal.class, AxisAlignedBB.getBoundingBox(getBaseMetaTileEntity().getXCoord() - 64, getBaseMetaTileEntity().getYCoord() - 64, getBaseMetaTileEntity().getZCoord() - 64, getBaseMetaTileEntity().getXCoord() + 64, getBaseMetaTileEntity().getYCoord() + 64, getBaseMetaTileEntity().getZCoord() + 64));
			    	            if ((tList != null) && (!tList.isEmpty()))
			    	            {
			    	              tList.removeAll(sUsedDragonCrystalList);
			    	              if (tList.size() > 0)
			    	              {
			    	                this.mTargetedCrystal = (tList.get(0));
			    	                if (this.mTargetedCrystal != null) {
			    	                  sUsedDragonCrystalList.add(this.mTargetedCrystal);
			    	                }
			    	              }
			    	            }
			    	          }
			    	          else if (this.mTargetedCrystal.isEntityAlive())
			    	          {
			    	            getBaseMetaTileEntity().increaseStoredEnergyUnits(sEnergyPerEnderCrystal * 10, false);
			    	          }
			    	          else
			    	          {
			    	            sUsedDragonCrystalList.remove(this.mTargetedCrystal);
			    	            this.mTargetedCrystal = null;
			    	          }
			    	        }
			    		
			    		//Absorb entchantments
			    		
			    		try
			            {
			              if ((this.mInventory[0] != null) && (this.mInventory[1] == null))
			              {
			                if ((this.mInventory[0].isItemEnchanted()) && (this.mInventory[0].getItem().getItemEnchantability() > 0))
			                {
			                  NBTTagList tEnchantments = this.mInventory[0].getEnchantmentTagList();
			                  if (tEnchantments != null)
			                  {
			                    for (int i = 0; i < tEnchantments.tagCount(); i++)
			                    {
			                      short tID = tEnchantments.getCompoundTagAt(i).getShort("id");
			                      short tLevel = tEnchantments.getCompoundTagAt(i).getShort("lvl");
			                      if ((tID > -1) && (tID < Enchantment.enchantmentsList.length))
			                      {
			                        Enchantment tEnchantment = Enchantment.enchantmentsList[tID];
			                        if (tEnchantment != null) {
			                          getBaseMetaTileEntity().increaseStoredEnergyUnits(1000000 * tLevel / (tEnchantment.getMaxLevel() * tEnchantment.getWeight()), true);
			                        }
			                      }
			                    }
			                    this.mInventory[0].stackTagCompound.removeTag("ench");
			                  }
			                }
			                else if ((this.mInventory[0].getItem() instanceof ItemEnchantedBook))
			                {
			                  NBTTagList tEnchantments = ((ItemEnchantedBook)this.mInventory[0].getItem()).func_92110_g(this.mInventory[0]);
			                  if (tEnchantments != null)
			                  {
			                    for (int i = 0; i < tEnchantments.tagCount(); i++)
			                    {
			                      short tID = tEnchantments.getCompoundTagAt(i).getShort("id");
			                      short tLevel = tEnchantments.getCompoundTagAt(i).getShort("lvl");
			                      if ((tID > -1) && (tID < Enchantment.enchantmentsBookList.length))
			                      {
			                        Enchantment tEnchantment = Enchantment.enchantmentsBookList[tID];
			                        if (tEnchantment != null) {
			                          getBaseMetaTileEntity().increaseStoredEnergyUnits(1000000 * tLevel / (tEnchantment.getMaxLevel() * tEnchantment.getWeight()), true);
			                        }
			                      }
			                    }
			                    this.mInventory[0] = new ItemStack(Items.book, 1);
			                  }
			                }
			                this.mInventory[1] = this.mInventory[0];
			                this.mInventory[0] = null;
			              }
			            }
			            catch (Throwable e){}			    		
					}
			    }
			    
			    @Override
				public void inValidate()
			    {
			      if (mActiveSiphon == this) {
			        mActiveSiphon = null;
			      }
			    }
			    
			    public boolean hasEgg()
			    {
			    	Block above = getBaseMetaTileEntity().getBlockOffset(0, 1, 0);
			    	if(above==null||Blocks.air==above){return false;}
			      return Blocks.dragon_egg == above || above.getUnlocalizedName().equals("tile.dragonEgg");
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
/* 51:42 */     return new ITexture[] { super.getFront(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC), Textures.BlockIcons.OVERLAYS_ENERGY_OUT[this.mTier] };
/* 52:   */   }
/* 53:   */   
/* 54:   */   @Override
public ITexture[] getBack(byte aColor)
/* 55:   */   {
/* 56:43 */     return new ITexture[] { super.getBack(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC_FRONT) };
/* 57:   */   }
/* 58:   */   
/* 59:   */   @Override
public ITexture[] getBottom(byte aColor)
/* 60:   */   {
/* 61:44 */     return new ITexture[] { super.getBottom(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC) };
/* 62:   */   }
/* 63:   */   
/* 64:   */   @Override
public ITexture[] getTop(byte aColor)
/* 65:   */   {
/* 66:45 */     return new ITexture[] { super.getTop(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_DRAGONEGG) };
/* 67:   */   }
/* 68:   */   
/* 69:   */   @Override
public ITexture[] getSides(byte aColor)
/* 70:   */   {
/* 71:46 */     return new ITexture[] { super.getSides(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC) };
/* 72:   */   }
/* 73:   */   
/* 74:   */   @Override
public ITexture[] getFrontActive(byte aColor)
/* 75:   */   {
/* 76:47 */     return new ITexture[] { super.getFrontActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC_ACTIVE), Textures.BlockIcons.OVERLAYS_ENERGY_OUT[this.mTier] };
/* 77:   */   }
/* 78:   */   
/* 79:   */   @Override
public ITexture[] getBackActive(byte aColor)
/* 80:   */   {
/* 81:48 */     return new ITexture[] { super.getBackActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC_FRONT_ACTIVE) };
/* 82:   */   }
/* 83:   */   
/* 84:   */   @Override
public ITexture[] getBottomActive(byte aColor)
/* 85:   */   {
/* 86:49 */     return new ITexture[] { super.getBottomActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC_ACTIVE) };
/* 87:   */   }
/* 88:   */   
/* 89:   */   @Override
public ITexture[] getTopActive(byte aColor)
/* 90:   */   {
/* 91:50 */     return new ITexture[] { super.getTopActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_DRAGONEGG) };
/* 92:   */   }
/* 93:   */   
/* 94:   */   @Override
public ITexture[] getSidesActive(byte aColor)
/* 95:   */   {
/* 96:51 */     return new ITexture[] { super.getSidesActive(aColor)[0], new GT_RenderedTexture(Textures.BlockIcons.MACHINE_CASING_MAGIC_ACTIVE) };
/* 97:   */   }
/* 98:   */ }