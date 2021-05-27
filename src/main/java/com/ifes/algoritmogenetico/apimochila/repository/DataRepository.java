package com.ifes.algoritmogenetico.apimochila.repository;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DataRepository<E> {
    public List<E> abasteceBaseDados();
}
