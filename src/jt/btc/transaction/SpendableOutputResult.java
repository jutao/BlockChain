package jt.btc.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/21
 * description：SpendableOutputResult
 * ****************************************
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpendableOutputResult {
    /**
     * 交易时的支付金额
     */
    private int accumulated;
    /**
     * 未花费的交易
     */
    private Map<String, int[]> unspentOuts;
}
