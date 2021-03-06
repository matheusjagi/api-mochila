package com.ifes.algoritmogenetico.apimochila.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cromossomo implements Serializable, Cloneable {

    private List<Long> genes = new ArrayList<Long>(Collections.nCopies(500, 0L));

    private Double avaliacao;

    private Double peso;

    private Long utilidade;

    private Double preco;

    @Override
    public Cromossomo clone() {
        return this.clone();
    }
}
