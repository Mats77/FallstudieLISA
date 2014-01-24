package Spielwiese;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class JsonStringGenerator {

	public Object object;
	public ObjectMapper om;
	public ObjectWriter ow;

	public JsonStringGenerator(Object object) {
		this.object = object;
		generateJson();
	}
	
	private void generateJson(){
		om = new ObjectMapper();
		ow = om.defaultPrettyPrintingWriter();
		
	}

}
