package cn.sdp.pkv.data.type;

import java.io.Serializable;

import cn.sdp.pkv.store.model.ValueObject;

public class TableOperate implements Serializable {
	private static final long serialVersionUID = -8528447288030535799L;
	private char type;
	private String tableName;
	private String key;
	private ValueObject valueObject;
	
	public TableOperate(char type, String tableName, String key, ValueObject vObj) {
		this.tableName = tableName;
		this.type = type;
		this.key = key;
		this.valueObject = vObj;
	}

	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setValueObject(ValueObject valueObject) {
		this.valueObject = valueObject;
	}
	public ValueObject getValueObject() {
		return valueObject;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	
}
