
package Models;

import API.CoreSingleton;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class Directory extends FileSystem{
	
	private ArrayList<FileSystem> tree;
	
	public Directory(String id, String fsName, TypeFS type) {
		super(id, fsName, type);
		this.tree = new ArrayList<>();
	}

	public ArrayList<FileSystem> getTree() {
		return tree;
	}

	public void setTree(ArrayList<FileSystem> tree) {
		this.tree = tree;
	}
	
	public int addFS(FileSystem fs){
		this.tree.add(fs);
		this.setModifiedDate(new Date());
		return 1;
	}
	
	public int addFS(FileSystem fs, String path, boolean forced){
		String query;
		if(fs.getType() == TypeFS.Directory || fs.getType() == TypeFS.Link){
			query = fs.getFsName();
		}
		else{
			query = ((File)fs).getFileName();
		}
		
		if( this.existsFS(query, path) && forced == true ){
			String pathToDelete = path + query;
			if(fs.getType() == TypeFS.Directory){
				pathToDelete += "/";
			}
			this.deleteFSbyPath(pathToDelete);
		}
		else if(this.existsFS(query, path) && forced == false ){
			System.out.println("[addFS]: No forced.");
			return 2;
		}
		
		if(path.equals("/")){
			this.tree.add(fs);
			this.setModifiedDate(new Date());
			return 1;
		}
		
		String[] parts = path.split("/");
		
		for(FileSystem i: this.tree){

			if(i.getType() == TypeFS.Directory
					&& i.getFsName().equals(parts[1])
					&& parts.length == 2
				){
				// There is the correct path
				// Check if the file or directory exists
				
				((Directory)i).tree.add(fs);
				this.setModifiedDate(new Date());
				return 1; // Ok

			}
			else if(i.getType() == TypeFS.Directory
					&& i.getFsName().equals(parts[1])
				){
				
				int start = parts[1].length()+1;
				String newPath = path.substring(start);
				
				int result = ((Directory) i).addFS(fs, newPath, forced);
				
				if(result == 1){
					this.setModifiedDate(new Date());
					return 1; // Ok
				}
				else{
					return result; // Invalid path(0) or No forced (2)
				}
			}
		}
		System.out.println("[addFS]: Invalid path.");
		return 0; // Invalid path
	}
	
	
	public int deleteFSbyPath(String path) {
		String[] parts = path.split("/");
		
		if(parts.length == 0){
			System.out.println("[deleteFSbyPath]: Invalid operation");
			return -1;
		}
		
		for(FileSystem i: this.tree){
			if(i.getType() == TypeFS.File
					&& ((File)i).getFileName().equals(parts[1])
				){
				this.tree.remove(i);
				this.setModifiedDate(new Date());
				return 1;
			}
			else if( (i.getType() == TypeFS.Directory || i.getType() == TypeFS.Link)
					&& i.getFsName().equals(parts[1])
				){
				
				if(parts.length == 2){ // lo que se va a borrar es un directorio
					this.tree.remove(i);
					this.setModifiedDate(new Date());
					return 1;
				}
				
				int start = parts[1].length()+1;
				String newPath = path.substring(start);
				
				if(((Directory) i).deleteFSbyPath(newPath) == 1){
					this.setModifiedDate(new Date());
					return 1;
				}
				else{
					return 0;
				}
			}
		}
		System.out.println("[deleteFSbyPath]: Invalid path");
		return 0;
	}
	
	public int deleteFSbyID(String id){
		for(FileSystem i: this.tree){
			if(i.getId().equals(id)){
				this.tree.remove(i);
				this.setModifiedDate(new Date());
				return 1;
			}
			else if(i.getType().name().equals("Directory")){
				
				if(((Directory) i).deleteFSbyID(id) == 1){
					this.setModifiedDate(new Date());
					return 1;
				}
			}
		}
		return 0;
	}
	
	public FileSystem getFSLbyPath(String path){
		if(path.equals("/")){
			return this;
		}
		
		String[] parts = path.split("/");
		
		if(parts.length == 0){
			System.out.println("[getObjbyPath]: Operación inválida");
			return null;
		}
		for(FileSystem i: this.tree){
			if( i.getFsName().equals(parts[1]) ){
				return i;
			}
		}
		System.out.println("[getObjbyPath]: Objeto no encontrado");
		return null;
	}
	
	public FileSystem getFSbyPath(String path){
		if(path.equals("/")){
			return this;
		}
		
		String[] parts = path.split("/");
		
		if(parts.length == 0){
			System.out.println("[getObjbyPath]: Operación inválida");
			return null;
		}
		
		for(FileSystem i: this.tree){
			if(i.getType() == TypeFS.File
					&& ((File)i).getFileName().equals(parts[1])
				){
				return i;
			}
			else if(i.getType() == TypeFS.Directory
					&& i.getFsName().equals(parts[1])
					&& parts.length == 2
				){
				return i;
			}
			else if(i.getType() == TypeFS.Directory
					&& i.getFsName().equals(parts[1])
				){
				int start = parts[1].length()+1;
				String newPath = path.substring(start);
				
				return ((Directory) i).getFSbyPath(newPath);
			}
		}
		System.out.println("[getObjbyPath]: Objeto no encontrado");
		return null;
	}
	
	// Parte del hecho que el path es válido
	public int updateFSbyPath(String path){
		if(path.equals("/")){
			this.setModifiedDate(new Date());
			return 1;
		}
		
		String[] parts = path.split("/");

		for(FileSystem i: this.tree){
			if(i.getType() == TypeFS.Directory
					&& i.getFsName().equals(parts[1])
					&& parts.length == 2
				){
				this.setModifiedDate(new Date());
				i.setModifiedDate(new Date());
				return 1;
			}
			else if(i.getType() == TypeFS.Directory
					&& i.getFsName().equals(parts[1])
				){
				int start = parts[1].length()+1;
				String newPath = path.substring(start);
				
				if(((Directory) i).updateFSbyPath(newPath) == 1){
					this.setModifiedDate(new Date());
					return 1;
				}
			}
		}
		return 0;
	}
	
	public boolean existsFS(String query, String path){
		
		Directory d = (Directory)this.getFSbyPath(path);
		if(d == null){
			return false;
		}
		
		for(FileSystem i: d.tree ){
			if(
					(
						(i.getType() == TypeFS.Directory || i.getType() == TypeFS.Link)
							&& 
						i.getFsName().equals(query)
					)
						||
					(
						i.getType() == TypeFS.File
							&&
						((File)i).getFileName().equals(query)
					)
				){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param source File or Directory
	 * @param destination Directory
	 * @param forced
	 * @return 2 Can't do the operation
	 *         1 All it's ok
	 *         0 Destination doesn't have a correct format
	 *         -1 Source not exists
	 *         -2 Destination not exists
	 */
	public int moveFS(String source, String destination, boolean forced){
		
		if(!destination.endsWith("/")){
			return 0;
		}
		
		FileSystem src = this.getFSbyPath(source);
		if(src == null)
			return -1;
		Directory dest = (Directory)this.getFSbyPath(destination);
		if(dest == null)
			return -2;
		
		String[] parts = source.split("/");
		
		if(!this.existsFS(parts[parts.length-1], destination)){
			dest.tree.add(src);
			this.deleteFSbyPath(source); // And update source
			this.updateFSbyPath(destination);
			return 1;
		}
		else if(forced == true){
			// Delete the existent
			String existent = destination + parts[parts.length-1] + ((source.endsWith("/"))? "/":"");
			this.deleteFSbyPath(existent);
			
			dest.tree.add(src);
			this.deleteFSbyPath(source); // And update source
			this.updateFSbyPath(destination);
			return 1;
		}
		return 2;
		
	}
	
	public FileSystem cloneFS(FileSystem fs){
		FileSystem fsNew = null;
		if(fs.getType() == TypeFS.File){
			File clon = (File) fs;
			fsNew = new File(
					CoreSingleton.getInstance().requestID(),
					clon.getFsName(),clon.getExtension(),clon.getType(),clon.getContent());
		}
		else{
			Directory clon = (Directory) fs;
			fsNew = new Directory(CoreSingleton.getInstance().requestID(),
					clon.getFsName(),clon.getType());
			for(FileSystem i: clon.tree){
				((Directory) fsNew).tree.add( this.cloneFS(i) );
			}
		}
		return fsNew;
	}
	
	/**
	 * 
	 * @param source File or Directory
	 * @param destination Directory
	 * @param forced
	 * @return 2 Can't do the operation
	 *         1 All it's ok
	 *         0 destination doesn't have a correct format
	 *         -1 Source not exists
	 *         -2 Destination not exists
	 */
	public int copyFS(String source, String destination, boolean forced){
		
		if(!destination.endsWith("/")){
			return 0;
		}
		
		FileSystem src = this.getFSbyPath(source);
		if(src == null)
			return -1;
		Directory dest = (Directory)this.getFSbyPath(destination);
		if(dest == null)
			return -2;
		
		String[] parts = source.split("/");
		
		if(!this.existsFS(parts[parts.length-1], destination)){
			dest.tree.add(this.cloneFS(src));
			this.updateFSbyPath(destination);
			return 1;
		}
		else if(forced == true){
			// Delete the existent
			String existent = destination + parts[parts.length-1] + ((source.endsWith("/"))? "/":"");
			this.deleteFSbyPath(existent);
			
			dest.tree.add(this.cloneFS(src));
			this.updateFSbyPath(destination);
			return 1;
		}
		return 2;
	}
	
	public void allocateDriveShared(JSONArray driveS) throws JSONException{
		for(int n = 0; n < driveS.length(); n++)
		{
			JSONObject FSObj = driveS.getJSONObject(n);
			Link l = new Link(
					FSObj.getString("id"),
					((JSONObject)FSObj.get("name")).getString("fsName"),
					TypeFS.Link,
					FSObj.getString("owner"),
					FSObj.getString("pathLink")
			);
			this.addFS(l);
		}
	}
	
	public void allocateDrive(JSONArray drive) throws JSONException{
		for(int n = 0; n < drive.length(); n++)
		{
			JSONObject FSObj = drive.getJSONObject(n);
			if(FSObj.get("type").equals(TypeFS.File.name())){
				File f = new File(
						FSObj.getString("id"),
						((JSONObject)FSObj.get("name")).getString("fsName"),
						((JSONObject)FSObj.get("name")).getString("extension"),
						TypeFS.File,
						FSObj.getString("content")
				);
				
				f.setModifiedDate(new Date(Long.parseLong(FSObj.getString("modifiedDate"))));
				f.setCreateDate(new Date(Long.parseLong(FSObj.getString("createDate"))));
				this.addFS(f);
			}
			else{ // Directory
				Directory d = new Directory(
						FSObj.getString("id"),
						((JSONObject)FSObj.get("name")).getString("fsName"),
						TypeFS.Directory
				);
				
				d.setModifiedDate(new Date(Long.parseLong(FSObj.getString("modifiedDate"))));
				d.setCreateDate(new Date(Long.parseLong(FSObj.getString("createDate"))));
				
				d.allocateDrive( FSObj.getJSONArray("tree") );
				
				this.addFS(d);
			}
		}
	}
	
	@Override
	public int getSize() {
		int size = 0; 
		for(FileSystem i: this.tree){
			// Recursive call
			// Carry the count of the size 
			size += i.getSize();
		}
		return size;
	}
	
	public void setOwner(JSONObject obj, String owner, String pathLink) throws JSONException{
		obj.put("owner", owner);
		obj.put("pathLink", pathLink);
		if(obj.has("tree")){
			JSONArray arr = obj.getJSONArray("tree");
			for(int i=0; i< arr.length(); i++){
				this.setOwner(arr.getJSONObject(i), owner, pathLink);
			}
		}
	}
	
	public JSONArray searchSharedFiles() throws JSONException{
		JSONArray obj = new JSONArray();
		
		for(FileSystem i: this.tree){
			Account acc = CoreSingleton
				.getInstance()
				.getAccount( ((Link)i).getOwner() );
			
			FileSystem FS = acc.getMyDrive().getFSbyPath(((Link)i).getPathLink());
			
			if(FS != null){
				JSONObject nObj;
				if(FS.getType() == TypeFS.File){
					nObj = ((File) FS).getJSON("/shared/");
					nObj.put("owner",((Link)i).getOwner());
					nObj.put("pathLink",((Link)i).getPathLink());
				}
				else{
					nObj = ((Directory) FS).getJSON("/shared/");
					this.setOwner(nObj, ((Link)i).getOwner(),((Link)i).getPathLink());
				}
				obj.put(nObj);
			}
		}
		
		return obj;
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
		
		// Set the obtained route with an / in the end.
		// that because this is a Directory
		obj.put("route", obj.get("route")+"/");
		
		// Create an array to store its children
		JSONArray arr = new JSONArray();
		int size = 0;
		for(FileSystem i: this.tree){
			// Recursive call
			JSONObject childObj = i.getJSON(obj.get("route").toString());
			arr.put(childObj);
			// Carry the count of the size 
			size += childObj.getInt("size");
		}
		obj.put("size", size);
		obj.put("tree", arr);
		
		return obj;
	}
	
}
