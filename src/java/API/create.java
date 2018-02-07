/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import Models.Account;
import Models.Directory;
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
public class create extends HttpServlet {
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
		
		// todo Validate username format and sizeStorage
		if(req.has("username") && req.has("sizeStorage")){
			try{
				
				if(CoreSingleton.getInstance().accountExists(req.getString("username"))){
					salida.put("status",2);
					salida.put("message","Account already exists");
				}
				else{
					// Create Account object
					Account acc = new Account(
							req.getString("username"),
							req.getInt("sizeStorage")
					);
					// Create FileSystem to this Account
					Directory d = new Directory(
							"0",//default
							"myDrive",
							TypeFS.FS);
					// Set FS
					acc.setMyDrive(d);
					// Create FileSystemShared to this Account
					Directory dS = new Directory(
							"0",//default
							"shared",
							TypeFS.FS);
					// Set FS
					acc.setShared(dS);
					// Save the register of this account
					acc.saveRegister();
					// Add to the global accounts
					CoreSingleton.getInstance().addAccount(acc);

					salida.put("status",1);
					salida.put("message","Account created");
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
