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
@RequestMapping("/api/cromossomo")
@RequiredArgsConstructor
public class CromossomoResource {

    private final CromossomoService cromossomoService;

    @GetMapping("/crossover/baseado-maioria")
    public ResponseEntity<List<Cromossomo>> crossoverBaseadoEmMaioria(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.populacaoCrossoverBaseadoEmMaioria(1000), HttpStatus.OK);
    }

    @GetMapping("/crossover/uniforme")
    public ResponseEntity<List<Cromossomo>> crossoverUniforme(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.crossoverUniforme(1000), HttpStatus.OK);
    }

    @GetMapping("/selecao/torneio")
    public ResponseEntity<List<Cromossomo>> torneio(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.torneio(1000, 2, 7), HttpStatus.OK);
    }

    @GetMapping("/selecao/ranking")
    public ResponseEntity<List<Cromossomo>> ranking(){
        cromossomoService.abasteceBaseDados();
        return new ResponseEntity<>(cromossomoService.ranking(1000, 10), HttpStatus.OK);
    }
}
