package cn.sdp.pkv.pyramid.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.sdp.pkv.pyramid.model.PyramidResult;
import cn.sdp.pkv.util.Configs;

 
public class ExtendPyramid extends Pyramid{
	private double hvInterval;
	private int hvDimension;
	private double[] sliceValue;
	private final int DIMENSION_THRESHOLD = Configs.DIMENSION_THRESHOLD;
	
	public ExtendPyramid(int d, Object[] lb, Object[] ub) {
		super(d, lb, ub);
		// TODO Auto-generated constructor stub
		if (d > DIMENSION_THRESHOLD)
			hvDimension = DIMENSION_THRESHOLD;
		else
			hvDimension = d-1;
		hvInterval = Math.pow(2.0, hvDimension);
		sliceValue = new double[dimension];
		Arrays.fill(sliceValue, 0.5);
	}

	@Override
	public PyramidResult getPyramidResult(Object[] origin)
	{
		double[] v = getNormalization(origin);
		int pyId = getPyramidId(v);
		double hv = getHv(v, pyId);
		return new PyramidResult(v, pyId*hvInterval+hv);
	}
	
	
	public List<PyramidRange> getPyramidRange(Object[] qlbound, Object[] qubound)
	{
		double[] ql = getNormalization(qlbound);
		double[] qu = getNormalization(qubound);
		
		List<Integer> intersectPy = getIntersects(ql, qu);
		return getRange(intersectPy, ql, qu);
	}
	
	public List<PyramidRange> getPyramidKNNRange(double[] norV, double distance, int pyId)
	{
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
		List<PyramidRange> knnRanges = new ArrayList<PyramidRange>();
		searchKNNRange(0, 0, 0, norV, distance, pyId, Math.min(low, high), Math.max(low, high), knnRanges);
		return knnRanges;
	}

	private void searchKNNRange(int depth, int d, int value, double[] norV, double distance, int pId,
								double vlow, double vhigh, List<PyramidRange> res)
	{
		if (depth == hvDimension)
		{
			res.add(new PyramidRange(pId*hvInterval+value+vlow, pId*hvInterval+value+vhigh));
			return;
		}
		if (d == pId%dimension)
		{
			searchKNNRange(depth, d+1, value, norV, distance, pId, vlow, vhigh, res);
		}
		else
		{
			double high = Double.compare(norV[d]+distance,1.0)>0?1.0:(norV[d]+distance);
			double low = Double.compare(norV[d]-distance,0.0)<0?0.0:(norV[d]-distance);
			if (high <= sliceValue[d])
			{
				value <<= 1;
				searchKNNRange(depth+1, d+1, value, norV, distance, pId, vlow, vhigh, res);
			}
			else if (low > sliceValue[d])
			{
				value <<= 1;
				value |= 1;
				searchKNNRange(depth+1, d+1, value, norV, distance, pId, vlow, vhigh, res);
			}
			else 
			{
				value <<= 1;
				searchKNNRange(depth+1, d+1, value, norV, distance, pId, vlow, vhigh, res);
				value |= 1;
				searchKNNRange(depth+1, d+1, value, norV, distance, pId, vlow, vhigh, res);
			}
		}
	}
	
	private List<PyramidRange> getRange(List<Integer> intersectPy, double[] ql, double[] qu)
	{
		List<PyramidRange> res = new ArrayList<PyramidRange>();
		for (int pId : intersectPy)
		{
			double tlow = Math.abs(0.5-ql[pId%dimension]);
			double thigh = Math.abs(0.5-qu[pId%dimension]);
			double low = 0.0;
			double high = 0.0;
			if ((0.5-ql[pId%dimension])*(0.5-qu[pId%dimension]) >= 0)
			{
				low = Math.min(tlow, thigh);
				high = Math.max(tlow, thigh);
			}
			else
			{
				if (pId < dimension)
					high = tlow;
				else
					high = thigh;
				low = 0;
			}
			searchRange(0, 0, 0, ql, qu, pId, low, high, res);
		}
		return res;
	}

	private void searchRange(int depth, int d, int value, double[] ql, double[] qu, int pId,
					double low, double high, List<PyramidRange> res)
	{
		if (depth == hvDimension)
		{
			res.add(new PyramidRange(pId*hvInterval+value+low, pId*hvInterval+value+high));
			return;
		}
		if (d == pId%dimension)
		{
			searchRange(depth, d+1, value, ql, qu, pId, low, high, res);
		}
		else
		{
			if (qu[d] <= sliceValue[d])
			{
				value <<= 1;
				searchRange(depth+1,  d+1, value, ql, qu, pId, low, high, res);
			}
			else if (ql[d] > sliceValue[d])
			{
				value <<= 1;
				value |= 1;
				searchRange(depth+1,  d+1, value, ql, qu, pId, low, high, res);
			}
			else 
			{
				value <<= 1;
				searchRange(depth+1,  d+1, value, ql, qu, pId, low, high, res);
				value |= 1;
				searchRange(depth+1,  d+1, value, ql, qu, pId, low, high, res);
			}
		}
		
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
		int partition = getPartitionValue(v, pyId);
		return partition+Math.abs(0.5-v[pyId%dimension]);
	}

	private int getPartitionValue(double[] v, int pyId) {
		int partition = 0;
		pyId %= dimension;
		int round = 0, i = 0;
		while (round < hvDimension)
		{
			if (i != pyId)
			{
				round++;
				partition <<= 1;
				if (v[i] < 0 || v[i] > sliceValue[i])
					partition |= 1;
			}
			i++;
		}
		return partition;
	}

	private int getPyramidId(double[] v) {
		int jmax = 0;
		double maxr = 0.0;
		for (int i = 0;i < dimension;i++)
		{
			if (v[i] >= 0)
			{
				double tempr = Math.abs(0.5-v[i]);
				if (tempr > maxr)
				{
					maxr = tempr;
					jmax = i;
				}
			}
		}
		if (v[jmax] < 0.5)
			return jmax;
		else
			return jmax+dimension;
	}

	@Override
	public int getInterval() {
		return ((Double)hvInterval).intValue();
	}

}
