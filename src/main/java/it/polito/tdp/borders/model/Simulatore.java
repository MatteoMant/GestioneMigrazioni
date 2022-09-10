package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {

	// Coda degli eventi
	private PriorityQueue<Event> queue;
	
	// Parametri di simulazione
	private int nInizialeMigranti;
	private Country nazioneIniziale;
	
	// Output della simulazione 
	private int nPassi; // variabile T
	private Map<Country, Integer> persone; // per ogni nazione, quanti migranti sono stanziali in quella nazione
	//oppure: List<CountryAndNumber> personeStanziali;
		
	// Stato del mondo simulato 
	private Graph<Country, DefaultEdge> grafo;
	// Map persone Country -> Integer
	
	public Simulatore(Graph<Country, DefaultEdge> grafo) {
		super();
		this.grafo = grafo;
	}
	
	// metodo che prepara la simulazione
	public void init(Country partenza, int migranti) {
		this.nazioneIniziale = partenza;
		this.nInizialeMigranti = migranti;
		
		this.persone = new HashMap<Country, Integer>(); // ogni volta che viene fatta una simulazione la mappa viene resettata
		for (Country c : this.grafo.vertexSet()) {
			this.persone.put(c, 0); // all'inizio gli stanziali nei vari paesi sono pari a 0
		}
		
		this.queue = new PriorityQueue<>();
		// adesso, prima che la simulazione parta, dobbiamo inserire il primo evento perchè se la coda non contiene
		// neppure un elemento la simulazione non può partire
		this.queue.add(new Event(1, this.nazioneIniziale, this.nInizialeMigranti));	
	}
	
	public void run() {
		while (!this.queue.isEmpty()) { // ci fermiamo quando la coda si esaurisce
			Event e = this.queue.poll(); // estre il primo evento dalla coda
			// System.out.println(e);
			processEvent(e); // elabora l'evento ed aggiorna lo stato del mondo , creando nuovi eventi
		}
	}

	private void processEvent(Event e) {
		int stanziali = e.getPersone() / 2; // quelli che diventano stanziali subito sono pari alla metà
		int migranti = e.getPersone() - stanziali;
		int confinanti = this.grafo.degreeOf(e.getNazione()); // ottengo le nazioni adiacenti del vertice in questione
		int gruppiMigranti = migranti / confinanti; // confinanti deve essere diverso da 0 
		stanziali += migranti % confinanti; // aggiungo a stanziali il resto di questa divisione (richiesta del problema)
		
		this.persone.put(e.getNazione(), this.persone.get(e.getNazione()) + stanziali); // stiamo aggiornando lo stato del mondo
		this.nPassi = e.getTime(); // quando elaboro un evento mi salvo il tempo (che ogni volta aggiorno e quindi ottengo l'ultimo)
		// vado a prendere la lista dei vertici vicini al vertice in questione 
		List<Country> vicini = Graphs.neighborListOf(this.grafo, e.getNazione());
		// a partire da questa lista, possiamo iterare su ciascun elemento e creare per ognuno di essi un evento
		if(gruppiMigranti != 0) {
			for (Country vicino : vicini) { // vicino = nazione verso cui mi sposto
				this.queue.add(new Event(e.getTime()+1, vicino, gruppiMigranti)); // predispongo gli eventi futuri
			}
		}
		
	}

	public int getnPassi() {
		return nPassi;
	}

	public Map<Country, Integer> getPersone() {
		return persone;
	}
	
}
