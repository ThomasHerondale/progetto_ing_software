package database;

import java.sql.SQLException;

/**
 * Questa eccezione è lanciata ogni qual volta si presenta un errore con la comunicazione al database.
 * @apiNote questa classe è in realtà un semplice wrapper di {@link SQLException}
 */
public class DBMSException extends Exception {
    static final long serialVersionUID = 7204297594101224587L;

    /**
     * Costruisce una {@link DBMSException} usando lo stesso messaggio della {@link SQLException} lanciata dai
     * metodi di accesso al database.
     */
    DBMSException(SQLException e) {
        super(e.getMessage());
    }

}
