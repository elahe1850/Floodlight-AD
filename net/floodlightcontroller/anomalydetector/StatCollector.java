package net.floodlightcontroller.anomalydetector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.simpleframework.http.Response;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;




public class StatCollector 
{
	private URL TargetURL = null;
	private HttpURLConnection conn = null;
	private BufferedReader BufferReader = null;
	
	private PrintWriter LogWriter = null;
	private String StatType = null;
	
	private String FileName = null;
	
	//Declare the constants
	protected static String OUTPUT_FILE_NAME = "LogWriter";
	protected static String STAT_FORMAT = "/json";
	
	
	protected static String ControllerSocket = "localhost:8080";
	protected static String ServiceURI = "/wm/core/switch";  
	protected static String HTTP_POST = "POST";
	protected static String HTTP_GET = "GET";
	protected static String LOG_FORMAT = "<DestIP/subnet NWproto SrcIP/subnet DestPort SrcPort byteCnt pktCnt>";
	private  String StringURL = null;
	/*Constructor for collecting "statType" parameter from each switch denoted by "dpid" */
	public StatCollector(String dpid, String StatType) 
	{
		 this.StatType = "/" + StatType + "/";
		 this.StringURL =  "http://" + StatCollector.ControllerSocket + StatCollector.ServiceURI + dpid +  StatCollector.STAT_FORMAT;
		 System.out.println(this.StringURL);
		 this.FileName = StatCollector.OUTPUT_FILE_NAME + "_" + dpid + "_" + ".txt";
		 
	}
	
	public StatCollector(String StatType) 
	{
		 this.StatType = "/" + StatType + "/";
		 this.StringURL =  "http://" + StatCollector.ControllerSocket + StatCollector.ServiceURI + "all" + this.StatType + StatCollector.STAT_FORMAT;
		 System.out.println(this.StringURL);
		 this.FileName = StatCollector.OUTPUT_FILE_NAME + "_" + "all" + "_" + StatType + ".txt";
		 
	}
	
	
	public void Connect()
	{
		try 
		{
			 this.TargetURL = new URL(this.StringURL);
			 this.conn = (HttpURLConnection) TargetURL.openConnection();
			 if (this.conn != null)
			 {
				 this.conn.setRequestMethod(StatCollector.HTTP_GET); 
				 this.conn.setRequestProperty("Accept", "application/json");
				 if (conn.getResponseCode() != 200) 
				 {
					throw new RuntimeException("Failed : HTTP error code : " + this.conn.getResponseCode());
				 }
				 else
				 {
					this.LogResponse(this.conn.getInputStream()); 
				 }
			 }
		}
		catch (Exception e) 
		{
			 e.printStackTrace();
		}
	}
	
	
	private void OpenLogWriter()
	{
		try 
		{
		   LogWriter = new PrintWriter(this.FileName);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	private void CloseLogWriter()
	{
		if (this.LogWriter != null)
		{
			try
			{
				this.LogWriter.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*Method to LogWriter the response */
	private void LogResponse(InputStream input)
	{
		String output;
		String parsedOutput;
		
		BufferReader = new BufferedReader(new InputStreamReader(input));
		this.OpenLogWriter();
		try 
		{
			
			while ((output = BufferReader.readLine()) != null) 
			{
					
					
				parsedOutput = this.ParseResult(output);
				
				/*We can just write it into a file for perhaps debugging purposes...
				 * Feel free to comment it out again
				 */
				this.LogWriter.println(StatCollector.LOG_FORMAT);
				this.LogWriter.append(parsedOutput);
			}
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		this.CloseLogWriter();
	}
	
	private String ParseResult(String input)
	{
		//Gson gson = new Gson();
		//String JsonInput= gson.toJson(input);
		//Type mapType = new TypeToken<Map<String,Map<String, String>>>() {}.getType();
		//Map<String,Map<String, String>> map = gson.fromJson(JsonInput, mapType);
		
		
		String result = "";
		StringTokenizer tokenizer = new StringTokenizer(input, "[ :,\"{}\\[\\]]+");
	
		while (tokenizer.hasMoreElements()) 
		{
		//System.out.println(tokenizer.nextToken());
			String temp;
			temp = tokenizer.nextToken();
			if(temp.equals("networkDestination"))
			{
				result = result + " <";
				result = result + tokenizer.nextToken();
				result = result + "/";
			}
			
			if(temp.equals("networkDestinationMaskLen"))
			{
				result = result + tokenizer.nextToken();
				result = result + " ";
			}
			
			if(temp.equals("networkProtocol"))
			{
				result = result + tokenizer.nextToken();
				result = result + " ";
			}
			
			if(temp.equals("networkSource"))
			{
				result = result + tokenizer.nextToken();
				result = result + "/";
			}
			
			if(temp.equals("networkSourceMaskLen"))
			{
				result = result + tokenizer.nextToken();
				result = result + " ";
			}
			
			if(temp.equals("transportDestination"))
			{
				result = result + tokenizer.nextToken();
				result = result + " ";
			}
			
			if(temp.equals("transportSource"))
			{
				result = result + tokenizer.nextToken();
				result = result + " ";
			}
			
			if(temp.equals("byteCount"))
			{
				result = result + tokenizer.nextToken();
				result = result + " ";
			}
			
			if(temp.equals("packetCount"))
			{
				result = result + tokenizer.nextToken();
				result = result + "> ";
				result = result + "\r\n";
			}
		}
		
		System.out.println(result);
		return result;
	}
	
	public List<StatResult> GetCounts()
	{
		this.Connect();
		List<StatResult> Counts = new ArrayList<StatResult>();
		
		//foreach 
		
		return Counts;
	}
	
	public class StatResult
	{
		String FlowName;
		int PacketCount;
		int ByteCount;
		public StatResult(String FlowName, int PacketCount, int ByteCount)
		{
			this.FlowName = FlowName;
			this.PacketCount = PacketCount;
			this.ByteCount = ByteCount;
		}
	}
	
 
}
 
