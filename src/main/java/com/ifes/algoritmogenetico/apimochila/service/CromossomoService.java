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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class CromossomoService {

    private final ItemRepository itemRepository;

    private List<Item> itens;

    private List<List<Cromossomo>> conjuntos = new ArrayList<>();

    static final Integer k = 4;
    static final Integer Y = 9;
    static final Integer M = 700;

    static final Integer PESO_TOTAL_MOCHILA = 12;

    public void abasteceBaseDados(){
        this.itens = itemRepository.abasteceBaseDados();
    }

    public Integer sorteaPorcentagem(){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        return random.nextInt(100);
    }

    public Cromossomo inicializaCromossomo(){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        Cromossomo cromossomo = new Cromossomo();
        cromossomo.setAvaliacao(0.0);
        Double capacidadeMochila = 0.0;
        Integer numeroSorteado = 0;

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

    public List<Cromossomo> inicializaPopulacao(Integer tamanhoPopulacao){
        List<Cromossomo> populacao = new ArrayList<>(Collections.nCopies(tamanhoPopulacao, null));

        populacao.forEach(cromossomo -> {
            Integer index = populacao.indexOf(cromossomo);
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

    public List<Cromossomo> selecionaPaisAleatorios(List<Cromossomo> populacao, Integer quantidadePais){
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

    public void calculaItensColocadosNaMochilaDaPopulacao(List<Cromossomo> populacao){
        populacao.forEach(cromossomo -> {
            cromossomo.setQuantidadeItensColocadosMochila(cromossomo.getGenes().stream().filter(gen -> gen.equals(1L)).count());
            populacao.get(0).setItens(IntStream.range(0,500).filter(index -> populacao.get(0).getGenes().get(index).equals(1L)).boxed().collect(Collectors.toList()));
        });
    }

    public List<Cromossomo> crossoverUniforme(List<Cromossomo> pais) throws CloneNotSupportedException {
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        List<Cromossomo> filhos = new ArrayList<>();

        Cromossomo primeiroPai = pais.get(0).clone();
        Cromossomo segundoPai = pais.get(1).clone();
        Cromossomo filho_1 = new Cromossomo();
        Cromossomo filho_2 = new Cromossomo();
        Cromossomo cromossomoGerador = new Cromossomo();

        cromossomoGerador.getGenes().forEach(gen -> {
            Integer index = cromossomoGerador.getGenes().indexOf(gen);
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

            Integer porcentagemSorteada = sorteaPorcentagem();

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

    public List<Cromossomo> torneio(List<Cromossomo> populacao, Integer tamanhoTorneio, Integer quantidadeIndividuosSelecionados){
        List<Cromossomo> individuosSelecionados = new ArrayList<>();

        IntStream.range(0,quantidadeIndividuosSelecionados).forEach(index -> {
            List<Cromossomo> listaTorneio = selecionaPaisAleatorios(populacao, tamanhoTorneio);
            individuosSelecionados.add(Collections.max(listaTorneio, Comparator.comparing(Cromossomo::getAvaliacao)));
        });

        return individuosSelecionados;
    }

    public Double calculaRanking(Double MIN, Double MAX, Double INDIVIDUOS, Integer CLASSIFICACAO){
        return MIN + (MAX - MIN) * ( (CLASSIFICACAO - 1) / (INDIVIDUOS - 1));
    }

    public Double sorteiaDouble(Double MIN, Double MAX){
        return MIN + (Double)(Math.random() * (MAX - MIN));
    }

    public List<Cromossomo> ranking(List<Cromossomo> populacao,  Integer quantidadeIndividuosSelecionados){
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
            Integer indexIndividuoEscolhido = ranking.indexOf(ranking.stream().filter(numero -> numero >= numeroSorteado).findFirst().get());
            individuosSelecionados.add(populacao.get(indexIndividuoEscolhido));
        });

        return individuosSelecionados;
    }

    public List<Cromossomo> miLambda(List<Cromossomo> populacao, Integer tamanhoPopulacaoInicial){
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

        calculaAvaliacaoCromossomo(cromossomo);
        calculaPesoCromossomo(cromossomo);
    }

    public void verificaPunicao(Cromossomo cromossomo){
        if (cromossomo.getPeso() > PESO_TOTAL_MOCHILA) {
            cromossomo.setAvaliacao(cromossomo.getAvaliacao() / ((cromossomo.getPeso() - 12) * 90000000000000000L));
        }
    }

    public Long calculaDiferencaEntreGenes(Cromossomo genotipo, Cromossomo individuo){
        return IntStream.range(0, genotipo.getGenes().size())
                .filter(index -> genotipo.getGenes().get(index) != individuo.getGenes().get(index))
                .count();
    }

    public void calculaConvergenciaGenetica(Cromossomo cromossomo){
        AtomicBoolean flag = new AtomicBoolean(true);

        if(conjuntos.isEmpty()) {
            conjuntos.add(new ArrayList<Cromossomo>(Arrays.asList(cromossomo)));
        } else {
            conjuntos.forEach(conjunto -> {
                Long calculo = calculaDiferencaEntreGenes(conjunto.get(0), cromossomo);

                if(calculo < k){
                    conjunto.add(cromossomo);
                    flag.set(false);
                }
            });

            if(flag.get()){
                conjuntos.add(new ArrayList<Cromossomo>(Arrays.asList(cromossomo)));
            }
        }
    }

    public boolean verificaVariavelM(List<List<Cromossomo>> convergencia){
        if(convergencia.stream().filter(cromossomo -> cromossomo.size() >= M).count() != 0){
            return true;
        }

        return false;
    }

    public List<Cromossomo> evolucaoRankingCrossoverBaseadoMaioriaMiLambda(Integer tamanhoPopulacao, Integer quantidadeEvolucao) throws CloneNotSupportedException {
        List<Cromossomo> populacao = inicializaPopulacao(tamanhoPopulacao);
        Integer tamanho = 0;
        Integer TAXA_CRUZAMENTO = 85;
        Integer TAXA_MUTACAO = 25;

        for (Integer iteracao = 0; iteracao < quantidadeEvolucao; iteracao++) {
            tamanho = populacao.size();

            if(iteracao % 3 == 0 && iteracao != 0) {
                conjuntos = new ArrayList<>();
                List<Cromossomo> populacaoAuxiliar = new ArrayList(populacao);
                populacaoAuxiliar.forEach(cromossomo -> calculaConvergenciaGenetica(cromossomo));

                if(conjuntos.size() < Y && this.verificaVariavelM(conjuntos)){
                    System.out.println("Convergência Genética");
                    TAXA_MUTACAO = 40;
                    Double taxaIncrementoPopulacao = populacao.size() * 0.3;
                    populacao = new ArrayList<>(miLambda(populacao, taxaIncrementoPopulacao.intValue()));
                    populacao.addAll(inicializaPopulacao(tamanhoPopulacao - taxaIncrementoPopulacao.intValue()));
                }
            }

            while (tamanho < (tamanhoPopulacao * 2)) {
                List<Cromossomo> pais = new ArrayList<>(ranking(populacao, 11));

                if(sorteaPorcentagem() <= TAXA_CRUZAMENTO) {
                    Cromossomo filho = crossoverBaseadoEmMaioria(pais);
                    if (sorteaPorcentagem() <= TAXA_MUTACAO) { mutacao(filho); }
                    verificaPunicao(filho);
                    populacao.add(filho);
                } else {
                    ordenaPorMelhorAvaliacao(pais);
                    populacao.add(pais.get(0).clone());
                }

                tamanho = populacao.size();
            }

            ordenaPorMelhorAvaliacao(populacao);
            populacao = new ArrayList<>(miLambda(populacao, tamanhoPopulacao));
        }

        calculaAtributosDaPopulacao(populacao);
        calculaItensColocadosNaMochilaDaPopulacao(populacao);

        return populacao;
    }
}
