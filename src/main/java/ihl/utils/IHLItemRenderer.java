package ihl.utils;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(value = Side.CLIENT)
public class IHLItemRenderer {
	private Random random = new Random();
    private RenderBlocks renderBlocks = new RenderBlocks();
    private boolean noRotation=false;
    
	public IHLItemRenderer(){}

    public IHLItemRenderer(boolean noRotation1) 
    {
    	noRotation=noRotation1;
	}

	public void doRender(RenderManager renderManager, ItemStack stack, double x, double y, double z)
    {
		if(stack!=null)
		{
			if(stack.getItem().isFull3D())
			{
				ItemRenderType type = ItemRenderType.ENTITY;
				IItemRenderer itemRenderer = MinecraftForgeClient.getItemRenderer(stack, type);
				if(itemRenderer!=null)
				{
					GL11.glPushMatrix();
					GL11.glTranslatef((float)x, (float)y-0.5F, (float)z);
					GL11.glScalef(0.5F, 0.5F, 0.5F);
					Object[] data={null,null};
					itemRenderer.renderItem(type, stack, data);
					GL11.glPopMatrix();
				}
				else
				{
					this.doRenderNative(renderManager, stack, x, y-0.1F, z);
				}
			}
			else
			{
				this.doRenderNative(renderManager, stack, x, y-0.1F, z);
			}
		}
    }
    
    public void doRenderNative(RenderManager renderManager, ItemStack var10, double par2, double par4, double par6)
    {
    	float scale=0.8F;
        if (var10.getItem() != null)
        {
        	renderManager.renderEngine.bindTexture(renderManager.renderEngine.getResourceLocation(var10.getItemSpriteNumber()));
            this.random.setSeed(187L);
            GL11.glPushMatrix();
            byte var13 = 1;

            if (var10.stackSize > 1)
            {
                var13 = 2;
            }

            if (var10.stackSize > 5)
            {
                var13 = 3;
            }

            if (var10.stackSize > 20)
            {
                var13 = 4;
            }

            if (var10.stackSize > 40)
            {
                var13 = 5;
            }

            GL11.glTranslatef((float)par2, (float)par4, (float)par6);
            
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float var19;
            float var18;
            int var24;

            if (var10.getItemSpriteNumber() == 0 && var10.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(var10.getItem()).getRenderType()))
            {
                Block var21 = Block.getBlockFromItem(var10.getItem());

                float var25 = 0.25F*scale;
                var24 = var21.getRenderType();

                if (var24 == 1 || var24 == 19 || var24 == 12 || var24 == 2)
                {
                    var25 = 0.5F*scale;
                }

                if (var21.getRenderBlockPass() > 0)
                {
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                GL11.glScalef(var25, var25, var25);

                for (int var26 = 0; var26 < var13; ++var26)
                {
                    GL11.glPushMatrix();

                    if (var26 > 0)
                    {
                        var18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var25;
                        var19 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var25;
                        float var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var25;
                        GL11.glTranslatef(var18, var19, var20);
                    }

                    this.renderBlocks.renderBlockAsItem(var21, var10.getItemDamage(), 1.0F);
                    GL11.glPopMatrix();
                }

                if (var21.getRenderBlockPass() > 0)
                {
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
            else
            {
                float var17;

                if (var10.getItemSpriteNumber() == 1 && var10.getItem().requiresMultipleRenderPasses())
                {
                        GL11.glScalef(0.5F*scale, 0.5F*scale, 0.5F*scale);

                    for (int var23 = 0; var23 <= 1; ++var23)
                    {
                        this.random.setSeed(187L);
                        IIcon var22 = var10.getItem().getIconFromDamageForRenderPass(var10.getItemDamage(), var23);
                        var24 = var10.getItem().getColorFromItemStack(var10, var23);
                        var17 = (var24 >> 16 & 255) / 255.0F;
                        var18 = (var24 >> 8 & 255) / 255.0F;
                        var19 = (var24 & 255) / 255.0F;
                        GL11.glColor4f(var17, var18, var19, 1.0F);
                        this.renderDroppedItem(renderManager, var10, var22, var13, var17, var18, var19);

                    }
                }
                else
                {
                    if (var10 != null && var10.getItem() instanceof ItemCloth)
                    {
                        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }
                    GL11.glScalef(0.5F*scale, 0.5F*scale, 0.5F*scale);
                    IIcon var14 = var10.getIconIndex();
                    int var15 = var10.getItem().getColorFromItemStack(var10, 0);
                    float var16 = (var15 >> 16 & 255) / 255.0F;
                    var17 = (var15 >> 8 & 255) / 255.0F;
                    var18 = (var15 & 255) / 255.0F;
                    this.renderDroppedItem(renderManager, var10, var14, var13, var16, var17, var18);
                    if (var10 != null && var10.getItem() instanceof ItemCloth)
                    {
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    private void renderDroppedItem(RenderManager renderManager, ItemStack stack, IIcon par2Icon, int par3, float par5, float par6, float par7)
    {
        Tessellator var8 = Tessellator.instance;

        if (par2Icon == null)
        {
            TextureManager var9 = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation var10 = var9.getResourceLocation(stack.getItemSpriteNumber());
            par2Icon = ((TextureMap)var9.getTexture(var10)).getAtlasSprite("missingno");
        }

        float var25 = par2Icon.getMinU();
        float var26 = par2Icon.getMaxU();
        float var11 = par2Icon.getMinV();
        float var12 = par2Icon.getMaxV();
        float var13 = 1.0F;
        float var14 = 0.5F;
        float var15 = 0.25F;
        float var17;

        if (renderManager.options.fancyGraphics)
        {
            GL11.glPushMatrix();

            //GL11.glRotatef((this.random .nextFloat() * 2.0F - 1.0F) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);

            float var16 = 0.0625F;
            var17 = 0.021875F;
            ItemStack var18 = stack;
            int var19 = var18.stackSize;
            byte var24;

            if (var19 < 2)
            {
                var24 = 1;
            }
            else if (var19 < 16)
            {
                var24 = 2;
            }
            else if (var19 < 32)
            {
                var24 = 3;
            }
            else
            {
                var24 = 4;
            }

            GL11.glTranslatef(-var14, -var15, -((var16 + var17) * var24 / 2.0F));

            for (int var20 = 0; var20 < var24; ++var20)
            {
                GL11.glTranslatef(0.0F, 0.0F, var16 + var17);

                if (var18.getItemSpriteNumber() == 0)
                {
                	renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                }
                else
                {
                	renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);
                }

                GL11.glColor4f(par5, par6, par7, 1.0F);
                ItemRenderer.renderItemIn2D(var8, var26, var11, var25, var12, par2Icon.getIconWidth(), par2Icon.getIconHeight(), var16);
            }

            GL11.glPopMatrix();
        }
        else
        {
            for (int var27 = 0; var27 < par3; ++var27)
            {
                GL11.glPushMatrix();

                if (var27 > 0)
                {
                    var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float var29 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float var28 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    GL11.glTranslatef(var17, var29, var28);
                }
                if(!noRotation)
                {
                	GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                }
                GL11.glColor4f(par5, par6, par7, 1.0F);
                var8.startDrawingQuads();
                var8.setNormal(0.0F, 1.0F, 0.0F);
                var8.addVertexWithUV(0.0F - var14, 0.0F - var15, 0.0D, var25, var12);
                var8.addVertexWithUV(var13 - var14, 0.0F - var15, 0.0D, var26, var12);
                var8.addVertexWithUV(var13 - var14, 1.0F - var15, 0.0D, var26, var11);
                var8.addVertexWithUV(0.0F - var14, 1.0F - var15, 0.0D, var25, var11);
                var8.draw();
                GL11.glPopMatrix();
            }
        }
    }

}
