package com.ifes.algoritmogenetico.apimochila.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {

    private Double peso;

    private Long utilidade;

    private Double preco;

    private Double avaliacao;
}
