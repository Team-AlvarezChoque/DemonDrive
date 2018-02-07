/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import Models.Account;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class enter extends HttpServlet {

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
		
		if(req.has("username")){
			try {
				
				if(!CoreSingleton.getInstance().accountExists(req.getString("username"))){
					salida.put("status",2);
					salida.put("message","Invalid account");
				}
				else{
					Account acc = CoreSingleton.getInstance().getAccount(req.getString("username"));
				
					salida.put("status",1);
					JSONObject out = new JSONObject();

					// Get the JSON of the myDrive
					JSONObject FSobj = acc.getMyDrive().getJSON("/");

					// Set data of the user
					out.put("user",acc.getUser());
					out.put("usageDisk",FSobj.getInt("size"));
					out.put("maxDisk",acc.getMaxDisk());
					out.put("myDrive",FSobj.getJSONArray("tree"));
					
					// Get the JSON of the myDrive
					JSONArray FSShareobj = acc.getShared().searchSharedFiles();                    
					out.put("shared",FSShareobj);

					salida.put("account", out);
				}
				
			} catch (JSONException ex) {
				Logger.getLogger(enter.class.getName()).log(Level.SEVERE, null, ex);
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
