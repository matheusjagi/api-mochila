package com.ifes.algoritmogenetico.apimochila.resource;

import com.ifes.algoritmogenetico.apimochila.domain.Cromossomo;
import com.ifes.algoritmogenetico.apimochila.service.CromossomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/algoritmo")
@RequiredArgsConstructor
public class CromossomoResource {

    private final CromossomoService cromossomoService;

    @GetMapping("/ranking/crossover-baseado-maioria/mi-lambda")
    public ResponseEntity<List<Cromossomo>> evolucaoRankingCrossoverBaseadoMaioriaMiLambda(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoRankingCrossoverBaseadoMaioriaMiLambda(2000, 1000), HttpStatus.OK);
    }

    @GetMapping("/torneio/crossover-baseado-maioria/mi-lambda")
    public ResponseEntity<List<Cromossomo>> evolucaoTorneioCrossoverBaseadoMaioriaMiLambda(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoTorneioCrossoverBaseadoMaioriaMiLambda(2000, 500), HttpStatus.OK);
    }

    @GetMapping("/ranking/crossover-uniforme/mi-lambda")
    public ResponseEntity<List<Cromossomo>> evolucaoRankingCrossoverUniformeMiLambda(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoRankingCrossoverUniformeMiLambda(2000, 500), HttpStatus.OK);
    }

    @GetMapping("/torneio/crossover-uniforme/mi-lambda")
    public ResponseEntity<List<Cromossomo>> evolucaoTorneioCrossoverUniformeMiLambda(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoTorneioCrossoverUniformeMiLambda(2000, 500), HttpStatus.OK);
    }

    @GetMapping("/ranking/crossover-baseado-maioria/elitismo")
    public ResponseEntity<List<Cromossomo>> evolucaoRankingCrossoverBaseadoMaioriaElitismo(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoRankingCrossoverBaseadoMaioriaElitismo(2000, 500), HttpStatus.OK);
    }

    @GetMapping("/torneio/crossover-baseado-maioria/elitismo")
    public ResponseEntity<List<Cromossomo>> evolucaoTorneioCrossoverBaseadoMaioriaElitismo(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoTorneioCrossoverBaseadoMaioriaElitismo(2000, 500), HttpStatus.OK);
    }

    @GetMapping("/ranking/crossover-uniforme/elitismo")
    public ResponseEntity<List<Cromossomo>> evolucaoRankingCrossoverUniformeElitismo(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoRankingCrossoverUniformeElitismo(2000, 500), HttpStatus.OK);
    }

    @GetMapping("/torneio/crossover-uniforme/elitismo")
    public ResponseEntity<List<Cromossomo>> evolucaoTorneioCrossoverUniformeElitismo(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.evolucaoTorneioCrossoverUniformeElitismo(2000, 500), HttpStatus.OK);
    }
}
