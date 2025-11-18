package model;

public class Cliente {

    private static long counter = 1;

    private long id;
    private String nome;
    private String documento;
    private String email;
    private String telefone;

    public Cliente(String nome, String documento, String email, String telefone) {
        this.id = counter++;
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.telefone = telefone;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Cliente: " + nome +
                " | Documento: " + documento +
                " | Email: " + email +
                " | Telefone: " + telefone;
    }
}


