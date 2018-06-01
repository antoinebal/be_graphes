package org.insa.algo.shortestpath;


import java.util.List;


import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;


import org.insa.graph.Graph;
import org.insa.graph.Node;


public class PointDeRencontre extends ShortestPathAlgorithm {
	

	

	//data du point de rencontre
	protected ShortestPathData data;
	protected String mapName;
	
	protected Node posV1;
	protected Node posV2;
	
	protected Graph graph;
	
	protected ArcInspector inspectorFastest;
	protected ArcInspector inspectorShortest;
	
	protected double distanceV1_V2;


	protected PointDeRencontre(ShortestPathData data) {
		super(data);
		mapName = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";

	}
	
	void aStarV1_V2() {
		ShortestPathData dataAStar = new ShortestPathData(graph, posV1, posV2, inspectorShortest);

		AStarAlgorithm AStar = new AStarAlgorithm(dataAStar);
		ShortestPathSolution solution = AStar.run();
		if (solution.isFeasible()) {
			distanceV1_V2 = solution.getPath().getLength();
		} else {
			distanceV1_V2 = 0;
		}
		
		
	}

	
	@Override
	protected ShortestPathSolution doRun() {

		List<ArcInspector> inspector = ArcInspectorFactory.getAllFilters();
		inspectorFastest = inspector.get(0);
		inspectorShortest = inspector.get(2);
		
		AStarAlgorithm AStar;
		ShortestPathData data = getInputData();
		graph = data.getGraph();
		//liste des noeuds respectant les conditions
		//ArrayList<Node> pointsM = null;
		
		ShortestPathSolution solution;

		//reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
		//graph = reader.read();
		

		posV1 = data.getOrigin();
		posV2 = data.getDestination();
		
		aStarV1_V2();
		
		if (distanceV1_V2 == 0) {
		
			//pas de solution, on sort
			
		}
		
		int nbNodes = graph.size();
		for (int i = 1 ; i < nbNodes ; i++) {
			if (verifie_duree(i)) {
				if (verifie_distance(i)) {
					//si non est ici, ça veut dire que le point respecte les 2 cond
					//pointsM.add(graph.get(i));
					notifyNodeReached(graph.get(i));
			}
		}
	
			
			
		}
		

			// Create the final solution.
			//je mets NULL dans le path, la valeur retournée n'a ici pas d'importance
			//car on va mettre des notify sur les noeuds qui font respectent les conditions
			solution = new ShortestPathSolution(data, Status.OPTIMAL, null);
		


		
		return solution;

	}
		
	
		//pour vérifier que le noeud courant respecte la condition de durée
		protected boolean verifie_duree(int i) {
			double dureeV1_M;
			double dureeV2_M;
			Node noeudCourant = graph.get(i);
			//data des A*		
			ShortestPathData dataAStar = new ShortestPathData(graph, posV1, noeudCourant, inspectorFastest);

			AStarAlgorithm AStar = new AStarAlgorithm(dataAStar);
			ShortestPathSolution solution = AStar.run();
			if (solution.isFeasible()) {
				dureeV1_M = solution.getPath().getMinimumTravelTime();
			} else {
				return false;
			}
				
			data = new ShortestPathData(graph, posV2, noeudCourant, inspectorFastest);
			AStar = new AStarAlgorithm(dataAStar);
			solution = AStar.run();
				
			if (solution.isFeasible()) {
				dureeV2_M = solution.getPath().getMinimumTravelTime();
			} else {
				return false;
			}
			
					
			if (similaire_15_pourcent(dureeV1_M, dureeV2_M)) {
				return true;
			} else {				
				return false;
			}
			
		}
		
		//pour vérifier que le noeud courant respecte la condition de distance
		protected boolean verifie_distance(int i) {
			double distanceV1_M;
			double distanceV2_M;
			Node noeudCourant = graph.get(i);
			//data des A*		
			ShortestPathData dataAStar = new ShortestPathData(graph, posV1, noeudCourant, inspectorShortest);

			AStarAlgorithm AStar = new AStarAlgorithm(dataAStar);
			ShortestPathSolution solution = AStar.run();
			if (solution.isFeasible()) {
				distanceV1_M = solution.getPath().getLength();
			} else {
				return false;
			}
				
			data = new ShortestPathData(graph, posV2, noeudCourant, inspectorShortest);
			AStar = new AStarAlgorithm(dataAStar);
			solution = AStar.run();
				
			if (solution.isFeasible()) {
				distanceV2_M = solution.getPath().getLength();
			} else {
				return false;
			}
			
			if (similaire_15_pourcent(distanceV1_M, distanceV2_M)) {
				return true;
			} else {				
				return false;
			}
			
		}
		
		
		protected boolean similaire_15_pourcent(double d1, double d2) {
			if ((d1 < d2 + d2*0.15) && (d1 > d2 - d2*0.15)) {
				return true;
			} else {
				return false;
			}
			
		}
		
		protected boolean similaire_30_pourcent(double d1, double d2) {
			if ((d1 < d2 + d2*0.3) && (d1 > d2 - d2*0.3)) {
				return true;
			} else {
				return false;
			}
			
		}
		
		
}
