package com.github.jitender94136.snapshotter;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

    public class Snapshotter {

        private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
        private String nowPlaying;
        private volatile boolean waitingThread;
        private String destPath;
        long timePeriodForSnapshot;
        public static void main(final String[] args) {
        	System.out.println(ClassLoader.getSystemResource("lib").toString());
        	if (ClassLoader.getSystemResource("lib").toString().startsWith("file")) {
        	    System.out.println("You're running inside Eclipse");
        	    NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "./lib");
        	} else {
        		System.out.println(ClassLoader.getSystemResource("lib").toString().replace("jar:file:/", "").replace("snapshotter.jar!/lib", "")+"/lib/");
        		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), ClassLoader.getSystemResource("lib").toString().replace("jar:file:/", "").replace("snapshotter.jar!/lib", "")+"/lib/");
        	}
        	Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        	SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new Snapshotter(args);
                }
            });
            
        }

        private Snapshotter(String[] args) {
            final JFrame frame = new JFrame("Snap-Shotter");

            mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

            //frame.setContentPane(mediaPlayerComponent);

            frame.setLocation(200, 100);
            frame.setSize(700, 600);
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //define what happens when jframe window is closed...
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("Closing the window");
                    mediaPlayerComponent.release(); //media player component will close all the associated resources....
                    System.exit(0);
                }
            });
            final JButton send = new JButton("Send");
            JMenuBar mb = new JMenuBar();
            JMenu m1 = new JMenu("FILE");
            mb.add(m1);
            JMenuItem m11 = new JMenuItem("Source");
            JMenuItem m12 = new JMenuItem("Destination");
            m1.add(m11);
            m1.add(m12);
            m11.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println(e.getActionCommand());
                            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                            jfc.setDialogTitle("Choose a Video File Only... ");
                            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            FileFilter filter = new FileNameExtensionFilter(
                                "video files only", "mp4", "mkv","avi","flv");
                                jfc.setFileFilter(filter);
                                jfc.setAcceptAllFileFilterUsed(false);
                            int returnValue = jfc.showOpenDialog(null);
                            if (returnValue == JFileChooser.APPROVE_OPTION) {
                                File selectedFile = jfc.getSelectedFile();
                            	if (selectedFile.isFile()) {
                            		System.out.println(selectedFile.getAbsolutePath());
                            		nowPlaying = selectedFile.getAbsolutePath();
                            		mediaPlayerComponent.getMediaPlayer().playMedia(selectedFile.getAbsolutePath());
                                }
                            }
                            send.setVisible(true);
                        }
            });
            m12.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                            jfc.setDialogTitle("Choose a Folder Only... ");
                            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            int returnValue = jfc.showOpenDialog(null);
                            if (returnValue == JFileChooser.APPROVE_OPTION) {
                                File selectedFile = jfc.getSelectedFile();
                            	if (selectedFile.isDirectory()) {
                            		System.out.println(selectedFile.getAbsolutePath());
                            		destPath = selectedFile.getAbsolutePath();
                                }
                            }
                            send.setVisible(true);
                        }
            });
            JPanel panel = new JPanel(); // the panel is not visible in output
            JLabel label = new JLabel("Enter Seconds");
            JTextField tf = new JTextField(4); // accepts upto 4 characters
            
            send.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
							System.out.println("the action occurred..."+e.getActionCommand());
							String seconds = tf.getText();
							if(!seconds.matches("\\d+")){
								JOptionPane.showMessageDialog(frame,
									    "Please enter numeric values only...");
								frame.revalidate();
								return;
							} else if(seconds.startsWith("0")){
								JOptionPane.showMessageDialog(frame,
									    "There should be no leading zeroes...");
								frame.revalidate();
								return;
							} else if(seconds.trim().isEmpty() || seconds.trim().length() > 4 || Integer.parseInt(seconds) < 3) {
								JOptionPane.showMessageDialog(frame,
									    "Input Text length should be greater than 0 and less than 5. The value should be greater than or equals to 3");
								frame.revalidate();
								return;
							} else if(nowPlaying == null) {
								JOptionPane.showMessageDialog(frame,
									    "Please open a video file first to take snapshot using FILE Menu Bar");
								frame.revalidate();
								return;
							} else if(destPath == null) {
								JOptionPane.showMessageDialog(frame,
									    "Please choose a folder for storing the snapshots...");
								frame.revalidate();
								return;
							}
							System.out.println("hiding the button....");
							send.setVisible(false);
							System.out.println(seconds);
							timePeriodForSnapshot = Integer.parseInt(seconds)*1000;
							//MediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();
							//mediaPlayer.setSnapshotDirectory(new File(".").getAbsolutePath());
							//MediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();
							new Thread(new Runnable() {
								@Override
								public void run() {
									MediaPlayerFactory factory = new MediaPlayerFactory();
								     MediaPlayer mediaPlayer = factory.newEmbeddedMediaPlayer();
								     System.out.println("The output directory is "+destPath);
								     mediaPlayer.setSnapshotDirectory(destPath);
								     mediaPlayer.startMedia(nowPlaying);
								     waitingThread = true;
									 mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
										 
										 	@Override
										    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
										 			System.out.println("time changed event fired "+ newTime);
										 			waitingThread = false;
										 			mediaPlayer.pause();
										 	}
										 	
										    @Override
										    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
										    		System.out.println("position changed event fired "+ newPosition);
										    		waitingThread = false;
										 			mediaPlayer.pause();
										    }

										 
								            @Override
								            public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
								                System.out.println("snapshotTaken(filename=" + filename + ")");
								                mediaPlayer.play();
								                waitingThread = true;
								            }
								      });
									  long mediaDuration = mediaPlayer.getLength();
									  System.out.println("media duration...."+mediaDuration);
									  while(true) {
										    if(mediaDuration > 0 && timePeriodForSnapshot >= mediaDuration) {
							                    break;
							                }
										    float position = (float)(new BigDecimal(timePeriodForSnapshot).floatValue()/new BigDecimal(mediaDuration).floatValue());
										    System.out.println("the value of position "+position);
										    mediaPlayer.setPosition(position);
										    System.out.println("before inner while loop and waiting thread:-"+timePeriodForSnapshot +" "+waitingThread);
										    while(waitingThread);
										    waitingThread = true;
										    System.out.println("after inner while loop time and waiting thread:-"+timePeriodForSnapshot +" "+waitingThread);
										    File file3 = new File(destPath,"vlcj-snapshot-"+timePeriodForSnapshot+".png");
										    //file3.deleteOnExit();
										    System.out.println("Snapshot created: "+mediaPlayer.saveSnapshot(file3));
										    timePeriodForSnapshot += Integer.parseInt(seconds)*1000;
									        
									        System.out.println("end before while loop  "+timePeriodForSnapshot);
									  }
									  System.out.println("after the while loop....");
									  mediaPlayer.stop();
									  mediaPlayer.release();
									  timePeriodForSnapshot = 0;
									  System.out.println("showing the button again....");
									  send.setVisible(true);
									  frame.revalidate();
									
								}
							}).start();
							 
				}
			});
            panel.add(label); // Components Added using Flow Layout
            panel.add(label); // Components Added using Flow Layout
            panel.add(tf);
            panel.add(send);
            frame.getContentPane().add(mediaPlayerComponent,BorderLayout.CENTER);
            frame.getContentPane().add(mb,BorderLayout.NORTH);
            frame.getContentPane().add(panel,BorderLayout.SOUTH);
            frame.setVisible(true);
            //mediaPlayerComponent.getMediaPlayer().playMedia("E:\\video.mp4");
        }
    }