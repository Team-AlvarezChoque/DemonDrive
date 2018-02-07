/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import Models.Account;
import Models.FileSystem;
import java.io.IOException;
import java.io.PrintWriter;
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
public class delete extends HttpServlet {

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
		if(req.has("username") && req.has("source")){
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
					}else{
						f =  acc.getMyDrive().getFSbyPath(
								req.getString("source") 
						);
					}
					
					if(f != null){
						
						int fLength = f.getSize();
						
						
						int op;
						
						if(req.has("shared")){
							op = acc.getShared().deleteFSbyPath(
										req.getString("source")
								);
						}
						else{
							op = acc.getMyDrive().deleteFSbyPath(
										req.getString("source")
								);
						}
						
						switch(op){
							case -1:
								salida.put("status",2);
								salida.put("message","Invalid operation");
								break;
							case 1:
								acc.increaseUsageDisk(-fLength);
								acc.saveRegister(); // To store in disk
								salida.put("status",1);
								salida.put("message","Success delete");
								break;
							case 0:
								salida.put("status",2);
								salida.put("message","Invalid path");
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

}
