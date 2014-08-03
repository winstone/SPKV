package cn.sdp.pkv.pyramid.index;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import cn.sdp.pkv.pyramid.model.PyramidResult;

public abstract class Pyramid {
	protected int dimension;
	protected Object[] lbound;
	protected Object[] ubound;

	/**
	 * Get the slice which may contain data that can satisfy the (point) query
	 * condition.
	 * 
	 * @param origin
	 * @return
	 */
	abstract public PyramidResult getPyramidResult(Object[] origin);

	/**
	 * Get slices which may contain data that can satisfy the (range) query
	 * condition.
	 * 
	 * @param qlbound
	 *            the lower bound of the query
	 * @param qubound
	 *            the upper bound of the query
	 * @return
	 */
	abstract public List<PyramidRange> getPyramidRange(Object[] qlbound,
			Object[] qubound);

	/**
	 * Get slices which may contain data that can satisfy the (kNN) query
	 * condition.
	 * 
	 * @param norV
	 * @param distance
	 * @param pyId
	 * @return
	 */
	abstract public List<PyramidRange> getPyramidKNNRange(double[] norV,
			double distance, int pyId);

	/**
	 * Get the interval value for each pId. The interval value can be used to
	 * generate the pv and also can be used to get pId from a pv.<br>
	 * <li>For basic pyramid technology, the interval is 1.</li><br>
	 * <li>For sliced pyramid technology, the interval is depend on dimension of
	 * data and the value is 2 power the dimension.</li>
	 * 
	 * @return
	 */
	abstract public int getInterval();

	public Pyramid(int d, Object[] lb, Object[] ub) {
		dimension = d;
		lbound = lb;
		ubound = ub;
	}

	/**
	 * Normalize the data and map the data to a value between 0~1.
	 * 
	 * @param origin
	 * @return A value between 0~1
	 */
	public double[] getNormalization(Object[] origin) {
		double[] v = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			if (origin[i] instanceof String)
				v[i] = normalization((String) origin[i], (String) lbound[i],
						(String) ubound[i]);
			if (origin[i] instanceof Double)
				v[i] = normalization((Double) origin[i], (Double) lbound[i],
						(Double) ubound[i]);
			if (origin[i] instanceof Integer)
				v[i] = normalization((Integer) origin[i], (Integer) lbound[i],
						(Integer) ubound[i]);
		}
		return v;
	}

	public String getNormalizationString(Object[] origin) {
		double[] v = getNormalization(origin);
		StringBuilder normals = new StringBuilder();
		for (int i = 0; i < dimension; i++) {
			if (i > 0)
				normals.append("," + String.format("%.08f", v[i]));
			else
				normals.append(String.format("%.08f", v[i]));
		}
		return normals.toString();
	}

	protected double normalization(int originValue, int lbound, int ubound) {
		if (originValue == -ubound)
			return -1;
		else
			return (originValue - lbound + 0.0) / (ubound - lbound);
	}

	protected double normalization(String originValue, String lbound,
			String ubound) {
		BigInteger u = BigInteger.ONE;
		u = u.shiftLeft(128).subtract(BigInteger.ONE);
		byte[] md5 = getMD5byte(originValue);

		BigDecimal temp = new BigDecimal(new BigInteger(1, md5));
		BigDecimal udec = new BigDecimal(u);
		return temp.divide(udec, 8, BigDecimal.ROUND_FLOOR).doubleValue();
	}

	protected double normalization(double originValue, double lbound,
			double ubound) {
		originValue = (originValue - lbound) / (ubound - lbound);
		return originValue;
	}

	private byte[] getMD5byte(String str) {
		byte[] md5byte = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update(str.getBytes("utf-8"));
			md5byte = md5.digest();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return md5byte;
	}

	public class PyramidRange {
		public double low;
		public double high;

		public PyramidRange(double l, double h) {
			low = l;
			high = h;
		}
	}

	public int getDimension() {
		return dimension;
	}

	public Object[] getLbound() {
		return lbound;
	}

	public Object[] getUbound() {
		return ubound;
	}

}
