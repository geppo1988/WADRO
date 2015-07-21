package eu.reply.hackathon.wadro.telegram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import eu.reply.hackathon.wadro.image.ImageDownloader;

public class TelegramBOTManager {

	private final static String WADROBOT_KEY = "98972795:AAHrR_La2pfmfRlPuZVH57q-JA4E5tEKh7g";
	private final static String WADROBOT_END_POINT = "https://api.telegram.org/bot";

	public static void main(String args[]){
		TelegramBOTManager tm = new TelegramBOTManager();
		tm.run();
	}

	public JSONObject getUpdates(Integer offset){
		JSONObject output = null;

		try {
			URL url = new URL(WADROBOT_END_POINT+WADROBOT_KEY+"/getUpdates");
			String postData = "&offset="+offset;
			output = doPost(url, postData);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;

	}
	public JSONObject sendRandomBoobs(int chat_id){
		JSONObject output = null;

		try {
			URL url = new URL("http://api.oboobs.ru/noise/");
			String postData = "&preview=";

			output = doPost(url, postData);
			String imageID = (String) output.get("preview");

			ImageDownloader.downloadImage("http://media.oboobs.ru/"+imageID, new File("").getAbsolutePath(), "tempBoobs.jpg");
			sendFile(new File("tempBoobs.jpg"), chat_id);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;

	}
	private JSONObject doPost(URL url,String postData) {
		JSONObject output = null;
		try {

			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			if(postData!=null)
				conn.getOutputStream().write(postData.getBytes());

			StringBuilder sb = new StringBuilder();

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			for ( int c = in.read(); c != -1; c = in.read() )
				sb.append((char)c);
			if(sb.toString()!=null){
				String outputString=sb.toString();
				if(outputString.startsWith("["))
					outputString=outputString.substring(1);
				if(!outputString.endsWith("]"))
					outputString=outputString.substring(0, outputString.length());
				output = new JSONObject(outputString);
			}

		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;

	}


	//makes a multipart/form-data request to upload the specified file
	public void sendFile (File fileToSend, int chatID) {

		final String charset = "UTF-8";

		String requestURL = WADROBOT_END_POINT+WADROBOT_KEY+"/sendPhoto";

		try {
			MultipartUtility multipart = new MultipartUtility(requestURL, charset);

			multipart.addHeaderField("Test-Header", "Header-Value");

			multipart.addFormField("chat_id", chatID+"");
			multipart.addFilePart("photo", fileToSend);

			List<String> response = multipart.finish();

			System.out.println("SERVER REPLIED:");

			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}
	// makes an HTTP GET request to send the specified message
	private void sendMessage(int chat_id2,String message)  {

		String chatIDParameters="?chat_id="+chat_id2+"&text=";
		//	String url_getMe = "https://api.telegram.org/bot"+WADROBOT_KEY+"/getMe";
		String url_sendMsg = WADROBOT_END_POINT+WADROBOT_KEY+"/sendMessage"+chatIDParameters+message;

		URL obj;
		try {
			obj = new URL(url_sendMsg);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("POST");


			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url_sendMsg);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void run()  {
		int last_update_id = 0; // last processed command

		//HttpResponse<JsonNode> response;
		while (true) {
			JSONObject output = getUpdates(last_update_id++);
			if(output!=null){
				JSONArray responses;

				try {
					responses = output.getJSONArray("result");


					if (responses.isNull(0)) continue;
					else
						last_update_id = responses
						.getJSONObject(responses.length() - 1)
						.getInt("update_id") + 1;

					for (int i = 0; i < responses.length(); i++) {
						JSONObject message = responses
								.getJSONObject(i)
								.getJSONObject("message");

						System.out.println("Update fields : " + responses);

						
						Iterator<String> messageFieldsIterator = message.getJSONObject("chat").keys();
						List<String> keysList = TelegramBOTManager.copyIterator(messageFieldsIterator);

						int chat_id=0;
						if(keysList.contains("id"))
							chat_id = message
							.getJSONObject("chat")
							.getInt("id");

						String name = null;
						if(keysList.contains("first_name"))
							name = message
							.getJSONObject("chat")
							.getString("first_name");
						else if(keysList.contains("title"))
							name = message
							.getJSONObject("from")
							.getString("first_name");

						String text = message
								.getString("text");


						if (text.contains("/start")) {
							String reply = "Hi, this is an example bot" +
									"Your chat_id is " + chat_id + "" +
									"Your name is " + name;
							sendMessage(chat_id, reply);
						} else if (text.contains("/echo")) {
							sendMessage(chat_id, "Received " + text);
						}
						else if (text.contains("/boobs")) {
							sendRandomBoobs(chat_id);

						}else if (text.contains("/toupper")) {
							String param = text.substring("/toupper".length(), text.length());
							sendMessage(chat_id, param.toUpperCase());
						} else sendMessage(chat_id, "I cannot understand you "+name+"..."+"Force Daniele to work more!");
					}
				}
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}
	public static <T> List<T> copyIterator(Iterator<T> iter) {
		List<T> copy = new ArrayList<T>();
		while (iter.hasNext())
			copy.add(iter.next());
		return copy;
	}

}

