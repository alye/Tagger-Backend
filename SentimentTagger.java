
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;



/**
 * Wrapper for Alchemy API
 */
public class SentimentTagger {
	
	public static LinkedList<String> API_list;
	public static String API_key;
	
	/**
	 * Internal method used to create a Linked List Containing multiple API keys
	 * Helps overcome API usage restrictions while working with huge amounts of data
	 */
	private static void setList(){
		API_list=new LinkedList<String>();
		API_list.add("api-key-1");
		API_list.add("api-key-2");
		API_list.add("api-key-3");
		API_list.add("api-key-4");
		API_list.add("api-key-5");
		API_list.add("api-key-6");
		API_list.add("api-key-7");
		API_list.add("api-key-8");
		//Add more keys, if required by the application		
		
	}
	
	/* Entry Point */
	public static void main(String[] args) throws Exception{
		
		setList();
		
		String my_working_dir=""; //Specify the working directory here
		
		ReadTextFile fil_list=new ReadTextFile(my_working_dir+"filenames.txt");
		
		while(fil_list.hasNext()){
			
			String curr_file=my_working_dir+fil_list.nextLine();
			
			if(fil_list.fileExists(curr_file)){
				
				ReadJSONFile f=new ReadJSONFile(curr_file);
				WriteJSON w=new WriteJSON(curr_file+"_1.json");
					
				w.processJSONArray(f.getJSONArr());
				w.close();
				
			}else{
				System.out.println("Invalid file: "+curr_file);
			}
		}		

	}

	public static String sendQuery(JSONObject j_obj) throws Exception {
		String tweetLang=j_obj.get("tweet_lang").toString();
		String text1=j_obj.get("text_en").toString();
		if(tweetLang=="en"){
			text1=j_obj.get("text_en").toString();
		}
		if(tweetLang=="de"){
			text1=j_obj.get("text_de").toString();
		}
		if(tweetLang=="ru"){
			text1=j_obj.get("text_ru").toString();
		}
		if(tweetLang=="fr"){
			text1=j_obj.get("text_fr").toString();
		}
		URL urlObj = null;
		
		try {
			urlObj = new URL("http://gateway-a.watsonplatform.net/calls/text/TextGetRankedNamedEntities?apikey=" + API_key + 						"&text="+URLEncoder.encode(text1,"UTF-8")+"&outputMode=json");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(urlObj.toString() + "\n");

		URLConnection connection = urlObj.openConnection();
		connection.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuilder builder = new StringBuilder();
		
		while ((line = reader.readLine()) !=  null) {
		    builder.append(line + "\n");
		}
		System.out.println(builder);
		
		String res=builder.toString();
		
		//insert loop to go through all the tweets here
		JSONObject obj=new JSONObject(res);
		
		String API_response=(String) obj.get("status");
		
		if(API_response.equalsIgnoreCase("OK")){
			
		
		
		JSONArray test2= (JSONArray) obj.get("entities");
		int test_len=test2.length();
		List<String> place = new LinkedList<String>();
		List<String> person = new ArrayList<String>();
		List<String> orgs = new ArrayList<String>();
		List<String> twitter_handle = new ArrayList<String>();
		
		for(int i=0;i<test_len;i++){
			JSONObject obj11=(JSONObject) test2.get(i);
			String type=obj11.get("type").toString();
			String text=obj11.get("text").toString();
			
			if(type.equals("Country") || type.equals("City")||type.equals("Continent"))
			{
				place.add(text);
				
			}
			if(type.equals("Person"))
			{
				person.add(text);
			}
			if(type.equals("Organization"))
			{
				twitter_handle.add(text);
			}
			if(type.equals("TwitterHandle"))
			{
				twitter_handle.add(text);
			}
			//System.out.println(type+":"+text);
		}
		String temp_place=place.toString();
		int index_place=temp_place.length()-1;
		String temp_person=person.toString();
		int index_person=temp_person.length()-1;
		j_obj.append("place",temp_place.substring(1,index_place));
		j_obj.append("person",temp_person.substring(1,index_person));
		String temp_orgs=orgs.toString();
		int index_orgs=temp_orgs.length()-1;
		String temp_twitter_handle=twitter_handle.toString();
		int index_twitter_handle=temp_twitter_handle.length()-1;
		j_obj.append("organization",temp_orgs.substring(1,index_orgs));
		j_obj.append("twitter handle",temp_twitter_handle.substring(1,index_twitter_handle));
		
		} else{
			API_response+=(String)obj.getString("statusInfo");
		}
		
		return(API_response);	
	}	
}

