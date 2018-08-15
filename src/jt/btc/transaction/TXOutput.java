package jt.btc.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/15
 * description：交易输出
 * ****************************************
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TXOutput {
    /**
     * 数值
     */
    private int value;
    /**
     * 公钥Hash
     */
    private byte[] pubKeyHash;


}
