
package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class Link extends FileSystem{
	
	// Shared
	private String owner;
	private String pathLink;

	public Link(String id, String fileName, TypeFS type, String owner, String pathLink) {
		super(id, fileName, type);
		this.owner = owner;
		this.pathLink = pathLink;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPathLink() {
		return pathLink;
	}

	public void setPathLink(String pathLink) {
		this.pathLink = pathLink;
	}

	@Override
	public int getSize() {
		return 0;
	}
	
	/**
	 * Create a JSONObject to store the structure of a Drive.
	 * Recursively sails between directories and files.
	 * @param parentPath To creat the path of inside FileSystem Objects.
	 * @return JSONObject
	 * @throws JSONException 
	 */
	@Override
	public JSONObject getJSON(String parentPath) throws JSONException {
		// Use the parent way to create the JSON
		// that includes set of id, name(fsName), 
		// createDate, modifiedDate, route, type
		JSONObject obj = super.getJSON(parentPath);
		
		obj.put("size", 0);
		obj.put("owner", owner);
		obj.put("pathLink", pathLink);
		
		return obj;
	}
}
