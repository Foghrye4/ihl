package ihl;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputFluidContainer;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
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

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.nbt.NBTTagCompound;

public class IHLModConfig 
{
	public int explosionVectorSizeBits=7;
	
	public int handpumpTier=1;
	public int handpumpMaxCharge=30000;
	public int handpumpOperationEUCost=180;
	
	public int advancedHandpumpTier=3;
	public int advancedHandpumpMaxCharge=1000000;
	public int advancedHandpumpOperationEUCost=10000;

	public int tditTier=4;
	public int tditMaxEnergyStorage=12000;
	public int tditEnergyConsumePerStack=12000;
	
	public boolean enableHandpump=true;
	public boolean enableRubberTreeSack=true;
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
	
	public IHLModConfig(FMLPreInitializationEvent evt) throws IOException
	{
		Configuration config = new Configuration(evt.getSuggestedConfigurationFile());
		config.load();
		skipRecipeLoad = config.get(Configuration.CATEGORY_GENERAL, "skipRecipeLoad", skipRecipeLoad).getBoolean(skipRecipeLoad);
		preventMachineBlockRegistrationName = config.get(Configuration.CATEGORY_GENERAL, "preventMachineBlockRegistrationName", preventMachineBlockRegistrationName).getString();
		enableHandpump = config.get(Configuration.CATEGORY_GENERAL, "enableHandpump", enableHandpump).getBoolean(enableHandpump);
		enableRubberTreeSack = config.get(Configuration.CATEGORY_GENERAL, "enableRubberTreeSack", enableRubberTreeSack).getBoolean(enableRubberTreeSack);
		enableWailers = config.get(Configuration.CATEGORY_GENERAL, "enableWailers", enableWailers).getBoolean(enableWailers);
		enableTDIT = config.get(Configuration.CATEGORY_GENERAL, "enableTDIT", enableTDIT).getBoolean(enableTDIT);
		
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
		
		tditTier = config.get(Configuration.CATEGORY_GENERAL, "tditTier", tditTier).getInt();
		tditMaxEnergyStorage = config.get(Configuration.CATEGORY_GENERAL, "tditMaxEnergyStorage", tditMaxEnergyStorage).getInt();
		tditEnergyConsumePerStack = config.get(Configuration.CATEGORY_GENERAL, "tditEnergyConsumePerStack", tditEnergyConsumePerStack).getInt();
			
		enableFlexibleCablesGridPowerLossCalculations = config.get(Configuration.CATEGORY_GENERAL, "enableFlexibleCablesGridPowerLossCalculations", enableFlexibleCablesGridPowerLossCalculations).getBoolean(enableFlexibleCablesGridPowerLossCalculations);
		additionalPowerLossesAtFrequencyGenerator = config.get(Configuration.CATEGORY_GENERAL, "additionalPowerLossesAtFrequencyGenerator", additionalPowerLossesAtFrequencyGenerator).getDouble(additionalPowerLossesAtFrequencyGenerator);
		enableFlexibleCablesCrafting = config.get(Configuration.CATEGORY_GENERAL, "enableFlexibleCablesCrafting", enableFlexibleCablesCrafting).getBoolean(enableFlexibleCablesCrafting);
		mirrorReflectionRange = config.get(Configuration.CATEGORY_GENERAL, "mirrorReflectionRange", mirrorReflectionRange).getInt();
		mirrorReflectionUpdateSpeed = config.get(Configuration.CATEGORY_GENERAL, "mirrorReflectionUpdateSpeed", mirrorReflectionUpdateSpeed).getInt();
		
		explosionVectorSizeBits = config.get(Configuration.CATEGORY_GENERAL, "explosionVectorSizeBits", explosionVectorSizeBits).getInt();
		
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
    		List<IRecipeInput> recipeInputsItems = new ArrayList<IRecipeInput>();
    		List<IRecipeInput> recipeInputsTools = new ArrayList<IRecipeInput>();
    		List<ItemStack> recipeInputsMachines = new ArrayList<ItemStack>();
	    	if(recipeInput!=null)
	    	{
	    		List<FluidStack> recipeInputsFluids = new ArrayList<FluidStack>();
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
				    	int iDamage = 0;
				    	int iQuantity = Integer.parseInt(riItemStackparameters.get(1));
				    	String sDamage = riItemStackparameters.get(2);
				    	if(sDamage.startsWith("hash"))
				    	{
				    		iDamage=extractFunctionAndParameters(sDamage)[1].hashCode() & Integer.MAX_VALUE;
				    	}
				    	else
				    	{
				    		iDamage=Integer.parseInt(sDamage);
				    	}
				    	recipeInputsItems.add(new RecipeInputItemStack(IHLUtils.getOtherModItemStackWithDamage(modAndItemName[0], modAndItemName[1], iDamage, iQuantity),iQuantity));
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
			    	else if(riItemFunctionAndParameters[0].startsWith("fluidcontainer"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeInputsItems.add(new RecipeInputFluidContainer(FluidRegistry.getFluid(riItemStackparameters.get(0)),Integer.parseInt(riItemStackparameters.get(1))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("toolitemstack"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	String[] modAndItemName = riItemStackparameters.get(0).split(":");
				    	ItemStack stack = IHLUtils.getOtherModItemStackWithDamage(modAndItemName[0], modAndItemName[1], Integer.parseInt(riItemStackparameters.get(2)), Integer.parseInt(riItemStackparameters.get(1)));
					    stack.stackTagCompound = new NBTTagCompound();
					    NBTTagCompound gtTagCompound = new NBTTagCompound();
					    gtTagCompound.setInteger("Damage",0);
					    gtTagCompound.setInteger("MaxDamage",2000);
					    stack.stackTagCompound.setTag("GT.ToolStats", gtTagCompound);
				    	recipeInputsTools.add(new RecipeInputItemStack(stack,stack.stackSize));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("tooloredict"))
			    	{
				    	recipeInputsTools.add(new RecipeInputOreDict(splitParameters(riItemFunctionAndParameters[1]).get(0)));
				    	
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
		    	uRecipeInput = new UniversalRecipeInput(recipeInputsFluids.toArray(),recipeInputsItems.toArray());
	    	}
    		List<ItemStack> recipeOutputsItems = new ArrayList<ItemStack>();
    		List<RecipeOutputItemStack> recipeOutputsRecipeOut = new ArrayList<RecipeOutputItemStack>();
	    	if(recipeOutput!=null)
	    	{
	    		List<FluidStack> recipeOutputsFluids = new ArrayList<FluidStack>();
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
				    	String sDamage = riItemStackparameters.get(2);
				    	int iDamage = 0;
				    	if(sDamage.startsWith("hash"))
				    	{
				    		iDamage=extractFunctionAndParameters(sDamage)[1].hashCode() & Integer.MAX_VALUE;
				    	}
				    	else
				    	{
				    		iDamage=Integer.parseInt(sDamage);
				    	}
				    	ItemStack stack = IHLUtils.getOtherModItemStackWithDamage(modAndItemName[0], modAndItemName[1], iDamage,Integer.parseInt(riItemStackparameters.get(1)));
			    		if(stack==null)
			    		{
			    			throw new java.lang.IllegalArgumentException("Item not found: "+parameter);
			    		}
				    	if(riItemStackparameters.size()>3){
				    		stack.stackTagCompound = new NBTTagCompound();
				    		String[] fp = extractFunctionAndParameters(riItemStackparameters.get(3));
				    		Iterator<String> iparams2 = splitParameters(fp[1]).iterator();
				    		while(iparams2.hasNext()){
					    		decodeNBT(iparams2.next(), stack.stackTagCompound);
				    		}
				    	}
				    	recipeOutputsItems.add(stack);
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(stack.copy(),Float.parseFloat(riItemStackparameters.get(1))));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("fiber"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeOutputsItems.add(IHLUtils.getThisModWireItemStackWithLength(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1))));
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(IHLUtils.getThisModWireItemStackWithLength(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1))),1));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("wire"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeOutputsItems.add(IHLUtils.getUninsulatedWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2))));
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(IHLUtils.getUninsulatedWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2))),1));
			    	}
			    	else if(riItemFunctionAndParameters[0].startsWith("cable"))
			    	{
				    	List<String> riItemStackparameters = splitParameters(riItemFunctionAndParameters[1]);
				    	recipeOutputsItems.add(IHLUtils.getInsulatedWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2)),riItemStackparameters.get(3),Integer.parseInt(riItemStackparameters.get(4))));
				    	recipeOutputsRecipeOut.add(new RecipeOutputItemStack(IHLUtils.getInsulatedWire(riItemStackparameters.get(0), Integer.parseInt(riItemStackparameters.get(1)), Integer.parseInt(riItemStackparameters.get(2)),riItemStackparameters.get(3),Integer.parseInt(riItemStackparameters.get(4))),1));
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
    		else if(action.equalsIgnoreCase("addcasting"))
    		{
    			IHLMod.moltenAmounts.put(parameters.get(0),Integer.parseInt(parameters.get(1)));
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

	private void decodeNBT(String string, NBTTagCompound out) {
		String[] fp = extractFunctionAndParameters(string);
		List<String> params = splitParameters(fp[1]);
		if(fp[0].equalsIgnoreCase("string")){
			out.setString(params.get(0), params.get(1));
		}
		else if(fp[0].equalsIgnoreCase("boolean")){
			out.setBoolean(params.get(0), Boolean.getBoolean(params.get(1)));
		}
		else if(fp[0].equalsIgnoreCase("float")){
			out.setFloat(params.get(0), Float.parseFloat(params.get(1)));
		}
		else if(fp[0].equalsIgnoreCase("double")){
			out.setDouble(params.get(0), Double.parseDouble(params.get(1)));
		}
		else if(fp[0].equalsIgnoreCase("integer")){
			out.setInteger(params.get(0), Integer.parseInt(params.get(1)));
		}
		else if(fp[0].equalsIgnoreCase("long")){
			out.setLong(params.get(0), Long.parseLong(params.get(1)));
		}
		else if(fp[0].equalsIgnoreCase("nbt")){
			NBTTagCompound out2 = new NBTTagCompound();
			for(int i = 1; i <  params.size(); i++){
				decodeNBT(params.get(i), out2);
			}
			out.setTag(params.get(0), out2);
		}
	}
	
	

	private List<String> splitParameters(String string) {
		ArrayList<String> result = new ArrayList<String>();
	        int bracketCounter=0;
	        boolean modificatorStart=false;
	        StringBuffer currentModificator = new StringBuffer("");
           	for(int i=0;i<string.length();i++)
           	{
           		char c = string.charAt(i);
           		if(c==',')
           		{
	           	        if(!modificatorStart) {
	           	        	result.add(currentModificator.toString());
	               			currentModificator.delete(0, currentModificator.length());
	           	        }
           		} else if(c=='(') {
	           	        bracketCounter++;
          			modificatorStart=true;
           		} else if(c==')') {
           		        bracketCounter--;
           		}
           		if(bracketCounter==0 && modificatorStart) {
          			modificatorStart=false;
           		}
       	        	if(c!=',' || modificatorStart) {
               			currentModificator.append(c);
       	        	}
           	}
        	result.add(currentModificator.toString());
			return result;
	}

	private String[] extractFunctionAndParameters(String string) {
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
