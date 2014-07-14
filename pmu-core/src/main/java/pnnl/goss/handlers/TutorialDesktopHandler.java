package pnnl.goss.handlers;

import java.sql.Connection;
import java.sql.Statement;

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
			
			Connection connection = GOSSTutorialDataSource.getInstance().getConnection();
			System.out.println(connection);
			Statement statement = connection.createStatement();
			
			PMUPhaseAngleDiffData data1 = (PMUPhaseAngleDiffData)uploadrequest.getData();
			String queryString = "replace into aggregator(`timestamp`,pmu1Phasor,pmu2Phasor,Result) values "+
									"('"+data1.getTimestamp()+"',"+data1.getPhasor1()+","+data1.getPhasor2()+","+data1.getDifference()+")";
			System.out.println(queryString);
			int rows =  statement.executeUpdate(queryString);
			System.out.println(rows);
			connection.commit();
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
