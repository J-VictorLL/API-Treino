package domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import domain.execption.EntidadeEmUsoExeption;
import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Estado;
import domain.repository.EstadoRepository;

@Service
public class CadastroEstadoService {

	@Autowired 
	private EstadoRepository estadoRepository;
	
	//Componente sem FK
	public Estado salvar(Estado estado) { //neste exemplo servira tanto para criar com para atualizar
		return estadoRepository.save(estado);
	}
	
	public void excluir(Long Id){ 
		try {
			estadoRepository.deleteById(Id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaExeption(
					String.format("Não existe cadastro do estado de código %d", Id));//Eception de negocio
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Estado de código %d não pode ser removida por estar em uso", Id));//Eception de negocio
		}
	}

	
}
