package api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Cidade;
import domain.model.Restaurante;
import domain.repository.CidadeRepository;
import domain.service.CadastroCidadeService;

@RestController //indica controler do rest
@RequestMapping(value = "/cidades") //indica URI deste recurso
public class CidadeController {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private CadastroCidadeService cadastroCidade;
	
	//Obter recursos
	//Comando GET
	@GetMapping
	public List<Cidade> listar() {
		return cidadeRepository.findAll();
	}

	//Singleton -> GET /URI/{resourceId}
	@GetMapping("/{cidadeId}") // -> /estadoss/estadoId 
	public ResponseEntity<Cidade> buscar(@PathVariable("cidadeId") Long Id) {
		Optional<Cidade> cidade = cidadeRepository.findById(Id);
		
		if(cidade.isPresent()) {
			return ResponseEntity.ok(cidade.get());
		}
		
		return ResponseEntity.notFound().build();
	}
	
	//Adicionar recursos
	//Comando POST
	@PostMapping 
	public ResponseEntity<?> adicionar(@RequestBody Cidade cidade) { 
		try {
			cidade = cadastroCidade.salvar(cidade);
			return ResponseEntity.status(HttpStatus.CREATED).body(cidade);
		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} 
	}
	

	//Atualização de recursos
	//Comando PUT	
	@PutMapping("/{cidadeId}") //Define o endereeço da requisição PUT
	public ResponseEntity<?> atualizar(@PathVariable("cidadeId") Long Id, @RequestBody Cidade cidade) {
		try {
			Optional<Cidade> cidadeAtual = cidadeRepository.findById(Id);
	
			if(cidadeAtual.isPresent()) {
				BeanUtils.copyProperties(cidade, cidadeAtual.get(), "id");//para realizar o processo com todos os elemetos
				Cidade cidadeSalva = cadastroCidade.salvar(cidadeAtual.get());
				
				return ResponseEntity.ok(cidadeSalva);
			}
			
			return ResponseEntity.notFound().build();
		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} 
		
	}
	
	//Exclusão de recurso
	//Comando DELETE
	@DeleteMapping("/{cidadeId}") 
	public ResponseEntity<Restaurante> remover(@PathVariable("cidadeId") Long Id) {
		try {
			
			cadastroCidade.excluir(Id); 
			return ResponseEntity.noContent().build();//Idica que está vazio

		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.notFound().build();
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
	}

}
