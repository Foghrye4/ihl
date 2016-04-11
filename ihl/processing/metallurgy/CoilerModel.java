package ihl.processing.metallurgy;

import ihl.model.IHLModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.common.util.ForgeDirection;

public class CoilerModel extends ModelBase
{
  //fields
    IHLModelRenderer Base;
    IHLModelRenderer RotatingPart2;
    IHLModelRenderer MotorPart1;
    IHLModelRenderer MotorPart2;
    IHLModelRenderer Belt;
    IHLModelRenderer Belt2;
    IHLModelRenderer CoilRotating;
    IHLModelRenderer Coil;
  
  public CoilerModel()
  {
	    textureWidth = 64;
	    textureHeight = 32;
	    setTextureOffset("Base.Shape6", 0, 0);
	    setTextureOffset("Base.Shape3", 0, 0);
	    setTextureOffset("Base.Shape2", 0, 0);
	    setTextureOffset("Base.Shape16", 0, 0);
	    setTextureOffset("RotatingPart2.PipeN1", 0, 0);
	    setTextureOffset("RotatingPart2.PipeN7", 49, 23);
	    setTextureOffset("RotatingPart2.PipeN4", 0, 17);
	    setTextureOffset("RotatingPart2.PipeN14", 0, 19);
	    setTextureOffset("RotatingPart2.PipeN17", 0, 17);
	    setTextureOffset("MotorPart1.PipeN41", 0, 0);
	    setTextureOffset("MotorPart1.PipeN12", 0, 0);
	    setTextureOffset("MotorPart1.PipeN13", 0, 0);
	    setTextureOffset("MotorPart2.Shape4", 0, 24);
	    setTextureOffset("MotorPart2.Shape7", 0, 9);
	    setTextureOffset("MotorPart2.Shape10", 0, 9);
	    setTextureOffset("Belt.Shape12", 0, 0);
	    setTextureOffset("Belt2.Shape13", 0, 0);
	    setTextureOffset("CoilRotating.PipeN15", 22, 19);
	    setTextureOffset("Coil.Shape1", 22, 30);
	    
	    Base = new IHLModelRenderer(this, "Base");
	    Base.setRotationPoint(0F, 16F, 0F);
	    setRotation(Base, 0F, 0F, 0F);
	    Base.mirror = true;
	      Base.addBox("Shape6", -8F, 7F, -8F, 16, 1, 16);
	      Base.addBox("Shape3", -5F, 0F, -7F, 4, 7, 1);
	      Base.addBox("Shape2", -5.5F, -1F, 7F, 5, 8, 1);
	      Base.addBox("Shape16", 3F, 3F, -6F, 3, 4, 1);
	    RotatingPart2 = new IHLModelRenderer(this, "RotatingPart2");
	    RotatingPart2.setRotationPoint(-3F, 18F, -7F);
	    setRotation(RotatingPart2, 0F, 0F, 0F);
	    RotatingPart2.mirror = true;
	      RotatingPart2.addTube("PipeN1", -0.5F, -0.5F, 0F, 1, 1, 14, 0F,1F,ForgeDirection.NORTH);
	      RotatingPart2.addTube("PipeN7", -2.5F, -2.5F, -1F, 5, 5, 1, 0F,1F,ForgeDirection.NORTH);
	      RotatingPart2.addTube("PipeN4", -4F, -4F, 2F, 8, 8, 1, 0F,1F,ForgeDirection.NORTH);
	      RotatingPart2.addTube("PipeN14", -4F, -4F, 12F, 8, 8, 1, 0F,1F,ForgeDirection.NORTH);
	      RotatingPart2.addTube("PipeN17", -1F, -1F, 3F, 2, 2, 9, 0F,1F,ForgeDirection.NORTH);
	      MotorPart1 = new IHLModelRenderer(this, "MotorPart1");
	      MotorPart1.setRotationPoint(4.5F, 20.5F, 0F);
	      setRotation(MotorPart1, 0F, 0F, 0F);
	      MotorPart1.mirror = true;
	        MotorPart1.addTube("PipeN41", -1.5F, -1.5F, -8F, 3, 3, 1, 0F,1F,ForgeDirection.NORTH);
	        MotorPart1.addTube("PipeN12", -0.5F, -0.5F, -7F, 1, 1, 1, 0F,1F,ForgeDirection.NORTH);
	        MotorPart1.addTube("PipeN13", -1.5F, -1.5F, -5F, 3, 3, 5, 0F,1F,ForgeDirection.NORTH);
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
	        Belt.addBox("Shape12", 0.5F, 0F, 0F, 8, 1, 1);
	      Belt2 = new IHLModelRenderer(this, "Belt2");
	      Belt2.setRotationPoint(-3F, 19.5F, -7.9F);
	      setRotation(Belt2, 0F, 0F, 0.19F);
	      Belt2.mirror = true;
	        Belt2.addBox("Shape13", 0F, 0F, 0F, 8, 1, 1);
	        CoilRotating = new IHLModelRenderer(this, "CoilRotating");
	        CoilRotating.setRotationPoint(-3F, 18F, -7F);
	        setRotation(CoilRotating, 0F, 0F, 0F);
	        CoilRotating.mirror = true;
	          CoilRotating.addTube("PipeN15", -2F, -2F, 3F, 4, 4, 9, 0F,1F,ForgeDirection.NORTH);
	        Coil = new IHLModelRenderer(this, "Coil");
	        Coil.setRotationPoint(-0.25F, 16F, 0F);
	        setRotation(Coil, 0F, 0F, -0.2F);
	        Coil.mirror = true;
	          Coil.addTube("Shape1", -8F, -0.25F, -0.5F, 5, 1, 1, 0F,1F,ForgeDirection.WEST);
  }
  
  private void setRotation(IHLModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
