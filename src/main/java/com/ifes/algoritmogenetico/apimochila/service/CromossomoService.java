package com.ifes.algoritmogenetico.apimochila.service;

import com.ifes.algoritmogenetico.apimochila.domain.Cromossomo;
import com.ifes.algoritmogenetico.apimochila.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class CromossomoService {

    private List<Item> itens = new ArrayList<>();

    public void abasteceBaseDados() {
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
                    this.itens.add(item);
                }
            }
        }catch (IOException error){
            error.printStackTrace();
        }
    }

    public Cromossomo inicializaCromossomo(){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        Cromossomo cromossomo = new Cromossomo();
        Double capacidadeMochila = 0D;
        int numeroSorteado = 0;

        while(capacidadeMochila <= 12){
            numeroSorteado = random.nextInt(500);
            capacidadeMochila += this.itens.get(numeroSorteado).getPeso();
            if(capacidadeMochila > 12) { break; }
            cromossomo.getGenes().set(numeroSorteado, 1L);
        }

        return cromossomo;
    }

    public List<Cromossomo> inicializaPopulacao(int tamanhoPopulacao){
        List<Cromossomo> populacao = new ArrayList<>(Collections.nCopies(tamanhoPopulacao, new Cromossomo()));

        populacao.forEach(cromossomo -> {
            int index = populacao.indexOf(cromossomo);
            populacao.set(index, inicializaCromossomo());
        });

        return populacao;
    }
}
