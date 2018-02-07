
package Models;

import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public abstract class FileSystem {
	
	private String id;
	private String fsName;
	private Date createDate;
	private Date modifiedDate;
	private TypeFS type;

	public FileSystem(String id, String fileName, TypeFS type) {
		this.id = id;
		this.fsName = fileName;
		this.type = type;
		this.createDate = new Date();
		this.modifiedDate = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFsName() {
		return fsName;
	}

	public void setFsName(String fsName) {
		this.fsName = fsName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public TypeFS getType() {
		return type;
	}

	public void setType(TypeFS type) {
		this.type = type;
	}
	
	public abstract int getSize();
	
	public JSONObject getJSON(String parentPath) throws JSONException{
		JSONObject obj = new JSONObject();
		
		obj.put("id", id);
		JSONObject name = new JSONObject();
		name.put("fsName", fsName);
		obj.put("name",name);
		obj.put("createDate", String.valueOf(createDate.getTime())); //*
		obj.put("modifiedDate", String.valueOf(modifiedDate.getTime())); //*
		//obj.put("size", size);
		obj.put("route", parentPath+fsName);
		obj.put("type", type.name());
		
		return obj;
	}
	
}
