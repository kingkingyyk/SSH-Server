package sshserver;

import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class TaskbarIcon extends TrayIcon {
	public JMenuItem startServerMenu;
	public JMenuItem stopServerMenu;
	public JMenuItem configMenu;
	public JMenuItem exitMenu;
	
	public TaskbarIcon(Image image) {
		super(image);

		setImageAutoSize(true);
		setToolTip(SSHServer.APP_NAME);
		
		JPopupMenu menu=new JPopupMenu();
		
		JDialog diag=new JDialog();
		diag.addWindowFocusListener(new WindowFocusListener () {
			@Override public void windowGainedFocus (WindowEvent  arg0) {}
			@Override
			public void windowLostFocus (WindowEvent  arg0) {
				menu.setVisible(false);
			}
		});
		
		menu.addPopupMenuListener(new PopupMenuListener() {
			@Override public void popupMenuCanceled(PopupMenuEvent arg0) {diag.setVisible(false);}
			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {diag.setVisible(false);}
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {}
		});
		
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                	diag.setLocation(e.getX(), e.getY());
                	diag.setVisible(true);
                	
                    menu.setLocation(e.getX(), e.getY());
                    menu.setInvoker(menu);
                    menu.setVisible(true);
                }
            }
        });
	
		menu.add(SSHServer.APP_NAME+" "+SSHServer.APP_VERSION);
		menu.addSeparator();
		
		startServerMenu=new JMenuItem("Start SSH Server");
		startServerMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					SSHServer.startSSHServer();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed to start SSH Server! "+e.getMessage(), SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		menu.add(startServerMenu);
		
		stopServerMenu=new JMenuItem("Stop SSH Server");
		stopServerMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					SSHServer.stopSSHServer();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed to stop SSH Server! "+e.getMessage(), SSHServer.APP_NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		menu.add(stopServerMenu);
		
		configMenu=new JMenuItem("Configure");
		configMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ConfigurationUI ui=new ConfigurationUI(false);
				ui.setLocationRelativeTo(null);
				ui.setVisible(true);
			}
		});
		menu.add(configMenu);
		
		exitMenu=new JMenuItem("Exit");
		exitMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		menu.add(exitMenu);
	}

}
