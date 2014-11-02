#SoundPoolFacade

This class is a facade around SoundPool.

Essentially it adds functionality for ensuring 'sounds' are loaded before attempting to play them.   

This caused me a hugabyte of a headache. So hopefully I've solved this issue for others as well.

Should work on Android V2.3+


## Example of usage:

``
AssetManager assetManager = getActivity().getAssets();
AssetFileDescriptor soundTest = assetManager.openFd("soundTest.wav");
AssetFileDescriptor soundTest2 = assetManager.openFd("soundTest2.wav");

AudioPlayer audioPlayer = new AudioPlayer();

audioPlayer.loadSound(soundTest, "soundTest");
audioPlayer.loadSound(soundTest2, "soundTest");

audioPlayer.waitTillLoaded();

// Loop the following
audioPlayer.play("soundTest", true, 1.0f); 
audioPlayer.play("soundTest2", true, 1.0f); 

audioPlayer.stopAll();

audioPlayer.release();
``
