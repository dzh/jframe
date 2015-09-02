/**
 * 
 */
package jframe.pay.domain.dao.mapper;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * @author dzh
 * @date Jul 20, 2015 3:00:03 PM
 * @since 1.0
 */
public class SqlTimestampTypeHandler extends BaseTypeHandler<Long> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.type.BaseTypeHandler#setNonNullParameter(java.sql.
	 * PreparedStatement, int, java.lang.Object,
	 * org.apache.ibatis.type.JdbcType)
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			Long parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == 0L)
			parameter = new Date().getTime();
		ps.setTimestamp(i, new java.sql.Timestamp(parameter));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.ResultSet
	 * , java.lang.String)
	 */
	@Override
	public Long getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		Timestamp t = rs.getTimestamp(columnName);
		return t == null ? null : t.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.ResultSet
	 * , int)
	 */
	@Override
	public Long getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		Timestamp t = rs.getTimestamp(columnIndex);
		return t == null ? null : t.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ibatis.type.BaseTypeHandler#getNullableResult(java.sql.
	 * CallableStatement, int)
	 */
	@Override
	public Long getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		Timestamp t = cs.getTimestamp(columnIndex);
		return t == null ? null : t.getTime();
	}

}
