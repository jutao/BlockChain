package jt.btc.cli;

import jt.btc.block.Block;
import jt.btc.block.BlockChain;
import jt.btc.pow.ProofOfWork;
import jt.btc.store.RocksDBUtils;
import org.apache.commons.cli.*;

import static java.lang.System.exit;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/15
 * description：程序命令行工具入口
 * ****************************************
 */
public class CLI {
    private String[] args;
    private Options options = new Options();

    public CLI(String[] args) {
        this.args = args;
        options.addOption("h", "help", false, "show help");
        options.addOption("add", "addBlock", true, "add a block to the blockChain");
        options.addOption("print", "printChain", false, "print all the blocks of the blockChain");
    }

    /**
     * 命令行解析入口
     */
    public void parse() {
        this.validateArgs(args);
        try {
            CommandLineParser parser=new DefaultParser();
            CommandLine cmd=parser.parse(options,args);

            if(cmd.hasOption("h")){
                help();
            }
            if (cmd.hasOption("add")) {
                String data=cmd.getOptionValue("add");
                addBlock(data);
            }
            if (cmd.hasOption("print")) {
                printChain();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RocksDBUtils.getInstance().closeDB();
        }
    }

    /**
     * 验证入参
     *
     * @param args
     */
    private void validateArgs(String[] args) {
        if (args == null || args.length < 1) {
            help();
        }
    }

    /**
     * 打印帮助信息
     */
    private void help() {
        HelpFormatter helpFormatter=new HelpFormatter();
        helpFormatter.printHelp("Main",options);
        exit(0);
    }

    /**
     * 添加区块
     *
     * @param data
     */
    private void addBlock(String data) throws Exception {
        BlockChain blockChain=BlockChain.newBlockChain();
        blockChain.addBlock(data);
    }

    /**
     * 打印出区块链中的所有区块
     */
    private void printChain() {
        BlockChain blockChain=BlockChain.newBlockChain();
        for (BlockChain.BlockChainIterator iterator=blockChain.getBlockChainIterator();iterator.hashNext();){
            Block block = iterator.next();
            if(block!=null){
                boolean validate= ProofOfWork.newProofOfWork(block).validate();
                System.out.println(block.toString() + ", validate = " + validate);
            }
        }
    }
}
