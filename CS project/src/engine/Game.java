package engine;

import java.io.*;
import java.util.*;

import model.abilities.*;
import model.world.*;
import model.effects.*;
import java.awt.*;
import exceptions.*;

public class Game {
	private Player firstPlayer;
	private Player secondPlayer;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private Object[][] board;
	private static ArrayList<Champion> availableChampions;
	private static ArrayList<Ability> availableAbilities;
	private PriorityQueue turnOrder;
	final private static int BOARDHEIGHT = 5;
	final private static int BOARDWIDTH = 5;

	public Game(Player first, Player second) {
		this.firstPlayer = first;
		this.secondPlayer = second;
		this.turnOrder = new PriorityQueue(6);
		firstLeaderAbilityUsed = false;
		secondLeaderAbilityUsed = false;
		availableChampions = new ArrayList<Champion>();
		availableAbilities = new ArrayList<Ability>();
		placeChampions();
		placeCovers();
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public boolean isFirstLeaderAbilityUsed() {
		return firstLeaderAbilityUsed;
	}

	public boolean isSecondLeaderAbilityUsed() {
		return secondLeaderAbilityUsed;
	}

	public Object[][] getBoard() {
		return board;
	}

	public static ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public static ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public PriorityQueue getTurnOrder() {
		return turnOrder;
	}

	public static int getBoardheight() {
		return BOARDHEIGHT;
	}

	public static int getBoardwidth() {
		return BOARDWIDTH;
	}

	private void placeChampions() {
		ArrayList<Champion> p1 = firstPlayer.getTeam();
		ArrayList<Champion> p2 = secondPlayer.getTeam();
		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		for (int i = 0; i < p1.size(); i++) {
			board[0][i + 1] = p1.get(i);
			p1.get(i).setLocation(new Point(0, i + 1));
		}
		for (int i = 0; i < p2.size(); i++) {
			board[4][i + 1] = p2.get(i);
			p2.get(i).setLocation(new Point(4, i + 1));
		}
	}

	private void placeCovers() {
		int c = 0;
		while (c < 5) {
			Random r = new Random();
			int x = r.nextInt(3) + 1;
			int y = r.nextInt(5);
			if (board[x][y] == null) {
				Cover v = new Cover(x, y);
				board[x][y] = v;
				c++;
			}
		}
	}

	public static void loadAbilities(String filePath) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String currLine = br.readLine();
		String[] a = currLine.split(",");
		while (currLine != null) {
			a = currLine.split(",");
			if (a[0].equals("DMG")) {
				availableAbilities.add(new DamagingAbility(a[1], Integer
						.parseInt(a[2]), Integer.parseInt(a[4]), Integer
						.parseInt(a[3]), AreaOfEffect.valueOf(a[5]), Integer
						.parseInt(a[6]), Integer.parseInt(a[7])));
			}
			if (a[0].equals("HEL")) {
				availableAbilities.add(new HealingAbility(a[1], Integer
						.parseInt(a[2]), Integer.parseInt(a[4]), Integer
						.parseInt(a[3]), AreaOfEffect.valueOf(a[5]), Integer
						.parseInt(a[6]), Integer.parseInt(a[7])));
			}
			if (a[0].equals("CC")) {
				Effect x;
				switch (a[7]) {
				case "Dodge":
					x = new Dodge(Integer.parseInt(a[8]));
					break;
				case "Shield":
					x = new Shield(Integer.parseInt(a[8]));
					break;
				case "PowerUp":
					x = new PowerUp(Integer.parseInt(a[8]));
					break;
				case "Stun":
					x = new Stun(Integer.parseInt(a[8]));
					break;
				case "Shock":
					x = new Shock(Integer.parseInt(a[8]));
					break;
				case "SpeedUp":
					x = new SpeedUp(Integer.parseInt(a[8]));
					break;
				case "Disarm":
					x = new Disarm(Integer.parseInt(a[8]));
					break;
				case "Silence":
					x = new Silence(Integer.parseInt(a[8]));
					break;
				case "Root":
					x = new Root(Integer.parseInt(a[8]));
					break;
				case "Embrace":
					x = new Embrace(Integer.parseInt(a[8]));
					break;
				default:
					x = null;
				}
				availableAbilities.add(new CrowdControlAbility(a[1], Integer
						.parseInt(a[2]), Integer.parseInt(a[4]), Integer
						.parseInt(a[3]), AreaOfEffect.valueOf(a[5]), Integer
						.parseInt(a[6]), x));
			}
			currLine = br.readLine();

		}

	}

	public static void loadChampions(String filePath) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String currLine = br.readLine();
		String[] a = currLine.split(",");
		while (currLine != null) {
			a = currLine.split(",");
			if (a[0].equals("H")) {
				ArrayList<Ability> h = new ArrayList<>();

				Hero x = new Hero(a[1], Integer.parseInt(a[2]),
						Integer.parseInt(a[3]), Integer.parseInt(a[4]),
						Integer.parseInt(a[5]), Integer.parseInt(a[6]),
						Integer.parseInt(a[7]));
				for(int i=0;i<availableAbilities.size() && x.getAbilities().size()!=3;i++) {
					if (availableAbilities.get(i).getName().equals(a[8])
						|| availableAbilities.get(i).getName().equals(a[9])||availableAbilities.get(i).getName().equals(a[10]))
					x.getAbilities().add(availableAbilities.get(i));
							}
				availableChampions.add(x);
			}
			if (a[0].equals("V")) {
				Villain x = new Villain(a[1], Integer.parseInt(a[2]),
						Integer.parseInt(a[3]), Integer.parseInt(a[4]),
						Integer.parseInt(a[5]), Integer.parseInt(a[6]),
						Integer.parseInt(a[7]));
				        
				for(int i=0;i<availableAbilities.size() && x.getAbilities().size()!=3 ;i++) {
					if (availableAbilities.get(i).getName().equals(a[8])
						|| availableAbilities.get(i).getName().equals(a[9])||availableAbilities.get(i).getName().equals(a[10]))
					x.getAbilities().add(availableAbilities.get(i));
				
			}
				availableChampions.add(x);

			}
			if (a[0].equals("A")) {
				AntiHero x = new AntiHero(a[1], Integer.parseInt(a[2]),
						Integer.parseInt(a[3]), Integer.parseInt(a[4]),
						Integer.parseInt(a[5]), Integer.parseInt(a[6]),
						Integer.parseInt(a[7]));
				for(int i=0;i<availableAbilities.size() && x.getAbilities().size()!=3;i++) {
					if (availableAbilities.get(i).getName().equals(a[8])
							|| availableAbilities.get(i).getName().equals(a[9])||availableAbilities.get(i).getName().equals(a[10]))
						x.getAbilities().add(availableAbilities.get(i));
									}
				availableChampions.add(x);
			}
			currLine = br.readLine();
		}
	}

}