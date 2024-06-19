package br.ada.caixa.service.conta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.ada.caixa.entity.Cliente;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.Optional;

public class ContaServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    ContaRepository contaRepository;

    @InjectMocks
    ContaService contaService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void abrirContaPoupancaTest() {
        String cpf = "1234567890";
        Cliente mockClient = new Cliente(); // create your client object here
        Conta mockConta = new Conta(); // create Conta object here

        when(clienteRepository.findByDocumento(anyString())).thenReturn(Optional.of(mockClient));

        mockConta.setCliente(mockClient);
        mockConta.setSaldo(BigDecimal.ZERO);
        mockConta.setTipo(TipoConta.CONTA_POUPANCA);

        when(contaRepository.save(any(Conta.class))).thenReturn(mockConta);

        Conta conta = contaService.abrirContaPoupanca(cpf);

        assertEquals(mockClient, conta.getCliente());
        assertEquals(BigDecimal.ZERO, conta.getSaldo());
        assertEquals(TipoConta.CONTA_POUPANCA, conta.getTipo());
        verify(clienteRepository, times(1)).findByDocumento(anyString());
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    public void abrirContaPoupancaTest_ClienteNotFound() {
        String cpf = "1234567890";

        when(clienteRepository.findByDocumento(anyString())).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> {
            Conta conta = contaService.abrirContaPoupanca(cpf);
        });
        verify(clienteRepository, times(1)).findByDocumento(anyString());
    }
}
