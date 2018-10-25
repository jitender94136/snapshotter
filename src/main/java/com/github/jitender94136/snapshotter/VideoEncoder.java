package com.github.jitender94136.snapshotter;


import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class VideoEncoder {
				
	 		private FFmpegFrameGrabber frameGrabber;	
	 		private  JPanel videoPanel;
	 		private JFrame topFrame;
	 		private volatile boolean running = false;
	 		private OpenCVFrameConverter.ToMat toMatConverter = new OpenCVFrameConverter.ToMat();
	 		private File videoFile = null;
	 		public VideoEncoder() {
                        //Creating the Frame
                        topFrame = new JFrame("Top Frame");
                        videoPanel = new JPanel();
                        topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        topFrame.setSize(600, 700);
                        //Creating the MenuBar and adding components
                        JMenuBar mb = new JMenuBar();
                        JMenu m1 = new JMenu("FILE");
                        mb.add(m1);
                        JMenuItem m11 = new JMenuItem("Open");
                        m1.add(m11);
                        m11.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println(e.getActionCommand());
                                        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                                        jfc.setDialogTitle("Choose a Video File Only... ");
                                        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                        FileFilter filter = new FileNameExtensionFilter(
                                            "video files only", "mp4", "mkv","avi","flv");
                                            jfc.setFileFilter(filter);
                                        int returnValue = jfc.showOpenDialog(null);
                                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                                            File selectedFile = jfc.getSelectedFile();
                                        	if (selectedFile.isFile()) {
                                        		videoFile = selectedFile;
                                                System.out.println("You selected the file: " + jfc.getSelectedFile());
                                            }
                                        }
                                    }
                        });
                        //Creating the panel at bottom and adding components
                        JPanel panel = new JPanel(); // the panel is not visible in output
                        JLabel label = new JLabel("Enter Text");
                        JTextField tf = new JTextField(10); // accepts upto 10 characters
                        JButton send = new JButton("Send");
                        JButton reset = new JButton("Reset");
                        panel.add(label); // Components Added using Flow Layout
                        panel.add(label); // Components Added using Flow Layout
                        panel.add(tf);
                        panel.add(send);
                        panel.add(reset);
                        // Text Area at the Center
                        JTextArea ta = new JTextArea();
                        //Adding Components to the frame.
                        topFrame.add(videoPanel, BorderLayout.CENTER);
                        //topFrame.getContentPane().add(BorderLayout.SOUTH, panel);
                        topFrame.getContentPane().add(BorderLayout.NORTH, mb);
                        //topFrame.getContentPane().add(BorderLayout.CENTER, ta);
                        topFrame.setVisible(true);
            }   
            
            public void start() {
            	
            	frameGrabber = new FFmpegFrameGrabber("desktop");
            	frameGrabber.setFormat("gdigrab");
            	frameGrabber.setFrameRate(30);
                frameGrabber.setImageWidth(1280);
                frameGrabber.setImageHeight(720);
                try {
                    frameGrabber.start();
                    System.out.println("Started frame grabber with image width-height : {}-{}"+frameGrabber.getImageWidth()+frameGrabber.getImageHeight());
                } catch (FrameGrabber.Exception e) {
                	System.out.println("Error when initializing the frame grabber"+e);
                    throw new RuntimeException("Unable to start the FrameGrabber", e);
                }

                SwingUtilities.invokeLater(() -> {
                	topFrame.setVisible(true);
                });
                process();
                System.out.println("Stopped frame grabbing.");
            }
            
            public void stop() {
                running = false;
                try {
                    System.out.println("Releasing and stopping FrameGrabber");
                    frameGrabber.release();
                    frameGrabber.stop();
                } catch (FrameGrabber.Exception e) {
                    System.out.println("Error occurred when stopping the FrameGrabber"+e);
                }

                topFrame.dispose();
            }
            
            private void process() {
                running = true;
                while (running) {
                    try {
                        // Here we grab frames from our camera
                        final Frame frame = frameGrabber.grab();

                        Mat mat = toMatConverter.convert(frame);

                        // Show the processed mat in UI
                        Frame processedFrame = toMatConverter.convert(mat);

                        Graphics graphics = videoPanel.getGraphics();
                        BufferedImage resizedImage = ImageUtils.getResizedBufferedImage(processedFrame, videoPanel);
                        SwingUtilities.invokeLater(() -> {
                            graphics.drawImage(resizedImage, 0, 0, videoPanel);
                        });
                    } catch (FrameGrabber.Exception e) {
                        System.out.println("Error when grabbing the frame"+e);
                    } catch (Exception e) {
                        System.out.println("Unexpected error occurred while grabbing and processing a frame"+e);
                    }
                }
            }

          

            public static void main(String[] args) {
                VideoEncoder javaCVExample = new VideoEncoder();

                System.out.println("Starting javacv example");
                new Thread(javaCVExample::start).start();

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Stopping javacv example");
                    javaCVExample.stop();
                }));

                try {
                    Thread.currentThread().join();
                } catch (InterruptedException ignored) { }
            }
            
            
            
}