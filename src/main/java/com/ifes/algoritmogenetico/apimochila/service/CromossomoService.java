package com.ifes.algoritmogenetico.apimochila.service;

import com.ifes.algoritmogenetico.apimochila.domain.Cromossomo;
import com.ifes.algoritmogenetico.apimochila.domain.Item;
import com.ifes.algoritmogenetico.apimochila.repository.DadosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class CromossomoService {
    private DadosRepository dadosRepository;
    private List<Item> itens = dadosRepository.abasteceBaseDados();

    public Cromossomo inicializaCromossomo(){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        Cromossomo cromossomo = new Cromossomo();
        Double capacidadeMochila = 0D;
        int numeroSorteado = 0;

        while(capacidadeMochila <= 12){
            numeroSorteado = random.nextInt(500);
            capacidadeMochila += this.itens.get(numeroSorteado).getPeso();
            if(capacidadeMochila > 12) { break; }
            cromossomo.setAvaliacao(cromossomo.getAvaliacao() + this.itens.get(numeroSorteado).getAvaliacao());
            cromossomo.getGenes().set(numeroSorteado, 1L);
        }

        return cromossomo;
    }

    public List<Cromossomo> inicializaPopulacao(int tamanhoPopulacao){
        List<Cromossomo> populacao = new ArrayList<>(Collections.nCopies(tamanhoPopulacao, null));

        populacao.forEach(cromossomo -> {
            int index = populacao.indexOf(cromossomo);
            populacao.set(index, inicializaCromossomo());
        });

        ordenaPorMelhorAvaliacao(populacao);

        return populacao;
    }

    public List<Cromossomo> ordenaPorMelhorAvaliacao(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getAvaliacao).reversed());
        return cromossomos;
    }

    public List<Cromossomo> crossoverUniforme(int tamanhoPopulacao){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        List<Cromossomo> populacaoCrossoverUniforme = inicializaPopulacao(tamanhoPopulacao);

        IntStream.range(0, (tamanhoPopulacao/2)).forEach(iterador -> {
            Cromossomo pai_1 = populacaoCrossoverUniforme.get(random.nextInt(tamanhoPopulacao));
            Cromossomo pai_2 = populacaoCrossoverUniforme.get(random.nextInt(tamanhoPopulacao));
            Cromossomo filho_1 = new Cromossomo();
            Cromossomo filho_2 = new Cromossomo();
            Cromossomo cromossomoGerador = new Cromossomo();

            cromossomoGerador.getGenes().forEach(gen -> {
                int index = cromossomoGerador.getGenes().indexOf(gen);
                cromossomoGerador.getGenes().set(index, Long.valueOf(random.nextInt(2) + 1));

                if(gen.equals(1)) {
                    filho_1.getGenes().set(index, pai_1.getGenes().get(index));
                    filho_2.getGenes().set(index, pai_2.getGenes().get(index));
                }else{
                    filho_1.getGenes().set(index, pai_2.getGenes().get(index));
                    filho_2.getGenes().set(index, pai_1.getGenes().get(index));
                }
            });

            calculaAvaliacaoCromossomo(filho_1);
            calculaAvaliacaoCromossomo(filho_2);

            populacaoCrossoverUniforme.addAll(Arrays.asList(filho_1,filho_2));
        });

        ordenaPorMelhorAvaliacao(populacaoCrossoverUniforme);

        return populacaoCrossoverUniforme;
    }


    public void calculaAvaliacaoCromossomo(Cromossomo cromossomo){
       IntStream.range(0,cromossomo.getGenes().size()).forEach(index -> {
           if(cromossomo.getGenes().get(index).equals(1L)){
               cromossomo.setAvaliacao(cromossomo.getAvaliacao() + this.itens.get(index).getAvaliacao());
           }
       });
    }
}
