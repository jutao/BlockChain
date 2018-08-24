package test.jt.btc;

import jt.btc.cli.CLIHelper;

public class BlockChainTest {
    public static void main(String[] args) {
        try{
            CLIHelper.getbalance("zyt");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
