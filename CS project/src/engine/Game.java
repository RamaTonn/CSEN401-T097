package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import model.abilities.*;
import model.effects.*;
import model.world.*;

public class Game {
	private Player firstPlayer;
	private Player secondPlayer;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private Object[][] board;
	private static ArrayList<Champion> availableChampions;
	private static ArrayList<Ability> availableAbilities;
	private PriorityQueue turnOrder;
	final private static int BOARDHEIGHT=5;
	final private static int BOARDWIDTH=5;

	public Game(Player first, Player second){
		this.firstPlayer = first;
		this.secondPlayer = second;
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
		 ArrayList<Champion> p1=firstPlayer.getTeam();
		 ArrayList<Champion> p2=secondPlayer.getTeam();
		 board=new Object[BOARDWIDTH][BOARDHEIGHT];
		 for(int i = 1; i<4;i++){
			 board[0][i] = p1.get(i-1);
		 }
		 for(int i = 1; i<4;i++){
			 board[4][i] = p2.get(i-1);
		 }
	}
	
	private void placeCovers() {
		int c = 0;
		while(c!=5){
			Random r = new Random();
			int x = r.nextInt(3)+1;
			int y = r.nextInt(5);
			if(board[x][y] == null){
				board[x][y] = new Cover(x, y);
				c++;
			}
		}		
	}
	
	public static void loadAbilities(String filePath) throws Exception {
		BufferedReader br= new BufferedReader(new FileReader(filePath));
		availableAbilities=new ArrayList<Ability>();
		String[] a = br.readLine().split(",");
		
		/*
		if(a[0].equals("DMG")){
			availableAbilities.add(new DamagingAbility(a[1], Integer.parseInt(a[2]), 
					Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),Integer.parseInt(a[7])));
		}
		if(a[0].equals("HEL")){
			availableAbilities.add(new HealingAbility(a[1], Integer.parseInt(a[2]), 
					Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),Integer.parseInt(a[7])));
		}
		else {
			switch(a[7]) {
			case "Disarm":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Disarm(a[1], Integer.parseInt(a[8]))));
				break;
			case "Dodge":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Dodge(a[1], Integer.parseInt(a[8]))));
				break;
			case "Embrace":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Embrace(a[1], Integer.parseInt(a[8]))));
				break;
			case "PowerUp":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new PowerUp(a[1], Integer.parseInt(a[8]))));
				break;
			case "Root":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Root(a[1], Integer.parseInt(a[8]))));
				break;	
			case "Shield":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Shield(a[1], Integer.parseInt(a[8]))));
				break;
			case "Shock":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Shock(a[1], Integer.parseInt(a[8]))));
				break;
			case "Silence":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Silence(a[1], Integer.parseInt(a[8]))));
				break;
			case "SpeedUp":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new SpeedUp(a[1], Integer.parseInt(a[8]))));
				break;
			case "Stun":
				availableAbilities.add(new CrowdControlAbility(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[4]), Integer.parseInt(a[3]), AreaOfEffect.valueOf(a[5]),Integer.parseInt(a[6]),new Stun(a[1], Integer.parseInt(a[8]))));
				break;
			}
		}
		
		*/
	}
	
	public static void loadChampions(String filePath) throws Exception {
		BufferedReader br= new BufferedReader(new FileReader("Champions.csv"));
	}
}
