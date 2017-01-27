package ihl.flexible_cable;

import ihl.IHLMod;
import ihl.interfaces.IEnergyNetNode;
import ihl.utils.IHLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class IHLGrid 
{
	private static final double powerLossLimitPerMeter=1.6D;
	public final Set<IEnergyNetNode> telist = new HashSet<IEnergyNetNode>();
	public double energy=0D;
	private IEnergyNetNode sink;
	private IEnergyNetNode source;
	private double voltage;
	private double lastVoltage;
	public boolean isGridValid=true;
	private double total20TicksEU;
	private int lastTickCounter=0;
	private int tickCounterFireStart=0;
	public final List<IEnergyNetNode> calculatedSinks = new ArrayList<IEnergyNetNode>();
	public final List<IEnergyNetNode> calculatedSources = new ArrayList<IEnergyNetNode>();
	public final Set<NBTTagCompound> cablesOnFire = new HashSet<NBTTagCompound> ();
	private final Map<IEnergyNetNode, Double> energyLossSinkMap = new HashMap<IEnergyNetNode, Double>();
	private final Map<IEnergyNetNode, Double> voltageSinkMap = new HashMap<IEnergyNetNode, Double>();
	private double averageEUTransfered;
	private double lastAverageEUTransfered=0D;
	
	public void drawEnergy(double amount, IEnergyNetNode sink1)
	{
		this.energy-=amount;
		if(energyLossSinkMap.get(sink1)!=null)
		{
			this.energy-=energyLossSinkMap.get(sink1)*amount*amount;
		}
		else
		{
			this.telist.add(sink1);
		}
		this.sink=sink1;
	}
	
	public void injectEnergy(double amount,	double voltage1, IEnergyNetNode source1) 
	{
    	this.energy+=amount;
    	this.voltage=voltage1;
   		this.source=source1;
		this.total20TicksEU+=amount;
		int tickCounter = MinecraftServer.getServer().getTickCounter();
		if(tickCounter-lastTickCounter<0)
		{
			lastTickCounter=tickCounter;
			this.total20TicksEU=0D;
		}
		Iterator<IEnergyNetNode> i = telist.iterator();
		while(this.energy>1d && i.hasNext())
		{
			IEnergyNetNode eNode = i.next();
			if(eNode.getEnergyAmountThisNodeWant()>0d)
			{
				double powerToInject = Math.min(energy,eNode.getEnergyAmountThisNodeWant());
				eNode.injectEnergyInThisNode(powerToInject, this.getSinkVoltage(eNode));
				this.energy-=powerToInject;
				if(energyLossSinkMap.get(eNode)!=null)
				{
					this.energy-=energyLossSinkMap.get(eNode)*powerToInject*powerToInject;
				}
				else
				{
					this.sink=eNode;
				}
			}
		}
		int d=tickCounter-lastTickCounter;
		if(d>=200)
		{
			this.averageEUTransfered=this.total20TicksEU/d;
			lastTickCounter=tickCounter;
			this.total20TicksEU=0D;
			if(IHLMod.config.enableFlexibleCablesGridPowerLossCalculations && isGridValid && this.averageEUTransfered>1D)
			{
				this.telist.add(source1);
				this.updateGrid();
			}
		}
		d=tickCounter-tickCounterFireStart;
		if(d>=200 && !this.cablesOnFire.isEmpty())
		{
			for(NBTTagCompound cable:this.cablesOnFire)
			{
    			this.removeCableAndSplitGrids(cable);
			}
			this.cablesOnFire.clear();
		}

	}
	
	public void removeCableAndSplitGrids(NBTTagCompound cable)
	{
		IHLUtils.removeChain(cable,null);
		Iterator<IEnergyNetNode> atei = this.telist.iterator();
		while(atei.hasNext())
		{
			IEnergyNetNode cte = atei.next();
			cte.setGrid(-1);
		}
		IHLMod.enet.removeCableEntities(cable);
		Iterator<IEnergyNetNode> atei2 = telist.iterator();
		while(atei2.hasNext())
		{
			IEnergyNetNode cte = atei2.next();
			if(cte.getGridID()==-1)//Warning! Potential future bugs are hidden here!
			{
				Iterator<IEnergyNetNode> atei3 = telist.iterator();
				while(atei3.hasNext())
				{
					IEnergyNetNode cte2 = atei3.next();
					if(cte2!=cte && IHLMod.enet.hasSame(cte.getCableList(),cte2.getCableList()))
					{
						int result=IHLMod.enet.mergeGrids(cte.getGridID(), cte2.getGridID());
						cte.setGrid(result);
						cte2.setGrid(result);
						break;
					}
				}
				
			}
		}
	}
	
	private void updateGrid()
	{
		if(this.source!=null && 
		this.sink!=null && 
		this.source!=this.sink &&
		!this.sink.getCableList().isEmpty() &&
		!this.source.getCableList().isEmpty() &&
		(!this.calculatedSources.contains(this.source) || 
		!this.calculatedSinks.contains(this.sink) || 
		this.averageEUTransfered>this.lastAverageEUTransfered ||
		this.voltage!=this.lastVoltage))
		{
			Map<IEnergyNetNode, NBTTagCompound> map = new HashMap<IEnergyNetNode, NBTTagCompound>();
			Set<IEnergyNetNode> templist = new HashSet<IEnergyNetNode>();
			Set<IEnergyNetNode> processlist = new HashSet<IEnergyNetNode>();
			Set<IEnergyNetNode> templist2 = new HashSet<IEnergyNetNode>();
			templist.addAll(telist);
			processlist.add(sink);
			int threads=0;
			a: while(!templist.isEmpty())
			{
				if(threads++>1000)
				{
					this.isGridValid=false;
					return;
				}
				templist.removeAll(processlist);
				templist2.clear();
				Iterator<IEnergyNetNode> it1 = processlist.iterator();
				while(it1.hasNext())
				{
					if(threads++>1000)
					{
						this.isGridValid=false;
						return;
					}
					IEnergyNetNode ate1 = it1.next();
					Iterator<IEnergyNetNode> it2 = templist.iterator();
					while(it2.hasNext())
					{
						if(threads++>1000)
						{
							this.isGridValid=false;
							return;
						}
						IEnergyNetNode ate2 = it2.next();
						if(ate1!=ate2)
						{
							NBTTagCompound cable = this.getSame(ate1.getCableList(), ate2.getCableList());
							if(cable!=null)
							{
								map.put(ate2, cable);
								templist2.add(ate2);
								if(ate2==source)
								{
									break a;
								}
							}
						}
					}
					templist.removeAll(templist2);
				}
				processlist.clear();
				processlist.addAll(templist2);
			}
			IEnergyNetNode cursor=source;
			{
				double voltage1=this.voltage;
				double euTransfered=this.averageEUTransfered;
				double voltageLossPerMeter=0D;
				double powerLossPerMeter=0D;
				double powerLossPerSquaredEU=0D;
				this.energyLossSinkMap.remove(sink);
				this.voltageSinkMap.remove(sink);
				while(cursor!=sink)
				{
					//System.out.println("cycle 4");
					NBTTagCompound cable = map.get(cursor);
					voltageLossPerMeter=IHLUtils.getResistance(cable)/1000D*euTransfered/voltage1;
					powerLossPerMeter=voltageLossPerMeter*euTransfered/voltage1;
					euTransfered-=powerLossPerMeter*cable.getInteger("length");
					voltage1-=voltageLossPerMeter*cable.getInteger("length");
					powerLossPerSquaredEU+=IHLUtils.getResistance(cable)/1000d*cable.getInteger("length")/voltage1/voltage1;
					//System.out.println("voltageLossPerMeter=" + voltageLossPerMeter);
					//System.out.println("powerLossPerMeter=" + powerLossPerMeter);
					//System.out.println("euTransfered=" + euTransfered);
					//System.out.println("voltage1=" + voltage1);
					//System.out.println("powerLossPerSquaredEU=" + powerLossPerSquaredEU);
					if(!this.cablesOnFire.contains(cable) && (powerLossPerMeter>IHLGrid.powerLossLimitPerMeter))
					{
						IHLMod.enet.setOnFire(cable);
						tickCounterFireStart=lastTickCounter;
						this.cablesOnFire.add(cable);
					}
					cursor=this.getHasCable(cable, cursor);
				}
				this.energyLossSinkMap.put(sink, powerLossPerSquaredEU);
				this.voltageSinkMap.put(sink, voltage1);
			}
			this.calculatedSources.add(this.source);
			this.calculatedSinks.add(this.sink); 
			this.lastAverageEUTransfered=this.averageEUTransfered;
			this.lastVoltage=this.voltage;
		}
	}
	
	private NBTTagCompound getSame(Set<NBTTagCompound> set, Set<NBTTagCompound> set2)
	{
		Iterator<NBTTagCompound> i1 = set.iterator();
		while(i1.hasNext())
		{
			NBTTagCompound cable=i1.next();
			if(set2.contains(cable))
			{
				return cable;
			}
		}
		return null;
	}
	
	public double getSinkVoltage(IEnergyNetNode node)
	{
		if(this.voltageSinkMap.containsKey(node))
		{
			return this.voltageSinkMap.get(node);
		}
		else
		{
			return this.voltage;
		}
	}
	
	private IEnergyNetNode getHasCable(NBTTagCompound cable, IEnergyNetNode exclude)
	{
		Iterator<IEnergyNetNode> it1 = this.telist.iterator();
		while(it1.hasNext())
		{
			IEnergyNetNode ate1 = it1.next();
			if(ate1!=exclude && ate1.getCableList().contains(cable))
			{
				return ate1;
			}
		}
		return null;
	}

	public void add(IEnergyNetNode e) 
	{
		this.telist.add(e);
		this.isGridValid=true;
		if(!e.getCableList().isEmpty())
		{
			for(NBTTagCompound cable:e.getCableList())
			{
				IHLMod.enet.cablesToGrids.put(cable.getInteger("chainUID"), this);
			}
		}
	}
}
