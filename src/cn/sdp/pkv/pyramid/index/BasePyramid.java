package cn.sdp.pkv.pyramid.index;

import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;

import cn.sdp.pkv.pyramid.index.Pyramid.PyramidRange;
import cn.sdp.pkv.pyramid.model.PyramidResult;


public class BasePyramid extends Pyramid{
	
	public BasePyramid(int d, Object[] lb, Object[] ub) {
		super(d, lb, ub);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PyramidResult getPyramidResult(Object[] origin)
	{
		double[] v = getNormalization(origin);
		int pyId = getPyramidId(v);
		double hv = getHv(v, pyId);
		return new PyramidResult(v, pyId+hv);
	}
	
	
	public List<PyramidRange> getPyramidRange(Object[] qlbound, Object[] qubound)
	{
		double[] ql = getNormalization(qlbound);
		double[] qu = getNormalization(qubound);
		
		List<Integer> intersectPy = getIntersects(ql, qu);		
		return getRange(intersectPy, ql, qu); 
	}

	@Override
	public List<PyramidRange> getPyramidKNNRange(double[] norV, double distance, int pyId)
	{
		List<PyramidRange> knnRanges = new ArrayList<PyramidRange>();
		int d = pyId%dimension;
		double high = Double.compare(norV[d]+distance,1.0)>0?1.0:(norV[d]+distance);
		double low = Double.compare(norV[d]-distance,0.0)<0?0.0:(norV[d]-distance);
		if (pyId < dimension)
		{
			if (Double.compare(low, 0.5) > 0)
				return Collections.emptyList();
			else
				low = Math.abs(0.5-low);
			if (Double.compare(high, 0.5) > 0)
				high = 0.0;
			else
				high = Math.abs(0.5-high);
		}
		else
		{
			if (Double.compare(high, 0.5) < 0)
				return Collections.emptyList();
			else
				high = Math.abs(0.5-high);
			if (Double.compare(low, 0.5) < 0)
				low = 0.0;
			else
				low = Math.abs(0.5-low);
		}
		knnRanges.add(new PyramidRange(Math.min(low, high), Math.max(low, high)));
		return knnRanges;
	}
	
	private List<PyramidRange> getRange(List<Integer> intersectPy, double[] ql, double[] qu) {
		List<PyramidRange> ranges = new ArrayList<PyramidRange>();
		for (int pId : intersectPy)
		{
			double low = 0;
			double high = 0;
			if (pId < dimension)
			{
				if ((0.5-ql[pId])*(0.5-qu[pId]) >= 0)
				{
					low = Math.min(Math.abs(0.5-ql[pId]), Math.abs(0.5-qu[pId]));
					high = Math.max(Math.abs(0.5-ql[pId]), Math.abs(0.5-qu[pId]));
				}
				else
				{
					low = 0;
					high = 0.5-ql[pId];
				}
			}
			else
			{
				if ((0.5-ql[pId-dimension])*(0.5-qu[pId-dimension]) >= 0)
				{
					low = Math.min(Math.abs(0.5-ql[pId-dimension]), Math.abs(0.5-qu[pId-dimension]));
					high = Math.max(Math.abs(0.5-ql[pId-dimension]), Math.abs(0.5-qu[pId-dimension]));
				}
				else
				{
					low = 0;
					high = 0.5-ql[pId-dimension];
				}
			}

			PyramidRange range = new PyramidRange(pId+low, pId+high);
			ranges.add(range);
		}
		return ranges;
	}

	private List<Integer> getIntersects(double[] ql, double[] qu) {
		List<Integer> inters = new ArrayList<Integer>();
		for (int i = 0;i < dimension;i++)
		{
			boolean flag = true;
			for (int j = 0;j < dimension;j++)
			{
				if (j == i)
					continue;
				if (ql[i] > qu[j] || ql[i] > 1-ql[j])
				{
					flag = false;
					break;
				}
			}
			if (flag)
				inters.add(i);
			flag = true;
			for (int j = 0;j < dimension;j++)
			{
				if (j == i)
					continue;
				if (1-qu[i] > qu[j] || 1-qu[i] > 1-ql[j])
				{
					flag = false;
					break;
				}
			}
			if (flag)
				inters.add(i+dimension);
		}
		return inters;
	}

	private double getHv(double[] v, int pyId) {
		return Math.abs(0.5-v[pyId%dimension]);
	}

	private int getPyramidId(double[] v) {
		int jmax = 0;
		double maxr = 0.0;
		for (int i = 0;i < dimension;i++)
		{
			double tempr = Math.abs(0.5-v[i]);
			if (tempr > maxr)
			{
				maxr = tempr;
				jmax = i;
			}
		}
		if (v[jmax] < 0.5)
			return jmax;
		else
			return jmax+dimension;
	}

	@Override
	public int getInterval() {
		return 1;
	}


}
