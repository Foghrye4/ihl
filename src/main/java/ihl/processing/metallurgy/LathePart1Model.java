package ihl.processing.metallurgy;

import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;

public class LathePart1Model extends ModelBase
{
  //fields
    IHLModelRenderer Base;
    IHLModelRenderer RotatingPart2;
    IHLModelRenderer MotorPart1;
    IHLModelRenderer MotorPart2;
    IHLModelRenderer Belt;
    IHLModelRenderer Belt2;
  
  public LathePart1Model()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape6", 0, 0);
    setTextureOffset("Base.Shape3", 0, 0);
    setTextureOffset("Base.Shape2", 0, 0);
    setTextureOffset("Base.NewShape4", 0, 0);
    setTextureOffset("RotatingPart2.PipeN1", 0, 0);
    setTextureOffset("RotatingPart2.PipeN7", 49, 23);
    setTextureOffset("RotatingPart2.Shape1", 56, 19);
    setTextureOffset("RotatingPart2.Shape5", 0, 8);
    setTextureOffset("RotatingPart2.Shape8", 0, 11);
    setTextureOffset("RotatingPart2.PipeN4", 0, 0);
    setTextureOffset("RotatingPart2.Shape9", 0, 0);
    setTextureOffset("MotorPart1.PipeN41", 0, 0);
    setTextureOffset("MotorPart1.PipeN12", 0, 0);
    setTextureOffset("MotorPart1.PipeN13", 0, 0);
    setTextureOffset("MotorPart2.Shape4", 0, 24);
    setTextureOffset("MotorPart2.Shape7", 0, 9);
    setTextureOffset("MotorPart2.Shape10", 0, 9);
    setTextureOffset("Belt.Shape12", 46, 0);
    setTextureOffset("Belt2.Shape13", 46, 0);
    
    Base = new IHLModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 16F, 0F);
    setRotation(Base, 0F, 0F, 0F);
    Base.mirror = true;
      Base.addBox("Shape6", -8F, 7F, -8F, 16, 1, 16);
      Base.addBox("Shape3", -5.5F, -1.5F, -7F, 7, 9, 1);
      Base.addBox("Shape2", -5.5F, -0.5F, -4F, 5, 8, 1);
      Base.addBox("NewShape4", 3F, 3F, -6F, 3, 4, 1);
    RotatingPart2 = new IHLModelRenderer(this, "RotatingPart2");
    RotatingPart2.setRotationPoint(-3F, 18F, -7F);
    setRotation(RotatingPart2, 0F, 0F, 0F);
    RotatingPart2.mirror = true;
      RotatingPart2.addTube("PipeN1", -1.5F, -1.5F, 0F, 3, 3, 5, 0F,1F,ForgeDirection.NORTH);
      RotatingPart2.addTube("PipeN7", -2.5F, -2.5F, -1F, 5, 5, 1, 0F,1F,ForgeDirection.NORTH);
      RotatingPart2.addBox("Shape1", 2F, -0.5F, 8.5F, 3, 1, 1);
      RotatingPart2.addBox("Shape5", -0.5F, -5F, 8.5F, 1, 3, 1);
      RotatingPart2.addBox("Shape8", -5F, -0.5F, 8.5F, 3, 1, 1);
      RotatingPart2.addTube("PipeN4", -4F, -4F, 4F, 8, 8, 5, 0.3F,1F,ForgeDirection.NORTH);
      RotatingPart2.addBox("Shape9", -0.5F, 2F, 8.5F, 1, 3, 1);
      MotorPart1 = new IHLModelRenderer(this, "MotorPart1");
      MotorPart1.setRotationPoint(0F, 21F, 0F);
      setRotation(MotorPart1, 0F, 0F, 0F);
      MotorPart1.mirror = true;
        MotorPart1.addTube("PipeN41", 3F, -2F, -8F, 3, 3, 1, 0F,1F,ForgeDirection.NORTH);
        MotorPart1.addTube("PipeN12", 4F, -1F, -7F, 1, 1, 1, 0F,1F,ForgeDirection.NORTH);
        MotorPart1.addTube("PipeN13", 3F, -2F, -5F, 3, 3, 5, 0F,1F,ForgeDirection.NORTH);
      MotorPart2 = new IHLModelRenderer(this, "MotorPart2");
      MotorPart2.setRotationPoint(0F, 16F, 0F);
      setRotation(MotorPart2, 0F, 0F, 0F);
      MotorPart2.mirror = true;
        MotorPart2.addBox("Shape4", 3F, 3F, 0F, 3, 4, 4);
        MotorPart2.addBox("Shape7", 6F, 3F, -5F, 2, 4, 5);
        MotorPart2.addBox("Shape10", 1F, 3F, -5F, 2, 4, 5);
      Belt = new IHLModelRenderer(this, "Belt");
      Belt.setRotationPoint(-3F, 15.5F, -7.9F);
      setRotation(Belt, 0F, 0F, 0.45F);
      Belt.mirror = true;
        Belt.addBox("Shape12", 0F, 0F, 0F, 8, 1, 1);
      Belt2 = new IHLModelRenderer(this, "Belt2");
      Belt2.setRotationPoint(-3F, 19.5F, -7.9F);
      setRotation(Belt2, 0F, 0F, 0.19F);
      Belt2.mirror = true;
        Belt2.addBox("Shape13", 0F, 0F, 0F, 8, 1, 1);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
