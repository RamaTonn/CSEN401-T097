package views;

import java.io.IOException;
import java.util.ArrayList;

import engine.Game;
import engine.Player;
import engine.PriorityQueue;

import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.abilities.Ability;
import model.world.Champion;

import javafx.scene.input.KeyEvent;

public class ViewFX extends Application {

	private static Game game;
	private static Player player1;
	private static Player player2;
	private static boolean attack = false;
	private static int abilityD = 0;
	private static int abilityS = 0;

	private static final String HOVERED_BUTTON_STYLE = "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;";

	// HELPERS

	public static GridPane createHoneyComb(int rows, int columns, double size, ArrayList<Champion> option) {
		double[] points = new double[12];
		for (int i = 0; i < 12; i += 2) {
			double angle = Math.PI * (0.5 + i / 6d);
			points[i] = Math.cos(angle);
			points[i + 1] = Math.sin(angle);
		}
		Polygon polygon = new Polygon(points);

		GridPane result = new GridPane();
		RowConstraints rc1 = new RowConstraints(size / 4);
		rc1.setFillHeight(true);
		RowConstraints rc2 = new RowConstraints(size / 2);
		rc2.setFillHeight(true);

		double width = Math.sqrt(0.75) * size;
		ColumnConstraints cc = new ColumnConstraints(width / 2);
		cc.setFillWidth(true);

		for (int i = 0; i < columns; i++) {
			result.getColumnConstraints().addAll(cc, cc);
		}

		ArrayList<Button> p = new ArrayList<Button>();

		for (int r = 0; r < rows; r++) {
			result.getRowConstraints().addAll(rc1, rc2);
			int offset = r % 2;
			int count = columns - offset;
			for (int c = 0; c < count; c++) {
				Button b = new Button("" + r + "," + c);
				p.add(b);
				b.setPrefSize(width, size);
				b.setShape(polygon);
				result.add(b, 2 * c + offset, 2 * r, 2, 3);
			}
		}

		for (Button x : p) {

			x.setFont(new Font("Buchanan", 17));
			x.setId("lion");

			Image img = null;
			ImageView view = null;

			switch (option.get(p.indexOf(x)).getName()) {
			case "Thor":
				img = new Image("Icon/Thor.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());
				x.setGraphic(view);
				break;
			case "Hela":
				img = new Image("Icon/Hela.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Loki":
				img = new Image("Icon/Loki.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Captain America":
				img = new Image("Icon/Captain.jpg");
				view = new ImageView(img);
				x.setText("Captain America");
				x.setGraphic(view);
				break;
			case "Deadpool":
				img = new Image("Icon/Deadpool.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());
				x.setGraphic(view);
				break;
			case "Venom":
				img = new Image("Icon/Venom.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Dr Strange":
				img = new Image("Icon/Dr_Strange.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Electro":
				img = new Image("Icon/Electro.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Hulk":
				img = new Image("Icon/Hulk.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Yellow Jacket":
				img = new Image("Icon/Yellowjacket.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Spiderman":
				img = new Image("Icon/Spiderman.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Quicksilver":
				img = new Image("Icon/Quicksilver.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Iceman":
				img = new Image("Icon/Iceman.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Ironman":
				img = new Image("Icon/Ironman.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Ghost Rider":
				img = new Image("Icon/Ghost_Rider.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;

			default:
				break;
			}
			x.setOnMouseEntered(event -> x.setStyle(HOVERED_BUTTON_STYLE));
			x.setOnMouseExited(event -> x.setStyle(""));
			x.setContentDisplay(ContentDisplay.TOP);
			x.setFocusTraversable(false);
			Champion c = Game.getAvailableChampions().get(p.indexOf(x));
			String s = "";
			s += "Name :" + c.getName() + "\n" + "MaxHP :" + c.getMaxHP() + "\n" + "CurrentHP :" + c.getCurrentHP()
					+ "\n" + "Mana :" + c.getMana() + "\n" + "MaxActionPointsPerTurn :" + c.getMaxActionPointsPerTurn()
					+ c.getCurrentActionPoints() + "\n" + "AttackRange :" + c.getAttackRange() + "\n" + "AttackDamage :"
					+ c.getAttackDamage() + "\n" + "Speed :" + c.getSpeed() + "\n" + "Condition :" + c.getCondition()
					+ "\n";
			for (Ability a : c.getAbilities()) {
				s += a.getName() + "\n";
			}

			x.setTooltip(new Tooltip(s));
		}

		result.getRowConstraints().add(rc1);
		return result;
	}

	public static int getAbilityD() {
		return abilityD;
	}

	public static void setAbilityD(int abilityD) {
		ViewFX.abilityD = abilityD;
	}

	public static int getAbilityS() {
		return abilityS;
	}

	public static void setAbilityS(int abilityS) {
		ViewFX.abilityS = abilityS;
	}

	public static boolean isAttack() {
		return attack;
	}

	public static void setAttack(boolean x) {
		attack = x;
	}

	public static TilePane createSelectionGrid(int rows, int columns, double size, ArrayList<Champion> option) {
		TilePane result = new TilePane();

		ArrayList<Button> p = new ArrayList<Button>();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				Button b = new Button();
				b.setPrefSize(size, size);
				b.setWrapText(true);
				b.setTextAlignment(TextAlignment.CENTER);
				p.add(b);
				result.getChildren().add(b);
			}
		}
		for (Button x : p) {

			x.setFont(new Font("Buchanan", 17));
			x.setId("lion");

			Image img = null;
			ImageView view = null;

			switch (option.get(p.indexOf(x)).getName()) {
			case "Thor":
				img = new Image("Icon/Thor.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());
				x.setGraphic(view);
				break;
			case "Hela":
				img = new Image("Icon/Hela.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Loki":
				img = new Image("Icon/Loki.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Captain America":
				img = new Image("Icon/Captain.jpg");
				view = new ImageView(img);
				x.setText("Captain America");
				x.setGraphic(view);
				break;
			case "Deadpool":
				img = new Image("Icon/Deadpool.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());
				x.setGraphic(view);
				break;
			case "Venom":
				img = new Image("Icon/Venom.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Dr Strange":
				img = new Image("Icon/Dr_Strange.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Electro":
				img = new Image("Icon/Electro.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Hulk":
				img = new Image("Icon/Hulk.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Yellow Jacket":
				img = new Image("Icon/Yellowjacket.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Spiderman":
				img = new Image("Icon/Spiderman.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Quicksilver":
				img = new Image("Icon/Quicksilver.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Iceman":
				img = new Image("Icon/Iceman.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Ironman":
				img = new Image("Icon/Ironman.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;
			case "Ghost Rider":
				img = new Image("Icon/Ghost_Rider.jpg");
				view = new ImageView(img);
				x.setText(option.get(p.indexOf(x)).getName());

				x.setGraphic(view);
				break;

			default:
				break;
			}
			x.setOnMouseEntered(event -> x.setStyle(HOVERED_BUTTON_STYLE));
			x.setOnMouseExited(event -> x.setStyle(""));
			x.setContentDisplay(ContentDisplay.TOP);
			x.setFocusTraversable(false);
			Champion c = Game.getAvailableChampions().get(p.indexOf(x));
			String s = "";
			s += "Name :" + c.getName() + "\n" + "MaxHP :" + c.getMaxHP() + "\n" + "CurrentHP :" + c.getCurrentHP()
					+ "\n" + "Mana :" + c.getMana() + "\n" + "MaxActionPointsPerTurn :" + c.getMaxActionPointsPerTurn()
					+ c.getCurrentActionPoints() + "\n" + "AttackRange :" + c.getAttackRange() + "\n" + "AttackDamage :"
					+ c.getAttackDamage() + "\n" + "Speed :" + c.getSpeed() + "\n" + "Condition :" + c.getCondition()
					+ "\n";
			for (Ability a : c.getAbilities()) {
				s += a.getName() + "\n";
			}

			x.setTooltip(new Tooltip(s));
		}

		return result;
	}

	public static void changeScene(Scene s, Stage stage) {
		stage.setScene(s);
	}

	// SCENES

	public static Scene startScreen(Stage stage) {

		BorderPane root = new BorderPane();
		root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		root.setMaxSize(500, 500);

		Scene board = new Scene(root);

		HBox box = new HBox();

		TextField p1 = new TextField();
		Label l1 = new Label("Player 1:");
		p1.setPromptText("Name");

		Region r = new Region();
		r.setPrefWidth(150);

		TextField p2 = new TextField();
		Label l2 = new Label("Player 2:");
		p2.setPromptText("Name");

		l1.setPrefWidth(100);
		l2.setPrefWidth(100);

		VBox u = new VBox(20);

		Button next = new Button("Start");
		Region r2 = new Region();

		next.setId("lion");
		next.setFont(new Font("Buchanan", 15));

		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind(p1.textProperty(), p2.textProperty());
			}

			@Override
			protected boolean computeValue() {
				return (p1.getText().isEmpty() || p2.getText().isEmpty());
			}
		};

		next.disableProperty().bind(bb);

		next.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				player1 = new Player(p1.getText());
				player2 = new Player(p2.getText());
				try {
					game = new Game(player1, player2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				changeScene(championSelectScreen(stage, player1), stage);
			}

		});

		u.setPrefSize(150, 150);
		r2.setPrefHeight(150);

		u.getChildren().addAll(next, r2);

		box.setSpacing(10);
		box.setAlignment(Pos.CENTER);
		box.getChildren().addAll(l1, p1, r, l2, p2);
		root.setCenter(box);
		root.setBottom(u);

		u.setAlignment(Pos.CENTER);

		board.getStylesheets().add("Style.css");

		return board;
	}

	public static Scene championSelectScreen(Stage stage, Player p) {

		BorderPane root = new BorderPane();

		TilePane grid = createSelectionGrid(3, 5, 123, game.getAvailableChampions());

		grid.setPadding(new Insets(5));
		grid.setHgap(2);
		grid.setVgap(2);

		Label currentTeam = new Label(p.getName() + "'s current champions: ");

		for (int i = 0; i < grid.getChildren().size(); i++) {
			if (grid.getChildren().get(i) instanceof Button) {

				Button b = (Button) grid.getChildren().get(i);
				for (Champion c : game.getAvailableChampions()) {
					if (p == game.getSecondPlayer() && game.getFirstPlayer().getTeam().contains(c)) {
						if (b.getText().equals(c.getName())) {
							b.setDisable(true);
						}
					}
				}
				b.setOnAction(event -> {

					for (Champion c : game.getAvailableChampions()) {
						if (c.getName().equals(b.getText())) {
							p.getTeam().add(c);

						}
					}

					b.setDisable(true);
					Controller.updateTeam(currentTeam, p.getTeam().get(p.getTeam().size() - 1));
					if (p.getTeam().size() == 3) {
						changeScene(leaderSelectScreen(stage, p), stage);
					}

				});
			}
		}

		currentTeam.setFont(new Font("Buchanan", 20));

		root.setTop(grid);
		root.setBottom(currentTeam);

		Scene main = new Scene(root);

		return main;
	}

	public static Scene leaderSelectScreen(Stage stage, Player p) {
		BorderPane root = new BorderPane();

		TilePane grid = createSelectionGrid(1, 3, 150, p.getTeam());

		grid.setPadding(new Insets(5));
		grid.setHgap(5);

		Scene scene = new Scene(root);

		Label leader = new Label(p.getName() + "'s Leader: ");

		Button next = new Button("Confirm");

		// next.setId("lion");
		next.setDisable(true);

		for (int i = 0; i < grid.getChildren().size(); i++) {
			if (grid.getChildren().get(i) instanceof Button) {
				Button b = (Button) grid.getChildren().get(i);

				b.setOnAction(event -> {
					next.setDisable(false);

					for (Champion c : p.getTeam()) {
						if (b.getText().equals(c.getName())) {
							leader.setText(p.getName() + "'s leader is: " + b.getText() + " is a " + c.getType());
							p.setLeader(c);
						}
					}

				});
			}
		}
		if (p == player1) {
			next.setOnAction(event -> changeScene(championSelectScreen(stage, player2), stage));
		} else {
			next.setOnAction(event -> changeScene(gameScreen(stage), stage));
		}

		root.setTop(grid);
		root.setCenter(leader);
		root.setBottom(next);
		return scene;
	}

	public static Scene gameScreen(Stage stage) {

		BorderPane root = new BorderPane();

		GridPane board = new GridPane();

		Pane Info = new Pane();

		BorderPane CPInfo = new BorderPane();

		Scene game1 = new Scene(root);

		CPInfo.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
		CPInfo.setPrefSize(650, 150);

		Info.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
		Info.setPrefSize(150, 500);

		board.setPrefSize(500, 500);
		try {
			game = new Game(player1, player2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Label[][] Cells = new Label[5][5];
		for (int j = 0; j < 5; j++) {
			int k = 4;
			for (int i = 0; i < 5 && k >= 0; i++) {
				Cells[i][j] = new Label("(" + i + "," + j + ")");
				Cells[i][j].setAlignment(Pos.CENTER);
				Cells[i][j].setPrefSize(100, 100);
				Cells[i][j].setBackground(new Background(new BackgroundFill(Color.BEIGE, null, null)));
				board.add(Cells[i][j], j, k);
				k--;
			}
		}

		root.setCenter(board);
		root.setRight(Info);
		root.setBottom(CPInfo);
		Controller.updatePos(Cells, game);
		Label x = new Label();
		String s = "";
		PriorityQueue q = new PriorityQueue(6);
		while (!game.getTurnOrder().isEmpty()) {
			s += (((Champion) (game.getTurnOrder().peekMin())).getName()) + "\n";
			q.insert(game.getTurnOrder().peekMin());
			game.getTurnOrder().remove();
		}
		while (!q.isEmpty()) {

			game.getTurnOrder().insert(q.peekMin());
			q.remove();
		}
		x.setText(s);
		Info.getChildren().add(x);

		Label abS = new Label("Choose ability location");
		abS.setVisible(false);
		CPInfo.getChildren().add(abS);

		Label abD = new Label("Choose ability direction");
		abD.setVisible(false);
		CPInfo.getChildren().add(abD);

		Label at = new Label("Choose attack direction");
		at.setVisible(false);
		CPInfo.getChildren().add(at);

		game1.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				try {
					Controller.chooseAction(game, event);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				abS.setVisible(abilityS != 0);
				abD.setVisible(abilityD != 0);
				at.setVisible(attack);

				Controller.updatePos(Cells, game);
				if (Controller.checkGameOver(game)) {
					changeScene(GameOverScreen(stage, game.checkGameOver()), stage);
				}
			}

		});

		return game1;
	}

	public static Scene GameOverScreen(Stage stage, Player p) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root);
		root.setTop(new Label("The winner is: "));
		root.setCenter(new Label(p.getName()));
		return scene;
	}

	// LAUNCH

	public void start(Stage stage) throws IOException {
		Game g = new Game(new Player(STYLESHEET_CASPIAN), new Player(STYLESHEET_CASPIAN));
		g.loadAbilities("Abilities.csv");
		g.loadChampions("Champions.csv");
		Scene board = startScreen(stage);

		Image logo = new Image("marvel-logo.png");
		stage.setHeight(650);
		stage.setWidth(650);
		stage.setResizable(true);
		stage.getIcons().add(logo);
		stage.setTitle("Marvel");
		stage.setScene(board);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);

	}

}
