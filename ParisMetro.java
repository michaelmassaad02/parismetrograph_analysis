import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Collections;

/*
 * Implementation by: Michael Massaad
 * Student Number: 300293612
 * This class deals with the operations for the Paris subway network. 
 * The implementations of the graph algorithms and reading from a text file
 * were provided in the GraphAlgorithms.java and the WeightGraph.java file from lab 8 were used as 
 * guidance for the implementations in this file. Chat GPT was also used to 
 * understand different errors/exceptions that arose during the creation of this file.
 */
public class ParisMetro{

    private static Graph<Integer,Integer> graphSystem;

    /*
     * creates an instance of ParisMetro from a file name
     */
    public ParisMetro(String fileName) throws Exception, IOException{
        graphSystem = new Graph<Integer, Integer>();
        readMetro(fileName);

    }

    /*
     * Reads the file to populate the graph with the stations/vertices and the edges/connections
     */
    public static void readMetro(String fileName) throws Exception, IOException{
        BufferedReader graphFile = new BufferedReader(new FileReader(fileName));
        
        //stores all the vertices/stations read in a hashmap
        Hashtable<Integer, Graph<Integer, Integer>.Vertex<Integer>> vertices = new Hashtable<Integer, Graph<Integer, Integer>.Vertex<Integer>>();

        String line;
        //reads the first line which contains the number of stations and edges in the graph
        line = graphFile.readLine();
        StringTokenizer st = new StringTokenizer(line);
        int numberV = Integer.parseInt(st.nextToken());
        int numberE = Integer.parseInt(st.nextToken());
        line = graphFile.readLine();

        //reading the vertices/stations that are in the file and storing in the graph as a vertex
        while(!(line.startsWith("$"))){
            st = new StringTokenizer(line);
            Integer vertexNum = Integer.parseInt(st.nextToken());
            String name = st.nextToken();
            while(st.hasMoreTokens()){
                name += (" "+ st.nextToken());
            }
            Graph<Integer, Integer>.Vertex<Integer> sv = vertices.get(vertexNum);
            if(sv == null){ //if the vertex isn't stored in the graph already
                sv = graphSystem.insertVertex(vertexNum, name);
                vertices.put(vertexNum, sv);
            }
            line = graphFile.readLine();
        }

        //reading the edges that are in the file and storing in the graph as an edge
        line = graphFile.readLine();
        while(line != null){
            st = new StringTokenizer(line);
            int source = Integer.parseInt(st.nextToken());
            int dest = Integer.parseInt(st.nextToken());
            int weight = Integer.parseInt(st.nextToken());
            
            Graph<Integer, Integer>.Vertex<Integer> sv = vertices.get(source);
            Graph<Integer, Integer>.Vertex<Integer> dv = vertices.get(dest);

            if(graphSystem.getEdge(sv, dv) == null){ // if the edge isn't stored in the graph already
                    Graph<Integer, Integer>.Edge<Integer> e = graphSystem.insertEdge(sv, dv, weight);
            }
            line = graphFile.readLine();
        }
        graphFile.close();
    }

    /*
     * Performs DFS starting at vertex/station u to find all the vertices/stations
     * on the same line. It adds the newly discovered vertices/stations (after adding u already)
     * to the stationsVisited list.
     */
    public static void DFS( Graph<Integer,Integer> g, Graph<Integer,Integer>.Vertex<Integer> u,ArrayList<Graph<Integer,Integer>.Vertex<Integer>> stationsVisited){
        if(!stationsVisited.contains(u)){
        stationsVisited.add(u); //storing u in the discovered vertex/station list if its not present already
        }
        for(Graph<Integer, Integer>.Edge<Integer> e : g.outgoingEdges(u)){
            if(e.getElement() != -1){ // if the edge weight is -1, that means the opposing vertex/station isnt part of the same line
                Graph<Integer,Integer>.Vertex<Integer> v = g.opposite(u,e);
                if(!stationsVisited.contains(v)){
                    stationsVisited.add(v);
                    DFS(g, v, stationsVisited); //recursive call to the DFS from the next vertex/station
                }
            }
        }
    }

    /*
     * this nested class represents the vertex/stations with their distance from
     * a source vertex/station, this is used during the implementation of 
     * Dijkstra's Algorithm
     */
    public static class PQEntry{
        Graph<Integer, Integer>.Vertex<Integer> vertex;
        int distance;

        public PQEntry(Graph<Integer, Integer>.Vertex<Integer> v, int d){
            vertex = v;
            distance = d;
        }

        public Graph<Integer, Integer>.Vertex<Integer> getVertex(){
            return vertex;
        }
        public int getDistance(){
            return distance;
        }

        public void setDistance(int dist){
            distance = dist;
        }

    }

    /*
     * Computes Dijkstra's Algorithm and computes the shortest path from 
     * one given vertex/station to another. The implementation of the
     * shortestPathLengths method from lab 8 and Chat GPT were used as guidance
     * to implement this method.
     */
    public static void shortestPath(Graph<Integer,Integer> g, Graph<Integer,Integer>.Vertex<Integer> u, Graph<Integer,Integer>.Vertex<Integer> v){
        //Stores the current "distance" from vertex/station u to v, we can use d.get(v) to find the distance
        HashMap<Graph<Integer,Integer>.Vertex<Integer>, Integer> d = new HashMap<>();
        // maps the discovered vertex/station v to its final "distance" from u
        HashMap<Graph<Integer,Integer>.Vertex<Integer>, Integer> cloud = new HashMap<>();
        //Stores the vertices/stations with the "distance" from u as the key, so it is sorted by min("distance")
        PriorityQueue<PQEntry> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.getDistance()));
        // maps from vertex/station to the pq locator
        HashMap<Graph<Integer, Integer>.Vertex<Integer>, PQEntry> pqTokens = new HashMap<>();
        //keeps track of the vertices/stations that were travelled in order to construct the "cloud", used to find shortest path from u to v
        HashMap<Graph<Integer, Integer>.Vertex<Integer>, Graph<Integer, Integer>.Vertex<Integer>> predecessors = new HashMap<>();
        //Keeps track of the total time to reach the vertex/station, used to find time taken to traverse shortest path
        HashMap<Graph<Integer, Integer>.Vertex<Integer>, Integer> totalTime = new HashMap<>();

        // for each vertex/station v of the graph, add an entry to the priority queue, with
        // the source having "distance" 0 and all others having infinite distance
        for(Graph<Integer,Integer>.Vertex<Integer> station : g.vertices() ){
            if(station == u){
                d.put(station, 0);
                totalTime.put(station, 0);
            }
            else{
                d.put(station, Integer.MAX_VALUE);
                totalTime.put(station, Integer.MAX_VALUE);
            }
            PQEntry newEntry = new PQEntry(station, d.get(station));
            pq.add(newEntry);
            pqTokens.put(station, newEntry);
        }
        // adding the vertices/stations to the cloud
        while(!pq.isEmpty()){
            PQEntry entry = pq.poll();
            int key = entry.getDistance();
            Graph<Integer,Integer>.Vertex<Integer> vert = entry.getVertex();
            cloud.put(vert, key); //storing the final "distance"
            pqTokens.remove(vert);//removing vert from pq, since it has been stored
            
            for(Graph<Integer,Integer>.Edge<Integer> e: g.outgoingEdges(vert)){
                Graph<Integer,Integer>.Vertex<Integer> op = g.opposite(vert, e);
                if(cloud.get(op) ==null){
                int wgt = e.getElement();

                if(wgt == -1){
                    wgt = 90;
                }                    
                if(d.get(vert) + wgt < d.get(op)){ // edge relaxation to update the "distance"
                    d.put(op, d.get(vert) + wgt);
                    totalTime.put(op, totalTime.get(vert) + wgt);
                    predecessors.put(op, vert);
                    PQEntry opEntry = pqTokens.get(op);
                    pq.remove(opEntry);
                    opEntry.setDistance(d.get(op));
                    pq.add(opEntry);
                }
                
            }
        }
    }
    // calling method to print the shortest path from u to v and the time it takes to traverse
    shortestPathV(u, v, predecessors, totalTime.get(v)); 
    }

    /*
     * Prints the shortest path and the time it takes to traverse from vertex/station u to v
     */
    public static void shortestPathV(Graph<Integer, Integer>.Vertex<Integer> u, Graph<Integer,Integer>.Vertex<Integer> v, HashMap<Graph<Integer,Integer>.Vertex<Integer>, Graph<Integer, Integer>.Vertex<Integer>> predecessors, int time ){
        ArrayList<Graph<Integer,Integer>.Vertex<Integer>> path = new ArrayList<>();
        Graph<Integer,Integer>.Vertex<Integer> current = v;

        while(current != null){ //traversing backwards until we reach the source node, which itself doesnt have a predecessor
            path.add(current); // we add the vertex visited in an arraylist which will allow us to iterate and print the vertices/stations
            current = predecessors.get(current);
        }
        
        Collections.reverse(path);
        System.out.println("Time = " + time);
        System.out.print("Path : " );
        for(Graph<Integer,Integer>.Vertex<Integer> vec : path){
            System.out.print(vec.getElement() + " ");
        }
    }

    /*
     * Execution of the operations of the Paris metro network based on the command line inputs
     */
    public static void main(String[] args){
        if(args.length == 1){
            try{
                ParisMetro graph = new ParisMetro("metro.txt");
                Graph<Integer, Integer>.Vertex<Integer> vert = graphSystem.getVertex(Integer.parseInt(args[0]));
                ArrayList<Graph<Integer,Integer>.Vertex<Integer>> stationsVisited = new ArrayList<Graph<Integer,Integer>.Vertex<Integer>>();
                stationsVisited.add(vert); 
                DFS(graphSystem, vert, stationsVisited);
        
                System.out.print("Line: ");
                for(Graph<Integer,Integer>.Vertex<Integer> v : stationsVisited){
                    System.out.print(v.getElement() + " ");
                }
            }
            catch(Exception except){
                System.err.print(except);
            }
        }
        else if(args.length == 2){
            try{
                ParisMetro graph = new ParisMetro("metro.txt");
                Graph<Integer, Integer>.Vertex<Integer> vert1 = graphSystem.getVertex(Integer.parseInt(args[0]));
                Graph<Integer, Integer>.Vertex<Integer> vert2 = graphSystem.getVertex(Integer.parseInt(args[1]));

                shortestPath(graphSystem, vert1, vert2);

            }catch(Exception except){
                System.err.print(except);
            }
        }
        else if(args.length == 3){
            try{
                ParisMetro graph = new ParisMetro("metro.txt");
                Graph<Integer, Integer>.Vertex<Integer> vert1 = graphSystem.getVertex(Integer.parseInt(args[0]));
                Graph<Integer, Integer>.Vertex<Integer> vert2 = graphSystem.getVertex(Integer.parseInt(args[1]));
                Graph<Integer, Integer>.Vertex<Integer> vert3 = graphSystem.getVertex(Integer.parseInt(args[2]));
                ArrayList<Graph<Integer,Integer>.Vertex<Integer>> stationsNF = new ArrayList<Graph<Integer,Integer>.Vertex<Integer>>();

                stationsNF.add(vert3);

                DFS(graphSystem, vert3, stationsNF); // finding all the stations that are on the line that is no longer functional

                for(Graph<Integer,Integer>.Vertex<Integer> v : stationsNF){
                    graphSystem.removeVertex(v);
                }

                shortestPath(graphSystem, vert1, vert2);

                

            }catch(Exception except){
                System.err.print(except);
            }

        }

    }

    }





