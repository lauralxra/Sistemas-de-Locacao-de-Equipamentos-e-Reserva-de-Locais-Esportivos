package com.lauralima;
package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AlocacaoController {

    @FXML
    private Button AdicionarMaisItens;

    @FXML
    private ChoiceBox<?> ChoicEcolherEequipamento;

    @FXML
    private ChoiceBox<?> ChoiceEscolherClientes;

    @FXML
    private Button EmprestarItens;

    @FXML
    private DatePicker EscolherData;

    @FXML
    private ChoiceBox<?> EscolherLocalLocacao;

    @FXML
    private Spinner<?> EscolherQUanrDeEquipamentos;

    @FXML
    private Button LocarParaUmLocal;

    @FXML
    private Button OnBtnVoltarParaMenu;

    @FXML
    private Spinner<?> SpinnerDuracao;

    @FXML
    private Spinner<?> SpinnerHora;

    @FXML
    private Spinner<?> SpinnerMinutos;

    @FXML
    private TableView<?> TabelaIntesEquipamentos;

    @FXML
    private TableColumn<?, ?> TabelaQuantdeItens;

    @FXML
    private TableColumn<?, ?> TabeladeItens;

    @FXML
    private Button removerItenSelecionado;

    @FXML
    void AdicionarItens(ActionEvent event) {

    }

    @FXML
    void AdicionarMaisItens(ActionEvent event) {

    }

    @FXML
    void EmprestarEquipamento(ActionEvent event) {

    }

    @FXML
    void LocarEquipamentos(ActionEvent event) {

    }

    @FXML
    void VoltarParaMenu(ActionEvent event) {

    }

}
