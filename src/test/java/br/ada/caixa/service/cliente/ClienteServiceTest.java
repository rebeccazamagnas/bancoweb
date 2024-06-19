package br.ada.caixa.service.cliente;
import br.ada.caixa.dto.request.RegistrarClientePFRequestDto;
import br.ada.caixa.dto.request.RegistrarClientePJRequestDto;
import br.ada.caixa.dto.response.ClienteResponseDto;
import br.ada.caixa.dto.response.RegistrarClienteResponseDto;
import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoCliente;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.enums.StatusCliente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import br.ada.caixa.entity.Cliente;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@ExtendWith(MockitoExtension.class)
    public class ClienteServiceTest {


        @Mock
        private ClienteRepository clienteRepository;

        @Mock
        private ContaRepository contaRepository;

        @Mock
        private ModelMapper modelMapper;

        @Test
        void testRegistrarPF() {
            ClienteService clienteService = new ClienteService(clienteRepository, modelMapper);
            RegistrarClientePFRequestDto requestDto = new RegistrarClientePFRequestDto();
            RegistrarClienteResponseDto responseDto = new RegistrarClienteResponseDto();

            when(modelMapper.map(any(RegistrarClientePFRequestDto.class), eq(Cliente.class))).thenReturn(new Cliente());
            when(clienteRepository.save(any(Cliente.class))).thenReturn(new Cliente());

            RegistrarClienteResponseDto response = clienteService.registrarPF(requestDto);

            assertNotNull(response);
            verify(modelMapper, times(1)).map(any(RegistrarClientePFRequestDto.class), eq(Cliente.class));
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        // ...

        @Test
        void testRegistrarPJ() {
            ClienteService clienteService = new ClienteService(clienteRepository, modelMapper);
            RegistrarClientePJRequestDto requestDto = new RegistrarClientePJRequestDto();
            RegistrarClienteResponseDto responseDto = new RegistrarClienteResponseDto();

            when(modelMapper.map(any(RegistrarClientePJRequestDto.class), eq(Cliente.class))).thenReturn(new Cliente());
            when(clienteRepository.save(any(Cliente.class))).thenReturn(new Cliente());

            RegistrarClienteResponseDto response = clienteService.registrarPJ(requestDto);

            assertNotNull(response);
            verify(modelMapper, times(1)).map(any(RegistrarClientePJRequestDto.class), eq(Cliente.class));
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        void testRegistrar() {
            ClienteService clienteService = new ClienteService(clienteRepository, modelMapper);
            Cliente cliente = new Cliente();

            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

            RegistrarClienteResponseDto response = clienteService.registrar(cliente);

            assertNotNull(response);
            assertEquals(cliente.getDocumento(), response.getDocumento());
            verify(clienteRepository, times(1)).save(cliente);
        }



    @Test
    void testCriarConta() {

        Cliente cliente = new Cliente();
        cliente.setTipo(TipoCliente.PF);
        cliente.setStatus(StatusCliente.ATIVO);


        Conta conta = ClienteService.criarConta(cliente);


        assertNotNull(conta);
        assertEquals(cliente, conta.getCliente());
        assertEquals(BigDecimal.ZERO, conta.getSaldo());
        assertEquals(TipoConta.CONTA_CORRENTE, conta.getTipo());
    }



        @Test
        void testListarTodos() {
            ClienteService clienteService = new ClienteService(clienteRepository, modelMapper);
            Cliente cliente = new Cliente();
            List<Cliente> clientes = Collections.singletonList(cliente);

            when(clienteRepository.findAll()).thenReturn(clientes);
            when(modelMapper.map(any(Cliente.class), eq(ClienteResponseDto.class))).thenReturn(new ClienteResponseDto());

            List<ClienteResponseDto> response = clienteService.listarTodos();

            assertNotNull(response);
            assertEquals(1, response.size());
            verify(clienteRepository, times(1)).findAll();
        }



        // O método criarConta() é privado e não pode ser testado diretamente.
        // Ele será indiretamente testado quando testarmos registrarPF(), registrarPJ(), e registrar() pois estes métodos chamam criarConta()


    }

