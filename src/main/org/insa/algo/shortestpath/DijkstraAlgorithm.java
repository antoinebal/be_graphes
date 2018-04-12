package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Collections;

import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.*;
import org.insa.graph.*;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

	public DijkstraAlgorithm(ShortestPathData data) {
		super(data);
	}

	@Override
	protected ShortestPathSolution doRun() {
		ShortestPathData data = getInputData();
		ShortestPathSolution solution = null;
		Graph graph = data.getGraph();

		// INITIALISATION
		// notre tas, vide au début
		BinaryHeap<Label> tas = new BinaryHeap<Label>();

		final int nbNodes = graph.size();

		// Initialisation du tableau de Label
		Label[] labTab = new Label[nbNodes];
		int i;
		for (i = 0; i < nbNodes; i++) {
			labTab[i] = new Label(i);
		}

		labTab[data.getOrigin().getId()].setCout(0);

		tas.insert(labTab[data.getOrigin().getId()]);
		labTab[data.getOrigin().getId()].setInsere(true);

		// Initialize array of predecessors.
		Arc[] predecessorArcs = new Arc[nbNodes];

		// ITERATIONS

		while (!tas.isEmpty()) {

			Label labMin = tas.deleteMin();
			labMin.mark();
			labMin.setInsere(false);

			/* si le label extrait correspond à celui de la destination, on sort */
			// TODO : equalTo dans Label
			if (labMin == labTab[data.getDestination().getId()]) {
				break;
			}

			// on récupère le noeud associé à l'id contenu dans labMin
			Node nodeMin = graph.get(labMin.getID());

			for (Arc arc : nodeMin) {
				double w = data.getCost(arc);
				double oldDistance = labTab[arc.getDestination().getId()].getCout();
				double newDistance = labTab[nodeMin.getId()].getCout() + w;

				if (newDistance < oldDistance) {

					if (labTab[arc.getDestination().getId()].dejaInsere()) {
						// si le label a déjà été inséré, on update en faisant remove + insert
						tas.remove(labTab[arc.getDestination().getId()]);
					}
					labTab[arc.getDestination().getId()].setCout(newDistance);
					labTab[arc.getDestination().getId()].setPere(nodeMin);
					predecessorArcs[arc.getDestination().getId()] = arc;
					tas.insert(labTab[arc.getDestination().getId()]);
				}

			}

		}
		// Destination has no predecessor, the solution is infeasible...
		if (predecessorArcs[data.getDestination().getId()] == null) {
			solution = new ShortestPathSolution(data, Status.INFEASIBLE);
		} else {

			// The destination has been found, notify the observers.
			notifyDestinationReached(data.getDestination());

			// Create the path from the array of predecessors...
			ArrayList<Arc> arcs = new ArrayList<>();
			Arc arc = predecessorArcs[data.getDestination().getId()];
			while (arc != null) {
				arcs.add(arc);
				arc = predecessorArcs[arc.getOrigin().getId()];
			}

			// Reverse the path...
			Collections.reverse(arcs);

			// Create the final solution.
			solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
		}

		return solution;

	}

}
