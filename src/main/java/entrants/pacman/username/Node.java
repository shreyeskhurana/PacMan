package entrants.pacman.username;

public class Node implements Comparable<Node> {

	public int index;
	public Node parent;
	public double distance;
	
  //Default constructor
	public Node(){
		index = 0;
		distance = 0.00;
	}
	
  //Constructor to initialize Node object
	public Node(int i, Node p, double dis){
		index = i;
		parent = p;
		distance = dis;
	}
	
  //Compare itself to another object on the basis of 'distance'
	@Override
	public int compareTo(Node o) {
		return (distance<o.distance)? 1 : (distance>o.distance)? -1 : 0;
	}
}