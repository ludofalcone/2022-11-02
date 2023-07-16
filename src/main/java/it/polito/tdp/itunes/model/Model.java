package it.polito.tdp.itunes.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	private ItunesDAO dao;
	private List<String> generi;
	private Graph<Track,DefaultEdge> grafo;
	private List<Track> vertici;
	private Map<Integer, Track> trackidmap;
	
	public Model() {
		dao=new ItunesDAO();
		this.generi=this.dao.getGeneri();
	}
	
	public void creaGrafo(String genere, int min, int max) {
		this.grafo=new SimpleGraph<Track,DefaultEdge>(DefaultEdge.class);
		this.vertici=this.dao.getTracksByMilli(genere, min, max);
		Graphs.addAllVertices(this.grafo, this.vertici);
		this.trackidmap=new HashMap<Integer,Track>();
		for(Track t: this.vertici) {
			trackidmap.put(t.getTrackId(), t);
		}
		List<Arco> archi=this.dao.getArco(genere, min, max);
		Set<Track> verticinuovi=this.grafo.vertexSet();
		List<Arco> archifiltrati=new ArrayList<>(archi);
		for(Arco a: archi) {
			if(!verticinuovi.contains(this.trackidmap.get(a.getT().getTrackId()))) {
				archifiltrati.remove(a);
			}
		}
		for(int i=0; i<archifiltrati.size(); i++) {
			for(int j=i; j<archifiltrati.size(); j++) {
				Track t1=this.trackidmap.get(archifiltrati.get(i).getT().getTrackId());
				Track t2=this.trackidmap.get(archifiltrati.get(j).getT().getTrackId());
				int numpres1= archifiltrati.get(i).getNumpresenze();
				int numpres2= archifiltrati.get(j).getNumpresenze();
				if(!t1.equals(t2) && numpres1==numpres2) {
					this.grafo.addEdge(t1, t2);
				}
			}
		}
	}
	
	public List<String> getGeneri(){
		Collections.sort(this.generi);
		return this.generi;
	}

	public int nVertici() {
		// TODO Auto-generated method stub
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		// TODO Auto-generated method stub
		return this.grafo.edgeSet().size();
	}
	
	public List<Set<Track>> compconnesse() {
		ConnectivityInspector<Track,DefaultEdge> inspector= new ConnectivityInspector<Track, DefaultEdge>(this.grafo);
		return inspector.connectedSets();
	}
	int m;
	
}
