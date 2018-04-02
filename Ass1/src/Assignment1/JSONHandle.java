package Assignment1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONHandle {
	public JSONHandle(){		
	}
	public String toJsonKey(String key, String md5){
		JSONObject jo = new JSONObject();
		jo.put("key", key);		
		jo.put("md5", md5);
		return jo.toJSONString();
	}
	public JSONObject parseJson(String filePath) throws FileNotFoundException, IOException, ParseException{
		return (JSONObject) new JSONParser().parse(new FileReader(filePath));
	}
	public String getKey(JSONObject jo){
		return (String) jo.get("key");
	}
	public String getMd5(JSONObject jo){
		return (String) jo.get("md5");
	}
}
