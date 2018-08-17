package jt.btc.transaction;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/17
 * description：交易输入
 * ****************************************
 */
public class TXInput {
    /**
     * 交易Id的hash值
     */
    private byte[] txId;

    /**
     * 交易输出索引
     */
    private int txOutputIndex;

    /**
     * 解锁脚本
     */
    private String scriptSig;
}
