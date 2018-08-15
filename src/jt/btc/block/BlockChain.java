package jt.btc.block;


import jt.btc.store.RocksDBUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

    private String lastBlockHash;


    /**
     * <p> 添加区块  </p>
     *
     * @param data
     */
    public void addBlock(String data) throws Exception {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            throw new Exception("Fail to add block into blockchain ! ");
        }
        this.addBlock(Block.newBlock(lastBlockHash, data));
    }

    /**
     * <p> 添加区块  </p>
     *
     * @param block
     */
    public void addBlock(Block block) {
        RocksDBUtils.getInstance().putLastBlockHash(block.getHash());
        RocksDBUtils.getInstance().putBlock(block);
        this.lastBlockHash = block.getHash();
    }

    /**
     * <p> 创建区块链 </p>
     *
     * @return
     */
    public static BlockChain newBlockChain() {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            Block genesisBlock = Block.newGenesisBlock();
            lastBlockHash = genesisBlock.getHash();
            RocksDBUtils.getInstance().putBlock(genesisBlock);
            RocksDBUtils.getInstance().putLastBlockHash(lastBlockHash);
        }
        return new BlockChain(lastBlockHash);
    }

    /**
     * 区块链迭代器
     */
    public class BlockChainIterator {

        private String currentBlockHash;

        public BlockChainIterator(String currentBlockHash) {
            this.currentBlockHash = currentBlockHash;
        }

        /**
         * 是否有下一个区块
         *
         * @return
         */
        public boolean hashNext() {
            if (StringUtils.isBlank(currentBlockHash)) {
                return false;
            }
            Block lastBlock = RocksDBUtils.getInstance().getBlock(currentBlockHash);
            if (lastBlock == null) {
                return false;
            }
            // 创世区块直接放行
            if (lastBlock.getPreviousHash().length() == 0) {
                return true;
            }
            return RocksDBUtils.getInstance().getBlock(lastBlock.getPreviousHash()) != null;
        }

        /**
         * 返回区块
         *
         * @return
         */
        public Block next() {
            Block currentBlock=RocksDBUtils.getInstance().getBlock(currentBlockHash);
            if(currentBlock!=null){
                this.currentBlockHash=currentBlock.getPreviousHash();
                return currentBlock;
            }
            return null;
        }
    }
    public BlockChainIterator getBlockChainIterator() {
        return new BlockChainIterator(lastBlockHash);
    }
}
