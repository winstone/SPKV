package cn.sdp.pkv.dht;

import java.nio.ByteBuffer;

import cn.sdp.pkv.dht.utils.MurmurHash;
import cn.sdp.pkv.util.PKVConverter;

public class Murmur3Partitioner
{
    private static Murmur3Partitioner instance = new Murmur3Partitioner();
    public final long MINIMUM = Long.MIN_VALUE;
    
    private Murmur3Partitioner() {}
    
    public static Murmur3Partitioner getInstance()
    {
    	return instance;
    }
     
    public long getToken(String strKey)
    {
    	ByteBuffer key = PKVConverter.toByteBuffer(strKey); 
        if (key.remaining() == 0)
            return MINIMUM;

        long hash = MurmurHash.hash3_x64_128(key, key.position(), key.remaining(), 0)[0];
        return normalize(hash);
    }

    private long normalize(long v)
    {
        // We exclude the MINIMUM value; see getToken()
        return v == Long.MIN_VALUE ? Long.MAX_VALUE : v;
    }

}
