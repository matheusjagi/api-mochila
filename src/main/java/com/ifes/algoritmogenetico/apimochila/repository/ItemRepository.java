package com.ifes.algoritmogenetico.apimochila.repository;

import com.ifes.algoritmogenetico.apimochila.domain.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepository implements DataRepository<Item> {
    @Override
    @Autowired
    public List<Item> abasteceBaseDados() {
        List<Item> dados;
        String urlArquivoCSV = "/home/titostauffer/Área de Trabalho/Trabalho Igor Pulini/itens.csv";

        try {
            return dados = Files.lines(Paths.get(urlArquivoCSV), StandardCharsets.ISO_8859_1)
                    .skip(1)
                    .map(line -> line.split(";"))
                    .map(col -> new Item(
                            Double.valueOf(col[1]),
                            Long.valueOf(col[2]),
                            Double.valueOf(col[3]),
                            Double.valueOf(col[2]) / Double.valueOf(col[3])))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
