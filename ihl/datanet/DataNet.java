package ihl.datanet;

import ihl.IHLMod;
import ihl.flexible_cable.NodeEntity;
import ihl.interfaces.IDataNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

public class DataNet {
	
	public Map<Integer, DataGrid> grids = new HashMap();
	private int griduid=0;
	
	public DataNet()
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
			DataGrid cgrid;
			cgrid=new DataGrid();
			grids.put(newGridID, cgrid);
			return newGridID;
		}
		else if(gridID!=gridID2)
		{
			Iterator<IDataNode> tei = grids.get(gridID2).telist.iterator();
			while(tei.hasNext())
			{
				IDataNode te = tei.next();
				te.setDataGrid(gridID);
			}
			grids.remove(gridID2);
			return gridID;
		}
		return gridID2;
	}

	
	public DataGrid getGrid(int gridID)
	{
		if(this.grids.get(gridID)==null)
		{
			DataGrid cgrid;
			cgrid=new DataGrid();
			grids.put(gridID, cgrid);
			return cgrid;
		}
		else
		{
			return this.grids.get(gridID);
		}
	}
	
	public void splitGrids(int gridID)
	{
		DataGrid grid1 = this.grids.get(gridID);
		Set<IDataNode> excludedNodes = grid1.getListOfExcludedNodes();
		if(!excludedNodes.isEmpty())
		{
			if(grid1.telist.size()==1)
			{
				IDataNode singleNode = grid1.telist.iterator().next();
				singleNode.setDataGrid(-1);
				grid1.telist.remove(singleNode);
			}
			if(excludedNodes.size()==1)
			{
				IDataNode singleNode = excludedNodes.iterator().next();
				singleNode.setDataGrid(-1);
			}
			else //Form a new grid
			{
				int newGridId = this.getNewUniqueGridID();
				Iterator<IDataNode> excludedNodesI = excludedNodes.iterator();
				while(excludedNodesI.hasNext())
				{
					excludedNodesI.next().setDataGrid(newGridId);
				}
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
	}
}
