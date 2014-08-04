package cn.sdp.pkv.pyramid.model;

public class PyramidResult {
	private double[] norCols;
	private double pv;
	
	public PyramidResult(double[] norCols, double pv) {
		super();
		this.norCols = norCols;
		this.pv = pv;
	}

	public double[] getNorCols() {
		return norCols;
	} 

	public double getPv() {
		return pv;
	}
	
}
