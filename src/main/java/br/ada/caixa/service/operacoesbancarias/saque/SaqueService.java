package br.ada.caixa.service.operacoesbancarias.saque;


import br.ada.caixa.entity.Conta;
import br.ada.caixa.exceptions.ContaNaoEncontradaException;
import br.ada.caixa.respository.ContaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaqueService {

    private final ContaRepository contaRepository;
    private final List<OperacaoSaque> operacaoSaqueList;

    public SaqueService(ContaRepository contaRepository, List<OperacaoSaque> operacaoSaqueList) {
        this.contaRepository = contaRepository;
        this.operacaoSaqueList = operacaoSaqueList;
    }

    public void sacar(Long numeroConta, BigDecimal valor) {
        Conta conta = contaRepository.findByNumero(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta com número " + numeroConta + " não encontrada"));

        operacaoSaqueList.forEach(operacaoSaque -> operacaoSaque.executar(conta, valor));
        contaRepository.save(conta);
    }

}
