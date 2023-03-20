package com.horqian.api.config.mp;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日期处理 DateTypeHandler
 *
 * @author bz
 */
@Component
public class LocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {

        Date date = (Date) rs.getObject(columnName);
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDate();
        }

        return null;
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        Date date = (Date) rs.getObject(columnIndex);
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();

            return instant.atZone(zoneId).toLocalDate();
        }

        return null;
    }

    @Override
    public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        Date date = cs.getDate(columnIndex);
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();

            return instant.atZone(zoneId).toLocalDate();
        }

        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int parameterIndex, LocalDate localDate, JdbcType jdbcType)
            throws SQLException {

        ps.setObject(parameterIndex, localDate);
    }

}