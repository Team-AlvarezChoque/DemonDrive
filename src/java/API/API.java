/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author adalvarez
 * @author mchoque
 */
public class API {

	public static JSONObject parsePost(HttpServletRequest request) throws IOException{

		JSONObject requestJSON = null;

		if(request.getParameter("data") == null){
			StringBuilder sb = new StringBuilder();
			BufferedReader br = request.getReader();
			String str = null;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			try{
				requestJSON = new JSONObject(sb.toString());
			}
			catch(Exception e){}
		}
		else{
			try {
				requestJSON = new JSONObject(request.getParameter("data").toString());
				
			} catch (Exception ex) {
			}
		}
		return requestJSON; 
	}

	public static void responseJSON(HttpServletResponse response, JSONObject salida) throws IOException{
		String json = salida.toString();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
