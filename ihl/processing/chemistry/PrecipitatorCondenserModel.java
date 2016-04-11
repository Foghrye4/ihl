package ihl.processing.chemistry;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PrecipitatorCondenserModel extends ModelBase
{
  //fields
    ModelRenderer Base;
  
  public PrecipitatorCondenserModel()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("Base.Shape1", 22, 0);
    setTextureOffset("Base.Shape2", 22, 17);
    setTextureOffset("Base.Shape4", 30, 16);
    setTextureOffset("Base.Shape3", 30, 15);
    setTextureOffset("Base.Shape6", 30, 2);
    setTextureOffset("Base.Shape7", 20, 0);
    setTextureOffset("Base.Shape8", 50, 0);
    setTextureOffset("Base.Shape9", 0, 0);
    setTextureOffset("Base.Shape10", 24, 0);
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
    setTextureOffset("Base.Shape25", 0, 1);
    setTextureOffset("Base.Shape26", 12, 24);
    setTextureOffset("Base.Shape27", 0, 0);
    setTextureOffset("Base.Shape28", 0, 18);
    setTextureOffset("Base.Shape29", 21, 0);
    setTextureOffset("Base.Shape30", 0, 0);
    setTextureOffset("Base.Shape31", 0, 0);
    setTextureOffset("Base.Shape32", 0, 0);
    setTextureOffset("Base.Shape33", 0, 0);
    setTextureOffset("Base.Shape34", 0, 0);
    setTextureOffset("Base.Shape35", 0, 0);
    setTextureOffset("Base.Shape36", 0, 0);
    setTextureOffset("Base.Shape37", 0, 0);
    setTextureOffset("Base.Shape38", 0, 0);
    setTextureOffset("Base.Shape39", 0, 0);
    setTextureOffset("Base.Shape40", 0, 0);
    setTextureOffset("Base.Shape5", 0, 0);
    setTextureOffset("Base.Shape23", 31, 0);
    setTextureOffset("Base.Shape24", 10, 0);
    setTextureOffset("Base.Shape41", 27, 26);
    setTextureOffset("Base.Shape42", 0, 0);
    setTextureOffset("Base.Shape43", 0, 0);
    setTextureOffset("Base.Shape44", 0, 0);
    setTextureOffset("Base.Shape45", 0, 0);
    setTextureOffset("Base.Shape46", 0, 0);
    
    Base = new ModelRenderer(this, "Base");
    Base.setRotationPoint(0F, 23F, 0F);
    Base.mirror = true;
      Base.addBox("Shape1", 6F, -15F, -7F, 1, 12, 14);
      Base.addBox("Shape2", -7F, -3F, -8F, 14, 3, 1);
      Base.addBox("Shape4", 6F, -15F, -8F, 1, 12, 1);
      Base.addBox("Shape3", -7F, -3F, 7F, 14, 3, 1);
      Base.addBox("Shape6", -7F, -15F, 7F, 1, 12, 1);
      Base.addBox("Shape7", -7F, -15F, -7F, 1, 15, 14);
      Base.addBox("Shape8", -7F, -15F, -8F, 1, 12, 1);
      Base.addBox("Shape9", 1F, 0F, -8F, 6, 1, 16);
      Base.addBox("Shape10", 6F, -15F, 7F, 1, 12, 1);
      Base.addBox("Shape11", -4F, -13F, 3F, 1, 12, 3);
      Base.addBox("Shape12", 4F, -2F, -1F, 2, 1, 3);
      Base.addBox("Shape13", 3F, -13F, -1F, 1, 12, 3);
      Base.addBox("Shape14", -3F, -13F, 5F, 9, 12, 1);
      Base.addBox("Shape15", -6F, -2F, -2F, 12, 1, 1);
      Base.addBox("Shape16", -4F, -13F, -5F, 1, 12, 3);
      Base.addBox("Shape17", -6F, -2F, 6F, 12, 1, 1);
      Base.addBox("Shape18", -6F, -2F, -7F, 12, 1, 2);
      Base.addBox("Shape19", -6F, -2F, -5F, 2, 1, 3);
      Base.addBox("Shape20", -6F, -2F, 2F, 12, 1, 1);
      Base.addBox("Shape21", -6F, -2F, 3F, 2, 1, 3);
      Base.addBox("Shape25", -6F, -15F, 7F, 12, 3, 1);
      Base.addBox("Shape26", -6F, -15F, -8F, 12, 3, 1);
      Base.addBox("Shape27", -8F, 0F, -8F, 7, 1, 16);
      Base.addBox("Shape28", -1F, 0F, 1F, 2, 1, 7);
      Base.addBox("Shape29", -1F, 0F, -8F, 2, 1, 7);
      Base.addBox("Shape30", -3F, -13F, 3F, 9, 12, 1);
      Base.addBox("Shape31", -6F, -13F, 1F, 9, 12, 1);
      Base.addBox("Shape32", -6F, -13F, -1F, 9, 12, 1);
      Base.addBox("Shape33", -3F, -13F, -3F, 9, 12, 1);
      Base.addBox("Shape34", -3F, -13F, -5F, 9, 12, 1);
      Base.addBox("Shape35", -6F, -13F, -7F, 12, 1, 2);
      Base.addBox("Shape36", -6F, -13F, -2F, 12, 1, 1);
      Base.addBox("Shape37", -6F, -13F, 2F, 12, 1, 1);
      Base.addBox("Shape38", -6F, -13F, 6F, 12, 1, 1);
      Base.addBox("Shape39", -6F, -13F, -5F, 2, 1, 3);
      Base.addBox("Shape40", -6F, -13F, 3F, 2, 1, 3);
      Base.addBox("Shape5", 4F, -13F, -1F, 2, 1, 3);
      Base.addBox("Shape23", 6F, -2F, -7F, 1, 2, 14);
      Base.addBox("Shape24", 6F, -3F, 3F, 1, 1, 4);
      Base.addBox("Shape41", 6F, -3F, -7F, 1, 1, 5);
      Base.addBox("Shape42", 7F, -4F, -2F, 4, 1, 5);
      Base.addBox("Shape43", 8F, 0F, -2F, 1, 1, 5);
      Base.addBox("Shape44", 10F, -3F, -2F, 1, 4, 5);
      Base.addBox("Shape45", 7F, -4F, 3F, 4, 5, 1);
      Base.addBox("Shape46", 7F, -4F, -3F, 4, 5, 1);
  }
}
