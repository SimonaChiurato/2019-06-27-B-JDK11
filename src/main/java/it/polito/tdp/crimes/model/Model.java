package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	EventsDao dao;
	SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	List<String> best;
	
	public Model() {
		this.dao= new EventsDao();
	}
	
	public List<Integer> listAllMonth(){
		return this.dao.listAllMonth();
	}
	public List<String> listAllCategory(){
		return this.dao.listAllCategory();
	}
	
	public void creaGrafo(int mese, String categoria) {
		this.grafo= new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, this.dao.listAllVertici(mese, categoria));
		for(Adiacenza a: this.dao.listAlladiacenze(mese, categoria)) {
			if(grafo.containsVertex(a.getId1()) && grafo.containsVertex(a.getId2())) {
			Graphs.addEdgeWithVertices(grafo, a.getId1(), a.getId2(), a.getPeso());
			}
		}
	}
	public  Set<String> vertici() {
		return this.grafo.vertexSet();
		}
		public  Set<DefaultWeightedEdge> archi() {
		return this.grafo.edgeSet();
		}
	public List<Adiacenza> pesoMaggioreMedia(){
		List<Adiacenza> result= new ArrayList<>();
		double somma=0.0;
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			somma+=grafo.getEdgeWeight(e);
		}
		somma= somma/this.grafo.edgeSet().size();
		result.add(new Adiacenza(null,null,somma));
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(grafo.getEdgeWeight(e)>somma) {
			result.add(new Adiacenza(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
			}
		}
		return result;
	}
	public List<String> cammino( DefaultWeightedEdge e){
		List<String> parziale= new ArrayList<>();
		parziale.add(grafo.getEdgeSource(e));
		best= new ArrayList<>();
		this.cerca(e,parziale);
		return best;
		
	}

	private void cerca(DefaultWeightedEdge e, List<String> parziale) {
		
		if(parziale.get(parziale.size()-1).equals(grafo.getEdgeTarget(e))) {
			if(parziale.size()>this.best.size()) {
				best= new ArrayList<>(parziale);
			}
		}
		
		List<String> vicini= Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1));
		for(String s: vicini) {
			if(!parziale.contains(s)) {
				parziale.add(s);
				cerca(e,parziale);
				parziale.remove(parziale.size()-1);
			}
		}
	}
}
