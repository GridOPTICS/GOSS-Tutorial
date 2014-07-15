package pnnl.goss.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pnnl.goss.core.DataError;
import pnnl.goss.core.DataResponse;
import pnnl.goss.core.Request;
import pnnl.goss.core.RequestAsync;
import pnnl.goss.core.Response;
import pnnl.goss.datasource.GOSSTutorialDataSource;
import pnnl.goss.request.TutorialDownloadRequestAsync;
import pnnl.goss.server.core.GossRequestHandler;
import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;

public class TutorialDesktopDownloadHandler extends GossRequestHandler{

	TutorialDownloadRequestAsync downloadRequest;
	long startTime = 0;
	long endTime = 0;
	Date startDate;
	
	@Override
	public Response handle(Request request) {
		return asynchronousHandle(request);
	}
	


	
	private Response asynchronousHandle(Request request){
		
		if(this.downloadRequest==null)
			this.downloadRequest = (TutorialDownloadRequestAsync) request;
		if(startTime==0)
			startTime = downloadRequest.getStartTime();
		else
			startTime = endTime; 
		endTime = startTime+downloadRequest.getSegment();
		if(startDate==null)
			startDate = downloadRequest.getStartDate();
		List<PMUPhaseAngleDiffData> dataList = new ArrayList<PMUPhaseAngleDiffData>();
		try{
			
	//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Connection connection = GOSSTutorialDataSource.getInstance().getConnection();
			System.out.println(connection);
			Statement statement = connection.createStatement();
			
			String time = PMUPhaseAngleDiffData.DATE_FORMAT.format(startDate);
			String queryString = "select `time`,phasor1,phasor2,diff from aggregator where `time`>='"+time+"'";
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
			DataError error = new DataError(e.getCause().toString());
			DataResponse response  = new DataResponse(error);
			return response;
		}	
		
		Gson gson = new GsonBuilder().setDateFormat(PMUPhaseAngleDiffData.DATE_FORMAT.toPattern()).create();
		String json = gson.toJson(dataList);
System.out.println("GENERATED JSON "+json);

		startDate = new Date();
		DataResponse response  = new DataResponse(json);
		if(endTime==downloadRequest.getEndTime())
			response.setResponseComplete(true);
		else
			response.setResponseComplete(false);
		return response;
	}

}



