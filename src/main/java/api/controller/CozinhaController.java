package api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import api.model.CozinhasXmlRepresentation;
import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Cozinha;
import domain.repository.CozinhaRepository;
import domain.service.CadastroCozinhaService;


@RestController //indica controler do rest
@RequestMapping(value = "/cozinhas") //indica URI deste recurso
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Autowired
	private CadastroCozinhaService cadastroCozinha;
	
	//Obter recursos
	//Comando GET. Modelo de requisição -> GET /URI HTTP/1.1  ou GET /URI/{resourceId}
	//listagem -> GET /URI HTTP/1.1
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Cozinha> listarJson() { // chamado quando se pede um documento Json
		return cozinhaRepository.findAll();
	}
	
	@GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
	public CozinhasXmlRepresentation listarXml() {// chamado quando se pede um documento Xml
		return new CozinhasXmlRepresentation(cozinhaRepository.findAll());
	}
	//Singleton -> GET /URI/{resourceId}
	@GetMapping("/{cozinhaId}") // -> /cozinhas/cozinhaId -> /cozinhas/1
	public ResponseEntity<Cozinha> buscar(@PathVariable("cozinhaId") Long Id) { // chamado para obeter Singleton Resource 
		//@PathVariable("tag") garante que a variavel seguinte recebera o valor colocado no lugar da tag na requisição
		Optional<Cozinha> cozinha = cozinhaRepository.findById(Id);

		if(cozinha.isPresent()) {
			return ResponseEntity.ok(cozinha.get());
		}
		
		return ResponseEntity.notFound().build();
		
		
	}
	
	//Busca por componenetes
	//  -> /cozinha/por-nome?nome = nome_buscado
	@GetMapping("/por-nome")
	public List<Cozinha> cozinhasPorNome(String nome) { 
		return cozinhaRepository.findAllByNomeContaining(nome);
	}
	
	@GetMapping("/unica-por-nome")
	public Optional<Cozinha> cozinhaPorNome(String nome) {
		return cozinhaRepository.findFristCozinhaByNome(nome);
	}
	
	@GetMapping("/exist")
	public boolean cozinhaExistir(String nome) {
		return cozinhaRepository.existByNome(nome);
	}
	
	//Adicionar recursos
	//Comando POST.
	//Modelo de requisição:
	/*
	POST  /URI HTTP/1.1
	//-Exemplo Geral-Content-Type: application/tipo-arquivo   -> o tipo arquivo pode ser json ou xml ou outro, isso define como devera escrever o corpo
	Content-Type: application/json   -> será usando para exemplificar
	
	{
	 "nome-coluna-propiedade": conteudo
	  ************ repetição do acima até preencher todos os campos fora o ID
	}
	*/
	
	/* Exemplo de só adição
	@PostMapping // define como sendo o chamado pelo POST
	@ResponseStatus(HttpStatus.CREATED) // Retorna o código de status HTTP created
	public void adicionar(@RequestBody Cozinha cozinha) { //@RequestBody faz com que a variavel seja preenchida com o que for colocad no corpo do Request
		cozinhaRepository.salvar(cozinha);
	}
	*/
	//ou
	//exemplo de retorno e adição
	@PostMapping // define como sendo o chamado pelo POST
	@ResponseStatus(HttpStatus.CREATED) // Retorna o código de status HTTP created
	public Cozinha adicionar(@RequestBody Cozinha cozinha) { //@RequestBody faz com que a variavel seja preenchida com o que for colocad no corpo do Request
		return cadastroCozinha.salvar(cozinha);
	}
	
	//Atualização de recursos
	//Comando PUT
	//Modelo de requisição:
		/*
		PUT  /URI HTTP/1.1 // atualiza a lista inteira
		//////////////
		PUT  /URI/{resourceId} HTTP/1.1 // atualiza apenas o element {resourceId}
		//-Exemplo Geral-Content-Type: application/tipo-arquivo   -> o tipo arquivo pode ser json ou xml ou outro, isso define como devera escrever o corpo
		Content-Type: application/json   -> será usando para exemplificar
		
		{
		 "nome-coluna-propiedade": conteudo_atualizado
		  ************ repetição do acima até preencher todos os campos fora o ID
		}
		*/
	
	@PutMapping("/{cozinhaId}") //Define o endereeço da requisição PUT
	public ResponseEntity<Cozinha> atualizar(@PathVariable("cozinhaId") Long Id, @RequestBody Cozinha cozinha) { // chamado para obeter Singleton Resource 
		Optional<Cozinha> cozinhaAtual = cozinhaRepository.findById(Id);
		//cozinhaAtual.setNome(cozinha.getNome());
		//ou
		if(cozinhaAtual.isPresent()) {
			BeanUtils.copyProperties(cozinha, cozinhaAtual.get(), "id");//para realizar o processo com todos os elemetos
			
			Cozinha cozinhaSalva = cadastroCozinha.salvar(cozinhaAtual.get());
			return ResponseEntity.ok(cozinhaSalva);
		}
		
		return ResponseEntity.notFound().build();
		
	}
	
	//Exclusão de recurso
	//Comando DELETE
	//Modelo de requisição:
		/*
		PUT  /URI   // deleta a lista inteira
		//////////////
		PUT  /URI/{resourceId} // deleta apenas o element {resourceId}
		*/
		
	@DeleteMapping("/{cozinhaId}") //Define o endereeço da requisição DELETE
	public ResponseEntity<Cozinha> remover(@PathVariable("cozinhaId") Long Id) { // chamado para obeter Singleton Resource 
		try {//Como cozinha esta vinculada a outras tabelas, ou seja
			//existe uma foreingKey dela em outra tabela, ela não pode
			//ser eliminada de forma simples
			
			cadastroCozinha.excluir(Id); 
			return ResponseEntity.noContent().build();//Idica que está vazio

		} catch (EntidadeNaoEncontradaExeption e) {
			return ResponseEntity.notFound().build();
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
	}
		
	
	
	
	
	//HTTP responses
/*
	// Causa o retorno e manipulação r respostas HTTP, quando se quer usar alguma lógica especifica
	@GetMapping("/{cozinhaId}") // -> /cozinhas/cozinhaId -> /cozinhas/1
	public ResponseEntity<Cozinha> buscarteste(@PathVariable("cozinhaId") Long Id) { // chamado para obeter Singleton Resource 
		Cozinha cozinha = cozinhaRepository.buscar(Id);
		//return ResponseEntity.status(HttpStatus.OK).body(cozinha);
		//ou
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.LOCATION, "Endereço");
		
		return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
		//ou
		//return ResponseEntity.ok(cozinha);
	}
*/
	/*
	// tratamento de resposta Http para caso não exista recurso 
		@GetMapping("/{cozinhaId}") // -> /cozinhas/cozinhaId -> /cozinhas/1
		public ResponseEntity<Cozinha> buscarteste(@PathVariable("cozinhaId") Long Id) { // chamado para obeter Singleton Resource 
			Cozinha cozinha = cozinhaRepository.buscar(Id);

			if(cozinha != null) {
				return ResponseEntity.ok(cozinha);
			}
			
			return ResponseEntity.notFound().build();
		}
	 */
}
