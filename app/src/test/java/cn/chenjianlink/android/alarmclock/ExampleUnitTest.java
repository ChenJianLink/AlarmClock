package cn.chenjianlink.android.alarmclock;

import org.junit.Test;

import cn.chenjianlink.android.alarmclock.utils.CommonValue;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void numTest() {
        byte by = 0b1111111;
        System.out.println(by);
    }

    @Test
    public void byteTest() {
        System.out.println((byte) (Integer.parseUnsignedInt("1111111", 2)));
    }

    @Test
    public void andTest() {
        int i = 0b0000100;
        System.out.println(i);
        System.out.println((4 & CommonValue.SUNDAY));
    }
}