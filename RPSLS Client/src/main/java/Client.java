import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	Socket socketClient;
	GameInfo theGame = new GameInfo();
	int port;
	String IP;
	ObjectOutputStream out;
	ObjectInputStream in;
	boolean inGame;

	private Consumer<Serializable> callback;

	Client(Consumer<Serializable> call, int p, String ip){
		callback = call;
		port = p;
		IP = ip;
		inGame = false;
	}

	public void run() {

		try {
			socketClient= new Socket(IP,port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}

		while(true) {
			try {
				theGame = (GameInfo) in.readObject();
				if(!theGame.message.equals("")) {
					callback.accept(theGame.message);
					theGame.message = "";
					sendGame();
				}
			}
			catch(Exception e) {}
		}
	}

	public void sendGame() {

		try {
			out.writeObject(theGame);
			out.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}