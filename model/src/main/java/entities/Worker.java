package entities;

import java.util.Objects;

public class Worker {
    private final String id;
    private final String name;
    private final String surname;
    private final char rank;
    private final String phone;
    private final String email;
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
