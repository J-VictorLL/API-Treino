package domain.repository;

import java.math.BigDecimal;
import java.util.List;

import domain.model.Restaurante;

public interface RestauranteRepositoryQueries {

	//O spring percebe que exite uma classe reposirorio de implementação
	//deste código (que obrigatriamente tem que ter o sufixo Impl)
	//e faz a ligação
	List<Restaurante> find(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal);

	List<Restaurante> findComFreteGratis(String nome);
}