/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.shardingjdbc.jdbc.resultset;

import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractResultSetAdapter;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.resultset.ResultSetUtil;
import org.apache.shardingsphere.underlying.executor.context.ExecutionContext;
import org.apache.shardingsphere.underlying.merge.result.MergedResult;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Result that support sharding.
 */
public final class ShardingResultSet extends AbstractResultSetAdapter {

    private final MergedResult mergeResultSet;

    private final Map<String, Integer> columnLabelAndIndexMap;

    public ShardingResultSet(final List<ResultSet> resultSets, final MergedResult mergeResultSet,
        final Statement statement, final ExecutionContext executionContext) throws SQLException {
        super(resultSets, statement, executionContext);
        this.mergeResultSet = mergeResultSet;
        columnLabelAndIndexMap = createColumnLabelAndIndexMap(resultSets.get(0).getMetaData());
    }

    private Map<String, Integer> createColumnLabelAndIndexMap(final ResultSetMetaData resultSetMetaData)
        throws SQLException {
        Map<String, Integer> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int columnIndex = resultSetMetaData.getColumnCount(); columnIndex > 0; columnIndex--) {
            result.put(resultSetMetaData.getColumnLabel(columnIndex), columnIndex);
        }
        return result;
    }

    @Override
    public boolean next() throws SQLException {
        return mergeResultSet.next();
    }

    @Override
    public boolean wasNull() throws SQLException {
        return mergeResultSet.wasNull();
    }

    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        return (boolean)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, boolean.class), boolean.class);
    }

    @Override
    public boolean getBoolean(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (boolean)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, boolean.class), boolean.class);
    }

    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        return (byte)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, byte.class), byte.class);
    }

    @Override
    public byte getByte(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (byte)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, byte.class), byte.class);
    }

    @Override
    public short getShort(final int columnIndex) throws SQLException {
        return (short)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, short.class), short.class);
    }

    @Override
    public short getShort(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (short)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, short.class), short.class);
    }

    @Override
    public int getInt(final int columnIndex) throws SQLException {
        return (int)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, int.class), int.class);
    }

    @Override
    public int getInt(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (int)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, int.class), int.class);
    }

    @Override
    public long getLong(final int columnIndex) throws SQLException {
        return (long)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, long.class), long.class);
    }

    @Override
    public long getLong(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (long)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, long.class), long.class);
    }

    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        return (float)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, float.class), float.class);
    }

    @Override
    public float getFloat(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (float)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, float.class), float.class);
    }

    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        return (double)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, double.class), double.class);
    }

    @Override
    public double getDouble(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (double)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, double.class), double.class);
    }

    @Override
    public String getString(final int columnIndex) throws SQLException {
        return (String)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, String.class), String.class);
    }

    @Override
    public String getString(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (String)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, String.class), String.class);
    }

    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return (BigDecimal)ResultSetUtil
            .convertValue(mergeResultSet.getValue(columnIndex, BigDecimal.class), BigDecimal.class);
    }

    @Override
    public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (BigDecimal)ResultSetUtil
            .convertValue(mergeResultSet.getValue(columnIndex, BigDecimal.class), BigDecimal.class);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return (BigDecimal)ResultSetUtil
            .convertValue(mergeResultSet.getValue(columnIndex, BigDecimal.class), BigDecimal.class);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (BigDecimal)ResultSetUtil
            .convertValue(mergeResultSet.getValue(columnIndex, BigDecimal.class), BigDecimal.class);
    }

    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        return (byte[])ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, byte[].class), byte[].class);
    }

    @Override
    public byte[] getBytes(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (byte[])ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, byte[].class), byte[].class);
    }

    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        return (Date)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, Date.class), Date.class);
    }

    @Override
    public Date getDate(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Date)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, Date.class), Date.class);
    }

    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return (Date)ResultSetUtil
            .convertValue(mergeResultSet.getCalendarValue(columnIndex, Date.class, cal), Date.class);
    }

    @Override
    public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Date)ResultSetUtil
            .convertValue(mergeResultSet.getCalendarValue(columnIndex, Date.class, cal), Date.class);
    }

    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        return (Time)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, Time.class), Time.class);
    }

    @Override
    public Time getTime(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Time)ResultSetUtil.convertValue(mergeResultSet.getValue(columnIndex, Time.class), Time.class);
    }

    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return (Time)ResultSetUtil
            .convertValue(mergeResultSet.getCalendarValue(columnIndex, Time.class, cal), Time.class);
    }

    @Override
    public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Time)ResultSetUtil
            .convertValue(mergeResultSet.getCalendarValue(columnIndex, Time.class, cal), Time.class);
    }

    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return (Timestamp)ResultSetUtil
            .convertValue(mergeResultSet.getValue(columnIndex, Timestamp.class), Timestamp.class);
    }

    @Override
    public Timestamp getTimestamp(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Timestamp)ResultSetUtil
            .convertValue(mergeResultSet.getValue(columnIndex, Timestamp.class), Timestamp.class);
    }

    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return (Timestamp)ResultSetUtil
            .convertValue(mergeResultSet.getCalendarValue(columnIndex, Timestamp.class, cal), Timestamp.class);
    }

    @Override
    public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Timestamp)ResultSetUtil
            .convertValue(mergeResultSet.getCalendarValue(columnIndex, Timestamp.class, cal), Timestamp.class);
    }

    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return mergeResultSet.getInputStream(columnIndex, "Ascii");
    }

    @Override
    public InputStream getAsciiStream(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return mergeResultSet.getInputStream(columnIndex, "Ascii");
    }

    @SuppressWarnings("deprecation")
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return mergeResultSet.getInputStream(columnIndex, "Unicode");
    }

    @SuppressWarnings("deprecation")
    @Override
    public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return mergeResultSet.getInputStream(columnIndex, "Unicode");
    }

    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return mergeResultSet.getInputStream(columnIndex, "Binary");
    }

    @Override
    public InputStream getBinaryStream(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return mergeResultSet.getInputStream(columnIndex, "Binary");
    }

    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        return (Reader)mergeResultSet.getValue(columnIndex, Reader.class);
    }

    @Override
    public Reader getCharacterStream(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Reader)mergeResultSet.getValue(columnIndex, Reader.class);
    }

    @Override
    public Blob getBlob(final int columnIndex) throws SQLException {
        return (Blob)mergeResultSet.getValue(columnIndex, Blob.class);
    }

    @Override
    public Blob getBlob(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Blob)mergeResultSet.getValue(columnIndex, Blob.class);
    }

    @Override
    public Clob getClob(final int columnIndex) throws SQLException {
        return (Clob)mergeResultSet.getValue(columnIndex, Clob.class);
    }

    @Override
    public Clob getClob(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (Clob)mergeResultSet.getValue(columnIndex, Clob.class);
    }

    @Override
    public URL getURL(final int columnIndex) throws SQLException {
        return (URL)mergeResultSet.getValue(columnIndex, URL.class);
    }

    @Override
    public URL getURL(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (URL)mergeResultSet.getValue(columnIndex, URL.class);
    }

    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return (SQLXML)mergeResultSet.getValue(columnIndex, SQLXML.class);
    }

    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (SQLXML)mergeResultSet.getValue(columnIndex, SQLXML.class);
    }

    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        return mergeResultSet.getValue(columnIndex, Object.class);
    }

    @Override
    public Object getObject(final String columnLabel) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return mergeResultSet.getValue(columnIndex, Object.class);
    }

    /**
     * 源码基础上修改：新增该方法，目的：支持枚举类
     */
    @Override
    public final <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        int columnIndex = columnLabelAndIndexMap.get(columnLabel);
        return (T)mergeResultSet.getValue(columnIndex, type);
    }

}
