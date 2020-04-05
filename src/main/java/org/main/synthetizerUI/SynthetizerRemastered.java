package org.main.synthetizerUI;

import org.main.bufferAudio.AudioThread;
import org.main.bufferAudio.Oscillator;
import org.main.utils.Utils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class SynthetizerRemastered {

    private static final HashMap<Character, Double> KEY_FREQUENCY = new HashMap<>();

    private boolean shouldGenerate;

    private final Oscillator[] oscillators = new Oscillator[3];
    private final WaveViewer waveViewer = new WaveViewer(oscillators);
    private final JFrame frame = new JFrame("Synthesizer Remastered");


    private final AudioThread thread = new AudioThread(() -> {
        if (!shouldGenerate) {
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i < AudioThread.BUFFER_SIZE; ++i) {
            double d = 0;
            for (Oscillator o : oscillators) {
                d += o.nextSample() / oscillators.length; // (1 + 0.5) / 2 = 0.75
            }
            s[i] = (short) (Short.MAX_VALUE * d);
        }
        return s;
    });

    static {
        final int STARTING_KEY = 16;
        final int KEY_FREQUENCY_INCREMENT = 2;
        final char[] KEYS = "<zxcvbnm,.-asdfghjklòàùqwertyuiopè+".toCharArray();
        for (int i = STARTING_KEY, key = 0; i < KEYS.length * KEY_FREQUENCY_INCREMENT + STARTING_KEY; i += KEY_FREQUENCY_INCREMENT, ++key) {
            KEY_FREQUENCY.put(KEYS[key], Utils.Math.getKeyFrequency(i));
        }
    }


    private final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {

            if (!KEY_FREQUENCY.containsKey(e.getKeyChar())) {
                return;
            }
            if (KEY_FREQUENCY.containsKey(e.getKeyChar())) {
                if (!thread.isRunning()) {
                    for (Oscillator o : oscillators) {
                        o.setKeyFrequency(KEY_FREQUENCY.get(e.getKeyChar()));
                    }
                    shouldGenerate = true;
                    thread.triggerPlayback();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            shouldGenerate = false;
        }
    };

    public KeyAdapter getKeyAdapter() {
        return this.keyAdapter;
    }

    public SynthetizerRemastered() {

        int y = 0;
        for (int i = 0; i < oscillators.length; ++i) {
            oscillators[i] = new Oscillator(this);
            oscillators[i].setLocation(5, y);
            frame.add(oscillators[i]);
            y += 105;
        }
        waveViewer.setBounds(290, 0, 310, 310);
        frame.add(waveViewer);
        frame.addKeyListener(keyAdapter);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(613, 357);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void updateWaveviewer() {
        waveViewer.repaint();
    }
}
