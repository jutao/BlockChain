package jt.btc.cli;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/23
 * description：
 * ****************************************
 */
public class CLIHelper {
    public static void  createblockchain(String address){
        String[] argss = {"createblockchain","-address","jutao"};
        CLI cli = new CLI(argss);
        cli.parse();
    }
    public static void  getbalance(String address){
        String[] argss = {"getbalance","-address",address};
        CLI cli = new CLI(argss);
        cli.parse();
    }
    public static void  print(){
        String[] argss = {"print"};
        CLI cli = new CLI(argss);
        cli.parse();
    }

    public static void sendBitCoin(String from,String to,String amount) {
        String[] argss = {"send","-from",from,"-to",to,"-amount",amount};
        CLI cli = new CLI(argss);
        cli.parse();
    }
}
