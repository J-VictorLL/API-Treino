package domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import domain.model.Restaurante;

@Repository
public interface RestauranteRepository extends CustomJpaRepository<Restaurante, Long>,
						RestauranteRepositoryQueries, JpaSpecificationExecutor<Restaurante> {
/*
	List<Restaurante> listar();
	Restaurante buscar(Long id);
	Restaurante salvar(Restaurante restaurante);
	void remover(Long Id);
	*/
	
	//Busca restaurante com taxa entre uma inicial e uma final
	List<Restaurante> findByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal);
	
	
	//Busca restaurantes que contem certa string no nome e um tipo de cozinha
	//List<Restaurante> findByNomeContainingAndCozinhaId(String nome, Long id);
	//OBS.: find, read, get, query e stream, funinam da mesma forma, podem ser
	//trocadas sem problema
	//Outra forma, caso o não tenha palara reservada ou o nome esteja muito grante e
	//complicado, é possivel usar a anotação @Query para fazer o metodo
	//@Query("from Restaurante where nome like %:nome% and cozinha.id = :id")
	//tambem é possivel externalizar essas conultas Query usando um arquivo xml 
	List<Restaurante> consultarPorNomeId(String nome, Long id);
	
	
	// primeiro
	Optional<Restaurante> findFristRestauranteByNome(String nome);
	
	
	//primeiros dois
	List<Restaurante> findTop2ByNomeContaining(String nome);
	
	//count retorna a contagem
	int countByCozinhaId(Long id);
	
	
	
}
