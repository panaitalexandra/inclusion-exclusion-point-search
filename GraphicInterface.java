import javax.swing.*;
import java.awt.*;

public class GraphicInterface extends JFrame {
    private Drawing canvas;
    private JTextArea output;

    public GraphicInterface() {
        this.setTitle("Rectangle Point Location");
        this.setLayout(new BorderLayout());
        canvas = new Drawing();

        this.add(canvas, BorderLayout.CENTER);

        output = new JTextArea(10, 32);
        output.setEditable(false);
        output.setBackground(new Color(223, 255, 254));
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font("Monospaced", Font.PLAIN, 12));
        output.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(output);
        this.add(scrollPane, BorderLayout.EAST);

        canvas.setStatusTextArea(output);
        canvas.postInitMessage();
    }
}