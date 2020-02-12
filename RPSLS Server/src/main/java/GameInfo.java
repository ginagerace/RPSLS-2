import java.io.Serializable;

public class GameInfo implements Serializable {

    int p1;
    int p2;
    int challenger;
    int challenged;
    String p1plays;
    String p2plays;
    String choice;
    String message;
    int name;
    boolean twoClients;

    GameInfo(){
        p1 = 0;
        p2 = 0;
        name = 0;
        p1plays = "";
        p2plays = "";
        message = "";
        choice = "";
        twoClients = false;
    }

    void setName(){
        if(p1 < p2)
            name = (p1*10) + p2;
        else
            name = (p2*10) + p1;
    }

    boolean isName(){
        int big, small;
        if(p1 > p2){
            big = p1;
            small = p2;
        }
        else{
            big = p2;
            small = p1;
        }
        if((name/10 == small) && (name%10 == big))
            return true;
        return false;
    }

    void update(int num){
        System.out.println(" NUM " + num);
        System.out.println(" p1 " + p1);
        if(num == p1) {
            p1plays = choice;
        }
        else
            p2plays = choice;
        choice = "";
    }

    //update score and returns winning player number (returns 0 if draw)
    int roundWinner() {
        int win;
        if(p1plays.equals(p2plays))
            win = 0;
        else if(p1plays.equals("rock") && (p2plays.equals("lizard") || p2plays.equals("scissors"))) {
            win = p1;
        }
        else if(p1plays.equals("lizard") && (p2plays.equals("spock") || p2plays.equals("paper"))){
            win = p1;
        }
        else if(p1plays.equals("spock") && (p2plays.equals("scissors") || p2plays.equals("rock"))){
            win = p1;
        }
        else if(p1plays.equals("scissors") && (p2plays.equals("paper") || p2plays.equals("lizard"))){
            win = p1;
        }
        else if(p1plays.equals("paper") && (p2plays.equals("spock") || p2plays.equals("rock"))){
            win = p1;
        }
        else{
            win = 2;
        }
        p1plays = "";
        p2plays = "";
        return win;
    }

    boolean bothPlayed(){
        if(p1plays.equals("") || p2plays.equals(""))
            return false;
        return true;
    }
}
