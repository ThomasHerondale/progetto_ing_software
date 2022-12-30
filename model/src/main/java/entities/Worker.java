package entities;

import java.util.Objects;

/**
 * Questa classe modella l'oggetto entity {@code <<Worker>>}, il singolo dipendente, con una specifica matricola,
 * il suo nome completo, il suo livello e le sue informazioni di contatto.
 * @apiNote le informazioni riguardo alla nascita e al codice fiscale non sono comprese in questa classe
 */
public class Worker {
    /**
     * La matricola del dipendente.
     */
    private final String id;
    /**
     * Il nome del dipendente.
     */
    private final String name;
    /**
     * Il cognome del dipendente.
     */
    private final String surname;
    /**
     * Il livello del dipendente.
     */
    private final char rank;
    /**
     * Il numero di telefono del dipendente.
     */
    private final String phone;
    /**
     * L'indirizzo e-mail del dipendente.
     */
    private final String email;
    /**
     * L'IBAN del dipendente.
     */
    private final String iban;

    public Worker(String id, String name, String surname, char rank, String phone, String email, String iban) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.rank = rank;
        this.phone = phone;
        this.email = email;
        this.iban = iban;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public char getRank() {
        return rank;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIban() {
        return iban;
    }

    /**
     * Ottiene il nome completo del dipendente.
     * @return nome e cognome del dipendente
     * @apiNote l'effetto è identico al seguente snippet: {@code getName() + " " + getSurname()}
     */
    public String getFullName() {
        return name + " " + surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return Objects.equals(id, worker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", rank=" + rank +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", iban='" + iban + '\'' +
                '}';
    }
}
