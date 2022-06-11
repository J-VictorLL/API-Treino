package domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import domain.execption.EntidadeEmUsoExeption;
import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Cozinha;
import domain.model.Restaurante;
import domain.repository.CozinhaRepository;
import domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {
	
	@Autowired 
	private RestauranteRepository restauranteRepository;
	
	@Autowired 
	private CozinhaRepository cozinhaRepository;
	
	//Componente com FK
	public Restaurante salvar(Restaurante restaurante) { //neste exemplo servira tanto para criar com para atualizar
		Long Id = restaurante.getCozinha().getId();
		Cozinha cozinha = cozinhaRepository.findById(Id)
				.orElseThrow(() -> new EntidadeNaoEncontradaExeption(
						String.format("Não existe cadastro de cozinha de código %d", Id)));//Eception de negocio );
		
		restaurante.setCozinha(cozinha);
		
		return restauranteRepository.save(restaurante);
	}
	
	public void excluir(Long Id){ 
		try {
			restauranteRepository.deleteById(Id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaExeption(
					String.format("Não existe cadastro do restaurante de código %d", Id));//Eception de negocio
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Restaurante de código %d não pode ser removida por estar em uso", Id));//Eception de negocio
		}
	}

}
