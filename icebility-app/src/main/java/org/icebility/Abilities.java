package org.icebility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.dialog.ProgressDialog;
import org.icebility.Ability.AbilityClass;
import org.icebility.Ability.BuffType;
import org.icebility.Ability.TargetStatus;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Abilities {

	private static final String PREF_SELECT_FOLDER = "selectFolder";

	final static String SERVER_TABLE_URI = "http://raw.githubusercontent.com/rockfireredmoon/iceee/master/Data/AbilityTable.txt";

	static Logger LOG = Logger.getLogger(Abilities.class.getName());
	private Stage stage;
	private AbilityTable tableLoader;

	@FXML
	private TableView<Ability> tableView;
	@FXML
	private TableColumn<Ability, Integer> idColumn;
	@FXML
	private TableColumn<Ability, String> nameColumn;
	@FXML
	private TableColumn<Ability, String> categoryColumn;
	@FXML
	private TableColumn<Ability, String> cooldownCategoryColumn;
	@FXML
	private TableColumn<Ability, Integer> levelColumn;
	@FXML
	private TableColumn<Ability, Integer> tierColumn;
	@FXML
	private TableColumn<Ability, Integer> groupIdColumn;
	@FXML
	private ComboBox<Integer> hostility;
	@FXML
	private TextField id;
	@FXML
	private TextField name;
	@FXML
	private ComboBox<String> groupId;
	@FXML
	private TextField duration;
	@FXML
	private TextField interval;
	@FXML
	private ComboBox<String> cooldownCategory;
	@FXML
	private ComboBox<String> warmupCue;
	@FXML
	private TextField cooldownTime;
	@FXML
	private ComboBox<Integer> tier;
	@FXML
	private TextArea description;
	@FXML
	private TextArea activationCriteria;
	@FXML
	private TextArea activationActions;
	@FXML
	private ComboBox<String> visualCue;
	@FXML
	private TextField level;
	@FXML
	private TextField crossCost;
	@FXML
	private TextField classCost;
	@FXML
	private CheckBox knight;
	@FXML
	private CheckBox mage;
	@FXML
	private CheckBox rogue;
	@FXML
	private CheckBox druid;
	@FXML
	private ListView<Integer> requiredAbilities;
	@FXML
	private TextField icon;
	@FXML
	private ComboBox<String> category;
	@FXML
	private TextField x;
	@FXML
	private TextField y;
	@FXML
	private ComboBox<Ability.AbilityClass> abilityClass;
	@FXML
	private ComboBox<Integer> useType;
	@FXML
	private TextField addMagicCharge;
	@FXML
	private TextField addMeleeCharge;
	@FXML
	private TextField ownage;
	@FXML
	private TextField goldCost;
	@FXML
	private ComboBox<Ability.BuffType> buffType;
	@FXML
	private ComboBox<Ability.TargetStatus> targetStatus;
	@FXML
	private ComboBox<String> buffCategory;
	@FXML
	private TextField buffTitle;
	@FXML
	private CheckBox secondaryChannel;
	@FXML
	private CheckBox unbreakableChannel;
	@FXML
	private CheckBox allowDeadState;
	@FXML
	private TextField filter;
	@FXML
	private Button saveItem;
	@FXML
	private Button cancelItem;
	@FXML
	private MenuItem loadServer;
	@FXML
	private MenuItem open;
	@FXML
	private MenuItem newItem;
	@FXML
	private MenuItem deleteItem;
	@FXML
	private MenuItem copy;
	@FXML
	private MenuItem saveAs;
	@FXML
	private MenuItem close;
	@FXML
	private MenuItem paste;
	@FXML
	private TextField warmupTime;
	private boolean changed;
	private boolean adjusting;
	private File currentFile;
	private int prevRow = -1;
	private Ability item;

	private boolean editing;

	void configure(Stage stage) {
		this.stage = stage;
		tableLoader = new AbilityTable();

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
		cooldownCategory.setItems(tableLoader.getCooldownCategories());
		visualCue.setItems(tableLoader.getEffects());
		warmupCue.setItems(tableLoader.getEffects());
		category.setItems(tableLoader.getCategories());
		groupId.setItems(tableLoader.getGroupIds());
		abilityClass.setItems(FXCollections.observableArrayList(AbilityClass.values()));
		targetStatus.setItems(FXCollections.observableArrayList(TargetStatus.values()));
		buffType.setItems(FXCollections.observableArrayList(BuffType.values()));
		useType.setItems(FXCollections.observableArrayList(0, 4));
		buffCategory.setItems(tableLoader.getBuffCategories());
		setAvailable();
	}

	@FXML
	private void initialize() {
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
		cooldownCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().cooldownCategoryProperty());
		levelColumn.setCellValueFactory(cellData -> cellData.getValue().levelProperty().asObject());
		tierColumn.setCellValueFactory(cellData -> cellData.getValue().tierProperty().asObject());
		groupIdColumn.setCellValueFactory(cellData -> cellData.getValue().groupIdProperty().asObject());

		TextFields.integerTextField(id);
		TextFields.integerTextField(duration);
		TextFields.integerTextField(interval);
		TextFields.integerTextField(cooldownTime);
		TextFields.integerTextField(warmupTime);
		TextFields.integerTextField(level);
		TextFields.integerTextField(groupId.getEditor());
		TextFields.integerTextField(crossCost);
		TextFields.integerTextField(classCost);
		TextFields.integerTextField(x);
		TextFields.integerTextField(y);
		TextFields.integerTextField(addMagicCharge);
		TextFields.integerTextField(addMeleeCharge);
		TextFields.integerTextField(ownage);
		TextFields.integerTextField(goldCost);

		hostility.getItems().add(-1);
		hostility.getItems().add(0);
		hostility.getItems().add(1);

		tier.getItems().add(1);
		tier.getItems().add(2);
		tier.getItems().add(3);
		tier.getItems().add(4);
		tier.getItems().add(5);
		tier.getItems().add(6);

		tableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showAbilityDetails(newValue));

		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				evtChanged();
			}
		};
		ChangeListener<Integer> il = new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				evtChanged();
			}
		};
		ChangeListener<Boolean> bl = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				evtChanged();
			}
		};

		id.textProperty().addListener(cl);
		name.textProperty().addListener(cl);
		hostility.valueProperty().addListener(il);
		warmupTime.textProperty().addListener(cl);
		duration.textProperty().addListener(cl);
		interval.textProperty().addListener(cl);
		cooldownCategory.valueProperty().addListener(cl);
		cooldownTime.textProperty().addListener(cl);
		tier.valueProperty().addListener(il);
		groupId.editorProperty().get().textProperty().addListener(cl);
		groupId.valueProperty().addListener(cl);
		description.textProperty().addListener(cl);
		level.textProperty().addListener(cl);
		crossCost.textProperty().addListener(cl);
		classCost.textProperty().addListener(cl);

		// TODO req abilities
		knight.selectedProperty().addListener(bl);
		mage.selectedProperty().addListener(bl);
		rogue.selectedProperty().addListener(bl);
		druid.selectedProperty().addListener(bl);

		visualCue.valueProperty().addListener(cl);
		warmupCue.valueProperty().addListener(cl);
		category.valueProperty().addListener(cl);
		icon.textProperty().addListener(cl);
		x.textProperty().addListener(cl);
		y.textProperty().addListener(cl);

		targetStatus.valueProperty().addListener(new ChangeListener<TargetStatus>() {
			@Override
			public void changed(ObservableValue<? extends TargetStatus> observable, TargetStatus oldValue, TargetStatus newValue) {
				evtChanged();
			}
		});
		activationCriteria.textProperty().addListener(cl);
		activationActions.textProperty().addListener(cl);
		buffType.valueProperty().addListener(new ChangeListener<BuffType>() {

			@Override
			public void changed(ObservableValue<? extends BuffType> observable, BuffType oldValue, BuffType newValue) {
				evtChanged();
			}
		});
		buffCategory.valueProperty().addListener(cl);
		buffTitle.textProperty().addListener(cl);

		secondaryChannel.selectedProperty().addListener(bl);
		unbreakableChannel.selectedProperty().addListener(bl);
		allowDeadState.selectedProperty().addListener(bl);

		abilityClass.valueProperty().addListener(new ChangeListener<AbilityClass>() {

			@Override
			public void changed(ObservableValue<? extends AbilityClass> observable, AbilityClass oldValue, AbilityClass newValue) {
				evtChanged();

			}
		});
		useType.valueProperty().addListener(il);
		addMagicCharge.textProperty().addListener(cl);
		addMeleeCharge.textProperty().addListener(cl);
		ownage.textProperty().addListener(cl);
		goldCost.textProperty().addListener(cl);

	}

	private void showAbilityDetails(Ability newValue) {
		if (adjusting) {
			return;
		}
		if (changed) {
			if (prevRow != -1) {
				adjusting = true;
				try {
					tableView.getSelectionModel().select(prevRow);
					throw new IllegalStateException();
				} finally {
					adjusting = false;
				}
			}
		}
		editing = true;
		setAbility(newValue);
		prevRow = tableView.getSelectionModel().getSelectedIndex();
	}

	private void setAbility(Ability newValue) {
		changed = false;
		adjusting = true;
		item = newValue;
		try {
			if (newValue == null) {
				id.setText("");
				name.setText("");
				hostility.getSelectionModel().select(1);
				warmupTime.setText("0");
				duration.setText("0");
				interval.setText("0");
				category.getSelectionModel().clearSelection();
				cooldownCategory.getSelectionModel().clearSelection();
				cooldownTime.setText("0");
				activationCriteria.setText("");
				activationActions.setText("");
				description.setText("");
				warmupCue.getSelectionModel().clearSelection();
				tier.getSelectionModel().clearSelection();
				level.setText("0");
				groupId.getSelectionModel().clearSelection();
				crossCost.setText("0");
				classCost.setText("0");
				knight.setSelected(false);
				mage.setSelected(false);
				druid.setSelected(false);
				rogue.setSelected(false);
				requiredAbilities.setItems(FXCollections.emptyObservableList());
				icon.setText("");
				category.getSelectionModel().clearSelection();
				x.setText("0");
				y.setText("0");
				abilityClass.getSelectionModel().clearSelection();
				useType.getSelectionModel().clearSelection();
				addMagicCharge.setText("0");
				addMeleeCharge.setText("0");
				ownage.setText("0");
				goldCost.setText("0");
				buffType.getSelectionModel().select(0);
				buffCategory.getSelectionModel().clearSelection();
				buffTitle.setText("");
				targetStatus.getSelectionModel().clearSelection();
				secondaryChannel.setSelected(false);
				unbreakableChannel.setSelected(false);
				allowDeadState.setSelected(false);
			} else {
				id.setText(String.valueOf(newValue.getId()));
				name.setText(newValue.getName());
				hostility.getSelectionModel().select((Integer) newValue.getHostility());
				warmupTime.setText(String.valueOf(newValue.getWarmupTime()));
				duration.setText(String.valueOf(newValue.getDuration()));
				interval.setText(String.valueOf(newValue.getInterval()));
				cooldownCategory.getSelectionModel().select(newValue.getCooldownCategory());
				cooldownTime.setText(String.valueOf(newValue.getCooldownTime()));
				warmupCue.getSelectionModel().select(newValue.getWarmupCue());
				activationCriteria.setText(newValue.getActivationCriteria());
				activationActions.setText(newValue.getActivationActions());
				tier.getSelectionModel().select(newValue.getTier() - 1);
				description.setText(newValue.getDescription());
				visualCue.getSelectionModel().select(newValue.getVisualCue());
				groupId.valueProperty().set(String.valueOf(newValue.getGroupId()));
				level.setText(String.valueOf(newValue.getLevel()));
				crossCost.setText(String.valueOf(newValue.getCrossCost()));
				classCost.setText(String.valueOf(newValue.getClassCost()));
				knight.setSelected(newValue.isKnight());
				mage.setSelected(newValue.isMage());
				druid.setSelected(newValue.isDruid());
				rogue.setSelected(newValue.isRogue());
				requiredAbilities.itemsProperty().get().clear();
				requiredAbilities.itemsProperty().get().addAll(newValue.requiredAbilitiesProperty());
				icon.setText(newValue.getIcon());
				category.getSelectionModel().select(newValue.getCategory());
				x.setText(String.valueOf(newValue.getX()));
				y.setText(String.valueOf(newValue.getY()));
				abilityClass.getSelectionModel().select(newValue.getAbilityClass());
				useType.getSelectionModel().select((Integer) newValue.getUseType());
				addMagicCharge.setText(String.valueOf(newValue.getAddMagicCharge()));
				addMeleeCharge.setText(String.valueOf(newValue.getAddMeleeCharge()));
				ownage.setText(String.valueOf(newValue.getOwnage()));
				targetStatus.getSelectionModel().select(newValue.getTargetStatus());
				goldCost.setText(String.valueOf(newValue.getGoldCost()));
				buffType.getSelectionModel().select(newValue.getBuffType());
				buffCategory.getSelectionModel().select(newValue.getBuffCategory());
				buffTitle.setText(newValue.getBuffTitle());

				secondaryChannel.setSelected(newValue.isSecondaryChannel());
				unbreakableChannel.setSelected(newValue.isUnbreakableChannel());
				allowDeadState.setSelected(newValue.isAllowDeadState());
			}
		} finally {
			adjusting = false;
		}
		setAvailable();
	}

	private void setAvailable() {
		copy.setDisable(changed || item == null);
		paste.setDisable(changed);
		id.setDisable(editing);
		deleteItem.setDisable(changed || tableView.getSelectionModel().isEmpty() || !editing);
		newItem.setDisable(changed);
		loadServer.setDisable(changed);
		open.setDisable(changed);
		filter.setDisable(changed);
		saveItem.setDisable(!changed);
		cancelItem.setDisable(!changed);
		saveAs.setDisable(tableLoader.getAbilities().isEmpty());
		close.setDisable(tableLoader.getAbilities().isEmpty());
		
		
		

		id.setDisable(item == null);
		name.setDisable(item == null);
		hostility.setDisable(item == null);
		warmupTime.setDisable(item == null);
		duration.setDisable(item == null);
		interval.setDisable(item == null);
		category.setDisable(item == null);
		cooldownCategory.setDisable(item == null);
		cooldownTime.setDisable(item == null);
		activationCriteria.setDisable(item == null);
		activationActions.setDisable(item == null);
		description.setDisable(item == null);
		warmupCue.setDisable(item == null);
		tier.setDisable(item == null);
		level.setDisable(item == null);
		groupId.setDisable(item == null);
		crossCost.setDisable(item == null);
		classCost.setDisable(item == null);
		knight.setDisable(item == null);
		mage.setDisable(item == null);
		druid.setDisable(item == null);
		rogue.setDisable(item == null);
		requiredAbilities.setDisable(item == null);
		icon.setDisable(item == null);
		x.setDisable(item == null);
		y.setDisable(item == null);
		abilityClass.setDisable(item == null);
		useType.setDisable(item == null);
		addMagicCharge.setDisable(item == null);
		addMeleeCharge.setDisable(item == null);
		ownage.setDisable(item == null);
		goldCost.setDisable(item == null);
		buffType.setDisable(item == null);
		buffCategory.setDisable(item == null);
		buffTitle.setDisable(item == null);
		targetStatus.setDisable(item == null);
		secondaryChannel.setDisable(item == null);
		unbreakableChannel.setDisable(item == null);
		allowDeadState.setDisable(item == null);
		
		
		
	}

	private void reloadTable() {
		prevRow = -1;
		setAvailable();
	}

	@FXML
	private void evtChanged() {
		if (!adjusting) {
			changed = true;
			setAvailable();
		}
	}

	@FXML
	private void evtFilterChanged() {

	}

	@FXML
	private void evtCancelItem() {
		setAbility(item);
	}

	@FXML
	private void evtSaveItem() {
		try {
			int id = Integer.parseInt(this.id.getText());
			item.setId(id);
			item.setName(validateNotEmpty("Name", name.getText()));
			item.setHostility(intValue(hostility));
			item.setWarmupTime(Long.parseLong(warmupTime.getText()));
			item.setWarmupCue(warmupCue.getValue());
			item.setDuration(Long.parseLong(duration.getText()));
			item.setInterval(Long.parseLong(interval.getText()));
			item.setCooldownCategory(cooldownCategory.getValue());
			item.setCooldownTime(Long.parseLong(cooldownTime.getText()));
			item.setActivationCriteria(activationCriteria.getText());
			item.setActivationActions(activationActions.getText());
			item.setVisualCue(visualCue.getValue());
			item.setTier(tier.getValue());
			item.setLevel(Integer.parseInt(level.getText()));
			item.setCrossCost(Integer.parseInt(crossCost.getText()));
			item.setClassCost(Integer.parseInt(classCost.getText()));
			ObservableList<Integer> items = requiredAbilities.getItems();
			item.requiredAbilitiesProperty().setAll(items);
			item.setKnight(knight.isSelected());
			item.setMage(mage.isSelected());
			item.setRogue(rogue.isSelected());
			item.setDruid(druid.isSelected());
			item.setIcon(icon.getText());
			item.setDescription(description.getText());
			item.setCategory(category.getValue());
			item.setX(Integer.parseInt(x.getText()));
			item.setY(Integer.parseInt(y.getText()));
			item.setGroupId(intValue(groupId));
			item.setAbilityClass(abilityClass.getValue());
			item.setUseType(useType.getValue());
			item.setAddMeleeCharge(Integer.parseInt(addMeleeCharge.getText()));
			item.setAddMagicCharge(Integer.parseInt(addMagicCharge.getText()));
			item.setOwnage(Integer.parseInt(ownage.getText()));
			item.setGoldCost(Integer.parseInt(goldCost.getText()));
			item.setBuffType(buffType.getValue());
			item.setBuffCategory(buffCategory.getValue());
			item.setBuffTitle(buffTitle.getText());
			item.setTargetStatus(targetStatus.getValue());
			item.setSecondaryChannel(secondaryChannel.isSelected());
			item.setUnbreakableChannel(unbreakableChannel.isSelected());
			item.setAllowDeadState(allowDeadState.isSelected());

			if (!editing && tableLoader.containsId(id)) {
				throw new IllegalArgumentException(String.format("Id %d is already in use.", id));
			}
			boolean isNew = !tableLoader.getAbilities().contains(item);
			if (isNew) {
				tableLoader.getAbilities().add(item);
				tableLoader.sortAbilities(tableLoader.getAbilities());
				tableView.getSelectionModel().select(item);
				tableView.scrollTo(item);
			}

			if (currentFile == null) {
				evtSaveAs();
			} else {
				save(currentFile);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to save.", e);
		}
	}

	private int intValue(ComboBox<?> b) {
		Object o;
		if (b.isEditable()) {
			o = b.editorProperty().getValue().textProperty().get();
		} else {
			o = b.getValue();
		}
		if (o instanceof Integer) {
			return (Integer) o;
		} else if (o.equals("")) {
			return 0;
		} else {
			return Integer.parseInt(o.toString());
		}
	}

	private String validateNotEmpty(String name, String value) {
		if (value.trim().equals("")) {
			throw new IllegalArgumentException(String.format("%s must not be blank."));
		}
		return value;
	}

	@FXML
	private void evtSaveAs() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Ability File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Ability files", Arrays.asList("*.txt")));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", Arrays.asList("*.*")));
		if (currentFile != null) {
			fileChooser.setInitialFileName(currentFile.getName());
			fileChooser.setInitialDirectory(currentFile.getParentFile());
		} else {
			fileChooser.setInitialDirectory(getDir());
		}
		File f = fileChooser.showSaveDialog(stage);
		if (f != null) {
			Main.PREFS.put(PREF_SELECT_FOLDER, f.getParentFile().getAbsolutePath());
			save(f);
			currentFile = f;
			setWindowTitle();
		}
	}

	private void save(File f) {
		try {
			FileOutputStream fout = new FileOutputStream(f);
			try {
				tableLoader.write(fout);
				changed = false;
			} finally {
				setAvailable();
				fout.close();
			}
		} catch (IOException ioe) {
			LOG.log(Level.SEVERE, "Failed to load ability table.", ioe);
		}
	}

	@FXML
	private void evtDeleteItem() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Item");
		alert.setHeaderText(String.format("Remove ability %s (%d) from the table", item.getName(), item.getId()));
		alert.setContentText("Are you ok with this?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			tableLoader.getAbilities().remove(item);
			if (currentFile == null) {
				evtSaveAs();
			} else {
				save(currentFile);
			}
		}
	}

	@FXML
	private void evtExit() {
		if (checkClose()) {
			System.exit(0);
		}
	}

	@FXML
	private void evtClose() {
		if (checkClose()) {
			changed = false;
			editing = false;
			adjusting = true;
			try {
				tableLoader.clear();
			} finally {
				adjusting = false;
			}
			setAvailable();
		}
	}

	private boolean checkClose() {
		if (changed) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Close Table");
			alert.setHeaderText("You have unsaved changes");
			alert.setContentText("Are you ok with this?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() != ButtonType.OK) {
				return false;
			}
		}
		return true;
	}

	@FXML
	private void evtCopy() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		content.putString(item.toString());
		clipboard.setContent(content);
	}

	@FXML
	private void evtPaste() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		try {
			String content = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
			Ability a = new Ability(content);
			a.setId(tableLoader.nextId());
			editing = false;
			setAbility(a);
			changed = true;
			setAvailable();
			name.requestFocus();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to paste.", e);
		}

	}

	@FXML
	private void evtNewItem() {
		editing = false;
		Ability a = new Ability();
		a.setId(tableLoader.nextId());
		setAbility(a);
		changed = true;
		id.requestFocus();
		setAvailable();
	}

	@FXML
	private void evtAddRequiredAbility() {
		URL resource = Search.class.getResource(Search.class.getSimpleName() + ".fxml");
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(Main.BUNDLE);
		try {
			Parent root = loader.load(resource.openStream());
			Search search = (Search) loader.getController();
			Stage stage = new Stage(StageStyle.DECORATED);
			search.configure(stage, tableLoader);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.showAndWait();
			if(search.getSelected() != null) {
				requiredAbilities.itemsProperty().get().add(search.getSelected().getId());
				changed = true;
				setAvailable();				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void evtRemoveRequiredAbility() {
		requiredAbilities.itemsProperty().get().remove(requiredAbilities.getSelectionModel().getSelectedItem());
		changed = true;
		setAvailable();								
	}

	@FXML
	private void evtLoadServerTable() {
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws InterruptedException {

						try {
							HttpURLConnection hu = null;
							String url = SERVER_TABLE_URI;
							int redirs = 0;
							while (true) {
								URL u = new URL(url);
								updateMessage(String.format("Contacting %s", u.getHost()));
								hu = (HttpURLConnection) u.openConnection();
								hu.setInstanceFollowRedirects(true);
								int status = hu.getResponseCode();
								if (status != HttpURLConnection.HTTP_OK) {
									if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
											|| status == HttpURLConnection.HTTP_SEE_OTHER) {
										String newUrl = hu.getHeaderField("Location");
										if (newUrl.equals(url) || redirs > 20) {
											throw new IOException("Stuck in a redirect loop.");
										}
										url = newUrl;
										redirs++;
									} else {
										throw new IOException("Error code " + status);
									}
								} else {
									break;
								}
							}
							final int contentLength = hu.getContentLength();
							updateProgress(0, contentLength);
							InputStream in = new FilterInputStream(hu.getInputStream()) {

								long read = 0;

								@Override
								public int read() throws IOException {
									int r = super.read();
									if (r != -1) {
										read++;
										updateProgress(read, contentLength);
									}
									return r;
								}

								@Override
								public int read(byte[] b, int off, int len) throws IOException {
									int r = super.read(b, off, len);
									if (r != -1) {
										read += r;
										updateProgress(read, contentLength);
									}
									return r;
								}

							};
							try {
								tableLoader.read(in);
								currentFile = null;
								updateMessage("Loaded table.");
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										setWindowTitle();
										setAvailable();
									}
								});
							} finally {
								in.close();
							}
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to load server table.", e);
						}
						return null;
					}
				};
			}
		};

		ProgressDialog progDiag = new ProgressDialog(service);
		progDiag.setTitle("Loading Server Ability Table");
		progDiag.setHeaderText("Loading the current server ability table from the internet");
		progDiag.initOwner(stage);
		progDiag.initModality(Modality.WINDOW_MODAL);
		service.start();
	}

	@FXML
	private void evtAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Icebility");
		alert.setHeaderText("A standalone ability table editor for Planet Forever by Emerald Icemoon");
		alert.setContentText("See http://armouree.vm.bytemark.co.uk");
		alert.showAndWait();
	}

	@FXML
	private void evtOpenFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(getDir());
		fileChooser.setTitle("Open Ability File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Ability files", Arrays.asList("*.txt")));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", Arrays.asList("*.*")));
		currentFile = fileChooser.showOpenDialog(stage);
		if (currentFile != null) {
			Main.PREFS.put(PREF_SELECT_FOLDER, currentFile.getParentFile().getAbsolutePath());
			try {
				FileInputStream fin = new FileInputStream(currentFile);
				try {
					tableLoader.read(fin);
					setWindowTitle();
				} finally {
					fin.close();
					reloadTable();
				}
			} catch (IOException ioe) {
				LOG.log(Level.SEVERE, "Failed to load ability table.", ioe);
			}
		}
	}

	protected File getDir() {
		String initDir = Main.PREFS.get(PREF_SELECT_FOLDER, "");
		File dir = null;
		if (!initDir.equals(""))
			dir = new File(initDir);
		if (dir == null || !dir.exists())
			dir = new File(".");
		return dir;
	}

	private void setWindowTitle() {
		stage.setTitle(currentFile == null ? Main.BUNDLE.getString("title")
				: MessageFormat.format(Main.BUNDLE.getString("title.open"), currentFile.getPath()));
	}
}
