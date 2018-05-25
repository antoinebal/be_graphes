package org.insa.algo.shortestpath;

import org.insa.graph.Node;

public class LabelStar extends Label implements Comparable<Label>{

	//coût du parcours de l'origine jusqu'à ce noeud
	private double cout_origine_;
	
	//coût estimé de ce noeud jusqu'à la destination
	private double cout_estime_dest_;
	
	public LabelStar(boolean marked, double cout_origine, double cout_estime_dest, Node pere, int id) {
		super(marked, cout_origine + cout_estime_dest, pere, id);
		cout_origine_ = cout_origine;
		cout_estime_dest_ = cout_estime_dest;	
	}
	
	//màj du coût total
	void setCout(double cout_origine, double cout_estime_dest) {
		cout_origine_ = cout_origine;
		cout_estime_dest_ = cout_estime_dest;
		setCout(cout_origine_ + cout_estime_dest_);
	}
	
	//màj du coût d'origine
	void setCoutOrigine(double cout_origine) {
		cout_origine_ = cout_origine;
		setCout(cout_origine_ + cout_estime_dest_);
	}
	

	public LabelStar(int id) {
		super(false, Double.POSITIVE_INFINITY, null, id);
	}
	
	
	public double getCoutEstimeDest() { return cout_estime_dest_; }
	
	public double getCoutOrigine() { return cout_origine_; }
	
	/*on redéfinit la fonction compareTo
	conformément à l'algo A*/
	public int compareTo(LabelStar o) { 
		if (this.getCout() < o.getCout()) {
			return -1;
		} else if  (this.getCout() > o.getCout()){
			return 1;
		} else { //si les deux coûts sont égaux
			
			if (this.getCoutEstimeDest() < o.getCoutEstimeDest()) {
				return -1;
			} else {
				return 1;
			}
		}
		
	}
	
}