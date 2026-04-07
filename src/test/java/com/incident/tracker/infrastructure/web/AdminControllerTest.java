package com.incident.tracker.infrastructure.web;

import com.incident.tracker.auth.infrastructure.web.controller.AdminController;
import com.incident.tracker.incident.application.service.DatabaseResetService;
import com.incident.tracker.auth.infrastructure.security.provider.JwtTokenProvider;
import com.incident.tracker.auth.infrastructure.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DatabaseResetService service;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("DELETE /api/admin/reset - Success")
    void shouldResetDatabaseAndReturnOk() throws Exception {
        mockMvc.perform(delete("/api/admin/reset").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Database reset"));

        verify(service, times(1)).resetDatabase();
    }

    @Test
    @DisplayName("DELETE /api/admin/reset - Returns 500 when service throws")
    void shouldReturn500WhenServiceThrows() throws Exception {

        Mockito.doThrow(new RuntimeException("no database connexion")).when(service).resetDatabase();

        mockMvc.perform(delete("/api/admin/reset").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).resetDatabase();
    }
}

