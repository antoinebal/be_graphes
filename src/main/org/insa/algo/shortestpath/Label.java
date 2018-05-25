package org.insa.algo.shortestpath;

import org.insa.graph.Node;

public class Label implements Comparable<Label> {
	protected boolean marked_;
	
	protected double cout_;
	
	protected Node pere_;
	
	//id associé au  noeud du label
	protected int id_;
	
	//pour voir si le label est dans le tas
	protected boolean deja_insere;
	
	public Label(boolean marked, double cout, Node pere, int id) {
		marked_ = marked;
		cout_ = cout;
		pere_ = pere;
		id_ = id;
	}
	

	public Label(int id) {
		marked_ = false;
		cout_ = Double.POSITIVE_INFINITY;
		pere_ = null;
		id_ = id;
	}
	
	public double getCout() { return cout_; }
	
	public boolean getMarked() { return marked_; }
	
	public boolean dejaInsere() { return deja_insere; }
	
	public Node getPere() { return pere_; }
	
	public int getID() { return id_; }
	
	public void setCout(double cout) { cout_ = cout; }
	
	public void setInsere(boolean insere) { deja_insere = insere; }
	
	public void mark() { marked_ = true; }
	
	public void setPere(Node pere) { pere_ = pere; }
	
	/*on a besoin de l'implémenter pour utiliser un
	tas de labels. Retourne -1 si this < o, 1 sinon*/
	public int compareTo(Label o) { 
		if (this.getCout() < o.getCout()) {
			return -1;
		} else {
			return 1;
		}
	}
}
