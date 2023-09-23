package com.piola.PiolaSchool.controller;

import com.piola.PiolaSchool.DAO.IAluno;
import com.piola.PiolaSchool.model.Aluno;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/Aluno")
public class AlunoController {

    @Autowired
    private IAluno dao;

    @Autowired
    private PasswordEncoder encoder;

    public AlunoController(IAluno dao){
        this.dao = dao;
        this.encoder = encoder;
    }


    @GetMapping
    public List<Aluno> AlunoList (){
        return (List<Aluno>)dao.findAll();
    }

    @PostMapping
    public Aluno criarAluno(@Valid @RequestBody Aluno aluno){
        aluno.setSenha(encoder.encode(aluno.getSenha()));
        Aluno alunoCreate = dao.save(aluno);
        return alunoCreate;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidatioExpetion(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        } ) ;
        return errors;
    }

    @DeleteMapping("/{matricula}")
    public Optional<Aluno> deletarAluno(@PathVariable Integer matricula){
        Optional<Aluno> Aluno = dao.findById(matricula);
        dao.deleteById(matricula);
        return Aluno;
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> validarSenha (@RequestParam String nome,
                                                 @RequestParam String senha){
        Optional<Aluno> optionalAluno = dao.findByNome(nome);
        if (optionalAluno.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        Aluno aluno = optionalAluno.get();
        boolean valid = encoder.matches(senha, aluno.getSenha());

        HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(valid);
    }

}