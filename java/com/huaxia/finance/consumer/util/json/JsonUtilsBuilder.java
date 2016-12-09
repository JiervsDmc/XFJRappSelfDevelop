package com.huaxia.finance.consumer.util.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonUtilsBuilder {
	
	private static  ObjectMapper ORI_MAPPER = null;
	
	static {
		ORI_MAPPER = new ObjectMapper();
		ORI_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
		ORI_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
		ORI_MAPPER.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		ORI_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		ORI_MAPPER.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
		
		ORI_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		ORI_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		
		ORI_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
		
		ORI_MAPPER.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
		
		ORI_MAPPER.setDateFormat(new SimpleDateFormat(DateUtils.PATTERN_DATE_DEFAULT));
		
		ORI_MAPPER.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>()
	    {
	      public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2)
	        throws IOException, JsonProcessingException
	      {
	        arg1.writeString("");
	      }
	    });
	}
	
	public ObjectMapper toObjectMapper (
			String dateFormater
			){
		return toObjectMapper(null,
				null,
				null,
				null,
				dateFormater
				);
	}
	
	public ObjectMapper toObjectMapper (){
		return toObjectMapper(null,
				null,
				null,
				null,
				null
				);
	}
	
	public ObjectMapper toObjectMapper(Map<SerializationFeature,Boolean> sFeatures,
			Map<DeserializationFeature,Boolean> dsFeatures,
			Map<JsonParser.Feature,Boolean> jpFeatures,
			Map<JsonGenerator.Feature,Boolean> jgFeatures,
			String dateFormater
			){
		ObjectMapper mapper = ORI_MAPPER.copy();
		if (CommonUtils.isNotEmpty(sFeatures)){
			for (Map.Entry<SerializationFeature,Boolean> sfea : sFeatures.entrySet()){
				mapper.configure(sfea.getKey(), sfea.getValue());
			}
		}
		
		if (CommonUtils.isNotEmpty(dsFeatures)){
			for (Map.Entry<DeserializationFeature,Boolean> dsfea : dsFeatures.entrySet()){
				mapper.configure(dsfea.getKey(), dsfea.getValue());
			}
		}
		
		if (CommonUtils.isNotEmpty(jpFeatures)){
			for (Map.Entry<JsonParser.Feature,Boolean> jpfea : jpFeatures.entrySet()){
				mapper.configure(jpfea.getKey(), jpfea.getValue());
			}
		}
		
		if (CommonUtils.isNotEmpty(jpFeatures)){
			for (Map.Entry<JsonGenerator.Feature,Boolean> jgfea : jgFeatures.entrySet()){
				mapper.configure(jgfea.getKey(), jgfea.getValue());
			}
		}
		
		if (CommonUtils.isNotEmpty(dateFormater)){
			mapper.setDateFormat(new SimpleDateFormat(dateFormater));
		}
		return mapper;
	}
}
