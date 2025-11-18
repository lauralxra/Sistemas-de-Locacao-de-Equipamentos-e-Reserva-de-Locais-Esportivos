package model;
import java.util.*;
import java.time.LocalDateTime;
public class Locacao {
    public enum StatusLocacao{
        LOCADO,
        ATIVO,
        FINALIZADO,
        CANCELADO,
    }
    private Cliente cliente;
    private Funcionario funcionario;
    private Map<Equipamento, Integer> equipamentos;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private long valor;
    private boolean devolvido;
    private int statusLocacao;
    public Locacao(Cliente cliente, LocalDateTime inicio, LocalDateTime fim, Funcionario funcionario, Map<Equipamento, Integer> equipamentos, StatusLocacao statusLocacao) {
        this.cliente = cliente;
        this.inicio = inicio;
        this.fim = fim;
        this.funcionario = funcionario;
        this.equipamentos = equipamentos;
        StatusLocacao status = StatusLocacao.LOCADO;

    }

    public LocalDateTime getInicio() {
        return inicio;
    }
    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }
    public LocalDateTime getFim() {
        return fim;
    }
    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }
    public Funcionario getFuncionario() {
        return funcionario;
    }
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
    public Map<Equipamento, Integer> getEquipamentos() {
        return equipamentos;
    }
    public void setEquipamentos(Map<Equipamento, Integer> equipamentos) {
        this.equipamentos = equipamentos;
    }
    public ArrayList<Equipamento> getEquipamentosAsList(){
        ArrayList<Equipamento> equipamentos = new ArrayList<>();
        for(Equipamento equipamento : this.equipamentos.keySet()){
            equipamentos.add(equipamento);
        }
        return equipamentos;
    }

    public Cliente getCliente() {

        return cliente;
    }

    public boolean isDevolvido() {
        return devolvido;
    }
    public void setDevolvido(boolean devolvido) {
        this.devolvido = devolvido;
    }
}
