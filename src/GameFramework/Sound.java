package GameFramework;

import java.net.URISyntaxException;
import javax.sound.sampled.*;
import java.io.IOException;
import java.io.File;

//Take Two ---------------------------------------------------------

public enum Sound{
    
    /**
     * This enum encapsulates all the sound effects of a game, so as to separate the sound playing
     * codes from the game codes.
     * 1. Define all your sound effect names and the associated wave file.
     * 2. To play a specific sound, simply invoke SoundEffect.SOUND_NAME.play().
     * 3. You might optionally invoke the static method SoundEffect.init() to pre-load all the
     *    sound files, so that the play is not paused while loading the file for the first time.
     * 4. You can use the static variable SoundEffect.volume to mute the sound.
    */
    
    //EXPLODE("explode.wav"), // explosion
    //GONG("gong.wav"), // gong
    //SHOOT("shoot.wav"); // bullet
    BUTTONPRESS("beep.wav"),
    DEBUG("mariomusic.wav"),
    CONSTRUCT("construction.wav");
    
    // Nested class for specifying volume
    public static enum Volume 
    {
        MUTE, LOW, MEDIUM, HIGH
    }
    
    //NOTE: Constants must be defined first, as seen above.
    public Volume volume = Volume.LOW;
    private Clip clip;
    private String path;
    private AudioInputStream audioInputStream;
    
    Sound(String fileName)
    {
        try{
            path = SettingsHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(0, path.indexOf("game1.jar")) + "Sounds/" + fileName;
                        
            audioInputStream = AudioSystem.getAudioInputStream(new File(path));
            
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        }catch (URISyntaxException uriE){
            //TEST FOR NOW. THIS CANNOT STAY.
            MainMenu.writeMainLog("uriE Exception");
        }catch (UnsupportedAudioFileException uafe){
            //Do something
            MainMenu.writeMainLog("unsupportedaudio");
        }catch (IOException ioe){
            //do something
            MainMenu.writeMainLog("ioexception");
        }catch (LineUnavailableException lue){
            MainMenu.writeMainLog("lue exception");
        }
    }
    
    public void play() {
        if (volume != Volume.MUTE) {
            if (clip.isRunning())
                clip.stop();   // Stop the player if it is still running
        clip.setFramePosition(0); // rewind to the beginning
        clip.start(); 
      }
   }
    
    public void stop()
    {
        clip.stop();
    }
    
    public void setVolume(double volume)
    {
        FloatControl gain = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float)(Math.log(volume) / Math.log(10) * 20);
        gain.setValue(dB);
    }
}