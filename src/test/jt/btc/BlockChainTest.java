package test.jt.btc;

import jt.btc.block.Block;
import jt.btc.block.BlockChain;
import jt.btc.pow.ProofOfWork;

public class BlockChainTest {
    public static void main(String[] args) {
        BlockChain blockChain=BlockChain.newBlockChain();
        blockChain.addBlock("send 1 BTC tO JT");
        blockChain.addBlock("send 2 BTC tO JT");
        blockChain.addBlock("send 3 BTC tO SDL");
        blockChain.addBlock("send 4 BTC tO YY");

        for (Block block : blockChain.getBlockList()) {
            System.out.println("Prev. hash: " + block.getPreviousHash());
            System.out.println("Data: " + block.getData());
            System.out.println("Hash: " + block.getHash());
            System.out.println("Nonce: " + block.getNonce());

            ProofOfWork pow = ProofOfWork.newProofOfWork(block);

            System.out.println("Pow valid: " + pow.validate() + "\n");
        }
    }
}
