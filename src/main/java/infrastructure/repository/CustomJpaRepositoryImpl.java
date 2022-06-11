package infrastructure.repository;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import domain.repository.CustomJpaRepository;

public class CustomJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID>
	implements CustomJpaRepository<T, ID>{

	// Precisa adiconar na classe Aplication a lina @EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
	// Só então este repositorio customizado vai funcionar
	
	private EntityManager manager;
	
	public CustomJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		
		this.manager = entityManager;
	}

	@Override
	public Optional<T> buscarPrimeiro() {
		var jpql = "from" + getDomainClass().getName();
		
		T entity = manager.createQuery(jpql, getDomainClass()).setMaxResults(1).getSingleResult();
		
		return Optional.ofNullable(entity);
	}

	
}
