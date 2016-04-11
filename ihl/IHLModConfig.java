package ihl;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.util.StackUtil;
import ihl.flexible_cable.IronWorkbenchTileEntity;
import ihl.recipes.IronWorkbenchRecipe;
import ihl.recipes.RecipeInputDie;
import ihl.recipes.RecipeInputWire;
import ihl.recipes.RecipeOutputItemStack;
import ihl.recipes.UniversalRecipeInput;
import ihl.recipes.UniversalRecipeManager;
import ihl.recipes.UniversalRecipeOutput;
import ihl.utils.IHLUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class IHLModConfig 
{
	public int handpumpTier=1;
	public int handpumpMaxCharge=30000;
	public int handpumpOperationEUCost=180;
	
	public int advancedHandpumpTier=3;
	public int advancedHandpumpMaxCharge=1000000;
	public int advancedHandpumpOperationEUCost=10000;

	public int harvesterTier=1;
	public int harvesterMaxEnergyStorage=110;
	public int harvesterIdleEUCost=1;
	public int harvesterOperationEUCost=100;

	public int blowerTier=1;
	public int blowerMaxEnergyStorage=100;
	public int blowerEnergyConsumePerTick=5;
	
	public int tditTier=4;
	public int tditMaxEnergyStorage=12000;
	public int tditEnergyConsumePerStack=12000;
	
	public int ts02DefaultTier=1;
	public int ts02DefaultMaxEnergyStorage=1000;
	public int ts02DefaultSpeed=400;
	public int ts02DefaultOperationEUCost=5;
	public String[] ts02BlockBlackListString;
	public String[] ts02BlockWhiteListString;
	
	public boolean enableExtendedLiquidPhysics=true;
	public boolean enableHandpump=true;
	public boolean enableFan=true;
	public boolean enableTunnelingShield=true;
	public boolean enableHarvester=true;
	public boolean enableRubberTreeSack=true;
	public boolean enableCollectors=true;
	public boolean enableWailers=true;
	public boolean enableTDIT=true;
	
	public boolean enableFlexibleCablesCrafting=true;
	public boolean enableFlexibleCablesGridPowerLossCalculations=true;
	public double additionalPowerLossesAtFrequencyGenerator = 0.01D;
	public boolean generateApatiteOre=true;
	public boolean generateSaltpeterOre=true;
	public boolean generateLimestone=true;
	public boolean generateGypsum=true;
	public boolean generatePotassiumFeldspar=true;
	public boolean generateTrona=true;
	public boolean generateRocksalt=true;
	public boolean generateCinnabar=true;
	public boolean generateGyubnera=true;
	public boolean generateBauxite=true;
	public boolean generateChromite=true;
	public boolean generateMuscovite=true;
	public boolean generateOil=true;
	public boolean generateDatolite=true;
	public boolean generateSaltwater=true;
	public boolean generateBischofite=true;
	public int mirrorReflectionRange=14;
	public int mirrorReflectionUpdateSpeed=128;
	public String preventMachineBlockRegistrationName="null";
	public boolean skipRecipeLoad=false;
	public boolean giveIHLManualOnPlayerWakeUpEvent=true;
	
	public IHLModConfig(FMLPreInitializationEvent evt) throws IOException
	{
		Configuration config = new Configuration(evt.getSuggestedConfigurationFile());
		config.load();
		skipRecipeLoad = config.get(Configuration.CATEGORY_GENERAL, "skipRecipeLoad", skipRecipeLoad).getBoolean(skipRecipeLoad);
		preventMachineBlockRegistrationName = config.get(Configuration.CATEGORY_GENERAL, "preventMachineBlockRegistrationName", preventMachineBlockRegistrationName).getString();
		String[] bl = {"bedrock", "reinforcedStone", "reinforcedGlass", "reinforcedDoorBlock" , "personalSafe", "end_portal_frame"};
		String[] wl = {"brown_mushroom_block", "cake", "fire", "lava", "water", "flowing_lava", "flowing_water", "redstone_torch", "redstone_wire", "web", "torch"};
		enableExtendedLiquidPhysics = config.get(Configuration.CATEGORY_GENERAL, "enableExtendedLiquidPhysics", enableExtendedLiquidPhysics).getBoolean(enableExtendedLiquidPhysics);
		enableHandpump = config.get(Configuration.CATEGORY_GENERAL, "enableHandpump", enableHandpump).getBoolean(enableHandpump);
		enableFan = config.get(Configuration.CATEGORY_GENERAL, "enableFan", enableFan).getBoolean(enableFan);
		enableTunnelingShield = config.get(Configuration.CATEGORY_GENERAL, "enableTunnelingShield", enableTunnelingShield).getBoolean(enableTunnelingShield);
		enableRubberTreeSack = config.get(Configuration.CATEGORY_GENERAL, "enableRubberTreeSack", enableRubberTreeSack).getBoolean(enableRubberTreeSack);
		enableCollectors = config.get(Configuration.CATEGORY_GENERAL, "enableCollectors", enableCollectors).getBoolean(enableCollectors);
		enableWailers = config.get(Configuration.CATEGORY_GENERAL, "enableWailers", enableWailers).getBoolean(enableWailers);
		enableTDIT = config.get(Configuration.CATEGORY_GENERAL, "enableTDIT", enableTDIT).getBoolean(enableTDIT);
		giveIHLManualOnPlayerWakeUpEvent = config.get(Configuration.CATEGORY_GENERAL, "giveIHLManualOnPlayerWakeUpEvent", giveIHLManualOnPlayerWakeUpEvent).getBoolean(giveIHLManualOnPlayerWakeUpEvent);
		
		generateApatiteOre = config.get(Configuration.CATEGORY_GENERAL, "generateApatiteOre", generateApatiteOre).getBoolean(generateApatiteOre);
		generateSaltpeterOre = config.get(Configuration.CATEGORY_GENERAL, "generateSaltpeterOre", generateSaltpeterOre).getBoolean(generateSaltpeterOre);
		generateLimestone = config.get(Configuration.CATEGORY_GENERAL, "generateLimestone", generateLimestone).getBoolean(generateLimestone);
		generateGypsum = config.get(Configuration.CATEGORY_GENERAL, "generateGypsum", generateGypsum).getBoolean(generateGypsum);
		generatePotassiumFeldspar = config.get(Configuration.CATEGORY_GENERAL, "generatePotassiumFeldspar", generatePotassiumFeldspar).getBoolean(generatePotassiumFeldspar);
		generateTrona = config.get(Configuration.CATEGORY_GENERAL, "generateTrona", generateTrona).getBoolean(generateTrona);
		generateRocksalt = config.get(Configuration.CATEGORY_GENERAL, "generateRocksalt", generateRocksalt).getBoolean(generateRocksalt);
		generateCinnabar = config.get(Configuration.CATEGORY_GENERAL, "generateCinnabar", generateCinnabar).getBoolean(generateCinnabar);
		generateGyubnera = config.get(Configuration.CATEGORY_GENERAL, "generateGyubnera", generateGyubnera).getBoolean(generateGyubnera);
		generateBauxite = config.get(Configuration.CATEGORY_GENERAL, "generateBauxite", generateBauxite).getBoolean(generateBauxite);
		generateChromite = config.get(Configuration.CATEGORY_GENERAL, "generateChromite", generateChromite).getBoolean(generateChromite);
		generateMuscovite = config.get(Configuration.CATEGORY_GENERAL, "generateMuscovite", generateMuscovite).getBoolean(generateMuscovite);
		generateOil = config.get(Configuration.CATEGORY_GENERAL, "generateOil", generateOil).getBoolean(generateOil);
		generateDatolite = config.get(Configuration.CATEGORY_GENERAL, "generateDatolite", generateDatolite).getBoolean(generateDatolite);
		generateSaltwater = config.get(Configuration.CATEGORY_GENERAL, "generateSaltwater", generateSaltwater).getBoolean(generateSaltwater);
		generateBischofite = config.get(Configuration.CATEGORY_GENERAL, "generateBischofite", generateBischofite).getBoolean(generateBischofite);
		
		handpumpTier = config.get(Configuration.CATEGORY_GENERAL, "handpumpTier", handpumpTier).getInt();
		handpumpMaxCharge = config.get(Configuration.CATEGORY_GENERAL, "handpumpMaxCharge", handpumpMaxCharge).getInt();
		handpumpOperationEUCost = config.get(Configuration.CATEGORY_GENERAL, "handpumpOperationEUCost", handpumpOperationEUCost).getInt();
		advancedHandpumpTier = config.get(Configuration.CATEGORY_GENERAL, "advancedHandpumpTier", advancedHandpumpTier).getInt();
		advancedHandpumpMaxCharge = config.get(Configuration.CATEGORY_GENERAL, "advancedHandpumpMaxCharge", advancedHandpumpMaxCharge).getInt();
		advancedHandpumpOperationEUCost = config.get(Configuration.CATEGORY_GENERAL, "advancedHandpumpOperationEUCost", advancedHandpumpOperationEUCost).getInt();
		blowerTier = config.get(Configuration.CATEGORY_GENERAL, "blowerTier", blowerTier).getInt();
		blowerMaxEnergyStorage = config.get(Configuration.CATEGORY_GENERAL, "blowerMaxEnergyStorage", blowerMaxEnergyStorage).getInt();
		blowerEnergyConsumePerTick = config.get(Configuration.CATEGORY_GENERAL, "blowerEnergyConsumePerTick", blowerEnergyConsumePerTick).getInt();
		
		tditTier = config.get(Configuration.CATEGORY_GENERAL, "tditTier", tditTier).getInt();
		tditMaxEnergyStorage = config.get(Configuration.CATEGORY_GENERAL, "tditMaxEnergyStorage", tditMaxEnergyStorage).getInt();
		tditEnergyConsumePerStack = config.get(Configuration.CATEGORY_GENERAL, "tditEnergyConsumePerStack", tditEnergyConsumePerStack).getInt();
		
		ts02DefaultTier = config.get(Configuration.CATEGORY_GENERAL, "ts02DefaultTier", ts02DefaultTier).getInt();
		ts02DefaultMaxEnergyStorage = config.get(Configuration.CATEGORY_GENERAL, "ts02DefaultMaxEnergyStorage", ts02DefaultMaxEnergyStorage).getInt();
		ts02DefaultSpeed = config.get(Configuration.CATEGORY_GENERAL, "ts02DefaultSpeed", ts02DefaultSpeed).getInt();
		ts02DefaultOperationEUCost = config.get(Configuration.CATEGORY_GENERAL, "ts02DefaultOperationEUCost", ts02DefaultOperationEUCost).getInt();

		harvesterTier = config.get(Configuration.CATEGORY_GENERAL, "harvesterTier", harvesterTier).getInt();
		harvesterMaxEnergyStorage = config.get(Configuration.CATEGORY_GENERAL, "harvesterMaxEnergyStorage", harvesterMaxEnergyStorage).getInt();
		harvesterIdleEUCost = config.get(Configuration.CATEGORY_GENERAL, "harvesterIdleEUCost", harvesterIdleEUCost).getInt();
		harvesterOperationEUCost = config.get(Configuration.CATEGORY_GENERAL, "harvesterOperationEUCost", harvesterOperationEUCost).getInt();
			
		enableFlexibleCablesGridPowerLossCalculations = config.get(Configuration.CATEGORY_GENERAL, "enableFlexibleCablesGridPowerLossCalculations", enableFlexibleCablesGridPowerLossCalculations).getBoolean(enableFlexibleCablesGridPowerLossCalculations);
		additionalPowerLossesAtFrequencyGenerator = config.get(Configuration.CATEGORY_GENERAL, "additionalPowerLossesAtFrequencyGenerator", additionalPowerLossesAtFrequencyGenerator).getDouble(additionalPowerLossesAtFrequencyGenerator);
		enableFlexibleCablesCrafting = config.get(Configuration.CATEGORY_GENERAL, "enableFlexibleCablesCrafting", enableFlexibleCablesCrafting).getBoolean(enableFlexibleCablesCrafting);
		mirrorReflectionRange = config.get(Configuration.CATEGORY_GENERAL, "mirrorReflectionRange", mirrorReflectionRange).getInt();
		mirrorReflectionUpdateSpeed = config.get(Configuration.CATEGORY_GENERAL, "mirrorReflectionUpdateSpeed", mirrorReflectionUpdateSpeed).getInt();
		
		Property blp = config.get(Configuration.CATEGORY_GENERAL, "ts02BlockBlackList", bl);
		Property wlp = config.get(Configuration.CATEGORY_GENERAL, "ts02BlockWhiteList", wl);
		if(blp.isList())
		{
				ts02BlockBlackListString = config.get(Configuration.CATEGORY_GENERAL, "ts02BlockBlackList", bl).getStringList();
		}
		else
		{
				blp.set(bl);
				ts02BlockBlackListString=bl;
		}
		if(wlp.isList())
		{
				ts02BlockWhiteListString = config.get(Configuration.CATEGORY_GENERAL, "ts02BlockWhiteList", wl).getStringList();
		}
		else
		{
				wlp.set(wl);
				ts02BlockWhiteListString=wl;
		}
		config.save();
	}
	
	private void loadRecipeConfig(InputStream resourceAsStream, boolean rewriteConfig) throws IOException 
	{
		OutputStreamWriter osWriter = null;
		BufferedWriter writer = null;
		if(rewriteConfig)
		{
        	osWriter = new OutputStreamWriter(new FileOutputStream(getFile()), "UTF-8");
			writer = new BufferedWriter(osWriter);
		}
        InputStreamReader isReader = new InputStreamReader(resourceAsStream, "UTF-8");
        LineNumberReader reader = new LineNumberReader(isReader);
        String line;
        StringBuffer currentModificator = new StringBuffer("");
        int bracketCounter=0;
        boolean modificatorStart=false;
		while ((line = reader.readLine()) != null)
        {
			if(osWriter!=null)
			{
				writer.newLine();
				writer.append(line);
			}
            line = IHLUtils.trim(line);
            if (!line.isEmpty() && !line.startsWith("//") && !line.startsWith(";"))
            {
            	currentModificator.append(line);
            	for(int i=0;i<line.length();i++)
            	{
            		char c = line.charAt(i);
            		if(c=='(')
            		{
            	        bracketCounter++;
            	        modificatorStart=true;
            		}
            		else if(c==')')
            		{
            	        bracketCounter--;
            		}
            		if(bracketCounter==0 && modificatorStart)
            		{
            			processModificator(currentModificator.toString());
            			modificatorStart=false;
            			currentModificator.delete(0, currentModificator.length());
            		}
            	}
            }
        }
		isReader.close();
		if(osWriter!=null)
		{
			writer.newLine();
			writer.newLine();
			writer.append("//Available machines:");
			writer.newLine();
			writer.append("//ironworkbench");
			Iterator<Entry<String, UniversalRecipeManager>> urmi = UniversalRecipeManager.machineRecipeManagers.entrySet().iterator();
			while(urmi.hasNext())
			{
				writer.newLine();
				writer.append("//"+urmi.next().getKey());
			}
			writer.close();
	        osWriter.close();
		}
	}
		
	    private void processModificator(String string) 
	    {
	    	String action = null;
	    	String machineName = null;
	    	String recipeInput = null;
	    	String recipeOutput = null;
	    	UniversalRecipeInput uRecipeInput = null;
	    	UniversalRecipeOutput uRecipeOutput = null;
	    	String[] functionAndParameters = extractFunctionAndParameters(string);
	    	action=functionAndParameters[0].toLowerCase();
	    	List<String> parameters = splitParameters(functionAndParameters[1]);
	    	Iterator<String> parametersi=parameters.iterator();
	    	while(parametersi.hasNext())
	    	{
	    		String parameter = parametersi.next();
	    		if(parameter.equalsIgnoreCase("ironworkbench"))
	    		{
	    			machineName=parameter;
	    		}
	    		else if(UniversalRecipeManager.machineRecipeManagers.containsKey(parameter))
	    		{
	    			machineName=parameter;
	    		}
	    		else if(parameter.startsWith("recipeinput"))
	    		{
	    			recipeInput=parameter;
	    		}
	    		else if(parameter.startsWith("recipeoutput"))
	    		{
	    			recipeOutput=parameter;
	    		}
	    	}
    		List<IRecipeInput> recipeInputsItems = new ArrayList();
    		List<IRecipeInput> recipeInputsTools = new ArrayList();
    		List<ItemStack> recipeInputsMachines = new ArrayList();
	    	if(recipeInput!=null)
	    	{
	    		List<FluidStack> recipeInputsFluids = new ArrayList();
		    	String[] rifunctionAndParameters = extractFunctionAndParameters(recipeInput);
		    	List<String> riparameters = splitParameters(rifunctionAndParameters[1]);
		    	Iterator<String> riparametersi=riparameters.iterator();
		    	while(riparametersi.hasNext())
		    	{
		    		String parameter = riparametersi.next();
			    	String[] riItemFunctionAndParameters = extractFunctionAndParameters(parameter);
			    	if(riItemFunctionAndParameters[0].startsWith("itemstack"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	String[] modAndItemName = riItemStackparameters.get(0).split(":");
				    	ItemStack stack = IHLUtils.getOtherModItemStackWithDamage(modAndItemName[0], modAndItemName[1], Integer.parseInt(riItemStackparameters.get(2)));
				    	stack.stackSize = Integer.parseInt(riItemStackparameters.get(1));
				    	recipeInputsItems.add(new RecipeInputItemStack(stack,stack.stackSize));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("die"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeInputsItems.add(new RecipeInputDie("setOfDies1_5sqmm", Integer.parseInt(riItemStackparameters.get(0))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("fiber"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	ItemStack stack = IHLUtils.getThisModWireItemStackWithLength(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)));
				    	recipeInputsItems.add(new RecipeInputWire(stack, Integer.parseInt(riItemStackparameters.get(1))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("wire"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeInputsItems.add(new RecipeInputWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("cable"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeInputsItems.add(new RecipeInputWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2)),riItemStackparameters.get(3),Integer.parseInt(riItemStackparameters.get(4)),Integer.parseInt(riItemStackparameters.get(5))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("oredict"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeInputsItems.add(new RecipeInputOreDict(riItemStackparameters.get(0),Integer.parseInt(riItemStackparameters.get(1))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("toolitemstack"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	String[] modAndItemName = riItemStackparameters.get(0).split(":");
				    	ItemStack stack = IHLUtils.getOtherModItemStackWithDamage(modAndItemName[0], modAndItemName[1], Integer.parseInt(riItemStackparameters.get(2)));
				    	stack.stackSize = Integer.parseInt(riItemStackparameters.get(1));
				    	recipeInputsTools.add(new RecipeInputItemStack(stack,stack.stackSize));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("tooloredict"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeInputsTools.add(new RecipeInputOreDict(riItemStackparameters.get(0),Integer.parseInt(riItemStackparameters.get(1))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("machine"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	String[] modAndItemName = riItemStackparameters.get(0).split(":");
				    	recipeInputsMachines.add(IHLUtils.getThisModItemStack(modAndItemName[1]));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("fluidstack"))
			    	{
				    	List<String> riFluidStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeInputsFluids.add(IHLUtils.getFluidStackWithSize(riFluidStackparameters.get(0),Integer.parseInt(riFluidStackparameters.get(1))));
			    	}
		    	}
		    	uRecipeInput = new UniversalRecipeInput(recipeInputsFluids,recipeInputsItems);
	    	}
    		List<ItemStack> recipeOutputsItems = new ArrayList();
    		List<RecipeOutputItemStack> recipeOutputsRecipeOut = new ArrayList();
	    	if(recipeOutput!=null)
	    	{
	    		List<FluidStack> recipeOutputsFluids = new ArrayList();
		    	String[] rifunctionAndParameters = extractFunctionAndParameters(recipeOutput);
		    	List<String> riparameters = splitParameters(rifunctionAndParameters[1]);
		    	Iterator<String> riparametersi=riparameters.iterator();
		    	while(riparametersi.hasNext())
		    	{
		    		String parameter = riparametersi.next();
			    	String[] riItemFunctionAndParameters = extractFunctionAndParameters(parameter);
			    	if(riItemFunctionAndParameters[0].startsWith("itemstack"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	String[] modAndItemName = riItemStackparameters.get(0).split(":");
				    	ItemStack stack = IHLUtils.getOtherModItemStackWithDamage(modAndItemName[0], modAndItemName[1], Integer.parseInt(riItemStackparameters.get(2)));
				    	stack.stackSize = Integer.parseInt(riItemStackparameters.get(1));
				    	recipeOutputsItems.add(stack);
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(stack,Float.parseFloat(riItemStackparameters.get(1))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("fiber"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	ItemStack stack = IHLUtils.getThisModWireItemStackWithLength(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)));
				    	recipeOutputsItems.add(stack);
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(stack,1));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("wire"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	ItemStack stack = IHLUtils.getUninsulatedWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2)));
				    	recipeOutputsItems.add(stack);
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(stack,1));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("cable"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	ItemStack stack = IHLUtils.getInsulatedWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2)),riItemStackparameters.get(3),Integer.parseInt(riItemStackparameters.get(4)));
				    	recipeOutputsItems.add(stack);
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(stack,1));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("fluidstack"))
			    	{
				    	List<String> riFluidStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeOutputsFluids.add(IHLUtils.getFluidStackWithSize(riFluidStackparameters.get(0),Integer.parseInt(riFluidStackparameters.get(1))));
			    	}
		    	}
		    	uRecipeOutput = new UniversalRecipeOutput(recipeOutputsFluids,recipeOutputsRecipeOut, 200);
	    	}
    		if(action.equalsIgnoreCase("addrecipe"))
    		{
    			if(machineName.equalsIgnoreCase("ironworkbench"))
    			{
    				IronWorkbenchTileEntity.addRecipe(new IronWorkbenchRecipe(recipeInputsTools,recipeInputsItems, recipeOutputsItems, recipeInputsMachines));
    			}
    			else
    			{
    				UniversalRecipeManager.machineRecipeManagers.get(machineName).addRecipe(uRecipeInput, uRecipeOutput);
    			}
    		}
    		else if(action.equalsIgnoreCase("removerecipe"))
    		{
    			if(machineName.equalsIgnoreCase("ironworkbench"))
    			{
    				if(recipeInputsTools.isEmpty() && recipeInputsItems.isEmpty() && recipeInputsMachines.isEmpty())
    				{
        				IronWorkbenchTileEntity.removeRecipeByOutput(recipeOutputsItems);
    				}
    				else
    				{
        				IronWorkbenchTileEntity.removeRecipeByInput(recipeInputsTools,recipeInputsItems, recipeInputsMachines);
    				}
    			}
    			else
    			{
    				if(uRecipeInput==null)
    				{
    					UniversalRecipeManager.machineRecipeManagers.get(machineName).removeRecipeByOutput(uRecipeOutput);
    				}
    				else
    				{
    					UniversalRecipeManager.machineRecipeManagers.get(machineName).removeRecipeByInput(uRecipeInput);
    				}
    			}
    		}
	    }

		private List<String> splitParameters(String string) {
			ArrayList result = new ArrayList();
	        int bracketCounter=0;
	        boolean modificatorStart=false;
	        String function = null;
	        String parameters = null;
	        StringBuffer currentModificator = new StringBuffer("");
           	for(int i=0;i<string.length();i++)
           	{
           		char c = string.charAt(i);
           		if(c==',')
           		{
           	        if(!modificatorStart)
           	        {
           	        	result.add(currentModificator.toString());
               			currentModificator.delete(0, currentModificator.length());
           	        }
           		}
           		else if(c=='(')
           		{
           	        bracketCounter++;
          			modificatorStart=true;
           		}
           		else if(c==')')
           		{
           	        bracketCounter--;
           		}
           		if(bracketCounter==0 && modificatorStart)
           		{
          			modificatorStart=false;
           		}
       	        if(c!=',' || modificatorStart)
       	        {
               		currentModificator.append(c);
       	        }
           	}
        	result.add(currentModificator.toString());
			return result;
		}

		private String[] extractFunctionAndParameters(String string) 
		{
	        int bracketCounter=0;
	        boolean modificatorStart=false;
	        String function = null;
	        String parameters = null;
	        StringBuffer currentModificator = new StringBuffer("");
           	for(int i=0;i<string.length();i++)
           	{
           		char c = string.charAt(i);
           		if(c=='(')
           		{
           	        bracketCounter++;
           	        if(!modificatorStart)
           	        {
           	        	function=currentModificator.toString();
               			currentModificator.delete(0, currentModificator.length());
               			modificatorStart=true;
           	        }
           		}
           		else if(c==')')
           		{
           	        bracketCounter--;
           		}
           		if(bracketCounter==0 && modificatorStart)
           		{
       	        	parameters=currentModificator.substring(1, currentModificator.length());
       	        	break;
           		}
           		currentModificator.append(c);
           	}
			return new String[] {function,parameters};
		}


	private void loadRecipeConfig(File configFile) throws IOException 
	{
        FileInputStream is = new FileInputStream(configFile);
        loadRecipeConfig(is, false);
	}

	public void CheckLists()
	{
	       for(int i6=0;i6<this.ts02BlockBlackListString.length;i6++)
	        {
	        	String blockName = this.ts02BlockBlackListString[i6];
	        	Block block = Block.getBlockFromName(blockName);
	        	if(block==null)
	        	{
	        		ItemStack stack = IC2Items.getItem(blockName);
	        		if(stack!=null)
	        		{
	        			block = StackUtil.getBlock(stack);
	        		}
	        	}
	        }
	       
	       for(int i7=0;i7<this.ts02BlockWhiteListString.length;i7++)
	        {
	        	String blockName = this.ts02BlockWhiteListString[i7];
	        	Block block = Block.getBlockFromName(blockName);
	        	if(block==null)
	        	{
	        		ItemStack stack = IC2Items.getItem(blockName);
	        		if(stack!=null)
	        		{
	        			block = StackUtil.getBlock(stack);
	        		}
	        	}
	        }
	}
	
    private static File getFile()
    {
        File folder = new File(IHLMod.proxy.getMinecraftDir(), "config");
        folder.mkdirs();
        return new File(folder, "ihl-recipe.cfg");
    }

	public void loadRecipeModificators() throws IOException 
	{
        File configFile = getFile();
        if (configFile.exists())
        {
            loadRecipeConfig(configFile);
        }
        else
        {
        	InputStream inputStream = IHLMod.class.getResourceAsStream("/assets/ihl/config/ihl-recipe.cfg");
        	loadRecipeConfig(inputStream, true);
        }

	}
}
