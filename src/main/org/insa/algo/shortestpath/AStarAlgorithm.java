package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Collections;

import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;
import org.insa.graph.Point;
import org.insa.algo.AbstractInputData;

public class AStarAlgorithm extends ShortestPathAlgorithm {


	public AStarAlgorithm(ShortestPathData data) {
		super(data);
	}

	@Override
	protected ShortestPathSolution doRun() {

		// Retrieve the graph.
		ShortestPathData data = getInputData();
		ShortestPathSolution solution = null;
		Graph graph = data.getGraph();

		// INITIALISATION
		// Notre tas, vide au début
		BinaryHeap<Label> tas = new BinaryHeap<Label>();
		boolean Fin = false; // Booléen qui stoppe la boucle si la destination est sortie du tas
		final int nbNodes = graph.size();

		// Initialisation du tableau de LabelStar (on n'insère que l'origine par souci de performance)
		LabelStar[] labTab = new LabelStar[nbNodes];
		labTab[data.getOrigin().getId()]=new LabelStar(true, 0, 0, null, data.getOrigin().getId());

		tas.insert(labTab[data.getOrigin().getId()]);
		labTab[data.getOrigin().getId()].setInsere(true);
		
		// Notify observers about the first event (origin processed).
		notifyOriginProcessed(data.getOrigin());

		// Initialize array of predecessors.
		Arc[] predecessorArcs = new Arc[nbNodes];

		// ITERATIONS
		//int i = 0;
		while (!tas.isEmpty() && !Fin) {
			//i++;

			/*normalement le compareTo de LabelStar
			 * devrait être appelé.
			 */
			LabelStar labMin = (LabelStar)tas.deleteMin();

			labMin.mark();
			labMin.setInsere(false);

			/* si le label extrait correspond à celui de la destination, on sort */
			// TODO : equalTo dans Label
			if (labMin == labTab[data.getDestination().getId()]) {
				Fin = true;
			}

			// on récupère le noeud associé à l'id contenu dans labMin
			Node nodeMin = graph.get(labMin.getID());
			Point pointDest = data.getDestination().getPoint();
			//System.out.println("Coût noeud extrait du tas : "+(labTab[nodeMin.getId()].getCout()));

			for (Arc arc : nodeMin) {
				if(data.isAllowed(arc)) {
				double w = data.getCost(arc);

				//si le noeud n'a jamais été visité
				if (labTab[arc.getDestination().getId()] == null) {
					Point pointCourant = arc.getDestination().getPoint();
					double vol_oiseau = pointDest.distanceTo(pointCourant);
					//System.out.println("vol oiseau :"+vol_oiseau);
					
					//si on cherche le FASTEST PATH, il faut changer vol_oiseau en durée
					if (data.getMode() == AbstractInputData.Mode.TIME) {
					/*comme on cherche une borne inférieure, on divise par la vitesse
					 * la plus élevée existante dans le graohe : 130 km/h (~36 m/s)
					 */
						double vitesse_max = 130000.0/3600.0;
						vol_oiseau /= vitesse_max;
					}
					/*si on cherche le SHORTEST PATH, pas besoin de changer le vol d'oiseau
					c'est déjà une borne inférieure car c'est la plus petite distance
					entre le node courant et la destination.*/
					
					//on remplit labtab avec le label star
					labTab[arc.getDestination().getId()] = new LabelStar(false, (labTab[nodeMin.getId()].getCoutOrigine() + w), vol_oiseau,
					nodeMin, arc.getDestination().getId());
					predecessorArcs[arc.getDestination().getId()] = arc;
					
					tas.insert(labTab[arc.getDestination().getId()]);
					notifyNodeReached(arc.getDestination());
					//System.out.println("Ajouté dans tas : "+labTab[nodeMin.getId()].getCout() + w);
				
				//si le noeud n'a jamais été visité		
				} else {
					//on compare les coûts totaux
					double oldDistance = labTab[arc.getDestination().getId()].getCoutOrigine();
					double newDistance = labTab[nodeMin.getId()].getCoutOrigine() + w;
					if (newDistance < oldDistance) {
						tas.remove(labTab[arc.getDestination().getId()]);
						//on màj uniquement le cout d'origine, qui devient donc le cout d'origine de nodemin + le cout de l'arc
						double newCoutOrigine = labTab[nodeMin.getId()].getCoutOrigine() + w;
						labTab[arc.getDestination().getId()].setCoutOrigine(newCoutOrigine);
						labTab[arc.getDestination().getId()].setPere(nodeMin);
						predecessorArcs[arc.getDestination().getId()] = arc;
						tas.insert(labTab[arc.getDestination().getId()]);
						//System.out.println("MAJ dans tas : "+labTab[nodeMin.getId()].getCout() + w);
					}
				}
				}
			}
			
		}
		
		// Destination has no predecessor, the solution is infeasible...
		if (predecessorArcs[data.getDestination().getId()] == null)

		{
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
			//System.out.println("Nbre itérations : "+i+" ; nombre d'arcs : "+arcs.size());
			
			
			// Reverse the path...
			Collections.reverse(arcs);

			// Create the final solution.
			solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
		}

		
		return solution;

	}

}
