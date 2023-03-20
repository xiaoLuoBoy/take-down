package com.horqian.api.config.mp;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日期处理 DateTypeHandler
 *
 * @author bz
 */
@Component
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {

        var date = (Date) rs.getObject(columnName);

        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        }

        return null;
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        var date = (Date) rs.getObject(columnIndex);
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        }

        return null;
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        Date date = cs.getDate(columnIndex);
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        }

        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int parameterIndex, LocalDateTime localDateTime,
                                    JdbcType jdbcType) throws SQLException {

        ps.setObject(parameterIndex, localDateTime);
    }

}