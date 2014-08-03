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

public class SPKVRow implements org.apache.thrift.TBase<SPKVRow, SPKVRow._Fields>, java.io.Serializable, Cloneable, Comparable<SPKVRow> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SPKVRow");

  private static final org.apache.thrift.protocol.TField KEY_FIELD_DESC = new org.apache.thrift.protocol.TField("key", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField COLS_FIELD_DESC = new org.apache.thrift.protocol.TField("cols", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField CONTENT_FIELD_DESC = new org.apache.thrift.protocol.TField("content", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField DISTANCE_FIELD_DESC = new org.apache.thrift.protocol.TField("distance", org.apache.thrift.protocol.TType.DOUBLE, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SPKVRowStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SPKVRowTupleSchemeFactory());
  }

  public String key; // required
  public List<Integer> cols; // required
  public List<String> content; // required
  public double distance; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    KEY((short)1, "key"),
    COLS((short)2, "cols"),
    CONTENT((short)3, "content"),
    DISTANCE((short)4, "distance");

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
        case 1: // KEY
          return KEY;
        case 2: // COLS
          return COLS;
        case 3: // CONTENT
          return CONTENT;
        case 4: // DISTANCE
          return DISTANCE;
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
  private static final int __DISTANCE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.KEY, new org.apache.thrift.meta_data.FieldMetaData("key", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.COLS, new org.apache.thrift.meta_data.FieldMetaData("cols", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields.CONTENT, new org.apache.thrift.meta_data.FieldMetaData("content", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.DISTANCE, new org.apache.thrift.meta_data.FieldMetaData("distance", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SPKVRow.class, metaDataMap);
  }

  public SPKVRow() {
  }

  public SPKVRow(
    String key,
    List<Integer> cols,
    List<String> content,
    double distance)
  {
    this();
    this.key = key;
    this.cols = cols;
    this.content = content;
    this.distance = distance;
    setDistanceIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SPKVRow(SPKVRow other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetKey()) {
      this.key = other.key;
    }
    if (other.isSetCols()) {
      List<Integer> __this__cols = new ArrayList<Integer>(other.cols);
      this.cols = __this__cols;
    }
    if (other.isSetContent()) {
      List<String> __this__content = new ArrayList<String>(other.content);
      this.content = __this__content;
    }
    this.distance = other.distance;
  }

  public SPKVRow deepCopy() {
    return new SPKVRow(this);
  }

  @Override
  public void clear() {
    this.key = null;
    this.cols = null;
    this.content = null;
    setDistanceIsSet(false);
    this.distance = 0.0;
  }

  public String getKey() {
    return this.key;
  }

  public SPKVRow setKey(String key) {
    this.key = key;
    return this;
  }

  public void unsetKey() {
    this.key = null;
  }

  /** Returns true if field key is set (has been assigned a value) and false otherwise */
  public boolean isSetKey() {
    return this.key != null;
  }

  public void setKeyIsSet(boolean value) {
    if (!value) {
      this.key = null;
    }
  }

  public int getColsSize() {
    return (this.cols == null) ? 0 : this.cols.size();
  }

  public java.util.Iterator<Integer> getColsIterator() {
    return (this.cols == null) ? null : this.cols.iterator();
  }

  public void addToCols(int elem) {
    if (this.cols == null) {
      this.cols = new ArrayList<Integer>();
    }
    this.cols.add(elem);
  }

  public List<Integer> getCols() {
    return this.cols;
  }

  public SPKVRow setCols(List<Integer> cols) {
    this.cols = cols;
    return this;
  }

  public void unsetCols() {
    this.cols = null;
  }

  /** Returns true if field cols is set (has been assigned a value) and false otherwise */
  public boolean isSetCols() {
    return this.cols != null;
  }

  public void setColsIsSet(boolean value) {
    if (!value) {
      this.cols = null;
    }
  }

  public int getContentSize() {
    return (this.content == null) ? 0 : this.content.size();
  }

  public java.util.Iterator<String> getContentIterator() {
    return (this.content == null) ? null : this.content.iterator();
  }

  public void addToContent(String elem) {
    if (this.content == null) {
      this.content = new ArrayList<String>();
    }
    this.content.add(elem);
  }

  public List<String> getContent() {
    return this.content;
  }

  public SPKVRow setContent(List<String> content) {
    this.content = content;
    return this;
  }

  public void unsetContent() {
    this.content = null;
  }

  /** Returns true if field content is set (has been assigned a value) and false otherwise */
  public boolean isSetContent() {
    return this.content != null;
  }

  public void setContentIsSet(boolean value) {
    if (!value) {
      this.content = null;
    }
  }

  public double getDistance() {
    return this.distance;
  }

  public SPKVRow setDistance(double distance) {
    this.distance = distance;
    setDistanceIsSet(true);
    return this;
  }

  public void unsetDistance() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __DISTANCE_ISSET_ID);
  }

  /** Returns true if field distance is set (has been assigned a value) and false otherwise */
  public boolean isSetDistance() {
    return EncodingUtils.testBit(__isset_bitfield, __DISTANCE_ISSET_ID);
  }

  public void setDistanceIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __DISTANCE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case KEY:
      if (value == null) {
        unsetKey();
      } else {
        setKey((String)value);
      }
      break;

    case COLS:
      if (value == null) {
        unsetCols();
      } else {
        setCols((List<Integer>)value);
      }
      break;

    case CONTENT:
      if (value == null) {
        unsetContent();
      } else {
        setContent((List<String>)value);
      }
      break;

    case DISTANCE:
      if (value == null) {
        unsetDistance();
      } else {
        setDistance((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case KEY:
      return getKey();

    case COLS:
      return getCols();

    case CONTENT:
      return getContent();

    case DISTANCE:
      return Double.valueOf(getDistance());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case KEY:
      return isSetKey();
    case COLS:
      return isSetCols();
    case CONTENT:
      return isSetContent();
    case DISTANCE:
      return isSetDistance();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SPKVRow)
      return this.equals((SPKVRow)that);
    return false;
  }

  public boolean equals(SPKVRow that) {
    if (that == null)
      return false;

    boolean this_present_key = true && this.isSetKey();
    boolean that_present_key = true && that.isSetKey();
    if (this_present_key || that_present_key) {
      if (!(this_present_key && that_present_key))
        return false;
      if (!this.key.equals(that.key))
        return false;
    }

    boolean this_present_cols = true && this.isSetCols();
    boolean that_present_cols = true && that.isSetCols();
    if (this_present_cols || that_present_cols) {
      if (!(this_present_cols && that_present_cols))
        return false;
      if (!this.cols.equals(that.cols))
        return false;
    }

    boolean this_present_content = true && this.isSetContent();
    boolean that_present_content = true && that.isSetContent();
    if (this_present_content || that_present_content) {
      if (!(this_present_content && that_present_content))
        return false;
      if (!this.content.equals(that.content))
        return false;
    }

    boolean this_present_distance = true;
    boolean that_present_distance = true;
    if (this_present_distance || that_present_distance) {
      if (!(this_present_distance && that_present_distance))
        return false;
      if (this.distance != that.distance)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(SPKVRow other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetKey()).compareTo(other.isSetKey());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetKey()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.key, other.key);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCols()).compareTo(other.isSetCols());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCols()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.cols, other.cols);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetContent()).compareTo(other.isSetContent());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetContent()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.content, other.content);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDistance()).compareTo(other.isSetDistance());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDistance()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.distance, other.distance);
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
    StringBuilder sb = new StringBuilder("SPKVRow(");
    boolean first = true;

    sb.append("key:");
    if (this.key == null) {
      sb.append("null");
    } else {
      sb.append(this.key);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("cols:");
    if (this.cols == null) {
      sb.append("null");
    } else {
      sb.append(this.cols);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("content:");
    if (this.content == null) {
      sb.append("null");
    } else {
      sb.append(this.content);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("distance:");
    sb.append(this.distance);
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

  private static class SPKVRowStandardSchemeFactory implements SchemeFactory {
    public SPKVRowStandardScheme getScheme() {
      return new SPKVRowStandardScheme();
    }
  }

  private static class SPKVRowStandardScheme extends StandardScheme<SPKVRow> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SPKVRow struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // KEY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.key = iprot.readString();
              struct.setKeyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // COLS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list48 = iprot.readListBegin();
                struct.cols = new ArrayList<Integer>(_list48.size);
                for (int _i49 = 0; _i49 < _list48.size; ++_i49)
                {
                  int _elem50;
                  _elem50 = iprot.readI32();
                  struct.cols.add(_elem50);
                }
                iprot.readListEnd();
              }
              struct.setColsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // CONTENT
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list51 = iprot.readListBegin();
                struct.content = new ArrayList<String>(_list51.size);
                for (int _i52 = 0; _i52 < _list51.size; ++_i52)
                {
                  String _elem53;
                  _elem53 = iprot.readString();
                  struct.content.add(_elem53);
                }
                iprot.readListEnd();
              }
              struct.setContentIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // DISTANCE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.distance = iprot.readDouble();
              struct.setDistanceIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, SPKVRow struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.key != null) {
        oprot.writeFieldBegin(KEY_FIELD_DESC);
        oprot.writeString(struct.key);
        oprot.writeFieldEnd();
      }
      if (struct.cols != null) {
        oprot.writeFieldBegin(COLS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct.cols.size()));
          for (int _iter54 : struct.cols)
          {
            oprot.writeI32(_iter54);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.content != null) {
        oprot.writeFieldBegin(CONTENT_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.content.size()));
          for (String _iter55 : struct.content)
          {
            oprot.writeString(_iter55);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(DISTANCE_FIELD_DESC);
      oprot.writeDouble(struct.distance);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SPKVRowTupleSchemeFactory implements SchemeFactory {
    public SPKVRowTupleScheme getScheme() {
      return new SPKVRowTupleScheme();
    }
  }

  private static class SPKVRowTupleScheme extends TupleScheme<SPKVRow> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SPKVRow struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetKey()) {
        optionals.set(0);
      }
      if (struct.isSetCols()) {
        optionals.set(1);
      }
      if (struct.isSetContent()) {
        optionals.set(2);
      }
      if (struct.isSetDistance()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetKey()) {
        oprot.writeString(struct.key);
      }
      if (struct.isSetCols()) {
        {
          oprot.writeI32(struct.cols.size());
          for (int _iter56 : struct.cols)
          {
            oprot.writeI32(_iter56);
          }
        }
      }
      if (struct.isSetContent()) {
        {
          oprot.writeI32(struct.content.size());
          for (String _iter57 : struct.content)
          {
            oprot.writeString(_iter57);
          }
        }
      }
      if (struct.isSetDistance()) {
        oprot.writeDouble(struct.distance);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SPKVRow struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.key = iprot.readString();
        struct.setKeyIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list58 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.cols = new ArrayList<Integer>(_list58.size);
          for (int _i59 = 0; _i59 < _list58.size; ++_i59)
          {
            int _elem60;
            _elem60 = iprot.readI32();
            struct.cols.add(_elem60);
          }
        }
        struct.setColsIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list61 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.content = new ArrayList<String>(_list61.size);
          for (int _i62 = 0; _i62 < _list61.size; ++_i62)
          {
            String _elem63;
            _elem63 = iprot.readString();
            struct.content.add(_elem63);
          }
        }
        struct.setContentIsSet(true);
      }
      if (incoming.get(3)) {
        struct.distance = iprot.readDouble();
        struct.setDistanceIsSet(true);
      }
    }
  }

}
