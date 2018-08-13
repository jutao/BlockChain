package jt.btc.block;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * 区块链
 * 区块链本质上是一种有序、反向链接链表的数据结构。这意味着，
 * block按照插入的顺序存放，同时每个block都保存指向上一个
 * block的链接。这种结构保证可以快速获取最新插入的block同
 * 时获取它的hash值。这种结构保证可以快速获取最新插入的block
 * 同时（高效地）获取它的hash值。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class BlockChain {

    private List<Block> blockList;

    public List<Block> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<Block> blockList) {
        this.blockList = blockList;
    }


    /**
     * <p> 添加区块  </p>
     *
     * @param data
     */
    public void addBlock(String data) {
        Block previousBlock=blockList.get(blockList.size()-1);
        this.addBlock(Block.newBlock(previousBlock.getHash(),data));
    }

    /**
     * <p> 添加区块  </p>
     *
     * @param block
     */
    public void addBlock(Block block) {
        this.blockList.add(block);
    }

    /**
     * <p> 创建区块链 </p>
     *
     * @return
     */
    public static BlockChain newBlockChain() {
        List<Block> blocks = new LinkedList<>();
        blocks.add(Block.newGenesisBlock());
        return new BlockChain(blocks);
    }
}
