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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Estado;
import domain.model.Restaurante;
import domain.repository.EstadoRepository;
import domain.service.CadastroEstadoService;

@RestController //indica controler do rest
@RequestMapping(value = "/estados") //indica URI deste recurso
public class EstadoControler {

	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private CadastroEstadoService cadastroEstado;
	
	//Obter recursos
	//Comando GET
	@GetMapping
	public List<Estado> listar() {
		return estadoRepository.findAll();
	}

	//Singleton -> GET /URI/{resourceId}
	@GetMapping("/{estadoId}") // -> /estadoss/estadoId 
	public ResponseEntity<Estado> buscar(@PathVariable("estadoId") Long Id) {
		Optional<Estado> estado = estadoRepository.findById(Id);
		
		if(estado.isPresent()) {
			return ResponseEntity.ok(estado.get());
		}
		
		return ResponseEntity.notFound().build();
	}
	
	//Adicionar recursos
	//Comando POST
	@PostMapping 
	@ResponseStatus(HttpStatus.CREATED) 
	public Estado adicionar(@RequestBody Estado estado) { 
		return cadastroEstado.salvar(estado);
	}
	

	//Atualização de recursos
	//Comando PUT	
	@PutMapping("/{estadoId}") //Define o endereeço da requisição PUT
	public ResponseEntity<?> atualizar(@PathVariable("estadoId") Long Id, @RequestBody Estado estado) {
		try {
			Optional<Estado> estadoAtual = estadoRepository.findById(Id);
	
			if(estadoAtual.isPresent()) {
				BeanUtils.copyProperties(estado, estadoAtual.get(), "id");//para realizar o processo com todos os elemetos
				Estado estadoSalvo = cadastroEstado.salvar(estadoAtual.get());
				
				return ResponseEntity.ok(estadoSalvo);
			}
			
			return ResponseEntity.notFound().build();
		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} 
		
	}
	
	//Exclusão de recurso
	//Comando DELETE
	@DeleteMapping("/{estadoId}") 
	public ResponseEntity<Restaurante> remover(@PathVariable("estadoId") Long Id) {
		try {
			
			cadastroEstado.excluir(Id); 
			return ResponseEntity.noContent().build();//Idica que está vazio

		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.notFound().build();
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
	}
		

}

