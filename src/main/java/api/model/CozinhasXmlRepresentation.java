package api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import domain.model.Cozinha;
import lombok.Data;
import lombok.NonNull;

@JacksonXmlRootElement(localName = "Cozinhas") // altera nome da root da lista
@Data//gerrar gets e sets
public class CozinhasXmlRepresentation {
	
	@JsonProperty("Cozinha") //também afeta o XML
	@JacksonXmlElementWrapper(useWrapping =  false)//remove o embrulho que combre odos os elementos
	@NonNull //Indica que não aeita ser vazio
	private List<Cozinha> cozinhas;
	
	
}
