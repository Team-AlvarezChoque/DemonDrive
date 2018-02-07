
package Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class File extends FileSystem{
	
	private String content;
	private String extension;
	private int size;
	
	public File(String id, String fsName, String extension, TypeFS type, String content) {
		super(id, fsName, type);
		this.content = content;
		this.extension = extension;
		this.size = content.getBytes().length;
	}
	
	public String getFileName(){
		return this.getFsName()+"."+this.getExtension();
	}

	@Override
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.size = content.getBytes().length;
	}

	@Override
	public JSONObject getJSON(String parentPath) throws JSONException {
		JSONObject obj = super.getJSON(parentPath);
		
		obj.put("route", obj.get("route")+"."+this.getExtension());
		
		obj.put("content", content);
		JSONObject name = (JSONObject) obj.get("name");
		name.put("extension", extension);
		obj.put("name", name);
		obj.put("size", this.getSize());
		
		return obj;
	}
	
}
