package API;

import Models.Account;
import Models.Directory;
import Models.File;
import Models.FileSystem;
import Models.TypeFS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class copyRV extends HttpServlet {
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject req = API.parsePost(request);
		JSONObject salida = new JSONObject();
		
		// todo Validate username format
		if(req.has("username") && req.has("source") && req.has("destination")){
			try{
				
				if(!CoreSingleton.getInstance().accountExists(req.getString("username"))){
					salida.put("status",3);
					salida.put("message","Invalid account");
				}
				else{ 
					
					Account acc = CoreSingleton
							.getInstance()
							.getAccount(req.getString("username"));
					
					Path path = Paths.get(req.getString("source"));
					
					Directory source = new Directory(
							"NA",
							"real_files",
							TypeFS.Directory);
					source.addFS(getFiles(req.getString("source")));
							
					if(Files.exists(path)){
						
						int fLength = source.getSize();
						
						if(acc.getUsageDisk()+fLength > acc.getMaxDisk()){
							salida.put("status",2);
							salida.put("message","You don't have space to store this file");
						}
						else{
							int op = 3;
							for(FileSystem realFile : source.getTree()){
								
								if(req.has("forced")){
									if(req.has("shared")){
										op = acc.getShared().addFS(
											realFile, 
											req.getString("destination"),
											req.getBoolean("forced")
										);
									}else{
										op = acc.getMyDrive().addFS(
											realFile, 
											req.getString("destination"),
											req.getBoolean("forced")
										);
									}
								}
								else{
									if(req.has("shared")){
										op = acc.getShared().addFS(
											realFile, 
											req.getString("destination"),
											false
										);
									}
									else{
										op = acc.getMyDrive().addFS(
											realFile, 
											req.getString("destination"),
											false
										);
									}
									
								}
								
							}

							switch(op){
								case 0:
									salida.put("status",2);
									salida.put("message","Invalid destination structure");
									break;
								case 1:
									acc.increaseUsageDisk(fLength);
									acc.saveRegister(); // To store in disk
									salida.put("status",1);
									salida.put("message","Success copy");
									break;
								case -2:
									salida.put("status",2);
									salida.put("message","Destination doesn't exist");
									break;
								case -1:
									salida.put("status",2);
									salida.put("message","Source doesn't exist");
									break;
								case 2:
									salida.put("status",5);
									salida.put("message","Can't do the operation (forced)");
									break;
								case 3:
									salida.put("status",2);
									salida.put("message","There's no files to be copied");
									break;
							}
						}
					}
					else{
						salida.put("status",2);
						salida.put("message","Source doesn't exist");
					}
				}
			}
			catch(JSONException ex){
				Logger.getLogger(create.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else{
			try {
				salida.put("status",0);
			} catch (JSONException ex) {
				Logger.getLogger(create.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		API.responseJSON(response, salida);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}
	
	public FileSystem getFiles(String source){
	   
	   try {
		   java.io.File fs = new java.io.File(source);
		   if (fs.isDirectory()) {
			   Directory d = new Directory(
				   CoreSingleton.getInstance().requestID(),
				   fs.getName(),
				   TypeFS.Directory);
			   for (java.io.File file : fs.listFiles()){
				   if (file.isDirectory()) {
					   d.addFS(getFiles(file.getPath()));
				   } else {
					   Scanner sc = new Scanner(file);
					   String content = "";
					   while(sc.hasNextLine()){
						   content += sc.nextLine();
					   }
					   sc.close();

					   System.out.println(file.getName());
					   String[] parts = file.getName().split("\\.");
					   String name = parts[0];
					   String extension = parts[1];

					   Models.File f = new Models.File(
							   CoreSingleton.getInstance().requestID(),
							   name,
							   extension,
							   TypeFS.File,
							   content
						   );
					   d.addFS(f);
				   }
			   }
			   return d;
		   } else {
			   Scanner sc = new Scanner(fs);
			   String content = "";
			   while(sc.hasNextLine()){
				   content += sc.nextLine();                     
			   }
			   sc.close();

			   System.out.println(fs.getName());
			   String[] parts = fs.getName().split("\\.");
			   String name = parts[0];
			   String extension = parts[1];

			   Models.File f = new Models.File(
					   CoreSingleton.getInstance().requestID(),
					   name,
					   extension,
					   TypeFS.File,
					   content
				   );
			   return f;
		   }
	   } catch (FileNotFoundException ex) {
		   System.out.println("File does not exist");
	   }
	   return null;
   }
}
