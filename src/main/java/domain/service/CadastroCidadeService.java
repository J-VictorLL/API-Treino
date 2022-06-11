package domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import domain.execption.EntidadeEmUsoExeption;
import domain.execption.EntidadeNaoEncontradaExeption;
import domain.model.Cidade;
import domain.model.Estado;
import domain.repository.CidadeRepository;
import domain.repository.EstadoRepository;

@Service
public class CadastroCidadeService {

	@Autowired 
	private CidadeRepository cidadeRepository;
	
	@Autowired 
	private EstadoRepository estadoRepository;
	
	//Componente com FK
	public Cidade salvar(Cidade cidade) { //neste exemplo servira tanto para criar com para atualizar
		Long Id = cidade.getEstado().getId();	
		Estado estado = estadoRepository.findById(Id)
				.orElseThrow(() -> new EntidadeNaoEncontradaExeption(
						String.format("Não existe cadastro de estado de código %d", Id)));//Eception de negocio
		
		cidade.setEstado(estado);
		
		return cidadeRepository.save(cidade);
	}
	
	public void excluir(Long Id){ 
		try {
			cidadeRepository.deleteById(Id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaExeption(
					String.format("Não existe cadastro do ciade de código %d", Id));//Eception de negocio
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Cidade de código %d não pode ser removida por estar em uso", Id));//Eception de negocio
		}
	}


}
