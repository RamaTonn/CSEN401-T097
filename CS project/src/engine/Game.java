package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.*;
import model.world.*;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Disarm;
import model.effects.Dodge;
import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;
import model.effects.PowerUp;
import model.effects.Root;
import model.effects.Shield;
import model.effects.Shock;
import model.effects.Silence;
import model.effects.SpeedUp;
import model.effects.Stun;
import model.world.*;

public class Game {
	private static ArrayList<Champion> availableChampions;
	private static ArrayList<Ability> availableAbilities;
	private Player firstPlayer;
	private Player secondPlayer;
	private Object[][] board;
	private PriorityQueue turnOrder;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private final static int BOARDWIDTH = 5;
	private final static int BOARDHEIGHT = 5;

	public Game(Player first, Player second) throws UnallowedMovementException, CloneNotSupportedException {
		firstPlayer = first;

		secondPlayer = second;
		availableChampions = new ArrayList<Champion>();
		availableAbilities = new ArrayList<Ability>();
		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		turnOrder = new PriorityQueue(6);
		prepareChampionTurns();
		placeChampions();
		placeCovers();
	}

	public void endTurn() throws CloneNotSupportedException {

		turnOrder.remove();
		if (turnOrder.isEmpty()) {
			prepareChampionTurns();
		}
		while (true) {

			if (((Champion) turnOrder.peekMin()).getCondition() != Condition.INACTIVE) {
				break;
			}
			durationUpdate();

			turnOrder.remove();
			if (turnOrder.isEmpty()) {
				prepareChampionTurns();
			}
		}
		durationUpdate();

	}

	public void durationUpdate() {
		Champion c = getCurrentChampion();
		c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
		for (int i = 0; i < c.getAppliedEffects().size(); i++) {
			c.getAppliedEffects().get(i).setDuration(c.getAppliedEffects().get(i).getDuration() - 1);
			if (c.getAppliedEffects().get(i).getDuration() == 0)
				c.getAppliedEffects().get(i).remove(c);
		}
		for (Ability a : c.getAbilities()) {
			a.setCurrentCooldown(a.getCurrentCooldown() - 1);
		}
	}

	public void prepare(Player p) throws CloneNotSupportedException {
		for (int i = 0; i < p.getTeam().size(); i++) {
			Champion c = p.getTeam().get(i);
			if (c.getCondition() == Condition.KNOCKEDOUT)
				p.getTeam().remove(i);
		}
	}

	public void add(ArrayList<Champion> a) {
		for (Champion c : a)
			turnOrder.insert(c);
	}

	private void prepareChampionTurns() throws CloneNotSupportedException {
		prepare(firstPlayer);
		prepare(secondPlayer);
		add(firstPlayer.getTeam());
		add(secondPlayer.getTeam());

	}

	public static void loadAbilities(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Ability a = null;
			AreaOfEffect ar = null;
			switch (content[5]) {
			case "SINGLETARGET":
				ar = AreaOfEffect.SINGLETARGET;
				break;
			case "TEAMTARGET":
				ar = AreaOfEffect.TEAMTARGET;
				break;
			case "SURROUND":
				ar = AreaOfEffect.SURROUND;
				break;
			case "DIRECTIONAL":
				ar = AreaOfEffect.DIRECTIONAL;
				break;
			case "SELFTARGET":
				ar = AreaOfEffect.SELFTARGET;
				break;

			}
			Effect e = null;
			if (content[0].equals("CC")) {
				switch (content[7]) {
				case "Disarm":
					e = new Disarm(Integer.parseInt(content[8]));
					break;
				case "Dodge":
					e = new Dodge(Integer.parseInt(content[8]));
					break;
				case "Embrace":
					e = new Embrace(Integer.parseInt(content[8]));
					break;
				case "PowerUp":
					e = new PowerUp(Integer.parseInt(content[8]));
					break;
				case "Root":
					e = new Root(Integer.parseInt(content[8]));
					break;
				case "Shield":
					e = new Shield(Integer.parseInt(content[8]));
					break;
				case "Shock":
					e = new Shock(Integer.parseInt(content[8]));
					break;
				case "Silence":
					e = new Silence(Integer.parseInt(content[8]));
					break;
				case "SpeedUp":
					e = new SpeedUp(Integer.parseInt(content[8]));
					break;
				case "Stun":
					e = new Stun(Integer.parseInt(content[8]));
					break;
				}
			}
			switch (content[0]) {
			case "CC":
				a = new CrowdControlAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), e);
				break;
			case "DMG":
				a = new DamagingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			case "HEL":
				a = new HealingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			}
			availableAbilities.add(a);
			line = br.readLine();
		}
		br.close();
	}

	public static void loadChampions(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Champion c = null;
			switch (content[0]) {
			case "A":
				c = new AntiHero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;

			case "H":
				c = new Hero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			case "V":
				c = new Villain(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			}

			c.getAbilities().add(findAbilityByName(content[8]));
			c.getAbilities().add(findAbilityByName(content[9]));
			c.getAbilities().add(findAbilityByName(content[10]));
			availableChampions.add(c);
			line = br.readLine();
		}
		br.close();
	}

	// helper
	private static Ability findAbilityByName(String name) {
		for (Ability a : availableAbilities) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}

	public void placeCovers() {
		int i = 0;
		while (i < 5) {
			int x = ((int) (Math.random() * (BOARDWIDTH - 2))) + 1;
			int y = (int) (Math.random() * BOARDHEIGHT);

			if (board[x][y] == null) {
				board[x][y] = new Cover(x, y);
				i++;
			}
		}

	}

	public void placeChampions() throws UnallowedMovementException {
		int i = 1;
		for (Champion c : firstPlayer.getTeam()) {
			board[0][i] = c;
			c.setLocation(new Point(0, i));
			i++;
		}
		i = 1;
		for (Champion c : secondPlayer.getTeam()) {
			board[BOARDHEIGHT - 1][i] = c;
			c.setLocation(new Point(BOARDHEIGHT - 1, i));
			i++;
		}

	}

	public ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public Object[][] getBoard() {
		return board;
	}

	public PriorityQueue getTurnOrder() {
		return turnOrder;
	}

	public boolean isFirstLeaderAbilityUsed() {
		return firstLeaderAbilityUsed;
	}

	public boolean isSecondLeaderAbilityUsed() {
		return secondLeaderAbilityUsed;
	}

	public static int getBoardwidth() {
		return BOARDWIDTH;
	}

	public static int getBoardheight() {
		return BOARDHEIGHT;
	}

	public Champion getCurrentChampion() {
		Champion champ = (Champion) turnOrder.peekMin();

		return champ;
	}

	public Player checkGameOver() {
		boolean f1 = true;
		for (Champion c : firstPlayer.getTeam()) {
			if (c.getCondition() != Condition.KNOCKEDOUT) {
				f1 = false;

			}
		}
		if (f1) {
			return secondPlayer;
		}

		boolean f2 = true;

		for (Champion c : secondPlayer.getTeam()) {
			if (c.getCondition() != Condition.KNOCKEDOUT) {
				f2 = false;

			}
		}

		if (f2) {
			return firstPlayer;
		}

		return null;
	}

	public void move(Direction d) throws UnallowedMovementException, NotEnoughResourcesException {
		Champion c = getCurrentChampion();

		if (c.getCurrentActionPoints() < 1) {
			throw new NotEnoughResourcesException();
		}

		if (c.getCondition() == Condition.ROOTED) {
			throw new UnallowedMovementException();
		}

		int x = c.getLocation().x;
		int y = c.getLocation().y;
		if (d == Direction.RIGHT) {
			if (y + 1 >= BOARDWIDTH) {
				throw new UnallowedMovementException();
			}
			if (board[x][y + 1] != null) {
				throw new UnallowedMovementException();
			} else {
				c.getLocation().move(x, y + 1);
				board[x][y] = null;
				board[x][y + 1] = c;
			}
		}

		if (d == Direction.LEFT) {
			if (y - 1 < 0) {
				throw new UnallowedMovementException();
			}
			if (board[x][y - 1] != null) {
				throw new UnallowedMovementException();
			} else {
				c.getLocation().move(x, y - 1);
				board[x][y] = null;
				board[x][y - 1] = c;
			}
		}

		if (d == Direction.UP) {
			if (x + 1 >= BOARDHEIGHT) {
				throw new UnallowedMovementException();
			}
			if (board[x + 1][y] != null) {
				throw new UnallowedMovementException();
			} else {
				c.getLocation().move(x + 1, y);
				board[x][y] = null;
				board[x + 1][y] = c;
			}
		}

		if (d == Direction.DOWN) {
			if (x - 1 < 0) {
				throw new UnallowedMovementException();
			}
			if (board[x - 1][y] != null) {
				throw new UnallowedMovementException();
			} else {
				c.getLocation().move(x - 1, y);
				board[x][y] = null;
				board[x - 1][y] = c;
			}
		}

		c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
	}

	public void attack(Direction d)
			throws NotEnoughResourcesException, ChampionDisarmedException, InvalidTargetException {

		Champion c = getCurrentChampion();

		if (c.getCurrentActionPoints() < 2) {
			throw new NotEnoughResourcesException();
		}

		c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);

		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Disarm)
				throw new ChampionDisarmedException();

		}

		if (attackTargets(d, c) == null) {
			return;
		}

		Damageable target = attackTargets(d, c);

		if (target instanceof Cover) {
			target.setCurrentHP(target.getCurrentHP() - c.getAttackDamage());
		}

		if (target instanceof Champion) {
			Champion ct = (Champion) target;
			Shield s = containsShield(ct);
			if (s != null) {
				s.remove(ct);
				return;
			}

			if (containsDodge(ct)) {
				boolean dodge = (new Random()).nextBoolean();
				if (dodge) {
					return;
				}
			}

			if ((c instanceof Hero && ct instanceof Villain) || (c instanceof Hero && ct instanceof AntiHero)
					|| (c instanceof AntiHero && ct instanceof Villain) || (c instanceof AntiHero && ct instanceof Hero)
					|| (c instanceof Villain && ct instanceof Hero)
					|| (c instanceof Villain && ct instanceof AntiHero)) {
				ct.setCurrentHP(ct.getCurrentHP() - (int) (c.getAttackDamage() * 1.5));
			}

			else {
				ct.setCurrentHP(ct.getCurrentHP() - c.getAttackDamage());
			}
		}

		removeDamageable(target);

	}

	public void castAbility(Ability a)
			throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException {
		Champion c = getCurrentChampion();

		if (!isEnough(c, a)) {
			throw new NotEnoughResourcesException();
		}

		c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());

		if (a.getCurrentCooldown() != 0) {
			throw new AbilityUseException();
		}

		if (c.getCondition() == Condition.INACTIVE) {
			throw new AbilityUseException();
		}

		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Silence) {
				throw new AbilityUseException();
			}
		}

		if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
			ArrayList<Damageable> target = new ArrayList<Damageable>();
			if (board[c.getLocation().x][c.getLocation().y] != null) {
				if ((a instanceof HealingAbility || (a instanceof CrowdControlAbility
						&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))) {

					target.add((Damageable) board[c.getLocation().x][c.getLocation().y]);
					a.execute(target);
				}
			}
		}

		if ((a instanceof HealingAbility || (a instanceof CrowdControlAbility
				&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))) {
			a.execute(inRange(getAlly(c), c, a));
		}

		if (a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
			a.execute(inRange(getEnemy(c), c, a));
		}

		if (a instanceof DamagingAbility) {
			if (a.getCastArea() == AreaOfEffect.SURROUND) {
				a.execute(inRange(getDamageable(c), c, a));
			} else {
				a.execute(inRange(getEnemy(c), c, a));
			}
			for (int i = 0; i < inRange(getDamageable(c), c, a).size(); i++) {
				removeDamageable(inRange(getDamageable(c), c, a).get(i));
			}
		}

		c.setMana(c.getMana() - a.getManaCost());
		a.setCurrentCooldown(a.getBaseCooldown());
	}

	public void castAbility(Ability a, Direction d)
			throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException {
		Champion c = getCurrentChampion();

		if (!isEnough(c, a)) {
			throw new NotEnoughResourcesException();
		}

		c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());

		if (a.getCurrentCooldown() != 0) {
			throw new AbilityUseException();
		}

		if (c.getCondition() == Condition.INACTIVE) {
			throw new AbilityUseException();
		}

		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Silence) {
				throw new AbilityUseException();
			}
		}

		a.execute(getTargets(d, c, a));

		for (int i = 0; i < getTargets(d, c, a).size(); i++) {
			removeDamageable(getTargets(d, c, a).get(i));
		}

		c.setMana(c.getMana() - a.getManaCost());
		a.setCurrentCooldown(a.getBaseCooldown());
	}

	public void castAbility(Ability a, int x, int y) throws NotEnoughResourcesException, AbilityUseException,
			InvalidTargetException, CloneNotSupportedException {
		Champion c = getCurrentChampion();

		if (!isEnough(c, a)) {
			throw new NotEnoughResourcesException();
		}

		c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());
		if (a.getCurrentCooldown() != 0) {
			throw new AbilityUseException();
		}

		if (c.getCondition() == Condition.INACTIVE) {
			throw new AbilityUseException();
		}

		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Silence) {
				throw new AbilityUseException();
			}
		}
		if (board[x][y] == null) {
			throw new InvalidTargetException();
		}
		if (!isInRange(a.getCastRange(), new Point(x, y), c.getLocation())) {
			throw new AbilityUseException();
		}

		if ((!(a instanceof DamagingAbility)) && (board[x][y] instanceof Cover)) {
			throw new InvalidTargetException();
		}

		if (a instanceof DamagingAbility && member(c, (Champion) board[x][y])) {
			throw new InvalidTargetException();
		}

		if (a instanceof HealingAbility && !member(c, (Champion) board[x][y])) {
			throw new InvalidTargetException();
		}

		if (ccType(a) != null) {
			if ((ccType(a) == EffectType.BUFF) && !member(c, (Champion) board[x][y])) {
				throw new InvalidTargetException();
			}
			if ((ccType(a) == EffectType.DEBUFF) && member(c, (Champion) board[x][y])) {
				throw new InvalidTargetException();
			}
		}

		ArrayList<Damageable> target = new ArrayList<Damageable>();
		target.add((Damageable) board[x][y]);
		a.execute(target);
		if (a instanceof DamagingAbility) {
			removeDamageable((Damageable) board[x][y]);
		}
		c.setMana(c.getMana() - a.getManaCost());
		a.setCurrentCooldown(a.getBaseCooldown());
	}

	public void useLeaderAbility()
			throws LeaderAbilityAlreadyUsedException, LeaderNotCurrentException, CloneNotSupportedException {
		Champion c = getCurrentChampion();
		if (!isLeader(c)) {
			throw new LeaderNotCurrentException();
		}
		if (isLeader(c)) {
			if (isFirstPlayer(c) && firstLeaderAbilityUsed) {
				throw new LeaderAbilityAlreadyUsedException();
			}
			if (!isFirstPlayer(c) && secondLeaderAbilityUsed) {
				throw new LeaderAbilityAlreadyUsedException();
			}
		}
		ArrayList<Champion> targets = new ArrayList<Champion>();
		if (c instanceof Hero) {
			for (Damageable d : getAlly(c)) {
				targets.add((Champion) d);
			}
		} else {
			if (c instanceof Villain) {
				for (Damageable d : getEnemy(c)) {
					targets.add((Champion) d);
				}
			} else {// except leaders of both teams
				for (Damageable d : getAlly(c)) {
					if (!isLeader((Champion) d))
						targets.add((Champion) d);
				}

				for (Damageable d : getEnemy(c)) {
					if (!isLeader((Champion) d))
						targets.add((Champion) d);
				}
			}
		}
		c.useLeaderAbility(targets);
		if (c instanceof Villain) {
			for (Champion x : targets) {
				removeDamageable(x);
			}
		}

		if (isFirstPlayer(c)) {
			firstLeaderAbilityUsed = true;
		} else {
			secondLeaderAbilityUsed = true;
		}
	}

	public boolean member(Champion c1, Champion c2) {
		boolean f1 = false;
		boolean f2 = false;
		for (Champion c : firstPlayer.getTeam()) {
			if (c == c1) {
				f1 = true;
			}
			if (c == c2) {
				f2 = true;
			}
		}
		if ((f1 == true && f2 == true) || (f1 == false && f2 == false)) {
			return true;
		}
		return false;
	}

	// helper
	public ArrayList<Damageable> getAlly(Champion c) {
		ArrayList<Damageable> d = new ArrayList();
		ArrayList<Champion> team = null;
		if (isFirstPlayer(c)) {
			team = firstPlayer.getTeam();
		}

		else {
			team = secondPlayer.getTeam();
		}

		for (Champion x : team) {
			d.add(x);

		}
		return d;
	}

	public ArrayList<Damageable> getAlly2(Champion c) {
		ArrayList<Damageable> allies = new ArrayList();
		for (int i = 0; i < BOARDWIDTH; i++) {
			for (int j = 0; j < BOARDHEIGHT; j++) {
				if (board[i][j] != null) {
					if (board[i][j] instanceof Champion) {
						Champion x = (Champion) board[i][j];
						if (member(c, x)) {
							allies.add(x);
						}
					}
				}
			}
		}
		return allies;
	}

	// helper
	public ArrayList<Damageable> getEnemy(Champion c) {
		ArrayList<Damageable> d = new ArrayList<Damageable>();
		ArrayList<Champion> team = null;
		if (isFirstPlayer(c)) {
			team = secondPlayer.getTeam();
		}

		else {
			team = firstPlayer.getTeam();
		}

		for (Champion x : team) {
			d.add(x);
		}

		return d;
	}

	// helper
	public ArrayList<Damageable> getDamageable(Champion c) {
		ArrayList<Damageable> d = new ArrayList<Damageable>();
		d.addAll(getEnemy(c));
		for (Object[] y : board) {
			for (Object e : y) {
				if (e instanceof Cover) {
					d.add((Damageable) e);
				}
			}
		}

		return d;
	}

	// helper
	public ArrayList<Damageable> inRange(ArrayList<Damageable> d, Champion c, Ability a) {
		ArrayList<Damageable> range = new ArrayList<Damageable>();

		if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
			for (Damageable x : d) {
				if (isInRange(a.getCastRange(), x.getLocation(), c.getLocation())) {
					range.add(x);
				}
			}
			if ((a instanceof HealingAbility || (a instanceof CrowdControlAbility
					&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))) {
				range.add(c);
			}
		}
		if (a.getCastArea() == AreaOfEffect.SURROUND) {
			int x = c.getLocation().x;
			int y = c.getLocation().y;
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if (i >= 0 && i < BOARDWIDTH && j >= 0 && j < BOARDHEIGHT) {
						for (Damageable r : d) {
							if (r.getLocation().x == i && r.getLocation().y == j) {
								if (r instanceof Cover || (r instanceof Champion && (Champion) r != c))
									range.add(r);
							}
						}
					}
				}
			}
		}

		return range;
	}

	// helper
	public static boolean isInRange(int range, Point p1, Point p2) {
		int distance = Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
		if (p1 != p2 && distance <= range) {
			return true;
		}
		return false;
	}

	public static boolean isEnough(Champion c, Ability a) {
		if (c.getMana() < a.getManaCost() || c.getCurrentActionPoints() < a.getRequiredActionPoints()) {
			return false;
		}
		return true;
	}

	public ArrayList<Damageable> getTargets(Direction d, Champion c, Ability a) {
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		ArrayList<Damageable> targets = new ArrayList<Damageable>();

		if ((a instanceof HealingAbility) || (ccType(a) == EffectType.BUFF)) {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y + 1; j < BOARDHEIGHT; j++) {

					if (board[x][j] instanceof Champion && (member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}
				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y - 1; j >= 0; j--) {

					if (board[x][j] instanceof Champion && (member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x - 1; i >= 0; i--) {

					if (board[i][y] instanceof Champion && (member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x + 1; i < BOARDWIDTH; i++) {

					if (board[i][y] instanceof Champion && (member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}
				}
			}
		}

		if (a instanceof DamagingAbility) {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y + 1; j < BOARDHEIGHT; j++) {

					if ((board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j])))
							|| (board[x][j] instanceof Cover)) {
						Damageable t = (Damageable) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
							targets.add(t);
						}
					}
				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y - 1; j >= 0; j--) {

					if ((board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j])))
							|| (board[x][j] instanceof Cover)) {
						Damageable t = (Damageable) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
							targets.add(t);
						}
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x - 1; i >= 0; i--) {

					if ((board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y])))
							|| (board[i][y] instanceof Cover)) {
						Damageable t = (Damageable) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
							targets.add(t);
						}
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x + 1; i < BOARDWIDTH; i++) {

					if ((board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y])))
							|| (board[i][y] instanceof Cover)) {
						Damageable t = (Damageable) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
							targets.add(t);
						}
					}
				}
			}
		}

		if (ccType(a) == EffectType.DEBUFF) {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y + 1; j < BOARDHEIGHT; j++) {

					if (board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}

				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y - 1; j >= 0; j--) {

					if (board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x - 1; i >= 0; i--) {

					if (board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x + 1; i < BOARDWIDTH; i++) {

					if (board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							targets.add(f);
						}
					}
				}
			}
		}

		return targets;
	}

	public ArrayList<Damageable> getTargets2(Direction d, Champion c, Ability a) {
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		ArrayList<Damageable> retrieve = new ArrayList<Damageable>();

		if (a instanceof HealingAbility || (a instanceof CrowdControlAbility
				&& (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))) {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y + 1; j < BOARDHEIGHT; j++) {

					if (board[x][j] instanceof Champion && (member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}
				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y - 1; j >= 0; j--) {

					if (board[x][j] instanceof Champion && (member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x - 1; i >= 0; i--) {

					if (board[i][y] instanceof Champion && (member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x + 1; i < BOARDWIDTH; i++) {

					if (board[i][y] instanceof Champion && (member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}
				}
			}
		}

		if (a instanceof DamagingAbility) {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y + 1; j < BOARDHEIGHT; j++) {

					if ((board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j])))
							|| (board[x][j] instanceof Cover)) {
						Damageable t = (Damageable) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
							retrieve.add(t);
						}
					}
				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y - 1; j >= 0; j--) {

					if ((board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j])))
							|| (board[x][j] instanceof Cover)) {
						Damageable t = (Damageable) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
							retrieve.add(t);
						}
					}
				}
				if (d.equals(Direction.DOWN)) {
					for (int i = x - 1; i >= 0; i--) {

						if ((board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y])))
								|| (board[i][y] instanceof Cover)) {
							Damageable t = (Damageable) board[i][y];
							if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
								retrieve.add(t);
							}
						}
					}
				}
				if (d.equals(Direction.UP)) {
					for (int i = x + 1; i < BOARDWIDTH; i++) {

						if ((board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y])))
								|| (board[i][y] instanceof Cover)) {
							Damageable t = (Damageable) board[i][y];
							if (isInRange(a.getCastRange(), c.getLocation(), t.getLocation())) {
								retrieve.add(t);
							}
						}
					}
				}
			}
		} else {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y + 1; j < BOARDHEIGHT; j++) {

					if (board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}

				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y - 1; j >= 0; j--) {

					if (board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x - 1; i >= 0; i--) {

					if (board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x + 1; i < BOARDWIDTH; i++) {

					if (board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						if (isInRange(a.getCastRange(), c.getLocation(), f.getLocation())) {
							retrieve.add(f);
						}
					}
				}
			}
		}

		return retrieve;
	}

	public Damageable attackTargets(Direction d, Champion c) {
		int x = c.getLocation().x;
		int y = c.getLocation().y;

		if (d == Direction.UP) {
			for (int i = x + 1; i < BOARDHEIGHT; i++) {
				if (board[i][y] != null) {
					if (isInRange(c.getAttackRange(), c.getLocation(), new Point(i, y))) {
						return (Damageable) board[i][y];
					}
				}
			}
		}
		if (d == Direction.DOWN) {
			for (int i = x - 1; i >= 0; i--) {
				if (board[i][y] != null) {
					if (isInRange(c.getAttackRange(), c.getLocation(), new Point(i, y))) {
						return (Damageable) board[i][y];
					}
				}
			}
		}
		if (d == Direction.RIGHT) {
			for (int i = y + 1; i < BOARDWIDTH; i++) {
				if (board[x][i] != null) {
					if (isInRange(c.getAttackRange(), c.getLocation(), new Point(x, i))) {
						return (Damageable) board[x][i];
					}
				}
			}
		}
		if (d == Direction.LEFT) {
			for (int i = y - 1; i >= 0; i--) {
				if (board[x][i] != null) {
					if (isInRange(c.getAttackRange(), c.getLocation(), new Point(x, i))) {
						return (Damageable) board[x][i];
					}
				}
			}
		}
		return null;
	}

	// helper
	public boolean isLeader(Champion c) {
		if (c == firstPlayer.getLeader()) {
			return true;
		} else if (c == secondPlayer.getLeader()) {
			return true;
		}
		return false;
	}

	// helper
	public boolean isFirstPlayer(Champion c) {
		for (Champion x : firstPlayer.getTeam()) {
			if (x == c) {
				return true;
			}
		}
		return false;
	}

	public void removeDamageable(Damageable d) {
		if (d.getCurrentHP() == 0) {
			board[d.getLocation().x][d.getLocation().y] = null;

			if (d instanceof Champion) {
				((Champion) d).setCondition(Condition.KNOCKEDOUT);
				if (isFirstPlayer((Champion) d)) {
					firstPlayer.getTeam().remove((Champion) d);
				} else {
					secondPlayer.getTeam().remove((Champion) d);
				}
				removeFromQ((Champion) d);
			}
		}
	}

	public Shield containsShield(Champion c) {
		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Shield) {
				return (Shield) e;
			}
		}
		return null;
	}

	public boolean containsDodge(Champion c) {
		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Dodge) {
				return true;
			}
		}
		return false;
	}

	public void removeFromQ(Comparable o) {
		PriorityQueue temp = new PriorityQueue(turnOrder.size());
		while (!turnOrder.isEmpty()) {
			if (turnOrder.peekMin() != o) {
				temp.insert(turnOrder.remove());
			} else {
				turnOrder.remove();
			}
		}
		while (!temp.isEmpty()) {
			turnOrder.insert(temp.remove());
		}
	}

	public EffectType ccType(Ability a) {
		if (a instanceof CrowdControlAbility) {
			return ((CrowdControlAbility) a).getEffect().getType();
		} else {
			return null;
		}
	}

	
}
