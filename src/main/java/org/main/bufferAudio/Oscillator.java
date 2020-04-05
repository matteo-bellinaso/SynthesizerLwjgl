package org.main.bufferAudio;

import org.main.synthetizerUI.SynthControlContainer;
import org.main.synthetizerUI.SynthetizerRemastered;
import org.main.utils.RefWrapper;
import org.main.utils.Utils;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class Oscillator extends SynthControlContainer {

    private static final int TONE_OFFSET_LIMIT = 2000;

    private RefWrapper<Integer> toneOffset = new RefWrapper<>(0);
    private RefWrapper<Integer> volume = new RefWrapper<>(100);

    private Wavetable wavetable = Wavetable.Sine;
    private int wavetableStepSide;
    private int wavetableIndex;
    private double keyFrequancy;

    public Oscillator(SynthetizerRemastered synth) {
        super(synth);

        JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());
        comboBox.setSelectedItem(Wavetable.Sine);
        comboBox.setBounds(10, 10, 105, 25);

        comboBox.addItemListener(l -> {
            if (l.getStateChange() == ItemEvent.SELECTED) {
                wavetable = (Wavetable) l.getItem();
            }
            synth.updateWaveviewer();
        });
        add(comboBox);

        JLabel toneParamenter = new JLabel("x0.00");
        toneParamenter.setBounds(155, 65, 60, 25);
        toneParamenter.setBorder(Utils.WindowDesign.LINE_BORDER);
        // mouse adapter
        Utils.parameterHandling.addParameterMouseListener(toneParamenter, this, -TONE_OFFSET_LIMIT,
                TONE_OFFSET_LIMIT, 2, toneOffset, () -> {
                    applyToneOffset();
                    toneParamenter.setText(" x" + String.format("%.3f", getToneOffset()));
                    synth.updateWaveviewer();
                });
        add(toneParamenter);

        JLabel toneText = new JLabel("Tone");
        toneText.setBounds(162, 40, 75, 25);
        add(toneText);

        JLabel volumeParameter = new JLabel("100%");
        volumeParameter.setBounds(222, 65, 50, 25);
        volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.parameterHandling.addParameterMouseListener(volumeParameter, this, 0, 100, 1, volume,
                () -> {
                    volumeParameter.setText(" " + volume.val + "%");
                    synth.updateWaveviewer();
                });

        add(volumeParameter);
        JLabel volumeText = new JLabel("Volume");
        volumeText.setBounds(225, 40, 75, 25);
        add(volumeText);

        setSize(279, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    public void setKeyFrequency(double freq) {
        keyFrequancy = freq;
        applyToneOffset();
    }

    private double getToneOffset() {
        return toneOffset.val / 1000d;
    }

    private double getVolumeMultiplier() {
        return volume.val / 100.0;
    }

    public double nextSample() {
        double sample = wavetable.getSamples()[wavetableIndex] * getVolumeMultiplier();
        wavetableIndex = (wavetableIndex + wavetableStepSide) % Wavetable.SIZE;
        return sample;
    }

    public double[] getSampleWaveform(int numOfSamples) {
        double[] samples = new double[numOfSamples];
        double frequency = 1.0 / (numOfSamples / (double) Utils.AudioInfo.SAMPLE_RATE) * 3.0;
        // aumentando il rate della moltiplicazione aumento le linee che visualizzo    (* 3.0)
        int index = 0;
        int stepSide = (int) (Wavetable.SIZE * Utils.Math.offsetTone(frequency, getToneOffset()) / Utils.AudioInfo.SAMPLE_RATE);
        for (int i = 0; i < numOfSamples; i++) {
            samples[i] = wavetable.getSamples()[index] * getVolumeMultiplier();
            index = (index + stepSide) % Wavetable.SIZE;
        }
        return samples;
    }


    private void applyToneOffset() {
        wavetableStepSide = (int) (Wavetable.SIZE * (Utils.Math.offsetTone(keyFrequancy, getToneOffset())) / Utils.AudioInfo.SAMPLE_RATE);
    }
}
