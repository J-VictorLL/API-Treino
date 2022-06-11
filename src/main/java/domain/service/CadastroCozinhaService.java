package domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import domain.execption.EntidadeEmUsoExeption;
import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Cozinha;
import domain.repository.CozinhaRepository;

@Service //Camada de dominio de serviços
public class CadastroCozinhaService {

	@Autowired 
	private CozinhaRepository cozinhaRepository;
	//Componente sem FK
	public Cozinha salvar(Cozinha cozinha) { //neste exemplo servira tanto para criar com para atualizar
		return cozinhaRepository.save(cozinha);
	}
	
	public void excluir(Long Id){ 
		try {
			cozinhaRepository.deleteById(Id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaExeption(
					String.format("Não existe cadastro da cozinha de código %d", Id));//Eception de negocio
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Cozinha de código %d não pode ser removida por estar em uso", Id));//Eception de negocio
		}
	}
	
}
