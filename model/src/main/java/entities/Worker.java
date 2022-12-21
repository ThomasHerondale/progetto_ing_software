package entities;

public class Worker {
    private String ID;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String IBAN;

    public Worker(String id, String name, String surname, String phone, String email, String iban) {
        this.ID = id;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.IBAN = iban;
    }

    public String getID() {
        return ID;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIBAN() {
        return IBAN;
    }

    public String getFullName() {
        return name + " " + surname;
    }
}
