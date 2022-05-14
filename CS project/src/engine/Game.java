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
			Champion c = (Champion) turnOrder.peekMin();
			for (Effect e : c.getAppliedEffects()) {
				if (e instanceof Stun) {
					e.setDuration(e.getDuration() - 1);
					if (e.getDuration() == 0) {
						e.remove(c);
					}
				}
			}
			if (((Champion) turnOrder.peekMin()).getCondition() != Condition.INACTIVE) {
				break;
			}
			turnOrder.remove();
		}
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
		for(Champion c : firstPlayer.getTeam()) {
			if(c.getCondition() != Condition.KNOCKEDOUT) {
				f1 = false;
				
			}
		}
		if(f1) {
			return secondPlayer;
		}
		
		boolean f2 = true;

		for(Champion c : secondPlayer.getTeam()) {
			if(c.getCondition() != Condition.KNOCKEDOUT) {
				f2 = false;
				
			}
		}
		
		if(f2) {
			return firstPlayer;
		}
		
		return null;
	}

	public void move(Direction d) throws UnallowedMovementException, NotEnoughResourcesException {
		Champion c = getCurrentChampion();

		if (c.getCurrentActionPoints() < 1) {
			throw new NotEnoughResourcesException();
		}
		
		if(c.getCondition() == Condition.ROOTED) {
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
				;
				board[x][y] = null;
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
			}
		}

		c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
	}

	public void attack(Direction d)
			throws NotEnoughResourcesException, InvalidTargetException, ChampionDisarmedException {

		Champion c = getCurrentChampion();

		if (c.getCurrentActionPoints() < 2) {
			throw new NotEnoughResourcesException();
		}
		for (Effect e : c.getAppliedEffects()) {
			if (e instanceof Disarm) {
				throw new ChampionDisarmedException();
			}
		}

		if (attackTargets(d, c).size() == 0) {
			return;
		}

		Damageable target = attackTargets(d, c).get(0);

		if (target instanceof Champion) {
			if (member(c, (Champion) target)) {
				throw new InvalidTargetException();
			}
		}

		if (isInRange(c.getAttackRange(), c.getLocation(), target.getLocation())) {
			if (target instanceof Champion) {
				Champion ct = (Champion) target;
				boolean flag = false;
				for (Effect e : ((Champion) target).getAppliedEffects()) {
					if (!flag) {
						if ((e instanceof Dodge) && e.getDuration() != 0) {
							Random randNum = new Random();
							boolean randomRes = randNum.nextBoolean();
							if (randomRes == true) {
								c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);
								flag = true;
							}
						} 
					}
					if (e instanceof Shield && e.getDuration() != 0) {
						e.remove((Champion) target);
						c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);
					}
				}
				if ((c instanceof Hero && ct instanceof Villain) || (c instanceof Hero && ct instanceof AntiHero)
						|| (c instanceof AntiHero && ct instanceof Villain)
						|| (c instanceof AntiHero && ct instanceof Hero) || (c instanceof Villain && ct instanceof Hero)
						|| (c instanceof Villain && ct instanceof AntiHero)) {
					ct.setCurrentHP(ct.getCurrentHP() - (int) (c.getAttackDamage() * 1.5));
				}

				else {
					ct.setCurrentHP(ct.getCurrentHP() - c.getAttackDamage());
				}
			} else {
				target.setCurrentHP(target.getCurrentHP() - c.getAttackDamage());
			}

		}
		c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);
		ArrayList<Damageable> u = new ArrayList<Damageable>();
		u.add(target);
		removeDamageable(u);
		
	}

	public void castAbility(Ability a)
			throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException {
		Champion c = getCurrentChampion();

		if (!isEnough(c, a)) {
			throw new NotEnoughResourcesException();
		}

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
			target.add((Damageable) board[c.getLocation().x][c.getLocation().y]);
			a.execute(target);
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
			removeDamageable(inRange(getDamageable(c), c, a));
		}

		c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());
		c.setMana(c.getMana() - a.getManaCost());
		a.setCurrentCooldown(a.getBaseCooldown());
	}

	public void castAbility(Ability a, Direction d)
			throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException {
		Champion c = getCurrentChampion();

		if (!isEnough(c, a)) {
			throw new NotEnoughResourcesException();
		}

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
		ArrayList<Damageable> targets = inRange(getTargets(d, c, a), c, a);
		a.execute(inRange(getTargets(d, c, a), c, a));
		removeDamageable(inRange(getTargets(d, c, a), c, a));

		c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());
		c.setMana(c.getMana() - a.getManaCost());
		a.setCurrentCooldown(a.getBaseCooldown());
	}

	public void castAbility(Ability a, int x, int y) throws NotEnoughResourcesException, AbilityUseException,
			InvalidTargetException, CloneNotSupportedException {
		Champion c = getCurrentChampion();

		if (!isEnough(c, a)) {
			throw new NotEnoughResourcesException();
		}

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
		
		if(!isInRange(a.getCastRange(), new Point(x,y), c.getLocation())) {
			throw new InvalidTargetException();
		}
		
		if(x == c.getLocation().x && y == c.getLocation().y) {
			throw new InvalidTargetException();
		}

		if (board[x][y] == null) {
			throw new InvalidTargetException();
		}

		if ((a instanceof CrowdControlAbility || a instanceof HealingAbility) && board[x][y] instanceof Cover) {
			throw new InvalidTargetException();
		}

		if ((a instanceof HealingAbility || (a instanceof CrowdControlAbility
				&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))
				&& (!member(c, (Champion) board[x][y]))) {
			throw new InvalidTargetException();
		}

		if ((a instanceof DamagingAbility || (a instanceof CrowdControlAbility
				&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF))
				&& (member(c, (Champion) board[x][y]))) {
			throw new InvalidTargetException();
		}
		if ((a instanceof DamagingAbility || a instanceof HealingAbility
				|| (a instanceof CrowdControlAbility
						&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))
				&& board[x][y] instanceof Cover) {
			throw new InvalidTargetException();
		}

		ArrayList<Damageable> target = new ArrayList<Damageable>();
		target.add((Damageable) board[x][y]);
		a.execute(target);
		if (a instanceof DamagingAbility) {
			removeDamageable(target);
		}
		c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());
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
				targets.remove(firstPlayer.getLeader());
				targets.remove(secondPlayer.getLeader());
			}
		}		
		c.useLeaderAbility(targets);
		if(isFirstPlayer(c)) {
			firstLeaderAbilityUsed = true;
		}
		else {
			secondLeaderAbilityUsed = true;
		}
	}

	public boolean member(Champion c1, Champion c2) {
		boolean f1 = false;
		boolean f2 = false;
		for (Champion c : firstPlayer.getTeam()) {
			if (c == c1) {
				f1 = true;
			} else if (c == c2) {
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
			//if(x!=c){
			d.add(x);
		}
		return d;
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
			range.add(c);
		}

		if (a.getCastArea() == AreaOfEffect.SURROUND) {
			int x = c.getLocation().x;
			int y = c.getLocation().y;
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if (i >= 0 && i < BOARDWIDTH && j >= 0 && j < BOARDHEIGHT) {
						for (Damageable r : d) {
							if (r.getLocation().x == i && r.getLocation().y == j) {
								range.add(r);
							}
						}
					}
				}
			}
			range.remove(c);
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

	/*public Damageable retrieve(Direction d, Champion c) {
		
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		if (d.equals(Direction.UP)) {
			for (int r = ++y; r <= c.getAttackRange() && y != BOARDHEIGHT - 1; r++) {
				y++;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						return f;
	
					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					return co;
	
				}
			}
		}
		if (d.equals(Direction.DOWN)) {
			for (int r = BOARDHEIGHT - 1; r <= c.getAttackRange() && y != 0; r++) {
				y--;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						return f;
	
					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					return co;
				}
			}
		}
		if (d.equals(Direction.LEFT)) {
			for (int r = BOARDWIDTH - 1; r <= c.getAttackRange() && x != 0; r++) {
				x--;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						return f;
	
					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					return co;
	
				}
			}
		}
		if (d.equals(Direction.RIGHT)) {
			for (int r = 0; r <= c.getAttackRange() && x != BOARDWIDTH - 1; r++) {
				x++;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						return f;
	
					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					return co;
				}
			}
		}
		return null;
	
	}*/

	public ArrayList<Damageable> getTargets(Direction d, Champion c, Ability a) {
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		ArrayList<Damageable> retrieve = new ArrayList<Damageable>();

		if (a instanceof HealingAbility || (a instanceof CrowdControlAbility
				&& (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))) {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y; j < BOARDHEIGHT; j++) {

					if (board[x][j] instanceof Champion && (member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						retrieve.add(f);
					}
				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y; j >= 0; j--) {

					if (board[x][j] instanceof Champion && (member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						retrieve.add(f);
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x; i >= 0; i--) {

					if (board[i][y] instanceof Champion && (member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						retrieve.add(f);
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x; i < BOARDWIDTH; i++) {

					if (board[i][y] instanceof Champion && (member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						retrieve.add(f);
					}
				}
			}
		}

		if (a instanceof DamagingAbility) {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y; j < BOARDHEIGHT; j++) {

					if ((board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j])))
							|| (board[x][j] instanceof Cover)) {
						retrieve.add((Damageable) board[x][j]);
					}
				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y; j >= 0; j--) {

					if ((board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j])))
							|| (board[x][j] instanceof Cover)) {
						retrieve.add((Damageable) board[x][j]);
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x; i >= 0; i--) {

					if ((board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y])))
							|| (board[i][y] instanceof Cover)) {
						retrieve.add((Damageable) board[i][y]);
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x; i < BOARDWIDTH; i++) {

					if ((board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y])))
							|| (board[i][y] instanceof Cover)) {
						retrieve.add((Damageable) board[i][y]);
					}
				}
			}
		} else {
			if (d.equals(Direction.RIGHT)) {
				for (int j = y; j < BOARDHEIGHT; j++) {

					if (board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						retrieve.add(f);
					}
					if (board[x][j] instanceof Cover) {
						Cover f = (Cover) board[x][j];
						retrieve.add(f);
					}
				}
			}

			if (d.equals(Direction.LEFT)) {
				for (int j = y; j >= 0; j--) {

					if (board[x][j] instanceof Champion && !(member(c, (Champion) board[x][j]))) {
						Champion f = (Champion) board[x][j];
						retrieve.add(f);
					}
					if (board[x][j] instanceof Cover) {
						Cover f = (Cover) board[x][j];
						retrieve.add(f);
					}
				}
			}
			if (d.equals(Direction.DOWN)) {
				for (int i = x; i >= 0; i--) {

					if (board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						retrieve.add(f);
					}
					if (board[i][y] instanceof Cover) {
						Cover f = (Cover) board[i][y];
						retrieve.add(f);
					}
				}
			}
			if (d.equals(Direction.UP)) {
				for (int i = x; i < BOARDWIDTH; i++) {

					if (board[i][y] instanceof Champion && !(member(c, (Champion) board[i][y]))) {
						Champion f = (Champion) board[i][y];
						retrieve.add(f);
					}
					if (board[i][y] instanceof Cover) {
						Cover f = (Cover) board[i][y];
						retrieve.add(f);
					}
				}
			}
		}
		return retrieve;

	}

	public ArrayList<Damageable> attackTargets(Direction d, Champion c) {
		ArrayList<Damageable> targets = new ArrayList<Damageable>();
		int x = c.getLocation().x;
		int y = c.getLocation().y;

		if (d == Direction.RIGHT) {
			for (int i = y; i < BOARDWIDTH && i <= (y + c.getAttackRange()); i++) {
				if (board[x][i] != null) {
					targets.add((Damageable) board[x][i]);
				}
			}
		}

		if (d == Direction.LEFT) {
			for (int i = y; i >= 0 && i >= (y - c.getAttackRange()); i--) {
				if (board[x][i] != null) {
					targets.add((Damageable) board[x][i]);
				}
			}
		}

		if (d == Direction.UP) {
			for (int i = x; i < BOARDHEIGHT && i <= (x + c.getAttackRange()); i++) {
				if (board[i][y] != null) {
					targets.add((Damageable) board[i][y]);
				}
			}
		}

		if (d == Direction.DOWN) {
			for (int i = x; i < BOARDWIDTH && i <= (x - c.getAttackRange()); i--) {
				if (board[i][y] != null) {
					targets.add((Damageable) board[i][y]);
				}
			}
		}

		return targets;
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

	public void removeDamageable(ArrayList<Damageable> d) {
		for (Damageable c : d) {
			if (c.getCurrentHP() == 0) {				
				board[c.getLocation().x][c.getLocation().y] = null;
				if(c instanceof Champion) {
					((Champion) c).setCondition(Condition.KNOCKEDOUT);
					if(isFirstPlayer((Champion)c)) {
						firstPlayer.getTeam().remove((Champion) c);
					}
					else {
						secondPlayer.getTeam().remove((Champion) c);
					}
				}
			}

		}
	}

	public void removeDamageable(Champion c, Damageable r) {

		if (c instanceof Champion && ((Champion) c).getCondition() == Condition.KNOCKEDOUT || c.getCurrentHP() == 0)
			board[c.getLocation().x][c.getLocation().y] = null;
		else if (r instanceof Cover && ((Cover) r).getCurrentHP() == 0)
			board[r.getLocation().x][r.getLocation().y] = null;
	}

	public void calculateHealthPointsAfterAttack(Champion champ, Champion target, boolean c, boolean r) {
		if (!c && !r) {
			target.setCurrentHP(target.getCurrentHP() - champ.getAttackDamage());
			champ.setCurrentHP(champ.getCurrentHP() - target.getAttackDamage());
		} else if (!c && r) {
			target.setCurrentHP((int) (target.getCurrentHP() - champ.getAttackDamage() * 0.5));
			champ.setCurrentHP((champ.getCurrentHP() - target.getAttackDamage()));
		} else {
			target.setCurrentHP((int) (target.getCurrentHP() - champ.getAttackDamage()));
			champ.setCurrentHP((int) (champ.getCurrentHP() - target.getAttackDamage() * 0.5));
		}

	}

}
