package br.ada.caixa.service.conta;

import br.ada.caixa.entity.Conta;
import br.ada.caixa.entity.TipoConta;
import br.ada.caixa.exceptions.ValidacaoException;
import br.ada.caixa.respository.ClienteRepository;
import br.ada.caixa.respository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    public Conta abrirContaPoupanca(String cpf) {
        //Regra: cliente PJ nao pode ter conta poupanca
        return clienteRepository.findByDocumento(cpf)
                .map(clientePF -> {
                    var contaPoupanca = new Conta();
                    contaPoupanca.setTipo(TipoConta.CONTA_POUPANCA);
                    contaPoupanca.setCliente(clientePF);
                    contaPoupanca.setSaldo(BigDecimal.ZERO);
                    return contaRepository.save(contaPoupanca);
                })
                .orElseThrow(() -> new ValidacaoException("Cliente nao encontrado com o CPF informado!"));
    }

}
