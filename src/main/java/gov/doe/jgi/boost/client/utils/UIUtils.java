package gov.doe.jgi.boost.client.utils;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

public class UIUtils {

	public static void openWebPage(String url) {
        try {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            }
            throw new NullPointerException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, url, "", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
