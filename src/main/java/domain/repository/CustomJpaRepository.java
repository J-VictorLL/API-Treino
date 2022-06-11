package domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

//Repositorio Jpa com comando customizados, precisa ser implementado
@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<T, ID> {//<T, ID> tipagem de generico

	Optional<T> buscarPrimeiro();
	
}
