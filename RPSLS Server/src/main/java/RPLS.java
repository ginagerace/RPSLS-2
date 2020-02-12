import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

//GuiServer class
public class RPLS extends Application{

    TextField enterPort;
    Button serverChoice;
    VBox bottom, buttonBox, top;
    Scene startScene;
    BorderPane startPane;
    Server serverConnection;
    ListView<String> listItems;
    Label con, gameInfo, portLab, space;
    int count = 0;
    PauseTransition pause = new PauseTransition(Duration.seconds(3));

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("RPSLS Server");

        portLab = new Label("Enter port number");
        portLab.setFont(new Font("Comic Sans MS", 16));
        enterPort = new TextField("5555");
        serverChoice = new Button("Start");
        space = new Label("\n\n");

        con = new Label("Clients Connected: "  + count);
        con.setFont(new Font("Comic Sans MS", 15));
        gameInfo = new Label("Game Information");
        gameInfo.setFont(new Font("Comic Sans MS", 15));

        this.serverChoice.setOnAction(e->{ primaryStage.setScene(createServerGui());
            primaryStage.setTitle("This is the Server");
            int port = Integer.parseInt(enterPort.getText());
            serverConnection = new Server(data -> {
                Platform.runLater(()->{
                    listItems.getItems().add(data.toString());
                    count = serverConnection.counter - serverConnection.left;
                    if(count < 0 )
                        count = 0;
                    con.setText("Clients Connected: "  + count);
                    primaryStage.setScene(createServerGui());
                });
            }, port);
        });

        buttonBox = new VBox(40, portLab, enterPort,serverChoice);
        startPane = new BorderPane();
        startPane.setPadding(new Insets(70));
        startPane.setCenter(buttonBox);
        startPane.setStyle("-fx-background-color: ivory");
        startPane.setPadding(new Insets(70));

        startScene = new Scene(startPane, 400,600);

        listItems = new ListView<String>();
        listItems.setPrefHeight(500);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    public Scene createServerGui() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: mistyrose");
        bottom = new VBox(con, space, gameInfo, listItems);
        pane.setCenter(bottom);
        return new Scene(pane, 400, 600);
    }
}