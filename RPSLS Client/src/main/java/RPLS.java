import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

//GuiClient class
public class RPLS extends Application{

    TextField enterPort, enterIP;
    Button connect, quit, r, p, s, l, sp;
    VBox entering, choices, bottom, text, mid, game;
    HBox options;
    Scene startScene;
    int cNum = 0;
    BorderPane startPane;
    Client clientConnection;
    ListView<String> listItems, clientsList;
    Label points, welcome, con, result, thing, thing2, wait, youAre, pos, info, pick;
    Image pic1, pic2, pic3, pic4, pic5;
    ImageView rock, paper, scissors, lizard, spock;
    String clean = "";
    boolean isConnected = false;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("RPSLS Client");

        this.connect = new Button("Connect");
        thing = new Label("Enter port number");
        thing.setFont(new Font("Comic Sans MS", 16));
        thing2 = new Label("\nEnter IP address");
        thing2.setFont(new Font("Comic Sans MS", 16));
        con = new Label("Clients Connected");
        con.setFont(new Font("Comic Sans MS", 26));
        enterPort = new TextField("5555");
        enterIP = new TextField("127.0.0.1");
        wait = new Label("");
        wait.setFont(new Font("Comic Sans MS", 16));
        youAre = new Label("\n");
        youAre.setFont(new Font("Comic Sans MS", 14));
        pos = new Label("Possible Opponents:");
        pos.setFont(new Font("Comic Sans MS", 14));
        info = new Label("Game Information:");
        info.setFont(new Font("Comic Sans MS", 14));
        pick = new Label("Pick your move");
        pick.setFont(new Font("Comic Sans MS", 20));

        pic1 = new Image("rock.jpg");
        pic2 = new Image("paper.jpg");
        pic3 = new Image("scissors.jpg");
        pic4 = new Image("lizard.jpg");
        pic5 = new Image("spock.jpg");
        rock = new ImageView(pic1);
        rock.setFitWidth(75);
        rock.setFitHeight(75);
        rock.setPreserveRatio(true);
        paper = new ImageView(pic2);
        paper.setFitWidth(75);
        paper.setFitHeight(75);
        paper.setPreserveRatio(true);
        scissors = new ImageView(pic3);
        scissors.setFitWidth(75);
        scissors.setFitHeight(75);
        scissors.setPreserveRatio(true);
        lizard = new ImageView(pic4);
        lizard.setFitWidth(75);
        lizard.setFitHeight(75);
        lizard.setPreserveRatio(true);
        spock = new ImageView(pic5);
        spock.setFitWidth(75);
        spock.setFitHeight(75);
        spock.setPreserveRatio(true);
        r = new Button("", rock);
        p = new Button("", paper);
        s = new Button("", scissors);
        l = new Button("", lizard);
        sp = new Button("", spock);

        points = new Label();
        points.setFont(new Font("Comic Sans MS", 16));

        this.connect.setOnAction(e-> {
            primaryStage.setTitle("This is a client");
            int port = Integer.parseInt(enterPort.getText());
            String ip = enterIP.getText();  //the ip is 127.0.0.1 or "localhost"
            clientConnection = new Client(data->{
                Platform.runLater(()->{
                    if(data.toString().contains("LIST:")) {
                        cNum = Integer.parseInt(data.toString().charAt(data.toString().length()-1) + "");
                        youAre.setText("You are Client " + cNum + "\n");
                        updateClientLists(data.toString().substring(6, data.toString().length()-1));
                        wait.setText("");
                    }
                    else if(data.toString().equals("are you ready")){
                        System.out.println("IN ARE YOU READY");
                        if(clientConnection.inGame)
                            clientConnection.theGame.message = "not ready";
                        else {
                            clientConnection.theGame.message = "am ready";
                        }
                        clientConnection.sendGame();
                        wait.setText("");
                    }
                    else if(data.toString().equals("it's go time")){
                        System.out.println("IN GO TIME");
                        clientConnection.inGame = true;
                        wait.setText("");
                        primaryStage.setScene(createClientGui());
                        primaryStage.show();
                    }
                    else if(data.toString().contains("busy")) {
                        wait.setText(data.toString());
                        primaryStage.setScene(createLobby());
                    }
                    else if(data.toString().contains("finished")) {
                        if(Integer.parseInt(data.toString().substring(8)) == 0)
                            wait.setText("It's a draw!");
                        else if(Integer.parseInt(data.toString().substring(8)) == cNum)
                            wait.setText("You won!");
                        else
                            wait.setText("You lost.");
                        clientConnection.inGame = false;
                        clientConnection.theGame.message = "";
                        primaryStage.setScene(createLobby());
                      //clientConnection.sendGame();
                    }
                    else {
                        if(!clientConnection.inGame){
                            //wait.setText("");
                            listItems.getItems().add(data.toString());
                            primaryStage.setScene(createLobby());
                            primaryStage.show();
                        }
                    }
                    clientsList.setOnMouseClicked(r-> {
                        System.out.println("HERE1");
                        clean = clientsList.getSelectionModel().getSelectedItem().toString().replaceAll("\\D+", "");
                        System.out.println("HERE2");
                        int i = Integer.parseInt(clean);
                        System.out.println("HERE3");
                        clientConnection.theGame.message = "challenge client " + i;
                        System.out.println("HERE4 " + clientConnection.theGame.message);
                        clientConnection.sendGame();
                        System.out.println(i);
                    });
                });
            }, port, ip);
            isConnected = true;
            clientConnection.start();
            connect.setDisable(true);
        });

        r.setOnAction(e-> {
            clientConnection.theGame.choice = "rock";
            clientConnection.theGame.message = "go";
            primaryStage.setScene(endGui());
            clientConnection.sendGame();
        });
        p.setOnAction(e-> {
            clientConnection.theGame.choice = "paper";
            clientConnection.theGame.message = "go";
            primaryStage.setScene(endGui());
            clientConnection.sendGame();
        });
        s.setOnAction(e-> {
            clientConnection.theGame.choice = "scissors";
            clientConnection.theGame.message = "go";
            primaryStage.setScene(endGui());
            clientConnection.sendGame();
        });
        l.setOnAction(e-> {
            clientConnection.theGame.choice = "lizard";
            clientConnection.theGame.message = "go";
            primaryStage.setScene(endGui());
            clientConnection.sendGame();
        });
        sp.setOnAction(e-> {
            clientConnection.theGame.choice = "spock";
            clientConnection.theGame.message = "go";
            primaryStage.setScene(endGui());
            clientConnection.sendGame();
        });

        welcome = new Label("Welcome to Rock,\n  Paper, Scissors,\n   Lizard, Spock!\n");
        welcome.setFont(new Font("Comic Sans MS", 36));
        welcome.setStyle("-fx-font-weight: bold");

        this.entering = new VBox(20, welcome, thing, enterPort, thing2, enterIP, connect,wait);
        entering.setAlignment(Pos.CENTER);
        startPane = new BorderPane();
        startPane.setStyle("-fx-background-color: honeydew");
        startPane.setPadding(new Insets(70));
        startPane.setCenter(entering);
        startScene = new Scene(startPane, 500,600);

        listItems = new ListView<String>();
        listItems.setPrefHeight(330);
        clientsList = new ListView<String>();
        clientsList.setPrefHeight(150);


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if(isConnected) {
                    clientConnection.theGame.message = "bye";
                    clientConnection.sendGame();
                    try {
                        clientConnection.socketClient.close();
                    } catch (IOException e) {
                    }
                }
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private void updateClientLists(String s) {
        clientsList.getItems().clear();
        int b = 0;
        int e = 2;
        while(e <= s.length()) {
            clientsList.getItems().add("client " + s.substring(b,e));
            b += 2;
            e += 2;
        }
    }

    public Scene createClientGui() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(30));
        choices = new VBox(20, pick,r,p,s,l,sp);
        choices.setAlignment(Pos.CENTER);
        VBox top = new VBox(20, points);
        pane.setStyle("-fx-background-color: lightblue");
        pane.setTop(top);
        //pane.setLeft(listItems);
        pane.setCenter(choices);
        pane.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        return new Scene(pane, 500, 600);
    }

    public Scene createLobby(){
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: lavender");
        text = new VBox (youAre);
        text.setAlignment(Pos.CENTER);
        mid = new VBox (text, pos, clientsList);
        game = new VBox (info, listItems);
        pane.setCenter(wait);
        pane.setBottom(game);
        pane.setTop(mid);
        return new Scene(pane, 500, 600);
    }

    public Scene endGui() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(30));
        result = new Label("   Waiting for\n  other player...");
        result.setFont(new Font("Comic Sans MS", 20));
        bottom = new VBox(30, result);
        bottom.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: lavender");
        pane.setCenter(bottom);
        pane.setBorder(new Border(new BorderStroke(Color.TEAL, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        return new Scene(pane, 500, 600);
    }
}