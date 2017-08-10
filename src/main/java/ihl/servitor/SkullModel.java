package ihl.servitor;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SkullModel extends ModelBase
{
  //fields
    ModelRenderer Base;
  
  public SkullModel()
  {
	    textureWidth = 256;
	    textureHeight = 256;
	    setTextureOffset("Base.Shape2", 110, 11);
	    setTextureOffset("Base.Shape3", 59, 62);
	    setTextureOffset("Base.Shape4", 83, 59);
	    setTextureOffset("Base.Shape5", 83, 59);
	    setTextureOffset("Base.Shape6", 0, 18);
	    setTextureOffset("Base.Shape8", 0, 26);
	    setTextureOffset("Base.Shape9", 2, 33);
	    setTextureOffset("Base.Shape10", 26, 37);
	    setTextureOffset("Base.Shape11", 28, 157);
	    setTextureOffset("Base.Shape12", 83, 111);
	    setTextureOffset("Base.Shape13", 67, 110);
	    setTextureOffset("Base.Shape14", 72, 99);
	    setTextureOffset("Base.Shape15", 92, 114);
	    setTextureOffset("Base.Shape16", 102, 112);
	    setTextureOffset("Base.Shape17", 82, 107);
	    setTextureOffset("Base.Shape18", 71, 108);
	    setTextureOffset("Base.Shape19", 89, 114);
	    setTextureOffset("Base.Shape20", 90, 124);
	    setTextureOffset("Base.Shape21", 106, 31);
	    setTextureOffset("Base.Shape22", 77, 139);
	    setTextureOffset("Base.Shape23", 115, 193);
	    setTextureOffset("Base.Shape24", 1, 163);
	    setTextureOffset("Base.Shape25", 127, 117);
	    setTextureOffset("Base.Shape26", 81, 116);
	    setTextureOffset("Base.Shape27", 81, 115);
	    setTextureOffset("Base.Shape28", 81, 114);
	    setTextureOffset("Base.Shape29", 6, 167);
	    setTextureOffset("Base.Shape1", 0, 154);
	    setTextureOffset("Base.Shape7", 92, 124);
	    setTextureOffset("Base.Shape30", 89, 124);
	    setTextureOffset("Base.Shape31", 128, 193);
	    setTextureOffset("Base.Shape32", 106, 88);
	    setTextureOffset("Base.Shape33", 109, 70);
	    setTextureOffset("Base.Shape34", 166, 109);
	    setTextureOffset("Base.Shape35", 100, 53);
	    setTextureOffset("Base.Shape36", 169, 98);
	    setTextureOffset("Base.Shape37", 99, 81);
	    setTextureOffset("Base.Shape38", 101, 108);
	    setTextureOffset("Base.Shape39", 92, 85);
	    setTextureOffset("Base.Shape40", 22, 89);
	    setTextureOffset("Base.Shape41", 62, 151);
	    setTextureOffset("Base.Shape42", 37, 97);
	    setTextureOffset("Base.Shape43", 9, 101);
	    setTextureOffset("Base.Shape44", 77, 15);
	    setTextureOffset("Base.Shape45", 18, 145);
	    setTextureOffset("Base.Shape46", 28, 156);
	    setTextureOffset("Base.Shape47", 28, 155);
	    setTextureOffset("Base.Shape48", 106, 104);
	    setTextureOffset("Base.Shape49", 77, 129);
	    setTextureOffset("Base.Shape50", 25, 82);
	    setTextureOffset("Base.Shape51", 46, 33);
	    setTextureOffset("Base.Shape52", 101, 85);
	    setTextureOffset("Base.Shape53", 141, 38);
	    setTextureOffset("Base.Shape54", 24, 149);
	    setTextureOffset("Base.Shape55", 83, 132);
	    setTextureOffset("Base.Shape56", 99, 132);
	    setTextureOffset("Base.Shape57", 30, 186);
	    setTextureOffset("Base.Shape58", 142, 220);
    
	    Base = new ModelRenderer(this, "Base");
	    Base.setRotationPoint(0F, -30F, -30F);
	    setRotation(Base, 0F, 0F, 0F);
	    Base.mirror = false;
	      Base.addBox("Shape2", -25F, -24F, 18F, 8, 24, 34);
	      Base.addBox("Shape3", -23F, -28F, 13F, 46, 4, 38);
	      Base.addBox("Shape4", -22F, -30F, 14F, 44, 2, 37);
	      Base.addBox("Shape5", -21F, -32F, 12F, 42, 2, 39);
	      Base.addBox("Shape6", -19F, -34F, 9F, 38, 2, 43);
	      Base.addBox("Shape8", -17F, -36F, 11F, 34, 2, 46);
	      Base.addBox("Shape9", -15F, -38F, 13F, 28, 2, 42);
	      Base.addBox("Shape10", -9F, -40F, 21F, 20, 2, 24);
	      Base.addBox("Shape11", -13F, -24F, 1F, 26, 26, 3);
	      Base.addBox("Shape12", -16F, -11.2F, 66F, 14, 4, 2);
	      Base.addBox("Shape13", 2F, -11.2F, 66F, 14, 4, 2);
	      Base.addBox("Shape14", -9F, -24F, 66F, 18, 17, 1);
	      Base.addBox("Shape15", -2F, -7F, 65F, 4, 7, 3);
	      Base.addBox("Shape16", -2F, -4F, 68F, 4, 4, 1);
	      Base.addBox("Shape17", 2F, -10F, 64F, 3, 4, 5);
	      Base.addBox("Shape18", -5F, -10F, 64F, 3, 4, 5);
	      Base.addBox("Shape19", -6F, 0F, 65F, 4, 1, 1);
	      Base.addBox("Shape20", -16F, 1F, 63F, 12, 5, 4);
	      Base.addBox("Shape21", -24F, 0F, 18F, 8, 4, 38);
	      Base.addBox("Shape22", -8F, 10F, 66F, 16, 5, 2);
	      Base.addBox("Shape23", -16F, 4F, 7F, 9, 3, 56);
	      Base.addBox("Shape24", -10F, 7F, 16F, 8, 3, 52);
	      Base.addBox("Shape25", -10F, 16F, 17F, 20, 1, 12);
	      Base.addBox("Shape26", -10F, 15F, 15F, 20, 1, 15);
	      Base.addBox("Shape27", -11F, 14F, 13F, 22, 1, 18);
	      Base.addBox("Shape28", -11F, 13F, 11F, 22, 1, 21);
	      Base.addBox("Shape29", -12F, 7.1F, 8F, 24, 6, 26);
	      Base.addBox("Shape1", 2F, 7F, 16F, 8, 3, 52);
	      Base.addBox("Shape7", 2F, 0F, 65F, 4, 1, 1);
	      Base.addBox("Shape30", 4F, 1F, 63F, 12, 5, 4);
	      Base.addBox("Shape31", 7F, 4F, 7F, 9, 3, 56);
	      Base.addBox("Shape32", 16F, 0F, 18F, 8, 4, 38);
	      Base.addBox("Shape33", 17F, -24F, 18F, 8, 24, 34);
	      Base.addBox("Shape34", 18F, -7F, 52F, 6, 7, 9);
	      Base.addBox("Shape35", -24F, -7F, 52F, 6, 7, 9);
	      Base.addBox("Shape36", 16F, -13F, 56F, 6, 16, 6);
	      Base.addBox("Shape37", 18F, -24F, 52F, 2, 17, 12);
	      Base.addBox("Shape38", -22F, -13F, 56F, 6, 16, 6);
	      Base.addBox("Shape39", -20F, -24F, 52F, 2, 17, 12);
	      Base.addBox("Shape40", -18F, -28F, 51F, 36, 4, 13);
	      Base.addBox("Shape41", -17.5F, -30F, 34F, 35, 2, 29);
	      Base.addBox("Shape42", -18F, -24F, 62F, 36, 17, 4);
	      Base.addBox("Shape43", -17F, -32F, 7F, 34, 2, 54);
	      Base.addBox("Shape44", -16.2F, -34F, 51F, 32, 2, 8);
	      Base.addBox("Shape45", -19.5F, -33F, 8F, 39, 37, 8);
	      Base.addBox("Shape46", -14F, -26F, 2F, 28, 30, 1);
	      Base.addBox("Shape47", -15F, -28F, 3F, 30, 32, 2);
	      Base.addBox("Shape48", -18F, -1F, 56F, 3, 5, 8);
	      Base.addBox("Shape49", 15F, -1F, 56F, 3, 5, 8);
	      Base.addBox("Shape50", -5F, -39F, 15F, 12, 1, 6);
	      Base.addBox("Shape51", -6F, -39F, 45F, 12, 1, 6);
	      Base.addBox("Shape52", -21.5F, -31F, 11F, 43, 34, 6);
	      Base.addBox("Shape53", -23.5F, -24F, 13F, 47, 26, 5);
	      Base.addBox("Shape54", -17F, -30F, 5F, 34, 34, 3);
	      Base.addBox("Shape55", 8F, 10F, 58F, 2, 4, 9);
	      Base.addBox("Shape56", -10F, 10F, 58F, 2, 4, 8);
	      Base.addBox("Shape57", -9F, 4F, 3F, 18, 5, 5);
	      Base.addBox("Shape58", -8F, 10F, 41F, 16, 1, 25);
  }
  
  @Override
public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    Base.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
