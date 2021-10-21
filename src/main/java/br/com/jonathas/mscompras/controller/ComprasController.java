package br.com.jonathas.mscompras.controller;

import br.com.jonathas.mscompras.config.MqProducer;
import br.com.jonathas.mscompras.model.CompraModel;
import br.com.jonathas.mscompras.model.StatusCompraEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/compras")
public class ComprasController {

    private final MqProducer producer;

    public ComprasController(MqProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<CompraModel> sendToMq(@RequestBody CompraModel compra) {
        try {
            compra.setIdCompra(UUID.randomUUID().toString());
            compra.setData(LocalDate.now());
            compra.setStatusCompraEnum(StatusCompraEnum.PENDENTE);
            log.info("Salvando compra no rabbit: " + compra);
            producer.sendMessage(compra);
        } catch (JsonProcessingException e) {
            log.error("Erro ao salvar no rabbit", e);
            return new ResponseEntity<CompraModel>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<CompraModel>(compra, HttpStatus.OK);
    }
}