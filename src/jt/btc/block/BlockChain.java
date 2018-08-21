package jt.btc.block;


import jt.btc.store.RocksDBUtils;
import jt.btc.transaction.SpendableOutputResult;
import jt.btc.transaction.TXInput;
import jt.btc.transaction.TXOutput;
import jt.btc.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

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
     * 从 DB 从恢复区块链数据
     *
     * @return
     * @throws Exception
     */
    public static BlockChain initBlockchainFromDB() throws Exception {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (lastBlockHash == null) {
            throw new Exception("ERROR: Fail to init blockChain from db. ");
        }
        return new BlockChain(lastBlockHash);
    }


    /**
     * <p> 添加区块  </p>
     *
     * @param transactions
     */
    public void addBlock(Transaction[] transactions) throws Exception {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (lastBlockHash == null) {
            throw new Exception("ERROR: Fail to get last block hash ! ");
        }
        Block block = Block.newBlock(lastBlockHash, transactions);
        this.addBlock(block);
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
     * 查找钱包地址对应的所有UTXO
     *
     * @param address 钱包地址
     * @return
     */
    public TXOutput[] findUTXO(String address) throws Exception {
        Transaction[] unspentTxs = this.findUnspentTransactions(address);
        TXOutput[] utxos = {};
        if (unspentTxs == null || unspentTxs.length == 0) {
            return utxos;
        }
        for (Transaction tx : unspentTxs) {
            for (TXOutput txOutput : tx.getOutputs()) {
                if (txOutput.canBeUnlockedWith(address)) {
                    utxos = ArrayUtils.add(utxos, txOutput);
                }

            }
        }
        return utxos;
    }

    /**
     * <p> 创建区块链 </p>
     *
     * @return
     */
    public static BlockChain createBlockChain(String address) {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (StringUtils.isBlank(lastBlockHash)) {
            Transaction coinbaseTX = Transaction.newCoinbaseTX(address, "");
            Block genesisBlock = Block.newGenesisBlock(coinbaseTX);
            lastBlockHash = genesisBlock.getHash();
            RocksDBUtils.getInstance().putBlock(genesisBlock);
            RocksDBUtils.getInstance().putLastBlockHash(lastBlockHash);
        }
        return new BlockChain(lastBlockHash);
    }

    /**
     * 打包交易，进行挖矿
     *
     * @param transactions
     */
    public void mineBlock(Transaction[] transactions) throws Exception {
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        if (lastBlockHash == null) {
            throw new Exception("ERROR: Fail to get last block hash ! ");
        }
        Block block = Block.newBlock(lastBlockHash, transactions);
        this.addBlock(block);
    }


    /**
     * 查找钱包地址对应的所有未花费的交易
     *
     * @param address 钱包地址
     * @return
     */
    private Transaction[] findUnspentTransactions(String address) throws Exception {
        Map<String, int[]> allSpentTXOs = this.getAllSpentTXOs(address);
        Transaction[] unspentTxs = {};
        // 再次遍历所有区块中的交易输出
        for (BlockChainIterator blockchainIterator = this.getBlockChainIterator(); blockchainIterator.hashNext(); ) {
            Block block = blockchainIterator.next();
            for (Transaction transaction : block.getTransactions()) {
                String txId = Hex.encodeHexString(transaction.getTxId());
                int[] spentOutIndexArray = allSpentTXOs.get(txId);
                for (int outIndex = 0; outIndex < transaction.getOutputs().length; outIndex++) {
                    if (spentOutIndexArray != null && ArrayUtils.contains(spentOutIndexArray, outIndex)) {
                        continue;
                    }
                    // 保存不存在 allSpentTXOs 中的交易
                    if (transaction.getOutputs()[outIndex].canBeUnlockedWith(address)) {
                        unspentTxs = ArrayUtils.add(unspentTxs, transaction);
                    }
                }
            }
        }
        return unspentTxs;
    }

    /**
     * 从交易输入中查询区块链中所有已被花费了的交易输出
     *
     * @param address 钱包地址
     * @return 交易ID以及对应的交易输出下标地址
     * @throws Exception
     */
    private Map<String, int[]> getAllSpentTXOs(String address) throws Exception {
        // 定义TxId ——> spentOutIndex[]，存储交易ID与已被花费的交易输出数组索引值
        Map<String, int[]> spentTXOs = new HashMap<>();
        for (BlockChainIterator blockChainIterator = this.getBlockChainIterator(); blockChainIterator.hashNext(); ) {
            Block block = blockChainIterator.next();
            for (Transaction transaction : block.getTransactions()) {
                //如果是 coinBase 交易，直接跳过，因为它不存在引用前一个区块的交易输出
                if (transaction.isCoinBase()) {
                    continue;
                }
                for (TXInput txInput : transaction.getInputs()) {
                    if (txInput.canUnlockOutputWith(address)) {
                        String inTxId = Hex.encodeHexString(txInput.getTxId());
                        int[] spentOutIndexArray = spentTXOs.get(inTxId);
                        if (spentOutIndexArray == null) {
                            spentTXOs.put(inTxId, new int[]{txInput.getTxOutputIndex()});
                        } else {
                            spentOutIndexArray = ArrayUtils.add(spentOutIndexArray, txInput.getTxOutputIndex());
                            spentTXOs.put(inTxId, spentOutIndexArray);
                        }
                    }
                }
            }
        }
        return spentTXOs;
    }

    /**
     * 寻找能够花费的交易
     *
     * @param address 钱包地址
     * @param amount  花费金额
     */
    public SpendableOutputResult findSpendableOutputs(String address, int amount) throws Exception {
        Transaction[] unspentTXs = this.findUnspentTransactions(address);
        int accumulated = 0;
        Map<String, int[]> unspentOuts = new HashMap<>();
        for (Transaction tx : unspentTXs) {
            String txId = Hex.encodeHexString(tx.getTxId());
            for (int outId = 0; outId < tx.getOutputs().length; outId++) {
                TXOutput txOutput = tx.getOutputs()[outId];
                if (txOutput.canBeUnlockedWith(address) && accumulated < amount) {
                    accumulated += txOutput.getValue();
                    int[] outIds = unspentOuts.get(txId);
                    if (outIds == null) {
                        outIds = new int[]{outId};
                    } else {
                        outIds = ArrayUtils.add(outIds, outId);
                    }
                    unspentOuts.put(txId, outIds);
                    if (accumulated >= amount) {
                        break;
                    }
                }
            }
        }
        return new SpendableOutputResult(accumulated,unspentOuts);
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
            Block currentBlock = RocksDBUtils.getInstance().getBlock(currentBlockHash);
            if (currentBlock != null) {
                this.currentBlockHash = currentBlock.getPreviousHash();
                return currentBlock;
            }
            return null;
        }
    }

    public BlockChainIterator getBlockChainIterator() {
        return new BlockChainIterator(lastBlockHash);
    }
}
