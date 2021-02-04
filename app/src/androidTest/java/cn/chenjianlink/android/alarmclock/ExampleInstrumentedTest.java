package cn.chenjianlink.android.alarmclock;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import cn.chenjianlink.android.alarmclock.utils.LogUtil;
import cn.chenjianlink.android.alarmclock.utils.TimeUtil;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("cn.chenjianlink.android.alarmclock", appContext.getPackageName());
    }

    /**
     * 测试日志工具
     */
    @Test
    public void logUtilTest() {
        LogUtil.v("Test", "verbose");
    }

    @Test
    public void timeTest() {
        System.out.println(TimeUtil.dateFormat(TimeUtil.nextTime(10, 20, 7)));
    }

    @Test
    public void RingtoneTest() {
        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        LogUtil.d("Test", defaultUri.toString());
    }
}