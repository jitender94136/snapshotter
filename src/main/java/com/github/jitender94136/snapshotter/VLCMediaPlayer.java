package com.github.jitender94136.snapshotter;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.windows.WindowsCanvas;

public class VLCMediaPlayer {

    // Create a media player factory
    private MediaPlayerFactory mediaPlayerFactory;

    // Create a new media player instance for the run-time platform
    private EmbeddedMediaPlayer mediaPlayer;

    private JPanel panel;
    private WindowsCanvas canvas;
    private JFrame frame;

    //Constructor
    public VLCMediaPlayer(String url){

        //Creating a panel that while contains the canvas
        panel = new JPanel();
        panel.setBackground(Color.BLACK);

        //Creating the canvas and adding it to the panel :
        canvas = new WindowsCanvas();
        panel.add(canvas);
        panel.revalidate();
        panel.repaint();

        //Creation a media player :
        mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        mediaPlayer.setVideoSurface(videoSurface);

        //Construction of the jframe :
        frame = new JFrame("Demo with Canvas AWT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.setSize(700, 500);

        //Adding the panel to the 
        frame.add(panel);
        frame.setVisible(true);

        //Playing the video
        mediaPlayer.playMedia(url);


    }
    //Main function :
    public static void main(String[] args) {
        NativeLibrary.addSearchPath("libvlc", "C:/Program Files/VideoLAN/VLC");

        final String url = "E:\\videoplayback.mp4";

        new VLCMediaPlayer(url);

    }

}