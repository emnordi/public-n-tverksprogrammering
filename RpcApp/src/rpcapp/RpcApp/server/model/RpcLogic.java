package rpcapp.RpcApp.server.model;

import java.util.List;

/**
 * Takes the list of inputed entries and compares to the one input by the user
 * to see how many points to award. Returns the amount of points won that round.
 */
public class RpcLogic {

    public int getWinner(List<String> allPicks, String chosen) {
        int res = 0;

        for (String pick : allPicks) {
            if (chosen.equals("rock") && pick.equals("scissors")) {
                res++;
            } else if (chosen.equals("paper") && pick.equals("rock")) {
                res++;
            } else if (chosen.equals("scissors") && pick.equals("paper")) {
                res++;
            }
        }
        return res;
    }
    public int getTotal(int current, int round){
        return current + round;
    }
    
    public String toString(int x){
        return Integer.toString(x);
    }
}
