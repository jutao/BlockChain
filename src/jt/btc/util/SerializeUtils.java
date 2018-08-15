package jt.btc.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * ****************************************
 * author：琚涛
 * time：2018/8/15
 * description：序列化工具类
 * RocksDB的Key与Value只能以byte[]的形式进行存储，这里我们需要用到序列化与反序列化库
 * ****************************************
 */
public class SerializeUtils {
    /**
     * 反序列化
     *
     * @param bytes 对象对应的字节数组
     * @return
     */
    public static Object deSerialize(byte[] bytes) {
        Input input = new Input(bytes);
        Object obj = new Kryo().readClassAndObject(input);
        input.close();
        return obj;
    }

    /**
     * 序列化
     *
     * @param object 需要序列化的对象
     * @return
     */
    public static byte[] serialize(Object object) {
        Output output = new Output(4096, -1);
        new Kryo().writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
}
