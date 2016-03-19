package Servlet;

import Bean.TexttoSpeechConnector;
import Bean.AlchemyConnector;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import java.awt.Component;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import com.ibm.watson.developer_cloud.service.WatsonService;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.util.ResponseUtil;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import javax.servlet.annotation.WebServlet;


import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.openstack4j.model.common.Payload;
import org.openstack4j.model.common.Payloads;

@WebServlet(name = "Servlet", urlPatterns = {"/Servlet"})
public class Servlet extends HttpServlet {
	private String FACE_ENDPOINT_URL = "http://gateway-a.watsonplatform.net/calls/url/URLGetRankedImageFaceTags";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOExceptioon if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Servlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Servlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) //text to speech
            throws ServletException, IOException {
				
				
        
        processRequest(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) //transtlator
            throws ServletException, IOException {
			
			//alchemy face info
			//AlchemyConnector connector = new AlchemyConnector();
			//AlchemyVision service = new AlchemyVision();
			//service.setApiKey(connector.getAPIKey());

			String input_url = (String) request.getParameter("img_url");
			StringBuilder sb = new StringBuilder();
			String line;
			
			//URL face_url = new URL(FACE_ENDPOINT_URL+"?url="+input_url+"&apikey="+connector.getAPIKey()+"&outputMode=json");
			URL face_url = new URL(FACE_ENDPOINT_URL+"?url="+input_url+"&apikey=da872b9290ea075fdb572e53df31eb9b455a6034&outputMode=json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(face_url.openStream()));
			while ((line = reader.readLine()) != null){
				sb.append(line);
			}
			
			String alchJSON = sb.toString();
			
			//request.setAttribute("face",sb.toString());

			//ImageFaces image_faces = service.recognizeFaces(input_url,false);
			//request.setAttribute("image_faces",image_faces);
		
			//response.setContentType("text/html");
			//response.setStatus(200);
			//request.getRequestDispatcher("index.jsp").forward(request, response);
			
			//JSON parse
			//JSONArray obj = null;
			String sub = null;
			try{
				JSONArray obj = (JSONArray) JSONSerializer.toJSON(alchJSON);
				sub = obj.getString(0);
			}catch(Exception e){
				JSONObject obj = (JSONObject) JSONSerializer.toJSON(alchJSON);
				sub = obj.getString("imageFaces");
			}
			//String sub = obj.getString("imageFaces");
			JSONArray obj2 = (JSONArray) JSONSerializer.toJSON(sub);
			
			
			
			/*gender*/
			JSONObject genderjsonobj = obj2.getJSONObject(1);
			sub = genderjsonobj.getString("gender");
			JSONObject genderjsonarr = (JSONObject) JSONSerializer.toJSON(sub);
			//JSONObject rec = genderjsonarr.getJSONObject(0);
			String gender = "The person in the picture is a " + genderjsonarr.getString("gender");//rec.getString("gender");
			
			/*age*/
			JSONObject agejsonobj = obj2.getJSONObject(0);
			sub = agejsonobj.getString("age");
			JSONObject agejsonarr = (JSONObject) JSONSerializer.toJSON(sub);
			//JSONObject rec2 = agejsonarr.getJSONObject(0);
			String age = null;
			if(genderjsonobj.getString("gender").equals("FEMALE")){
				age = "Her age is " + agejsonarr.getString("ageRange");//rec2.getString("ageRange");
			}else{
				age = "His age is " + agejsonarr.getString("ageRange");//rec2.getString("ageRange");
			}
			
			String f_output = "Information Extraction Complete. " + gender + ". " + age;
			
			//Text to speech
			TexttoSpeechConnector tsconnector = new TexttoSpeechConnector(); 
			TextToSpeech service = new TextToSpeech();
			service.setUsernameAndPassword(tsconnector.getUsername(),tsconnector.getPassword());
			
			String text = f_output;
        	String format = "audio/wav";

  			InputStream speech = service.synthesize(text, format);
			
            OutputStream output = response.getOutputStream();

			byte[] buf = new byte[2046];
			int len;
			while ((len = speech.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
                        
            response.setContentType("audio/wav"); 
			response.setHeader("Content-disposition","attachment;filename=output.wav");  
 
			OutputStream os = output;   
                                
			os.flush();  
			os.close();
			
			response.sendRedirect("http://exp-points-a.mybluemix.net/convert.jsp");  
		processRequest(request, response);
		/*obj storage
		ObjectStorageConnector connect = new ObjectStorageConnector();
		Payload upfile = Payloads.create(speech);
		connect.uploadFile("sample", "Servlet.wav", upfile);
		*/
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
