package engine;

import java.io.*;
import java.util.ArrayList;
import model.abilities.*;
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
		
	}
	
	private void placeCovers() {
		
	}
	
	public static void loadAbilities(String filePath) throws Exception {
		BufferedReader br= new BufferedReader(new FileReader("Abilities.csv"));
		availableChampions=new ArrayList<>();
	}
	
	public static void loadChampions(String filePath) throws Exception {
		BufferedReader br= new BufferedReader(new FileReader("Champions.csv"));
	}
}
