package infrastructure.repository.spec;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import domain.model.Restaurante;

public class RestauranteSpecs {

	//Tem que ir em Window -> preferências -> java -> Editor -> Content Assist -> favoritos
	//e adicinar esta classe como novo tipo
	
	public static Specification<Restaurante> comFreteGratis() {
		return (root, query, builder) -> 
				builder.equal(root.get("taxaFrete"), BigDecimal.ZERO);
	}
	
	public static Specification<Restaurante> comNomeSemelhante(String nome) {
		return (root, query, builder) -> 
				builder.equal(root.get("nome"),  "%" + nome + "%");
	}

}
