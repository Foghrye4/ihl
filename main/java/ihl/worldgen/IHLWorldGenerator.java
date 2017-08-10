package ihl.worldgen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import cpw.mods.fml.common.IWorldGenerator;
import ihl.IHLMod;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class IHLWorldGenerator implements IWorldGenerator {

	private static final Block[] replaceableMinerals = new Block[] { Blocks.stone, Blocks.gravel , Blocks.gold_ore, Blocks.coal_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.clay, Blocks.diamond_ore, Blocks.redstone_ore, Blocks.emerald_ore};
	private static final Block[] replaceableSand = new Block[] { Blocks.sand };
	private static final Block[] replaceableForOil = new Block[] { Blocks.lava, Blocks.flowing_lava, Blocks.obsidian};
	public static IHLWorldGenerator instance;
	private final Set<WorldGeneratorBase> generators = new HashSet<WorldGeneratorBase>(8);

	public IHLWorldGenerator()
	{
		super();
		if (IHLMod.config.generateApatiteOre)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreApatite"), replaceableMinerals));
		}
		if (IHLMod.config.generateSaltpeterOre)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreSaltpeter"), replaceableMinerals));
		}
		if (IHLMod.config.generateGyubnera)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreGyubnera"), replaceableMinerals));
		}
		if (IHLMod.config.generateCinnabar)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreCinnabar"), replaceableMinerals));
		}
		if (IHLMod.config.generateRocksalt)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreRockSalt"), replaceableMinerals));
		}
		if (IHLMod.config.generateLimestone)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreLimestone"), replaceableMinerals));
		}
		if (IHLMod.config.generateGypsum)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreGypsum"), replaceableMinerals));
		}
		if (IHLMod.config.generatePotassiumFeldspar)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("orePotassiumFeldspar"), replaceableMinerals));
		}
		if (IHLMod.config.generateTrona)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreTrona"), replaceableMinerals));
		}
		if (IHLMod.config.generateBauxite)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreBauxite"), replaceableMinerals));
		}
		if (IHLMod.config.generateChromite)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreChromite"), replaceableMinerals));
		}
		if (IHLMod.config.generateMuscovite)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreMica"), replaceableMinerals));
		}
		if (IHLMod.config.generateOil)
		{	
			generators.add(new WorldGeneratorUndergroundLake(IHLFluid.getBlock("oil"), Blocks.obsidian, replaceableForOil));
		}
		if (IHLMod.config.generateSaltwater)
		{
			generators.add(new WorldGeneratorSurfaceLake(IHLFluid.getBlock("saltwater"), replaceableSand));
		}
		if (IHLMod.config.generateDatolite)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreDatolite"), replaceableMinerals));
		}
		if (IHLMod.config.generateBischofite)
		{
			generators.add(new WorldGeneratorVein(IHLUtils.getOreDictBlock("oreBischofite"), replaceableMinerals));
		}
		instance=this;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		for (WorldGeneratorBase generator : generators) {
			generator.generate(world, chunkX, chunkZ);
		}
	}
}
