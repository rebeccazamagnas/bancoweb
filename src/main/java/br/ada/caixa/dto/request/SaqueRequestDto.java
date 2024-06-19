package br.ada.caixa.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaqueRequestDto {

    private Long numeroConta;
    private BigDecimal valor;

    @Override
    public String toString() {
        return "DepositoRequestDto{" +
                "numeroConta='" + numeroConta + '\'' +
                ", valor=" + valor +
                '}';
    }
}
