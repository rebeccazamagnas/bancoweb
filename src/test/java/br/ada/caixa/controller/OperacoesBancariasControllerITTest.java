package br.ada.caixa.controller;

import br.ada.caixa.dto.request.DepositoRequestDto;
import br.ada.caixa.dto.request.InvestimentoRequestDto;
import br.ada.caixa.dto.request.SaqueRequestDto;
import br.ada.caixa.dto.request.TransferenciaRequestDto;
import br.ada.caixa.dto.response.*;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.enums.StatusCliente;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import br.ada.caixa.service.conta.ContaService;
import br.ada.caixa.service.operacoesbancarias.deposito.DepositoService;
import br.ada.caixa.service.operacoesbancarias.investimento.InvestimentoService;
import br.ada.caixa.service.operacoesbancarias.saldo.SaldoService;
import br.ada.caixa.service.operacoesbancarias.saque.SaqueService;
import br.ada.caixa.service.operacoesbancarias.transferencia.TransferenciaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class OperacoesBancariasControllerITTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @SpyBean
    private ContaRepository contaRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    DepositoService depositoService;
    @Autowired
    SaqueService saqueService;
    @Autowired
    TransferenciaService transferenciaService;
    @Autowired
    SaldoService saldoService;
    @Autowired
    InvestimentoService investimentoService;
    @Autowired
    ContaService contaService;

    private String url;

    @BeforeEach
    void setUp() {
        //SET URL
        url = "http://localhost:" + port + "/operacoes";

        //CRIAR CLIENTES
        var cliente1 = Cliente.builder()
                .documento("123456889")
                .nome("Teste 1")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();
        var cliente2 = Cliente.builder()
                .documento("1234567891")
                .nome("Teste 2")
                .dataNascimento(LocalDate.now())
                .status(StatusCliente.ATIVO)
                .tipo(TipoCliente.PF)
                .createdAt(LocalDate.now())
                .build();

        clienteRepository.saveAllAndFlush(List.of(cliente1, cliente2));

        //CRIAR CONTAS
        var contaCorrente1 = Conta.builder()
                .numero(1L)
                .saldo(BigDecimal.valueOf(100))
                .tipo(TipoConta.CONTA_CORRENTE)
//                .cliente(clienteRepository.findByDocumento(cliente1.getDocumento()).get())
                .cliente(cliente1)
                .createdAt(LocalDate.now())
                .build();

        var contaCorrente2 = Conta.builder()
                .numero(2L)
                .saldo(BigDecimal.valueOf(100))
                .tipo(TipoConta.CONTA_CORRENTE)
//                .cliente(clienteRepository.findByDocumento(cliente2.getDocumento()).get())
                .cliente(cliente2)
                .createdAt(LocalDate.now())
                .build();

        contaRepository.saveAllAndFlush(List.of(contaCorrente1, contaCorrente2));

    }

    @AfterEach
    void tearDown() {
        contaRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
    }

    @Test
    void depositarTest() {
        //given
        final var valor = BigDecimal.valueOf(100.50);
        final var numeroConta = 1L;
        DepositoRequestDto depositoRequestDto =
                DepositoRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/depositar", depositoRequestDto, Void.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        Mockito.verify(contaRepository).save(any(Conta.class));
    }
    @Test
    void sacarTest() {
        //given
        final var valor = BigDecimal.valueOf(50.00);
        final var numeroConta = 1L;
        SaqueRequestDto saqueRequestDto =
                SaqueRequestDto.builder()
                        .numeroConta(numeroConta)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/sacar", saqueRequestDto, Void.class);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(BigDecimal.ZERO.compareTo(contaRepository.findByNumero(numeroConta).get().getSaldo().subtract(valor))).isZero();
        Mockito.verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void transferirTest() {
        //given
        final var valor = BigDecimal.valueOf(50.00);
        final var numeroContaOrigem = 1L;
        final var numeroContaDestino = 2L;

        TransferenciaRequestDto transferenciaRequestDto =
                TransferenciaRequestDto.builder()
                        .numeroContaOrigem(numeroContaOrigem)
                        .numeroContaDestino(numeroContaDestino)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/transferir", transferenciaRequestDto, Void.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(BigDecimal.valueOf(100).compareTo(contaRepository.findByNumero(numeroContaOrigem).get().getSaldo().add(valor))).isZero();
        assertThat(BigDecimal.valueOf(100).compareTo(contaRepository.findByNumero(numeroContaDestino).get().getSaldo().subtract(valor))).isZero();

        Mockito.verify(contaRepository, Mockito.times(2)).save(any(Conta.class));
    }




    @Test
    void consultarSaldoTest() {
        //given
        final var numeroConta = 1L;
        //when
        var response = restTemplate.getForEntity(url + "/consultarSaldo/" + numeroConta, BigDecimal.class);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(BigDecimal.ZERO.compareTo(response.getBody().subtract(contaRepository.findByNumero(numeroConta).get().getSaldo()))).isZero();
    }

    @Test
    void investirTest() {
        //given
        final var valor = BigDecimal.valueOf(500.00);
        final var documentoCliente = "12345678901";
        InvestimentoRequestDto investimentoRequestDto =
                InvestimentoRequestDto.builder()
                        .documentoCliente(documentoCliente)
                        .valor(valor)
                        .build();
        //when
        var response = restTemplate.postForEntity(url + "/investimento", investimentoRequestDto, SaldoResponseDto.class);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var saldoResponseDto = response.getBody();
        assertThat(saldoResponseDto).isNotNull();
        assertThat(BigDecimal.ZERO.compareTo(saldoResponseDto.getSaldo().subtract(valor))).isZero();
    }


}