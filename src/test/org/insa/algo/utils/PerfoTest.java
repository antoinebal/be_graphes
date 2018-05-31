package org.insa.algo.utils;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.insa.graph.*;

import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.insa.algo.*;
import org.insa.algo.shortestpath.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class PerfoTest {

	private static GraphReader reader;
	private static Graph graph;
	private static ArrayList<int[]> CoordonneesAleatoires = new ArrayList<int[]>();
	private static int Taille = 100;

	@BeforeClass
	public static void initAll() throws IOException {
		String mapName = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/carre-dense.mapgr";

		reader = new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
		graph = reader.read();

		for (int i = 0; i < Taille; i++) {
			int origin = ThreadLocalRandom.current().nextInt(0, graph.size());
			int destination = ThreadLocalRandom.current().nextInt(0, graph.size());
			if (graph.get(origin).getPoint().distanceTo(graph.get(destination).getPoint()) < 5000) {
				i--;
			} else {
				int[] point = { origin, destination };
				CoordonneesAleatoires.add(point);
			}
		}
	}

	@Test
	public void testPerformanceDijsktraAStar() {
		// Initialisation :
		ShortestPathSolution SolutionDijkstra;
		ShortestPathSolution SolutionAStar;
		DijkstraAlgorithm Dijkstra;
		AStarAlgorithm AStar;
		ShortestPathData data;
		Duration TempsDijkstra;
		Duration TempsAStar;
		double DistanceDijkstra = 0;
		double DistanceAStar = 0;
		int NoeudsExploresDijkstra;
		int NoeudsExploresAStar;
		long SommeTempsDijkstra = 0;
		long SommeTempsAStar = 0;
		int SommeNoeudsExploresDijkstra = 0;
		int SommeNoeudsExploresAStar = 0;
		double Erreur = 0;

		List<ArcInspector> inspector = ArcInspectorFactory.getAllFilters();
		// Pour tout les filtres
		for (ArcInspector inspect : inspector) {
			System.out.println("Test pour : " + inspect.toString());
			// Pour toutes les coordonnées aléatoires
			for (int[] point : CoordonneesAleatoires) {
				data = new ShortestPathData(graph, graph.get(point[0]), graph.get(point[1]), inspect);
				Dijkstra = new DijkstraAlgorithm(data);
				AStar = new AStarAlgorithm(data);
				SolutionDijkstra = Dijkstra.run();
				NoeudsExploresDijkstra = Dijkstra.getNombreNoeudsExplores();
				SolutionAStar = AStar.run();
				NoeudsExploresAStar = AStar.getNombreNoeudsExplores();
				TempsDijkstra = SolutionDijkstra.getSolvingTime();
				TempsAStar = SolutionAStar.getSolvingTime();
				
				if (SolutionDijkstra.isFeasible() && SolutionAStar.isFeasible()) {
					DistanceDijkstra = SolutionDijkstra.getPath().getLength();
					DistanceAStar = SolutionAStar.getPath().getLength();
				} else {				
					DistanceDijkstra = 0;
					DistanceAStar = 0;
				}
				if ((Math.abs(DistanceDijkstra - DistanceAStar) / DistanceDijkstra) < 0.3) {
					
					SommeTempsDijkstra += TempsDijkstra.toMillis();
					SommeTempsAStar += TempsAStar.toMillis();

					SommeNoeudsExploresDijkstra += NoeudsExploresDijkstra;
					SommeNoeudsExploresAStar += NoeudsExploresAStar;
				} else {
					Erreur++;
				}
			}
			System.out.println("Nombre de tests : " + Taille);
			System.out.println("Temps total Dijkstra = " + SommeTempsDijkstra + "ms");
			System.out.println("Temps total AStar = " + SommeTempsAStar + "ms");
			System.out.println("Moyenne temps Dijkstra = " + (SommeTempsDijkstra / ((Taille * 6) - Erreur)) + "ms");
			System.out.println("Moyenne temps AStar = " + (SommeTempsAStar / ((Taille * 6) - Erreur)) +"ms");
			System.out.println("Nombre de chemins impossibles : " + Erreur);
			System.out.println("Total des noeuds parcourues par Dijkstra : " + SommeNoeudsExploresDijkstra);
			System.out.println("Total des noeuds parcourues par AStar : " + SommeNoeudsExploresAStar + "\n");
			SommeTempsDijkstra = 0;
			SommeTempsAStar = 0;
			SommeNoeudsExploresDijkstra = 0;
			SommeNoeudsExploresAStar = 0;
			Erreur = 0;
		}

	}
}