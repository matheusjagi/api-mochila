package com.ifes.algoritmogenetico.apimochila.repository;

import com.ifes.algoritmogenetico.apimochila.domain.Item;
import org.springframework.stereotype.Repository;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DadosRepository {

    public List<Item> abasteceBaseDados() {
        List<Item> itens = new ArrayList<>();
        String urlArquivoCSV = "/home/titostauffer/Área de Trabalho/Trabalho Igor Pulini/itens.csv";
        String csvDivisor = ";";
        String linha = "";
        BufferedReader bufferedReader = null;
        boolean primeiraVez = true;

        try {
            bufferedReader = new BufferedReader(new FileReader(urlArquivoCSV));

            while ((linha = bufferedReader.readLine()) != null) {
                if(primeiraVez){
                    primeiraVez = false;
                }else{
                    String[] divisorLinha = linha.split(csvDivisor);

                    Item item = new Item();
                    item.setPeso(Double.valueOf(divisorLinha[1]));
                    item.setUtilidade(Long.valueOf(divisorLinha[2]));
                    item.setPreco(Double.valueOf(divisorLinha[3]));
                    item.setAvaliacao(item.getUtilidade()/item.getPreco());
                    itens.add(item);
                }
            }

            return itens;
        }catch (IOException error){
            error.printStackTrace();
        }

        return null;
    }
}
