package ihl.processing.chemistry;

import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;

public class GoldChimneyKneeModel extends ModelBase
{
  //fields
	IHLModelRenderer Base;
  
  public GoldChimneyKneeModel()
  {
	    textureWidth = 64;
	    textureHeight = 32;
	    setTextureOffset("Base.Shape1", 26, 0);
	    setTextureOffset("Base.Shape2", 0, 17);
	    setTextureOffset("Base.Shape4", 30, 16);
	    setTextureOffset("Base.Shape3", 0, 15);
	    setTextureOffset("Base.Shape5", 11, 12);
	    setTextureOffset("Base.Shape6", 9, 2);
	    setTextureOffset("Base.Shape7", 24, 7);
	    setTextureOffset("Base.Shape8", 50, 0);
	    setTextureOffset("Base.Shape9", 0, 0);
	    setTextureOffset("Base.Shape10", 6, 0);
	    setTextureOffset("Base.Shape11", 18, 0);
	    setTextureOffset("Base.Pipe", 0, 0);
	    
	    Base = new IHLModelRenderer(this, "Base");
	    Base.setRotationPoint(0F, 23F, -6F);
	    Base.mirror = true;
	      Base.addBox("Shape1", 5F, -14F, 0F, 1, 15, 12, false);
	      Base.addBox("Shape2", -6F, -1F, 0F, 11, 2, 1, false);
	      Base.addBox("Shape4", 5F, -14F, -2F, 1, 13, 2, false);
	      Base.addBox("Shape3", -6F, -2F, -2F, 11, 1, 3, false);
	      Base.addBox("Shape5", -6F, -14F, -2F, 11, 1, 14, false);
	      Base.addBox("Shape6", -5F, -13F, 11F, 4, 14, 1, false);
	      Base.addBox("Shape7", -6F, -13F, 1F, 1, 14, 11, false);
	      Base.addBox("Shape8", -6F, -13F, -2F, 1, 11, 3, false);
	      Base.addBox("Shape9", 1F, -13F, 11F, 4, 14, 1, false);
	      Base.addBox("Shape10", -1F, -13F, 11F, 2, 4, 1, false);
	      Base.addBox("Shape11", -1F, -7F, 11F, 2, 8, 1, false);
	      Base.addTube("Pipe", -2F, -10F, 2F, 4, 4, 12, 0.5F, 1F,ForgeDirection.NORTH);
  }
}
