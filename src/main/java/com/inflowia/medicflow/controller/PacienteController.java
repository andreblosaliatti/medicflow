package com.inflowia.medicflow.controller;

import com.inflowia.medicflow.dto.paciente.DadosAtualizacaoPaciente;
import com.inflowia.medicflow.dto.paciente.DadosCadastroPaciente;
import com.inflowia.medicflow.dto.paciente.DadosDetalhamentoPaciente;
import com.inflowia.medicflow.dto.paciente.DadosListagemPaciente;
import com.inflowia.medicflow.entities.paciente.Paciente;
import com.inflowia.medicflow.repositories.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;


/**
 * Controller REST responsável por lidar com operações relacionadas a pacientes.
 * Segue os princípios do SOLID, em especial o de responsabilidade única.
 */
@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados) {
        Paciente novoPaciente = Paciente.builder()
                .primeiroNome(dados.primeiroNome())
                .sobrenome(dados.sobrenome())
                .cpf(dados.cpf())
                .dataNascimento(dados.dataNascimento())
                .telefone(dados.telefone())
                .email(dados.email())
                .endereco(dados.endereco().toEntity())
                .build();

        repository.save(novoPaciente);
        return ResponseEntity.ok("Paciente cadastrado com sucesso!");
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPaciente>> listar(
        @PageableDefault(size = 10, sort = "primeiroNome") Pageable pageable) {
        
        var pages = repository.findAllByAtivoTrue(pageable).map(DadosListagemPaciente::new);
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoPaciente> buscarPorId(@PathVariable Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<String> atualizar(@PathVariable Long id,@RequestBody @Valid DadosAtualizacaoPaciente dados) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        paciente.atualizarInformacoes(dados);
        repository.save(paciente);

        return ResponseEntity.ok("Paciente atualizado com sucesso");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        paciente.setAtivo(false);
        repository.save(paciente);
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        repository.delete(paciente);
        return ResponseEntity.noContent().build();
    }
}

