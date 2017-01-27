package ihl.worldgen;

import ihl.IHLMod;
import ihl.utils.IHLUtils;
import ihl.worldgen.ores.IHLFluid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class IHLWorldGenerator implements IWorldGenerator {
	
	private WorldGenMinableMeta 
	apatiteGenerator, 
	saltpeterGenerator, 
	limestoneGenerator, 
	gypsumGenerator, 
	potassiumFeldsparGenerator, 
	tronaGenerator,
	gyubneraGenerator,
	rocksaltGenerator,
	cinnabarGenerator,
	bauxiteGenerator,
	chromiteGenerator,
	muscoviteGenerator,
	oilGenerator,
	datoliteGenerator,
	saltwaterGenerator,
	bischofiteGenerator;
	private WorldGenMinableMeta currentGenerator;
	protected final Map<Long,Set<int[]>> oreCoordinatesToChunkCoordinates = new HashMap<Long,Set<int[]>>();
	protected final Map<Long,WorldGenMinableMeta> blockToOreCoordinates = new HashMap<Long,WorldGenMinableMeta>();
	public final Set<Long> precalculatedChunkChache = new HashSet<Long>();
	public static IHLWorldGenerator instance;
	
	
	public IHLWorldGenerator()
	{
		super();
		apatiteGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates, IHLUtils.getOreDictBlock("oreApatite"), 0, 360, new Block[] {Blocks.stone,Blocks.gravel});
		saltpeterGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreSaltpeter"), 0, 140, new Block[] {Blocks.stone,Blocks.gravel});
		limestoneGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreLimestone"), 0, 140, new Block[] {Blocks.stone,Blocks.gravel});
		gypsumGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreGypsum"), 0, 140, new Block[] {Blocks.stone,Blocks.gravel});
		potassiumFeldsparGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("orePotassiumFeldspar"), 0, 140, new Block[] {Blocks.stone,Blocks.gravel});
		tronaGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreTrona"), 0, 700, new Block[] {Blocks.stone,Blocks.gravel});
		gyubneraGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreGyubnera"), 0, 700, new Block[] {Blocks.stone,Blocks.gravel});
		rocksaltGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreRockSalt"), 0, 140, new Block[] {Blocks.stone,Blocks.gravel});
		cinnabarGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreCinnabar"), 0, 700, new Block[] {Blocks.stone,Blocks.gravel});
		bauxiteGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreBauxite"), 0, 700, new Block[] {Blocks.stone,Blocks.gravel});
		chromiteGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreChromite"), 0, 700, new Block[] {Blocks.stone,Blocks.gravel});
		muscoviteGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreMica"), 0, 140, new Block[] {Blocks.stone,Blocks.gravel});
		oilGenerator = new WorldGenLiquidOre(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLFluid.getBlock("oil"),Blocks.clay, 0, 32, 0, 8, new Block[] {Blocks.lava,Blocks.clay});
		datoliteGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreDatolite"), 0, 700, new Block[] {Blocks.stone,Blocks.gravel});
		saltwaterGenerator = new WorldGenLiquidOre(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLFluid.getBlock("saltwater"),Blocks.sandstone, 0, 32, 62, 62, new Block[] {Blocks.sand,Blocks.sandstone});
		bischofiteGenerator = new WorldGenMinableMeta(precalculatedChunkChache,blockToOreCoordinates, oreCoordinatesToChunkCoordinates,IHLUtils.getOreDictBlock("oreBischofite"), 0, 1400, new Block[] {Blocks.stone,Blocks.gravel});
		instance=this;
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		for(int ix=-16+chunkX;ix<=16+chunkX;ix++)
		{
			for(int iz=-16+chunkZ;iz<=16+chunkZ;iz++)
			{
				long chunkKey = (ix<<16)+iz;
				if(!this.precalculatedChunkChache.contains(chunkKey))
				{
					if(!world.getChunkProvider().chunkExists(ix, iz) || (ix==chunkX && iz==chunkZ))
					{
						currentGenerator = switchCurrentOreGenerator(world, random, ix, iz);
						if(currentGenerator!=null)
						{
							if(currentGenerator.generateNewVein(random, ix, iz))
							{
								
							}
						}
					}
					this.precalculatedChunkChache.add(chunkKey);
				}
			}
		}
		long chunkKey = (chunkX<<16)+chunkZ;
		if(this.precalculatedChunkChache.contains(chunkKey))
		{
			Set<int[]> oreCoordinatesSet = this.oreCoordinatesToChunkCoordinates.get(chunkKey);
			if(oreCoordinatesSet!=null)
			{
				Object[] oreCoordinatesSetA = oreCoordinatesSet.toArray();
				for(int i=0;i<oreCoordinatesSetA.length;i++)
				{
						int[] oreCoordinate = (int[]) oreCoordinatesSetA[i];
						int x0 = oreCoordinate[0];
						int y0 = oreCoordinate[1];
						int z0 = oreCoordinate[2];
						long oreCoordinatesKey = (x0<<30)+(y0<<15)+z0;
						this.blockToOreCoordinates.get(oreCoordinatesKey).generate(world, random, x0, y0, z0);
				}
			}
		}
	}
	
	private WorldGenMinableMeta switchCurrentOreGenerator(World world, Random random, int chunkX, int chunkZ)
	{
		switch(random.nextInt(1000))
		{
			case 0:
			if (IHLMod.config.generateApatiteOre)
			{
				if (random.nextFloat() < 0.25f) 
				{
					return apatiteGenerator;
				}
			}
			break;
			case 1:
			if (IHLMod.config.generateSaltpeterOre)
			{
				if (random.nextFloat() < 0.25f) 
				{
					return saltpeterGenerator;
				}
			}
			break;
			case 2:
			if (IHLMod.config.generateGyubnera)
			{
				if (random.nextFloat() < 0.25f) 
				{
					return gyubneraGenerator;
				}
			}
			break;
			case 3:
			if (IHLMod.config.generateCinnabar)
			{
				if (random.nextFloat() < 0.25f) 
				{
					return cinnabarGenerator;
				}
			}
			break;
			case 4:
			if (IHLMod.config.generateRocksalt)
			{
				if (random.nextFloat() < 0.2F) 
				{
					return rocksaltGenerator;
				}
			}
			break;
			case 5:
			if (IHLMod.config.generateLimestone)
			{
				return limestoneGenerator;
			}
			break;
			case 6:
			if (IHLMod.config.generateGypsum)
			{
				return gypsumGenerator;
			}
			break;
			case 7:
			if (IHLMod.config.generatePotassiumFeldspar)
			{
				return potassiumFeldsparGenerator;
			}
			break;
			case 8:
			if (IHLMod.config.generateTrona)
			{
				return tronaGenerator;
			}
			break;
			case 9:
			if (IHLMod.config.generateBauxite)
			{
				if (random.nextFloat() < 0.25F) 
				{
					return bauxiteGenerator;
				}
			}
			break;
			case 10:
			if (IHLMod.config.generateChromite)
			{
				if (random.nextFloat() < 0.25F) 
				{
					return chromiteGenerator;
				}
			}
			break;
			case 11:
			if (IHLMod.config.generateMuscovite)
			{
				return muscoviteGenerator;
			}
			break;
			case 12:
			if (IHLMod.config.generateOil)
			{	
				return oilGenerator;
			}
			break;
			case 13:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			if (IHLMod.config.generateSaltwater)
			{
				BiomeGenBase biome = world.getBiomeGenForCoords(chunkX<<4, chunkZ<<4);
				if(biome.temperature > 1.9f && biome.rainfall==0f)
				{
					return saltwaterGenerator;
				}
			}
			break;
			case 14:
			if (IHLMod.config.generateDatolite)
			{
				if (random.nextFloat() < 0.25F) 
				{
					return datoliteGenerator;
				}
			}
			break;
			case 15:
			if (IHLMod.config.generateBischofite)
			{
				if (random.nextFloat() < 0.25F) 
				{
					return bischofiteGenerator;
				}
			}
		}
	return null;
	}
}
