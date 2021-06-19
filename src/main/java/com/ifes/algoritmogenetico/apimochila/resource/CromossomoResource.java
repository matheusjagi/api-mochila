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
        return new ResponseEntity<>(cromossomoService.evolucaoRankingCrossoverBaseadoMaioriaMiLambda(1000, 300), HttpStatus.OK);
    }
}
