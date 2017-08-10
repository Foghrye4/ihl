package ihl.model;
/*  This code with minimal changes was 
 *  taken by me (Foghrye4) from 
 *  RailCraft GitHub.
 */

/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(value=Side.CLIENT)
public class FluidRenderer {

	private static RenderBlocks renderBlocks = new RenderBlocks();
    private static final ResourceLocation BLOCK_TEXTURE = TextureMap.locationBlocksTexture;
    private static final Map<Fluid, int[]> flowingRenderCache = new HashMap<Fluid, int[]>();
    private static final Map<Fluid, int[]> stillRenderCache = new HashMap<Fluid, int[]>();
    public static final int DISPLAY_STAGES = 100;
    private static final RenderInfo liquidBlock = new RenderInfo();

    static {
        liquidBlock.texture = new IIcon[1];
    }

    public static IIcon getFluidTexture(FluidStack fluidStack, boolean flowing) {
        if (fluidStack == null)
            return ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null)
            return ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
        IIcon icon = flowing ? fluid.getFlowingIcon() : fluid.getStillIcon();
        if(icon==null)
        {
        	icon = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
        }
        return icon;
    }

    public static ResourceLocation getFluidSheet(FluidStack liquid) {
        return BLOCK_TEXTURE;
    }

    public static ResourceLocation setupFlowingLiquidTexture(FluidStack liquid, IIcon[] texArray) {
        if (liquid == null || liquid.amount <= 0)
            return null;

        Fluid fluid = liquid.getFluid();
        if (fluid == null)
            return null;
        IIcon top = fluid.getStillIcon();
        IIcon side = fluid.getFlowingIcon();
        texArray[0] = top;
        texArray[1] = top;
        texArray[2] = side;
        texArray[3] = side;
        texArray[4] = side;
        texArray[5] = side;
        return getFluidSheet(liquid);
    }

    public static int[] getLiquidDisplayLists(FluidStack fluidStack) {
        return getLiquidDisplayLists(fluidStack, false);
    }

    public static int[] getLiquidDisplayLists(FluidStack fluidStack, boolean flowing) {
        if (fluidStack == null)
            return null;
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null)
            return null;
        Map<Fluid, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;
        int[] diplayLists = cache.get(fluid);
        if (diplayLists != null)
            return diplayLists;

        diplayLists = new int[DISPLAY_STAGES];

        liquidBlock.texture[0] = null;

        if (fluid.getBlock() != null) {
            liquidBlock.template = fluid.getBlock();
            liquidBlock.texture[0] = getFluidTexture(fluidStack, flowing);
        } else {
            liquidBlock.template = Blocks.water;
            liquidBlock.texture[0] = getFluidTexture(fluidStack, flowing);
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        for (int s = 0; s < DISPLAY_STAGES; ++s) {
            diplayLists[s] = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(diplayLists[s], 4864 /*GL_COMPILE*/);

            liquidBlock.minX = 0.01f;
            liquidBlock.minY = 0;
            liquidBlock.minZ = 0.01f;

            liquidBlock.maxX = 0.99f;
            liquidBlock.maxY = (float) s / (float) DISPLAY_STAGES;
            liquidBlock.maxZ = 0.99f;
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            RenderInfo info = liquidBlock;
            renderBlocks.setRenderBounds(info.minX, info.minY, info.minZ, info.maxX, info.maxY, info.maxZ);

            if (info.renderSide[0])
                renderBlocks.renderFaceYNeg(info.template, 0D, 0D, 0D, info.getBlockTextureFromSide(0));
            if (info.renderSide[1])
                renderBlocks.renderFaceYPos(info.template, 0D, 0D, 0D, info.getBlockTextureFromSide(1));
            if (info.renderSide[2])
                renderBlocks.renderFaceZNeg(info.template, 0D, 0D, 0D, info.getBlockTextureFromSide(2));
            if (info.renderSide[3])
                renderBlocks.renderFaceZPos(info.template, 0D, 0D, 0D, info.getBlockTextureFromSide(3));
            if (info.renderSide[4])
                renderBlocks.renderFaceXNeg(info.template, 0D, 0D, 0D, info.getBlockTextureFromSide(4));
            if (info.renderSide[5])
                renderBlocks.renderFaceXPos(info.template, 0D, 0D, 0D, info.getBlockTextureFromSide(5));
            tessellator.draw();
            GL11.glEndList();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);

        cache.put(fluid, diplayLists);

        return diplayLists;
    }

}
