import java.util.HashSet;
import java.util.Set;

public class Vertex{
    public Set<Integer> neighboors;
    public Set<Edge> edges;
    public int n;
    public Vertex(int n){
        this.neighboors=new HashSet<>();
        this.edges=new HashSet<>();
        this.n=n;
    }
    public String toString(){
        return ("Neighbors "+ this.neighboors.toString() + "; Edges "+this.edges.toString());
    }
}