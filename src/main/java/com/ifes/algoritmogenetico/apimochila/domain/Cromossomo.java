package com.ifes.algoritmogenetico.apimochila.domain;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Cromossomo implements Serializable {

    private List<Long> genes = new ArrayList<Long>(Collections.nCopies(500, 0L));
}
