package com.performance.config;

import com.performance.common.util.AesEncryptUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

/**
 * MyBatis类型处理器：BigDecimal字段AES加密存储/解密读取
 * 用于薪酬等敏感字段的加密存储，满足金融合规性要求
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(BigDecimal.class)
public class EncryptedBigDecimalTypeHandler extends BaseTypeHandler<BigDecimal> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BigDecimal parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, AesEncryptUtil.encryptWithPrefix(parameter.toPlainString()));
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decryptToBigDecimal(value);
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decryptToBigDecimal(value);
    }

    @Override
    public BigDecimal getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decryptToBigDecimal(value);
    }

    private BigDecimal decryptToBigDecimal(String value) {
        if (value == null || value.isEmpty()) return null;
        String decrypted = AesEncryptUtil.decryptWithPrefix(value);
        return new BigDecimal(decrypted);
    }
}
