package ihl.processing.chemistry;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class AbsorberCoolerModel extends ModelBase
{
  //fields
    ModelRenderer Base;
  
  public AbsorberCoolerModel()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape1", 26, 0);
    setTextureOffset("Base.Shape2", 0, 17);
    setTextureOffset("Base.Shape4", 30, 16);
    setTextureOffset("Base.Shape3", 0, 15);
    setTextureOffset("Base.Shape5", 2, 12);
    setTextureOffset("Base.Shape6", 0, 2);
    setTextureOffset("Base.Shape7", 24, 4);
    setTextureOffset("Base.Shape8", 50, 0);
    setTextureOffset("Base.Shape9", 0, 0);
    setTextureOffset("Base.Shape10", 0, 0);
    setTextureOffset("Base.Shape11", 0, 0);
    setTextureOffset("Base.Shape12", 0, 0);
    setTextureOffset("Base.Shape13", 0, 0);
    setTextureOffset("Base.Shape14", 0, 0);
    setTextureOffset("Base.Shape15", 0, 0);
    setTextureOffset("Base.Shape16", 0, 0);
    setTextureOffset("Base.Shape17", 0, 0);
    setTextureOffset("Base.Shape18", 0, 0);
    setTextureOffset("Base.Shape19", 0, 0);
    setTextureOffset("Base.Shape20", 0, 0);
    setTextureOffset("Base.Shape21", 0, 0);
    setTextureOffset("Base.Shape22", 0, 0);
    setTextureOffset("Base.Shape23", 0, 24);
    setTextureOffset("Base.Shape24", 7, 12);
    setTextureOffset("Base.Shape25", 21, 8);
    setTextureOffset("Base.Shape26", 12, 24);
    setTextureOffset("Base.Shape27", 0, 0);
    setTextureOffset("Base.Shape28", 0, 18);
    setTextureOffset("Base.Shape29", 21, 0);
    
    Base = new ModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 23F, 0F);
    Base.mirror = true;
      Base.addBox("Shape1", 6F, -14F, -7F, 1, 14, 14);
      Base.addBox("Shape2", -7F, -1F, -8F, 14, 1, 1);
      Base.addBox("Shape4", 6F, -14F, -8F, 1, 13, 1);
      Base.addBox("Shape3", -7F, -1F, 7F, 14, 1, 1);
      Base.addBox("Shape5", -7F, -15F, -8F, 6, 1, 16);
      Base.addBox("Shape6", -7F, -14F, 7F, 1, 13, 1);
      Base.addBox("Shape7", -7F, -14F, -7F, 1, 14, 14);
      Base.addBox("Shape8", -7F, -14F, -8F, 1, 13, 1);
      Base.addBox("Shape9", 1F, 0F, -8F, 7, 1, 16);
      Base.addBox("Shape10", 6F, -14F, 7F, 1, 13, 1);
      Base.addBox("Shape11", 3.5F, -13F, -7F, 1, 1, 14);
      Base.addBox("Shape12", 1.5F, -13F, -7F, 1, 1, 14);
      Base.addBox("Shape13", -2.5F, -13F, -7F, 1, 1, 14);
      Base.addBox("Shape14", -4.5F, -13F, -7F, 1, 1, 14);
      Base.addBox("Shape15", -0.5F, -13F, -7F, 1, 1, 14);
      Base.addBox("Shape16", -6F, -12.5F, -1.5F, 12, 1, 1);
      Base.addBox("Shape17", -6F, -12.5F, -3.5F, 12, 1, 1);
      Base.addBox("Shape18", -6F, -12.5F, -5.5F, 12, 1, 1);
      Base.addBox("Shape19", -6F, -12.5F, 0.5F, 12, 1, 1);
      Base.addBox("Shape20", -6F, -12.5F, 2.5F, 12, 1, 1);
      Base.addBox("Shape21", -6F, -12.5F, 4.5F, 12, 1, 1);
      Base.addBox("Shape22", -6F, 9F, 7F, 12, 2, 1);
      Base.addBox("Shape23", -6F, -14F, -8F, 12, 2, 1);
      Base.addBox("Shape24", 1F, -15F, -8F, 6, 1, 16);
      Base.addBox("Shape25", -1F, -15F, 1F, 2, 1, 7);
      Base.addBox("Shape26", -1F, -15F, -8F, 2, 1, 7);
      Base.addBox("Shape27", -8F, 0F, -8F, 7, 1, 16);
      Base.addBox("Shape28", -1F, 0F, 1F, 2, 1, 7);
      Base.addBox("Shape29", -1F, 0F, -8F, 2, 1, 7);
  }
}
