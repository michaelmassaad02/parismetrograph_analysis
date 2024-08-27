import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Implementation by: Michael Massaad
 * Student Number: 300293612
 * This class represents the implementation of the graph for the 
 * Paris metro network. The implementation of the adjacency map
 * provided in the AdjacencyMapGraph.java file from Lab 8 was used as guidance. Chat GPT was also used to 
 * understand issues that arose during implementation, such as exceptions and 
 * errors
 */
public class Graph<V, E>{

    /*
     * The vertices in the graph, contains the stations of the paris metro system
     */
    private LinkedList<Vertex<V>> vertices = new LinkedList<>();

    /*
     * The edges of the graph, contains the connections between stations of the paris metro system
     */
    private LinkedList<Edge<E>> edges = new LinkedList<>();

    // -------- nested vertex class --------

    /*
     * Nested vertex class that contains the information of the vertex/station
     */
    public class Vertex<V>{
        /*
         * "key" of the vertex/station number
         */
        private V element;

        /*
         * name of the station
         */
        private String station;

        /*
         * represents the connections the station/vertex has with other stations/vertex.
         * Since we are representing a directed graph, we split the edges based on the type
         * (incoming or outgoing).
         */
        private ConcurrentHashMap<Vertex<V>, Edge<E>> outgoing, incoming;


        /*
         * Constructs a vertex/station with the station number and name.
         */
        public Vertex(V elem, String stationName){
            element = elem;
            station = stationName;
            outgoing = new ConcurrentHashMap<>();
            incoming = new ConcurrentHashMap<>();
        }

        /*
         * returns the station number
         */
        public V getElement(){
            return element;
        }

        /*
         * returns the station name
         */
        public String getStation(){
            return station;
        }

        /*
         * makes sure that the vertex/station instance belongs to the given graph
         */
        public boolean validate(Graph<V, E> graph) {
			return Graph.this == graph;
        }
        
        /*
         * returns reference to the map of incoming edges for a vertex/station instance
         */
        public ConcurrentHashMap<Vertex<V>, Edge<E>> getIncoming(){
            return incoming;
        }

        /*
         * returns reference to the map of outgoing edges for a vertex/station instance
         */
        public ConcurrentHashMap<Vertex<V>, Edge<E>> getOutgoing(){
            return outgoing;
        }
    }
    // --------- end of vertex class ---------

    // --------- nested Edge class -----------
    /*
     * Nested vertex class that contains the information of the edge between 2 vertices/stations
     */
    public class Edge<E>{
        /*
         * weight of the edge (which is the travel time from one station to another)
         */
        private E element;

        /*
         * vertices/stations connected by the edge instance
         */
        private Vertex<V>[] endpoints;

        /*
         * Constructs an edge/connection instance from u to v, storing the weight of 
         * the edge 
         */
        @SuppressWarnings({"unchecked"})
        public Edge(Vertex<V> u, Vertex<V> v, E elem){
            element = elem;
            endpoints = (Vertex<V>[]) new Vertex[] {u, v};
        }

        /*
         * returns the weight of the edge
         */
        public E getElement(){
            return element;
        }

        /*
         * makes sure that the edge instance belongs to the given graph
         */
        public boolean validate(Graph<V, E> graph) {
			return Graph.this == graph;
        }

        /*
         * returns an array containing the endpoints/stations connected by the edge
         */
        public Vertex<V>[] getEndpoints(){
            return endpoints;
        }

    }
    // -------- end of edge class ---------

    public Graph(){

    }

    /*
     * returns the number of vertices/stations in the graph
     */
    public int numVertices(){
        return vertices.size();
    }

    /*
     * returns the vertices/stationsof the graph as an iterable collection
     */
    public Iterable<Vertex<V>> vertices(){
        return vertices;
    }

    /*
     * returns the number of edges in the graph
     */
    public int numEdges(){
        return edges.size();
    }

    /*
     * returns the edges of the graph as an iterable collection
     */
    public Iterable<Edge<E>> edges(){
        return edges;
    }

    /*
     * returns the edge from u to v, or null if they aren't adjacent
     */
    public Edge<E> getEdge(Vertex<V> u , Vertex<V> v) throws IllegalArgumentException{
        Vertex<V> origin = validate(u);
        return origin.getOutgoing().get(v);
    } 

    /*
     * returns an instance of vertex from the station number, returns null
     * if the station number corresponds to a vertex/station that is not in the graph
     */
    public Vertex<V> getVertex(V stationNumber){
        for(Vertex<V> v:vertices){
            if(v.getElement().equals(stationNumber)){
                return v;
            }
            
        }
        return null;
    }

    /*
	 * Returns the vertices of edge e as an array of length two. The first vertex is the origin, and the second is the destination.
	 */
    public Vertex<V>[] endVertices(Edge<E> e) throws IllegalArgumentException{
        Edge<E> edge = validate(e);
        return edge.getEndpoints();
    }

    /*
     * Returns the vertex/station that is opposite vertex/station v on edge e.
     */
    public Vertex<V> opposite(Vertex<V> v, Edge<E> e) throws IllegalArgumentException{
        Edge<E> edge = validate(e);
        Vertex<V>[] endpoints = edge.getEndpoints();
        if(endpoints[0] == v){
            return endpoints[1];
        }
        else if(endpoints[1] == v){
            return endpoints[0];
        }
        else{
            throw new IllegalArgumentException("v is not incident to this edge");
        }
    }

    /*
     * Returns the number of edges for which vertex/station v is the origin. 
     */
    public int outDegree(Vertex<V> v) throws IllegalArgumentException{
        Vertex<V> vert = validate(v);
        return vert.getOutgoing().size();
    }

    /*
     * Returns the number of edges for which vertex/station v is the destination.
     */
    public int inDegree(Vertex<V> v) throws IllegalArgumentException{
        Vertex<V> vert = validate(v);
        return vert.getIncoming().size();
    
    }

    /*
     * Returns an iterable collection of edges for which vertex/station v is the origin.
     */
    public Iterable<Edge<E>> outgoingEdges(Vertex<V> v ) throws IllegalArgumentException{
        Vertex<V> vert = validate(v);
        return vert.getOutgoing().values();
    }

    /*
     * Returns an iterable collection of edges for which vertex/station v is the
	 * destination.
     */
    public Iterable<Edge<E>> incomingEdges(Vertex<V> v) throws IllegalArgumentException{
        Vertex<V> vert = validate(v);
        return vert.getIncoming().values();
    }

    /*
     * Inserts and returns a new vertex/station with the given element.
     */
    public Vertex<V> insertVertex(V element, String stationName){
        Vertex<V> v = new Vertex<>(element, stationName);
        vertices.addLast(v);
        return v;
    }

    /*
     * Inserts and returns a new edge between vertice/stations u and v, storing given
	 * element/weight.
     */
    public Edge<E> insertEdge(Vertex<V> u, Vertex<V> v, E element) throws IllegalArgumentException{
        if (getEdge(u, v) == null){
            Edge<E> e = new Edge<>(u, v, element);
            edges.add(e);
            Vertex<V> origin = validate(u);
            Vertex<V> dest = validate(v);
            origin.getOutgoing().put(v, e);
            dest.getIncoming().put(u, e);
            return e;
        }else{
            throw new IllegalArgumentException("Edge from u to v exists");
        }
    }
    
    /*
     * Removes a vertex/station and all its incident edges from the graph.
     */
    public void removeVertex(Vertex<V> v) throws IllegalArgumentException{
        Vertex<V> vert = validate(v);

        for(Edge<E> e : vert.getOutgoing().values()){
            removeEdge(e);
        }
        for(Edge<E> e : vert.getIncoming().values()){
            removeEdge(e);
        }

        vertices.remove(vert);
    }

    /*
     * Removes an edge from the graph.
     */
    public void removeEdge(Edge<E> e) throws IllegalArgumentException{
        Edge<E> edge = validate(e);

        Vertex<V>[] verts = (Vertex<V>[]) edge.getEndpoints();
        verts[0].getOutgoing().remove(verts[1]);
        verts[1].getIncoming().remove(verts[0]);

        edges.remove(edge);

    }


    @SuppressWarnings({"unchecked"})
    private Vertex<V> validate(Vertex<V> v){
        if(!(v instanceof Vertex<V>)){
            throw new IllegalArgumentException("Invalid vertex");
        }
        Vertex<V> vert = (Vertex<V>) v;
        if(!(vert.validate(this))){
            throw new IllegalArgumentException("Invalid vertex");
        }
        return vert;
    }

    @SuppressWarnings({ "unchecked" })
	private Edge<E> validate(Edge<E> e) {
		if (!(e instanceof Edge<E>))
			throw new IllegalArgumentException("Invalid edge");
		Edge<E> edge = (Edge<E>) e;
		if (!(edge.validate(this)))
			throw new IllegalArgumentException("Invalid edge");
		return edge;
	}
    
}