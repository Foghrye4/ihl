package ihl.enviroment;

import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;


public class SpotlightModel extends ModelBase
{
  //fields
    IHLModelRenderer Base;
    IHLModelRenderer RotatingPart1;
    IHLModelRenderer RotatingPart2;
    IHLModelRenderer RotatingPart3Halo;
  
  public SpotlightModel()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape1", 0, 0);
    setTextureOffset("RotatingPart1.Shape3", 0, 0);
    setTextureOffset("RotatingPart1.Shape2", 0, 0);
    setTextureOffset("RotatingPart1.Shape4", 0, 0);
    setTextureOffset("RotatingPart2.Shape5", 0, 15);
    setTextureOffset("RotatingPart2.Shape6", 13, 14);
    setTextureOffset("RotatingPart2.Shape7", 7, 15);
    setTextureOffset("RotatingPart2.Shape8", 14, 8);
    setTextureOffset("RotatingPart2.Shape9", 6, 8);
    setTextureOffset("RotatingPart2.Shape10", 0, 23);
    setTextureOffset("RotatingPart2.Shape11", 0, 0);
    setTextureOffset("RotatingPart2.Shape12", 0, 0);
    setTextureOffset("RotatingPart3Halo.Shape13", 32, 0);
    
    Base = new IHLModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 16F, 0F);
    setRotation(Base, 0F, 0F, 0F);
    Base.mirror = false;
      Base.addBox("Shape1", -7F, -7F, 7F, 14, 14, 1);
    RotatingPart1 = new IHLModelRenderer(this, "RotatingPart1");
    RotatingPart1.setRotationPoint(0F, 16F, 0F);
    setRotation(RotatingPart1, 0F, 0F, 0F);
    RotatingPart1.mirror = false;
      RotatingPart1.addBox("Shape3", 5F, -1F, -1F, 1, 2, 8);
      RotatingPart1.addBox("Shape2", -6F, -1F, -1F, 1, 2, 8);
      RotatingPart1.addBox("Shape4", -5F, -1F, 6F, 10, 2, 1);
    RotatingPart2 = new IHLModelRenderer(this, "RotatingPart2");
    RotatingPart2.setRotationPoint(0F, 16F, 0F);
    setRotation(RotatingPart2, 0F, 0F, 0F);
    RotatingPart2.mirror = false;
      RotatingPart2.addBox("Shape5", -4F, -4F, -6F, 7, 1, 7);
      RotatingPart2.addBox("Shape6", -4F, -4F, 1F, 8, 8, 1);
      RotatingPart2.addBox("Shape7", -3F, 3F, -6F, 7, 1, 7);
      RotatingPart2.addBox("Shape8", 3F, -4F, -6F, 1, 7, 7);
      RotatingPart2.addBox("Shape9", -4F, -3F, -6F, 1, 7, 7);
      RotatingPart2.addBox("Shape10", -3F, -3F, -5F, 6, 6, 1);
      RotatingPart2.addBox("Shape11", 4F, -1F, -1F, 1, 2, 2);
      RotatingPart2.addBox("Shape12", -5F, -1F, -1F, 1, 2, 2);
    RotatingPart3Halo = new IHLModelRenderer(this, "RotatingPart3Halo");
    RotatingPart3Halo.setRotationPoint(0F, 16F, 0F);
    setRotation(RotatingPart3Halo, 0F, 0F, 0F);
    RotatingPart3Halo.mirror = false;
      RotatingPart3Halo.addBox("Shape13", -8F, -8F, -6.1F, 16, 16, 0);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
