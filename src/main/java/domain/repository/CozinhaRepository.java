package domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.model.Cozinha;

@Repository
public interface CozinhaRepository extends JpaRepository<Cozinha, Long>{
	//os abaixo foram implementados inicialmente, mas o JPA já os implementa
	//List<Cozinha> listar();
	// precisa usar find, palavras como bucar ou semelhantes podem causar problema
	//Pode-se colocar qualquer coisa entre o find e o By, fora palavras chave
	//Optional gatante o recebimento de apenas um elemento
	//Frist{Classe} faz com que seja salvo o primeiro objeto da classe encontrado
	Optional<Cozinha> findFristCozinhaByNome(String nome);
	//A palavra chave Containing faz com que a busca seja dos que contem o que foi coloca,
	//como os que tem a letra a ou a uma parte Bra, por exemplo
	List<Cozinha> findAllByNomeContaining(String nome);
	//Cozinha buscar(Long id);
	//Cozinha salvar(Cozinha cozinha);
	//void remover(Long Id);
	
	//exist retorna verdadeiro ou falso
	boolean existByNome(String nome);
	
}
