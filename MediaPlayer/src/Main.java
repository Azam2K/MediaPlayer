import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import javax.swing.*;
import java.io.IOException;
import java.util.logging.LogManager;


public class Main {
    public static void main(String[] args) {


        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());//to display the same on windows and mac
            } catch (Exception e) {
                e.printStackTrace();
            }
            view v = new view();
            controller c = new controller(v);
            LogManager.getLogManager().reset();//hides the logs in the terminal for the library that gets metadata because its just unecessary


        });
    }
}