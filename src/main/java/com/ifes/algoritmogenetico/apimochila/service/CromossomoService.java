package com.ifes.algoritmogenetico.apimochila.service;

import com.ifes.algoritmogenetico.apimochila.domain.Cromossomo;
import com.ifes.algoritmogenetico.apimochila.domain.Item;
import com.ifes.algoritmogenetico.apimochila.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

        calculaAtributosDoCromossomo(cromossomo);

        return cromossomo;
    }

    public List<Cromossomo> inicializaPopulacao(Integer tamanhoPopulacao){
        List<Cromossomo> populacao = new ArrayList<>(Collections.nCopies(tamanhoPopulacao, null));

        populacao.forEach(cromossomo -> {
            Integer index = populacao.indexOf(cromossomo);
            populacao.set(index, inicializaCromossomo());
        });

        return populacao;
    }

    public List<Cromossomo> ordenaPorMenorDominio(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getDominio));
        return cromossomos;
    }

    public List<Cromossomo> ordenaPorDistanciaAglomeracao(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getDistanciaAglomeracao).reversed());
        return cromossomos;
    }

    public List<Cromossomo> ordenaPorMelhorAvaliacao(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getAvaliacao).reversed());
        return cromossomos;
    }

    public List<Cromossomo> ordenaPorPiorAvaliacao(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getAvaliacao));
        return cromossomos;
    }

    public List<Cromossomo> ordenaPorMelhorUtilidade(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getUtilidade).reversed());
        return cromossomos;
    }

    public List<Cromossomo> ordenaPorMelhorPreco(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getPreco).reversed());
        return cromossomos;
    }

    public List<Cromossomo> ordenaPorMelhorAHP(List<Cromossomo> cromossomos){
        cromossomos.sort(Comparator.comparing(Cromossomo::getPesoComparacaoAHP).reversed());
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

    public void calculaAtributosDoCromossomo(Cromossomo cromossomo){
        calculaAvaliacaoCromossomo(cromossomo);
        calculaPesoCromossomo(cromossomo);
        calculaPrecoCromossomo(cromossomo);
        calculaUtilidadeCromossomo(cromossomo);
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

        calculaAtributosDoCromossomo(filho_1);
        calculaAtributosDoCromossomo(filho_2);

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

        calculaAtributosDoCromossomo(filhoGerado);

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

        calculaAtributosDoCromossomo(cromossomo);
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
            conjuntos.add(new ArrayList<Cromossomo>(Collections.singletonList(cromossomo)));
        } else {
            conjuntos.forEach(conjunto -> {
                Long calculo = calculaDiferencaEntreGenes(conjunto.get(0), cromossomo);

                if(calculo < k){
                    conjunto.add(cromossomo);
                    flag.set(false);
                }
            });

            if(flag.get()){
                conjuntos.add(new ArrayList<Cromossomo>(Collections.singletonList(cromossomo)));
            }
        }
    }

    public boolean verificaVariavelM(List<List<Cromossomo>> convergencia){
        if(convergencia.stream().filter(cromossomo -> cromossomo.size() >= M).count() != 0){
            return true;
        }

        return false;
    }

    @SneakyThrows
    public List<Cromossomo> evolucaoRankingCrossoverBaseadoMaioriaMiLambda(Integer tamanhoPopulacao, Integer quantidadeEvolucao) {
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

                if(conjuntos.size() < Y && verificaVariavelM(conjuntos)){
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

    public void calculaDistanciaAglomeracao(List<Cromossomo> cromossomos){
        Double precoMax = cromossomos.stream().max(Comparator.comparing(Cromossomo::getPreco)).get().getPreco();
        Double precoMin = cromossomos.stream().min(Comparator.comparing(Cromossomo::getPreco)).get().getPreco();
        Long utilidadeMax = cromossomos.stream().max(Comparator.comparing(Cromossomo::getUtilidade)).get().getUtilidade();
        Long utilidadeMin = cromossomos.stream().min(Comparator.comparing(Cromossomo::getUtilidade)).get().getUtilidade();

        IntStream.range(0, cromossomos.size()).forEach(index -> {
            if(index == 0) {
                cromossomos.get(index).setDistanciaAglomeracao(
                        ((cromossomos.get(index + 1).getPreco() - 0) / (precoMax - precoMin)) +
                                ((0 - cromossomos.get(index + 1).getUtilidade()) / (utilidadeMax - utilidadeMin))
                );
            } else if(index == cromossomos.size() - 1) {
                cromossomos.get(index).setDistanciaAglomeracao(
                        ((0 - cromossomos.get(index - 1).getPreco()) / (precoMax - precoMin)) +
                                ((cromossomos.get(index - 1).getUtilidade() - 0) / (utilidadeMax - utilidadeMin))
                );
            } else {
                cromossomos.get(index).setDistanciaAglomeracao(
                        ((cromossomos.get(index + 1).getPreco() - cromossomos.get(index - 1).getPreco()) / (precoMax - precoMin)) +
                                ((cromossomos.get(index - 1).getUtilidade() - cromossomos.get(index + 1).getUtilidade()) / (utilidadeMax - utilidadeMin))
                );
            }
        });
    }

    public List<Cromossomo> grupoPontoCorte(List<Cromossomo> populacao){
        Integer pontoCorte = populacao.get((populacao.size() / 2) - 1).getDominio();

        return populacao.stream().filter(cromossomo -> cromossomo.getDominio().equals(pontoCorte)).collect(Collectors.toList());
    }

    public List<Cromossomo> removeItensComPesoEstourado(List<Cromossomo> populacao){
        return populacao.stream().filter(cromossomo -> cromossomo.getPeso() <= PESO_TOTAL_MOCHILA).collect(Collectors.toCollection(ArrayList::new));
    }

    public void comparaDominancia(Cromossomo cromossomo, List<Cromossomo> populacao){
        populacao.forEach(result -> {
            if(cromossomo.getPreco() < result.getPreco() || cromossomo.getUtilidade() < result.getUtilidade()){
                cromossomo.setDominio(cromossomo.getDominio() + 1);
            }
        });
    }

    public void calculaDominancia(List<Cromossomo> cromossomos){
        List<Cromossomo> populacaoAuxiliar = new ArrayList<>(cromossomos);

        cromossomos.forEach(cromossomo -> {
            cromossomo.setDominio(0);
            comparaDominancia(cromossomo, populacaoAuxiliar);
        });
    }

    public List<Cromossomo> cortePorFrentePareto(List<Cromossomo> populacao, Integer pontoCorte, Integer tamanhoPopulacaoInicial){
        populacao = populacao.stream().limit(tamanhoPopulacaoInicial).collect(Collectors.toList());
        return populacao.stream()
                .filter(cromossomo -> !cromossomo.getDominio().equals(pontoCorte))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Cromossomo metodoDeBorda(List<Cromossomo> populacaoNaoDominados){
        if(populacaoNaoDominados.size() == 1){
            return populacaoNaoDominados.get(0);
        }

        List<Cromossomo> listaOrganizadoraUtilidade = ordenaPorMelhorUtilidade(populacaoNaoDominados);
        List<Cromossomo> listaOrganizadoraPreco = ordenaPorMelhorPreco(populacaoNaoDominados);

        populacaoNaoDominados.forEach(cromossomo -> {
            cromossomo.setDominio(listaOrganizadoraUtilidade.indexOf(cromossomo) + listaOrganizadoraPreco.indexOf(cromossomo));
        });

        ordenaPorMenorDominio(populacaoNaoDominados);

        return populacaoNaoDominados.get(0);
    }

    public List<Long> montaTabelaSaatyUtilidade(List<Cromossomo> populacaoNaoDominados){
        List<Long> tabelaSaatyUtilidade = new ArrayList<Long>();

        Long maiorUtilidade = populacaoNaoDominados.stream().max(Comparator.comparing(Cromossomo::getUtilidade)).get().getUtilidade();
        Long menorUtilidade = populacaoNaoDominados.stream().min(Comparator.comparing(Cromossomo::getUtilidade)).get().getUtilidade();
        Long metricaUtilidade = (maiorUtilidade - menorUtilidade) / 7;
        tabelaSaatyUtilidade.add(menorUtilidade);

        IntStream.range(0, 8).forEach(index -> {
            tabelaSaatyUtilidade.add(tabelaSaatyUtilidade.get(tabelaSaatyUtilidade.size() - 1) + metricaUtilidade);
        });

        return tabelaSaatyUtilidade;
    }

    public List<Double> montaTabelaSaatyPreco(List<Cromossomo> populacaoNaoDominados){
        List<Double> tabelaSaatyPreco = new ArrayList<Double>();

        Double maiorPreco = populacaoNaoDominados.stream().max(Comparator.comparing(Cromossomo::getPreco)).get().getPreco();
        Double menorPreco = populacaoNaoDominados.stream().min(Comparator.comparing(Cromossomo::getPreco)).get().getPreco();
        Double metricaPreco = (maiorPreco - menorPreco) / 7;
        tabelaSaatyPreco.add(menorPreco);

        IntStream.range(0, 8).forEach(index -> {
            tabelaSaatyPreco.add(tabelaSaatyPreco.get(tabelaSaatyPreco.size() - 1) + metricaPreco);
        });

        return tabelaSaatyPreco;
    }

    public Cromossomo metodoAPH(List<Cromossomo> populacaoNaoDominados){
        if(populacaoNaoDominados.size() == 1){
            return populacaoNaoDominados.get(0);
        }

        Double pesoCriterioUtilidade = 0.83;
        Double pesoCriterioPreco = 0.16;

        List<Long> tabelaSaatyUtilidade = montaTabelaSaatyUtilidade(populacaoNaoDominados);
        List<Double> tabelaSaatyPreco = montaTabelaSaatyPreco(populacaoNaoDominados);

        for(int iterator = 0; iterator < populacaoNaoDominados.size(); iterator++) {
            Double avaliacaoUtilidade = 0D;
            Double avaliacaoPreco = 0D;

            for (int index = 0; index < 9; index++) {
                if (populacaoNaoDominados.get(iterator).getUtilidade().equals(tabelaSaatyUtilidade.get(index))) {
                    avaliacaoUtilidade = index * pesoCriterioUtilidade;
                    break;
                } else if (populacaoNaoDominados.get(iterator).getUtilidade() < tabelaSaatyUtilidade.get(index)) {
                    avaliacaoUtilidade = (index - 1) * pesoCriterioUtilidade;
                    break;
                }
            }

            for (int index = 0; index < 9; index++) {
                if (populacaoNaoDominados.get(iterator).getPreco().equals(tabelaSaatyPreco.get(index))) {
                    avaliacaoPreco = index * pesoCriterioPreco;
                    break;
                } else if (populacaoNaoDominados.get(iterator).getPreco() < tabelaSaatyPreco.get(index)) {
                    avaliacaoPreco = (index - 1) * pesoCriterioPreco;
                    break;
                }
            }

            populacaoNaoDominados.get(iterator).setPesoComparacaoAHP(avaliacaoPreco + avaliacaoUtilidade);
        }

        ordenaPorMelhorAHP(populacaoNaoDominados);

        return populacaoNaoDominados.get(0);
    }

    @SneakyThrows
    public Cromossomo evolucaoMultiobjetivoNSGAIIMetodoDeBorda(Integer tamanhoPopulacao, Integer quantidadeEvolucao) {
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

                populacao = removeItensComPesoEstourado(populacao);
                tamanho = populacao.size();
            }

            calculaDominancia(populacao);
            ordenaPorMenorDominio(populacao);
            List<Cromossomo> grupoPontoDeCorte = grupoPontoCorte(populacao);

            if(grupoPontoDeCorte.size() > 1){
                calculaDistanciaAglomeracao(grupoPontoDeCorte);
                ordenaPorDistanciaAglomeracao(grupoPontoDeCorte);
                populacao = cortePorFrentePareto(populacao, grupoPontoDeCorte.stream().findFirst().get().getDominio(), tamanhoPopulacao);
                populacao.addAll(grupoPontoDeCorte.stream().limit(tamanhoPopulacao - populacao.size()).collect(Collectors.toCollection(ArrayList::new)));
            } else {
                populacao = new ArrayList<>(miLambda(populacao, tamanhoPopulacao));
            }
        }

        ordenaPorMenorDominio(populacao);
        Integer melhorNivelDominio = populacao.get(0).getDominio();

        Cromossomo melhorIndividuo = metodoDeBorda(populacao.stream().filter(cromossomo -> cromossomo.getDominio().equals(melhorNivelDominio)).collect(Collectors.toCollection(ArrayList::new)));

        return melhorIndividuo;
    }

    @SneakyThrows
    public Cromossomo evolucaoMultiobjetivoNSGAIIAHP(Integer tamanhoPopulacao, Integer quantidadeEvolucao) {
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

                populacao = removeItensComPesoEstourado(populacao);
                tamanho = populacao.size();
            }

            calculaDominancia(populacao);
            ordenaPorMenorDominio(populacao);
            List<Cromossomo> grupoPontoDeCorte = grupoPontoCorte(populacao);

            if(grupoPontoDeCorte.size() > 1){
                calculaDistanciaAglomeracao(grupoPontoDeCorte);
                ordenaPorDistanciaAglomeracao(grupoPontoDeCorte);
                populacao = cortePorFrentePareto(populacao, grupoPontoDeCorte.stream().findFirst().get().getDominio(), tamanhoPopulacao);
                populacao.addAll(grupoPontoDeCorte.stream().limit(tamanhoPopulacao - populacao.size()).collect(Collectors.toCollection(ArrayList::new)));
            } else {
                populacao = new ArrayList<>(miLambda(populacao, tamanhoPopulacao));
            }
        }

        ordenaPorMenorDominio(populacao);
        Integer melhorNivelDominio = populacao.get(0).getDominio();

        Cromossomo melhorIndividuo = metodoAPH(populacao.stream().filter(cromossomo -> cromossomo.getDominio().equals(melhorNivelDominio)).collect(Collectors.toCollection(ArrayList::new)));

        return melhorIndividuo;
    }
}
