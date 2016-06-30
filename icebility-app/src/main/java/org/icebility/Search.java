package org.icebility;

import java.util.logging.Logger;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Search {


	static Logger LOG = Logger.getLogger(Search.class.getName());
	private Stage stage;
	private AbilityTable tableLoader;

	@FXML
	private TableView<Ability> tableView;
	@FXML
	private TableColumn<Ability, Integer> idColumn;
	@FXML
	private TableColumn<Ability, String> nameColumn;
	@FXML
	private TextField filter;
	
	private boolean adjusting;
	private Ability item;
	private Ability selected;
	

	public Ability getSelected() {
		return selected;
	}

	void configure(Stage stage, AbilityTable tableLoader) {
		this.stage = stage;
		this.tableLoader = tableLoader;

		// 1. Wrap the ObservableList in a FilteredList (initially display all
		// data).
		FilteredList<Ability> filteredData = new FilteredList<>(tableLoader.getAbilities(), p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filter.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();
				if (person.getName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (person.getDescription().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (person.getCooldownCategory().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (person.getCategory().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (String.valueOf(person.getTier()).equals(lowerCaseFilter)) {
					return true;
				} else if (String.valueOf(person.getLevel()).equals(lowerCaseFilter)) {
					return true;
				} else if (String.valueOf(person.getId()).equals(lowerCaseFilter)) {
					return true;
				}
				return false;
			});
		});

		tableView.setItems(filteredData);
		setAvailable();
	}

	@FXML
	private void initialize() {
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

		tableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showAbilityDetails(newValue));

	}

	private void showAbilityDetails(Ability newValue) {
		if (adjusting) {
			return;
		}
		setAbility(newValue);
	}

	private void setAbility(Ability newValue) {
		adjusting = true;
		item = newValue;
		try {
		} finally {
			adjusting = false;
		}
		setAvailable();
	}

	private void setAvailable() {
	}
	
	@FXML
	private void evtSelect() {
		selected = item;
		stage.close();
	}

	@FXML
	private void evtFilterChanged() {

	}

	@FXML
	private void evtCancelItem() {
		setAbility(item);
	}

}
