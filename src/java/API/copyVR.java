package API;

import Models.Account;
import Models.Directory;
import Models.File;
import Models.FileSystem;
import Models.Link;
import Models.TypeFS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
public class copyVR extends HttpServlet {
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
					
					
					FileSystem f;
					
					if(req.has("shared")){
						f =  acc.getShared().getFSLbyPath(
								req.getString("source")
						);
						
						Account accOwner = CoreSingleton
								.getInstance()
								.getAccount(
									((Link)f).getOwner()
								);
						
						f = accOwner
								.getMyDrive()
								.getFSbyPath(((Link)f).getPathLink());
					}
					else{
						f =  acc.getMyDrive().getFSbyPath(
								req.getString("source")
						);
					}
					
					Path path = Paths.get(req.getString("destination"));
					
					if(Files.exists(path)){
						
						int op = this.createFiles(f, req.getString("destination"));

						switch(op){
							case 1:
								salida.put("status",1);
								salida.put("message","Success copy");
								break;
							case -2:
								salida.put("status",2);
								salida.put("message","Encoding error");
								break;
							case -1:
								salida.put("status",2);
								salida.put("message","Source doesn't exist");
								break;
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
	
	public int createFiles(FileSystem fs, String destination){
		try{
			if (fs.getType() == TypeFS.File){
				Models.File fsFile = (Models.File) fs;
				PrintWriter writer = new PrintWriter(
						destination + fsFile.getFileName(),
						"UTF-8");
				writer.print(fsFile.getContent());
				writer.close();
			}
			else{
				Directory fsDir = (Directory) fs;
				java.io.File theDir = new java.io.File(destination + fsDir.getFsName());
				theDir.mkdir();
				for(FileSystem newFile : fsDir.getTree()){
					createFiles(newFile, destination + fsDir.getFsName() + "\\");
				}
			}
			return 1;
		} catch (FileNotFoundException ex) {
			System.out.println("File not found");
			return -1;
		} catch (UnsupportedEncodingException ex) {
			System.out.println("Encoding error when trying to create a file in the system");
			return -2;
		}
	}
	
}
