package jt.btc.pow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/13
 * description：工作量计算结果
 * ****************************************
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowResult {
    /**
     * 计数器
     */
    private long nonce;
    /**
     * hash值
     */
    private String hash;
}
