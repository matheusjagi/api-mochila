package com.ifes.algoritmogenetico.apimochila.domain;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class Item implements Serializable {

    private Double peso;

    private Long utilidade;

    private Double preco;

    private Double avaliacao;
}
