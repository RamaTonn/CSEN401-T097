package engine;

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

	public Game(Player first, Player second) throws UnallowedMovementException {
		firstPlayer = first;

		secondPlayer = second;
		availableChampions = new ArrayList<Champion>();
		availableAbilities = new ArrayList<Ability>();
		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		turnOrder = new PriorityQueue(6);
		fillTurnOrder();
		placeChampions();
		placeCovers();
	}

	//helper
	public void fillTurnOrder() {
		for (Champion champ : firstPlayer.getTeam())
			turnOrder.insert((Comparable) champ);
		for (Champion champ : secondPlayer.getTeam())
			turnOrder.insert((Comparable) champ);
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

	//helper
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

	//should it be static?
	public static ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	//should it be static?
	public static ArrayList<Ability> getAvailableAbilities() {
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
		if (firstPlayer.getTeam() == null) {
			return secondPlayer;
		}
		if (secondPlayer.getTeam() == null) {
			return firstPlayer;
		}
		return null;
	}

	// Implemented try catch blocks
	public void move(Direction d) throws UnallowedMovementException, NotEnoughResourcesException {
		try {
			Champion champ = getCurrentChampion();
			Point position = champ.getLocation();
			int x = position.x;
			int y = position.y;
			if (champ.getCondition().equals(Condition.INACTIVE) || champ.getCondition().equals(Condition.KNOCKEDOUT)
					|| champ.getCondition().equals(Condition.ROOTED))
				throw new UnallowedMovementException();
			else if (champ.getCurrentActionPoints() == 0)
				throw new NotEnoughResourcesException();
			else if (d.equals(Direction.LEFT)) {
				x--;
				if (x < 0)
					throw new UnallowedMovementException();
				else if (board[x][y] != null)
					throw new UnallowedMovementException();
				else {
					champ.setLocation(new Point(x, y));
					champ.setCurrentActionPoints(champ.getCurrentActionPoints() - 1);
				}

			}
			if (d.equals(Direction.RIGHT)) {
				x++;
				if (x == BOARDWIDTH)
					throw new UnallowedMovementException();
				else if (board[x][y] != null)
					throw new UnallowedMovementException();
				else {
					champ.setLocation(new Point(x, y));
					champ.setCurrentActionPoints(champ.getCurrentActionPoints() - 1);
				}
			}
			if (d.equals(Direction.UP)) {
				y++;
				if (y == BOARDHEIGHT)
					throw new UnallowedMovementException();
				else if (board[x][y] != null)
					throw new UnallowedMovementException();
				else {
					champ.setLocation(new Point(x, y));
					champ.setCurrentActionPoints(champ.getCurrentActionPoints() - 1);
				}
			}
			if (d.equals(Direction.DOWN)) {
				y--;
				if (y < 0)
					throw new UnallowedMovementException();
				else if (board[x][y] != null)
					throw new UnallowedMovementException();
				else {
					champ.setLocation(new Point(x, y));
					champ.setCurrentActionPoints(champ.getCurrentActionPoints() - 1);
				}
			}
		} catch (UnallowedMovementException | NotEnoughResourcesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//helper
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
		if (f1 == true && f2 == true) {
			return true;
		}
		return false;
	}

	//helper
	public ArrayList<Damageable> retrieve(Direction d, Champion c) {
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		ArrayList<Damageable> retrieve = null;
		if (d.equals(Direction.UP)) {
			for (int r = 1; r <= c.getAttackRange() && y != BOARDHEIGHT - 1; r++) {
				y++;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						retrieve.add(f);

					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					retrieve.add(co);

				}
			}
		}
		if (d.equals(Direction.DOWN)) {
			for (int r = 1; r <= c.getAttackRange() && y != 0; r++) {
				y--;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						retrieve.add(f);

					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					retrieve.add(co);
				}
			}
		}
		if (d.equals(Direction.LEFT)) {
			for (int r = 1; r <= c.getAttackRange() && x != 0; r++) {
				x--;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						retrieve.add(f);

					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					retrieve.add(co);

				}
			}
		}
		if (d.equals(Direction.RIGHT)) {
			for (int r = 1; r <= c.getAttackRange() && x != BOARDWIDTH - 1; r++) {
				x++;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == false) {
						retrieve.add(f);

					}
				}
				if (board[x][y] instanceof Cover) {
					Cover co = (Cover) board[x][y];
					retrieve.add(co);
				}
			}
		}
		return retrieve;

	}

	//helper
	public ArrayList<Damageable> retrieve2(Direction d, Champion c) {
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		ArrayList<Damageable> retrieve = null;
		if (d.equals(Direction.UP)) {
			for (int r = 1; r <= c.getAttackRange() && y != BOARDHEIGHT - 1; r++) {
				y++;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == true) {
						retrieve.add(f);

					}
				}
			}
		}

		if (d.equals(Direction.DOWN)) {
			for (int r = 1; r <= c.getAttackRange() && y != 0; r++) {
				y--;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == true) {
						retrieve.add(f);

					}
				}
			}
		}
		if (d.equals(Direction.LEFT)) {
			for (int r = 1; r <= c.getAttackRange() && x != 0; r++) {
				x--;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == true) {
						retrieve.add(f);

					}
				}
			}
		}
		if (d.equals(Direction.RIGHT)) {
			for (int r = 1; r <= c.getAttackRange() && x != BOARDWIDTH - 1; r++) {
				x++;
				if (board[x][y] instanceof Champion) {
					Champion f = (Champion) board[x][y];
					if (member(c, f) == true) {
						retrieve.add(f);

					}
				}
			}
		}
		return retrieve;

	}

	// Implemented try catch blocks
	public void attack(Direction d) throws NotEnoughResourcesException, ChampionDisarmedException {
		try {
			Champion c = getCurrentChampion();
			if (c.getCurrentActionPoints() < 2)
				throw new NotEnoughResourcesException();
			for (Effect e : c.getAppliedEffects()) {
				if (e.getName().equals("Disarm") && e.getDuration() != 0)
					throw new ChampionDisarmedException();
			}
			if (c.getCondition().equals(Condition.INACTIVE)) {
				return;
			}
			Damageable r = retrieve(d, c).remove(0);
			if (r instanceof Champion) {
				if ((r instanceof Hero && c instanceof Villain) || (c instanceof Hero && r instanceof Villain)) {
					r.setCurrentHP((int) (r.getCurrentHP() - 1.5 * c.getAttackDamage()));
				} else
					r.setCurrentHP(r.getCurrentHP() - c.getAttackDamage());
			}
			if (r instanceof Cover) {
				r.setCurrentHP(r.getCurrentHP() - c.getAttackDamage());
			}
			if (r.getCurrentHP() == 0) {
				int x = r.getLocation().x;
				int y = r.getLocation().y;
				board[x][y] = null;
			}
			c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);
		} catch (NotEnoughResourcesException | ChampionDisarmedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//helper
	public static boolean isEnough(Champion c, Ability a) {
		if (c.getMana() < a.getManaCost() || c.getCurrentActionPoints() < a.getRequiredActionPoints()) {
			return false;
		}
		return true;
	}

	//helper
	public ArrayList<Damageable> getAlly(Champion c) {
		ArrayList<Champion> t1 = firstPlayer.getTeam();
		ArrayList<Damageable> d = new ArrayList();
		boolean flag = true;
		for (Champion a : t1) {
			if (!member(a, c)) {
				flag = false;

			}
		}
		if (flag) {
			for (Champion a : t1) {
				d.add(a);
			}
		} else {
			ArrayList<Champion> t2 = secondPlayer.getTeam();
			for (Champion a : t2) {
				d.add(a);
			}
		}
		return d;
	}

	//helper
	public ArrayList<Damageable> getEnemy(Champion c) {
		ArrayList<Champion> t1 = firstPlayer.getTeam();
		ArrayList<Damageable> d = new ArrayList();
		boolean flag = true;
		for (Champion a : t1) {
			if (!member(a, c)) {
				flag = false;

			}
		}
		if (!flag) {
			for (Champion a : t1) {
				d.add(a);
			}
		} else {
			ArrayList<Champion> t2 = secondPlayer.getTeam();
			for (Champion a : t2) {
				d.add(a);
			}
		}
		return d;
	}

	//helper
	public ArrayList<Damageable> getTarget(Champion c) {
		ArrayList<Damageable> d = new ArrayList();
		int x = c.getLocation().x;
		int y = c.getLocation().y;
		for (int i = x - 1; i <= x + 1 && i < BOARDWIDTH; i++) {
			for (int j = y - 1; j <= y + 1 && j < BOARDHEIGHT; j++) {
				if (i != x && j != y) {
					d.add((Damageable) board[i][j]);
				}

			}
		}
		return d;
	}

	//helper
	public static ArrayList<Damageable> inRange(ArrayList<Damageable> d, Champion c, Ability a) {
		ArrayList<Damageable> range = new ArrayList<Damageable>();
		for (Damageable x : d) {
			if (isInRange(a.getCastRange(), x.getLocation(), c.getLocation())) {
				range.add(x);
			}
		}
		return range;
	}

	//helper
	public static boolean isInRange(int range, Point p1, Point p2) {
		int distance = Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
		if (distance <= range) {
			return true;
		}
		return false;
	}

	// Implemented try catch blocks.
	public void castAbility(Ability a) throws NotEnoughResourcesException, AbilityUseException, InvalidTargetException {

		try {
			Champion c = getCurrentChampion();
			if (!isEnough(c, a)) {
				throw new NotEnoughResourcesException();
			}
			ArrayList<Damageable> d = new ArrayList();
			ArrayList<Damageable> e = new ArrayList();
			if (a.getCastArea().equals(AreaOfEffect.SELFTARGET)) {
				d.add(c);
				Castf(inRange(d, c, a), a);
			}
			if (a.getCastArea().equals(AreaOfEffect.TEAMTARGET)) {
				d = getAlly(c);
				Castf(inRange(d, c, a), a);
				e = getEnemy(c);
				Caste(inRange(e, c, a), a);
			}
			if (a.getCastArea().equals(AreaOfEffect.SURROUND)) {
				ArrayList<Damageable> t = getTarget(c);
				for (Damageable y : d) {
					if (y instanceof Champion) {
						if (member(c, (Champion) y)) {
							d.add(y);
						} else
							e.add(y);
					} else {
						e.add(y);
					}
				}
				Castf(inRange(d, c, a), a);
				Caste(inRange(e, c, a), a);
			}
			c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());
			c.setMana(c.getMana() - a.getManaCost());
		} catch (NotEnoughResourcesException e) {
			// TODO: handle exception
		} catch (AbilityUseException e) {
			// TODO: handle exception
		} catch (InvalidTargetException e) {
			// TODO: handle exception
		}
	}

	// Implemented try catch blocks.
	public void castAbility(Ability a, Direction d)
			throws NotEnoughResourcesException, AbilityUseException, InvalidTargetException {
		try {
			Champion c = getCurrentChampion();
			if (!isEnough(c, a)) {
				throw new NotEnoughResourcesException();
			}
			ArrayList<Damageable> r = retrieve(d, c);
			ArrayList<Damageable> Target = inRange(r, c, a);
			Caste(Target, a);
			ArrayList<Damageable> r2 = retrieve2(d, c);
			ArrayList<Damageable> Target2 = inRange(r, c, a);
			Castf(Target2, a);
			c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());
			c.setMana(c.getMana() - a.getManaCost());
		} catch (NotEnoughResourcesException e) {
			// TODO: handle exception
		} catch (AbilityUseException e) {
			// TODO: handle exception
		} catch (InvalidTargetException e) {
			// TODO: handle exception
		}
	}

	// Implemented try catch blocks.
	public static void Caste(ArrayList<Damageable> d, Ability a)
			throws NotEnoughResourcesException, AbilityUseException, InvalidTargetException {
		try {
			if (a instanceof DamagingAbility) {
				a.execute(d);
			}
			if (a instanceof CrowdControlAbility) {
				if (((CrowdControlAbility) a).getEffect().getType().equals(EffectType.DEBUFF)) {
					for (Damageable x : d) {
						if (x instanceof Champion) {
							a.execute(d);
						}
					}
				}
			} else {
				throw new InvalidTargetException();
			}
		} catch (NotEnoughResourcesException | AbilityUseException | InvalidTargetException e) {
			// TODO Auto-generated catch block

		}
	}

	// Implemented try catch blocks.
	public static void Castf(ArrayList<Damageable> d, Ability a) {
		try {
			if (a instanceof HealingAbility) {
				try {
					a.execute(d);
				} catch (NotEnoughResourcesException | AbilityUseException | InvalidTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (a instanceof CrowdControlAbility) {
				if (((CrowdControlAbility) a).getEffect().getType().equals(EffectType.BUFF)) {
					try {
						a.execute(d);
					} catch (NotEnoughResourcesException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (AbilityUseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					throw new InvalidTargetException();

				}
			}
		} catch (InvalidTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//helper
	public ArrayList<Damageable> getTargets(Champion c, Ability a) {

		// This method gets all targets within range depending on the ability and effect
		// types.
		// Does not work for DIRECTIONAL.
		if (a.getCastArea() == AreaOfEffect.SINGLETARGET) {
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			targets.add(c);
			return targets;
		}
		if (a instanceof DamagingAbility) {
			ArrayList<Damageable> targets = inRange(getEnemy(c), c, a);
			for (int i = 0; i < BOARDWIDTH; i++) {
				for (int j = 0; j < BOARDHEIGHT; j++) {
					if (board[i][j] instanceof Cover) {
						Cover x = (Cover) board[i][j];
						if (isInRange(a.getCastRange(), c.getLocation(), x.getLocation())) {
							targets.add(x);
						}
					}
				}
			}
			return targets;
		} else if (a instanceof HealingAbility) {
			return inRange(getAlly(c), c, a);
		} else {
			CrowdControlAbility cc = null;
			if (cc.getEffect().getType() == EffectType.BUFF) {
				return inRange(getAlly(c), c, a);
			} else {
				ArrayList<Damageable> targets = inRange(getEnemy(c), c, a);
				return targets;
			}
		}

	}

	//helper
	public void Cast(ArrayList<Damageable> d, Ability a)
			throws NotEnoughResourcesException, AbilityUseException, InvalidTargetException {
		try {
			if (a instanceof DamagingAbility) {
				DamagingAbility dmg = (DamagingAbility) a;
				for (Damageable target : d) {
					target.setCurrentHP(target.getCurrentHP() - dmg.getDamageAmount());
				}
				dmg.setCurrentCooldown(dmg.getBaseCooldown());
			} else if (a instanceof HealingAbility) {
				HealingAbility hel = (HealingAbility) a;
				for (Damageable target : d) {
					target.setCurrentHP(target.getCurrentHP() + hel.getHealAmount());
				}
				hel.setCurrentCooldown(hel.getBaseCooldown());
			} else {
				CrowdControlAbility cc = (CrowdControlAbility) a;
				for (Damageable target : d) {
					Champion champ = (Champion) target;
					cc.getEffect().apply(champ);
				}
				cc.setCurrentCooldown(cc.getBaseCooldown());
			}
		} catch (NotEnoughResourcesException e) {
			// TODO: handle exception
		} catch (AbilityUseException e) {
			// TODO: handle exception
		} catch (InvalidTargetException e) {
			// TODO: handle exception
		}
	}

	public void useLeaderAbility() throws LeaderAbilityAlreadyUsedException, LeaderNotCurrentException{
		Champion c = getCurrentChampion();
		if(!isLeader()) {
			throw new LeaderNotCurrentException();
		}
		if(isLeader()) {
			if(isFirstPlayer() && firstLeaderAbilityUsed) {
				throw new LeaderAbilityAlreadyUsedException();
			}
			if(!isFirstPlayer() && secondLeaderAbilityUsed) {
				throw new LeaderAbilityAlreadyUsedException();
			}
		}
		ArrayList<Champion> targets = new ArrayList<Champion>();		
		if (c instanceof Hero) {
			for(Damageable d: getAlly(c)) {
				targets.add((Champion)d);
			}
		}
		else if(c instanceof Villain) {
			for(Damageable d: getEnemy(c)) {
				targets.add((Champion)d);
			}
		}
		else {
			for(Damageable d: getAlly(c)) {
				targets.add((Champion)d);
			}
			for(Damageable d: getEnemy(c)) {
				targets.add((Champion)d);
			}
		}
		c.useLeaderAbility(targets);
	}
	
	//helper
	public boolean isLeader() {
		Champion c = getCurrentChampion();
		if(c == firstPlayer.getTeam().get(0)) {
			return true;
		}
		else if(c == firstPlayer.getTeam().get(0)) {
			return true;
		}
		return false;
	}
	
	//helper
	public boolean isFirstPlayer() {
		Champion c = getCurrentChampion();
		for(Champion x : firstPlayer.getTeam()) {
			if(x == c) {
				return true;
			}
		}
		return false;
	}
}
