package org.main.synthetizerUI;

import org.main.bufferAudio.Oscillator;
import org.main.utils.Utils;

import java.awt.*;
import java.util.function.Function;
import javax.swing.*;

public class WaveViewer extends JPanel {
    private Oscillator[] oscillators;

    public WaveViewer(Oscillator[] oscillators) {
        this.oscillators = oscillators;
        setBorder(Utils.WindowDesign.LINE_BORDER);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        final int PAD = 25;

        Graphics2D graphics2D = (Graphics2D) graphics;
        int numOfSumples = getWidth() - PAD * 2;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double[] mixedSamples = new double[numOfSumples];


        for (Oscillator oscillator : oscillators) {
            double[] samples = oscillator.getSampleWaveform(numOfSumples);
            for (int i = 0; i < samples.length; i++) {
                mixedSamples[i] += samples[i] / oscillators.length;
            }
        }
        int midY = getHeight() / 2;
        graphics2D.drawLine(PAD, midY, getWidth() - PAD, midY);
        graphics2D.drawLine(PAD, PAD, PAD, getHeight() - PAD);

        Function<Double, Integer> sampleToYCoord = sample -> (int)(midY + sample * (midY - PAD));

        for (int i = 0; i < numOfSumples; i++) {
            int nextY = i == numOfSumples - 1 ? sampleToYCoord.apply(mixedSamples[i]) : sampleToYCoord.apply(mixedSamples[i + 1]);
            graphics2D.drawLine(PAD + i, sampleToYCoord.apply(mixedSamples[i]), PAD + i + 1, nextY);
        }
    }
}
