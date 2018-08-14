package jt.btc.pow;

import jt.btc.block.Block;
import jt.btc.uti.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/13
 * description：工作量证明
 * <p>
 * 1、每次区块被添加到区块链之前，先要进行挖矿（Pow）
 * 2、挖矿过程中，产生的 Hash 值，如果小于难度目标值则添加进区块，否则继续挖矿，直到找到正确的Hash为止
 * 3、最后，验证区块Hash是否有效
 * ****************************************
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ProofOfWork {
    /**
     * 难度目标位
     * <p>
     * 设定一个难度目标位TARGET_BITS，表示最终挖矿挖出来Hash值，转化为二进制后，与256相比，
     * 长度少了多少bit，也即二进制前面有多少bit是零.
     * <p>
     * ARGET_BITS 越大，最终targetValue就越小，要求计算出来的Hash越来越小，也就是挖矿的难度越来越大。
     * <p>
     * 我们这里的TARGET_BITS是固定的，但是在真实的比特币中，难度目标是随着时间的推推，会动态调整的。
     */
    public static final int TARGET_BITS = 9;

    /**
     * 区块
     */
    private Block block;

    /**
     * 难度目标值
     * <p>
     * 由于数值比较大，这里要使用BitInteger类型。
     */
    private BigInteger target;

    /**
     * 创建新的工作量证明，设定难度目标值
     * <p>
     * 对1进行移位运算，将1向左移动 (256 - TARGET_BITS) 位，得到我们的难度目标值
     *
     * 向左移动的位数越多，数值越小。前面的0越多。
     * 可以想象我们随机生成一个[0，32)的十进制数字，你如果想得到一个小于20的数字，概率是20/32，获得小于10的数字概率是10/32
     * 所以我们只需要指定左移位数增加(数字变小)，并且规定随机生成的数必须小于这个数字，左移位数越多，获得小于这个数
     * 的概率也就越小，工作量也就越大
     *
     * @param block
     * @return
     */
    public static ProofOfWork newProofOfWork(Block block) {
        //相当于2^(256 - TARGET_BITS)
        BigInteger targetValue = BigInteger.valueOf(1).shiftLeft(256 - TARGET_BITS);
        return new ProofOfWork(block, targetValue);
    }

    /**
     * 准备数据
     * <p>
     * 注意：在准备区块数据时，一定要从原始数据类型转化为byte[]，不能直接从字符串进行转换
     *
     * @param nonce
     * @return
     */
    private byte[] prepareData(long nonce) {
        byte[] prevBlockHashBytes = {};
        if (StringUtils.isNoneBlank(this.getBlock().getPreviousHash())) {
            prevBlockHashBytes = new BigInteger(this.getBlock().getPreviousHash(), 16).toByteArray();
        }

        return ByteUtils.merge(
                //前一个区块（父区块）的Hash值
                prevBlockHashBytes,
                // 区块中的交易数据
                this.getBlock().getData().getBytes(),
                //区块生成的时间
                ByteUtils.toBytes(this.getBlock().getTimeStamp()),
                //难度目标
                ByteUtils.toBytes(TARGET_BITS),
                //用于工作量证明算法的计数器
                ByteUtils.toBytes(nonce));
    }

    /**
     * 运行工作量证明，开始挖矿，找到小于难度目标值的Hash
     *
     * @return
     */
    public PowResult run() {
        long nonce = 0;
        String shaHex = "";
        log.info("Mining the block containing：{} \n", new Object[]{this.getBlock().getData()});
        long startTime = System.currentTimeMillis();
        while (nonce < Long.MAX_VALUE) {
            byte[] data = this.prepareData(nonce);
            shaHex = DigestUtils.sha256Hex(data);
            //计算出的值小于目标
            if (new BigInteger(shaHex, 16).compareTo(this.target) == -1) {
                log.info("Elapsed Time: {} seconds \n", new Object[]{(float) (System.currentTimeMillis() - startTime) / 1000});
                log.info("correct hash Hex: {} \n", new Object[]{shaHex});
                break;
            }else {
                nonce++;
            }
        }
        return new PowResult(nonce,shaHex);
    }


    /**
     * 验证区块是否有效
     *
     * @return
     */
    public boolean validate() {
        byte[] data=this.prepareData(this.getBlock().getNonce());
        return new BigInteger(DigestUtils.sha256Hex(data),16).compareTo(this.target)==-1;
    }
}
