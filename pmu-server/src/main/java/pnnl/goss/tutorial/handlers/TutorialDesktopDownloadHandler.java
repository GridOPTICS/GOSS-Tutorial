package pnnl.goss.tutorial.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pnnl.goss.core.DataError;
import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Request;
import pnnl.goss.core.RequestAsync;
import pnnl.goss.core.Response;
import pnnl.goss.core.client.Client;
import pnnl.goss.core.client.GossClient;
import pnnl.goss.core.client.GossClient.PROTOCOL;
import pnnl.goss.tutorial.request.TutorialDownloadRequestAsync;
import pnnl.goss.tutorial.request.TutorialDownloadRequestSync;
import pnnl.goss.server.core.GossRequestHandler;
import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;
import pnnl.goss.tutorial.datasource.GOSSTutorialDataSource;

public class TutorialDesktopDownloadHandler extends GossRequestHandler {

	TutorialDownloadRequestAsync asyncDownloadRequest;
	long startTime = 0;
	long endTime = 0;
	Date startDate;
	
	
	@Override
	public Response handle(Request request) {
		if(request instanceof RequestAsync){
			return asynchronousHandle(request);
		} else {
			return synchronousHandle(request);
		}
	}
	 
	
	private Response synchronousHandle(Request request){
		Date startDate = ((TutorialDownloadRequestSync)request).getStartDate();
		String resultStr = queryResults(startDate);
		return new DataResponse(resultStr);
	}
	
	private Response asynchronousHandle(Request request){
		
		if(this.asyncDownloadRequest==null)
			this.asyncDownloadRequest = (TutorialDownloadRequestAsync) request;
		if(startTime==0)
			startTime = asyncDownloadRequest.getStartTime();
		else
			startTime = endTime; 
		endTime = startTime+asyncDownloadRequest.getSegment();
		if(startDate==null)
			startDate = asyncDownloadRequest.getStartDate();
		String json = queryResults(startDate);

		startDate = new Date();
		DataResponse response  = new DataResponse(json);
		if(endTime==asyncDownloadRequest.getEndTime())
			response.setResponseComplete(true);
		else
			response.setResponseComplete(false);
		return response;
	}

	
	private String queryResults(Date startDate){
		List<PMUPhaseAngleDiffData> dataList = new ArrayList<PMUPhaseAngleDiffData>();
		try{
			
	//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			//TODO
			Connection connection = GOSSTutorialDataSource.getInstance().getConnection();
			//System.out.println(connection);
			Statement statement = connection.createStatement();
			
			String time = PMUPhaseAngleDiffData.DATE_FORMAT.format(startDate);
			String queryString = "select `time`,phasor1,phasor2,diff from aggregator where `time`>'"+time+"' order by `time`";
			System.out.println(queryString);
			ResultSet rows =  statement.executeQuery(queryString);
			
			
			if (rows.isBeforeFirst() ) {   //make sure it isn't empty
				rows.next();
				while(!rows.isLast()){
					Statement stmt = rows.getStatement();
					if(stmt!=null){
						PMUPhaseAngleDiffData data = new PMUPhaseAngleDiffData();
						String timeStr = rows.getString("time");
						System.out.println(timeStr);
						data.setTimestamp(PMUPhaseAngleDiffData.DATE_FORMAT.parse(timeStr));
						data.setPhasor1(rows.getDouble("phasor1"));
						data.setPhasor2(rows.getDouble("phasor2"));
						data.setDifference(rows.getDouble("diff"));
						dataList.add(data);
					}
					rows.next();
				}
			}
			//connection.commit();
			connection.close();
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}	
		
		Gson gson = new GsonBuilder().setDateFormat(PMUPhaseAngleDiffData.DATE_FORMAT.toPattern()).create();
		String json = gson.toJson(dataList);
		System.out.println("GENERATED JSON "+json);
		return json;
	}
	
}



