package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * 
 *
 */
public final class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    /**
     * 
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        new Thread(agent).start();
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                agent.stopCounting();
                up.setEnabled(false);
                down.setEnabled(false);
                stop.setEnabled(false);
            }
        });
        up.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                agent.upCount();
            }
        });
        down.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                agent.downCount();
            }
        });
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private int counter;
        private volatile boolean switcher = true;
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final int currentcounter = this.counter;
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            ConcurrentGUI.this.display.setText(Integer.toString(currentcounter));
                        }
                    });
                    if (this.switcher) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }

                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void upCount() {
            this.switcher = true;
        }

        public void downCount() {
            this.switcher = false;
        }
    }
}
