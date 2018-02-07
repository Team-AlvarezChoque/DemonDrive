
package Models;

import java.io.FileWriter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class Account {
	
	private String user;
	private int maxDisk;
	private int usageDisk;
	private Directory myDrive;
	private Directory shared;

	public Account(String user, int maxDisk) {
		this.user = user;
		this.maxDisk = maxDisk;
		this.usageDisk = 0;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getMaxDisk() {
		return maxDisk;
	}

	public void setMaxDisk(int maxDisk) {
		this.maxDisk = maxDisk;
	}

	public Directory getMyDrive() {
		return myDrive;
	}

	public void setMyDrive(Directory myDrive) {
		this.myDrive = myDrive;
	}

	public int getUsageDisk() {
		return usageDisk;
	}

	public void setUsageDisk(int usageDisk) {
		this.usageDisk = usageDisk;
	}
	
	public void increaseUsageDisk(int i){
		this.usageDisk = this.usageDisk +i;
	}

	public Directory getShared() {
		return shared;
	}

	public void setShared(Directory shared) {
		this.shared = shared;
	}
	
	/**
	 * Create a file in the default folder with the register
	 * of the account in question.
	 */
	public void saveRegister(){
		try {
			JSONObject out = new JSONObject();
			
			// Get the JSON of the myDrive
			JSONObject FSobj = this.getMyDrive().getJSON("/");
			
			// Set data of the user
			out.put("user",this.getUser());
			out.put("usageDisk",FSobj.getInt("size")); // Calculated
			out.put("maxDisk",this.getMaxDisk());
			// May be necessary store the information of 
			// modified date of the myDrive, 
			// currently it doesn't store
			out.put("myDrive",FSobj.getJSONArray("tree"));
			
			// Get the JSON of the myDrive
			JSONObject FSSharedobj = this.getShared().getJSON("/");
			out.put("shared",FSSharedobj.getJSONArray("tree")); 
			
			FileWriter file = new FileWriter(
					"C:\\drive\\"+this.getUser()+".json");
			file.write(out.toString());
			file.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}
