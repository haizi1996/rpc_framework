package framework_rpc.common;

import org.apache.commons.codec.binary.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



public class JsonUtil {

	private static ObjectMapper objMapper = new ObjectMapper();
	private static Base64 base64 = new Base64();
	public static <T> T jsonToObject(String json, Class<T> cls) {
		T t = null;
		try {
			t = objMapper.readValue(json, cls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("解码错误!");
		}
		return t;
	}

	public static String objectToJson(Object obj) {
		String json = "";
		try {
			json = objMapper.writeValueAsString(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	public static byte[] serialize(Object obj){
		try {
			return objMapper.writeValueAsBytes(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("编码成字节数组错误");
			
		}
	}
	
	public static <T> T deserialize(byte[] bytes,Class<T> cls){
		try {
			return objMapper.readValue(bytes, cls);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("解码成对象错误");
		}
		
	}
	
	public static  byte[] decoder(String json){
		return base64.decode(json);
	}
	
	public static  String encoder(byte[] arrays){
		return base64.encodeToString(arrays);
	}
	public static void main(String[] args) {
		RpcRequest request = new RpcRequest();
		request.setClassName("sss");
		
		byte[] bytes = serialize(request);
		
		RpcRequest m =  deserialize(bytes, RpcRequest.class);
		String value = objectToJson(m);
		System.out.println(value);
		System.out.println(m.equals(request));
		
		
	}
}
