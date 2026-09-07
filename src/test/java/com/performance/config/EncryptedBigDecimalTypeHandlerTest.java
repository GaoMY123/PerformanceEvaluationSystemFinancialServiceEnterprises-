package com.performance.config;

import com.performance.common.util.AesEncryptUtil;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptedBigDecimalTypeHandlerTest {

    private final EncryptedBigDecimalTypeHandler handler = new EncryptedBigDecimalTypeHandler();

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private CallableStatement callableStatement;

    @Test
    void setNonNullParameterWritesEnCPrefixedValue() throws Exception {
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        handler.setNonNullParameter(preparedStatement, 1, new BigDecimal("3500.00"), JdbcType.VARCHAR);

        verify(preparedStatement).setString(eq(1), valueCaptor.capture());
        assertTrue(valueCaptor.getValue().startsWith("ENC:"));
        assertEquals("3500.00", AesEncryptUtil.decryptWithPrefix(valueCaptor.getValue()));
    }

    @Test
    void getNullableResultByColumnNameDecryptsValue() throws Exception {
        when(resultSet.getString("base_salary"))
                .thenReturn(AesEncryptUtil.encryptWithPrefix("1234.56"));

        BigDecimal result = handler.getNullableResult(resultSet, "base_salary");

        assertEquals(0, result.compareTo(new BigDecimal("1234.56")));
    }

    @Test
    void getNullableResultReturnsNullForNullAndEmptyValues() throws Exception {
        when(resultSet.getString(1)).thenReturn(null);
        assertNull(handler.getNullableResult(resultSet, 1));

        when(resultSet.getString(1)).thenReturn("");
        assertNull(handler.getNullableResult(resultSet, 1));
    }

    @Test
    void getNullableResultFromCallableStatementDecryptsValue() throws Exception {
        when(callableStatement.getString(1))
                .thenReturn(AesEncryptUtil.encryptWithPrefix("999.99"));

        BigDecimal result = handler.getNullableResult(callableStatement, 1);

        assertEquals(0, result.compareTo(new BigDecimal("999.99")));
    }
}
