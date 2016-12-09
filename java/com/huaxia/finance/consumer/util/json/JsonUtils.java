package com.huaxia.finance.consumer.util.json;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;


public class JsonUtils {
	
	private static ObjectMapper _tool = null;
	private static JsonUtils active;
	private ObjectMapper i_tool = null;
	
	public synchronized static JsonUtils of(){
		if (null == _tool){
			active = new JsonUtils();
		}
		return active;
	}
	
	public synchronized static JsonUtils of(String datePattern){
		return new JsonUtils(datePattern);
	}
	
	private JsonUtils() {
		_tool = new JsonUtilsBuilder().toObjectMapper();
		i_tool = _tool;
	}
	
	private JsonUtils(String datePattern) {
		if (null == i_tool){
			i_tool = new JsonUtilsBuilder().toObjectMapper(datePattern);
		}
	}
	
	public String toJson(Object entity) throws Exception {
		try {
			return this.i_tool.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			throw new Exception("对象转换为Json时出错！！",e);
		}
	}
	
	public void toJson(Writer writer,Object entity) throws Exception {
		try {
			this.i_tool.writeValue(writer, entity);
		} catch (Exception e) {
			throw new Exception("对象转换为Json形式的Writer流时出错！！",e);
		}
	}
	
	public <T> T toObject(String json,Class<T> T) throws Exception {
		try {
			return this.i_tool.readValue(json,T);
		} catch (Exception e) {
			throw new Exception("Json字符串转换为对象时出错！！",e);
		}
	}
	
	public <T> T toObject(Reader reader,Class<T> T) throws Exception {
		try {
			return this.i_tool.readValue(reader,T);
		} catch (Exception e) {
			throw new Exception("通过Reader流将Json转换为对象的时出错！！",e);
		}
	}
	
	public <T> List<T> toList(Reader reader,Class<T> T) throws Exception {
		try {
			TypeReference<List<T>> typeRef = new TypeReference<List<T>>() {};
			return this.i_tool.readValue(reader,typeRef);
		} catch (Exception e) {
			throw new Exception("通过Reader流将Json转换为对象的时出错！！",e);
		}
	}
	
	public JsonNode toNode(String json) throws Exception  {
		try {
			return this.i_tool.readTree(json);
		} catch (Exception e) {
			throw new Exception("通过json字符串将Json转换为JsonNode的时出错！！",e);
		}
	}
	
	public JsonNode toArrayNode(String jsonRoot,String path)  throws Exception  {
		try {
			JsonNode root = toNode(jsonRoot);
			return root.findPath(path);
		} catch (Exception e) {
			throw new Exception("通过json字符串将Json转换为JsonNode的时出错！！",e);
		}
	}
	
	public JsonNode toArrayNode(JsonNode root,String path)  throws Exception  {
		try {
			return root.findPath(path);
		} catch (Exception e) {
			throw new Exception("通过json字符串将Json转换为JsonNode的时出错！！",e);
		}
	}
	
	public JsonNode appendStringNode(JsonNode nodeToAppend, String propName,String propValue) throws Exception  {
		try {
			ObjectNode objNode = (ObjectNode)nodeToAppend;
			TextNode node = objNode.textNode(propValue);
			return objNode.set(propName,node);
		} catch (Exception e) {
			throw new Exception("通过json字符串将Json转换为JsonNode的时出错！！",e);
		}
	}
	
	public ObjectNode createNewObjectNode() throws Exception  {
		try {
			return this.i_tool.createObjectNode();
		} catch (Exception e) {
			throw new Exception("通过json字符串将Json转换为JsonNode的时出错！！",e);
		}
	}
	
	public ArrayNode createArrayNode() throws Exception  {
		try {
			return this.i_tool.createArrayNode();
		} catch (Exception e) {
			throw new Exception("通过json字符串将Json转换为JsonNode的时出错！！",e);
		}
	}
	
	public JsonNode removeNode(JsonNode node, String propName) throws Exception  {
		try {
			ObjectNode objNode = (ObjectNode)node;
			return objNode.remove(propName);
			
		} catch (Exception e) {
			throw new Exception("通过json字符串将Json转换为JsonNode的时出错！！",e);
		}
	}
	
	public static void main(String[] args) {
		
	}
}
