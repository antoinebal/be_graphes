package org.insa.algo.utils;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.insa.graph.*;
import org.insa.graph.RoadInformation.RoadType;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.insa.algo.*;
import org.insa.algo.shortestpath.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class DijkstraTest {

	// Small graph use for tests
	private static Graph graph;

	// List of Solutions
	private static double[][] solutions;

	private static ArcInspector inspector;

	// List of nodes
	private static Node[] nodes;

	// List of arcs in the graph, a2b is the arc from node A (0) to B (1).
	@SuppressWarnings("unused")
	private static Arc x1tox2, x1tox3, x2tox4, x2tox5, x2tox6, x3tox1, x3tox2, x3tox6, x5tox3, x5tox4, x5tox6, x6tox5;

	@BeforeClass
	public static void initAll() throws IOException {

		// Create nodes
		nodes = new Node[6];
		for (int i = 0; i < nodes.length; ++i) {
			nodes[i] = new Node(i, null);
		}

		// Create solutions
		solutions = new double[6][6];

		// RoadInformation
		RoadInformation speed = new RoadInformation(RoadType.UNCLASSIFIED, null, true, 36, "");

		// Add arcs...
		x1tox2 = Node.linkNodes(nodes[0], nodes[1], 7, speed, null);
		x1tox3 = Node.linkNodes(nodes[0], nodes[2], 8, speed, null);
		x2tox4 = Node.linkNodes(nodes[1], nodes[3], 4, speed, null);
		x2tox5 = Node.linkNodes(nodes[1], nodes[4], 1, speed, null);
		x2tox6 = Node.linkNodes(nodes[1], nodes[5], 5, speed, null);
		x3tox1 = Node.linkNodes(nodes[2], nodes[0], 7, speed, null);
		x3tox2 = Node.linkNodes(nodes[2], nodes[1], 2, speed, null);
		x3tox6 = Node.linkNodes(nodes[2], nodes[5], 2, speed, null);
		x5tox3 = Node.linkNodes(nodes[4], nodes[2], 2, speed, null);
		x5tox4 = Node.linkNodes(nodes[4], nodes[3], 2, speed, null);
		x5tox6 = Node.linkNodes(nodes[4], nodes[5], 3, speed, null);
		x6tox5 = Node.linkNodes(nodes[5], nodes[4], 3, speed, null);

		graph = new Graph("ID", "", Arrays.asList(nodes), null);

		inspector = ArcInspectorFactory.getAllFilters().get(0);

	}

	@Test
	public void testDijkstraSansBellman() {
		ShortestPathData data;
		DijkstraAlgorithm Dijkstra;
		double ResultatsAttendu[][] = { { -1, 7, 8, 10, 8, 10 }, { 10, -1, 3, 3, 1, 4 }, { 7, 2, -1, 5, 3, 2 },
				{ -1, -1, -1, -1, -1, -1 }, { 9, 4, 2, 2, -1, 3 }, { 12, 7, 5, 5, 3, -1 } };
		// Lancement de Djikstra
		for (int origin = 0; origin < nodes.length; origin++) {
			for (int destination = 0; destination < nodes.length; destination++) {
				data = new ShortestPathData(graph, nodes[origin], nodes[destination], inspector);
				Dijkstra = new DijkstraAlgorithm(data);

				if (origin == destination || !(Dijkstra.run().isFeasible())) {
					solutions[origin][destination] = -1;
				} else {
					solutions[origin][destination] = Dijkstra.run().getPath().getLength();
					// Test des résultats de Dijkstra
					assertEquals(solutions[origin][destination], ResultatsAttendu[origin][destination], 1e-6);
				}
			}
		}
		// Afficher le distancier :
		System.out.println("Distancier sans Bellman comme oracle:");
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				System.out.print(" " + (int) solutions[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	
	
	@Test
	public void testDijkstraAvecBellman() {
		ShortestPathData data;
		DijkstraAlgorithm Dijkstra;
		BellmanFordAlgorithm Bellman;
		// Lancement de Djikstra
		for (int origin = 0; origin < nodes.length; origin++) {
			for (int destination = 0; destination < nodes.length; destination++) {
				data = new ShortestPathData(graph, nodes[origin], nodes[destination], inspector);
				Dijkstra = new DijkstraAlgorithm(data);
				Bellman = new BellmanFordAlgorithm(data);
				if (origin == destination || !(Dijkstra.run().isFeasible())) {
					solutions[origin][destination] = -1;
				} else {
					solutions[origin][destination] = Dijkstra.run().getPath().getLength();
					// Test des résultats de Dijkstra
					assertEquals(solutions[origin][destination], Bellman.run().getPath().getLength(), 1e-6);
				}
			}
		}
		// Afficher le distancier :
		System.out.println("Distancier avec Bellman comme oracle:");
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				System.out.print(" " + (int) solutions[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	@Test
	public void testDijkstraSurCarte() {
		ShortestPathData data;
		DijkstraAlgorithm Dijkstra;
		BellmanFordAlgorithm Bellman;
		List<ArcInspector> inspector = ArcInspectorFactory.getAllFilters();		
        String mapName = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr";
        
        try {
        	GraphReader reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
			Graph graph = reader.read();
			// On test pour tout les filtres 
			for (ArcInspector inspect : inspector) {
				System.out.println("Trying for :" + inspect.toString());
				
				// Scenario hardcodé de 18765 jusqu'à 17389:
				data = new ShortestPathData(graph, graph.get(18765), graph.get(17389), inspect);
				
				Dijkstra = new DijkstraAlgorithm(data);
				Bellman = new BellmanFordAlgorithm(data);
				assertTrue(Dijkstra.run().isFeasible());
				assertTrue(Bellman.run().isFeasible());
				
				//Si BellmanFord et Dijkstra sont faisables
				if(Bellman.run().isFeasible() && Dijkstra.run().isFeasible()) {
					assertEquals(Dijkstra.run().getPath().getLength(), Bellman.run().getPath().getLength(), 1e-6);
				} else {
					System.out.println("Pas de chemin.");
				}
				if(Dijkstra.run().getPath().getLength() == Bellman.run().getPath().getLength()) {
					System.out.println("OK");
				} else {
					System.out.println("NON OK");
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}
	
	
	@Test
	public void testDijkstraUnfeasible() {
		ShortestPathData data;
		DijkstraAlgorithm Dijkstra;
		ArcInspector inspector = ArcInspectorFactory.getAllFilters().get(1); // inspector = "Shortest path, only roads
																				// open for car."
		String mapName = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr";

		try {
			GraphReader reader = new BinaryGraphReader(
					new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
			Graph graph = reader.read();

			// Scenario hardcodé de 38943 jusqu'à 16290, chemin inaccesible en voiture:
			data = new ShortestPathData(graph, graph.get(38943), graph.get(16290), inspector);

			Dijkstra = new DijkstraAlgorithm(data);
			assertTrue(!Dijkstra.run().isFeasible());

			// Si Dijkstra est faisables
			if (Dijkstra.run().isFeasible()) {
				System.out.println("Unfeasible test : Chemin existant.");
			} else {
				System.out.println("Unfeasible test : Pas de chemin.");
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}
}