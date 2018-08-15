package test.jt.btc;

import jt.btc.block.Block;
import jt.btc.block.BlockChain;
import jt.btc.pow.ProofOfWork;

public class BlockChainTest {
    public static void main(String[] args) {
        try{
            BlockChain blockChain=BlockChain.newBlockChain();
            blockChain.addBlock("send 1 BTC tO JT");
            blockChain.addBlock("send 2 BTC tO JT");
            blockChain.addBlock("send 3 BTC tO SDL");
            blockChain.addBlock("send 4 BTC tO YY");

            for (BlockChain.BlockChainIterator iterator=blockChain.getBlockChainIterator();iterator.hashNext();) {
                Block block=iterator.next();
                if(block!=null){
                    boolean validate = ProofOfWork.newProofOfWork(block).validate();
                    System.out.println(block.toString() + ", validate = " + validate);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
