package org.icebility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Ability {

	public enum BuffType {
		World, Buff, Debuff, None;

		public static BuffType fromString(String t) {
			if (t.startsWith("w/")) {
				return BuffType.World;
			} else if (t.startsWith("+/")) {
				return BuffType.Buff;
			} else if (t.startsWith("-/")) {
				return BuffType.Debuff;
			}
			return BuffType.None;
		}

		public String toChar() {
			switch (this) {
			case World:
				return "w";
			case Buff:
				return "+";
			case Debuff:
				return "-";
			default:
				return "";
			}
		}
	}

	public enum TargetStatus {
		None, Dead, Enemy, Enemy_Alive, Friend, Friend_Alive, Friend_Dead;

		public static TargetStatus fromString(String str) {
			return str.trim().equals("") ? None : TargetStatus.valueOf(str.trim().replace(" ", "_"));
		}

		public String toTargetString() {
			return this == None ? "" : name().replace("_", " ");
		}
	}

	public enum AbilityClass {
		Passive, Use, None, Buff, Healing, Execute, Travel, Charge, Debuff, Visual, Damage, Taunt, Detaunt, Cast;

		public static AbilityClass fromString(String str) {
			if (str.equals("")) {
				return None;
			} else if (str.equals("cast")) {
				return Cast;
			} else
				return AbilityClass.valueOf(str);
		}

		public String toClassString() {
			switch (this) {
			case None:
				return "";
			default:
				return name();
			}
		}
	}

	private IntegerProperty id = new SimpleIntegerProperty();
	private StringProperty name = new SimpleStringProperty();
	private IntegerProperty hostility = new SimpleIntegerProperty();
	private LongProperty warmupTime = new SimpleLongProperty();
	private StringProperty warmupCue = new SimpleStringProperty();
	private LongProperty duration = new SimpleLongProperty();
	private LongProperty interval = new SimpleLongProperty();
	private StringProperty cooldownCategory = new SimpleStringProperty();
	private LongProperty cooldownTime = new SimpleLongProperty();
	private StringProperty activationCriteria = new SimpleStringProperty();
	private StringProperty activationActions = new SimpleStringProperty();
	private StringProperty visualCue = new SimpleStringProperty();
	private IntegerProperty tier = new SimpleIntegerProperty();
	private IntegerProperty level = new SimpleIntegerProperty();
	private IntegerProperty crossCost = new SimpleIntegerProperty();
	private IntegerProperty classCost = new SimpleIntegerProperty();
	private BooleanProperty knight = new SimpleBooleanProperty();
	private BooleanProperty mage = new SimpleBooleanProperty();
	private BooleanProperty druid = new SimpleBooleanProperty();
	private BooleanProperty rogue = new SimpleBooleanProperty();
	private ObservableList<Integer> requiredAbilities;
	private StringProperty icon = new SimpleStringProperty();
	private StringProperty description = new SimpleStringProperty();
	private StringProperty category = new SimpleStringProperty();
	private IntegerProperty x = new SimpleIntegerProperty();
	private IntegerProperty y = new SimpleIntegerProperty();
	private IntegerProperty groupId = new SimpleIntegerProperty();
	private IntegerProperty useType = new SimpleIntegerProperty();
	private IntegerProperty addMeleeCharge = new SimpleIntegerProperty();
	private IntegerProperty addMagicCharge = new SimpleIntegerProperty();
	private IntegerProperty ownage = new SimpleIntegerProperty();
	private IntegerProperty goldCost = new SimpleIntegerProperty();
	private ObjectProperty<AbilityClass> abilityClass = new SimpleObjectProperty<AbilityClass>(AbilityClass.None);
	private ObjectProperty<TargetStatus> targetStatus = new SimpleObjectProperty<TargetStatus>(TargetStatus.Friend);
	private ObjectProperty<BuffType> buffType = new SimpleObjectProperty<BuffType>(BuffType.None);

	private BooleanProperty secondaryChannel = new SimpleBooleanProperty();
	private BooleanProperty unbreakableChannel = new SimpleBooleanProperty();
	private BooleanProperty allowDeadState = new SimpleBooleanProperty();

	private StringProperty buffCategory = new SimpleStringProperty();
	private StringProperty buffTitle = new SimpleStringProperty();

	// "-/DBMove/Debuff:Attack Speed" "Enemy Alive"/

	public Ability() {
		requiredAbilities = FXCollections.observableArrayList();
	}

	public Ability(String abLine) throws ParseException {
		this();

		List<String> b = parse(abLine);
		setId(Integer.parseInt(b.get(0)));
		setName(b.get(1));
		setHostility(Integer.parseInt(b.get(2)));
		setWarmupTime(Long.parseLong(b.get(3)));
		setWarmupCue(b.get(4));
		setDuration(Long.parseLong(b.get(5)));
		setInterval(Long.parseLong(b.get(6)));
		setCooldownCategory(b.get(7));

		setCooldownTime(Long.parseLong(b.get(8)));
		setActivationCriteria(b.get(9));
		setActivationActions(b.get(10));

		setVisualCue(b.get(11));

		setTier(zeroIfBlank(b.get(12)));

		String[] prereq = b.get(13).split(",");
		if (prereq.length > 0) {
			setLevel(zeroIfBlank(prereq[0]));
			if (prereq.length > 1) {
				setCrossCost(zeroIfBlank(prereq[1]));
				if (prereq.length > 2) {
					setClassCost(zeroIfBlank(prereq[2]));
					if (prereq.length > 3) {
						String abstr = prereq[3];
						while (abstr.startsWith("(")) {
							abstr = abstr.substring(1);
						}
						while (abstr.endsWith(")")) {
							abstr = abstr.substring(0, abstr.length() - 1);
						}
						if (abstr.length() > 0) {
							String[] abs = abstr.split("\\|");
							for (String ab : abs) {
								try {
									requiredAbilities.add(Integer.parseInt(ab));
								} catch (NumberFormatException nfe) {
									throw new ParseException("Invalid required abilities list '" + abstr + "'", 0);
								}
							}
						}
						// Abs
						if (prereq.length > 4) {
							// Abs
							resetClasses();
							for (char c : prereq[4].toCharArray()) {
								switch (c) {
								case 'K':
									setKnight(true);
									break;
								case 'M':
									setMage(true);
									break;
								case 'D':
									setDruid(true);
									break;
								case 'R':
									setRogue(true);
									break;
								}
							}
						}
					}
				}
			}
		}

		setIcon(b.get(14));
		setDescription(b.get(15));

		setCategory(b.get(16));

		setX(Integer.parseInt(b.get(17)));
		setY(Integer.parseInt(b.get(18)));

		setGroupId(Integer.parseInt(b.get(19)));

		setAbilityClass(AbilityClass.fromString(b.get(20)));
		setUseType(Integer.parseInt(b.get(21)));

		setAddMeleeCharge(Integer.parseInt(b.get(22)));
		setAddMagicCharge(Integer.parseInt(b.get(23)));
		setOwnage(Integer.parseInt(b.get(24)));
		setGoldCost(Integer.parseInt(b.get(25)));

		String buff = b.get(26);
		setBuffType(BuffType.fromString(buff));
		if (getBuffType() != BuffType.None) {
			setBuffCategory(buff.substring(2, buff.indexOf(':')));
			setBuffTitle(buff.substring(buff.indexOf(':') + 1));
		}
		setTargetStatus(TargetStatus.fromString(b.get(27)));

		if (b.size() > 28) {
			for (String s : b.get(28).split("\\|")) {
				if (s.equalsIgnoreCase("secondarychannel")) {
					setSecondaryChannel(true);
				} else if (s.equalsIgnoreCase("unbreakablechannel")) {
					setUnbreakableChannel(true);
				} else if (s.equalsIgnoreCase("allowdeadstate")) {
					setAllowDeadState(true);
				}
			}
		}
	}

	public String getBuffCategory() {
		return buffCategory.get();
	}

	public void setBuffCategory(String buffCategory) {
		this.buffCategory.set(buffCategory);
	}

	public StringProperty buffCategoryProperty() {
		return buffCategory;
	}

	public String getBuffTitle() {
		return buffTitle.get();
	}

	public void setBuffTitle(String buffTitle) {
		this.buffTitle.set(buffTitle);
	}

	public StringProperty buffTitleProperty() {
		return buffTitle;
	}

	public BuffType getBuffType() {
		return buffType.get();
	}

	public void setBuffType(BuffType BuffType) {
		this.buffType.set(BuffType);
	}

	public ObjectProperty<BuffType> BuffTypeProperty() {
		return buffType;
	}

	public void setSecondaryChannel(boolean secondaryChannel) {
		this.secondaryChannel.set(secondaryChannel);
	}

	public boolean isSecondaryChannel() {
		return secondaryChannel.get();
	}

	public BooleanProperty secondaryChannelProperty() {
		return secondaryChannel;
	}

	public void setUnbreakableChannel(boolean unbreakableChannel) {
		this.unbreakableChannel.set(unbreakableChannel);
	}

	public boolean isUnbreakableChannel() {
		return unbreakableChannel.get();
	}

	public BooleanProperty unbreakableChannelProperty() {
		return unbreakableChannel;
	}

	public void setAllowDeadState(boolean allowDeadState) {
		this.allowDeadState.set(allowDeadState);
	}

	public boolean isAllowDeadState() {
		return allowDeadState.get();
	}

	public BooleanProperty allowDeadStateProperty() {
		return allowDeadState;
	}

	public int getGoldCost() {
		return goldCost.get();
	}

	public void setGoldCost(int goldCost) {
		this.goldCost.set(goldCost);
	}

	public IntegerProperty goldCostProperty() {
		return goldCost;
	}

	public int getOwnage() {
		return ownage.get();
	}

	public void setOwnage(int ownage) {
		this.ownage.set(ownage);
	}

	public IntegerProperty ownageProperty() {
		return ownage;
	}

	public int getAddMagicCharge() {
		return addMagicCharge.get();
	}

	public void setAddMagicCharge(int addMagicCharge) {
		this.addMagicCharge.set(addMagicCharge);
	}

	public IntegerProperty addMagicChargeProperty() {
		return addMagicCharge;
	}

	public int getAddMeleeCharge() {
		return addMeleeCharge.get();
	}

	public void setAddMeleeCharge(int addMeleeCharge) {
		this.addMeleeCharge.set(addMeleeCharge);
	}

	public IntegerProperty addMeleeChargeProperty() {
		return addMeleeCharge;
	}

	public int getUseType() {
		return useType.get();
	}

	public void setUseType(int useType) {
		this.useType.set(useType);
	}

	public IntegerProperty useTypeProperty() {
		return useType;
	}

	public TargetStatus getTargetStatus() {
		return targetStatus.get();
	}

	public void setTargetStatus(TargetStatus targetStatus) {
		this.targetStatus.set(targetStatus);
	}

	public ObjectProperty<TargetStatus> targetStatusProperty() {
		return targetStatus;
	}

	public AbilityClass getAbilityClass() {
		return abilityClass.get();
	}

	public void setAbilityClass(AbilityClass abilityClass) {
		this.abilityClass.set(abilityClass);
	}

	public ObjectProperty<AbilityClass> abilityClassProperty() {
		return abilityClass;
	}

	public void setGroupId(int groupId) {
		this.groupId.set(groupId);
	}

	public int getGroupId() {
		return groupId.get();
	}

	public IntegerProperty groupIdProperty() {
		return groupId;
	}

	public void setY(int y) {
		this.y.set(y);
	}

	public int getY() {
		return y.get();
	}

	public IntegerProperty yProperty() {
		return y;
	}

	public void setX(int x) {
		this.x.set(x);
	}

	public int getX() {
		return x.get();
	}

	public IntegerProperty xProperty() {
		return x;
	}

	public String getCategory() {
		return category.get();
	}

	public void setCategory(String category) {
		this.category.set(category);
	}

	public StringProperty categoryProperty() {
		return category;
	}

	public String getIcon() {
		return icon.get();
	}

	public void setIcon(String icon) {
		this.icon.set(icon);
	}

	public StringProperty iconProperty() {
		return icon;
	}

	public ObservableList<Integer> requiredAbilitiesProperty() {
		return requiredAbilities;
	}

	public void setKnight(boolean knight) {
		this.knight.set(knight);
	}

	public boolean isKnight() {
		return knight.get();
	}

	public BooleanProperty knightProperty() {
		return knight;
	}

	public void setMage(boolean mage) {
		this.mage.set(mage);
	}

	public boolean isMage() {
		return mage.get();
	}

	public BooleanProperty mageProperty() {
		return mage;
	}

	public void setDruid(boolean mage) {
		this.druid.set(mage);
	}

	public boolean isDruid() {
		return druid.get();
	}

	public BooleanProperty druidProperty() {
		return druid;
	}

	public void setRogue(boolean rogue) {
		this.rogue.set(rogue);
	}

	public boolean isRogue() {
		return rogue.get();
	}

	public BooleanProperty rogueProperty() {
		return rogue;
	}

	public void setCrossCost(int crossCost) {
		this.crossCost.set(crossCost);
	}

	public int getCrossCost() {
		return crossCost.get();
	}

	public IntegerProperty crossCostProperty() {
		return crossCost;
	}

	public void setClassCost(int classCost) {
		this.classCost.set(classCost);
	}

	public int getClassCost() {
		return classCost.get();
	}

	public IntegerProperty classCostProperty() {
		return classCost;
	}

	public void setLevel(int level) {
		this.level.set(level);
	}

	public int getLevel() {
		return level.get();
	}

	public IntegerProperty levelProperty() {
		return level;
	}

	public void setTier(int tier) {
		this.tier.set(tier);
	}

	public int getTier() {
		return tier.get();
	}

	public IntegerProperty tierProperty() {
		return tier;
	}

	public void setVisualCue(String visualCue) {
		this.visualCue.set(visualCue);
	}

	public String getVisualCue() {
		return visualCue.get();
	}

	public StringProperty visualCueProperty() {
		return visualCue;
	}

	public void setActivationCriteria(String activationCriteria) {
		this.activationCriteria.set(activationCriteria);
	}

	public String getActivationCriteria() {
		return activationCriteria.get();
	}

	public StringProperty activationCriteriaProperty() {
		return activationCriteria;
	}

	public void setActivationActions(String activationActions) {
		this.activationActions.set(activationActions);
	}

	public String getActivationActions() {
		return activationActions.get();
	}

	public StringProperty activationActionsProperty() {
		return activationActions;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setHostility(int hostility) {
		this.hostility.set(hostility);
	}

	public int getHostility() {
		return hostility.get();
	}

	public IntegerProperty hostilityProperty() {
		return hostility;
	}

	public void setWarmupTime(long warmupTime) {
		this.warmupTime.set(warmupTime);
	}

	public long getWarmupTime() {
		return warmupTime.get();
	}

	public LongProperty warmupTimeProperty() {
		return warmupTime;
	}

	public void setCooldownTime(long cooldownTime) {
		this.cooldownTime.set(cooldownTime);
	}

	public long getCooldownTime() {
		return cooldownTime.get();
	}

	public LongProperty cooldownTimeProperty() {
		return cooldownTime;
	}

	public void setInterval(long interval) {
		this.interval.set(interval);
	}

	public long getInterval() {
		return interval.get();
	}

	public LongProperty intervalProperty() {
		return interval;
	}

	public void setDuration(long duration) {
		this.duration.set(duration);
	}

	public long getDuration() {
		return duration.get();
	}

	public LongProperty durationProperty() {
		return duration;
	}

	public void setWarmupCue(String warmupCue) {
		this.warmupCue.set(warmupCue);
	}

	public String getWarmupCue() {
		return warmupCue.get();
	}

	public StringProperty cooldownCategoryProperty() {
		return cooldownCategory;
	}

	public void setCooldownCategory(String cooldownCategory) {
		this.cooldownCategory.set(cooldownCategory);
	}

	public String getCooldownCategory() {
		return cooldownCategory.get();
	}

	public StringProperty warmupCueProperty() {
		return warmupCue;
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public int getId() {
		return id.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void resetClasses() {
		mage.set(false);
		druid.set(false);
		knight.set(false);
		rogue.set(false);
	}

	public String toString() {

		StringBuilder bui = new StringBuilder();
		appendField(bui, getId());
		appendField(bui, getName());
		appendField(bui, getHostility());
		appendField(bui, getWarmupTime());
		appendField(bui, getWarmupCue());
		appendField(bui, getDuration());
		appendField(bui, getInterval());
		appendField(bui, getCooldownCategory());
		appendField(bui, getCooldownTime());
		appendField(bui, getActivationCriteria());
		appendField(bui, getActivationActions());
		appendField(bui, getVisualCue());
		appendField(bui, getTier());

		String abs = "";
		for (int ab : requiredAbilitiesProperty()) {
			if (abs.length() > 0)
				abs += "|";
			abs += ab;
		}
		String classes = "";
		if (isKnight())
			classes += "K";
		if (isRogue())
			classes += "R";
		if (isMage())
			classes += "M";
		if (isDruid())
			classes += "D";

		String prereq = blankIfZero(getLevel()) + "," + blankIfZero(getCrossCost()) + "," + blankIfZero(getClassCost()) + ",("
				+ abs + ")," + classes;
		appendField(bui, prereq);

		appendField(bui, getIcon());
		appendField(bui, getDescription());
		appendField(bui, getCategory());
		appendField(bui, getX());
		appendField(bui, getY());
		appendField(bui, getGroupId());
		appendField(bui, getAbilityClass().toClassString());
		appendField(bui, getUseType());
		appendField(bui, getAddMeleeCharge());
		appendField(bui, getAddMagicCharge());
		appendField(bui, getOwnage());
		appendField(bui, getGoldCost());

		String b = "";
		if (getBuffType() != BuffType.None) {
			b += getBuffType().toChar() + "/";
			b += getBuffCategory() + ":";
			b += getBuffTitle();
		}

		appendField(bui, b);
		appendField(bui, getTargetStatus().toTargetString());

		if (isSecondaryChannel() || isAllowDeadState() || isUnbreakableChannel()) {
			String x = "";
			if (isSecondaryChannel())
				x += "secondarychannel";
			if (isUnbreakableChannel()) {
				if (x.length() > 0)
					x += "|";
				x += "unbreakablechannel";
			}
			if (isAllowDeadState()) {
				if (x.length() > 0)
					x += "|";
				x += "allowdeadstate";
			}
			appendField(bui, x);
		}
		return bui.toString();
	}

	private List<String> parse(String line) {
		List<String> a = new ArrayList<String>();
		for (String s : line.split("\t")) {
			if (s.startsWith("\"")) {
				s = s.substring(1);
				if (s.endsWith("\"")) {
					s = s.substring(0, s.length() - 1);
					a.add(s);
				} else {
					throw new IllegalArgumentException("Expected column to end with \"");
				}
			} else {
				throw new IllegalArgumentException("Expected column to start with \"");
			}
		}
		return a;
	}

	private int zeroIfBlank(String n) {
		return n.trim().equals("") ? 0 : Integer.parseInt(n.trim());
	}

	private void appendField(StringBuilder bui, Object val) {
		if (bui.length() > 0) {
			bui.append("\t");
		}
		bui.append("\"");
		bui.append(val == null ? "" : val.toString());
		bui.append("\"");
	}

	private String blankIfZero(int z) {
		return z == 0 ? "" : String.valueOf(z);
	}
}
