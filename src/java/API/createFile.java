/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import Models.Account;
import Models.File;
import Models.TypeFS;
import java.io.IOException;
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
public class createFile extends HttpServlet {

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
		if(req.has("username") && req.has("fileName") && req.has("fileExtention")
				&& req.has("content") && req.has("path")){
			try{
				
				if(!CoreSingleton.getInstance().accountExists(req.getString("username"))){
					salida.put("status",3);
					salida.put("message","Invalid account");
				}
				else{
					
					int fileSize = req.getString("content").getBytes().length;
					
					Account acc = CoreSingleton
							.getInstance()
							.getAccount(req.getString("username"));
					
					if(acc.getUsageDisk() + fileSize > acc.getMaxDisk()){
						salida.put("status",4);
						salida.put("message","You don't have space to store this file");
					}
					else{
						File f = new File(
							CoreSingleton.getInstance().requestID(),
							req.getString("fileName"),
							req.getString("fileExtention"),
							TypeFS.File,
							req.getString("content")
						);

						int op;

						if(req.has("forced")){
							op = acc.getMyDrive().addFS(
									f, 
									req.getString("path"),
									req.getBoolean("forced")
							);
						}
						else
						{
							op = acc.getMyDrive().addFS(f, req.getString("path"), false);
						}

						if(op == 1){
							acc.increaseUsageDisk(fileSize);
							acc.saveRegister(); // To store in disk
							salida.put("status",1);
							salida.put("message","File created");
						}
						else if(op == 2){
							salida.put("status",2);
							salida.put("message","No forced");
						}    
						else{
							salida.put("status",3);
							salida.put("message","Invalid path");
						}
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
