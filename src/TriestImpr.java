import java.awt.font.TextHitInfo;
import java.util.*;

public class TriestImpr implements DataStreamAlgo {
	//Each vertex stores information about its neighbors as well as incident edges

	//edges are stored as hashset
	//vertices are stored as (vertex_number => (neighboors, incidentedges)) map
	private Set<Edge> edges;
	private Map<Integer,Vertex> vertices_info;
	private final int sample_size;
	private double estimate;
	private int t=1;


	public TriestImpr(int samsize) {
		this.vertices_info = new HashMap<Integer, Vertex>();
		this.edges = new HashSet<>();
		this.sample_size =samsize;
		this.estimate=0;
		this.t=0;
	}

	public void handleEdge(Edge new_edge) {
		if (this.t <= this.sample_size){
			this.estimate += this.induced_triangles(new_edge);
			this.addEdge(new_edge);
		}
		else{
			double num = (double)(this.t-1)*(this.t-2);
			double den = (double)(this.sample_size)*(this.sample_size-1);
			double nu = num/den;
			this.estimate+= nu *  induced_triangles(new_edge);
			Random rand = new Random();
			if (rand.nextDouble() < ((float)this.sample_size)/(float)this.t){
				//pick a random edge from our hashset to delete
				int size = edges.size();
				int item = new Random().nextInt(size);
				int i = 0;

				Edge delete_edge = null;

				for (Edge edge:this.edges){
					if (item==i) delete_edge = edge;
					i++;
				}


				this.removeEdge(delete_edge);
				this.addEdge(new_edge);
			}
		}
		this.t++;
	}
	private void addEdge(Edge edge){

		this.edges.add(edge);
		Vertex U;
		if(this.vertices_info.containsKey(edge.u)){
			U = this.vertices_info.get(edge.u);
			U.neighboors.add(edge.v);
			U.edges.add(edge);
			this.vertices_info.put(edge.u,U);
		}
		else{
			U = new Vertex(edge.u);
			U.neighboors.add(edge.v);
			U.edges.add(edge);
			this.vertices_info.put(edge.u,U);
		}

		Vertex V;
		if(this.vertices_info.containsKey(edge.v)){
			V = this.vertices_info.get(edge.v);
			V.neighboors.add(edge.u);
			V.edges.add(edge);
			this.vertices_info.put((Integer)edge.v,V);
		}
		else{
			V = new Vertex(edge.v);
			V.neighboors.add(edge.u);
			V.edges.add(edge);
			this.vertices_info.put((Integer)edge.v,V);
		}
	}

	private void removeEdge(Edge to_remove){
		Vertex U = this.vertices_info.get(to_remove.u);
		U.edges.remove(to_remove);
		U.neighboors.remove(to_remove.v);
		this.vertices_info.put(to_remove.u,U);

		Vertex V = this.vertices_info.get(to_remove.v);
		V.edges.remove(to_remove);
		V.neighboors.remove(to_remove.u);
		this.vertices_info.put(to_remove.v,V);

		this.edges.remove(to_remove);
	}

	private int induced_triangles(Edge added_edge){
		Set<Integer> neighbors_u = (this.vertices_info.containsKey(added_edge.u) ? this.vertices_info.get(added_edge.u).neighboors : new HashSet<>());
		Set<Integer> neighbors_v = (this.vertices_info.containsKey(added_edge.v) ? this.vertices_info.get(added_edge.v).neighboors : new HashSet<>());
		Set<Integer> intersection = new HashSet<>(neighbors_u);
		intersection.retainAll(neighbors_v);
		//System.out.println("Neighbors of "+Integer.toString(added_edge.u) + ": "+neighbors_u.toString());
		//System.out.println("Neighbors of "+Integer.toString(added_edge.v) + ": "+neighbors_v.toString());
		//System.out.println("Intersection: " + intersection.toString());

		return intersection.size();
	}

	public int getEstimate() {
		return ((int)this.estimate);
	}
}

//correct 1353476
//40k: 1345758 1348218 1366498
