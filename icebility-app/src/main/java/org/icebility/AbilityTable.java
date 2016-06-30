package org.icebility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.icebility.Ability.BuffType;

public class AbilityTable {

	final static Logger LOG = Logger.getLogger(AbilityTable.class.getName());
	private ObservableList<Ability> abilities;
	private ObservableList<String> cooldownCategories;
	private ObservableList<String> categories;
	private ObservableList<String> effects;
	private ObservableList<String> groupIds;
	private ObservableList<String> buffCategories;

	public AbilityTable() {
		abilities = FXCollections.observableArrayList();
		cooldownCategories = FXCollections.observableArrayList();
		effects = FXCollections.observableArrayList();
		categories = FXCollections.observableArrayList();
		groupIds = FXCollections.observableArrayList();
		buffCategories = FXCollections.observableArrayList();
	}

	public ObservableList<String> getGroupIds() {
		return groupIds;
	}

	public ObservableList<String> getCategories() {
		return categories;
	}

	public ObservableList<Ability> getAbilities() {
		return abilities;
	}

	public ObservableList<String> getCooldownCategories() {
		return cooldownCategories;
	}

	public ObservableList<String> getBuffCategories() {
		return buffCategories;
	}

	public ObservableList<String> getEffects() {
		return effects;
	}

	public void write(OutputStream out) throws IOException {
		PrintWriter pw = new PrintWriter(out);
		for (Ability a : abilities) {
			pw.println(a.toString());
		}
		pw.flush();
	}

	public void read(InputStream in) throws IOException {

		ObservableList<Ability> newAbilities = FXCollections.observableArrayList();
		ObservableList<String> newCooldownCategories = FXCollections.observableArrayList();
		ObservableList<String> newCategories = FXCollections.observableArrayList();
		ObservableList<String> newEffects = FXCollections.observableArrayList();
		ObservableList<String> newGroupIds = FXCollections.observableArrayList();
		ObservableList<String> newBuffCategories = FXCollections.observableArrayList();

		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		int no = 0;
		while ((line = br.readLine()) != null) {
			try {
				Ability a = createAbility(newCooldownCategories, newCategories, newEffects, newGroupIds, newBuffCategories, line);

				newAbilities.add(a);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, String.format("Line %d. %s", no, e.getMessage()), e);
			}
			no++;
		}
		sortAbilities(newAbilities);
		newCooldownCategories.sort(new StringComparator());
		newEffects.sort(new StringComparator());
		newCategories.sort(new StringComparator());
		newGroupIds.sort(new StringComparator());

		Runnable r = new Runnable() {

			@Override
			public void run() {
				AbilityTable.this.abilities.setAll(newAbilities);
				AbilityTable.this.cooldownCategories.setAll(newCooldownCategories);
				AbilityTable.this.effects.setAll(newEffects);
				AbilityTable.this.categories.setAll(newCategories);
				AbilityTable.this.groupIds.setAll(newGroupIds);
				AbilityTable.this.buffCategories.setAll(newBuffCategories);

			}
		};
		if (Platform.isFxApplicationThread())
			r.run();
		else {
			Platform.runLater(r);
		}
	}

	private Ability createAbility(ObservableList<String> newCooldownCategories, ObservableList<String> newCategories,
			ObservableList<String> newEffects, ObservableList<String> newGroupIds, ObservableList<String> newBuffCategories,
			String line) throws ParseException {
		Ability a = new Ability(line);
		if (!newCooldownCategories.contains(a.getCooldownCategory()))
			newCooldownCategories.add(a.getCooldownCategory());
		if (!newEffects.contains(a.getVisualCue()))
			newEffects.add(a.getVisualCue());
		if (!newCategories.contains(a.getCategory()))
			newCategories.add(a.getCategory());
		String gid = String.valueOf(a.getGroupId());
		if (!newGroupIds.contains(gid)) {
			newGroupIds.add(gid);
		}
		if (a.getBuffType() != BuffType.None) {
			if (!newBuffCategories.contains(a.getBuffCategory())) {
				newBuffCategories.add(a.getBuffCategory());
			}
		}
		return a;
	}

	public void sortAbilities(ObservableList<Ability> abilities) {
		abilities.sort(new Comparator<Ability>() {

			@Override
			public int compare(Ability o1, Ability o2) {
				return Integer.valueOf(o1.getId()).compareTo(o2.getId());
			}
		});
	}

	private final class StringComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	}

	private final class IntegerComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	}

	public Ability newAbility() {
		int nextId = nextId();
		Ability ab = new Ability();
		ab.setId(nextId);
		abilities.add(ab);
		sortAbilities(abilities);
		return ab;

	}

	public int nextId() {
		int nextId = 1;
		for (Ability a : abilities) {
			if (a.getId() != nextId)
				break;
			nextId++;
		}
		return nextId;
	}

	public boolean containsId(int id) {
		for (Ability a : abilities) {
			if (a.getId() == id)
				return true;
		}
		return false;
	}

	public void clear() {
		abilities.clear();
		cooldownCategories.clear();
		categories.clear();
		effects.clear();
		groupIds.clear();
		buffCategories.clear();

	}
}
