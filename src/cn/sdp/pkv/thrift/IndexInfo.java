/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package cn.sdp.pkv.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexInfo implements org.apache.thrift.TBase<IndexInfo, IndexInfo._Fields>, java.io.Serializable, Cloneable, Comparable<IndexInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("IndexInfo");

  private static final org.apache.thrift.protocol.TField TABLE_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("tableName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField LB_FIELD_DESC = new org.apache.thrift.protocol.TField("lb", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField UB_FIELD_DESC = new org.apache.thrift.protocol.TField("ub", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField D_FIELD_DESC = new org.apache.thrift.protocol.TField("d", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField INDEX_COLUMN_FIELD_DESC = new org.apache.thrift.protocol.TField("indexColumn", org.apache.thrift.protocol.TType.LIST, (short)5);
  private static final org.apache.thrift.protocol.TField INDEX_CONTENT_FIELD_DESC = new org.apache.thrift.protocol.TField("indexContent", org.apache.thrift.protocol.TType.LIST, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new IndexInfoStandardSchemeFactory());
    schemes.put(TupleScheme.class, new IndexInfoTupleSchemeFactory());
  }

  public String tableName; // required
  public List<Integer> lb; // required
  public List<Integer> ub; // required
  public int d; // required
  public List<String> indexColumn; // required
  public List<String> indexContent; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TABLE_NAME((short)1, "tableName"),
    LB((short)2, "lb"),
    UB((short)3, "ub"),
    D((short)4, "d"),
    INDEX_COLUMN((short)5, "indexColumn"),
    INDEX_CONTENT((short)6, "indexContent");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // TABLE_NAME
          return TABLE_NAME;
        case 2: // LB
          return LB;
        case 3: // UB
          return UB;
        case 4: // D
          return D;
        case 5: // INDEX_COLUMN
          return INDEX_COLUMN;
        case 6: // INDEX_CONTENT
          return INDEX_CONTENT;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __D_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TABLE_NAME, new org.apache.thrift.meta_data.FieldMetaData("tableName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LB, new org.apache.thrift.meta_data.FieldMetaData("lb", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields.UB, new org.apache.thrift.meta_data.FieldMetaData("ub", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields.D, new org.apache.thrift.meta_data.FieldMetaData("d", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.INDEX_COLUMN, new org.apache.thrift.meta_data.FieldMetaData("indexColumn", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.INDEX_CONTENT, new org.apache.thrift.meta_data.FieldMetaData("indexContent", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(IndexInfo.class, metaDataMap);
  }

  public IndexInfo() {
  }

  public IndexInfo(
    String tableName,
    List<Integer> lb,
    List<Integer> ub,
    int d,
    List<String> indexColumn,
    List<String> indexContent)
  {
    this();
    this.tableName = tableName;
    this.lb = lb;
    this.ub = ub;
    this.d = d;
    setDIsSet(true);
    this.indexColumn = indexColumn;
    this.indexContent = indexContent;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public IndexInfo(IndexInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetTableName()) {
      this.tableName = other.tableName;
    }
    if (other.isSetLb()) {
      List<Integer> __this__lb = new ArrayList<Integer>(other.lb);
      this.lb = __this__lb;
    }
    if (other.isSetUb()) {
      List<Integer> __this__ub = new ArrayList<Integer>(other.ub);
      this.ub = __this__ub;
    }
    this.d = other.d;
    if (other.isSetIndexColumn()) {
      List<String> __this__indexColumn = new ArrayList<String>(other.indexColumn);
      this.indexColumn = __this__indexColumn;
    }
    if (other.isSetIndexContent()) {
      List<String> __this__indexContent = new ArrayList<String>(other.indexContent);
      this.indexContent = __this__indexContent;
    }
  }

  public IndexInfo deepCopy() {
    return new IndexInfo(this);
  }

  @Override
  public void clear() {
    this.tableName = null;
    this.lb = null;
    this.ub = null;
    setDIsSet(false);
    this.d = 0;
    this.indexColumn = null;
    this.indexContent = null;
  }

  public String getTableName() {
    return this.tableName;
  }

  public IndexInfo setTableName(String tableName) {
    this.tableName = tableName;
    return this;
  }

  public void unsetTableName() {
    this.tableName = null;
  }

  /** Returns true if field tableName is set (has been assigned a value) and false otherwise */
  public boolean isSetTableName() {
    return this.tableName != null;
  }

  public void setTableNameIsSet(boolean value) {
    if (!value) {
      this.tableName = null;
    }
  }

  public int getLbSize() {
    return (this.lb == null) ? 0 : this.lb.size();
  }

  public java.util.Iterator<Integer> getLbIterator() {
    return (this.lb == null) ? null : this.lb.iterator();
  }

  public void addToLb(int elem) {
    if (this.lb == null) {
      this.lb = new ArrayList<Integer>();
    }
    this.lb.add(elem);
  }

  public List<Integer> getLb() {
    return this.lb;
  }

  public IndexInfo setLb(List<Integer> lb) {
    this.lb = lb;
    return this;
  }

  public void unsetLb() {
    this.lb = null;
  }

  /** Returns true if field lb is set (has been assigned a value) and false otherwise */
  public boolean isSetLb() {
    return this.lb != null;
  }

  public void setLbIsSet(boolean value) {
    if (!value) {
      this.lb = null;
    }
  }

  public int getUbSize() {
    return (this.ub == null) ? 0 : this.ub.size();
  }

  public java.util.Iterator<Integer> getUbIterator() {
    return (this.ub == null) ? null : this.ub.iterator();
  }

  public void addToUb(int elem) {
    if (this.ub == null) {
      this.ub = new ArrayList<Integer>();
    }
    this.ub.add(elem);
  }

  public List<Integer> getUb() {
    return this.ub;
  }

  public IndexInfo setUb(List<Integer> ub) {
    this.ub = ub;
    return this;
  }

  public void unsetUb() {
    this.ub = null;
  }

  /** Returns true if field ub is set (has been assigned a value) and false otherwise */
  public boolean isSetUb() {
    return this.ub != null;
  }

  public void setUbIsSet(boolean value) {
    if (!value) {
      this.ub = null;
    }
  }

  public int getD() {
    return this.d;
  }

  public IndexInfo setD(int d) {
    this.d = d;
    setDIsSet(true);
    return this;
  }

  public void unsetD() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __D_ISSET_ID);
  }

  /** Returns true if field d is set (has been assigned a value) and false otherwise */
  public boolean isSetD() {
    return EncodingUtils.testBit(__isset_bitfield, __D_ISSET_ID);
  }

  public void setDIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __D_ISSET_ID, value);
  }

  public int getIndexColumnSize() {
    return (this.indexColumn == null) ? 0 : this.indexColumn.size();
  }

  public java.util.Iterator<String> getIndexColumnIterator() {
    return (this.indexColumn == null) ? null : this.indexColumn.iterator();
  }

  public void addToIndexColumn(String elem) {
    if (this.indexColumn == null) {
      this.indexColumn = new ArrayList<String>();
    }
    this.indexColumn.add(elem);
  }

  public List<String> getIndexColumn() {
    return this.indexColumn;
  }

  public IndexInfo setIndexColumn(List<String> indexColumn) {
    this.indexColumn = indexColumn;
    return this;
  }

  public void unsetIndexColumn() {
    this.indexColumn = null;
  }

  /** Returns true if field indexColumn is set (has been assigned a value) and false otherwise */
  public boolean isSetIndexColumn() {
    return this.indexColumn != null;
  }

  public void setIndexColumnIsSet(boolean value) {
    if (!value) {
      this.indexColumn = null;
    }
  }

  public int getIndexContentSize() {
    return (this.indexContent == null) ? 0 : this.indexContent.size();
  }

  public java.util.Iterator<String> getIndexContentIterator() {
    return (this.indexContent == null) ? null : this.indexContent.iterator();
  }

  public void addToIndexContent(String elem) {
    if (this.indexContent == null) {
      this.indexContent = new ArrayList<String>();
    }
    this.indexContent.add(elem);
  }

  public List<String> getIndexContent() {
    return this.indexContent;
  }

  public IndexInfo setIndexContent(List<String> indexContent) {
    this.indexContent = indexContent;
    return this;
  }

  public void unsetIndexContent() {
    this.indexContent = null;
  }

  /** Returns true if field indexContent is set (has been assigned a value) and false otherwise */
  public boolean isSetIndexContent() {
    return this.indexContent != null;
  }

  public void setIndexContentIsSet(boolean value) {
    if (!value) {
      this.indexContent = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TABLE_NAME:
      if (value == null) {
        unsetTableName();
      } else {
        setTableName((String)value);
      }
      break;

    case LB:
      if (value == null) {
        unsetLb();
      } else {
        setLb((List<Integer>)value);
      }
      break;

    case UB:
      if (value == null) {
        unsetUb();
      } else {
        setUb((List<Integer>)value);
      }
      break;

    case D:
      if (value == null) {
        unsetD();
      } else {
        setD((Integer)value);
      }
      break;

    case INDEX_COLUMN:
      if (value == null) {
        unsetIndexColumn();
      } else {
        setIndexColumn((List<String>)value);
      }
      break;

    case INDEX_CONTENT:
      if (value == null) {
        unsetIndexContent();
      } else {
        setIndexContent((List<String>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TABLE_NAME:
      return getTableName();

    case LB:
      return getLb();

    case UB:
      return getUb();

    case D:
      return Integer.valueOf(getD());

    case INDEX_COLUMN:
      return getIndexColumn();

    case INDEX_CONTENT:
      return getIndexContent();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TABLE_NAME:
      return isSetTableName();
    case LB:
      return isSetLb();
    case UB:
      return isSetUb();
    case D:
      return isSetD();
    case INDEX_COLUMN:
      return isSetIndexColumn();
    case INDEX_CONTENT:
      return isSetIndexContent();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof IndexInfo)
      return this.equals((IndexInfo)that);
    return false;
  }

  public boolean equals(IndexInfo that) {
    if (that == null)
      return false;

    boolean this_present_tableName = true && this.isSetTableName();
    boolean that_present_tableName = true && that.isSetTableName();
    if (this_present_tableName || that_present_tableName) {
      if (!(this_present_tableName && that_present_tableName))
        return false;
      if (!this.tableName.equals(that.tableName))
        return false;
    }

    boolean this_present_lb = true && this.isSetLb();
    boolean that_present_lb = true && that.isSetLb();
    if (this_present_lb || that_present_lb) {
      if (!(this_present_lb && that_present_lb))
        return false;
      if (!this.lb.equals(that.lb))
        return false;
    }

    boolean this_present_ub = true && this.isSetUb();
    boolean that_present_ub = true && that.isSetUb();
    if (this_present_ub || that_present_ub) {
      if (!(this_present_ub && that_present_ub))
        return false;
      if (!this.ub.equals(that.ub))
        return false;
    }

    boolean this_present_d = true;
    boolean that_present_d = true;
    if (this_present_d || that_present_d) {
      if (!(this_present_d && that_present_d))
        return false;
      if (this.d != that.d)
        return false;
    }

    boolean this_present_indexColumn = true && this.isSetIndexColumn();
    boolean that_present_indexColumn = true && that.isSetIndexColumn();
    if (this_present_indexColumn || that_present_indexColumn) {
      if (!(this_present_indexColumn && that_present_indexColumn))
        return false;
      if (!this.indexColumn.equals(that.indexColumn))
        return false;
    }

    boolean this_present_indexContent = true && this.isSetIndexContent();
    boolean that_present_indexContent = true && that.isSetIndexContent();
    if (this_present_indexContent || that_present_indexContent) {
      if (!(this_present_indexContent && that_present_indexContent))
        return false;
      if (!this.indexContent.equals(that.indexContent))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(IndexInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetTableName()).compareTo(other.isSetTableName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTableName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tableName, other.tableName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLb()).compareTo(other.isSetLb());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLb()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.lb, other.lb);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUb()).compareTo(other.isSetUb());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUb()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ub, other.ub);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetD()).compareTo(other.isSetD());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetD()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.d, other.d);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIndexColumn()).compareTo(other.isSetIndexColumn());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndexColumn()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.indexColumn, other.indexColumn);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIndexContent()).compareTo(other.isSetIndexContent());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndexContent()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.indexContent, other.indexContent);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("IndexInfo(");
    boolean first = true;

    sb.append("tableName:");
    if (this.tableName == null) {
      sb.append("null");
    } else {
      sb.append(this.tableName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("lb:");
    if (this.lb == null) {
      sb.append("null");
    } else {
      sb.append(this.lb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("ub:");
    if (this.ub == null) {
      sb.append("null");
    } else {
      sb.append(this.ub);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("d:");
    sb.append(this.d);
    first = false;
    if (!first) sb.append(", ");
    sb.append("indexColumn:");
    if (this.indexColumn == null) {
      sb.append("null");
    } else {
      sb.append(this.indexColumn);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("indexContent:");
    if (this.indexContent == null) {
      sb.append("null");
    } else {
      sb.append(this.indexContent);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class IndexInfoStandardSchemeFactory implements SchemeFactory {
    public IndexInfoStandardScheme getScheme() {
      return new IndexInfoStandardScheme();
    }
  }

  private static class IndexInfoStandardScheme extends StandardScheme<IndexInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, IndexInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // TABLE_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.tableName = iprot.readString();
              struct.setTableNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // LB
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.lb = new ArrayList<Integer>(_list0.size);
                for (int _i1 = 0; _i1 < _list0.size; ++_i1)
                {
                  int _elem2;
                  _elem2 = iprot.readI32();
                  struct.lb.add(_elem2);
                }
                iprot.readListEnd();
              }
              struct.setLbIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // UB
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list3 = iprot.readListBegin();
                struct.ub = new ArrayList<Integer>(_list3.size);
                for (int _i4 = 0; _i4 < _list3.size; ++_i4)
                {
                  int _elem5;
                  _elem5 = iprot.readI32();
                  struct.ub.add(_elem5);
                }
                iprot.readListEnd();
              }
              struct.setUbIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // D
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.d = iprot.readI32();
              struct.setDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // INDEX_COLUMN
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list6 = iprot.readListBegin();
                struct.indexColumn = new ArrayList<String>(_list6.size);
                for (int _i7 = 0; _i7 < _list6.size; ++_i7)
                {
                  String _elem8;
                  _elem8 = iprot.readString();
                  struct.indexColumn.add(_elem8);
                }
                iprot.readListEnd();
              }
              struct.setIndexColumnIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // INDEX_CONTENT
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list9 = iprot.readListBegin();
                struct.indexContent = new ArrayList<String>(_list9.size);
                for (int _i10 = 0; _i10 < _list9.size; ++_i10)
                {
                  String _elem11;
                  _elem11 = iprot.readString();
                  struct.indexContent.add(_elem11);
                }
                iprot.readListEnd();
              }
              struct.setIndexContentIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, IndexInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.tableName != null) {
        oprot.writeFieldBegin(TABLE_NAME_FIELD_DESC);
        oprot.writeString(struct.tableName);
        oprot.writeFieldEnd();
      }
      if (struct.lb != null) {
        oprot.writeFieldBegin(LB_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct.lb.size()));
          for (int _iter12 : struct.lb)
          {
            oprot.writeI32(_iter12);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.ub != null) {
        oprot.writeFieldBegin(UB_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct.ub.size()));
          for (int _iter13 : struct.ub)
          {
            oprot.writeI32(_iter13);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(D_FIELD_DESC);
      oprot.writeI32(struct.d);
      oprot.writeFieldEnd();
      if (struct.indexColumn != null) {
        oprot.writeFieldBegin(INDEX_COLUMN_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.indexColumn.size()));
          for (String _iter14 : struct.indexColumn)
          {
            oprot.writeString(_iter14);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.indexContent != null) {
        oprot.writeFieldBegin(INDEX_CONTENT_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.indexContent.size()));
          for (String _iter15 : struct.indexContent)
          {
            oprot.writeString(_iter15);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class IndexInfoTupleSchemeFactory implements SchemeFactory {
    public IndexInfoTupleScheme getScheme() {
      return new IndexInfoTupleScheme();
    }
  }

  private static class IndexInfoTupleScheme extends TupleScheme<IndexInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, IndexInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetTableName()) {
        optionals.set(0);
      }
      if (struct.isSetLb()) {
        optionals.set(1);
      }
      if (struct.isSetUb()) {
        optionals.set(2);
      }
      if (struct.isSetD()) {
        optionals.set(3);
      }
      if (struct.isSetIndexColumn()) {
        optionals.set(4);
      }
      if (struct.isSetIndexContent()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetTableName()) {
        oprot.writeString(struct.tableName);
      }
      if (struct.isSetLb()) {
        {
          oprot.writeI32(struct.lb.size());
          for (int _iter16 : struct.lb)
          {
            oprot.writeI32(_iter16);
          }
        }
      }
      if (struct.isSetUb()) {
        {
          oprot.writeI32(struct.ub.size());
          for (int _iter17 : struct.ub)
          {
            oprot.writeI32(_iter17);
          }
        }
      }
      if (struct.isSetD()) {
        oprot.writeI32(struct.d);
      }
      if (struct.isSetIndexColumn()) {
        {
          oprot.writeI32(struct.indexColumn.size());
          for (String _iter18 : struct.indexColumn)
          {
            oprot.writeString(_iter18);
          }
        }
      }
      if (struct.isSetIndexContent()) {
        {
          oprot.writeI32(struct.indexContent.size());
          for (String _iter19 : struct.indexContent)
          {
            oprot.writeString(_iter19);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, IndexInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.tableName = iprot.readString();
        struct.setTableNameIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list20 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.lb = new ArrayList<Integer>(_list20.size);
          for (int _i21 = 0; _i21 < _list20.size; ++_i21)
          {
            int _elem22;
            _elem22 = iprot.readI32();
            struct.lb.add(_elem22);
          }
        }
        struct.setLbIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list23 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.ub = new ArrayList<Integer>(_list23.size);
          for (int _i24 = 0; _i24 < _list23.size; ++_i24)
          {
            int _elem25;
            _elem25 = iprot.readI32();
            struct.ub.add(_elem25);
          }
        }
        struct.setUbIsSet(true);
      }
      if (incoming.get(3)) {
        struct.d = iprot.readI32();
        struct.setDIsSet(true);
      }
      if (incoming.get(4)) {
        {
          org.apache.thrift.protocol.TList _list26 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.indexColumn = new ArrayList<String>(_list26.size);
          for (int _i27 = 0; _i27 < _list26.size; ++_i27)
          {
            String _elem28;
            _elem28 = iprot.readString();
            struct.indexColumn.add(_elem28);
          }
        }
        struct.setIndexColumnIsSet(true);
      }
      if (incoming.get(5)) {
        {
          org.apache.thrift.protocol.TList _list29 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.indexContent = new ArrayList<String>(_list29.size);
          for (int _i30 = 0; _i30 < _list29.size; ++_i30)
          {
            String _elem31;
            _elem31 = iprot.readString();
            struct.indexContent.add(_elem31);
          }
        }
        struct.setIndexContentIsSet(true);
      }
    }
  }

}
