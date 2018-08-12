package test.jt.btc;

import jt.btc.block.Block;
import jt.btc.block.BlockChain;

public class BlockChainTest {
    public static void main(String[] args) {
        BlockChain blockChain=BlockChain.newBlockChain();
        blockChain.addBlock("send 1 BTC tO JT");
        blockChain.addBlock("send 2 BTC tO JT");

        for (Block block : blockChain.getBlockList()) {
            System.out.println("Prev. hash: " + block.getPreviousHash());
            System.out.println("Data: " + block.getData());
            System.out.println("Hash: " + block.getHash());
            System.out.println();
        }
    }
}
