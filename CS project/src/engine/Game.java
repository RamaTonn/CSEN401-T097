package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
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
import model.effects.Embrace;
import model.effects.PowerUp;
import model.effects.Root;
import model.effects.Shield;
import model.effects.Shock;
import model.effects.Silence;
import model.effects.SpeedUp;
import model.effects.Stun;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Condition;
import model.world.Cover;
import model.world.Direction;
import model.world.Hero;
import model.world.Villain;

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
	
	public void fillTurnOrder() {
		for(Champion champ :firstPlayer.getTeam())
			turnOrder.insert((Comparable)champ);
		for(Champion champ :secondPlayer.getTeam())
			turnOrder.insert((Comparable)champ);
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

	public static ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public static ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public  Player getFirstPlayer() {
		return firstPlayer;
	}

	public  Player getSecondPlayer() {
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
		Champion champ = (Champion)turnOrder.remove();

		return champ;
	}
	
	public Player checkGameOver() {
		boolean p1 = false;
		boolean p2 = false;
		
		for(Champion champ:firstPlayer.getTeam()) {
			if(champ.getCondition() != Condition.KNOCKEDOUT)
				p1 = true;
		}
		
		for(Champion champ:secondPlayer.getTeam()) {
			if(champ.getCondition() != Condition.KNOCKEDOUT)
				p2 = true;
		}
		
		if(p1 == p2)
			return null;
		else if(p1)
			return firstPlayer;
		else
			return secondPlayer;
	}
	
	public void move(Direction d) throws UnallowedMovementException, NotEnoughResourcesException  {
		Champion champ = getCurrentChampion() ;
		Point position =champ.getLocation();
		int x= position.x;
		int y= position.y;
		
		if(champ.getCondition().equals(Condition.INACTIVE)||champ.getCondition().equals(Condition.KNOCKEDOUT)||champ.getCondition().equals(Condition.ROOTED))
			
			throw new UnallowedMovementException();
		
		else if(champ.getCurrentActionPoints()==0)
			throw new NotEnoughResourcesException();
		
		else if(d.equals(Direction.LEFT)){
			x--;
			if(x<0)
				throw new UnallowedMovementException();
			else if(board[x][y] != null)
				throw new UnallowedMovementException();
			else {
				champ.setLocation(new Point(x,y));
				champ.setCurrentActionPoints(champ.getCurrentActionPoints()-1);
			}

		}
		if(d.equals(Direction.RIGHT)) {
			x++;
			if(x>BOARDWIDTH)
				throw new UnallowedMovementException();
			else if(board[x][y] != null)
				throw new UnallowedMovementException();
			else {
				champ.setLocation(new Point(x,y));
				champ.setCurrentActionPoints(champ.getCurrentActionPoints()-1);
			}
		}
		if(d.equals(Direction.UP)) {
			y++;
			if(y>BOARDHEIGHT)
				throw new UnallowedMovementException();
			else if(board[x][y] != null)
				throw new UnallowedMovementException();
			else {
				champ.setLocation(new Point(x,y));
				champ.setCurrentActionPoints(champ.getCurrentActionPoints()-1);
			}
		}
		if(d.equals(Direction.DOWN)) {
			y--;
			if(y<0)
				throw new UnallowedMovementException();
			else if(board[x][y] != null)
				throw new UnallowedMovementException();
			else { 
				champ.setLocation(new Point(x,y));
				champ.setCurrentActionPoints(champ.getCurrentActionPoints()-1);
			}
		}
	}
	
	public void attack(Direction d) throws NotEnoughResourcesException, ChampionDisarmedException {
		Champion c =  getCurrentChampion();
		if(c.getCurrentActionPoints()<2)
			throw new NotEnoughResourcesException();
		for (Effect e : c.getAppliedEffects()){			
			if(e.getName().equals("Disarm") && e.getDuration()!=0)
				throw new ChampionDisarmedException();	 	
		}
		// if(c.getCondition().equals(Condition.INACTIVE))


	}
}

