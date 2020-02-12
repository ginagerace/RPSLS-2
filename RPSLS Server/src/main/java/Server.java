import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{
    public
    int counter = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    int port;
    int left = 1;
    String p1Hold, p2Hold;
    private Consumer<Serializable> callback;
    GameInfo theGame = new GameInfo();
    GameInfo tempGame = new GameInfo();
    GameInfo aGame = new GameInfo();
    ArrayList<GameInfo> games = new ArrayList<GameInfo>();

    Server(Consumer<Serializable> call, int p){
        //theGame = new GameInfo();
        callback = call;
        server = new TheServer();
        server.start();
        port = p;
        p1Hold = "";
        p2Hold = "";
    }

    public class TheServer extends Thread{

        public void run() {
            try(ServerSocket mysocket = new ServerSocket(port);){
                System.out.println("Server is waiting for a client!");

                while(true) {

                    ClientThread c = new ClientThread(mysocket.accept());
                    callback.accept("client has connected to server: " + "client #" + counter);
                    clients.add(c);
                    c.start();
                    counter++;
                }
            }//end of try
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }

    class ClientThread extends Thread{

        Socket connection;
        int count;
//        int challenger;
//        int challenged;
        ObjectInputStream in;
        ObjectOutputStream out;


        ClientThread(Socket s){
            this.connection = s;
            this.count = counter;
            tempGame.name = 0;
            games.add(tempGame);
        }

        public void updateClients(String message) {
            for(int i = 0; i < clients.size(); i++) {
                theGame.message = message;
                ClientThread t = clients.get(i);
                try {
                    t.out.writeObject(theGame);
                    t.out.reset();
                }
                catch(Exception e) {}
            }
        }

        public void sendClientList(){
            for(int i = 0; i < clients.size(); i++) {
                String nums = "";
                ClientThread t = clients.get(i);
                int n0 = t.count;

                for (int j = 0; j < clients.size(); j++) {
                    ClientThread t2 = clients.get(j);
                    int n = t2.count;
                    if(n != n0)
                        nums += n + " ";
                }
                theGame.message = "LIST: " + nums + n0;
                try {
                    t.out.writeObject(theGame);
                    t.out.reset();
                } catch (Exception e) {
                }
            }
        }

        public void sendGame() {
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.out.writeObject(theGame);
                    t.out.reset();
                }
                catch(Exception e) {}
            }
        }

        private void individualMessage(int store, String s) {
            theGame.message = s;
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                if(t.count == store) {
                    try {
                        t.out.writeObject(theGame);
                        t.out.reset();
                    } catch (Exception e) {
                    }
                }
            }
        }

        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            updateClients("new client on server: client #"+count);
            if(clients.size() < 2) {
                updateClients("waiting for second player");
            }
            sendClientList();

            while(true) {
                try {
                    tempGame = (GameInfo) in.readObject();
                    System.out.println("NAME IS " + tempGame.name);
                    aGame = getGame(tempGame.name);
                    if (aGame.message.contains("challenge")) {
                        System.out.println("IN CHALLENGE");
                        aGame.challenged = Integer.parseInt(aGame.message.substring(aGame.message.length() - 1));
                        individualMessage(aGame.challenged, "are you ready");
                        aGame.challenger = count;
                        callback.accept("client " + aGame.challenger + " challenged client " + aGame.challenged);
                    }
                    else if (aGame.message.equals("am ready")) {
                        System.out.println("IN AM READY");
                       // if(theGame.p1 == 0) {
                            theGame.p1 = aGame.challenger;
                            theGame.p2 = aGame.challenged;
                            aGame.challenged = 0;
                            aGame.challenger = 0;
                            theGame.setName();
                            System.out.println("2ED " + theGame.p2);
                            System.out.println("2ER " + theGame.p1);
                            individualMessage(theGame.p1, "it's go time");
                            individualMessage(theGame.p2, "it's go time");
                            theGame.message = "";
                            games.add(theGame);
                            System.out.println("ADDING " + theGame.name);
                      //  }
//                        else{
//                            aGame = theGame;
//                            aGame.p1 = theGame.challenger;
//                            aGame.p2 = theGame.challenged;
//                            aGame.setName();
//                            System.out.println("2ED " + aGame.p2);
//                            System.out.println("2ER " + aGame.p1);
//                            individualMessage(aGame.p1, "it's go time");
//                            individualMessage(aGame.p2, "it's go time");
//                            aGame.message = "";
//                            games.add(aGame);
//                            System.out.println("ADDING " + aGame.name);
//                        }
                    }
                    else if (aGame.message.equals("not ready")) {
                        //aGame.challenged = 0;
                        //aGame.challenger = 0;
                        individualMessage(aGame.challenger, "client " + aGame.challenged + " is busy");
                        System.out.println("hold2 is " + aGame.challenged);
                        aGame.challenged = 0;
                        aGame.challenger = 0;
                        aGame.message = "";
                    }
                    else if (aGame.message.equals("go")) {
                        theGame = getGame(aGame.name);
                        callback.accept("client: " + count + " played: " + theGame.choice);
                        theGame.update(count);
                        System.out.println("COUNT " + count);
                        aGame.message = "";
                        theGame.message = "";
                        System.out.println("P1 " + theGame.p1plays);
                        System.out.println("P2 " + theGame.p2plays);
                        sendGame();
                    }

                    if (aGame.bothPlayed()) {
                        theGame = getGame(aGame.name);
                        System.out.println("BOTH PLAYED");
                        p1Hold = theGame.p1plays;
                        p2Hold = theGame.p2plays;
                        int win = theGame.roundWinner();
                        System.out.println("--ED " + theGame.p2);
                        System.out.println("--ER " + theGame.p1);
                        if (win == 0) {
                            individualMessage(theGame.p1, "finished" + win);
                            individualMessage(theGame.p2, "finished" + win);
                            theGame.message = "Client "+theGame.p1+" played "+p1Hold+"\nClient "+theGame.p2+" played "+p2Hold+"\nIt's a draw!";
                            callback.accept("It's a draw!");
                        }
                        else {
                            individualMessage(theGame.p1, "finished" + win);
                            individualMessage(theGame.p2, "finished" + win);
                            theGame.message = "Client "+theGame.p1+" played "+p1Hold+"\nClient "+theGame.p2+" played "+p2Hold+"\nClient "+win+ " wins the round!";
                            callback.accept("Client "+win+ " wins the round!");
                        }
                        sendGame();
                        System.out.println("REMOVING game " + theGame.name);
                        games.remove(theGame);
                        tempGame.p1 = 0;
                        tempGame.p2 = 0;
                        tempGame.challenged = 0;
                        tempGame.challenger = 0;
                        tempGame.name = 0;
                        theGame = tempGame;
                        System.out.println("Game is now " + tempGame.name);
                        //theGame = new GameInfo(0,0);
                    }
                }
                catch(Exception e) {
                    if(aGame.message.equals("bye")) {
                        callback.accept("OOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                        left++;
                        clients.remove(this);
                        if(clients.size() < 2)
                            updateClients("waiting for second player");
                        sendClientList();
                        //sendGame();
                        break;
                    }
                }
            }
        }//end of run

        private GameInfo getGame(int name) {
            for(int i=0; i<games.size(); i++){
                GameInfo holder = games.get(i);
                if(holder.name == name) {
                    System.out.println("returning Game " + name);
                    holder.message = tempGame.message;
                    holder.choice = tempGame.choice;
                    return holder;
                }
            }
            System.out.println("NEW GAME");
            return new GameInfo();
        }
    }//end of client thread
}