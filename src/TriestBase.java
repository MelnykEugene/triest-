import java.awt.font.TextHitInfo;
import java.util.*;

public class TriestBase implements DataStreamAlgo {
	//Each vertex stores information about its neighbors as well as incident edges


	//edges are stored as hashset
	//vertices are stored as (vertex_number => (neighboors, incidentedges)) map
    private Set<Edge> edges;
    private Map<Integer,Vertex> vertices_info;
    private final int sample_size;
    private int ntriangles;
    private int t=1;


	public TriestBase(int samsize) {
		this.vertices_info = new HashMap<Integer, Vertex>();
		this.edges = new HashSet<>();
		this.sample_size =samsize;
		this.ntriangles=0;
		this.t=0;
	}

	public void handleEdge(Edge new_edge) {
		if (this.t <= this.sample_size){
			this.add_triangles(new_edge);
			this.addEdge(new_edge);
		}
		else{
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

				assert(delete_edge != null);

				this.remove_triangles(delete_edge);
				this.removeEdge(delete_edge);
				this.add_triangles(new_edge);
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

	private void add_triangles(Edge added_edge){
		Set<Integer> neighbors_u = (this.vertices_info.containsKey(added_edge.u) ? this.vertices_info.get(added_edge.u).neighboors : new HashSet<>());
		Set<Integer> neighbors_v = (this.vertices_info.containsKey(added_edge.v) ? this.vertices_info.get(added_edge.v).neighboors : new HashSet<>());
		Set<Integer> intersection = new HashSet<>(neighbors_u);
		intersection.retainAll(neighbors_v);
		//System.out.println("Neighbors of "+Integer.toString(added_edge.u) + ": "+neighbors_u.toString());
		//System.out.println("Neighbors of "+Integer.toString(added_edge.v) + ": "+neighbors_v.toString());
		//System.out.println("Intersection: " + intersection.toString());

		this.ntriangles+= (intersection.size());
	}
	private void remove_triangles(Edge removed_edge){
		Set<Integer> neighbors_u = this.vertices_info.get(removed_edge.u).neighboors;
		Set<Integer> neighbors_v = this.vertices_info.get(removed_edge.v).neighboors;
		Set<Integer> intersection = new HashSet<>(neighbors_u);
		intersection.retainAll(neighbors_v);
		this.ntriangles -= (intersection.size());
	}

	public int getEstimate() {
		if(this.t < sample_size) return  ntriangles;
		else{
			double num = (double) this.sample_size*(this.sample_size-1)*(this.sample_size-2);
			double den = (double) this.t*(this.t-1)*(this.t-2);
			double pi = num/den;
			return (int)(ntriangles/pi);
		}
	}
}

//correct 1353476
//5k: 1804933 1493738 933586 1680455 560151
//40k: 1388102 1371578 1326017