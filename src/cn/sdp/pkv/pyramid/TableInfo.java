package cn.sdp.pkv.pyramid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TableInfo implements Serializable {
	private static final long serialVersionUID = -1163728946629292983L;
	private final String tableName;
	private final List<Integer> lb;
	private final List<Integer> ub;
	private final List<String> indexColumn;
	private final List<String> indexContent;
	private final int dimension;
	private final boolean type;
	
	public TableInfo(String tableName, List<Integer> lb, List<Integer> ub, int dimension, boolean type) {
		this.lb = lb;
		this.ub = ub;
		this.dimension = dimension;
		this.tableName = tableName;
		this.type = type;
		this.indexColumn = new ArrayList<String>();
		this.indexContent = new ArrayList<String>();
	}
	
	public TableInfo(String tableName, List<Integer> lb, List<Integer> ub, int dimension, boolean type, 
			List<String> index, List<String> content) {
		this.lb = lb;
		this.ub = ub;
		this.dimension = dimension;
		this.tableName = tableName;
		this.type = type;
		this.indexColumn = index;
		this.indexContent = content;
	}

	public List<Integer> getLb() {
		return lb;
	}
	public List<Integer> getUb() {
		return ub;
	}
	public int getDimension() {
		return dimension;
	}
	public String getTableName() {
		return tableName;
	}
	public boolean isType() {
		return type;
	}
	public List<String> getIndexColumn() {
		return indexColumn;
	}
	public List<String> getIndexContent() {
		return indexContent;
	}
}
