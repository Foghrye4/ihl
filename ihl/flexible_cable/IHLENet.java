package ihl.flexible_cable;

import ihl.IHLMod;
import ihl.interfaces.IEnergyNetNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class IHLENet {
	
	public Map<Integer, IHLGrid> grids = new HashMap();
	public Map<Integer, IHLGrid> cablesToGrids = new HashMap();
	
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
		telist.remove(exclude);
		Iterator<IEnergyNetNode> atei = telist.iterator();
		while(atei.hasNext())
		{
			IEnergyNetNode cte = atei.next();
			cte.getCableList().removeAll(exclude.getCableList());
			cte.setGrid(-1);
		}
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
	
	public void removeCableAndSplitGrids(int gridID, NBTTagCompound cable)
	{
		this.grids.get(gridID).removeCableAndSplitGrids(cable);
	}
	
	public boolean hasSame(Set<NBTTagCompound> set, Set<NBTTagCompound> set2)
	{
		Iterator<NBTTagCompound> i1 = set.iterator();
		while(i1.hasNext())
		{
			NBTTagCompound num1=i1.next();
			if(set2.contains(num1))
			{
				return true;
			}
		}
		return false;
	}

	public void setOnFire(NBTTagCompound cable) 
	{
		Set<NodeEntity> cs = IHLMod.proxy.nodeEntityRegistry.get(cable.getInteger("chainUID"));
		if(cs!=null)
		{
			for(NodeEntity ne:cs)
			{
				ne.setFire(10);
			}
		}
	}

	public void removeCableEntities(NBTTagCompound cable) 
	{
		int uid = cable.getInteger("chainUID");
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