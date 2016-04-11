package ihl.collector;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CollectorModel extends ModelBase {
	  //fields
    ModelRenderer Base;
    ModelRenderer Eleron;
    ModelRenderer Indicator;
    ModelRenderer Flares;
	  
	    public CollectorModel()
	    {
	        textureWidth = 256;
	        textureHeight = 128;
	        int barSize=31;
	        setTextureOffset("Base.Shape1", 34, 10);
	        setTextureOffset("Base.Shape6", 74, 41);
	        setTextureOffset("Base.Shape8", 22, 6);
	        setTextureOffset("Base.Shape27", 0, 0);
	        setTextureOffset("Base.Shape9", 13, 3);
	        setTextureOffset("Eleron.Shape4", 0, 6);
	        setTextureOffset("Eleron.Shape2", 0, 7);
	        setTextureOffset("Eleron.Shape3", 0, 0);
	        setTextureOffset("Eleron.Shape5", 0, 0);
	        setTextureOffset("Eleron.Shape7", 74, 0);
	        setTextureOffset("Eleron.Shape14", 56, 0);
	        setTextureOffset("Eleron.Shape11", 48, 0);
	        setTextureOffset("Eleron.Shape10", 52, 0);
	        setTextureOffset("Eleron.Shape12", 50, 0);
	        setTextureOffset("Eleron.Shape13", 54, 0);
	        setTextureOffset("Eleron.Shape15", 58, 0);
	        setTextureOffset("Eleron.Shape18", 0, 0);
	        setTextureOffset("Eleron.Shape16", 0, 3);
	        setTextureOffset("Eleron.Shape17", 0, 4);
	        for(int i=0;i<barSize;i++)
	        {
	        	setTextureOffset("Indicator.ShapeA"+i, 12, 40+i);
	        }
	        setTextureOffset("Flares.Shape20", 6, 47);
	        setTextureOffset("Flares.Shape21", 6, 42);
	        setTextureOffset("Flares.Shape22", 6, 42);
	        setTextureOffset("Flares.Shape23", 6, 47);
	        setTextureOffset("Flares.Shape24", 0, 41);
	        setTextureOffset("Flares.Shape25", 0, 80);
	        
	        Base = new ModelRenderer(this, "Base");
	        Base.setRotationPoint(0F, 0F, 0F);
	        setRotation(Base, 0F, 0F, 0F);
	        Base.mirror = true;
	          Base.addBox("Shape1", -9F, 0F, -9F, 18, 1, 18);
	          Base.addBox("Shape6", -9F, 5F, -9F, 18, 6, 18);
	          Base.addBox("Shape8", -13F, 1F, -13F, 26, 1, 26);
	          Base.addBox("Shape27", -20F, 4F, -20F, 40, 1, 40);
	          Base.addBox("Shape9", -16F, 2F, -16F, 32, 1, 32);
	        Eleron = new ModelRenderer(this, "Eleron");
	        Eleron.setRotationPoint(0F, 0F, 0F);
	        setRotation(Eleron, 0F, 0F, 0F);
	        Eleron.mirror = true;
	          Eleron.addBox("Shape4", 32F, 5F, 31F, 8, 20, 1);
	          Eleron.addBox("Shape2", 31F, 5F, 39F, 8, 20, 1);
	          Eleron.addBox("Shape3", 39F, 5F, 32F, 1, 20, 7);
	          Eleron.addBox("Shape5", 31F, 5F, 31F, 1, 20, 8);
	          Eleron.addBox("Shape7", 15F, 3F, 15F, 3, 1, 3);
	          Eleron.addBox("Shape14", 18F, 9F, 18F, 12, 1, 12);
	          Eleron.addBox("Shape11", 6F, 5F, 6F, 16, 1, 16);
	          Eleron.addBox("Shape10", 12F, 7F, 12F, 14, 1, 14);
	          Eleron.addBox("Shape12", 9F, 6F, 9F, 15, 1, 15);
	          Eleron.addBox("Shape13", 15F, 8F, 15F, 13, 1, 13);
	          Eleron.addBox("Shape15", 21F, 10F, 21F, 11, 1, 11);
	          Eleron.addBox("Shape18", 29F, 11F, 22F, 2, 2, 7);
	          Eleron.addBox("Shape16", 23F, 11F, 29F, 16, 2, 2);
	          Eleron.addBox("Shape17", 29F, 11F, 31F, 2, 2, 8);
	        Indicator = new ModelRenderer(this, "Indicator");
	        Indicator.setRotationPoint(0F, 0F, 0F);
	        setRotation(Indicator, 0F, 0F, 0F);
	        Indicator.mirror = true;
	        for(int i=0;i<barSize;i++)
	        {
	          Indicator.addBox("ShapeA"+i, -15F, 3F, 15F, 30, 1, 1);
	        }
	        Flares = new ModelRenderer(this, "Flares");
	        Flares.setRotationPoint(0F, 0F, 0F);
	        setRotation(Flares, 0F, 0F, 0F);
	        Flares.mirror = true;
	          Flares.addBox("Shape20", 33F, 5F, 32F, 6, 20, 1);
	          Flares.addBox("Shape21", 32F, 5F, 32F, 1, 20, 6);
	          Flares.addBox("Shape22", 38F, 5F, 33F, 1, 20, 6);
	          Flares.addBox("Shape23", 32F, 5F, 38F, 6, 20, 1);
	          Flares.addBox("Shape24", 39F, 5F, 39F, 1, 20, 1);
	          Flares.addBox("Shape25", 6F, 11F, -6F, 1, 1, 13);
	    }

	  private void setRotation(ModelRenderer model, float x, float y, float z)
	  {
	    model.rotateAngleX = x;
	    model.rotateAngleY = y;
	    model.rotateAngleZ = z;
	  }
	  
	  @Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	  {
	    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	  }
}


