package ihl.collector;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ChargerEjectorModel extends ModelBase {
	  //fields
    ModelRenderer Base;
    ModelRenderer ItemPieces;
    ModelRenderer LightOnA;
    ModelRenderer LightOnB;
    ModelRenderer LightOnC;
    ModelRenderer LightOffA;
    ModelRenderer LightOffB;
    ModelRenderer LightOffC;
    
    public ChargerEjectorModel()
    {
    	   textureWidth = 64;
    	    textureHeight = 64;
    	    setTextureOffset("Base.Shape1", 0, 0);
    	    setTextureOffset("Base.Shape2", 0, 0);
    	    setTextureOffset("Base.Shape3", 44, 17);
    	    setTextureOffset("Base.Shape4", 28, 17);
    	    setTextureOffset("Base.Shape6", 0, 0);
    	    setTextureOffset("Base.Shape15", 0, 0);
    	    setTextureOffset("Base.Shape16", 0, 0);
    	    setTextureOffset("Base.Shape17", 0, 0);
    	    setTextureOffset("Base.Shape18", 0, 0);
    	    setTextureOffset("Base.Shape19", 0, 0);
    	    setTextureOffset("Base.Shape20", 0, 0);
    	    setTextureOffset("Base.Shape21", 0, 0);
    	    setTextureOffset("Base.Shape22", 0, 0);
    	    setTextureOffset("Base.Shape23", 0, 0);
    	    setTextureOffset("Base.Shape24", 0, 0);
    	    setTextureOffset("Base.Shape25", 0, 0);
    	    setTextureOffset("Base.Shape26", 0, 0);
    	    setTextureOffset("Base.Shape27", 0, 0);
    	    setTextureOffset("Base.Shape28", 0, 0);
    	    setTextureOffset("Base.Shape29", 0, 0);
    	    setTextureOffset("Base.Shape30", 0, 0);
    	    setTextureOffset("Base.Shape31", 0, 0);
    	    setTextureOffset("Base.Shape32", 0, 0);
    	    setTextureOffset("Base.Shape33", 0, 0);
    	    setTextureOffset("Base.Shape34", 0, 0);
    	    setTextureOffset("Base.Shape35", 0, 0);
    	    setTextureOffset("Base.Shape36", 0, 0);
    	    
    	    setTextureOffset("ItemPieces.Shape37", 0, 32);
    	    setTextureOffset("ItemPieces.Shape38", 0, 32);
    	    setTextureOffset("ItemPieces.Shape39", 0, 32);
    	    
    	    setTextureOffset("LightOnA.Shape7", 0, 17);
    	    setTextureOffset("LightOnA.Shape5", 0, 24);
    	    setTextureOffset("LightOnA.Shape8", 14, 19);
    	    setTextureOffset("LightOnA.Shape9", 14, 17);
    	    setTextureOffset("LightOnB.Shape10", 14, 21);
    	    setTextureOffset("LightOnB.Shape11", 14, 23);
    	    setTextureOffset("LightOnB.Shape12", 14, 25);
    	    setTextureOffset("LightOnB.Shape13", 14, 28);
    	    setTextureOffset("LightOnC.Shape14", 20, 25);
    	    
    	    setTextureOffset("LightOffA.ShapeA7", 0, 17+14);
    	    setTextureOffset("LightOffA.ShapeA5", 0, 24+14);
    	    setTextureOffset("LightOffA.ShapeA8", 14, 19+14);
    	    setTextureOffset("LightOffA.ShapeA9", 14, 17+14);
    	    setTextureOffset("LightOffB.ShapeA10", 14, 21+14);
    	    setTextureOffset("LightOffB.ShapeA11", 14, 23+14);
    	    setTextureOffset("LightOffB.ShapeA12", 14, 25+14);
    	    setTextureOffset("LightOffB.ShapeA13", 14, 28+14);
    	    setTextureOffset("LightOffC.ShapeA14", 20, 25+14);
    	    
    	    Base = new ModelRenderer(this, "Base");
    	    Base.setRotationPoint(0F, 0F, 0F);
    	    Base.mirror = true;
    	      Base.addBox("Shape1", -8F, 23F, -8F, 16, 1, 16);
    	      Base.addBox("Shape2", -8F, 11F, -8F, 16, 1, 16);
    	      Base.addBox("Shape3", -1F, 0F, -1F, 2, 10, 2);
    	      Base.addBox("Shape4", -2F, 12F, -2F, 4, 10, 4);
    	      Base.addBox("Shape6", -4F, -1F, -4F, 8, 1, 8);
    	      Base.addBox("Shape15", -1F, 22F, -8F, 2, 1, 16);
    	      Base.addBox("Shape16", 7F, 22F, -8F, 1, 1, 16);
    	      Base.addBox("Shape17", -8F, 22F, -8F, 1, 1, 16);
    	      Base.addBox("Shape18", 2F, 22F, -1F, 5, 1, 2);
    	      Base.addBox("Shape19", -7F, 22F, -1F, 5, 1, 2);
    	      Base.addBox("Shape20", 1F, 22F, 7F, 6, 1, 1);
    	      Base.addBox("Shape21", -7F, 22F, 7F, 6, 1, 1);
    	      Base.addBox("Shape22", 1F, 22F, -8F, 6, 1, 1);
    	      Base.addBox("Shape23", -7F, 22F, -8F, 6, 1, 1);
    	      Base.addBox("Shape24", -8F, 10F, -1F, 16, 1, 2);
    	      Base.addBox("Shape25", -8F, 10F, -8F, 16, 1, 1);
    	      Base.addBox("Shape26", -8F, 10F, 7F, 16, 1, 1);
    	      Base.addBox("Shape27", -1F, 10F, 1F, 2, 1, 6);
    	      Base.addBox("Shape28", -1F, 10F, -7F, 2, 1, 6);
    	      Base.addBox("Shape29", -8F, 10F, -7F, 1, 1, 6);
    	      Base.addBox("Shape30", -8F, 10F, 1F, 1, 1, 6);
    	      Base.addBox("Shape31", 7F, 10F, 1F, 1, 1, 6);
    	      Base.addBox("Shape32", 7F, 10F, -7F, 1, 1, 6);
    	      Base.addBox("Shape33", 3F, -2F, -4F, 1, 1, 8);
    	      Base.addBox("Shape34", -4F, -2F, -4F, 1, 1, 8);
    	      Base.addBox("Shape35", -3F, -2F, -4F, 6, 1, 1);
    	      Base.addBox("Shape36", -3F, -2F, 3F, 6, 1, 1);
    	      
    	      ItemPieces = new ModelRenderer(this, "ItemPieces");
    	      ItemPieces.setRotationPoint(0F, 0F, 0F);
    	      ItemPieces.mirror = true;
    	        ItemPieces.addBox("Shape37", -3F, -2F, -3F, 6, 1, 6);
    	        ItemPieces.addBox("Shape38", -7F, 10.1F, -7F, 14, 1, 14);
    	        ItemPieces.addBox("Shape39", -7F, 22.1F, -7F, 14, 1, 14);
    	      
    	    LightOnA = new ModelRenderer(this, "LightOnA");
    	    LightOnA.setRotationPoint(0F, 0F, 0F);
    	    LightOnA.mirror = true;
    	      LightOnA.addBox("Shape7", 6F, 22F, 1F, 1, 1, 6);
    	      LightOnA.addBox("Shape5", 1F, 22F, 1F, 1, 1, 6);
    	      LightOnA.addBox("Shape8", 2F, 22F, 1F, 4, 1, 1);
    	      LightOnA.addBox("Shape9", 2F, 22F, 6F, 4, 1, 1);
    	    LightOnB = new ModelRenderer(this, "LightOnB");
    	    LightOnB.setRotationPoint(0F, 0F, 0F);
    	    LightOnB.mirror = true;
    	      LightOnB.addBox("Shape10", 2F, 22F, 2F, 4, 1, 1);
    	      LightOnB.addBox("Shape11", 2F, 22F, 5F, 4, 1, 1);
    	      LightOnB.addBox("Shape12", 2F, 22F, 3F, 1, 1, 2);
    	      LightOnB.addBox("Shape13", 5F, 22F, 3F, 1, 1, 2);
    	    LightOnC = new ModelRenderer(this, "LightOnC");
    	    LightOnC.setRotationPoint(0F, 0F, 0F);
    	    LightOnC.mirror = true;
    	      LightOnC.addBox("Shape14", 3F, 22F, 3F, 2, 1, 2);
    	      
    	 	    LightOffA = new ModelRenderer(this, "LightOffA");
        	    LightOffA.setRotationPoint(0F, 0F, 0F);
        	    LightOffA.mirror = true;
        	      LightOffA.addBox("ShapeA7", 6F, 22F, 1F, 1, 1, 6);
        	      LightOffA.addBox("ShapeA5", 1F, 22F, 1F, 1, 1, 6);
        	      LightOffA.addBox("ShapeA8", 2F, 22F, 1F, 4, 1, 1);
        	      LightOffA.addBox("ShapeA9", 2F, 22F, 6F, 4, 1, 1);
        	    LightOffB = new ModelRenderer(this, "LightOffB");
        	    LightOffB.setRotationPoint(0F, 0F, 0F);
        	    LightOffB.mirror = true;
        	      LightOffB.addBox("ShapeA10", 2F, 22F, 2F, 4, 1, 1);
        	      LightOffB.addBox("ShapeA11", 2F, 22F, 5F, 4, 1, 1);
        	      LightOffB.addBox("ShapeA12", 2F, 22F, 3F, 1, 1, 2);
        	      LightOffB.addBox("ShapeA13", 5F, 22F, 3F, 1, 1, 2);
        	    LightOffC = new ModelRenderer(this, "LightOffC");
        	    LightOffC.setRotationPoint(0F, 0F, 0F);
        	    LightOffC.mirror = true;
        	      LightOffC.addBox("ShapeA14", 3F, 22F, 3F, 2, 1, 2);
    }

}
