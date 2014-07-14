package pnnl.goss.handlers;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import pnnl.goss.core.Request;
import pnnl.goss.core.UploadRequest;
import pnnl.goss.core.UploadResponse;
import pnnl.goss.datasource.GOSSTutorialDataSource;
import pnnl.goss.server.core.GossRequestHandler;
import pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData;

public class TutorialDesktopHandler extends GossRequestHandler{
	
public UploadResponse handle(Request request) {
		
		UploadResponse response = null;
		
		try{
			UploadRequest uploadrequest = (UploadRequest)request;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Connection connection = GOSSTutorialDataSource.getInstance().getConnection();
			System.out.println(connection);
			Statement statement = connection.createStatement();
			
			PMUPhaseAngleDiffData data = (PMUPhaseAngleDiffData)uploadrequest.getData();
			
			String time = formatter.format(data.getTimestamp());
			String queryString = "replace into aggregator(`time`,phasor1,phasor2,diff) values "+
									"('"+time+"',"+data.getPhasor1()+","+data.getPhasor2()+","+data.getDifference()+")";
			System.out.println(queryString);
			int rows =  statement.executeUpdate(queryString);
			System.out.println(rows);
			//connection.commit();
			connection.close();
		}
		catch(Exception e){
			response = new UploadResponse(false);
			response.setMessage(e.getMessage());
			e.printStackTrace();
			return response;
		}
		response = new UploadResponse(true);
		return response;
	}

}
