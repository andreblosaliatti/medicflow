package com.inflowia.medicflow.controllers;

import com.inflowia.medicflow.dto.exame.ExameBaseDTO;
import com.inflowia.medicflow.dto.exame.ExameBaseDetailsDTO;
import com.inflowia.medicflow.dto.exame.ExameBaseMinDTO;
import com.inflowia.medicflow.dto.exame.ExameBaseUpdateDTO;
import com.inflowia.medicflow.services.exame.ExameBaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/exames-base")
public class ExameBaseController {

    @Autowired
    private ExameBaseService service;

    // LISTAGEM COM FILTRO OPCIONAL POR NOME
    @GetMapping
    public ResponseEntity<Page<ExameBaseMinDTO>> findAll(
            @RequestParam(value = "nome", required = false) String nome,
            Pageable pageable) {

        Page<ExameBaseMinDTO> page = service.findAll(nome, pageable);
        return ResponseEntity.ok(page);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ExameBaseDetailsDTO> findById(@PathVariable Long id) {
        ExameBaseDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    // CRIAR
    @PostMapping
    public ResponseEntity<ExameBaseDetailsDTO> create(@Valid @RequestBody ExameBaseDTO dto) {
        ExameBaseDetailsDTO created = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    // ATUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<ExameBaseDetailsDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ExameBaseUpdateDTO dto) {

        ExameBaseDetailsDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
