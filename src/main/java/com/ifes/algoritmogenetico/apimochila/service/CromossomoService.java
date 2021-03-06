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
import java.util.stream.Collectors;
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
        return random.nextInt(100);
    }

    public Cromossomo inicializaCromossomo(){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        Cromossomo cromossomo = new Cromossomo();
        cromossomo.setAvaliacao(0.0);
        Double capacidadeMochila = 0.0;
        int numeroSorteado = 0;

        while(capacidadeMochila <= 12){
            numeroSorteado = random.nextInt(500);
            capacidadeMochila += this.itens.get(numeroSorteado).getPeso();
            if(capacidadeMochila > 12) { break; }
            cromossomo.setAvaliacao(cromossomo.getAvaliacao() + this.itens.get(numeroSorteado).getAvaliacao());
            cromossomo.getGenes().set(numeroSorteado, 1L);
            cromossomo.setPeso(capacidadeMochila);
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

    public List<Cromossomo> ordenaPorPiorAvaliacao(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getAvaliacao));
        return cromossomos;
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
        cromossomo.setAvaliacao(0.0);
        IntStream.range(0,cromossomo.getGenes().size()).forEach(index -> {
            if(cromossomo.getGenes().get(index).equals(1L)){
                cromossomo.setAvaliacao(cromossomo.getAvaliacao() + this.itens.get(index).getAvaliacao());
            }
        });
    }

    public void calculaPesoCromossomo(Cromossomo cromossomo){
        cromossomo.setPeso(0.0);
        IntStream.range(0,cromossomo.getGenes().size()).forEach(index -> {
            if(cromossomo.getGenes().get(index).equals(1L)){
                cromossomo.setPeso(cromossomo.getPeso() + this.itens.get(index).getPeso());
            }
        });
    }

    public void calculaUtilidadeCromossomo(Cromossomo cromossomo){
        cromossomo.setUtilidade(0L);
        IntStream.range(0,cromossomo.getGenes().size()).forEach(index -> {
            if(cromossomo.getGenes().get(index).equals(1L)){
                cromossomo.setUtilidade(cromossomo.getUtilidade() + this.itens.get(index).getUtilidade());
            }
        });
    }

    public void calculaPrecoCromossomo(Cromossomo cromossomo){
        cromossomo.setPreco(0.0);
        IntStream.range(0,cromossomo.getGenes().size()).forEach(index -> {
            if(cromossomo.getGenes().get(index).equals(1L)){
                cromossomo.setPreco(cromossomo.getPreco() + this.itens.get(index).getPreco());
            }
        });
    }

    public void calculaAtributosDaPopulacao(List<Cromossomo> populacao){
        populacao.forEach(cromossomo -> {
            calculaAvaliacaoCromossomo(cromossomo);
            calculaPesoCromossomo(cromossomo);
            calculaPrecoCromossomo(cromossomo);
            calculaUtilidadeCromossomo(cromossomo);
        });
    }

    public List<Cromossomo> crossoverUniforme(List<Cromossomo> pais){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        List<Cromossomo> filhos = new ArrayList<>();

        Cromossomo primeiroPai = pais.get(0);
        Cromossomo segundoPai = pais.get(1);
        Cromossomo filho_1 = new Cromossomo();
        Cromossomo filho_2 = new Cromossomo();
        Cromossomo cromossomoGerador = new Cromossomo();

        cromossomoGerador.getGenes().forEach(gen -> {
            int index = cromossomoGerador.getGenes().indexOf(gen);
            cromossomoGerador.getGenes().set(index, Long.valueOf(random.nextInt(2) + 1));

            if(gen.equals(1)) {
                filho_1.getGenes().set(index, primeiroPai.getGenes().get(index));
                filho_2.getGenes().set(index, segundoPai.getGenes().get(index));
            }else{
                filho_1.getGenes().set(index, segundoPai.getGenes().get(index));
                filho_2.getGenes().set(index, primeiroPai.getGenes().get(index));
            }
        });

        calculaAvaliacaoCromossomo(filho_1);
        calculaPesoCromossomo(filho_1);
        calculaAvaliacaoCromossomo(filho_2);
        calculaPesoCromossomo(filho_2);

        filhos.addAll(Arrays.asList(filho_1,filho_2));

        return filhos;
    }

    public Cromossomo crossoverBaseadoEmMaioria(List<Cromossomo> pais){
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
        calculaPesoCromossomo(filhoGerado);

        return filhoGerado;
    }

    public List<Cromossomo> torneio(List<Cromossomo> populacao, int tamanhoTorneio, int quantidadeIndividuosSelecionados){
        List<Cromossomo> individuosSelecionados = new ArrayList<>();

        IntStream.range(0,quantidadeIndividuosSelecionados).forEach(index -> {
            List<Cromossomo> listaTorneio = selecionaPaisAleatorios(populacao, tamanhoTorneio);
            individuosSelecionados.add(Collections.max(listaTorneio, Comparator.comparing(Cromossomo::getAvaliacao)));
        });

        return individuosSelecionados;
    }

    public Double calculaRanking(Double MIN, Double MAX, Double INDIVIDUOS, int CLASSIFICACAO){
        return MIN + (MAX - MIN) * ( (CLASSIFICACAO - 1) / (INDIVIDUOS - 1));
    }

    public Double sorteiaDouble(Double MIN, Double MAX){
        return MIN + (Double)(Math.random() * (MAX - MIN));
    }

    public List<Cromossomo> ranking(List<Cromossomo> populacao,  int quantidadeIndividuosSelecionados){
        ordenaPorPiorAvaliacao(populacao);

        final Double MIN = 0.9;
        final Double MAX = 1.1;
        final Double INDIVIDUOS = Double.valueOf(populacao.size());

        List<Double> ranking = new ArrayList<Double>();

        IntStream.range(0,populacao.size()).forEach(indexCalculo -> {
            ranking.add(calculaRanking(MIN,MAX,INDIVIDUOS,(indexCalculo + 1)));
        });

        List<Cromossomo> individuosSelecionados = new ArrayList<>();

        IntStream.range(0,quantidadeIndividuosSelecionados).forEach(index -> {
            Double numeroSorteado = sorteiaDouble(MIN, MAX);
            int indexIndividuoEscolhido = ranking.indexOf(ranking.stream().filter(numero -> numero >= numeroSorteado).findFirst().get());
            individuosSelecionados.add(populacao.get(indexIndividuoEscolhido));
        });

        return individuosSelecionados;
    }

    public List<Cromossomo> miLambda(List<Cromossomo> populacao, int tamanhoPopulacaoInicial){
        ordenaPorMelhorAvaliacao(populacao);
        return populacao.stream().limit(tamanhoPopulacaoInicial).collect(Collectors.toCollection(ArrayList::new));
    }

    public void mutacao(Cromossomo cromossomo){
        IntStream.range(0,cromossomo.getGenes().size()).forEach(index -> {
            if(sorteaPorcentagem() <= 1){
                if(cromossomo.getGenes().get(index).equals(0L)){
                    cromossomo.getGenes().set(index, 1L);
                }else{
                    cromossomo.getGenes().set(index, 0L);
                }
            }
        });

        calculaPesoCromossomo(cromossomo);
    }

    public List<Cromossomo> evolucaoRankingCrossoverBaseadoMaioriaMiLambda(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = populacao.size();

            while (tamanho < (tamanhoPopulacao * 2)) {
                List<Cromossomo> pais = new ArrayList<>(ranking(populacao, 7));

                if(sorteaPorcentagem() > 20) {
                    Cromossomo filho = crossoverBaseadoEmMaioria(pais);

                    if (sorteaPorcentagem() <= 20) {
                        mutacao(filho);
                    }

                    if (filho.getPeso() > 12) {
                        filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                    }

                    populacao.add(filho);
                }else{
                    ordenaPorMelhorAvaliacao(pais);
                    populacao.add(pais.get(0));
                }

                tamanho = populacao.size();
            }

            ordenaPorMelhorAvaliacao(populacao);
            populacao = new ArrayList<>(miLambda(populacao, tamanhoPopulacao));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }

    public List<Cromossomo> evolucaoTorneioCrossoverBaseadoMaioriaMiLambda(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = populacao.size();

            while (tamanho < (tamanhoPopulacao * 2)) {
                List<Cromossomo> pais = new ArrayList<>(torneio(populacao, 2, 7));

                if(sorteaPorcentagem() > 20) {
                    Cromossomo filho = crossoverBaseadoEmMaioria(pais);

                    if (sorteaPorcentagem() <= 20) {
                        mutacao(filho);
                    }

                    if (filho.getPeso() > 12) {
                        filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                    }

                    populacao.add(filho);
                }else{
                    ordenaPorMelhorAvaliacao(pais);
                    populacao.add(pais.get(0));
                }

                tamanho = populacao.size();
            }

            ordenaPorMelhorAvaliacao(populacao);
            populacao = new ArrayList<>(miLambda(populacao, tamanhoPopulacao));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }

    public List<Cromossomo> evolucaoRankingCrossoverUniformeMiLambda(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = populacao.size();

            while (tamanho < (tamanhoPopulacao * 2)) {
                List<Cromossomo> pais = new ArrayList<>(ranking(populacao, 2));

                if(sorteaPorcentagem() > 20) {
                    List<Cromossomo> filhos = crossoverUniforme(pais);

                    filhos.forEach(filho -> {
                        if (sorteaPorcentagem() <= 20) {
                            mutacao(filho);
                        }

                        if (filho.getPeso() > 12) {
                            filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                        }
                    });

                    populacao.addAll(filhos);
                }else{
                    ordenaPorMelhorAvaliacao(pais);
                    populacao.add(pais.get(0));
                }

                tamanho = populacao.size();
            }

            ordenaPorMelhorAvaliacao(populacao);
            populacao = new ArrayList<>(miLambda(populacao, tamanhoPopulacao));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }

    public List<Cromossomo> evolucaoTorneioCrossoverUniformeMiLambda(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = populacao.size();

            while (tamanho < (tamanhoPopulacao * 2)) {
                List<Cromossomo> pais = new ArrayList<>(torneio(populacao, 2, 2));

                if(sorteaPorcentagem() > 20) {
                    List<Cromossomo> filhos = crossoverUniforme(pais);

                    filhos.forEach(filho -> {
                        if (sorteaPorcentagem() <= 20) {
                            mutacao(filho);
                        }

                        if (filho.getPeso() > 12) {
                            filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                        }
                    });

                    populacao.addAll(filhos);
                }else{
                    ordenaPorMelhorAvaliacao(pais);
                    populacao.add(pais.get(0));
                }

                tamanho = populacao.size();
            }

            ordenaPorMelhorAvaliacao(populacao);
            populacao = new ArrayList<>(miLambda(populacao, tamanhoPopulacao));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }

    public List<Cromossomo> evolucaoRankingCrossoverBaseadoMaioriaElitismo(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        List<Cromossomo> novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = novaPopulacao.size();

            while (tamanho < tamanhoPopulacao) {
                List<Cromossomo> pais = new ArrayList<>(ranking(populacao, 7));

                if (sorteaPorcentagem() > 20) {
                    Cromossomo filho = crossoverBaseadoEmMaioria(pais);

                    if (sorteaPorcentagem() <= 20) {
                        mutacao(filho);
                    }

                    if (filho.getPeso() > 12) {
                        filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                    }

                    novaPopulacao.add(filho);
                } else {
                    ordenaPorMelhorAvaliacao(pais);
                    novaPopulacao.add(pais.get(0));
                }

                tamanho = novaPopulacao.size();
            }

            ordenaPorMelhorAvaliacao(novaPopulacao);
            populacao = new ArrayList<>(novaPopulacao);
            novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }

    public List<Cromossomo> evolucaoTorneioCrossoverBaseadoMaioriaElitismo(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        List<Cromossomo> novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = novaPopulacao.size();

            while (tamanho < tamanhoPopulacao) {
                List<Cromossomo> pais = new ArrayList<>(torneio(populacao, 2, 7));

                if (sorteaPorcentagem() > 20) {
                    Cromossomo filho = crossoverBaseadoEmMaioria(pais);

                    if (sorteaPorcentagem() <= 20) {
                        mutacao(filho);
                    }

                    if (filho.getPeso() > 12) {
                        filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                    }

                    novaPopulacao.add(filho);
                } else {
                    ordenaPorMelhorAvaliacao(pais);
                    novaPopulacao.add(pais.get(0));
                }

                tamanho = novaPopulacao.size();
            }

            ordenaPorMelhorAvaliacao(novaPopulacao);
            populacao = new ArrayList<>(novaPopulacao);
            novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }

    public List<Cromossomo> evolucaoRankingCrossoverUniformeElitismo(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        List<Cromossomo> novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = novaPopulacao.size();

            while (tamanho < tamanhoPopulacao) {
                List<Cromossomo> pais = new ArrayList<>(ranking(populacao, 2));

                if (sorteaPorcentagem() > 20) {
                    List<Cromossomo> filhos = crossoverUniforme(pais);

                    filhos.forEach(filho -> {
                        if (sorteaPorcentagem() <= 20) {
                            mutacao(filho);
                        }

                        if (filho.getPeso() > 12) {
                            filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                        }
                    });

                    novaPopulacao.addAll(filhos);
                } else {
                    ordenaPorMelhorAvaliacao(pais);
                    novaPopulacao.add(pais.get(0));
                }

                tamanho = novaPopulacao.size();
            }

            ordenaPorMelhorAvaliacao(novaPopulacao);
            populacao = new ArrayList<>(novaPopulacao);
            novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }

    public List<Cromossomo> evolucaoTorneioCrossoverUniformeElitismo(int tamanhoPopulacao, int quantidadeEvolucao){
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        List<Cromossomo> novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        int tamanho = 0;

        for (int iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = novaPopulacao.size();

            while (tamanho < tamanhoPopulacao) {
                List<Cromossomo> pais = new ArrayList<>(torneio(populacao, 2, 2));

                if (sorteaPorcentagem() > 20) {
                    List<Cromossomo> filhos = crossoverUniforme(pais);

                    filhos.forEach(filho -> {
                        if (sorteaPorcentagem() <= 20) {
                            mutacao(filho);
                        }

                        if (filho.getPeso() > 12) {
                            filho.setAvaliacao(filho.getAvaliacao() / ((filho.getPeso() - 12) * 90000000000000000L));
                        }
                    });

                    novaPopulacao.addAll(filhos);
                } else {
                    ordenaPorMelhorAvaliacao(pais);
                    novaPopulacao.add(pais.get(0));
                }

                tamanho = novaPopulacao.size();
            }

            ordenaPorMelhorAvaliacao(novaPopulacao);
            populacao = new ArrayList<>(novaPopulacao);
            novaPopulacao = populacao.stream().limit(10).collect(Collectors.toCollection(ArrayList::new));
        }

        calculaAtributosDaPopulacao(populacao);
        return populacao;
    }
}
