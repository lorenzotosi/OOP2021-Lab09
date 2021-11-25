package it.unibo.oop.lab.reactivegui03;

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
public class AnotherConcurrentGUI extends JFrame {
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
    public AnotherConcurrentGUI() {

        /**
         * 
         */
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
        final Agent1 agent1 = new Agent1(agent);
        new Thread(agent).start();
        new Thread(agent1).start();
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                agent.stopCounting();
            }
        });
        up.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                agent.upCount();
                disabilitAll();
            }
        });
        down.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                agent.downCount();
            }
        });
    }

    private void disabilitAll() {
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private int counter;
        private volatile boolean switcher = true;
        private volatile boolean stopper;
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final int currentcounter = this.counter;
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            AnotherConcurrentGUI.this.display.setText(Integer.toString(currentcounter));
                        }
                    });
                    if (this.switcher) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    if (stopper) {
                        stopCounting();
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public synchronized void stopCounting() {
            this.stop = true;
        }

        public void upCount() {
            this.switcher = true;
        }

        public void downCount() {
            this.switcher = false;
        }

        /*public boolean getStop1() {
            return this.stop1;
        }*/

        public void setStopper(final boolean bool) {
            this.stopper = bool;
        }
    }

    private class Agent1 implements Runnable {
        private static final int SECONDS = 10_000;
        private final Agent a;
        Agent1(final Agent x) {
            this.a = x;
        }
        public void run() {
            try {
                Thread.sleep(SECONDS);
                //System.out.println(a.getStop1());
                a.setStopper(true);
                //System.out.println(a.getStop1());
                disabilitAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

