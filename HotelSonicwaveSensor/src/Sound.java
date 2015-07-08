import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Sound {

    public static void ring(String str) throws LineUnavailableException {

        float sampleRate = 11025f;
        int sampleSizeInBits = 8;
        int channels = 1;
        int frameSize = 1;
        float frameRate = 11025f;
        boolean bigEndian = true;

        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
            sampleRate, sampleSizeInBits,channels,frameSize, frameRate, bigEndian);

        SourceDataLine line = null;

        try {

            line = (SourceDataLine) AudioSystem.getLine(
                new DataLine.Info(SourceDataLine.class, format));

            line.open();
            line.start();

            float key;
            switch (str) {
			case "C":
				key = 260.7f; // C
				break;
			case "G":
				key = 391.9f;//G
			default:
				key = 260.7f; // C
				break;
			}
            

            byte[] buffer = new byte[(int)sampleRate];
            int rate = (int) (sampleRate / key);
            int volume = 25;

            boolean isUp = true;

            for (int i = 0; i < buffer.length; i++) {
                if (i % rate == 0) {
                    isUp = isUp ? false : true;
                }
                buffer[i] = isUp
                        ? (byte) volume
                        : (byte) (volume * -1);
            }

            line.write(buffer, 0, buffer.length);

        } finally {
            if (line != null) {
                line.close();
            }
        }

    }

}