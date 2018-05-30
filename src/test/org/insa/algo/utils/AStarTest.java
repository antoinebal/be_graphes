package org.insa.algo.utils;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.insa.graph.*;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.insa.algo.*;
import org.insa.algo.shortestpath.*;
import org.junit.Test;

public class AStarTest {
	@Test
	public void testAStarSurCarte() {
		ShortestPathData data;
		AStarAlgorithm AStar;
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
				
				AStar = new AStarAlgorithm(data);
				Bellman = new BellmanFordAlgorithm(data);
				assertTrue(AStar.run().isFeasible());
				assertTrue(Bellman.run().isFeasible());
				
				//Si BellmanFord et AStar sont faisables
				if(Bellman.run().isFeasible() && AStar.run().isFeasible()) {
					assertEquals(AStar.run().getPath().getLength(), Bellman.run().getPath().getLength(), 1e-6);
				} else {
					System.out.println("Pas de chemin.");
				}
				if(AStar.run().getPath().getLength() == Bellman.run().getPath().getLength()) {
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
	public void testAStarUnfeasible() {
		ShortestPathData data;
		AStarAlgorithm AStar;
		ArcInspector inspector = ArcInspectorFactory.getAllFilters().get(1); // inspector = "Shortest path, only roads
																				// open for car."
		String mapName = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr";

		try {
			GraphReader reader = new BinaryGraphReader(
					new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
			Graph graph = reader.read();

			// Scenario hardcodé de 38943 jusqu'à 16290, chemin inaccesible en voiture:
			data = new ShortestPathData(graph, graph.get(38943), graph.get(16290), inspector);

			AStar = new AStarAlgorithm(data);
			assertTrue(!AStar.run().isFeasible());

			// Si AStar est faisables
			if (AStar.run().isFeasible()) {
				System.out.println("Unfeasible test : Chemin existant.");
			} else {
				System.out.println("Unfeasible test : Pas de chemin.");
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}
}