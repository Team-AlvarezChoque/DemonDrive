/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import Models.Account;
import Models.Link;
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
public class share extends HttpServlet {

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
		
		if(req.has("username") && req.has("usernameToShare") && req.has("source") 
				&& req.has("nameFS") ){
			try{
				
				if(!CoreSingleton.getInstance().accountExists(req.getString("username"))){
					salida.put("status",3);
					salida.put("message","Invalid account");
				}
				else{ 
					
					// For us, the links doesn't need real space in the account to share
					Link l = new Link(
							CoreSingleton.getInstance().requestID(),
							req.getString("nameFS"),
							TypeFS.Link,
							req.getString("username"),
							req.getString("source")
					);
					
					Account acc = CoreSingleton
							.getInstance()
							.getAccount(req.getString("usernameToShare"));
					
					if(acc == null){
						salida.put("status",3);
						salida.put("message","Invalid account to share");
					}
					else{
						acc.getShared().addFS(l, "/", false);

						acc.saveRegister(); // To store in disk

						salida.put("status",1);
						salida.put("message","Success share");
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
