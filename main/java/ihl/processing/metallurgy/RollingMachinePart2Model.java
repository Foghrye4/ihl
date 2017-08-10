package ihl.processing.metallurgy;


import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;

public class RollingMachinePart2Model extends ModelBase
{
  //fields
    IHLModelRenderer Base;
  
  public RollingMachinePart2Model()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape6", 0, 0);
    setTextureOffset("Base.Shape16", 0, 0);
    setTextureOffset("Base.Shape5", 0, 0);
    setTextureOffset("Base.Shape8", 0, 0);
    setTextureOffset("Base.Shape11", 0, 0);
    setTextureOffset("Base.Shape9", 0, 0);
    
    Base = new IHLModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 16F, 0F);
    setRotation(Base, 0F, 0F, 0F);
    Base.mirror = true;
      Base.addBox("Shape6", -8F, 7F, -8F, 16, 1, 16);
      Base.addBox("Shape16", -3F, -7F, 5F, 6, 14, 1);
      Base.addBox("Shape5", 2F, -1F, -8F, 6, 1, 13);
      Base.addBox("Shape8", -8F, -1F, -8F, 6, 1, 13);
      Base.addBox("Shape11", 3F, 0F, -8F, 4, 7, 1);
      Base.addBox("Shape9", -7F, 0F, -8F, 4, 7, 1);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
