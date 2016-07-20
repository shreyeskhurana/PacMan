package entrants.pacman.username;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.*;

import entrants.pacman.username.Node;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */

public class MyPacMan extends PacmanController {
private static final Node NULL = null;

//###########################   DATA MEMBERS   ###########################    
	private MOVE myMove = MOVE.NEUTRAL;
    			 
    public static int pos = 0, count = 0;
    
//########################### MEMBER FUNCTIONS ###########################  
//D.F.S. Algorithm
    public MOVE DFS(Game game, long timeDue) {
    	//Pacman's position at the moment
    	int pacman_pos = game.getPacmanCurrentNodeIndex();
        
    	//Check for repeated moves to prevent 
    	if(Math.abs(pos - pacman_pos)<=7){
    		if(count>=10) {
    			count = 0;
    			return randomMove();
    		}
    		else{
    			count++;
    			pos = pacman_pos;
    		}
    	}
    	else{
    		pos = pacman_pos;
    	}
    	
    	//Retrieve all active pills in the game 
    	int[] pills = game.getActivePillsIndices();
    	
    	if(pills==null){
    		return MOVE.NEUTRAL;
    	}
    	
    	//Initialize stack 's' to backtrack during DFS 
        Stack<Integer> s = new Stack<Integer>();        
        
        //Initialize set 'visited' to avoid repetition
        Set<Integer> visited = new HashSet<Integer>();
        
        //Aim for the "nearest pill" as the GOAL
        int goal = getNearestNeighbor(pills, pacman_pos, game);
        
        //The coordinates of Pacman's position 
        int pacman_x = game.getNodeXCood(pacman_pos),
        	pacman_y = game.getNodeYCood(pacman_pos);
        
        //Retrieve the neighboring nodes from the pacman's position
        int[] first_neighbors = game.getNeighbouringNodes(pacman_pos);
        
        //Temporary coordinates to track position of nodes during DFS
        int x,y;
        
        //To keep track of position (index) of current node in DFS
        int curr_pos = pacman_pos;
        
        //Line of sight from pacman to the goal
        Color color = new Color(0,255,255);
        GameView.addLines(game, color, curr_pos, goal);
        
        //DEPTH FIRST SEARCH
        //For each neighbor do:
        for(int i = 0; i<first_neighbors.length; i++){
        	//Retrieve coordinates of each neighbor
        	x = game.getNodeXCood(first_neighbors[i]);
        	y = game.getNodeYCood(first_neighbors[i]);
            
        	//Decide direction of movement
        	myMove = setMove(pacman_x, pacman_y, x, y);
        	
        	//Push current neighbor to Stack
        	s.push(first_neighbors[i]);
            
        	//Carry out depth first traversal till goal is achieved
        	while(curr_pos!=goal){
        		//Assign current position as the top of stack
        		curr_pos = s.pop();
        		
        		//Mark as visited
        		visited.add(curr_pos);
        	
        		//Retrieve neighbors of current node
        		int[] neighbors = game.getNeighbouringNodes(curr_pos);

        		for(Integer value:neighbors){
        			if(!visited.contains(value)){
        				s.push(value);
        			}
        		}
        	}
        	//Return move if goal achieved
        	if(curr_pos==goal){
        		return myMove;
        	}
        	
        	s.clear();
        }
        
        //else return previous move
        return MOVE.NEUTRAL;
	}

//A* Algorithm
    public MOVE A_STAR(Game game, long timeDue) {
      //Pacman's position at the moment (Source of Search)
    	int pacman_pos = game.getPacmanCurrentNodeIndex();
        System.out.println("\n"+"NEW"+"\n"+"PAC INDX: "+pacman_pos);
    	
      //Retrieve ALL active pills in the game
    	int[] pills = game.getActivePillsIndices();
    	
    	if(pills == null){
    		return MOVE.NEUTRAL;
    	}
    	
      //Aim for the "nearest pill" as the GOAL
        int goal = getNearestNeighbor(pills, pacman_pos, game);
        System.out.print("GOAL INDX: "+goal+": "); printxy(goal,game);
        
        double initial_dis = game.getEuclideanDistance(pacman_pos, goal);
        
        Node pacman = new Node(pacman_pos, NULL, initial_dis);
        Node current = pacman;
    	
      //Initialize a priority queue 'pq' to track the optimal node 
        PriorityQueue<Node> pq = new PriorityQueue<Node>();        
        
      //Initialize set 'visited' to avoid repetition
        Set<Integer> visited = new HashSet<Integer>();
        
      //Insert the source node for A* 
        pq.add(pacman);
        visited.add(pacman.index);
        
      //Line of sight from pacman to the goal
        Color color = new Color(0,255,255);
        GameView.addLines(game, color, pacman_pos, goal);
        
        int depth = 0;
        
      //A* SEARCH
        while(!pq.isEmpty()){
        	current = pq.remove();
        	System.out.println(current.index+": "); printxy(current.index,game);
        	
        	if(current.index == goal){
        		System.out.println("Yei!");
        		break;
        	}
        	int[] children = game.getNeighbouringNodes(current.index);
        	depth++;
        	for(int i = 0; i < children.length; i++){
        		Node temp = new Node(children[i], 
        							 current, 
        							 depth + game.getEuclideanDistance(pacman_pos, goal));
        		
        		if(!visited.contains(temp.index)){
        			pq.add(temp);
        			visited.add(temp.index);
        		}
        		else
        			continue;
        	}
        }
        
        int next_index;
        
      //Create a function to trace back to the parent
        while(current!=NULL){
        	current = current.parent;
        	
        	if(current.parent == pacman){
        		next_index = current.index;
        		
        		System.out.println("Yay!");
        		
        		return setMove(game.getNodeXCood(pacman_pos),
        					   game.getNodeYCood(pacman_pos),
        					   game.getNodeXCood(next_index),
        					   game.getNodeYCood(next_index));
        	}
        }
        
        return myMove;
    }
    
//Print coordinates
    void printxy(int index, Game game){
    	System.out.println("("+game.getNodeXCood(index)+", "+game.getNodeYCood(index)+")");
    }

//Random move generator
    MOVE randomMove() {
    	Random rand = new Random();
    	int num = rand.nextInt(4);
    	
    	if(num == 0)
    		lastMove = MOVE.LEFT;
    	else if(num == 1)
    		lastMove = MOVE.RIGHT;
    	else if(num == 2)
    		lastMove = MOVE.UP;
    	else{
    		lastMove =  MOVE.DOWN;
    	}
    	
    	return lastMove;
    }
    
    static void shuffleArray(int[] ar)
    {
      // If running on Java 6 or older, use `new Random()` on RHS here
      Random rnd = ThreadLocalRandom.current();
      for (int i = ar.length - 1; i > 0; i--)
      {
        int index = rnd.nextInt(i + 1);
        // Simple swap
        int a = ar[index];
        ar[index] = ar[i];
        ar[i] = a;
      }
    }
    
//Find and return the nearest neighbor of the
    public int getNearestNeighbor(int[] pills, int pos, Game game){
    	
    	int min = 32767,
    		index=-1,
    		dis;
    	
    	for(int i=0; i<pills.length; i++){
    		dis = game.getShortestPathDistance(pos,pills[i]);
    		if(dis<min){
    			index = i;
    			min = dis;
    		}
    	}
    	
    	return pills[index];
    }

//A function that sets (returns) the move depending upon the direction
//  of Depth First Traversal
    public MOVE setMove(int xp, int yp, int xn, int yn){
    	if(xp>xn){
    		System.out.println("Should go Left");
    		return MOVE.LEFT;
    	}
    	else if(xp<xn){
        		System.out.println("Should go Right");
        		return MOVE.RIGHT;
        	}
    	else if(yp>yn){
    		System.out.println("Should go Up");
    		return MOVE.UP;
    	}
    	else{
    		System.out.println("Should go Down");
    		return MOVE.DOWN;
    	}
    		
    }
    
//Function call to calculate Pacman's next move
	public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
    	try{	
			//myMove = DFS(game,timeDue);
    		myMove = A_STAR(game,timeDue);
			return myMove;

    	}
    	catch (Exception e){
			return MOVE.NEUTRAL;
    	}
    }

//##########################################################################
}