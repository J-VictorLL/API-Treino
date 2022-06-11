package infrastructure.repository;

import static infrastructure.repository.spec.RestauranteSpecs.comFreteGratis;
import static infrastructure.repository.spec.RestauranteSpecs.comNomeSemelhante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import domain.model.Restaurante;
import domain.repository.RestauranteRepository;
import domain.repository.RestauranteRepositoryQueries;

//Repositorio customizado do com JpaRepository
@Repository
public class RestauranteRepositoryImpl implements RestauranteRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired @Lazy //Sem o @Lazy o código fica em dependencia circular, o @Lazy só instancia quando precisar, removendo a dependencia circular
	private RestauranteRepository restayranteRepository;
	
	public List<Restaurante> find(String nome, BigDecimal taxaFreteInicial,
			BigDecimal taxaFreteFinal){
		//Criteria API -> ferramenta para geração de querys dinâmicas -> muito burrocratica
		//Usada para consultas mais complexas
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Restaurante> criteria = builder.createQuery(Restaurante.class);
		Root<Restaurante> root = criteria.from(Restaurante.class);//from Restaurante
		
		
	/* Modo estatico
		//determina as condições do predicado -> feito estaticamente
		//predicado like nome
		Predicate nomePredicate = builder.like(root.get("nome"), "%" + nome + "%");
		//predicado maior que taxa
		Predicate taxaInicialPredicate = builder
				.greaterThanOrEqualTo(root.get("taxaFrete"), taxaFreteInicial);
		//predicado menor que taxa
		Predicate taxaFinalPredicate = builder
				.lessThanOrEqualTo(root.get("taxaFrete"), taxaFreteFinal);
	*/
		
	//modo dinamico
		
		var predicate = new ArrayList<Predicate>();
		
		if(StringUtils.hasText(nome)) {
			predicate.add(builder.like(root.get("nome"), "%" + nome + "%"));
		}
		if(taxaFreteInicial != null) {
			predicate.add(builder.greaterThanOrEqualTo(root.get("taxaFrete"), taxaFreteInicial));
		}
		if(taxaFreteFinal != null) {
			predicate.add(builder.lessThanOrEqualTo(root.get("taxaFrete"), taxaFreteFinal));
		}
		
		criteria.where(predicate.toArray(new Predicate[0]));//converte ArrayList para Array
		// realiza a filtragem where de acordo com o que esta no predicado
		
		TypedQuery<Restaurante> query = manager.createQuery(criteria);
		return query.getResultList();
		
		
		
		
		
		
		//Codigo antigo -> para estudo
		/*
		//Implementação estatica
		//var jpql = "from Restaurante where nome like :nome"
		//		+"and taxaFrete between :taxaInical and :taxaFinal";
		
		//Implementação dinâmica
			var jpql = new StringBuilder();
			jpql.append("from Restaurante where 0 = 0 ");
			
			var parametros = new HashMap<String, Object>();
			//implementação considerando que os campos podem receber null
			if(StringUtils.hasLength(nome)) {
				jpql.append("and nome like :nome ");
				parametros.put("nome", "%" + nome + "%");
			}
			if(taxaFreteInicial != null) {
				jpql.append("and taxaFrete >= :taxaInicial ");
				parametros.put("taxaInicial", taxaFreteInicial);
			}
			if(taxaFreteFinal != null) {
				jpql.append("and taxaFrete <= :taxaFinal ");
				parametros.put("taxaFinal", taxaFreteFinal);
			}
			
			//implementação dinâmica
			TypedQuery<Restaurante> query = manager.createQuery(jpql.toString(), Restaurante.class);
			
			parametros.forEach((chave, valor) -> query.setParameter(chave, valor));
				
			return query.getResultList();
				//Implementação antiga, qaundo era estático
				/*
				 * 
			return manager.createQuery(jpql.toString(), Restaurante.class)
					.setParameter("nome", "%" + nome + "%")
					.setParameter("taxaInicial", taxaFreteInicial)
					.setParameter("taxaFinal", taxaFreteFinal)
					.getResultList();
				*/
	}

	@Override
	public List<Restaurante> findComFreteGratis(String nome) {
		return restayranteRepository.findAll(comFreteGratis()
				.and(comNomeSemelhante(nome)));
	
	}
	
	
	
	

}
