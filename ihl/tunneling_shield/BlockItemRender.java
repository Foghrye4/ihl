package ihl.tunneling_shield;

import ic2.core.block.RenderBlockDefault;
import ihl.items_blocks.IHLItemBlock;
import ihl.model.IHLModelRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class BlockItemRender implements IItemRenderer{
	private ModelBase model;
	private ResourceLocation tex;
	private int renderFrom=0;
	private int renderTo=0;
	private RenderBlockDefault rbd;
	private float amendment=0.0F;
	private float inventoryOffset=0.0F;
	private boolean renderHand;

public BlockItemRender()
{
	rbd=new RenderBlockDefault();
}

public BlockItemRender(ModelBase model1, ResourceLocation texture, int renderFrom1, int renderTo1, float amendment1, float inventoryOffset1)
{
	this.model=model1;
	this.tex=texture;
	this.renderFrom=renderFrom1;
	this.renderTo=renderTo1;
	this.amendment=amendment1;
	this.inventoryOffset=inventoryOffset1;
	this.renderHand=false;
}

public BlockItemRender(ModelBase model1, ResourceLocation texture, int renderFrom1, int renderTo1, float amendment1, float inventoryOffset1, boolean renderHand1)
{
	this.model=model1;
	this.tex=texture;
	this.renderFrom=renderFrom1;
	this.renderTo=renderTo1;
	this.amendment=amendment1;
	this.inventoryOffset=inventoryOffset1;
	this.renderHand=renderHand1;
}

@Override
public boolean handleRenderType(ItemStack item, ItemRenderType type) {
	switch (type) {
		case ENTITY:
			return true;
		case EQUIPPED:
			return true;
		case EQUIPPED_FIRST_PERSON:
			return true;
		case INVENTORY:
			return true;
		default:
			return false;
	}
}

@Override
public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
	return true;
}

@Override
public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
	if(tex!=null)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
	}
	GL11.glPushMatrix();
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	IHLItemBlock bItem =(IHLItemBlock) item.getItem();
	Block block = bItem.getBlockContained();
	GL11.glScalef(1F, -1F+amendment, -1F);
	switch (type) {
		case ENTITY:
		    GL11.glTranslatef(0,-1.5F,0);
		    if(model!=null)
		    {
		    	for(int i = this.renderFrom;i<model.boxList.size()-this.renderTo;i++)
		    	{
		    		if(model.boxList.get(i) instanceof ModelRenderer)
		    		{
		    			ModelRenderer piece = (ModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    		else if(model.boxList.get(i) instanceof IHLModelRenderer)
		    		{
		    			IHLModelRenderer piece = (IHLModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    	}
		    }
		    else
		    {
		    	rbd.renderInventoryBlock(block, 3, 0, new RenderBlocks());
		    }
		    GL11.glPopMatrix();
			break;
		case EQUIPPED:
		    GL11.glTranslatef(0.25F,-2F,-0.75F);
		    if(model!=null)
		    {
		    	for(int i = this.renderFrom;i<model.boxList.size()-this.renderTo;i++)
		    	{
		    		if(model.boxList.get(i) instanceof ModelRenderer)
		    		{
		    			ModelRenderer piece = (ModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    		else if(model.boxList.get(i) instanceof IHLModelRenderer)
		    		{
		    			IHLModelRenderer piece = (IHLModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    	}
		    }
		    else
		    {
		    	rbd.renderInventoryBlock(block, 3, 0, new RenderBlocks());
		    }
		    GL11.glPopMatrix();
            break;
		case EQUIPPED_FIRST_PERSON:
			if(this.renderHand)
			{
			    RenderPlayer var25;
			    EntityClientPlayerMP var3 = Minecraft.getMinecraft().thePlayer;
				Minecraft.getMinecraft().getTextureManager().bindTexture(var3.getLocationSkin());
				var25 = (RenderPlayer)RenderManager.instance.getEntityRenderObject(Minecraft.getMinecraft().thePlayer);
				GL11.glRotatef(135F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(35F, 0.0F, 1.0F, 0.0F);
				var25.renderFirstPersonArm(Minecraft.getMinecraft().thePlayer);
				GL11.glRotatef(-35F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-135F, 1.0F, 0.0F, 0.0F);
				Minecraft.getMinecraft().renderEngine.bindTexture(tex);
			    GL11.glTranslatef(0.0F,0.2F,0F);
			}
		    GL11.glTranslatef(-0.25F,-2.0F-amendment,0F);
		    if(model!=null)
		    {
		    	for(int i = this.renderFrom;i<model.boxList.size()-this.renderTo;i++)
		    	{
		    		if(model.boxList.get(i) instanceof ModelRenderer)
		    		{
		    			ModelRenderer piece = (ModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    		else if(model.boxList.get(i) instanceof IHLModelRenderer)
		    		{
		    			IHLModelRenderer piece = (IHLModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    	}
		    }
		    else
		    {
		    	rbd.renderInventoryBlock(block, 3, 0, new RenderBlocks());
		    }
		    GL11.glPopMatrix();
			break;
		case INVENTORY:
		    GL11.glRotatef(15F, 0F, 1F, 0F);
		    GL11.glTranslatef(0.0F,-1.0F+inventoryOffset,0F);
		    if(model!=null)
		    {
		    	for(int i = this.renderFrom;i<model.boxList.size()-this.renderTo;i++)
		    	{
		    		if(model.boxList.get(i) instanceof ModelRenderer)
		    		{
		    			ModelRenderer piece = (ModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    		else if(model.boxList.get(i) instanceof IHLModelRenderer)
		    		{
		    			IHLModelRenderer piece = (IHLModelRenderer) model.boxList.get(i);
		    			piece.render(1.0F/16.0F);
		    		}
		    	}
		    }
		    else
		    {
		    	
		    	rbd.renderInventoryBlock(block, 3, 0, new RenderBlocks());
		    }
		    GL11.glDisable(GL11.GL_BLEND);
		    GL11.glPopMatrix();
			break;
		default:
	}
}
}