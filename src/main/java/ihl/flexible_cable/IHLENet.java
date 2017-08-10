package ihl.flexible_cable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ihl.IHLMod;
import ihl.interfaces.IEnergyNetNode;

public class IHLENet {
	
	public Map<Integer, IHLGrid> grids = new HashMap<Integer, IHLGrid>();
	public Map<Integer, IHLGrid> cablesToGrids = new HashMap<Integer, IHLGrid>();
	
	private int griduid=0;
	
	public IHLENet()
	{
	}
	
	public int getNewUniqueGridID()
	{
		for(int i=0;i<Integer.MAX_VALUE;i++)
		{
			if(grids.get(++griduid)==null)
			{
				return griduid;
			}
		}
		return -1;
	}

	public int mergeGrids(int gridID, int gridID2) 
	{
		if(gridID==-1 && gridID2!=-1)
		{
			return gridID2;
		}
		else if(gridID!=-1 && gridID2==-1)
		{
			return gridID;
		}
		else if(gridID==-1 && gridID2==-1)
		{
			int newGridID=this.getNewUniqueGridID();
			IHLGrid cgrid;
			cgrid=new IHLGrid();
			grids.put(newGridID, cgrid);
			return newGridID;
		}
		else if(gridID!=gridID2)
		{
			Iterator<IEnergyNetNode> tei = grids.get(gridID2).telist.iterator();
			while(tei.hasNext())
			{
				IEnergyNetNode te = tei.next();
				te.setGrid(gridID);
			}
			grids.remove(gridID2);
			return gridID;
		}
		return gridID2;
	}

	
	public IHLGrid getGrid(int gridID)
	{
		if(gridID==-1)
			throw new IllegalArgumentException("Node should never ask grid with ID -1");
		if(this.grids.get(gridID)==null)
		{
			IHLGrid cgrid;
			cgrid=new IHLGrid();
			grids.put(gridID, cgrid);
			return cgrid;
		}
		else
		{
			return this.grids.get(gridID);
		}
	}
	
	public void splitGrids(int gridID, IEnergyNetNode exclude)
	{
		Set<IEnergyNetNode> telist = this.grids.get(gridID).telist;
		Set<IEnergyNetNode> telist2 = new HashSet<IEnergyNetNode>();
		telist.remove(exclude);
		Iterator<IEnergyNetNode> atei = telist.iterator();
		while(atei.hasNext())
		{
			IEnergyNetNode cte = atei.next();
			atei.remove();
			telist2.add(cte);
			cte.getCableList().removeAll(exclude.getCableList());
			cte.setGrid(-1);
		}
		Iterator<IEnergyNetNode> atei2 = telist2.iterator();
		while(atei2.hasNext())
		{
			IEnergyNetNode cte = atei2.next();
			if(cte.getGridID()==-1)//Warning! Potential future bugs are hidden here!
			{
				Iterator<IEnergyNetNode> atei3 = telist2.iterator();
				while(atei3.hasNext())
				{
					IEnergyNetNode cte2 = atei3.next();
					if(cte2!=cte && hasSame(cte.getCableList(),cte2.getCableList()))
					{
						int result=this.mergeGrids(cte.getGridID(), cte2.getGridID());
						cte.setGrid(result);
						cte2.setGrid(result);
						break;
					}
				}
			}
		}
	}
	
	public void removeCableAndSplitGrids(int gridID, IHLCable cable)
	{
		this.grids.get(gridID).removeCableAndSplitGrids(cable);
	}
	
	public boolean hasSame(Set<IHLCable> set, Set<IHLCable> set2)
	{
		Iterator<IHLCable> i1 = set.iterator();
		while(i1.hasNext())
		{
			IHLCable num1=i1.next();
			if(set2.contains(num1))
			{
				return true;
			}
		}
		return false;
	}

	public void setOnFire(IHLCable cable) 
	{
		Set<NodeEntity> cs = IHLMod.proxy.nodeEntityRegistry.get(cable.chainUID);
		if(cs!=null)
		{
			for(NodeEntity ne:cs)
			{
				ne.setFire(10);
			}
		}
	}

	public void removeCableEntities(IHLCable cable) 
	{
		int uid = cable.chainUID;
		Set<NodeEntity> cs = IHLMod.proxy.nodeEntityRegistry.get(uid);
		if(cs!=null)
		{
			for(NodeEntity ne:cs)
			{
				if(ne!=null)
				{
					ne.setDead();
				}
			}
		}
		cablesToGrids.remove(uid);
	}
}