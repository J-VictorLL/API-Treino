package api.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Restaurante;
import domain.repository.RestauranteRepository;
import domain.service.CadastroRestauranteService;

@RestController //indica controler do rest
@RequestMapping(value = "/restaurantes") //indica URI deste recurso
public class RestauranteController {


	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroRestauranteService cadastroRestaurante;
	
	//Obter recursos
	//Comando GET
	@GetMapping
	public List<Restaurante> listar() { // chamado quando se pede um documento Json
		return restauranteRepository.findAll();
	}

	//Singleton -> GET /URI/{resourceId}
	@GetMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> buscar(@PathVariable("restauranteId") Long Id) {
		Optional<Restaurante> restaurante = restauranteRepository.findById(Id);
		
		if(restaurante.isPresent()) {
			return ResponseEntity.ok(restaurante.get());
		}
		
		return ResponseEntity.notFound().build();
	}
	
	
	//Buscar por taxa
	@GetMapping("/por-taxa-frete")
	public List<Restaurante> RestaurantesPorTaxaFrete(BigDecimal taxaInicial, BigDecimal taxaFinal) {
		return restauranteRepository.findByTaxaFreteBetween(taxaInicial, taxaFinal);
	}
	
	@GetMapping("/por-nome-e-frete")
	public List<Restaurante> restaurantesPorNomeFrete(String nome,BigDecimal taxaInicial, BigDecimal taxaFinal) {
		return restauranteRepository.find(nome,taxaInicial, taxaFinal);
	}
	
	@GetMapping("/por-nome-cozinha")
	public List<Restaurante> RestaurantesPorNomeCozinha(String nome, Long id) {
		return restauranteRepository.consultarPorNomeId(nome, id);
	}
	
	@GetMapping("/primeiro-por-nome")
	public Optional<Restaurante> RestaurantesPrimeiroPorNome(String nome) {
		return restauranteRepository.findFristRestauranteByNome(nome);
	}
	
	@GetMapping("/to2-por-nome-cozinha")
	public List<Restaurante> RestaurantesTop2PorNome(String nome) {
		return restauranteRepository.findTop2ByNomeContaining(nome);
	}
	
	@GetMapping("/count-por-cozinha")
	public int restaurantesCountId(Long id) {
		return restauranteRepository.countByCozinhaId(id);
	}
	
	//Por padrçao Spacifications (DDD) com o SDJ
	//É bom pela responsividade dos filtros
	//Porem, as especificações fica nas mão do programador
	//Pode ocorrer de seu uso levar a codigo repetido ou atrapalhar alterações e ajustes
	//depois
	@GetMapping("/com-frete-gratix")
	public List<Restaurante> restaurantesComFreteGratis(String nome) {
		return restauranteRepository.findComFreteGratis(nome);
	}
	
	//Adicionar recursos
	//Comando POST
	@PostMapping 
	public ResponseEntity<?> adicionar(@RequestBody Restaurante restaurante) { 
		try {
			restaurante = cadastroRestaurante.salvar(restaurante);
			return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} 
	}
	
	
	//Teste com comando do repositorio customizado
	@GetMapping("/primeiro")
	public Optional<Restaurante> Primeiro() {
		return restauranteRepository.buscarPrimeiro();
	}
	
	//Atualização de recursos
	//Comando PUT	
	@PutMapping("/{restauranteId}") //Define o endereeço da requisição PUT
	public ResponseEntity<?> atualizar(@PathVariable("restauranteId") Long Id, @RequestBody Restaurante restaurante) {
		try {
			Optional<Restaurante> restauranteAtual = restauranteRepository.findById(Id);
	
			if(restauranteAtual.isPresent()) {
				BeanUtils.copyProperties(restaurante, restauranteAtual.get(), "id");//para realizar o processo com todos os elemetos
				Restaurante restauranteSalvo = cadastroRestaurante.salvar(restauranteAtual.get());
				
				return ResponseEntity.ok(restauranteSalvo);
			}
			
			return ResponseEntity.notFound().build();
		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} 
		
	}
	
	//Atualização parcial de recursos
	//Comando PATCH
	@PatchMapping("/{restauranteId}") 
	public ResponseEntity<?> atualizaParcial(@PathVariable("restauranteId") Long Id, @RequestBody Map<String, Object> campos) {
		Optional<Restaurante> restaurante = restauranteRepository.findById(Id);
	
		if(restaurante.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		merge(campos, restaurante.get());		
		return atualizar(Id,restaurante.get());
	}
	
	
	private void merge(Map<String, Object> camposOrigem, Restaurante restauranteDestino) {
		ObjectMapper objectMapper = new ObjectMapper();
		//Garantir que os valores serão dos mesmos tipos dos existentes na classe 
		Restaurante restauranteOrigem = objectMapper.convertValue(camposOrigem, Restaurante.class);
		
		camposOrigem.forEach((nomePropiedade, valorPropiedade) -> {
			Field field = ReflectionUtils.findField(Restaurante.class, nomePropiedade);//mapeia os campos que receberão os valores
			field.setAccessible(true); //permite acesso a variaveis privadas
			//ValorPropiedade pode conter tipos diferentes, o abaixo contem os corretos
			Object novoValor = ReflectionUtils.getField(field,restauranteOrigem);
			
			ReflectionUtils.setField(field, restauranteDestino, novoValor);//Coloca os valores nos campos
		});
	}
	
	//Exclusão de recurso
	//Comando DELETE
	@DeleteMapping("/{restauranteId}") 
	public ResponseEntity<Restaurante> remover(@PathVariable("restauranteId") Long Id) {
		try {
			
			cadastroRestaurante.excluir(Id); 
			return ResponseEntity.noContent().build();//Idica que está vazio

		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.notFound().build();
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
	}
		
}
