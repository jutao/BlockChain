package test.jt.btc;

import jt.btc.cli.CLI;

public class BlockChainTest {
    public static void main(String[] args) {
        try{
            String[] argss = {"print"};
            CLI cli = new CLI(argss);
            cli.parse();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
