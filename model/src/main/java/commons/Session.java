package commons;

import entities.Worker;

/**
 * Questa classe rappresenta una sessione di login all'interno dell'applicazione.
 * Viene istanziata al momento del login e invalidata al momento di logout.
 * La classe è implementata come <i>Singleton</i>, in quanto il login può essere effettuato ovviamente
 * da un solo utente per volta.
 * @apiNote l'invalidazione dell'istanza mediante chiamata a {@link #invalidate()} è cruciale per il corretto
 * funzionamento della sessione
 */
public class Session {
    /**
     * Il dipendente loggato attualmente in questa sessione.
     */
    private Worker worker;
    /**
     * L'istanza di questa sessione, secondo il pattern <i>Singleton</i>.
     */
    private static Session instance;

    /* Costruttore privato per impedire l'istanziazione diretta di questa classe */
    private Session() {}

    /**
     * Ritorna la sessione di login corrente per l'applicazione.
     * @return l'unica istanza possibile di questa classe
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * Invalida la sessione di login in seguito al logout da parte del dipendente.
     */
    public static void invalidate(){
        instance = null;
    }

    /**
     * Modifica i dati del dipendente attualmente loggato, in seguito a una modifica delle sue informazioni
     * da lui richiesta.
     * @param worker le nuove informazioni del dipendente
     */
    public void update(Worker worker){
        this.worker = worker;
    }

    /**
     * Ottiene le informazioni del dipendente attualmente loggato.
     * @return le informazioni del dipendente corrente
     */
    public Worker getWorker(){
        return worker;
    }
}
