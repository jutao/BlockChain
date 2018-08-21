package jt.btc.cli;

import jt.btc.block.Block;
import jt.btc.block.BlockChain;
import jt.btc.pow.ProofOfWork;
import jt.btc.store.RocksDBUtils;
import jt.btc.transaction.TXOutput;
import jt.btc.transaction.Transaction;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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

        Option helpCmd = Option.builder("h").desc("show help").build();
        options.addOption(helpCmd);

        Option address = Option.builder("address").hasArg(true).desc("Source wallet address").build();
        Option sendFrom = Option.builder("from").hasArg(true).desc("Source wallet address").build();
        Option sendTo = Option.builder("to").hasArg(true).desc("Destination wallet address").build();
        Option sendAmount = Option.builder("amount").hasArg(true).desc("Amount to send").build();

        options.addOption(address);
        options.addOption(sendFrom);
        options.addOption(sendTo);
        options.addOption(sendAmount);
    }

    /**
     * 命令行解析入口
     */
    public void parse() {
        this.validateArgs(args);
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            switch (args[0]) {
                case "createblockchain":
                    String createblockchainAddress = cmd.getOptionValue("address");
                    if (StringUtils.isBlank(createblockchainAddress)) {
                        help();
                    }
                    this.createBlockchain(createblockchainAddress);
                    break;
                case "getbalance":
                    String getBalanceAddress = cmd.getOptionValue("address");
                    if (StringUtils.isBlank(getBalanceAddress)) {
                        help();
                    }
                    this.getBalance(getBalanceAddress);
                    break;
                case "h":
                    this.help();
                    break;
                case "print":
                    this.printChain();
                    break;
                case "send":
                    String sendFrom = cmd.getOptionValue("from");
                    String sendTo = cmd.getOptionValue("to");
                    String sendAmount = cmd.getOptionValue("amount");
                    if (StringUtils.isBlank(sendFrom) ||
                            StringUtils.isBlank(sendTo) ||
                            !NumberUtils.isDigits(sendAmount)) {
                        help();
                    }
                    this.send(sendFrom, sendTo, Integer.valueOf(sendAmount));
                    break;
                default:
                    this.help();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RocksDBUtils.getInstance().closeDB();
        }
    }

    /**
     * 创建区块链
     *
     * @param address
     */
    private void createBlockchain(String address) throws Exception {
        BlockChain.createBlockChain(address);
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
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Main", options);
        exit(0);
    }

    /**
     * 转账
     *
     * @param from
     * @param to
     * @param amount
     */
    private void send(String from, String to, int amount) throws Exception {
        BlockChain blockChain = BlockChain.createBlockChain(from);
        Transaction transaction = Transaction.newUTXOTransaction(from, to, amount, blockChain);
        blockChain.mineBlock(new Transaction[]{transaction});
        RocksDBUtils.getInstance().closeDB();
        System.out.println("Success!");
    }

    /**
     * 打印出区块链中的所有区块
     */
    private void printChain() throws Exception {
        BlockChain blockChain = BlockChain.initBlockchainFromDB();
        for (BlockChain.BlockChainIterator iterator = blockChain.getBlockChainIterator(); iterator.hashNext(); ) {
            Block block = iterator.next();
            if (block != null) {
                boolean validate = ProofOfWork.newProofOfWork(block).validate();
                System.out.println(block.toString() + ", validate = " + validate);
            }
        }
    }

    /**
     * 查询钱包余额
     *
     * @param address 钱包地址
     */
    private void getBalance(String address) throws Exception {
        BlockChain blockChain = BlockChain.createBlockChain(address);
        TXOutput[] txOutputs = blockChain.findUTXO(address);
        int balance = 0;
        if (txOutputs != null && txOutputs.length > 0) {
            for (TXOutput txOutput : txOutputs) {
                balance += txOutput.getValue();
            }
        }
        System.out.printf("Balance of '%s': %d\n", address, balance);
    }
}
