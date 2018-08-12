package jt.btc.block;

import jt.btc.uti.ByteUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.time.Instant;

/**
 * 区块
 */
public class Block {
    private static final String ZERO_HASH = Hex.encodeHexString(new byte[32]);

    /**
     * 区块hash值
     */
    private String hash;
    /**
     * 前一个区块的hash值
     */
    private String previousHash;
    /**
     * 区块数据
     */
    private String data;
    /**
     * 区块创建时间(单位:秒)
     */
    private long timeStamp;

    public Block() {
    }

    public Block(String hash, String previousHash, String data, long timeStamp) {
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    /**
     * <p> 创建创世区块 </p>
     *
     * @return
     */
    public static Block newGenesisBlock() {
        return Block.newBlock("", "Genesis Block");
    }


    /**
     * <p> 创建新区块 </p>
     *
     * @param previousHash
     * @param data
     * @return
     */
    public static Block newBlock(String previousHash, String data) {
        Block block = new Block(ZERO_HASH, previousHash, data, Instant.now().getEpochSecond());
        block.setHash();
        return block;
    }


    /**
     * 计算区块Hash
     * <p>
     * 注意：在准备区块数据时，一定要从原始数据类型转化为byte[]，不能直接从字符串进行转换
     * <p>
     * 加密Hash值，一个通过SHA256算法对区块头进行二次哈希计算而得到的数字指纹。Hash值用于确保
     * blockChain的安全。Hash计算是计算敏感的操作，即使在高性能电脑也需要花费一段时间来完成计算
     * (这也就是为什么人们购买高性能GPU进行比特币挖矿的原因)。blockChain架构设计有意使Hash计算
     * 变得困难，这样做是为了加大新增一个block的难度，进而防止block在增加后被随意修改。
     *
     * @return
     */
    private void setHash() {
        byte[] prevBlockHashBytes = {};
        if (StringUtils.isNoneBlank(this.getPreviousHash())) {
            prevBlockHashBytes = new BigInteger(this.getPreviousHash(), 16).toByteArray();
        }
        byte[] headers = ByteUtils.merge(prevBlockHashBytes, this.getData().getBytes(), ByteUtils.toBytes(this.getTimeStamp()));
        this.setHash(DigestUtils.sha256Hex(headers));
    }
}