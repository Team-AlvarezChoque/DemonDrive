package API;

import Models.Account;
import Models.Directory;
import Models.TypeFS;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class CoreSingleton {

	private static CoreSingleton instance;
	
	private JSONObject accsRegister;
	private ArrayList<Account> accounts;
	private int currentID;
	
	private CoreSingleton() {
		accounts = new ArrayList<>();
		this.loadDrive();
		
		try {
			FileInputStream idFile = new FileInputStream("C:\\drive\\id.txt");
			currentID = Integer.valueOf(IOUtils.toString(idFile));
			idFile.close();
		} catch(Exception e) {
			System.out.println("Error to load ID");
		}
		
	}

	public String requestID(){
		currentID += 1;
		String id = String.valueOf(currentID);
		try{
			FileWriter file = new FileWriter("C:\\drive\\id.txt");
			file.write(id);
			file.close();
		}catch(Exception e) {
			System.out.println("Error to update ID");
		}
		return id; 
	}
	
	private void loadDrive(){
		try{
			// Read the file base that contains all the counts
			FileInputStream driveSetting = new FileInputStream("C:\\drive\\drive.json");
			String driveString = IOUtils.toString(driveSetting);
			driveSetting.close();
			
			// Parse JSON
			accsRegister = new JSONObject(driveString);
			JSONObject accountsJSON = ((JSONObject)accsRegister.get("accounts"));

			// Iterate the JSON of accounts index in drive.json
			Iterator<?> keys = accountsJSON.keys();
			while( keys.hasNext() ) {
				String key = (String)keys.next();
				
				// Read an account file
				FileInputStream accountFile = new FileInputStream((String) accountsJSON.get(key));
				String accountString = IOUtils.toString(accountFile);
				accountFile.close();
				
				// Parse JSON
				JSONObject accountObj = new JSONObject(accountString);
				
				// Create an account
				Account acc = new Account(
						accountObj.get("user").toString(),
						Integer.valueOf(accountObj.get("maxDisk").toString())
				);
				
				// Set usage disk
				acc.setUsageDisk(Integer.valueOf(accountObj.get("usageDisk").toString()));
				
				// Create a FileSystem to store the drive of the user.
				Directory FS = new Directory("0","myDrive",TypeFS.FS);
				
				// Allocate the Drive based on the JSON of myDrive
				// Remember, the allocate method omit the size file and paths.
				FS.allocateDrive(accountObj.getJSONArray("myDrive"));
				
				// Set the FileSystem to the Account
				acc.setMyDrive(FS);
				
				// Create a FileSystem to store the drive of the user (Shared).
				Directory FSShare = new Directory("0","shared",TypeFS.FS);
				
				// Allocate the DriveShared based on the JSON of myDrive
				// Remember, the allocate method omit the size file and paths.
				FSShare.allocateDriveShared(accountObj.getJSONArray("shared"));
				
				// Set the FileSystem (Shared) to the Account.
				acc.setShared(FSShare);
				
				// Store the account.
				accounts.add(acc);
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}    

	public ArrayList<Account> getAccounts() {
		return accounts;
	}
	
	public void addAccount(Account acc) throws JSONException{
		this.accounts.add(acc);
		((JSONObject)this.accsRegister.get("accounts")).put(
				acc.getUser(), 
				"C:\\drive\\"+acc.getUser()+".json");
		this.updateAccountsRegister();
	}
	
	public void updateAccountsRegister(){
		try{
			FileWriter file = new FileWriter("C:\\drive\\drive.json");
			file.write(this.accsRegister.toString());
			file.close();
		}catch(Exception e) {
			System.out.println("Error to update accounts register");
		}
	}
	
	public boolean accountExists(String username){
		for(Account a: this.accounts){
			if(a.getUser().equals(username))
				return true;
		}
		return false;
	}
	
	public Account getAccount(String username){
		for(Account a: this.accounts){
			if(a.getUser().equals(username))
				return a;
		}
		return null;
	}

	public static synchronized CoreSingleton getInstance(){
		if(instance == null){
			instance = new CoreSingleton();
		}
		return instance;
	}
	
}
