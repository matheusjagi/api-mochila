package com.ifes.algoritmogenetico.apimochila.service;

import com.ifes.algoritmogenetico.apimochila.domain.Cromossomo;
import com.ifes.algoritmogenetico.apimochila.domain.Item;
import com.ifes.algoritmogenetico.apimochila.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class CromossomoService {

    private final ItemRepository itemRepository;

    private List<Item> itens;

    public void abasteceBaseDados(){
        this.itens = itemRepository.abasteceBaseDados();
    }

    public int sorteaPorcentagem(){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        return random.nextInt(100) + 1;
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

    public List<Cromossomo> populacaocrossoverBaseadoEmMaioria(int tamanhoPopulacao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);

        IntStream.range(0,tamanhoPopulacao).forEach(incrementa -> {
            populacao.add(crossoverBaseadoEmMaioria(populacao));
        });

        ordenaPorMelhorAvaliacao(populacao);

        return populacao;
    }

    public Cromossomo crossoverBaseadoEmMaioria(List<Cromossomo> populacao){
        List<Cromossomo> pais = selecionaPaisAleatorios(populacao, 7);

        Long[] cromossomoVerificador = new Long[pais.size()];
        Cromossomo filhoGerado = new Cromossomo();

        IntStream.range(0,pais.get(0).getGenes().size()).forEach( index -> {
            IntStream.range(0, pais.size()).forEach( iterator -> {
                cromossomoVerificador[iterator] = pais.get(iterator).getGenes().get(index);
            });

            Long contUm = Arrays.stream(cromossomoVerificador).filter(gen -> gen.equals(1L)).count();
            Long contZero = Arrays.stream(cromossomoVerificador).filter(gen -> gen.equals(0L)).count();

            Long porcentagemIncidenciaGen_1 = (contUm * 100) / cromossomoVerificador.length;
            Long porcentagemIncidenciaGen_0 = (contZero * 100) / cromossomoVerificador.length;

            int porcentagemSorteada = sorteaPorcentagem();

            if(contUm.equals(Long.valueOf(pais.size())) || contZero.equals(Long.valueOf(pais.size()))){
                if(contZero.equals(0L)) {
                    filhoGerado.getGenes().set(index, 1L);
                }else {
                    filhoGerado.getGenes().set(index, 0L);
                }
            }
            else {
                if (porcentagemIncidenciaGen_1.compareTo(porcentagemIncidenciaGen_0) > 0) {
                    if(porcentagemSorteada > (100 - porcentagemIncidenciaGen_1)) {
                        filhoGerado.getGenes().set(index, 1L);
                    }else {
                        filhoGerado.getGenes().set(index, 0L);
                    }
                } else {
                    if(porcentagemSorteada > (100 - porcentagemIncidenciaGen_0)){
                        filhoGerado.getGenes().set(index, 0L);
                    }else {
                        filhoGerado.getGenes().set(index, 1L);
                    }
                }
            }
        });

        calculaAvaliacaoCromossomo(filhoGerado);

        return filhoGerado;
    }

    public List<Cromossomo> selecionaPaisAleatorios(List<Cromossomo> populacao, int quantidadePais){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        List<Cromossomo> pais = new ArrayList<>();

        IntStream.range(0, quantidadePais).forEach(index -> {
            pais.add(populacao.get(random.nextInt(populacao.size())));
        });

        return pais;
    }

    public void calculaAvaliacaoCromossomo(Cromossomo cromossomo){
       IntStream.range(0,cromossomo.getGenes().size()).forEach(index -> {
           if(cromossomo.getGenes().get(index).equals(1L)){
               cromossomo.setAvaliacao(cromossomo.getAvaliacao() + this.itens.get(index).getAvaliacao());
           }
       });
    }
}
