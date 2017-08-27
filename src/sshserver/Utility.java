package sshserver;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Utility {

	public static boolean isNumber (String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (Exception e) {}
		return false;
	}
	
    public static ImageIcon resizeImageIcon (ImageIcon ic, int width, int height) {
    	return new ImageIcon(ic.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
    
    public static ImageIcon getAppIcon () {
    	return new ImageIcon(Utility.class.getResource("/icon.png"));
    }
    
    public static ImageIcon getAppIconSmall () {
    	return new ImageIcon(Utility.class.getResource("/icon_small.png"));
    }
}
