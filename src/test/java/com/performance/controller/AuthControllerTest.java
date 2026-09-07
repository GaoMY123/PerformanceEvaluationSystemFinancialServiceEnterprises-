package com.performance.controller;

import com.performance.common.exception.BusinessException;
import com.performance.common.exception.GlobalExceptionHandler;
import com.performance.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private SysUserService sysUserService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        sysUserService = mock(SysUserService.class);
        AuthController controller = new AuthController();
        ReflectionTestUtils.setField(controller, "sysUserService", sysUserService);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void loginReturnsTokenEnvelope() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("token", "jwt-token");
        when(sysUserService.login(any())).thenReturn(data);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }

    @Test
    void registerReturnsValidationErrorForBlankUsername() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"secret\",\"realName\":\"Alice\"}"))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void loginMapsBusinessExceptionCode() throws Exception {
        when(sysUserService.login(any())).thenThrow(new BusinessException(401, "bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"wrong\"}"))
                .andExpect(jsonPath("$.code").value(401));
    }
}
