package jt.btc.cli;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/15
 * description：
 * ****************************************
 */
public class Main {
    public static void main(String[] args) {
        CLI cli=new CLI(args);
        cli.parse();
    }
}
