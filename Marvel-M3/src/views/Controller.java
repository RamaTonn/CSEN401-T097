package views;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.text.View;

import engine.Game;
import engine.Player;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.abilities.Ability;
import model.world.Champion;
import model.world.Cover;
import model.world.Direction;

public class Controller {

	public static void castAbility(Game g, KeyEvent e) throws NotEnoughResourcesException, AbilityUseException,
			CloneNotSupportedException, InvalidTargetException {
		int i = Character.getNumericValue(e.getCode().toString().charAt(5)) - 1;
		Champion c = g.getCurrentChampion();
		Ability a = c.getAbilities().get(i);
		switch (a.getCastArea()) {
		case DIRECTIONAL:
			ViewFX.setAbilityD(i + 1);
			System.out.println(ViewFX.getAbilityD());
			break;
		case SINGLETARGET:
			ViewFX.setAbilityS(i + 1);
		default:
			g.castAbility(a);
		}

	}

	public static void castAbility(Game g, KeyEvent e, int abilityD)
			throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException {
		Champion c = g.getCurrentChampion();
		Ability a = c.getAbilities().get(abilityD - 1);
		switch (e.getCode().toString()) {
		case "UP":
			g.castAbility(a, Direction.UP);
			break;
		case "DOWN":
			g.castAbility(a, Direction.DOWN);
			break;
		case "LEFT":
			g.castAbility(a, Direction.LEFT);
			break;
		case "RIGHT":
			g.castAbility(a, Direction.RIGHT);
			break;
		}
		ViewFX.setAbilityD(0);
	}

	public static void castAbility(Game g, String cor,int abilityS) throws NotEnoughResourcesException, AbilityUseException, InvalidTargetException, CloneNotSupportedException {
		String[] coor= cor.split(",");
		int x = Integer.parseInt(coor[0]);
		int y = Integer.parseInt(coor[1]);
		Champion c = g.getCurrentChampion();
		Ability a = c.getAbilities().get(abilityS-1);
		g.castAbility(a, x, y);
	}
	
	public static void chooseAction(Game g, KeyEvent e) throws NotEnoughResourcesException, ChampionDisarmedException,
			InvalidTargetException, UnallowedMovementException, AbilityUseException, CloneNotSupportedException,
			LeaderNotCurrentException, LeaderAbilityAlreadyUsedException {
		if (ViewFX.isAttack()) {
			if (e.getCode() == KeyCode.E) {
				ViewFX.setAttack(false);
				return;
			}
			attack(g, e); 
		}
		if (ViewFX.getAbilityD() != 0) {
			castAbility(g, e, ViewFX.getAbilityD());
		} else {
			switch (e.getCode().toString()) {
			case "W":
			case "A":
			case "S":
			case "D":
				move(g, e);
				break;
			case "E":
				ViewFX.setAttack(true);
				break;
			case "DIGIT1":
			case "DIGIT2":
			case "DIGIT3":
				castAbility(g, e);
				break;
			case "Q":
				useLeaderAbility(g);
				break;
			case "ESCAPE":
				endTurn(g);
				break;
			}
		}
	}

	public static void useLeaderAbility(Game g) throws LeaderNotCurrentException, LeaderAbilityAlreadyUsedException {
		g.useLeaderAbility();
	}

	public static void updatePos(Label[][] cells, Game g) {
		Image img;
		ImageView view;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if ((g.getBoard())[i][j] == null) {
					cells[i][j].setGraphic(null);
					cells[i][j].setTooltip(null);
				}
				if ((g.getBoard())[i][j] != null) {
					Object o = (g.getBoard())[i][j];
					if (o instanceof Champion) {
						Champion c = (Champion) o;
						cells[i][j].setTooltip(new Tooltip(c.getType() + "\nHP: " + c.getCurrentHP()
								+ "\nAction points: " + c.getCurrentActionPoints() + "\nMana: " + c.getMana()));
						switch (((Champion) o).getName()) {
						case "Thor":
							img = new Image("Icon/Thor.jpg");
							view = new ImageView(img);
							cells[i][j].setGraphic(view);
							break;
						case "Hela":
							img = new Image("Icon/Hela.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Loki":
							img = new Image("Icon/Loki.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Captain America":
							img = new Image("Icon/Captain.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Deadpool":
							img = new Image("Icon/Deadpool.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Venom":
							img = new Image("Icon/Venom.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Dr Strange":
							img = new Image("Icon/Dr_Strange.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Electro":
							img = new Image("Icon/Electro.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Hulk":
							img = new Image("Icon/Hulk.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Yellow Jacket":
							img = new Image("Icon/Yellowjacket.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Spiderman":
							img = new Image("Icon/Spiderman.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Quicksilver":
							img = new Image("Icon/Quicksilver.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Iceman":
							img = new Image("Icon/Iceman.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Ironman":
							img = new Image("Icon/Ironman.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;
						case "Ghost Rider":
							img = new Image("Icon/Ghost_Rider.jpg");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
							break;

						default:
							break;
						}
					} else {
						if (o instanceof Cover) {
							Cover c = (Cover) o;
							cells[i][j].setTooltip(new Tooltip("HP: " + c.getCurrentHP()));
							img = new Image("Icon/cover.png");
							view = new ImageView(img);

							cells[i][j].setGraphic(view);
						}

					}
				}
			}
		}

	}

	public static void move(Game g, KeyEvent e) throws NotEnoughResourcesException, UnallowedMovementException {

		switch (e.getCode().toString()) {
		case "W":
			g.move(Direction.UP);
			return;
		case "A":
			g.move(Direction.LEFT);
			return;
		case "S":
			g.move(Direction.DOWN);
			return;
		case "D":
			g.move(Direction.RIGHT);
			return;

		default:
			return;
		}

	}

	public static void endTurn(Game g) {
		g.endTurn();
	}

	public static boolean checkGameOver(Game g) {
		if (g.checkGameOver() == null) {
			return false;
		}
		return true;
	}

	public static void attack(Game g, KeyEvent e)
			throws NotEnoughResourcesException, ChampionDisarmedException, InvalidTargetException {
		switch (e.getCode().toString()) {
		case "UP":
			g.attack(Direction.UP);
			break;
		case "DOWN":
			g.attack(Direction.DOWN);
			break;
		case "LEFT":
			g.attack(Direction.LEFT);
			break;
		case "RIGHT":
			g.attack(Direction.RIGHT);
			break;
		}
		ViewFX.setAttack(false);
	}

	public static void updateTeam(Label l, Champion c) {
		l.setText(l.getText() + c.getName() + ", ");
	}

	public static void updateAbilities(ArrayList<Label> arr, Champion c) {
		for(int i = 0 ; i < arr.size(); i++) {
			arr.get(i).setText(c.getAbilities().get(i).toString());
		}
	}
	
}
