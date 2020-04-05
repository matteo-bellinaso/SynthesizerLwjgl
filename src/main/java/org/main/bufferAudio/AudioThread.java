package org.main.bufferAudio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.main.excaption.OpenAlcException;
import org.main.utils.Utils;

import java.util.function.Supplier;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class AudioThread extends Thread {

    public static final int BUFFER_SIZE = 512; // quanti sample un buffer contiene
    public static final int BUFFER_COUNT = 8;  // il numero di buffer in queue

    private final Supplier<short[]> bufferSupplier; //

    private final int[] buffer = new int[BUFFER_COUNT];
    private final long device = alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER)); // apre un default device
    private final long context = alcCreateContext(device, new int[1]);
    private final int source;

    private int bufferIndex;
    private boolean closed;
    private boolean running;

    public AudioThread(Supplier<short[]> bufferSupplier) {
        this.bufferSupplier = bufferSupplier;
        alcMakeContextCurrent(context); // porta la memoria al contesto che abbiamo selezionato
        AL.createCapabilities(ALC.createCapabilities(device)); // crea la capabilities del device
        source = alGenSources();
        for (int i = 0; i < BUFFER_COUNT; i++) {
            bufferSamples(new short[0]);

        }
        alSourcePlay(source);
        catchInternalExcaption();
        start();
    }

    @Override
    public synchronized void run() {
        while (!closed) {
            while (!running) {
                Utils.handleProcedure(this::wait, true);
            }
            int processedBuf = alGetSourcei(source, AL_BUFFERS_PROCESSED);
            for (int i = 0; i < processedBuf; ++i) {

                short[] samples  = bufferSupplier.get();
                if (samples == null) {
                    running = false;
                    break;
                }
                alDeleteBuffers(alSourceUnqueueBuffers(source));
                buffer[bufferIndex] = alGenBuffers();
                bufferSamples(samples);
            }
            if (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING) {
                alSourcePlay(source);
            }
            catchInternalExcaption();
        }
        setClosed();
        alDeleteSources(source);
        alDeleteBuffers(buffer);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    public synchronized void triggerPlayback() {
        running = true;
        notify();
    }

    public void setClosed() {
        closed = true;
        triggerPlayback();
    }

    public boolean isRunning() {
        return running;
    }

    private void bufferSamples(short[] samples) {
        int buf = buffer[bufferIndex++];
        alBufferData(buf, AL_FORMAT_MONO16, samples, Utils.AudioInfo.SAMPLE_RATE); // mono16 = single channel audio, suoniamo 44100 samples per second
        alSourceQueueBuffers(source, buf);
        bufferIndex %= BUFFER_COUNT; // last 8 % 8 = 0
    }

    private void catchInternalExcaption() {
        int err = alcGetError(device);

        if (err != ALC_NO_ERROR) {
            throw new OpenAlcException(err);
        }
    }
}
