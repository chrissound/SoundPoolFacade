// package packagenamegoeshereabcxyz;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 */
public class AudioPlayer {

    public HashMap<String, Integer> soundIndexes = new HashMap<String, Integer>();
    public ArrayList<Integer> loopingStreamIds = new ArrayList<Integer>();
    public SoundPool soundPool = null;
    private final Semaphore soundLoaded = new Semaphore(0);
    public Integer soundLoadedCounter = 0;
    private Looper looper;
    private SoundPoolFactory soundPoolFactory;

    AudioPlayer() {
        soundPoolFactory = new SoundPoolFactory();
    }

    public class SoundPoolFactory extends Thread {

        public SoundPool soundPool;
        public HashMap<String, AssetFileDescriptor> soundLoadList = new HashMap<String, AssetFileDescriptor>();

        public void run()
        {
            Looper.prepare();
            looper = Looper.myLooper();

            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundLoaded.release();
                }
            });

            Iterator iterator = soundLoadList.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                loadSound((AssetFileDescriptor)entry.getValue(), (String)entry.getKey());
            }

            Looper.loop();
        }

        public void loadSound(AssetFileDescriptor assetFile, String soundIndex)
        {
            try {
                soundIndexes.put(soundIndex, soundPool.load(assetFile, 1));
            } catch (Exception e)
            {
            }
        }

        public SoundPool getSoundPoolInstance()
        {
            try {
                return soundPool;
            }
            catch (Exception e)
            {
            }

            return null;
        }
    }

    public void stopAll() {
        for (Integer loopingStreamId : loopingStreamIds) {
            soundPool.stop(loopingStreamId);
        }
    }

    public void waitTillLoaded() {
        try {
            soundPoolFactory.start();
            soundLoaded.acquire(soundLoadedCounter);
            looper.quit();

            soundPool = soundPoolFactory.getSoundPoolInstance();

        } catch (Exception e)
        {
        }
    }

    public void release()
    {
        soundPool.release();
        soundPool = null;
    }

    public void loadSound(AssetFileDescriptor assetFile, String soundIndex)
    {
        soundLoadedCounter++;

        soundPoolFactory.soundLoadList.put(soundIndex, assetFile);
    }

    public void play(String soundIndex, boolean loop, float speed)
    {
        int streamId = soundPoolFactory.soundPool.play(soundIndexes.get(soundIndex), 1.0f, 1.0f, 1, loop ? -1 : 0, speed);

        if (loop)
        {
            loopingStreamIds.add(streamId);
        }
    }
}

