package br.ufc.insight.pyconnector;

import java.util.ArrayList;
import java.util.List;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.AllEdgesIterator;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;

import py4j.GatewayServer;

public class ShortestPathGraphHopperGateway {

	public class Point {
		double latitude;
		double longitude;

		public Point(double latitude, double longitude) {
			super();
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}
	}

	private GraphHopperOSM hopper = new GraphHopperOSM();
	// private static final CarFlagEncoder encoder = new CarFlagEncoder();
	// private String osmFile;
	// private String graphastDir;
	// private ShortestPathService service;

	public void setup(String osmFile, String graphHopperLocation) {
		hopper.setDataReaderFile(osmFile);
		hopper.setGraphHopperLocation(graphHopperLocation);
		hopper.setMinNetworkSize(200, 200); // VERY, VERY IMPORTANT!!!
		hopper.setEncodingManager(new EncodingManager("car"));
		hopper.importOrLoad();
		System.out.println("finished");		
		
	}
	
	public GraphHopperStorage getGraphHopperStorage() {
		return hopper.getGraphHopperStorage();
	}

	public List<Point> getShortestPath(double latFrom, double lonFrom, double latTo, double lonTo) throws Exception {

		// simple configuration of the request object, see the GraphHopperServlet classs
		// for more possibilities.
		GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).setWeighting("fastest").setVehicle("car");
		GHResponse rsp = hopper.route(req);
		System.out.println(rsp);
		// first check for errors
		// if (rsp.hasErrors()) {
		// // handle them!
		// System.out.println(rsp.getErrors());
		// // rsp.getErrors()
		// return;
		// }
		
		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = rsp.getBest();
		
		// points, distance in meters and time in millis of the full path
		PointList pointList = path.getPoints();
		List<Point> points = new ArrayList<>();
		for (GHPoint3D ghPoint3D : pointList) {
			points.add(new Point(ghPoint3D.lat, ghPoint3D.lon));
		}
		return points;

	}
	
	public GHRequest getRequest(double latFrom, double lonFrom, double latTo, double lonTo) {
		return new GHRequest(latFrom, lonFrom, latTo, lonTo).setWeighting("fastest").setVehicle("car");		
	}
	
	public GHResponse getShortestPathObject(GHRequest req)
			throws Exception {

		// simple configuration of the request object, see the GraphHopperServlet classs
		// for more possibilities.
		GHResponse rsp = hopper.route(req);
		//GHResponse rsp = new GHResponse();		
		return rsp;
	}
	
	
	public List<Integer> getPath(GHRequest req, GHResponse rsp) {
		List<Path> calcPaths = hopper.calcPaths(req, rsp);

		List<Integer> edges = new ArrayList<Integer>();
		System.out.println(calcPaths.size());
		Path p = calcPaths.get(0);
		for (EdgeIteratorState e : p.calcEdges()) {
			edges.add(e.getEdge());
		}
		return edges;
	}

	public PathWrapper getShortestPathObject(double latFrom, double lonFrom, double latTo, double lonTo)
			throws Exception {

		// simple configuration of the request object, see the GraphHopperServlet classs
		// for more possibilities.
		GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).setWeighting("fastest").setVehicle("car");
		GHResponse rsp = hopper.route(req);
		//GHResponse rsp = new GHResponse();
		
		List<Path> calcPaths = hopper.calcPaths(req, rsp);
		
		List<Integer> edges = new ArrayList<Integer>();
	    for(Path p:calcPaths){
	        for(EdgeIteratorState e:p.calcEdges()){
	            edges.add(e.getEdge());
	        }
	    }
		System.out.println(edges);
		//System.out.println(rsp);
		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = rsp.getBest();
		return path;

	}
	
	public PathWrapper getShortestPathObject(double latFrom, double lonFrom, double latTo, double lonTo, String weighting)
			throws Exception {

		// simple configuration of the request object, see the GraphHopperServlet classs
		// for more possibilities.
		GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).setWeighting(weighting).setVehicle("car");
		
		GHResponse rsp = hopper.route(req);
		//System.out.println(rsp);
		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = rsp.getBest();
	
		return path;

	}
	
	

	public static void main(String[] args) {
		// create an entry point
		GatewayServer server = new GatewayServer(new ShortestPathGraphHopperGateway());
		server.start();
		//test();
	}

	private static void test() {
		ShortestPathGraphHopperGateway g = new ShortestPathGraphHopperGateway();
		try {
			g.setup("/media/livia/DATA/Workspace/trajectory_sensors_analysis/osm/fortaleza_sensors.osm", "/media/livia/DATA/Workspace/trajectory_sensors_analysis/osm/");
			
			AllEdgesIterator alledges = g.hopper.getGraphHopperStorage().getAllEdges();
			alledges.next();
			System.out.println(alledges.getEdge());
			int base = alledges.getBaseNode();
			int adj = alledges.getAdjNode();
			
			System.out.println(g.hopper.getGraphHopperStorage().getNodeAccess().getLat(adj));
			System.out.println(g.hopper.getGraphHopperStorage().getNodeAccess().getLat(base));
			
			GHRequest request = g.getRequest(-3.807466044377247,-38.60481337107505, -3.721000,-38.547000);
			GHResponse rsp = g.getShortestPathObject(request);
			g.getPath(request, rsp);
					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
