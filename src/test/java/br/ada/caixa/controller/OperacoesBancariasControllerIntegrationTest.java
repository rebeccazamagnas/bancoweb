package br.ada.caixa.controller;

// Importando libs necessárias
import br.ada.caixa.dto.request.DepositoRequestDto;
import br.ada.caixa.dto.request.*;
import br.ada.caixa.service.operacoesbancarias.deposito.DepositoService;
import br.ada.caixa.service.operacoesbancarias.saque.SaqueService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = OperacoesBancariasController.class)
public class OperacoesBancariasControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepositoService depositoService;

    @MockBean
    private SaqueService saqueService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldDepositWhenPostToDepositar() throws Exception {
        DepositoRequestDto depositoRequestDto = new DepositoRequestDto();
        depositoRequestDto.setNumeroConta(123456L);
        depositoRequestDto.setValor(BigDecimal.valueOf(1000.00));

        doNothing().when(depositoService).depositar(depositoRequestDto.getNumeroConta(), depositoRequestDto.getValor());

        ResultActions result = mockMvc.perform(
                post("/operacoes/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositoRequestDto)));

        result.andExpect(status().isOk());
        verify(depositoService, times(1)).depositar(depositoRequestDto.getNumeroConta(), depositoRequestDto.getValor());
    }

    @Test
    void shouldWithdrawWhenPostToSacar() throws Exception {
        SaqueRequestDto saqueRequestDto = new SaqueRequestDto();
        saqueRequestDto.setNumeroConta(123456L);
        saqueRequestDto.setValor(BigDecimal.valueOf(100.00));

        doNothing().when(saqueService).sacar(saqueRequestDto.getNumeroConta(), saqueRequestDto.getValor());

        ResultActions result = mockMvc.perform(
                post("/operacoes/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saqueRequestDto)));

        result.andExpect(status().isOk());
        verify(saqueService, times(1)).sacar(saqueRequestDto.getNumeroConta(), saqueRequestDto.getValor());
    }
}
